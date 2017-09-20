package org.girlscouts.web.councilupdate.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
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
import org.girlscouts.web.councilupdate.PageReplicator;
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
 * Girl Scouts Page Replicator - DL
 * This process activates a queue of pages, in batches, with a timed delay between batches
 * This system of staggering activations allows the dispatcher caches to rebuild during large rollouts
 * The process runs at a scheduled time as a cron job, but it can also be called as a sling service and run at any time with the run() method
 */
@Component(
		metatype = true, 
		immediate = true,
		label = "Girl Scouts Page Replication Service", 
		description = "Activates/Deletes pages at night to make sure cache-clearing does not interfere with traffic on production sites" 
		)
@Service(value = {Runnable.class, PageReplicator.class})
@Properties({
		@Property(name = "service.description", value = "Girl Scouts GS Replication Service", propertyPrivate = true),
	@Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate=true), 
	@Property(name = "scheduler.expression", label="scheduler.expression", description="cron expression"),
	@Property(name = "scheduler.concurrent", boolValue=false, propertyPrivate=true),
		@Property(name = "scheduler.runOn", value = "SINGLE", propertyPrivate = true)
})

public class PageReplicatorImpl
		implements Runnable, PageReplicator, PageActivationConstants, PageActivationConstants.Email {
	
	private static Logger log = LoggerFactory.getLogger(PageReplicatorImpl.class);
	@Reference
	protected ResourceResolverFactory resolverFactory;
	@Reference
	protected RolloutManager rolloutManager;
	@Reference 
	protected Replicator replicator;
	@Reference
	protected SlingSettingsService settingsService;
	@Reference
	protected GSEmailService gsEmailService;
	@Reference
	protected GirlScoutsNotificationAction notificationAction;
	@Reference
	protected LiveRelationshipManager relationManager;
	
	protected ResourceResolver rr;
	
	@Activate
	private void activate(ComponentContext context) {
		try {
			rr= resolverFactory.getAdministrativeResourceResolver(null);
		} catch (LoginException e) {
			e.printStackTrace();
		}
		log.info("Girlscouts Page Replication Service Deactivated.");
	}

	@Override
	public void run() {
		log.error("Running PageReplicatorImpl");
		if (isPublisher()) {
			log.error("Publisher instance - will not execute Page Replicator");
			return;
		}

		List<Node> queuedReplications = queueDelayedReplications();
		if (queuedReplications != null) {
			List<Node> replicationsToCrawl = new ArrayList<Node>();
			for (Node dateRolloutNode : queuedReplications) {
				Boolean crawl = true;
				try {
					crawl = dateRolloutNode.getProperty(PARAM_CRAWL).getBoolean();
					if (crawl) {
						replicationsToCrawl.add(dateRolloutNode);
					} else {
						processReplicationNode(dateRolloutNode);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (!replicationsToCrawl.isEmpty()) {
				try {
					if (replicationsToCrawl.size() > 1) {
						aggregateReplicateCrawl(replicationsToCrawl);
					} else {
						processReplicationNode(replicationsToCrawl.get(0));
					}
				} catch (RepositoryException e) {
					e.printStackTrace();
				}
			}
		}
		log.error("Finished PageReplicatorImpl");
	}

	@Override
	public void processReplicationNode(Node dateRolloutNode) throws RepositoryException {
		long begin = System.currentTimeMillis();
		Session session = dateRolloutNode.getSession();
		try {
			if (dateRolloutNode.hasProperty(PARAM_STATUS)
					&& !STATUS_PROCESSING.equals(dateRolloutNode.getProperty(PARAM_STATUS).getString())
					&& !STATUS_FAILED.equals(dateRolloutNode.getProperty(PARAM_STATUS).getString())
					&& !STATUS_COMPLETE.equals(dateRolloutNode.getProperty(PARAM_STATUS).getString())
					&& !STATUS_AGGREGATED.equals(dateRolloutNode.getProperty(PARAM_STATUS).getString())
					&& !STATUS_QUEUED.equals(dateRolloutNode.getProperty(PARAM_STATUS).getString())) {
				dateRolloutNode.setProperty(PARAM_STATUS, STATUS_PROCESSING);
				session.save();
			}
		} catch (Exception e) {
			log.error("Girlscouts Page Replicator encountered error: ", e);
			return;
		}
		Boolean delay = false, crawl = false;
		try {
			delay = dateRolloutNode.getProperty(PARAM_DELAY).getBoolean();
		} catch (Exception e) {
		}
		try {
			crawl = dateRolloutNode.getProperty(PARAM_CRAWL).getBoolean();
		} catch (Exception e) {
		}
	
		PageActivationReporter reporter = new PageActivationReporter(dateRolloutNode, session);
		reporter.report("Initializing process");
		Set<String> pages = null;
		Set<String> pagesToDelete = null;
		reporter.report("Retrieving page queue");
		try {
			pages = PageActivationUtil.getPagesToActivate(dateRolloutNode);
		} catch (Exception e) {
			reporter.report("Failed to get initial pages to activate count");
			e.printStackTrace();
		}
		try {
			pagesToDelete = PageActivationUtil.getPagesToDelete(dateRolloutNode);
		} catch (Exception e) {
			reporter.report("Failed to get initial pages to delete count");
			e.printStackTrace();
		}
		if (pages.isEmpty() && pagesToDelete.isEmpty()) {
			reporter.report("No pages found in page queue. Will not proceed");
			PageActivationUtil.markActivationFailed(session, dateRolloutNode);
			return;
		}
		TreeSet<String> unmappedPages = new TreeSet<String>();
		Map<String, Set<String>> replicatedPages = new HashMap<String, Set<String>>();
		HashMap<String, TreeSet<String>> toActivate = new HashMap<String, TreeSet<String>>();
		HashMap<String, TreeSet<String>> toDelete = new HashMap<String, TreeSet<String>>();
		reporter.report("Arranging " + pages.size() + " pages by council");
		try {
			toActivate = groupByCouncil(pages, unmappedPages);
		} catch (Exception e) {
			reporter.report("Failed to group pages to activate by council");
			PageActivationUtil.markActivationFailed(session, dateRolloutNode);
		}
		try {
			toDelete = groupByCouncil(pagesToDelete, unmappedPages);
		} catch (Exception e) {
			reporter.report("Failed to group pages to delete by council");
			PageActivationUtil.markActivationFailed(session, dateRolloutNode);
		}
		if (unmappedPages.size() > 0) {
			for (String u : unmappedPages) {
				reporter.report("Page " + u + " could not be mapped to an external url");
			}
		}
		if (toActivate.size() > 0 || toDelete.size() > 0) {
			if (crawl) {
				replicatedPages = replicateAndCrawl(toActivate, toDelete, dateRolloutNode, reporter);
			} else {
				replicatedPages = replicatePages(toActivate, toDelete, dateRolloutNode, reporter);
			}
		}
		long end = System.currentTimeMillis();
		reporter.report("Sending report email");
		try {
			sendReportEmail(begin, end, delay, crawl, replicatedPages, dateRolloutNode, reporter);
			reporter.report("Report email delivered");
		} catch (Exception e) {
			reporter.report("Unable to send report email");
		}
	
		try {
			Set<String> activatedPages = replicatedPages.get(PARAM_ACTIVATED_PAGES);
			Set<String> deletedPages = replicatedPages.get(PARAM_DELETED_PAGES);
			dateRolloutNode.setProperty(PARAM_ACTIVATED_PAGES,
					activatedPages.toArray(new String[activatedPages.size()]));
			dateRolloutNode.setProperty(PARAM_DELETED_PAGES, deletedPages.toArray(new String[deletedPages.size()]));
			dateRolloutNode.setProperty(PARAM_UNMAPPED_PAGES, unmappedPages.toArray(new String[unmappedPages.size()]));
			dateRolloutNode.setProperty(PARAM_STATUS, STATUS_COMPLETE);
			session.save();
			reporter.report("Moving " + dateRolloutNode.getPath() + " to " + dateRolloutNode.getParent().getPath() + "/"
					+ COMPLETED_NODE + "/" + dateRolloutNode.getName());
			PageActivationUtil.archive(dateRolloutNode);
			reporter.report("Process completed");
		} catch (Exception e) {
			log.error("Girlscouts Page Replicator encountered error: ", e);
		}
	}

	protected boolean isPublisher() {
		if (settingsService.getRunModes().contains("publish")) {
			return true;
		}
		return false;
	}

	protected Map<String, Set<String>> replicatePages(HashMap<String, TreeSet<String>> toActivate,
			HashMap<String, TreeSet<String>> toDelete, Node dateNode,
			PageActivationReporter reporter) {
		reporter.report("Replicating all pages immediately");
		Map<String, Set<String>> replicatedPages = new HashMap<String, Set<String>>();
		Set<String> activatedPages = new TreeSet<String>();
		Set<String> deletedPages = new TreeSet<String>();
		Set<String> councilDomainsSet = new TreeSet<String>();
		councilDomainsSet.addAll(toActivate.keySet());
		councilDomainsSet.addAll(toDelete.keySet());
		for (String domain : councilDomainsSet) {
			Map<String, Set<String>> replicatedPagesForDomain = replicate(dateNode, reporter,
					toDelete.get(domain), toActivate.get(domain));
			activatedPages.addAll(replicatedPagesForDomain.get(PARAM_ACTIVATED_PAGES));
			deletedPages.addAll(replicatedPagesForDomain.get(PARAM_DELETED_PAGES));
		}
		replicatedPages.put(PARAM_ACTIVATED_PAGES, activatedPages);
		replicatedPages.put(PARAM_DELETED_PAGES, deletedPages);
		return replicatedPages;
	}

	protected Map<String, Set<String>> replicateAndCrawl(HashMap<String, TreeSet<String>> toActivate,
			HashMap<String, TreeSet<String>> toDelete, Node dateNode, PageActivationReporter reporter) {
		Map<String, Set<String>> replicatedPages = new HashMap<String, Set<String>>();
		Set<String> activatedPages = new TreeSet<String>();
		Set<String> deletedPages = new TreeSet<String>();
		Set<String> councilDomainsSet = new TreeSet<String>();
		councilDomainsSet.addAll(toActivate.keySet());
		councilDomainsSet.addAll(toDelete.keySet());
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
			reporter.report("Failed to retrieve any dispatcher 1 ips");
		}
		try {
			ipsGroupTwo = PageActivationUtil.getIps(rr, 2);
		} catch (Exception e) {
			reporter.report("Failed to retrieve any dispatcher 2 ips");
		}
		for (String domain : councilDomainsSet) {
			counter++;
			reporter.report(
					"Processing Council " + domain + " [" + counter + " out of " + councilDomainsSet.size() + "]");
			if ((counter > batchSize) && (counter % batchSize == 0)) {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					reporter.report("Waiting Failed - process (including activations) cancelled prematurely");
					log.error("Girlscouts Page Replicator encountered error: ", e);
					break;
				}
			}
			try {
				Map<String, Set<String>> replicatedPagesForDomain = replicate(dateNode, reporter, toDelete.get(domain),
						toActivate.get(domain));
				activatedPages.addAll(replicatedPagesForDomain.get(PARAM_ACTIVATED_PAGES));
				deletedPages.addAll(replicatedPagesForDomain.get(PARAM_DELETED_PAGES));
				reporter.report("Waiting 5 sec for stat file to update before cache build");
				try {
					// Wait 5 seconds for stat file to update
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					reporter.report("5 second break failed - process concluded prematurely");
					log.error("Girlscouts Page Replicator encountered error: ", e);
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
				log.error("Girlscouts Page Replicator An error occurred while processing: " + domain);
				log.error("Girlscouts Page Replicator encountered error: ", e);
				try {
					reporter.report("Cache may not have built correctly for " + domain);
					Node detailedReportNode = dateNode.addNode(domain, "nt:unstructured");
					detailedReportNode.setProperty("message", e.getMessage());
				} catch (Exception e1) {
					log.error("Girlscouts Page Replicator encountered error: ", e1);
					continue;
				}
			}
		}
		replicatedPages.put(PARAM_ACTIVATED_PAGES, activatedPages);
		replicatedPages.put(PARAM_DELETED_PAGES, deletedPages);
		return replicatedPages;
	}
	
	protected HashMap<String, TreeSet<String>> groupByCouncil(Set<String> pages, TreeSet<String> unmapped) {
		HashMap<String, TreeSet<String>> map = new HashMap <String, TreeSet<String>>();
		for(String page : pages){
			try{
				String domain = getDomain(page);
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
				log.error("Girlscouts Page Replicator encountered error: ", e);
				continue;
			}
		}
		return map;
	}
	
	protected String getDomain(String path) throws Exception {
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
		
		Resource pageRes = rr.resolve(path);
		Page pagePage = pageRes.adaptTo(Page.class);
		Page homePage = pagePage.getAbsoluteParent(2);
		homepagePath = homePage.getPath() + ".html";
		
		Session session = rr.adaptTo(Session.class);
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
	
	protected void sendReportEmail(long startTime, long endTime, Boolean delay, Boolean crawl,
			Map<String, Set<String>> replicatedPages,
			Node dateNode, PageActivationReporter reporter) throws RepositoryException {
		if (replicatedPages.size() > 0) {
			Set<String> activatedPages = replicatedPages.get(PARAM_ACTIVATED_PAGES);
			Set<String> deletedPages = replicatedPages.get(PARAM_DELETED_PAGES);
			reporter.report("Retrieving email addresses for report");
			List<String> emails = PageActivationUtil.getReportEmails(rr);
			if (emails != null && emails.size() > 0) {
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
				if (deletedPages != null) {
					html.append("<p>Deleted Pages:</p><ul>");
					for (String page : deletedPages) {
						html.append("<li>" + page + "</li>");
					}
					html.append("</ul>");
				}
				if (activatedPages != null) {
					html.append("<p>Activated Pages:</p><ul>");
					for (String page : activatedPages) {
						html.append("<li>" + page + "</li>");
					}
					html.append("</ul>");
				}
				long timeDiff = (endTime - startTime) / 60000;
				html.append("<p>The entire process took approximately " + timeDiff + " minutes to complete</p>");
				html.append("</body></html>");

				StringBuffer logData = new StringBuffer();
				logData.append("The following is the process log for the activation process so far:\n\n");
				for (String s : reporter.getStatusList()) {
					logData.append(s + "\n");
				}
				try {
					Set<GSEmailAttachment> attachments = new HashSet<GSEmailAttachment>();
					String fileName = DEFAULT_COMPLETION_REPORT_ATTACHMENT + "_" + dateNode.getName();
					GSEmailAttachment attachment = new GSEmailAttachment(fileName, logData.toString(), null,
							GSEmailAttachment.MimeType.TEXT_PLAIN);
					attachments.add(attachment);
					gsEmailService.sendEmail(DEFAULT_COMPLETION_REPORT_SUBJECT, emails, html.toString(), attachments);
				} catch (EmailException | MessagingException | IOException e) {
					e.printStackTrace();
				}
			} else {
				reporter.report("No email addresses found in email address property. Can't send any emails.");
			}
		}
	}

	private Map<String, Set<String>> replicate(Node dateNode, PageActivationReporter reporter,
			TreeSet<String> pagesToDelete, TreeSet<String> pagesToActivate) {
		Map<String, Set<String>> replicatedPages = new HashMap<String, Set<String>>();
		if (pagesToDelete != null && !pagesToDelete.isEmpty()) {
			Set<String> deletedPages = new TreeSet<String>();
			reporter.report("Replicating Deletions");
			for (String pageToDelete : pagesToDelete) {
				reporter.report("Deactivating " + pageToDelete);
				try {
					replicator.replicate(dateNode.getSession(), ReplicationActionType.DEACTIVATE, pageToDelete);
				} catch (Exception e) {
					reporter.report("Failed to deactivate " + pageToDelete);
					log.error("Girlscouts Page Replicator encountered error: ", e);
				}
				reporter.report("Deleting " + pageToDelete);
				try {
					replicator.replicate(dateNode.getSession(), ReplicationActionType.DELETE, pageToDelete);
					deletedPages.add(pageToDelete);
				} catch (Exception e) {
					reporter.report("Failed to delete " + pageToDelete);
					log.error("Girlscouts Page Replicator encountered error: ", e);
				}
			}
			replicatedPages.put(PARAM_DELETED_PAGES, deletedPages);
		}
		if (pagesToActivate != null && !pagesToActivate.isEmpty()) {
			Set<String> activatedPages = new TreeSet<String>();
			reporter.report("Replicating Activations");
			for (String pageToActivate : pagesToActivate) {
				reporter.report("Activating " + pageToActivate);
				try {
					replicator.replicate(dateNode.getSession(), ReplicationActionType.ACTIVATE, pageToActivate);
					activatedPages.add(pageToActivate);
				} catch (Exception e) {
					reporter.report("Failed to activate " + pageToActivate);
					log.error("Girlscouts Page Replicator encountered error: ", e);
				}
			}
			replicatedPages.put(PARAM_ACTIVATED_PAGES, activatedPages);
		}
		return replicatedPages;
	}

	private List<Node> queueDelayedReplications() {
		List<Node> activations = null;
		Session session = rr.adaptTo(Session.class);
		Resource gsDelayedRes = rr.resolve(PAGE_ACTIVATIONS_PATH + "/" + DELAYED_NODE);
		if (!gsDelayedRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
			Node gsDelayedNode = gsDelayedRes.adaptTo(Node.class);
			activations = new ArrayList<Node>();
			try {
				NodeIterator it = gsDelayedNode.getNodes();
				if (it != null) {
					while (it.hasNext()) {
						Node dateRolloutNode = it.nextNode();
						if (dateRolloutNode.hasProperty(PARAM_STATUS)
								&& STATUS_DELAYED.equals(dateRolloutNode.getProperty(PARAM_STATUS).getString())) {
							dateRolloutNode.setProperty(PARAM_STATUS, STATUS_QUEUED);
							session.save();
							activations.add(dateRolloutNode);
						}
					}
					it.hasNext();
				}
			} catch (RepositoryException e1) {
				e1.printStackTrace();
			}
			try {
				activations.add(getEventsReplicationNode(gsDelayedRes));
			} catch (Exception e) {
				log.error("Girlscouts Page Replicator encountered error: ", e);
			}
		}
		return activations;
	}

	private Node getEventsReplicationNode(Resource gsDelayedRes) throws RepositoryException {
		Node eventReplicationNode = null;
		try {
			Resource etcRes = rr.resolve("/etc");
			Node etcNode = etcRes.adaptTo(Node.class);
			Node replicationsNode = null;
			if (etcNode.hasNode(EVENT_ACTIVATIONS_NODE)) {
				replicationsNode = etcNode.getNode(EVENT_ACTIVATIONS_NODE);
			} else {
				replicationsNode = etcNode.addNode(EVENT_ACTIVATIONS_NODE);
			}
			if (replicationsNode != null) {
				Set<String> pages = PageActivationUtil.getPagesToActivate(replicationsNode);
				if (pages != null && !pages.isEmpty()) {
					String eventsNodeName = "E-" + PageActivationUtil.getDateRes();
					Node gsDelayedNode = gsDelayedRes.adaptTo(Node.class);
					eventReplicationNode = gsDelayedNode.addNode(eventsNodeName);
					eventReplicationNode.setProperty(PARAM_PAGES, pages.toArray(new String[pages.size()]));
					eventReplicationNode.setProperty(PARAM_CRAWL, Boolean.TRUE);
					eventReplicationNode.setProperty(PARAM_DELAY, Boolean.TRUE);
					eventReplicationNode.setProperty(PARAM_STATUS, STATUS_CREATED);
					eventReplicationNode.getSession().save();
				}
				replicationsNode.setProperty(PARAM_PAGES, new String[] {});
				replicationsNode.getSession().save();
			}
		} catch (Exception e) {
			log.error("Girlscouts Page Replicator encountered error: ", e);
		}
		return eventReplicationNode;
	}

	private Node getAggregateDateRolloutNode() throws RepositoryException {
		Node dateRolloutNode = null;
		try {
			Session session = rr.adaptTo(Session.class);
			Resource etcRes = rr.resolve("/etc");
			Node etcNode = etcRes.adaptTo(Node.class);
			Node activationsNode = null;
			Node activationTypeNode = null;
			if (etcNode.hasNode(PAGE_ACTIVATIONS_NODE)) {
				activationsNode = etcNode.getNode(PAGE_ACTIVATIONS_NODE);
			} else {
				activationsNode = etcNode.addNode(PAGE_ACTIVATIONS_NODE);
			}
			if (activationsNode.hasNode(DELAYED_NODE)) {
				activationTypeNode = activationsNode.getNode(DELAYED_NODE);
			} else {
				activationTypeNode = activationsNode.addNode(DELAYED_NODE);
				session.save();
			}
			String aggregateNodeName = PageActivationUtil.getDateRes();
			if (activationTypeNode.hasNode(aggregateNodeName)) {
				dateRolloutNode = activationTypeNode.getNode(aggregateNodeName);
			} else {
				dateRolloutNode = activationTypeNode.addNode(aggregateNodeName);
				session.save();
			}
		} catch (Exception e) {
			log.error("Girlscouts Page Replicator encountered error: ", e);
		}
		return dateRolloutNode;
	}

	private void aggregate(Node replicationNode, Node aggregatedRolloutNode) {
		try {
			log.error("Girlscouts Page Replicator: Aggregating " + replicationNode.getPath() + " into "
						+ aggregatedRolloutNode.getPath());
			Set<String> aggregatedPagesToActivate = new TreeSet<String>();
			Set<String> aggregatedPagesToDelete = new TreeSet<String>();
			aggregatedPagesToActivate.addAll(PageActivationUtil.getPagesToActivate(aggregatedRolloutNode));
			aggregatedPagesToDelete.addAll(PageActivationUtil.getPagesToDelete(aggregatedRolloutNode));
			aggregatedPagesToActivate.addAll(PageActivationUtil.getPagesToActivate(replicationNode));
			aggregatedPagesToDelete.addAll(PageActivationUtil.getPagesToDelete(replicationNode));
			aggregatedRolloutNode.setProperty(PARAM_PAGES,
					aggregatedPagesToActivate.toArray(new String[aggregatedPagesToActivate.size()]));
			aggregatedRolloutNode.setProperty(PARAM_PAGES_TO_DELETE,
					aggregatedPagesToDelete.toArray(new String[aggregatedPagesToDelete.size()]));
			aggregatedRolloutNode.getSession().save();
			replicationNode.setProperty(PARAM_STATUS, STATUS_AGGREGATED);
			replicationNode.getSession().move(replicationNode.getPath(),
					aggregatedRolloutNode.getPath() + "/" + replicationNode.getName());
			replicationNode.getSession().save();
		} catch (RepositoryException e) {
			log.error("Girlscouts Page Replicator encountered error: ", e);
		}
	}

	private void aggregateReplicateCrawl(List<Node> replicationsToCrawl) throws RepositoryException {
		if (replicationsToCrawl != null && !replicationsToCrawl.isEmpty()) {
			log.error("Girlscouts Page Replicator: Aggregating " + replicationsToCrawl.size()
					+ " nodes to activate and crawl.");
			Node aggregatedRolloutNode = getAggregateDateRolloutNode();
			aggregatedRolloutNode.setProperty(PARAM_CRAWL, Boolean.TRUE);
			aggregatedRolloutNode.setProperty(PARAM_DELAY, Boolean.TRUE);
			aggregatedRolloutNode.setProperty(PARAM_STATUS, STATUS_CREATED);
			aggregatedRolloutNode.getSession().save();
			for (Node activationNode : replicationsToCrawl) {
				aggregate(activationNode, aggregatedRolloutNode);
			}
			aggregatedRolloutNode.getSession().save();
			processReplicationNode(aggregatedRolloutNode);
		}
	}

	@Deactivate
	private void deactivate(ComponentContext componentContext) {
		log.info("Girlscouts Page Replication Service Deactivated.");
	}
}