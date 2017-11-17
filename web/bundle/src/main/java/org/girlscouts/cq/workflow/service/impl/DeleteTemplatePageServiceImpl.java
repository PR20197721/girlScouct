package org.girlscouts.cq.workflow.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.Node;
import javax.jcr.RangeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.cq.workflow.service.DeleteTemplatePageService;
import org.girlscouts.web.components.GSEmailAttachment;
import org.girlscouts.web.components.PageReplicationUtil;
import org.girlscouts.web.constants.PageReplicationConstants;
import org.girlscouts.web.service.email.GSEmailService;
import org.girlscouts.web.service.replication.PageReplicator;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.cq.wcm.msm.api.RolloutManager;

/*
 * Girl Scouts Page Deletion Service - DL
 * This process deletes a template page from council sites
 * This system of staggering deletions allows the dispatcher caches to rebuild during large rollouts
 */
@Component
@Service
public class DeleteTemplatePageServiceImpl
		implements DeleteTemplatePageService, PageReplicationConstants, PageReplicationConstants.Email {

	@Property(value = "Determine all live sync pages from submitted template page, and then deactivate and delete target pages.")
	static final String DESCRIPTION = Constants.SERVICE_DESCRIPTION;
	@Property(value = "Girl Scouts")
	static final String VENDOR = Constants.SERVICE_VENDOR;
	@Property(value = "Girl Scouts Page Deletion Service")
	static final String LABEL = "process.label";
	private static Logger log = LoggerFactory.getLogger(DeleteTemplatePageServiceImpl.class);

	@Reference
	private ResourceResolverFactory resolverFactory;
	@Reference
	private RolloutManager rolloutManager;
	@Reference
	private SlingSettingsService settingsService;
	@Reference
	public GSEmailService gsEmailService;
	@Reference
	private LiveRelationshipManager relationManager;
	@Reference
	private PageReplicator pageReplicator;

	private ResourceResolver rr;

	@Activate
	private void activate(ComponentContext context) {
		try {
			rr = resolverFactory.getAdministrativeResourceResolver(null);
		} catch (LoginException e) {
			log.error("Girlscouts Page Deletion Service encountered error: ", e);
		}
		log.info("Girlscouts Page Deletion Service Activated.");
	}

	@Override
	public void delete(String path) {
		if (isPublisher()) {
			return;
		}
		try {
			// N second remorse wait time in case workflow needs to be stopped.
			Thread.sleep(DEFAULT_REMORSE_WAIT_TIME);
		} catch (InterruptedException e) {
			log.error("Girlscouts Page Deletion Service encountered error: ", e);
		}
		Session session = rr.adaptTo(Session.class);
		Resource dateRolloutRes = rr.resolve(path);
		if (!dateRolloutRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
			Node dateRolloutNode = dateRolloutRes.adaptTo(Node.class);
			String srcPath = "", templatePath = "";
			Boolean notify = false, delay = false, useTemplate = false;
			Set<String> councils = null;
			Set<String> notifyCouncils = new TreeSet<String>();
			try {
				dateRolloutNode.setProperty(PARAM_STATUS, STATUS_PROCESSING);
				session.save();
				try {
					srcPath = dateRolloutNode.getProperty(PARAM_SOURCE_PATH).getString();
				} catch (Exception e) {
					log.error("Girlscouts Page Deletion Service encountered error: ", e);
				}
				try {
					notify = dateRolloutNode.getProperty(PARAM_NOTIFY).getBoolean();
				} catch (Exception e) {
					log.error("Girlscouts Page Deletion Service encountered error: ", e);
				}
				try {
					delay = dateRolloutNode.getProperty(PARAM_DELAY).getBoolean();
				} catch (Exception e) {
					log.error("Girlscouts Page Deletion Service encountered error: ", e);
				}
				try {
					useTemplate = dateRolloutNode.getProperty(PARAM_USE_TEMPLATE).getBoolean();
				} catch (Exception e) {
					log.error("Girlscouts Page Deletion Service encountered error: ", e);
				}
				try {
					templatePath = dateRolloutNode.getProperty(PARAM_TEMPLATE_PATH).getString();
				} catch (Exception e) {
					log.error("Girlscouts Page Deletion Service encountered error: ", e);
				}
				if (useTemplate && (templatePath == null || templatePath.trim().length() == 0)) {
					log.error(
							"Girlscouts Page Deletion Service encountered error: Use Template checked, but no template provided. Cancelling.");
					PageReplicationUtil.markReplicationFailed(dateRolloutNode);
					return;
				}
				councils = PageReplicationUtil.getCouncils(dateRolloutNode);
				List<String> deletionLog = new ArrayList<String>();
				Resource srcRes = rr.resolve(srcPath);
				if (relationManager.isSource(srcRes)) {
					Page srcPage = (Page) srcRes.adaptTo(Page.class);
					if (srcPage != null) {
						Set<String> pagesToDelete = new HashSet<String>();
						selectLiveRelationshipsForDeletion(councils, srcRes, pagesToDelete, deletionLog,
								notifyCouncils);
						if (!councils.isEmpty()) {
							int councilNameIndex = srcPath.indexOf("/", "/content/".length());
							String srcRelativePath = srcPath.substring(councilNameIndex);
							for (String council : councils) {
								notifyCouncils.add(council + srcRelativePath);
								log.error("Failed to process deletion for {} council", council);
								deletionLog.add("Failed to process deletion for " + council + " council");
							}
						}
						dateRolloutNode.setProperty(PARAM_PAGES_TO_DELETE,
								pagesToDelete.toArray(new String[pagesToDelete.size()]));
						dateRolloutNode.setProperty(PARAM_NOTIFY_COUNCILS,
								notifyCouncils.toArray(new String[notifyCouncils.size()]));
						session.save();
					} else {
						log.error("Resource is not a page. Quit. " + srcPath);
						PageReplicationUtil.markReplicationFailed(dateRolloutNode);
						return;
					}
				} else {
					log.error("Not a live copy source page. Quit. " + srcPath);
					PageReplicationUtil.markReplicationFailed(dateRolloutNode);
					return;
				}
				try {
					List<String> councilNotificationLog = new ArrayList<String>();
					Boolean isTestMode = PageReplicationUtil.isTestMode(rr);
					if (notify && notifyCouncils.size() > 0) {
						sendCouncilNotifications(dateRolloutNode, councilNotificationLog, isTestMode);
					} else {
						dateRolloutNode.setProperty(PARAM_COUNCIL_NOTIFICATIONS_SENT, Boolean.FALSE);
					}
					sendGSUSANotifications(dateRolloutNode, deletionLog, councilNotificationLog, isTestMode);
				} catch (Exception e) {
					log.error("Girlscouts Rollout Service encountered error: ", e);
				}
				try {
					if (delay) {
						dateRolloutNode.setProperty(PARAM_STATUS, STATUS_DELAYED);
						session.save();
					} else {
						pageReplicator.processReplicationNode(dateRolloutNode);
					}
				} catch (Exception e) {
					PageReplicationUtil.markReplicationFailed(dateRolloutNode);
					log.error("Girlscouts Rollout Service encountered error: ", e);
				}
			} catch (Exception e) {
				log.error("Girlscouts Page Deletion Service encountered error: ", e);
				PageReplicationUtil.markReplicationFailed(dateRolloutNode);
				return;
			}
		}

	}

	private void selectLiveRelationshipsForDeletion(Set<String> councils, Resource srcRes, Set<String> pagesToDelete,
			List<String> deletionLog, Set<String> notifyCouncils) throws RepositoryException, WCMException {
		RangeIterator relationIterator = relationManager.getLiveRelationships(srcRes, null, null);
		while (relationIterator.hasNext()) {
			try {
				LiveRelationship relation = (LiveRelationship) relationIterator.next();
				String targetPath = relation.getTargetPath();
				int councilNameIndex = targetPath.indexOf("/", "/content/".length());
				String councilPath = targetPath.substring(0, councilNameIndex);
				if (councils.contains(councilPath)) {
					deletionLog.add("Attempting to queue for deletion: " + targetPath);
					Resource targetResource = rr.resolve(targetPath);
					if (targetResource != null
							&& !targetResource.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
						councils.remove(councilPath);
						if (!isPageInheritanceBroken(targetResource, deletionLog)) {
							Set<String> components = getComponents(srcRes);
							if (!isComponentsInheritanceBroken(components, targetPath, deletionLog)) {
								pagesToDelete.add(targetPath);
								deletionLog.add("Page " + targetPath + " added to deletion queue");
							} else {
								notifyCouncils.add(targetPath);
								deletionLog.add("Page " + targetPath + " was not added to deletion queue");
							}
						} else {
							notifyCouncils.add(targetPath);
							deletionLog.add("The page has Break Inheritance checked off. Will not delete");
						}
					} else {
						deletionLog.add("Resource " + targetPath + " not found.");
						deletionLog.add("Will NOT delete this page");
					}
				}
			} catch (Exception e) {
				log.error("Girlscouts Page Deletion Service encountered error: ", e);
			}
		}
	}

	private boolean isPageInheritanceBroken(Resource targetResource, List<String> deletionLog) {
		try {
			Node targetNode = targetResource.adaptTo(Node.class);
			if (targetNode.hasProperty(PARAM_BREAK_INHERITANCE)) {
				deletionLog.add("Girlscouts Page Deletion Service: Resource at " + targetResource.getPath()
						+ " has parameter breakInheritance = "
						+ targetNode.getProperty(PARAM_BREAK_INHERITANCE).getBoolean());
				return targetNode.getProperty(PARAM_BREAK_INHERITANCE).getBoolean();
			} else {
				if (targetNode.hasProperty("jcr:content/" + PARAM_BREAK_INHERITANCE)) {
					deletionLog.add("Girlscouts Page Deletion Service: Resource at " + targetResource.getPath()
							+ "/jcr:content has parameter breakInheritance = "
							+ targetNode.getProperty("jcr:content/" + PARAM_BREAK_INHERITANCE).getBoolean());
					return targetNode.getProperty("jcr:content/" + PARAM_BREAK_INHERITANCE).getBoolean();
				}
			}
		} catch (Exception e) {
			log.error("Girlscouts Page Deletion Service encountered error: ", e);
		}
		return false;
	}


	private boolean isComponentsInheritanceBroken(Set<String> components, String targetPath, List<String> deletionLog) {
		boolean inheritanceBroken = false;
		if (components != null && components.size() > 0) {
			Set<String> brokenComponents = new HashSet<String>();
			for (String component : components) {
				if (isInheritanceBroken(targetPath, component, deletionLog)) {
					inheritanceBroken = true;
					brokenComponents.add(component);
					log.error(
							"Girlscouts Page Deletion Service: Council {} has broken inheritance with template component at {}. Removing from RolloutParams.",
							targetPath, component);
					deletionLog.add("Girlscouts Page Deletion Service: Council " + targetPath
							+ " has broken inheritance with template component at " + component + ".");
				}
			}
			components.removeAll(brokenComponents);
		}
		return inheritanceBroken;
	}

	private boolean isInheritanceBroken(String targetPath, String component, List<String> rolloutLog) {
		Resource componentRes = rr.resolve(component);
		if (componentRes != null && !componentRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
			try {
				RangeIterator relationIterator = relationManager.getLiveRelationships(componentRes, null, null);
				boolean relationShipExists = false;
				while (relationIterator.hasNext()) {
					LiveRelationship relation = (LiveRelationship) relationIterator.next();
					String relationPath = relation.getTargetPath();
					if (relationPath.startsWith(targetPath)) {
						relationShipExists = true;
						Resource targetComponentRes = rr.resolve(relationPath);
						if (targetComponentRes != null
								&& !targetComponentRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
							try {
								Node componentNode = targetComponentRes.adaptTo(Node.class);
								if (componentNode != null) {
									NodeType[] mixinTypes = componentNode.getMixinNodeTypes();
									if (mixinTypes != null && mixinTypes.length > 0) {
										for (NodeType mixinType : mixinTypes) {
											if (mixinType.isNodeType(PARAM_LIVE_SYNC_CANCELLED)) {
												log.error(
														"Girlscouts Page Deletion Service: Component at {} has mixinType {}",
														relationPath, mixinType.getName());
												rolloutLog.add("Girlscouts Page Deletion Service: Component at "
														+ relationPath + " has mixinType " + mixinType.getName());
												return true;
											}
										}
									}
								}
							} catch (RepositoryException e) {
								log.error("Girlscouts Page Deletion Service encountered error: ", e);
							}
						} else {
							log.error("Girlscouts Page Deletion Service: Component at {} is not found.", relationPath);
							rolloutLog
									.add("Girlscouts Page Deletion Service: Component at " + relationPath
											+ " is not found.");
							return true;
						}
					}
				}
				if (!relationShipExists) {
					log.error(
							"Girlscouts Page Deletion Service: Source Site Component {} does not have live sync relationship for {}.",
							component, targetPath);
					rolloutLog.add("Girlscouts Page Deletion Service: Council " + targetPath
							+ " does not have live sync relationship for " + component);
					return true;
				}
			} catch (WCMException e1) {
				log.error("Girlscouts Page Deletion Service encountered error: ", e1);
			}
		}
		return false;
	}

	private Set<String> getComponents(Resource srcRes) {
		Set<String> components = null;
		try {
			components = new HashSet<String>();
			Resource content = srcRes.getChild("jcr:content");
			traverseNodeForComponents(content, components);
		} catch (Exception e) {
			log.error("Girlscouts Page Deletion Service encountered error: ", e);
		}
		return components;
	}

	private void traverseNodeForComponents(Resource srcRes, Set<String> components) {
		if (srcRes != null) {
			components.add(srcRes.getPath());
			Iterable<Resource> children = srcRes.getChildren();
			if (children != null) {
				Iterator<Resource> it = children.iterator();
				while (it.hasNext()) {
					traverseNodeForComponents(it.next(), components);
				}
			}
		}
	}

	private void sendGSUSANotifications(Node dateRolloutNode, List<String> rolloutLog,
			List<String> councilNotificationLog, Boolean isTestMode) {
		Set<String> councils = null;
		String councilNotificationSubject = DEFAULT_NOTIFICATION_SUBJECT;
		StringBuffer html = new StringBuffer();
		html.append(DEFAULT_REPORT_HEAD);
		html.append("<body>");
		html.append("<p>" + DEFAULT_REPORT_SUBJECT + "</p>");
		html.append("<p>" + DEFAULT_REPORT_GREETING + "</p>");
		html.append("<p>" + DEFAULT_REPORT_INTRO + "</p>");
		Date runtime = new Date();
		html.append("<p>The workflow was run on " + runtime.toString() + ".</p>");
		String message = "", templatePath = "", srcPath = "";
		try {
			Boolean notify = false, useTemplate = false, delay = false, activate = true;
			try {
				notify = dateRolloutNode.getProperty(PARAM_NOTIFY).getBoolean();
			} catch (Exception e) {
				log.error("Girlscouts Page Deletion Service encountered error: ", e);
			}
			try {
				activate = dateRolloutNode.getProperty(PARAM_ACTIVATE).getBoolean();
			} catch (Exception e) {
				log.error("Girlscouts Page Deletion Service encountered error: ", e);
			}
			try {
				delay = dateRolloutNode.getProperty(PARAM_DELAY).getBoolean();
			} catch (Exception e) {
				log.error("Girlscouts Page Deletion Service encountered error: ", e);
			}
			try {
				useTemplate = dateRolloutNode.getProperty(PARAM_USE_TEMPLATE).getBoolean();
			} catch (Exception e) {
				log.error("Girlscouts Page Deletion Service encountered error: ", e);
			}
			try {
				if (useTemplate) {
					councilNotificationSubject = PageReplicationUtil.getTemplateSubject(templatePath, rr);
				} else {
					councilNotificationSubject = dateRolloutNode.getProperty(PARAM_EMAIL_SUBJECT).getString();
				}
			} catch (Exception e) {
				log.error("Girlscouts Page Deletion Service encountered error: ", e);
			}
			try {
				if (useTemplate) {
					message = PageReplicationUtil.getTemplateMessage(templatePath, rr);
				} else {
					message = dateRolloutNode.getProperty(PARAM_EMAIL_MESSAGE).getString();
				}
			} catch (Exception e) {
				log.error("Girlscouts Page Deletion Service encountered error: ", e);
			}
			try {
				templatePath = dateRolloutNode.getProperty(PARAM_TEMPLATE_PATH).getString();
			} catch (Exception e) {
				log.error("Girlscouts Page Deletion Service encountered error: ", e);
			}
			try {
				srcPath = dateRolloutNode.getProperty(PARAM_SOURCE_PATH).getString();
			} catch (Exception e) {
				log.error("Girlscouts Page Deletion Service encountered error: ", e);
			}
			try {
				councils = PageReplicationUtil.getCouncils(dateRolloutNode);
			} catch (Exception e) {
				log.error("Girlscouts Page Deletion Service encountered error: ", e);
				return;
			}
			html.append("<p>This workflow will " + (notify ? "" : "not ") + "send emails to councils.</p>");
			html.append("<p>This workflow will " + (activate ? "" : "not ") + "activate pages upon completion.</p>");
			if (activate) {
				html.append("<p>This workflow will " + (delay ? "" : "not ")
						+ "delay the page activations until tonight.</p>");
			}
			if (useTemplate) {
				html.append("<p>An email template is in use. The path to the template is " + templatePath + "</p>");
			}
			html.append("<p>The email subject is " + councilNotificationSubject + "</p>");
			html.append("<p>The email message is: " + message + "</p>");
			html.append("<p>The following councils have been selected:</p>");
			for (String council : councils) {
				html.append("<p>" + council + "</p>");
			}
			html.append("<p><b>Processing:</b><br>Source Page:" + srcPath + "</p>");
			for (String log : rolloutLog) {
				html.append("<p>" + log + "</p>");
			}

			StringBuffer logData = new StringBuffer();
			logData.append("The following is the process log for council notifications:\n\n");
			if (councilNotificationLog != null && !councilNotificationLog.isEmpty()) {
				for (String log : councilNotificationLog) {
					logData.append(log + "\n");
				}
			}
			List<String> gsusaEmails = PageReplicationUtil.getGsusaEmails(rr);
			Set<GSEmailAttachment> attachments = new HashSet<GSEmailAttachment>();
			String fileName = DEFAULT_REPORT_ATTACHMENT + "_" + dateRolloutNode.getName();
			GSEmailAttachment attachment = new GSEmailAttachment(fileName, logData.toString(), null,
					GSEmailAttachment.MimeType.TEXT_PLAIN);
			attachments.add(attachment);
			String reportSubject = "(" + PageReplicationUtil.getEnvironment(rr) + ") " + DEFAULT_REPORT_SUBJECT;
			if (isTestMode) {
				gsusaEmails = PageReplicationUtil.getReportEmails(rr);
				gsEmailService.sendEmail(reportSubject, gsusaEmails, html.toString(), attachments);
			} else {
				gsEmailService.sendEmail(reportSubject, gsusaEmails, html.toString(), attachments);
			}
		} catch (Exception e) {
			log.error("Girlscouts Page Deletion Service encountered error: ", e);
		}
	}

	private void sendCouncilNotifications(Node dateRolloutNode, List<String> councilNotificationLog,
			Boolean isTestMode) {
		Set<String> notifyCouncils = new TreeSet<String>();
		String subject = DEFAULT_NOTIFICATION_SUBJECT;
		String message = "", templatePath = "", srcPath = "";
		Boolean notify = false, useTemplate = false;
		try {
			notify = dateRolloutNode.getProperty(PARAM_NOTIFY).getBoolean();
			if (notify) {
				try {
					useTemplate = dateRolloutNode.getProperty(PARAM_USE_TEMPLATE).getBoolean();
				} catch (Exception e) {
					log.error("Girlscouts Page Deletion Service encountered error: ", e);
				}
				try {
					if (useTemplate) {
						subject = PageReplicationUtil.getTemplateSubject(templatePath, rr);
					} else {
						subject = dateRolloutNode.getProperty(PARAM_EMAIL_SUBJECT).getString();
					}
				} catch (Exception e) {
					log.error("Girlscouts Page Deletion Service encountered error: ", e);
				}
				try {
					if (useTemplate) {
						message = PageReplicationUtil.getTemplateMessage(templatePath, rr);
					} else {
						message = dateRolloutNode.getProperty(PARAM_EMAIL_MESSAGE).getString();
					}
				} catch (Exception e) {
					log.error("Girlscouts Page Deletion Service encountered error: ", e);
				}
				try {
					templatePath = dateRolloutNode.getProperty(PARAM_TEMPLATE_PATH).getString();
				} catch (Exception e) {
					log.error("Girlscouts Page Deletion Service encountered error: ", e);
				}
				try {
					srcPath = dateRolloutNode.getProperty(PARAM_SOURCE_PATH).getString();
				} catch (Exception e) {
					log.error("Girlscouts Page Deletion Service encountered error: ", e);
				}
				try {
					notifyCouncils.addAll(PageReplicationUtil.getNotifyCouncils(dateRolloutNode));
				} catch (Exception e) {
					log.error("Girlscouts Page Deletion Service encountered error: ", e);
					return;
				}
				Resource source = rr.resolve(srcPath);
				if (!source.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
					if (notifyCouncils != null && !notifyCouncils.isEmpty()) {
						for (String targetPath : notifyCouncils) {
							if (message != null && message.trim().length() > 0) {
								try {
									String branch = PageReplicationUtil.getBranch(targetPath);
									// get the email addresses configured in
									// page properties of the council's homepage
									Page homepage = rr.resolve(branch + "/en").adaptTo(Page.class);
									ValueMap valuemap = homepage.getProperties();
									List<String> toAddresses = PageReplicationUtil
											.getCouncilEmails(homepage.adaptTo(Node.class));
									log.error(
											"Girlscouts Page Deletion Service: sending email to " + branch.substring(9)
											+ " emails:" + toAddresses.toString());
									String body = generateCouncilNotification(srcPath, targetPath, valuemap, message);
									try {
										if (isTestMode) {
											councilNotificationLog.add("Notification is running in test mode!");
											councilNotificationLog.add("Replacing " + toAddresses.toString());
											toAddresses = PageReplicationUtil.getReportEmails(rr);
											councilNotificationLog.add("with " + toAddresses.toString());
											gsEmailService.sendEmail(subject, toAddresses, body);
										} else {
											gsEmailService.sendEmail(subject, toAddresses, body);
										}
										councilNotificationLog.add("Notification for " + branch.substring(9)
												+ " council sent to emails:" + toAddresses.toString());
										councilNotificationLog.add("Notification Email Body: \n" + body);
										try {
											dateRolloutNode.setProperty(PARAM_COUNCIL_NOTIFICATIONS_SENT, Boolean.TRUE);
											dateRolloutNode.getSession().save();
										} catch (RepositoryException e1) {
											log.error("Girlscouts Page Deletion Service encountered error: ", e1);
										}
									} catch (Exception e) {
										log.error("Girlscouts Page Deletion Service encountered error: ", e);
										try {
											dateRolloutNode.setProperty(PARAM_COUNCIL_NOTIFICATIONS_SENT,
													Boolean.FALSE);
											dateRolloutNode.getSession().save();
										} catch (RepositoryException e1) {
											log.error("Girlscouts Page Deletion Service encountered error: ", e1);
										}
										councilNotificationLog.add("Failed to send notification for "
												+ branch.substring(9) + " council to emails:" + toAddresses.toString());
									}
								} catch (WCMException e) {
									log.error("Girlscouts Page Deletion Service encountered error: ", e);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("Girlscouts Page Deletion Service encountered error: ", e);
		}
	}

	private String generateCouncilNotification(String nationalPage, String councilPage, ValueMap vm, String message) {
		String html = message.replaceAll("<%template-page%>", PageReplicationUtil.getURL(nationalPage))
				.replaceAll("&lt;%template-page%&gt;", PageReplicationUtil.getURL(nationalPage))
				.replaceAll("<%council-page%>", PageReplicationUtil.getCouncilUrl(rr, settingsService, councilPage))
				.replaceAll("&lt;%council-page%&gt;",
						PageReplicationUtil.getCouncilUrl(rr, settingsService, councilPage))
				.replaceAll("<%council-author-page%>", PageReplicationUtil.getURL(councilPage))
				.replaceAll("&lt;%council-author-page%&gt;",
						"https://authornew.girlscouts.org" + PageReplicationUtil.getURL(councilPage))
				.replaceAll("<%a", "<a").replaceAll("<%/a>", "</a>").replaceAll("&lt;%a", "<a")
				.replaceAll("&lt;%/a&gt;", "</a>");
		html = html.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		return html;
	}

	@Deactivate
	private void deactivate(ComponentContext componentContext) {
		log.info("Girlscouts Page Deletion Service Deactivated.");
	}

	private boolean isPublisher() {
		if (settingsService.getRunModes().contains("publish")) {
			return true;
		}
		return false;
	}

}
