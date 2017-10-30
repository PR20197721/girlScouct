package org.girlscouts.web.service.rollout.impl;

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
import javax.jcr.Value;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Query;
import org.girlscouts.web.components.GSEmailAttachment;
import org.girlscouts.web.components.PageActivationUtil;
import org.girlscouts.web.constants.PageActivationConstants;
import org.girlscouts.web.councilrollout.GirlScoutsNotificationAction;
import org.girlscouts.web.councilupdate.PageActivator;
import org.girlscouts.web.service.email.GSEmailService;
import org.girlscouts.web.service.rollout.GSRolloutService;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;

import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.cq.wcm.msm.api.RolloutConfig;
import com.day.cq.wcm.msm.api.RolloutConfigManager;
import com.day.cq.wcm.msm.api.RolloutManager;

import org.apache.sling.settings.SlingSettingsService;

import org.apache.sling.api.resource.ResourceResolver;

/*
 * Girl Scouts Page Activator - DL
 * This process activates a queue of pages, in batches, with a timed delay between batches
 * This system of staggering activations allows the dispatcher caches to rebuild during large rollouts
 * The process runs at a scheduled time as a cron job, but it can also be called as a sling service and run at any time with the run() method
 */
@Component
@Service
public class GSRolloutServiceImpl implements GSRolloutService, PageActivationConstants, PageActivationConstants.Email {
	@Property(value = "Roll out a page if it is the source page of a live copy, and then activate target pages.")
	static final String DESCRIPTION = Constants.SERVICE_DESCRIPTION;
	@Property(value = "Girl Scouts")
	static final String VENDOR = Constants.SERVICE_VENDOR;
	@Property(value = "Girl Scouts Roll out Service")
	static final String LABEL = "process.label";
	private static Logger log = LoggerFactory.getLogger(GSRolloutServiceImpl.class);
	
	@Reference
	private ResourceResolverFactory resolverFactory;
	@Reference
	private RolloutManager rolloutManager;
	@Reference 
	private Replicator replicator;
	@Reference
    private SlingSettingsService settingsService;
	@Reference
	public GSEmailService gsEmailService;
	@Reference
	private GirlScoutsNotificationAction notificationAction;
	@Reference
	private LiveRelationshipManager relationManager;
	@Reference
	private PageActivator pageActivator;
	
	private ResourceResolver rr;
	
	@Activate
	private void activate(ComponentContext context) {
		try {
			rr = resolverFactory.getAdministrativeResourceResolver(null);
		} catch (LoginException e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
		}
	}

