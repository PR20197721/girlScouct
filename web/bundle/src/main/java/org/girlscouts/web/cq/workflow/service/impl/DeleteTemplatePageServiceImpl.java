package org.girlscouts.web.cq.workflow.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.Node;
import javax.jcr.RangeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
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
import org.girlscouts.common.util.PageReplicationUtil;
import org.girlscouts.common.constants.PageReplicationConstants;
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
		log.info("Girlscouts Page Deletion Service Start.");
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
			log.info("dateRolloutRes={}", path);
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
					log.info("srcPath={}", srcPath);
					try {
						notify = dateRolloutNode.getProperty(PARAM_NOTIFY).getBoolean();
					} catch (Exception e) {
						log.error("Girlscouts Page Deletion Service encountered error: ", e);
					}
					log.info("notify={}", notify);
					try {
						delay = dateRolloutNode.getProperty(PARAM_DELAY).getBoolean();
					} catch (Exception e) {
						log.error("Girlscouts Page Deletion Service encountered error: ", e);
					}
					log.info("delay={}", delay);
					try {
						useTemplate = dateRolloutNode.getProperty(PARAM_USE_TEMPLATE).getBoolean();
					} catch (Exception e) {
						log.error("Girlscouts Page Deletion Service encountered error: ", e);
					}
					log.info("useTemplate={}", useTemplate);
					try {
						templatePath = dateRolloutNode.getProperty(PARAM_TEMPLATE_PATH).getString();
					} catch (Exception e) {
						log.error("Girlscouts Page Deletion Service encountered error: ", e);
					}
					log.info("templatePath={}", templatePath);
					if (useTemplate && (templatePath == null || templatePath.trim().length() == 0)) {
						log.error(
								"Girlscouts Page Deletion Service encountered error: Use Template checked, but no template provided. Cancelling.");
						PageReplicationUtil.markReplicationFailed(dateRolloutNode);
						return;
					}
					councils = PageReplicationUtil.getCouncils(dateRolloutNode);
					log.info("councils={}", councils);
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

	private void selectLiveRelationshipsForDeletion(Set<String> submittedCouncils, Resource sourcePageResource,
			Set<String> pagesToDelete,
			List<String> deletionLog, Set<String> notifyCouncils, ResourceResolver rr)
			throws RepositoryException, WCMException {
		log.info("Processing existing live relationships.");
		Set<String> processedRelationCouncils = new HashSet<String>();
		for (String councilPath : submittedCouncils) {
			log.info("Looking up live relationships in {}", councilPath);
			RangeIterator relationsIterator = relationManager.getLiveRelationships(sourcePageResource, councilPath,
					null);
			while (relationsIterator.hasNext()) {
				try {
					LiveRelationship relation = (LiveRelationship) relationsIterator.next();
					String relationPagePath = relation.getTargetPath();
					log.info("Attempting to delete {}", relationPagePath);
					deletionLog.add("Attempting to delete: " + relationPagePath);
					Resource relationPageResource = rr.resolve(relationPagePath);
					if (relationPageResource != null
							&& !relationPageResource.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
						processedRelationCouncils.add(councilPath);
						if (PageReplicationUtil.isPageInheritanceBroken(relationPageResource, deletionLog)) {
							notifyCouncils.add(relationPagePath);
							log.info(
									"Page {} has Break Inheritance checked. Will not delete.",
									relationPagePath);
							deletionLog.add("The page " + relationPagePath
									+ " has Break Inheritance checked off. Will not delete");
						} else {
							Map<String, Set<String>> relationComponents = PageReplicationUtil
									.categorizeRelationComponents(relationPageResource, deletionLog, rr);
							if (relationComponents.get(RELATION_CANC_INHERITANCE_COMPONENTS).size() > 0
									|| relationComponents.get(RELATION_CUSTOM_COMPONENTS).size() > 0) {
								notifyCouncils.add(relationPagePath);
								deletionLog.add("Page " + relationPagePath + " was not added to deletion queue");
								log.info("Page {} was not added to deletion queue.", relationPagePath);
							} else {
								pagesToDelete.add(relationPagePath);
								deletionLog.add("Page " + relationPagePath + " added to deletion queue");
								log.info("Page {} added to deletion queue.", relationPagePath);
							}
						}
					} else {
						log.info("Resource {} not found.", relationPagePath);
						log.info("Will NOT delete this page");
						deletionLog.add("Resource " + relationPagePath + " not found.");
						deletionLog.add("Will NOT delete this page");
					}
				} catch (Exception e) {
					log.error("Girlscouts Page Deletion Service encountered error: ", e);
				}
			}
		}
		submittedCouncils.removeAll(processedRelationCouncils);
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
			Boolean notify = false, useTemplate = false, delay = false, delete = true;
			try {
				notify = dateRolloutNode.getProperty(PARAM_NOTIFY).getBoolean();
			} catch (Exception e) {
				log.error("Girlscouts Page Deletion Service encountered error: ", e);
			}
			try {
				delete = dateRolloutNode.getProperty(PARAM_DELETE).getBoolean();
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
			html.append("<p>This workflow will " + (delete ? "" : "not ") + "delete pages upon completion.</p>");
			if (delete) {
				html.append("<p>This workflow will " + (delay ? "" : "not ")
						+ "delay the page deletion until tonight.</p>");
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
									log.error("sending email to "
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
		log.info("checking if running on publisher instance.");
		if (settingsService.getRunModes().contains("publish")) {
			return true;
		}
		return false;
	}

	}
