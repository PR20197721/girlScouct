package org.girlscouts.web.councilupdate.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import javax.mail.MessagingException;
import org.girlscouts.web.components.GSEmailAttachment;
import org.girlscouts.web.components.PageActivationReporter;
import org.girlscouts.web.components.PageActivationUtil;
import org.girlscouts.web.constants.PageActivationConstants;
import org.girlscouts.web.councilrollout.GirlScoutsNotificationAction;
import org.girlscouts.web.councilupdate.CacheThread;
import org.girlscouts.web.councilupdate.PageActivator;
import org.girlscouts.web.service.email.GSEmailService;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.mail.EmailException;
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
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.cq.wcm.msm.api.RolloutManager;

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

public class PageActivatorImpl
		implements Runnable, PageActivator, PageActivationConstants, PageActivationConstants.Email {
	
	private static Logger log = LoggerFactory.getLogger(PageActivatorImpl.class);
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

		List<Node> queuedActivations = queueDelayedActivations();
		if (queuedActivations != null) {
			List<Node> activationsToCrawl = new ArrayList<Node>();
			for (Node dateRolloutNode : queuedActivations) {
				Boolean crawl = true;
				try {
					crawl = dateRolloutNode.getProperty(PARAM_CRAWL).getBoolean();
					if (crawl) {
						activationsToCrawl.add(dateRolloutNode);
					} else {
						processActivationNode(dateRolloutNode);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (!activationsToCrawl.isEmpty()) {
				try {
					if (activationsToCrawl.size() > 1) {
						aggregateActivateCrawl(activationsToCrawl);
					} else {
						processActivationNode(activationsToCrawl.get(0));
					}
				} catch (RepositoryException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void processActivationNode(Node dateRolloutNode) throws RepositoryException {
		long begin = System.currentTimeMillis();
		Session session = dateRolloutNode.getSession();
		try {
			if (dateRolloutNode.hasProperty(PARAM_STATUS)
					&& !STATUS_PROCESSING.equals(dateRolloutNode.getProperty(PARAM_STATUS).getString())) {
				dateRolloutNode.setProperty(PARAM_STATUS, STATUS_PROCESSING);
				session.save();
			}
		} catch (Exception e) {
			log.error("GS Page Activator - Failed to check if process in progress already");
			return;
		}
		Boolean delay = false, crawl = false, activate = true;
		try {
			delay = dateRolloutNode.getProperty(PARAM_DELAY).getBoolean();
		} catch (Exception e) {
		}
		try {
			crawl = dateRolloutNode.getProperty(PARAM_CRAWL).getBoolean();
		} catch (Exception e) {
		}
		try {
			activate = dateRolloutNode.getProperty(PARAM_ACTIVATE).getBoolean();
		} catch (Exception e) {
		}
	
		PageActivationReporter reporter = new PageActivationReporter(dateRolloutNode, session);
		reporter.report("Initializing process");
		Set<String> pages = null;
		reporter.report("Retrieving page queue");
		try {
			pages = PageActivationUtil.getPages(dateRolloutNode);
		} catch (Exception e) {
			reporter.report("Failed to get initial page count");
			log.error("GS Page Activator - failed to get initial page count");
			e.printStackTrace();
			PageActivationUtil.markActivationFailed(session, dateRolloutNode);
			return;
		}
		if (pages.isEmpty()) {
			reporter.report("No pages found in page queue. Will not proceed");
			PageActivationUtil.markActivationFailed(session, dateRolloutNode);
			return;
		}
		Set<String> activatedPages = new TreeSet<String>();
		TreeSet<String> unmappedPages = new TreeSet<String>();
		HashMap<String, TreeSet<String>> toActivate = new HashMap<String, TreeSet<String>>();
		reporter.report("Arranging " + pages.size() + " pages by council");
		try {
			toActivate = groupByCouncil(pages, rr, unmappedPages);
		} catch (Exception e) {
			reporter.report("Failed to sort pages by council");
			log.error("GS Page Activator - failed to arrange councils");
			PageActivationUtil.markActivationFailed(session, dateRolloutNode);
		}
		if (unmappedPages.size() > 0) {
			for (String u : unmappedPages) {
				reporter.report("Page " + u + " could not be mapped to an external url");
			}
		}
		if (toActivate.size() > 0 && activate) {
			if (crawl) {
				activatedPages = activateAndBuildCache(toActivate, dateRolloutNode, reporter);
			} else {
				activatedPages = activatePages(toActivate, dateRolloutNode, reporter);
			}
		}
		long end = System.currentTimeMillis();
		reporter.report("Sending report email");
		try {
			sendReportEmail(begin, end, delay, crawl, activatedPages, dateRolloutNode, session, reporter);
			reporter.report("Report email delivered");
		} catch (Exception e) {
			reporter.report("Unable to send report email");
			log.error("Girl Scouts Page Activator - Unable to send report email");
			log.error(e.getMessage());
		}
	
		try {
			dateRolloutNode.setProperty(PARAM_ACTIVATED_PAGES,
					activatedPages.toArray(new String[activatedPages.size()]));
			dateRolloutNode.setProperty(PARAM_UNMAPPED_PAGES, unmappedPages.toArray(new String[unmappedPages.size()]));
			dateRolloutNode.setProperty(PARAM_STATUS, STATUS_COMPLETE);
			session.save();
			reporter.report("Moving " + dateRolloutNode.getPath() + " to " + dateRolloutNode.getParent().getPath() + "/"
					+ COMPLETED_NODE + "/" + dateRolloutNode.getName());
			PageActivationUtil.archive(dateRolloutNode);
			log.info("GS Page Activator - Process completed");
			reporter.report("Process completed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void aggregateActivateCrawl(List<Node> activationsToCrawl) throws RepositoryException {
		if (activationsToCrawl != null && !activationsToCrawl.isEmpty()) {
			Node aggregatedRolloutNode = getDateRolloutNode();
			for (Node activationNode : activationsToCrawl) {
				aggregate(activationNode, aggregatedRolloutNode);
			}
			Set<String> pages = PageActivationUtil.getPages(aggregatedRolloutNode);
			if (aggregatedRolloutNode != null && pages != null && !pages.isEmpty()) {
				processActivationNode(aggregatedRolloutNode);
			}
		}
	}

	private void aggregate(Node activationNode, Node aggregatedRolloutNode) {
		try {
			Set<String> pages = PageActivationUtil.getPages(activationNode);
			if (pages != null && !pages.isEmpty()) {
				Set<String> aggregatedPages = PageActivationUtil.getPages(aggregatedRolloutNode);
				aggregatedPages.addAll(pages);
				aggregatedRolloutNode.setProperty(PARAM_PAGES, aggregatedPages.toArray(new String[pages.size()]));
				aggregatedRolloutNode.setProperty(PARAM_CRAWL, Boolean.TRUE);
				aggregatedRolloutNode.setProperty(PARAM_DELAY, Boolean.TRUE);
				aggregatedRolloutNode.setProperty(PARAM_ACTIVATE, Boolean.TRUE);
				Session session = aggregatedRolloutNode.getSession();
				session.move(activationNode.getPath(),
						aggregatedRolloutNode.getPath() + "/" + activationNode.getName());
				session.save();
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

	private boolean isPublisher() {
		if (settingsService.getRunModes().contains("publish")) {
			return true;
		}
		return false;
	}

	private List<Node> queueDelayedActivations() {
		List<Node> activations = null;
		Session session = rr.adaptTo(Session.class);
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
								&& STATUS_DELAYED.equals(dateRolloutNode.getProperty(PARAM_STATUS).getString())) {
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

	private Set<String> activatePages(HashMap<String, TreeSet<String>> toActivate, Node dateNode,
			PageActivationReporter reporter) {
		Set<String> activatedPages = new TreeSet<String>();
		Set<String> councilDomainsSet = new TreeSet<String>(toActivate.keySet());
		reporter.report("Activating all pages immediately");
		for (String domain : councilDomainsSet) {
			TreeSet<String> pagesToActivate = toActivate.get(domain);
			for (String pageToActivate : pagesToActivate) {
				reporter.report("Activating " + pageToActivate);
				try {
					replicator.replicate(dateNode.getSession(), ReplicationActionType.ACTIVATE, pageToActivate);
				} catch (Exception e) {
					reporter.report("Failed to activate " + pageToActivate);
				}
				activatedPages.add(pageToActivate);
			}
		}
		return activatedPages;
	}

	private TreeSet<String> activateAndBuildCache(HashMap<String, TreeSet<String>> toActivate,
			Node dateNode, PageActivationReporter reporter) {
		TreeSet<String> activatedPages = new TreeSet<String>();
		Set<String> councilDomainsSet = new TreeSet<String>(toActivate.keySet());
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
		for (String domain : councilDomainsSet) {
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
				TreeSet<String> pagesToActivate = toActivate.get(domain);
				for (String pageToActivate : pagesToActivate) {
					reporter.report("Activating " + pageToActivate);
					replicator.replicate(dateNode.getSession(), ReplicationActionType.ACTIVATE, pageToActivate);
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
				toActivate.remove(domain);
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
		return activatedPages;
	}
	
	private HashMap<String, TreeSet<String>> groupByCouncil(Set<String> pages, ResourceResolver rr,
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
			mappingPath = "/etc/map.publish/http";
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
	
	private void sendReportEmail(long startTime, long endTime, Boolean delay, Boolean crawl, Set<String> builtPages,
			Node dateNode, Session session, PageActivationReporter reporter) throws RepositoryException {
		ArrayList<String> emails = new ArrayList<String>();
		if (builtPages.size() > 0) {
			reporter.report("Retrieving email addresses for report");
			if (dateNode.hasProperty("emails")) {
				Value[] values = dateNode.getProperty("emails").getValues();
				for (Value v : values) {
					emails.add(v.toString());
				}
			} else {
				reporter.report("No email address property found. Can't send any emails");
				return;
			}
			if (emails.size() < 1) {
				reporter.report("No email addresses found in email address property. Can't send any emails.");
				return;
			}
			StringBuffer html = new StringBuffer();
			html.append(DEFAULT_COMPLETION_REPORT_HEAD);
			html.append("<body><p>The Girl Scouts Activation Process has just finished running.</p>");
			if (delay) {
				html.append("<p>It was of type - Scheduled Activation</p>");
			} else {
				if (crawl) {
					html.append("<p>It was of type - Immediate Activation with Crawl</p>");
				} else {
					html.append("<p>It was of type - Immediate Activation without Crawl</p>");
					;
				}
			}
			html.append("<p>Pages Activated:</p><ul>");
			for (String page : builtPages) {
				html.append("<li>" + page + "</li>");
			}
			html.append("</ul>");
			long timeDiff = (endTime - startTime) / 60000;
			html.append("<p>The entire process took approximately " + timeDiff + " minutes to complete</p>");
			html.append("</body></html>");

			StringBuffer logData = new StringBuffer();
			logData.append("The following is the process log for the activation process so far:\n\n");
			for (String s : reporter.getStatusList()) {
				logData.append(s + "/n");
			}
			try {
				Set<GSEmailAttachment> attachments = new HashSet<GSEmailAttachment>();
				String fileName = DEFAULT_COMPLETION_REPORT_ATTACHMENT + "_" + dateNode.getName();
				GSEmailAttachment attachment = new GSEmailAttachment(fileName, logData.toString(),
						GSEmailAttachment.MimeType.TEXT_PLAIN);
				attachments.add(attachment);
				gsEmailService.sendEmail(DEFAULT_COMPLETION_REPORT_SUBJECT, emails, html.toString(), attachments);
			} catch (EmailException | MessagingException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	private Node getDateRolloutNode() throws RepositoryException {
		Node dateRolloutNode = null;
		try {
			Session session = rr.adaptTo(Session.class);
			Resource etcRes = rr.resolve("/etc");
			Node etcNode = etcRes.adaptTo(Node.class);
			Node activationsNode = null;
			Node activationTypeNode = null;
			String date = PageActivationUtil.getDateRes();
			if (etcNode.hasNode(ACTIVATIONS_NODE)) {
				activationsNode = etcNode.getNode(ACTIVATIONS_NODE);
			} else {
				activationsNode = etcNode.addNode(ACTIVATIONS_NODE);
			}
			if (activationsNode.hasNode(DELAYED_NODE)) {
				activationTypeNode = activationsNode.getNode(DELAYED_NODE);
			} else {
				activationTypeNode = activationsNode.addNode(DELAYED_NODE);
				session.save();
			}
			if (activationTypeNode.hasNode(date)) {
				dateRolloutNode = activationTypeNode.getNode(date);
			} else {
				dateRolloutNode = activationTypeNode.addNode(date);
				session.save();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateRolloutNode;
	}
}