	@Override
	public void rollout(String path) {
		if (isPublisher()) {
			return;
		}
		try {
			// 30 second remorse wait time in case workflow needs to be stopped.
			Thread.sleep(DEFAULT_REMORSE_WAIT_TIME);
		} catch (InterruptedException e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
		}
		Session session = rr.adaptTo(Session.class);
		Resource dateRolloutRes = rr.resolve(path);
		if (!dateRolloutRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
			Node dateRolloutNode = dateRolloutRes.adaptTo(Node.class);
			String srcPath = "", templatePath = "";
			Boolean notify = false, activate = true, delay = false, useTemplate = false, newPage = false;
			Set<String> councils = null;
			Set<String> notifyCouncils = new TreeSet<String>();
			try {
				dateRolloutNode.setProperty(PARAM_STATUS, STATUS_PROCESSING);
				session.save();
				try {
					srcPath = dateRolloutNode.getProperty(PARAM_SOURCE_PATH).getString();
				} catch (Exception e) {
					log.error("Girlscouts Rollout Service encountered error: ", e);
				}
				try {
					newPage = dateRolloutNode.getProperty(PARAM_NEW_PAGE).getBoolean();
				} catch (Exception e) {
					log.error("Girlscouts Rollout Service encountered error: ", e);
				}
				try {
					notify = dateRolloutNode.getProperty(PARAM_NOTIFY).getBoolean();
				} catch (Exception e) {
					log.error("Girlscouts Rollout Service encountered error: ", e);
				}
				try {
					activate = dateRolloutNode.getProperty(PARAM_ACTIVATE).getBoolean();
				} catch (Exception e) {
					log.error("Girlscouts Rollout Service encountered error: ", e);
				}
				try {
					delay = dateRolloutNode.getProperty(PARAM_DELAY).getBoolean();
				} catch (Exception e) {
					log.error("Girlscouts Rollout Service encountered error: ", e);
				}
				try {
					useTemplate = dateRolloutNode.getProperty(PARAM_USE_TEMPLATE).getBoolean();
				} catch (Exception e) {
					log.error("Girlscouts Rollout Service encountered error: ", e);
				}
				try {
					templatePath = dateRolloutNode.getProperty(PARAM_TEMPLATE_PATH).getString();
				} catch (Exception e) {
					log.error("Girlscouts Rollout Service encountered error: ", e);
				}
				if (useTemplate && (templatePath == null || templatePath.trim().length() == 0)) {
					log.error("Rollout Error - Use Template checked, but no template provided. Cancelling.");
					PageActivationUtil.markActivationFailed(session, dateRolloutNode);
					return;
				}
				councils = PageActivationUtil.getCouncils(dateRolloutNode);
				List<String> rolloutLog = new ArrayList<String>();
				Resource srcRes = rr.resolve(srcPath);
				if (relationManager.isSource(srcRes)) {
					Page srcPage = (Page) srcRes.adaptTo(Page.class);
					if (srcPage != null) {
						Set<String> pages = new HashSet<String>();
						if (newPage) {
							processNewLiveRelationships(councils, srcRes, pages, rolloutLog, notifyCouncils);
						} else {
							processExistingLiveRelationships(councils, srcRes, pages, rolloutLog, notifyCouncils);
						}

						if (!councils.isEmpty()) {
							notifyCouncils.addAll(councils);
							for (String council : councils) {
								log.error("Failed to rollout processing for %s council", council);
							}
						}
						dateRolloutNode.setProperty(PARAM_PAGES,
								pages.toArray(new String[pages.size()]));
						dateRolloutNode.setProperty(PARAM_NOTIFY_COUNCILS,
								notifyCouncils.toArray(new String[notifyCouncils.size()]));
						session.save();
					} else {
						log.error("Resource is not a page. Quit. " + srcPath);
						PageActivationUtil.markActivationFailed(session, dateRolloutNode);
						return;
					}
				} else {
					log.error("Not a live copy source page. Quit. " + srcPath);
					PageActivationUtil.markActivationFailed(session, dateRolloutNode);
					return;
				}
				List<String> councilNotificationLog = new ArrayList<String>();
				Boolean isTestMode = PageActivationUtil.isTestMode(rr);
				if (notify && notifyCouncils.size() > 0) {
					sendCouncilNotifications(dateRolloutNode, councilNotificationLog, isTestMode);
				} else {
					dateRolloutNode.setProperty(PARAM_COUNCIL_NOTIFICATIONS_SENT, Boolean.FALSE);
				}
				sendGSUSANotifications(dateRolloutNode, rolloutLog, councilNotificationLog, isTestMode);
				if (activate) {
					if (!delay) {
						pageActivator.processActivationNode(dateRolloutNode);
					} else {
						dateRolloutNode.setProperty(PARAM_STATUS, STATUS_DELAYED);
						session.save();
					}
				} else {
					dateRolloutNode.setProperty(PARAM_STATUS, STATUS_COMPLETE);
					PageActivationUtil.archive(dateRolloutNode);
				}
			} catch (Exception e) {
				log.error("Girlscouts Rollout Service encountered error: ", e);
				return;
			}
		}

	}

	private boolean isPublisher() {
		if (settingsService.getRunModes().contains("publish")) {
			return true;
		}
		return false;
	}
	
	
	@Deactivate
	private void deactivate(ComponentContext componentContext) {
		log.info("GS Page Rollout Service Deactivated.");
	}

