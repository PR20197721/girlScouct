package org.girlscouts.web.service.rollout.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.jcr.Node;
import javax.jcr.RangeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Query;
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
			e.printStackTrace();
		}
	}

	@Override
	public void rollout(String path) {
		if (isPublisher()) {
			return;
		}
		try {
			// 30 second remorse wait time in case instant activation needs to
			// be stopped.
			Thread.sleep(DEFAULT_REMORSE_WAIT_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Session session = rr.adaptTo(Session.class);
		Resource dateRolloutRes = rr.resolve(path);
		if (!dateRolloutRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
			Node dateRolloutNode = dateRolloutRes.adaptTo(Node.class);
			String srcPath = "", templatePath = "";
			;
			Boolean notify = false, activate = true, delay = false, useTemplate = false;
			Set<String> councils = null;
			try {
				try {
					srcPath = dateRolloutNode.getProperty(PARAM_SOURCE_PATH).getString();
				} catch (Exception e) {
				}
				try {
					notify = dateRolloutNode.getProperty(PARAM_NOTIFY).getBoolean();
				} catch (Exception e) {
				}
				try {
					activate = dateRolloutNode.getProperty(PARAM_ACTIVATE).getBoolean();
				} catch (Exception e) {
				}
				try {
					delay = dateRolloutNode.getProperty(PARAM_DELAY).getBoolean();
				} catch (Exception e) {
				}
				try {
					useTemplate = dateRolloutNode.getProperty(PARAM_USE_TEMPLATE).getBoolean();
				} catch (Exception e) {
				}
				try {
					templatePath = dateRolloutNode.getProperty(PARAM_TEMPLATE_PATH).getString();
				} catch (Exception e) {
				}
				if (useTemplate && (templatePath == null || templatePath.trim().length() == 0)) {
					log.error("Rollout Error - Use Template checked, but no template provided. Cancelling.");
					PageActivationUtil.markActivationFailed(session, dateRolloutNode);
					return;
				}
				councils = PageActivationUtil.getCouncils(dateRolloutNode);
				Resource srcRes = rr.resolve(srcPath);
				if (relationManager.isSource(srcRes)) {
					Page srcPage = (Page) srcRes.adaptTo(Page.class);
					if (srcPage != null) {
						Set<String> pages = new HashSet<String>();
						processExistingLiveRelationships(councils, srcRes, pages);
						if (!councils.isEmpty()) {
							processNewLiveRelationships(councils, srcRes, pages);
						}
						if (!councils.isEmpty()) {
							for (String council : councils) {
								log.error("Failed to rollout processing for %s council", council);
							}
						}
						dateRolloutNode.setProperty(PARAM_PAGES,
								pages.toArray(new String[pages.size()]));
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
				final String dateRolloutNodePath = dateRolloutNode.getPath();
				if (notify) {
					try {
						new Thread(new Runnable() {
							@Override
							public void run() {
								notificationAction.notifyCouncils(dateRolloutNodePath);
							}
						}).start();
					} catch (Exception e) {
					}
				}
				// Spawn a thread to process sending email
				// notifications to GSUSA
				try {
					new Thread(new Runnable() {
						@Override
						public void run() {
							notificationAction.notifyGSUSA(dateRolloutNodePath);
						}
					}).start();
				} catch (Exception e) {
				}
				if (activate) {
					if (!delay) {
						pageActivator.processActivationNode(dateRolloutNode);
					}
				} else {
					dateRolloutNode.setProperty(PARAM_STATUS, STATUS_COMPLETED);
					PageActivationUtil.archive(dateRolloutNode, session);
				}
			} catch (Exception e) {
				log.error("GS Page Rollout - encountered error ", e);
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

	private void processNewLiveRelationships(Set<String> councils, Resource srcRes, Set<String> pagesToActivate)
			throws RepositoryException, WCMException {
		Session session = rr.adaptTo(Session.class);
		RangeIterator relationIterator = relationManager.getLiveRelationships(srcRes.getParent(), null, null);
		while (relationIterator.hasNext()) {
			LiveRelationship relation = (LiveRelationship) relationIterator.next();
			String parentPath = relation.getTargetPath();
			Resource targetResource = rr.resolve(parentPath);
			if (!targetResource.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
				int councilNameIndex = parentPath.indexOf("/", "/content/".length());
				String councilPath = parentPath.substring(0, councilNameIndex);
				if (councils.contains(councilPath)) {
					councils.remove(councilPath);
					Boolean breakInheritance = false;
					try {
						ValueMap contentProps = ResourceUtil.getValueMap(targetResource);
						breakInheritance = contentProps.get(PARAM_BREAK_INHERITANCE, false);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (!breakInheritance) {
						PageManager pageManager = rr.adaptTo(PageManager.class);
						Page srcPage = (Page) srcRes.adaptTo(Page.class);
						Page copyPage = pageManager.copy(srcPage,
								targetResource.getParent().getPath() + "/" + srcPage.getName(), srcPage.getName(),
								false, true);
						RolloutConfigManager configMgr = (RolloutConfigManager) rr
								.adaptTo(RolloutConfigManager.class);
						RolloutConfig gsConfig = configMgr.getRolloutConfig("/etc/msm/rolloutconfigs/gsdefault");
						LiveRelationship relationship = relationManager.establishRelationship(srcPage, copyPage, true,
								false, gsConfig);
						String targetPath = relationship.getTargetPath();
						cancelInheritance(rr, copyPage.getPath());
						rolloutManager.rollout(rr, relation, false);
						session.save();
						if (targetPath.endsWith("/jcr:content")) {
							targetPath = targetPath.substring(0, targetPath.lastIndexOf('/'));
						}
						pagesToActivate.add(targetPath);
					}
				}
			}
		}

	}

	private void processExistingLiveRelationships(Set<String> councils, Resource srcRes, Set<String> pagesToActivate)
			throws RepositoryException, WCMException {
		Session session = rr.adaptTo(Session.class);
		RangeIterator relationIterator = relationManager.getLiveRelationships(srcRes, null, null);
		while (relationIterator.hasNext()) {
			LiveRelationship relation = (LiveRelationship) relationIterator.next();
			String targetPath = relation.getTargetPath();
			Resource targetResource = rr.resolve(targetPath);
			if (!targetResource.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
				int councilNameIndex = targetPath.indexOf("/", "/content/".length());
				String councilPath = targetPath.substring(0, councilNameIndex);
				if (councils.contains(councilPath)) {
					councils.remove(councilPath);
					Boolean breakInheritance = false;
					try {
						ValueMap contentProps = ResourceUtil.getValueMap(targetResource);
						breakInheritance = contentProps.get(PARAM_BREAK_INHERITANCE, false);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (!breakInheritance) {
						rolloutManager.rollout(rr, relation, false);
						session.save();
						if (targetPath.endsWith("/jcr:content")) {
							targetPath = targetPath.substring(0, targetPath.lastIndexOf('/'));
						}
						pagesToActivate.add(targetPath);
					}
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
			log.debug("SQL " + sql);
			for (Iterator<Resource> it = rr.findResources(sql, Query.JCR_SQL2); it.hasNext();) {
				Resource target = it.next();
				relationManager.endRelationship(target, true);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}