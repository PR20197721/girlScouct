package org.girlscouts.web.councilupdate.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.RowIterator;
import org.girlscouts.web.components.PageActivationReporter;
import org.girlscouts.web.components.PageActivationUtil;
import org.girlscouts.web.constants.PageActivationConstants;
import org.girlscouts.web.councilupdate.CacheThread;
import org.girlscouts.web.councilupdate.PageActivator;
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
import com.day.cq.mailer.MessageGatewayService;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;

import org.apache.sling.settings.SlingSettingsService;

import org.apache.sling.api.resource.ResourceResolver;

/*
 * Girl Scouts Page Activator - DL
 * This process activates a queue of pages, in batches, with a timed delay between batches
 * This system of staggering activations allows the dispatcher caches to rebuild during large rollouts
 * The process runs at a scheduled time as a cron job, but it can also be called as a sling service and run at any time with the run() method
 */
@Component(
		metatype = true, 
		immediate = true,
		label = "Girl Scouts Page Activation Service", 
		description = "Activates pages at night to make cache-clearing interfere less with production sites" 
		)
@Service(value = {Runnable.class, PageActivator.class})
@Properties({
	@Property(name = "service.description", value = "Girl Scouts GS Activation Service",propertyPrivate=true),
	@Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate=true), 
	@Property(name = "scheduler.expression", label="scheduler.expression", description="cron expression"),
	@Property(name = "scheduler.concurrent", boolValue=false, propertyPrivate=true),
		@Property(name = "scheduler.runOn", value = "SINGLE", propertyPrivate = true)
})

public class PageActivatorImpl implements Runnable, PageActivator, PageActivationConstants {
	
	private static Logger log = LoggerFactory.getLogger(PageActivatorImpl.class);
	@Reference
	private ResourceResolverFactory resolverFactory;
	@Reference 
	private Replicator replicator;
	@Reference
    private SlingSettingsService settingsService;
	@Reference
	public MessageGatewayService messageGatewayService;
	
	private ResourceResolver rr;
	//configuration fields
	