	private void processNewLiveRelationships(Set<String> councils, Resource srcRes, Set<String> pagesToActivate,
			List<String> rolloutLog, Set<String> notifyCouncils)
			throws RepositoryException, WCMException {
		Session session = rr.adaptTo(Session.class);
		RangeIterator relationIterator = relationManager.getLiveRelationships(srcRes.getParent(), null, null);
		while (relationIterator.hasNext()) {
			LiveRelationship relation = (LiveRelationship) relationIterator.next();
			String parentPath = relation.getTargetPath();
			int councilNameIndex = parentPath.indexOf("/", "/content/".length());
			String councilPath = parentPath.substring(0, councilNameIndex);
			if (councils.contains(councilPath)) {
				councils.remove(councilPath);
				rolloutLog.add("Attempting to roll out a child page of: " + parentPath);
				Resource parentResource = rr.resolve(parentPath);
				if (parentResource != null
						&& !parentResource.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
					PageManager pageManager = rr.adaptTo(PageManager.class);
					Page srcPage = (Page) srcRes.adaptTo(Page.class);
					Page copyPage = pageManager.copy(srcPage, parentPath + "/" + srcPage.getName(), srcPage.getName(),
							false, true);
					RolloutConfigManager configMgr = (RolloutConfigManager) rr.adaptTo(RolloutConfigManager.class);
					RolloutConfig gsConfig = configMgr.getRolloutConfig(GS_ROLLOUT_CONFIG);
					LiveRelationship relationship = relationManager.establishRelationship(srcPage, copyPage, true,
							false, gsConfig);
					String targetPath = relationship.getTargetPath();
					cancelInheritance(rr, copyPage.getPath());
					rolloutManager.rollout(rr, relation, false);
					session.save();
					rolloutLog.add("Page " + copyPage.getPath() + " created");
					rolloutLog.add("Live copy established");
					if (targetPath.endsWith("/jcr:content")) {
						targetPath = targetPath.substring(0, targetPath.lastIndexOf('/'));
					}
					pagesToActivate.add(targetPath);
					rolloutLog.add("Page added to activation/cache build queue");
				} else {
					rolloutLog.add(
							"No resource can be found to serve as a suitable parent page. In order to roll out to this council, you must roll out the parent of this template page first.");
					rolloutLog.add("Will NOT rollout to this council");
				}
			}
		}

	}

	private void processExistingLiveRelationships(Set<String> councils, Resource srcRes, Set<String> pagesToActivate,
			List<String> rolloutLog, Set<String> notifyCouncils)
			throws RepositoryException, WCMException {
		RangeIterator relationIterator = relationManager.getLiveRelationships(srcRes, null, null);
		while (relationIterator.hasNext()) {
			LiveRelationship relation = (LiveRelationship) relationIterator.next();
			String targetPath = relation.getTargetPath();
			int councilNameIndex = targetPath.indexOf("/", "/content/".length());
			String councilPath = targetPath.substring(0, councilNameIndex);
			if (councils.contains(councilPath)) {
				rolloutLog.add("Attempting to roll out to: " + targetPath);
				councils.remove(councilPath);
				Resource targetResource = rr.resolve(targetPath);
				if (targetResource != null
						&& !targetResource.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
					Boolean breakInheritance = false;
					try {
						ValueMap contentProps = ResourceUtil.getValueMap(targetResource);
						breakInheritance = contentProps.get(PARAM_BREAK_INHERITANCE, false);
					} catch (Exception e) {
						log.error("Girlscouts Rollout Service encountered error: ", e);
					}
					if (!breakInheritance) {
						validateRolloutConfig(srcRes, targetResource);
						RolloutManager.RolloutParams params = new RolloutManager.RolloutParams();
						String[] components = getComponents(srcRes);
						boolean notifyCouncil = isLiveSyncCancelled(components, targetResource);
						if (notifyCouncil) {
							notifyCouncils.add(targetPath);
						}
						params.isDeep = false;
						params.master = srcRes.adaptTo(Page.class);
						params.targets = new String[] { targetPath };
						params.paragraphs = components;
						params.trigger = RolloutManager.Trigger.ROLLOUT;
						params.reset = false;
						rolloutManager.rollout(params);
						rolloutLog.add("Rolled out content to page");
						if (targetPath.endsWith("/jcr:content")) {
							targetPath = targetPath.substring(0, targetPath.lastIndexOf('/'));
						}
						pagesToActivate.add(targetPath);
						rolloutLog.add("Page added to activation/cache build queue");
					} else {
						notifyCouncils.add(targetPath);
						rolloutLog.add("The page has Break Inheritance checked off. Will not roll out");
					}
				} else {
					notifyCouncils.add(targetPath);
					rolloutLog.add("Resource not found in this council.");
					rolloutLog.add("Will NOT rollout to this page");
				}
			}
		}
	}

