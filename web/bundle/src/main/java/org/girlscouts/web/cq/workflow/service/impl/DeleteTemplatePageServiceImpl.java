package org.girlscouts.web.cq.workflow.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.common.components.GSEmailAttachment;
import org.girlscouts.common.osgi.service.GSEmailService;
import org.girlscouts.web.components.PageReplicationUtil;
import org.girlscouts.web.constants.PageReplicationConstants;
import org.girlscouts.web.cq.workflow.service.DeleteTemplatePageService;
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

	protected Map<String, Object> serviceParams;

	@Activate
	private void activate(ComponentContext context) {
		serviceParams = new HashMap<String, Object>();
		serviceParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-process-service");
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
		ResourceResolver rr = null;
		try {
			rr = resolverFactory.getServiceResourceResolver(serviceParams);
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
									notifyCouncils, rr);
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
					List<String> councilNotificationLog = new ArrayList<String>();
					Boolean isTestMode = PageReplicationUtil.isTestMode(rr);
					try {
						if (notify && notifyCouncils.size() > 0) {
							sendCouncilNotifications(dateRolloutNode, councilNotificationLog, isTestMode, rr);
						} else {
							dateRolloutNode.setProperty(PARAM_COUNCIL_NOTIFICATIONS_SENT, Boolean.FALSE);
						}
					} catch (Exception e) {
						log.error("Girlscouts Rollout Service encountered error: ", e);
					}
					try {
						sendGSUSANotifications(dateRolloutNode, deletionLog, councilNotificationLog, isTestMode, rr);
					} catch (Exception e) {
						log.error("Girlscouts Rollout Service encountered error: ", e);
					}
					try {
						if (delay) {
							dateRolloutNode.setProperty(PARAM_STATUS, STATUS_DELAYED);
							session.save();
						} else {
							pageReplicator.processReplicationNode(dateRolloutNode, rr);
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
		} catch (LoginException e) {
			log.error("Girlscouts Page Deletion Service encountered error: ", e);
		} finally {
			try {
				rr.close();
			} catch (Exception e) {

			}
		}

	}

	private void selectLiveRelationshipsForDeletion(Set<String> councils, Resource srcRes, Set<String> pagesToDelete,
			List<String> deletionLog, Set<String> notifyCouncils, ResourceResolver rr)
			throws RepositoryException, WCMException {
		RangeIterator relationIterator = relationManager.getLiveRelationships(srcRes, null, null);
		Map<String, Set<String>> componentRelationsMap = getComponentRelations(srcRes, deletionLog, rr);
		Set<String> councilsToRemove = new HashSet<String>();
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
						councilsToRemove.add(councilPath);
						// councils.remove(councilPath);
						if (!isPageInheritanceBroken(targetResource, deletionLog)) {
							Set<String> inheritedComponents = new HashSet<String>();
							Set<String> notInheritedComponents = new HashSet<String>();
							filterInheritedComponents(inheritedComponents, notInheritedComponents,
									componentRelationsMap, targetPath, deletionLog, rr);
							if (notInheritedComponents.size() > 0) {
								notifyCouncils.add(targetPath);
								deletionLog.add("Page " + targetPath + " was not added to deletion queue");
							} else {
								pagesToDelete.add(targetPath);
								deletionLog.add("Page " + targetPath + " added to deletion queue");
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
		councils.removeAll(councilsToRemove);
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

	private void filterInheritedComponents(Set<String> inheritedComponents, Set<String> notInheritedComponents,
			Map<String, Set<String>> componentRelationsMap, String targetPath, List<String> rolloutLog,
			ResourceResolver rr) {
		Set<String> srcComponents = componentRelationsMap.keySet();
		if (srcComponents != null && srcComponents.size() > 0) {
			for (String component : srcComponents) {
				if (isInheritanceBroken(targetPath, componentRelationsMap, component, rolloutLog, rr)) {
					notInheritedComponents.add(component);
					log.error(
							"Girlscouts Rollout Service: Council {} has broken inheritance with template component at {}. Removing from RolloutParams.",
							targetPath, component);
					rolloutLog.add("Girlscouts Rollout Service: Council " + targetPath
							+ " has broken inheritance with template component at " + component + ".");
				} else {
					inheritedComponents.add(component);
				}
			}
			}
		}

	private boolean isInheritanceBroken(String targetPath, Map<String, Set<String>> componentRelationsMap,
			String component, List<String> deletionLog, ResourceResolver rr) {
		Resource componentRes = rr.resolve(component);
		if (componentRes != null && !componentRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
			try {
				boolean relationShipExists = false;
				for (String relationPath : componentRelationsMap.get(component)) {
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
												deletionLog.add("Girlscouts Page Deletion Service: Component at "
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
							deletionLog.add("Girlscouts Page Deletion Service: Component at " + relationPath
									+ " is not found.");
							return true;
						}
						}
					}
				if (!relationShipExists) {
					log.error(
							"Girlscouts Page Deletion Service: Source Site Component {} does not have live sync relationship for {}.",
							component, targetPath);
					deletionLog.add("Girlscouts Page Deletion Service: Council " + targetPath
							+ " does not have live sync relationship for " + component);
					return true;
				}
			} catch (Exception e1) {
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

	private Map<String, Set<String>> getComponentRelations(Resource srcRes, List<String> deletionLog,
			ResourceResolver rr) {
		Map<String, Set<String>> componentRelationsMap = new HashMap<String, Set<String>>();
		Set<String> srcComponents = getComponents(srcRes);
		if (srcComponents != null && srcComponents.size() > 0) {
			for (String component : srcComponents) {
				Resource componentRes = rr.resolve(component);
				if (componentRes != null
						&& !componentRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
					log.error("Girlscouts Page Deletion Service: Looking up relations for {}.", componentRes);
					deletionLog.add("Girlscouts Page Deletion Service: Looking up relations for " + componentRes + ".");
					try {
						final LiveRelationshipManager relationManager = rr.adaptTo(LiveRelationshipManager.class);
						RangeIterator relationIterator = relationManager.getLiveRelationships(componentRes, null, null);
						if (relationIterator.hasNext()) {
							Set<String> componentRelations = new TreeSet<String>();
							while (relationIterator.hasNext()) {
								LiveRelationship relation = (LiveRelationship) relationIterator.next();
								String relationPath = relation.getTargetPath();
								componentRelations.add(relationPath);
							}
							componentRelationsMap.put(component, componentRelations);
						}
					} catch (Exception e) {
						}
					}
				}
			}
		return componentRelationsMap;
		}

	private void sendGSUSANotifications(Node dateRolloutNode, List<String> rolloutLog,
			List<String> councilNotificationLog, Boolean isTestMode, ResourceResolver rr) {
		Set<String> councils = null;
		String councilNotificationSubject = DEFAULT_DELETION_NOTIFICATION_SUBJECT;
		StringBuffer html = new StringBuffer();
		html.append(DEFAULT_REPORT_HEAD);
		html.append("<body>");
		html.append("<p>" + DEFAULT_DELETION_REPORT_SUBJECT + "</p>");
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
			String fileName = DEFAULT_DELETION_REPORT_ATTACHMENT + "_" + dateRolloutNode.getName();
			GSEmailAttachment attachment = new GSEmailAttachment(fileName, logData.toString(), null,
					GSEmailAttachment.MimeType.TEXT_PLAIN);
			attachments.add(attachment);
			String reportSubject = "(" + PageReplicationUtil.getEnvironment(rr) + ") "
					+ DEFAULT_DELETION_REPORT_SUBJECT;
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

	private void sendCouncilNotifications(Node dateRolloutNode, List<String> councilNotificationLog, Boolean isTestMode,
			ResourceResolver rr) {
		Set<String> notifyCouncils = new TreeSet<String>();
		String subject = DEFAULT_DELETION_NOTIFICATION_SUBJECT;
		String message = DEFAULT_DELETION_NOTIFICATION_MESSAGE, templatePath = "", srcPath = "";
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
								String branch = null;
								List<String> toAddresses = null;
								try {
									branch = PageReplicationUtil.getCouncilName(targetPath);
									// get the email addresses configured in
									// page properties of the council's homepage
									Page homepage = rr.resolve(branch + "/en").adaptTo(Page.class);
									toAddresses = PageReplicationUtil.getCouncilEmails(homepage.adaptTo(Node.class));
									log.error("Girlscouts Page Deletion Service: sending email to "
											+ branch.substring(9) + " emails:" + toAddresses.toString());
									String body = PageReplicationUtil.generateCouncilNotification(srcPath, targetPath,
											message, rr, settingsService);
									if (isTestMode) {
										councilNotificationLog.add("Notification is running in test mode!");
										councilNotificationLog.add("Replacing " + toAddresses.toString());
										toAddresses = PageReplicationUtil.getReportEmails(rr);
										councilNotificationLog.add("with " + toAddresses.toString());
										gsEmailService.sendEmail(subject, toAddresses, body);
									} else {
										gsEmailService.sendEmail(subject, toAddresses, body);
									}
									councilNotificationLog.add("Notification for " + branch + " council sent to emails:"
											+ String.valueOf(toAddresses));
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
										dateRolloutNode.setProperty(PARAM_COUNCIL_NOTIFICATIONS_SENT, Boolean.FALSE);
										dateRolloutNode.getSession().save();
									} catch (RepositoryException e1) {
										log.error("Girlscouts Page Deletion Service encountered error: ", e1);
									}
									councilNotificationLog.add("Failed to send notification for " + branch
											+ " council to emails:" + String.valueOf(toAddresses));
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