	@Activate
	private void activate(ComponentContext context) {
		try {
			rr= resolverFactory.getAdministrativeResourceResolver(null);
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		if (isPublisher()) {
			return;
		}
		Session session = rr.adaptTo(Session.class);
		List<Node> queuedActivations = queueDelayedActivations(rr, session);
		if (queuedActivations != null) {
			for (Node dateRolloutNode : queuedActivations) {
				try {
					process(dateRolloutNode.getPath(), session);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void run(String path) {
		if (isPublisher()) {
			return;
		}
		process(path, rr.adaptTo(Session.class));
	}

	private void process(String path, Session session) {
		long begin = System.currentTimeMillis();
		Resource dateRolloutRes = rr.resolve(path);
		Node dateRolloutNode = dateRolloutRes.adaptTo(Node.class);
		try {
			if (dateRolloutNode.hasProperty(PARAM_STATUS)
					&& (STATUS_CREATED.equals(dateRolloutNode.getProperty(PARAM_STATUS).getString())
							|| STATUS_QUEUED.equals(dateRolloutNode.getProperty(PARAM_STATUS).getString()))) {
				dateRolloutNode.setProperty(PARAM_STATUS, STATUS_PROCESSING);
				session.save();
			} else {
				log.info("GS page Activator - Process already running or completed");
				return;
			}
		} catch (Exception e) {
			log.error("GS Page Activator - Failed to check if process in progress already");
			return;
		}
		String subject = "", message = "", templatePath = "";
		Boolean dontSend = false, delay = false, crawl = false, dontActivate = true, useTemplate = false;
		try {
			delay = dateRolloutNode.getProperty(PARAM_DELAY).getBoolean();
		} catch (Exception e) {
		}
		try {
			crawl = dateRolloutNode.getProperty(PARAM_CRAWL).getBoolean();
		} catch (Exception e) {
		}
		try {
			dontActivate = dateRolloutNode.getProperty(PARAM_DONT_ACTIVATE).getBoolean();
		} catch (Exception e) {
		}
		try {
			useTemplate = dateRolloutNode.getProperty(PARAM_USE_TEMPLATE).getBoolean();
		} catch (Exception e) {
		}
		try {
			dontSend = dateRolloutNode.getProperty(PARAM_DONT_SEND_EMAIL).getBoolean();
		} catch (Exception e) {
		}
		try {
			subject = dateRolloutNode.getProperty(PARAM_EMAIL_SUBJECT).getString();
		} catch (Exception e) {
		}
		try {
			message = dateRolloutNode.getProperty(PARAM_EMAIL_MESSAGE).getString();
		} catch (Exception e) {
		}
		try {
			templatePath = dateRolloutNode.getProperty(PARAM_TEMPLATE_PATH).getString();
		} catch (Exception e) {
		}

		PageActivationReporter reporter = new PageActivationReporter(dateRolloutNode, session, messageGatewayService);
		reporter.report("Initializing process");
		String[] pages = new String[0];
		reporter.report("Retrieving page queue");
		try {
			pages = getPages(dateRolloutNode, session);
		} catch (Exception e) {
			reporter.report("Failed to get initial page count");
			log.error("GS Page Activator - failed to get initial page count");
			e.printStackTrace();
			markRolloutFailed(session, dateRolloutNode);
			return;
		}
		if (pages.length == 0) {
			reporter.report("No pages found in page queue. Will not proceed");
			return;
		}
		TreeSet<String> activatedPages = new TreeSet<String>();
		TreeSet<String> unmappedPages = new TreeSet<String>();
		HashMap<String, TreeSet<String>> toActivate = new HashMap<String, TreeSet<String>>();
		// while (pages.length > 0) {
		reporter.report("Arranging pages by council");
		try {
			toActivate = groupByCouncil(pages, rr, unmappedPages);
		} catch (Exception e) {
			reporter.report("Failed to sort pages by council");
			log.error("GS Page Activator - failed to arrange councils");
			markRolloutFailed(session, dateRolloutNode);
		}
		for (int i = 0; i <= 100; i++) {
			if (unmappedPages.size() > 0) {
				for (String u : unmappedPages) {
					reporter.report("Page " + u + " could not be mapped to an external url");
				}
			}
		}
		if (toActivate.size() > 0 && !dontActivate) {
			activateAndBuildCache(toActivate, activatedPages, session, dateRolloutNode, crawl, reporter);
		}
		// }

		long end = System.currentTimeMillis();
		reporter.report("Sending notification emails");
		try {
			reporter.sendReportEmail(begin, end, delay, crawl, activatedPages, dateRolloutNode, session);
			reporter.report("Notification emails delivered");
		} catch (Exception e) {
			reporter.report("Unable to send report email");
			log.error("Girl Scouts Page Activator - Unable to send report email");
			log.error(e.getMessage());
		}

		try {
			dateRolloutNode.setProperty(PARAM_PROCESSED_PAGES, activatedPages.toArray(new String[activatedPages.size()]));
			dateRolloutNode.setProperty(PARAM_UNMAPPED_PAGES, unmappedPages.toArray(new String[unmappedPages.size()]));
			dateRolloutNode.setProperty(PARAM_STATUS, STATUS_COMPLETED);
			session.save();
			reporter.report("Moving " + dateRolloutNode.getName() + " from " + dateRolloutNode.getPath() + " to "
					+ dateRolloutNode.getParent().getPath() + "/" + COMPLETED_NODE + "/" + dateRolloutNode.getName());
			archive(dateRolloutNode, session);
			log.info("GS Page Activator - Process completed");
			reporter.report("GS Page Activator - Process completed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void archive(Node dateRolloutNode, Session session) {
		try {
			Node parent = dateRolloutNode.getParent();
			if (!parent.hasNode(COMPLETED_NODE)) {
				parent.addNode(COMPLETED_NODE);
			}
			session.move(dateRolloutNode.getPath(),
					parent.getPath() + "/" + COMPLETED_NODE + "/" + dateRolloutNode.getName());
			session.save();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void markRolloutFailed(Session session, Node dateRolloutNode) {
		try {
			dateRolloutNode.setProperty(PARAM_STATUS, STATUS_FAILED);
			session.save();
		} catch (RepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private boolean isPublisher() {
		if (settingsService.getRunModes().contains("publish")) {
			return true;
		}
		return false;
	}

	private List<Node> queueDelayedActivations(ResourceResolver rr, Session session) {
		List<Node> activations = null;
		Resource gsDelayedRes = rr.resolve(GS_ACTIVATIONS_PATH + "/" + DELAYED_NODE);
		if (!gsDelayedRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
			Iterable<Resource> gsDelayedJobs = gsDelayedRes.getChildren();
			if (gsDelayedJobs.iterator() != null) {
				activations = new ArrayList<Node>();
				Iterator<Resource> activationJobs = gsDelayedJobs.iterator();
				while (activationJobs.hasNext()) {
					try {
						Resource dateRolloutRes = activationJobs.next();
						Node dateRolloutNode = dateRolloutRes.adaptTo(Node.class);
						if (dateRolloutNode.hasProperty(PARAM_STATUS)
								&& STATUS_CREATED.equals(dateRolloutNode.getProperty(PARAM_STATUS).getString())) {
							dateRolloutNode.setProperty(PARAM_STATUS, STATUS_QUEUED);
							session.save();
							activations.add(dateRolloutNode);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return activations;
	}

	private String[] getPages(Node n, Session session) throws RepositoryException {
		if (n.hasProperty("pages")) {
			Value[] values = n.getProperty("pages").getValues();
			String[] pages = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				pages[i] = values[i].getString();
			}
			n.setProperty("pages", new String[0]);
			session.save();
			return pages;
		} else {
			return new String[0];
		}
	}
	
	private void activateAndBuildCache(HashMap<String, TreeSet<String>> toBuild, TreeSet<String> activatedPages,
			Session session, Node dateNode, Boolean crawl, PageActivationReporter reporter) {
		Set<String> councilDomainsSet = new TreeSet<String>(toBuild.keySet());
		if(!crawl){
			reporter.report("Activating all pages immediately");
			for(String domain : councilDomainsSet){
				TreeSet<String> pagesToActivate = toBuild.get(domain);
				for(String pageToActivate : pagesToActivate){
					reporter.report("Activating " + pageToActivate);
					try{
						replicator.replicate(session, ReplicationActionType.ACTIVATE, pageToActivate);
					}catch(Exception e){
						reporter.report("Failed to activate " + pageToActivate);
					}
					activatedPages.add(pageToActivate);
				} 
			}
			return;
		}else{
			int batchSize = PageActivationUtil.getGroupSize(rr);
			int sleepTime = PageActivationUtil.getMinutes(rr) * 60 * 1000;
			int depth = PageActivationUtil.getCrawlDepth(rr);
			int counter = 0;
			String[] ipsGroupOne = null;
			String[] ipsGroupTwo = null;
			reporter.report("Obtaining IP addresses for crawling");
			try {
				ipsGroupOne = PageActivationUtil.getIps(rr, 1);
			} catch (Exception e) {
				log.error("GS Page Activator - failed to retrieve dispatcher 1 ips");
				reporter.report("Failed to retrieve any dispatcher 1 ips");
			}
			try {
				ipsGroupTwo = PageActivationUtil.getIps(rr, 2);
			} catch (Exception e) {
				log.error("GS Page Activator - failed to retrieve dispatcher 2 ips");
				reporter.report("Failed to retrieve any dispatcher 2 ips");
			}
			for(String domain:councilDomainsSet){
				counter++;
				if ((counter > batchSize) && (counter % batchSize == 0)) {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						log.error("GS Page Activator - could not sleep");
						reporter.report("Waiting Failed - process (including activations) cancelled prematurely");
						break;
					}
				}
				try {
					TreeSet<String> pagesToActivate = toBuild.get(domain);
					for (String pageToActivate : pagesToActivate) {
						reporter.report("Activating " + pageToActivate);
						replicator.replicate(session, ReplicationActionType.ACTIVATE, pageToActivate);
						activatedPages.add(pageToActivate);
					}
					reporter.report("Waiting 5 sec for stat file to update before cache build");
					try {
						// Wait 5 seconds for stat file to update
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						log.error("GS Page Activator - could not sleep after replication");
						reporter.report("5 second break failed - process concluded prematurely");
						break;
					}
					reporter.report("Crawling " + domain);
					for (int l = 0; l < ipsGroupOne.length; l++) {
						Thread dispatcherIPOneThread = null;
						Thread dispatcherIPTwoThread = null;
						ArrayList<String> dispatcher1StatusList = new ArrayList<String>();
						if (ipsGroupOne[l] != null) {
							Runnable dispatcherIPOneRunnable = new CacheThread("/", domain, ipsGroupOne[l], "",
									dispatcher1StatusList, "Dispatcher 1 #" + l + 1, depth);
							dispatcherIPOneThread = new Thread(dispatcherIPOneRunnable, "dispatcherGroupOneThread" + l);
							dispatcherIPOneThread.start();
						}
						ArrayList<String> dispatcher2StatusList = new ArrayList<String>();
						if (ipsGroupTwo != null && ipsGroupTwo.length >= l + 1 && ipsGroupTwo[l] != null) {
							Runnable dispatcherIPTwoRunnable = new CacheThread("/", domain, ipsGroupTwo[l], "",
									dispatcher2StatusList, "Dispatcher 2 #" + l + 1, depth);
							dispatcherIPTwoThread = new Thread(dispatcherIPTwoRunnable, "dispatcherGroupTwoThread" + l);
							dispatcherIPTwoThread.start();
						}
						if (dispatcherIPOneThread != null) {
							dispatcherIPOneThread.join();
							for (String s : dispatcher1StatusList) {
								reporter.report(s);
							}
						}
						if (dispatcherIPTwoThread != null) {
							dispatcherIPTwoThread.join();
							for (String s : dispatcher2StatusList) {
								reporter.report(s);
							}
						}
					}
					toBuild.remove(domain);
				} catch (Exception e) {
					log.error("An error occurred while processing: " + domain);
					try {
						reporter.report("Cache may not have built correctly for " + domain);
						Node detailedReportNode = dateNode.addNode(domain, "nt:unstructured");
						detailedReportNode.setProperty("message", e.getMessage());
					} catch (Exception e1) {
						log.error("GS Page Activator - An exception occurred while creating error node");
						log.error(e.getMessage());
						continue;
					}
				}
			}
		}
	}
	
	private HashMap<String, TreeSet<String>> groupByCouncil(String[] pages, ResourceResolver rr,
			TreeSet<String> unmapped) {
		HashMap<String, TreeSet<String>> map = new HashMap <String, TreeSet<String>>();
		for(String page : pages){
			try{
				String domain = getDomain(rr, page);
				TreeSet<String> set;
				if(map.get(domain) != null){
					set = map.get(domain);
				}else{
					set = new TreeSet<String>();
				}
				set.add(page);
				map.put(domain, set);
			}catch(Exception e){
				unmapped.add(page);
				log.error("Could not map page " + page);
				continue;
			}
		}
		return map;
	}
	
	private String getDomain(ResourceResolver resolver, String path) throws Exception {
		String mappingPath, homepagePath;
		Set<String> runmodes = settingsService.getRunModes();
		if(runmodes.contains("prod")){
			mappingPath = "/etc/map.publish.prod/http";
		}else if(runmodes.contains("uat")){
			mappingPath = "/etc/map.publish.uat/http";
		}else if(runmodes.contains("stage")){
			mappingPath = "/etc/map.publish.stage/http";
		}else if(runmodes.contains("dev")){
			mappingPath = "/etc/map.publish.dev/http";
		}else if(runmodes.contains("local")){
			mappingPath = "/etc/map.publish.local/http";
		}else{
			mappingPath = "/etc/map.publish/httpd";
		}
		
		Resource pageRes = resolver.resolve(path);
		Page pagePage = pageRes.adaptTo(Page.class);
		Page homePage = pagePage.getAbsoluteParent(2);
		homepagePath = homePage.getPath() + ".html";
		
		Session session = resolver.adaptTo(Session.class);
		QueryManager qm = session.getWorkspace().getQueryManager();
		String query = "SELECT [sling:match] FROM [sling:Mapping] as s WHERE ISDESCENDANTNODE(s,'" 
		+ mappingPath + "') AND [sling:internalRedirect]='" + homepagePath + "'";
	    Query q = qm.createQuery(query, Query.JCR_SQL2); 
	    QueryResult result = q.execute();
	    RowIterator rowIt = result.getRows();
	    String toReturn = rowIt.nextRow().getValue("sling:match").getString();
	    if(toReturn.endsWith("/$")){
	    	toReturn = toReturn.substring(0,toReturn.length() - 2);
	    }
	    return toReturn;
	}
	
	@Deactivate
	private void deactivate(ComponentContext componentContext) {
		log.info("GS Page Activation Service Deactivated.");
	}
	
}