	private void validateRolloutConfig(Resource srcRes, Resource targetResource) {
		try {
			Node page = targetResource.adaptTo(Node.class);
			Node srcPage = srcRes.adaptTo(Node.class);
			if (!isGSRolloutConfig(page)) {
				Node liveSyncNode = null;
				if (getLiveSyncNode(page) != null) {
					liveSyncNode = getLiveSyncNode(page);
				} else {
					liveSyncNode = createLiveSyncNode(page, srcPage);
				}
				liveSyncNode.setProperty(PARAM_CQ_MASTER, srcPage.getPath());
				liveSyncNode.setProperty(PARAM_CQ_IS_DEEP, Boolean.TRUE);
				liveSyncNode.setProperty(PARAM_CQ_ROLLOUT_CONFIG, new String[] { GS_ROLLOUT_CONFIG });
				liveSyncNode.getSession().save();

			}
		} catch (RepositoryException e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
		}
	}

	private Node createLiveSyncNode(Node page, Node srcPage) {
		try {
			Node contentNode = page.getNode("jcr:content");
			if (contentNode != null) {
				Node liveSyncNode = contentNode.addNode("cq:LiveSyncConfig");
				liveSyncNode.setPrimaryType(PARAM_CQ_LIVE_SYNC_CONFIG);
				return liveSyncNode;
			}
		} catch (RepositoryException e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
		}
		return null;
	}

	private boolean isLiveSyncCancelled(String[] components, Resource targetResource) {
		if (components != null && components.length > 0) {
			if (targetResource != null) {
				Node targetNode = targetResource.adaptTo(Node.class);
				for (String component : components) {
					int srcContentIndex = component.indexOf("jcr:content");
					if (srcContentIndex > 0) {
						try {
							String srcComponentPath = component.substring(srcContentIndex);
							Node componentNode = targetNode.getNode(srcComponentPath);
							NodeType[] mixinTypes = componentNode.getMixinNodeTypes();
							if (mixinTypes != null && mixinTypes.length > 0) {
								for (NodeType mixinType : mixinTypes) {
									if (mixinType.isNodeType(PARAM_LIVE_SYNC_CANCELLED)) {
										return true;
									}
								}
							}
						} catch (RepositoryException e) {
							log.error("Girlscouts Rollout Service encountered error: ", e);
							return true;
						}
					}
				}
			}
		} else {
			return true;
		}
		return false;
	}

	private String[] getComponents(Resource srcRes) {
		String[] paragraphs = null;
		try {
			List<String> parList = new ArrayList<String>();
			Resource content = srcRes.getChild("jcr:content");
			traverseNodeForComponents(content, parList);
			paragraphs = parList.toArray(new String[parList.size()]);
		} catch (Exception e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
		}
		return paragraphs;
	}

	private void traverseNodeForComponents(Resource srcRes, List<String> parList) {
		if (srcRes != null) {
			parList.add(srcRes.getPath());
			Iterable<Resource> children = srcRes.getChildren();
			if (children != null) {
				Iterator<Resource> it = children.iterator();
				while (it.hasNext()) {
					traverseNodeForComponents(it.next(), parList);
				}
			}
		}
	}

	/**
	 * cancel the Inheritance for certain components (nodes with mixin type
	 * "cq:LiveSyncCancelled" under national template page)
	 */
	private void cancelInheritance(ResourceResolver rr, String councilPath) {
		try {
			String sql = "SELECT * FROM [cq:LiveSyncCancelled] AS s WHERE ISDESCENDANTNODE(s, '" + councilPath + "')";
			log.error("Girlscouts Rollout Service SQL2: " + sql);
			for (Iterator<Resource> it = rr.findResources(sql, Query.JCR_SQL2); it.hasNext();) {
				Resource target = it.next();
				log.error("Girlscouts Rollout Service : End relationship " + target.getPath());
				relationManager.endRelationship(target, true);
			}
		} catch (Exception e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
		}
	}

