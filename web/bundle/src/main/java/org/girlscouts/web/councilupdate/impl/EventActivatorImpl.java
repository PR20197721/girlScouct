package org.girlscouts.web.councilupdate.impl;

import java.util.Set;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.girlscouts.web.components.PageActivationUtil;
import org.girlscouts.web.constants.PageActivationConstants;
import org.girlscouts.web.councilupdate.EventActivator;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;

/*
 * Girl Scouts Page Activator - DL
 * This process activates a queue of pages, in batches, with a timed delay between batches
 * This system of staggering activations allows the dispatcher caches to rebuild during large rollouts
 * The process runs at a scheduled time as a cron job, but it can also be called as a sling service and run at any time with the run() method
 */
@Component(
		metatype = true, 
		immediate = true,
		label = "Girl Scouts Event Activation Service", description = "Activates events at night to make cache-clearing interfere less with production sites"
		)
@Service(value = { Runnable.class, EventActivator.class })
@Properties({
		@Property(name = "service.description", value = "Girl Scouts GS Event Activation Service", propertyPrivate = true),
	@Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate=true), 
	@Property(name = "scheduler.expression", label="scheduler.expression", description="cron expression"),
	@Property(name = "scheduler.concurrent", boolValue=false, propertyPrivate=true),
		@Property(name = "scheduler.runOn", value = "SINGLE", propertyPrivate = true)
})

public class EventActivatorImpl extends PageActivatorImpl
		implements Runnable, EventActivator, PageActivationConstants, PageActivationConstants.Email {
	
	private static Logger log = LoggerFactory.getLogger(EventActivatorImpl.class);
	
	@Reference
	protected ResourceResolverFactory resolverFactory;

	@Activate
	private void activate(ComponentContext context) {
		try {
			rr = resolverFactory.getAdministrativeResourceResolver(null);
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		if (isPublisher()) {
			return;
		}
		try {
			Node eventActivations = getEventsActivationNode();
			if (eventActivations != null) {
				processActivationNode(eventActivations);
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
	private Node getEventsActivationNode() throws RepositoryException {
		Node eventActivationNode = null;
		try {
			Resource etcRes = rr.resolve("/etc");
			Node etcNode = etcRes.adaptTo(Node.class);
			Node activationsNode = null;
			if (etcNode.hasNode(EVENT_ACTIVATIONS_NODE)) {
				activationsNode = etcNode.getNode(EVENT_ACTIVATIONS_NODE);
			} else {
				activationsNode = etcNode.addNode(EVENT_ACTIVATIONS_NODE);
			}
			if (activationsNode != null) {
				Set<String> pages = PageActivationUtil.getPages(activationsNode);
				if (pages != null && !pages.isEmpty()) {
					String dateNodeName = PageActivationUtil.getDateRes();
					eventActivationNode = activationsNode.addNode(dateNodeName);
					eventActivationNode.setProperty(PARAM_PAGES, pages.toArray(new String[pages.size()]));
					eventActivationNode.setProperty(PARAM_CRAWL, Boolean.TRUE);
					eventActivationNode.setProperty(PARAM_DELAY, Boolean.TRUE);
					eventActivationNode.setProperty(PARAM_ACTIVATE, Boolean.TRUE);
					eventActivationNode.setProperty(PARAM_ACTIVATE, Boolean.TRUE);
					eventActivationNode.setProperty(PARAM_STATUS, STATUS_CREATED);
					eventActivationNode.getSession().save();
				}
				activationsNode.setProperty(PARAM_PAGES, new String[] {});
				activationsNode.getSession().save();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return eventActivationNode;
	}

	@Deactivate
	private void deactivate(ComponentContext componentContext) {
		log.info("GS Page Activation Service Deactivated.");
	}
}