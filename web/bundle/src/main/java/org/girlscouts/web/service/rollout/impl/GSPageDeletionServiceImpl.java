package org.girlscouts.web.service.rollout.impl;

import javax.jcr.Node;
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
import org.girlscouts.web.constants.PageActivationConstants;
import org.girlscouts.web.councilupdate.PageReplicator;
import org.girlscouts.web.service.email.GSEmailService;
import org.girlscouts.web.service.rollout.GSPageDeletionService;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.cq.wcm.msm.api.RolloutManager;

/*
 * Girl Scouts Page Deletion Service - DL
 * This process deletes a template page from council sites
 * This system of staggering deletions allows the dispatcher caches to rebuild during large rollouts
 */
@Component
@Service
public class GSPageDeletionServiceImpl
		implements GSPageDeletionService, PageActivationConstants, PageActivationConstants.Email {

	@Property(value = "Determine all live sync pages from submitted template page, and then deactivate and delete target pages.")
	static final String DESCRIPTION = Constants.SERVICE_DESCRIPTION;
	@Property(value = "Girl Scouts")
	static final String VENDOR = Constants.SERVICE_VENDOR;
	@Property(value = "Girl Scouts Page Deletion Service")
	static final String LABEL = "process.label";
	private static Logger log = LoggerFactory.getLogger(GSPageDeletionServiceImpl.class);

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
			log.error("Girlscouts Rollout Service encountered error: ", e);
		}
		Session session = rr.adaptTo(Session.class);
		Resource dateRolloutRes = rr.resolve(path);
		if (!dateRolloutRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
			Node dateRolloutNode = dateRolloutRes.adaptTo(Node.class);
			// TODO:
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