	private void sendGSUSANotifications(Node dateRolloutNode, List<String> rolloutLog,
			List<String> councilNotificationLog, Boolean isTestMode) {
		Set<String> councils = null;
		String subject = DEFAULT_NOTIFICATION_SUBJECT;
		StringBuffer html = new StringBuffer();
		html.append(DEFAULT_REPORT_HEAD);
		html.append("<body>");
		html.append("<p>" + DEFAULT_REPORT_SUBJECT + "</p>");
		html.append("<p>" + DEFAULT_REPORT_GREETING + "</p>");
		html.append("<p>" + DEFAULT_REPORT_INTRO + "</p>");
		Date runtime = new Date();
		html.append("<p>The workflow was run on " + runtime.toString() + ".</p>");
		String message = "", templatePath = "", srcPath = "";
		Boolean notify = false, useTemplate = false, delay = false, activate = true;
		try {
			notify = dateRolloutNode.getProperty(PARAM_NOTIFY).getBoolean();
		} catch (Exception e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
		}
		try {
			activate = dateRolloutNode.getProperty(PARAM_ACTIVATE).getBoolean();
		} catch (Exception e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
		}
		try {
			delay = dateRolloutNode.getProperty(PARAM_DELAY).getBoolean();
		} catch (Exception e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
		}
		try {
			useTemplate = dateRolloutNode.getProperty(PARAM_USE_TEMPLATE).getBoolean();
		} catch (Exception e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
		}
		try {
			if (useTemplate) {
				subject = PageActivationUtil.getTemplateSubject(templatePath, rr);
			} else {
				subject = dateRolloutNode.getProperty(PARAM_EMAIL_SUBJECT).getString();
			}
		} catch (Exception e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
		}
		try {
			if (useTemplate) {
				message = PageActivationUtil.getTemplateMessage(templatePath, rr);
			} else {
				message = dateRolloutNode.getProperty(PARAM_EMAIL_MESSAGE).getString();
			}
		} catch (Exception e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
		}
		try {
			templatePath = dateRolloutNode.getProperty(PARAM_TEMPLATE_PATH).getString();
		} catch (Exception e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
		}
		try {
			srcPath = dateRolloutNode.getProperty(PARAM_SOURCE_PATH).getString();
		} catch (Exception e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
		}
		try {
			councils = PageActivationUtil.getCouncils(dateRolloutNode);
		} catch (Exception e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
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
		html.append("<p>The email subject is " + subject + "</p>");
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
		List<String> gsusaEmails = PageActivationUtil.getGsusaEmails(rr);
		try {
			Set<GSEmailAttachment> attachments = new HashSet<GSEmailAttachment>();
			String fileName = DEFAULT_REPORT_ATTACHMENT + "_" + dateRolloutNode.getName();
			GSEmailAttachment attachment = new GSEmailAttachment(fileName, logData.toString(), null,
					GSEmailAttachment.MimeType.TEXT_PLAIN);
			attachments.add(attachment);
			if (isTestMode) {
				gsusaEmails = PageActivationUtil.getReportEmails(rr);
				gsEmailService.sendEmail(DEFAULT_REPORT_SUBJECT, gsusaEmails, html.toString(), attachments);
			} else {
				gsEmailService.sendEmail(DEFAULT_REPORT_SUBJECT, gsusaEmails, html.toString(), attachments);
			}

		} catch (Exception e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
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
		} catch (Exception e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
		}
		if (notify) {
			try {
				useTemplate = dateRolloutNode.getProperty(PARAM_USE_TEMPLATE).getBoolean();
			} catch (Exception e) {
				log.error("Girlscouts Rollout Service encountered error: ", e);
			}
			try {
				if (useTemplate) {
					subject = PageActivationUtil.getTemplateSubject(templatePath, rr);
				} else {
					subject = dateRolloutNode.getProperty(PARAM_EMAIL_SUBJECT).getString();
				}
			} catch (Exception e) {
				log.error("Girlscouts Rollout Service encountered error: ", e);
			}
			try {
				if (useTemplate) {
					message = PageActivationUtil.getTemplateMessage(templatePath, rr);
				} else {
					message = dateRolloutNode.getProperty(PARAM_EMAIL_MESSAGE).getString();
				}
			} catch (Exception e) {
				log.error("Girlscouts Rollout Service encountered error: ", e);
			}
			try {
				templatePath = dateRolloutNode.getProperty(PARAM_TEMPLATE_PATH).getString();
			} catch (Exception e) {
				log.error("Girlscouts Rollout Service encountered error: ", e);
			}
			try {
				srcPath = dateRolloutNode.getProperty(PARAM_SOURCE_PATH).getString();
			} catch (Exception e) {
				log.error("Girlscouts Rollout Service encountered error: ", e);
			}
			try {
				notifyCouncils.addAll(PageActivationUtil.getNotifyCouncils(dateRolloutNode));
			} catch (Exception e) {
				log.error("Girlscouts Rollout Service encountered error: ", e);
				return;
			}
			Resource source = rr.resolve(srcPath);
			if (!source.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
				if (notifyCouncils != null && !notifyCouncils.isEmpty()) {
					for (String targetPath : notifyCouncils) {
						if (message != null && message.trim().length() > 0) {
							try {
								String branch = PageActivationUtil.getBranch(targetPath);
								// get the email addresses configured in
								// page properties of the council's homepage
								Page homepage = rr.resolve(branch + "/en").adaptTo(Page.class);
								ValueMap valuemap = homepage.getProperties();
								List<String> toAddresses = PageActivationUtil
										.getCouncilEmails(homepage.adaptTo(Node.class));
								log.error("Girlscouts Rollout Service: sending email to " + branch.substring(9)
										+ " emails:" + toAddresses.toString());
								String body = generateCouncilNotification(srcPath, targetPath, valuemap, message);
								try {
									if (isTestMode) {
										councilNotificationLog.add("Notification is running in test mode!");
										councilNotificationLog.add("Replacing " + toAddresses.toString());
										toAddresses = PageActivationUtil.getReportEmails(rr);
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
										log.error("Girlscouts Rollout Service encountered error: ", e1);
									}
								} catch (Exception e) {
									log.error("Girlscouts Rollout Service encountered error: ", e);
									try {
										dateRolloutNode.setProperty(PARAM_COUNCIL_NOTIFICATIONS_SENT, Boolean.FALSE);
										dateRolloutNode.getSession().save();
									} catch (RepositoryException e1) {
										log.error("Girlscouts Rollout Service encountered error: ", e1);
									}
									councilNotificationLog.add("Failed to send notification for " + branch.substring(9)
											+ " council to emails:" + toAddresses.toString());
								}
							} catch (WCMException e) {
								log.error("Girlscouts Rollout Service encountered error: ", e);
							}
						}
					}
				}
			}
		}
	}

	private String generateCouncilNotification(String nationalPage, String councilPage, ValueMap vm, String message) {
		String html = message.replaceAll("<%template-page%>", PageActivationUtil.getURL(nationalPage))
				.replaceAll("&lt;%template-page%&gt;", PageActivationUtil.getURL(nationalPage))
				.replaceAll("<%council-page%>", PageActivationUtil.getCouncilUrl(rr, settingsService, councilPage))
				.replaceAll("&lt;%council-page%&gt;", PageActivationUtil.getCouncilUrl(rr, settingsService, councilPage))
				.replaceAll("<%council-author-page%>", PageActivationUtil.getURL(councilPage))
				.replaceAll("&lt;%council-author-page%&gt;",
						"https://authornew.girlscouts.org" + PageActivationUtil.getURL(councilPage))
				.replaceAll("<%a", "<a").replaceAll("<%/a>", "</a>").replaceAll("&lt;%a", "<a")
				.replaceAll("&lt;%/a&gt;", "</a>");
		html = html.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		return html;
	}

	private Boolean isGSRolloutConfig(Node pageNode) {
		boolean isValid = false;
		if (pageNode != null) {
			Node liveSync = getLiveSyncNode(pageNode);
			if (liveSync != null) {
				try {
					if (liveSync.hasProperty("cq:rolloutConfigs")) {
						Value[] configs = liveSync.getProperty("cq:rolloutConfigs").getValues();
						if (configs != null && configs.length > 0) {
							for (Value config : configs) {
								if (GS_ROLLOUT_CONFIG.equals(config.getString())) {
									isValid = true;
								}
							}
						}
					}
				} catch (RepositoryException e) {
					log.error("Girlscouts Rollout Service encountered error: ", e);
				}
			} else {
				isValid = isGSRolloutConfig(getAncestorWithLiveSync(pageNode));
			}
		}
		return isValid;
	}

	private Node getLiveSyncNode(Node node) {
		try {
			if (node.hasNode("jcr:content/cq:LiveSyncConfig")) {
				return node.getNode("jcr:content/cq:LiveSyncConfig");
			}
		} catch (Exception e) {
		}
		return null;
	}

	private Node getAncestorWithLiveSync(Node node) {
		try {
			Node parentNode = node.getParent();
			if (parentNode != null) {
				if (getLiveSyncNode(parentNode) != null) {
					return parentNode;
				} else {
					return getAncestorWithLiveSync(parentNode);
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
}