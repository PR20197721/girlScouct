package org.girlscouts.web.service.replication.impl;

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
import javax.mail.MessagingException;
import org.girlscouts.web.components.GSEmailAttachment;
import org.girlscouts.web.components.PageActivationReporter;
import org.girlscouts.web.components.PageReplicationUtil;
import org.girlscouts.web.constants.PageReplicationConstants;
import org.girlscouts.web.councilupdate.CacheThread;
import org.girlscouts.web.service.email.GSEmailService;
import org.girlscouts.web.service.replication.PageReplicator;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
		implements Runnable, PageReplicator, PageReplicationConstants, PageReplicationConstants.Email {
	
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
	protected LiveRelationshipManager relationManager;
	
	protected Map<String, Object> serviceParams;
	
	@Activate
	private void activate(ComponentContext context) {
		Map<String, Object> serviceParams = new HashMap<String, Object>();
		serviceParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-process-service");
		log.info("Girlscouts Page Replicator Activated.");
	}

	@Override
	public void run() {
		log.error("Running PageReplicatorImpl");
		if (isPublisher()) {
			log.error("Publisher instance - will not execute Page Replicator");
			return;
		}
		ResourceResolver rr = null;
		try {
			rr = resolverFactory.getServiceResourceResolver(serviceParams);
			List<Node> queuedReplications = queueDelayedReplications(rr);
			if (queuedReplications != null) {
				List<Node> replicationsToCrawl = new ArrayList<Node>();
				for (Node dateRolloutNode : queuedReplications) {
					Boolean crawl = true;
					try {
						crawl = dateRolloutNode.getProperty(PARAM_CRAWL).getBoolean();
						if (crawl) {
							replicationsToCrawl.add(dateRolloutNode);
						} else {
							processReplicationNode(dateRolloutNode, rr);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (!replicationsToCrawl.isEmpty()) {
					try {
						if (replicationsToCrawl.size() > 1) {
							aggregateReplicateCrawl(replicationsToCrawl, rr);
						} else {
							processReplicationNode(replicationsToCrawl.get(0), rr);
						}
					} catch (RepositoryException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (LoginException e) {
			log.error("Girlscouts PageReplicatorImpl encountered error: ", e);
		} finally {
			try {
				rr.close();
			} catch (Exception e) {
			}
		}
		log.error("Finished PageReplicatorImpl");
	}

	@Override
	public void processReplicationNode(Node dateRolloutNode, ResourceResolver rr) throws RepositoryException {
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
			PageReplicationUtil.markReplicationFailed(dateRolloutNode);
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
		Set<String> pagesToActivate = null;
		Set<String> pagesToDelete = null;
		reporter.report("Retrieving page queue");
		try {
			pagesToActivate = PageReplicationUtil.getPagesToActivate(dateRolloutNode);
		} catch (Exception e) {
			reporter.report("Failed to get initial pages to activate count");
			e.printStackTrace();
		}
		try {
			pagesToDelete = PageReplicationUtil.getPagesToDelete(dateRolloutNode);
		} catch (Exception e) {
			reporter.report("Failed to get initial pages to delete count");
			e.printStackTrace();
		}
		if (pagesToActivate.isEmpty() && pagesToDelete.isEmpty()) {
			reporter.report("No pages found in activate and delete queues. Will not proceed");
			PageReplicationUtil.markReplicationComplete(dateRolloutNode);
			PageReplicationUtil.archive(dateRolloutNode);
			return;
		}
		TreeSet<String> unmappedPages = new TreeSet<String>();
		Map<String, Set<String>> replicatedPages = new HashMap<String, Set<String>>();
		HashMap<String, TreeSet<String>> toActivate = new HashMap<String, TreeSet<String>>();
		HashMap<String, TreeSet<String>> toDelete = new HashMap<String, TreeSet<String>>();
		reporter.report("Arranging " + pagesToActivate.size() + " pages by council");
		try {
			toActivate = groupByCouncil(pagesToActivate, unmappedPages);
		} catch (Exception e) {
			reporter.report("Failed to group pages to activate by council");
			PageReplicationUtil.markReplicationFailed(dateRolloutNode);
		}
		try {
			toDelete = groupByCouncil(pagesToDelete, unmappedPages);
		} catch (Exception e) {
			reporter.report("Failed to group pages to delete by council");
			PageReplicationUtil.markReplicationFailed(dateRolloutNode);
		}
		if (unmappedPages.size() > 0) {
			for (String u : unmappedPages) {
				reporter.report("Page " + u + " could not be mapped to an external url");
			}
		}
		if (toActivate.size() > 0 || toDelete.size() > 0) {
			if (crawl) {
				replicatedPages = replicateAndCrawl(toActivate, toDelete, dateRolloutNode, reporter, rr);
			} else {
				replicatedPages = replicatePages(toActivate, toDelete, dateRolloutNode, reporter, rr);
			}
		}
		long end = System.currentTimeMillis();
		reporter.report("Sending report email");
		try {
			sendReportEmail(begin, end, delay, crawl, replicatedPages, dateRolloutNode, reporter, rr);
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
			PageReplicationUtil.archive(dateRolloutNode);
			reporter.report("Process completed");
			return;
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
			PageActivationReporter reporter, ResourceResolver rr) {
		reporter.report("Replicating all pages immediately");
		Map<String, Set<String>> replicatedPages = new HashMap<String, Set<String>>();
		Set<String> activatedPages = new TreeSet<String>();
		Set<String> deletedPages = new TreeSet<String>();
		Set<String> councilDomainsSet = new TreeSet<String>();
		councilDomainsSet.addAll(toActivate.keySet());
		councilDomainsSet.addAll(toDelete.keySet());
		for (String domain : councilDomainsSet) {
			Map<String, Set<String>> replicatedPagesForDomain = replicate(dateNode, reporter,
					toDelete.get(domain), toActivate.get(domain), rr);
			activatedPages.addAll(replicatedPagesForDomain.get(PARAM_ACTIVATED_PAGES));
			deletedPages.addAll(replicatedPagesForDomain.get(PARAM_DELETED_PAGES));
		}
		replicatedPages.put(PARAM_ACTIVATED_PAGES, activatedPages);
		replicatedPages.put(PARAM_DELETED_PAGES, deletedPages);
		return replicatedPages;
	}

	protected Map<String, Set<String>> replicateAndCrawl(HashMap<String, TreeSet<String>> toActivate,
			HashMap<String, TreeSet<String>> toDelete, Node dateNode, PageActivationReporter reporter,
			ResourceResolver rr) {
		Map<String, Set<String>> replicatedPages = new HashMap<String, Set<String>>();
		Set<String> activatedPages = new TreeSet<String>();
		Set<String> deletedPages = new TreeSet<String>();
		Set<String> councils = new TreeSet<String>();
		councils.addAll(toActivate.keySet());
		councils.addAll(toDelete.keySet());
		int batchSize = PageReplicationUtil.getGroupSize(rr);
		int sleepTime = PageReplicationUtil.getMinutes(rr) * 60 * 1000;
		int depth = PageReplicationUtil.getCrawlDepth(rr);
		int counter = 0;
		String[] ipsGroupOne = null;
		String[] ipsGroupTwo = null;
		reporter.report("Obtaining IP addresses for crawling");
		try {
			ipsGroupOne = PageReplicationUtil.getIps(rr, 1);
		} catch (Exception e) {
			reporter.report("Failed to retrieve any dispatcher 1 ips");
		}
		try {
			ipsGroupTwo = PageReplicationUtil.getIps(rr, 2);
		} catch (Exception e) {
			reporter.report("Failed to retrieve any dispatcher 2 ips");
		}
		for (String council : councils) {
			counter++;
			reporter.report(
					"Processing Council " + council + " [" + counter + " out of " + councils.size() + "]");
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
				Map<String, Set<String>> replicatedPagesForDomain = replicate(dateNode, reporter, toDelete.get(council),
						toActivate.get(council), rr);
				if (replicatedPagesForDomain.containsKey(PARAM_ACTIVATED_PAGES)) {
					activatedPages.addAll(replicatedPagesForDomain.get(PARAM_ACTIVATED_PAGES));
				}
				if (replicatedPagesForDomain.containsKey(PARAM_DELETED_PAGES)) {
					deletedPages.addAll(replicatedPagesForDomain.get(PARAM_DELETED_PAGES));
				}
				reporter.report("Waiting 5 sec for stat file to update before cache build");
				try {
					// Wait 5 seconds for stat file to update
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					reporter.report("5 second break failed - process concluded prematurely");
					log.error("Girlscouts Page Replicator encountered error: ", e);
					break;
				}
				String domain = PageReplicationUtil.getCouncilLiveDomain(rr, settingsService, council);
				if (!StringUtils.isEmpty(domain)) {
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
				} else {
					reporter.report("Did not find live url mapping for " + council);
					reporter.report("Will not crawl " + council);
				}
				toActivate.remove(council);
			} catch (Exception e) {
				log.error("Girlscouts Page Replicator An error occurred while processing: " + council);
				log.error("Girlscouts Page Replicator encountered error: ", e);
				try {
					reporter.report("Cache may not have built correctly for " + council);
					Node detailedReportNode = dateNode.addNode(council, "nt:unstructured");
					detailedReportNode.setProperty("message", String.valueOf(ExceptionUtils.getStackTrace(e)));
					detailedReportNode.getSession().save();
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
				String domain = PageReplicationUtil.getCouncilName(page);
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
	
	protected void sendReportEmail(long startTime, long endTime, Boolean delay, Boolean crawl,
			Map<String, Set<String>> replicatedPages,
			Node dateNode, PageActivationReporter reporter, ResourceResolver rr) throws RepositoryException {
		if (replicatedPages.size() > 0) {
			Set<String> activatedPages = replicatedPages.get(PARAM_ACTIVATED_PAGES);
			Set<String> deletedPages = replicatedPages.get(PARAM_DELETED_PAGES);
			reporter.report("Retrieving email addresses for report");
			List<String> emails = PageReplicationUtil.getReportEmails(rr);
			if (emails != null && emails.size() > 0) {
				StringBuffer html = new StringBuffer();
				html.append(DEFAULT_COMPLETION_REPORT_HEAD);
				html.append("<body><p>The Girl Scouts Replication Process has just finished running.</p>");
				if (delay) {
					html.append("<p>It was of type - Scheduled Replication</p>");
				} else {
					if (crawl) {
						html.append("<p>It was of type - Immediate Replication with Crawl</p>");
					} else {
						html.append("<p>It was of type - Immediate Replication without Crawl</p>");
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
					String reportSubject = "(" + PageReplicationUtil.getEnvironment(rr) + ") "
							+ DEFAULT_COMPLETION_REPORT_SUBJECT;
					gsEmailService.sendEmail(reportSubject, emails, html.toString(), attachments);
				} catch (EmailException | MessagingException | IOException e) {
					e.printStackTrace();
				}
			} else {
				reporter.report("No email addresses found in email address property. Can't send any emails.");
			}
		}
	}

	private Map<String, Set<String>> replicate(Node dateNode, PageActivationReporter reporter,
			TreeSet<String> pagesToDelete, TreeSet<String> pagesToActivate, ResourceResolver rr) {
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
					Node nodeDelete = rr.resolve(pageToDelete).adaptTo(Node.class);
					Node parentOfDeletedNode = nodeDelete.getParent();
					nodeDelete.remove();
					parentOfDeletedNode.getSession().save();
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

	private List<Node> queueDelayedReplications(ResourceResolver rr) {
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
				activations.add(getEventsReplicationNode(gsDelayedRes, rr));
			} catch (Exception e) {
				log.error("Girlscouts Page Replicator encountered error: ", e);
			}
		}
		return activations;
	}

	private Node getEventsReplicationNode(Resource gsDelayedRes, ResourceResolver rr) throws RepositoryException {
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
				Set<String> pages = PageReplicationUtil.getPagesToActivate(replicationsNode);
				if (pages != null && !pages.isEmpty()) {
					String eventsNodeName = "E-" + PageReplicationUtil.getDateRes();
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

	private Node getAggregateDateRolloutNode(ResourceResolver rr) throws RepositoryException {
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
			String aggregateNodeName = PageReplicationUtil.getDateRes();
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
			aggregatedPagesToActivate.addAll(PageReplicationUtil.getPagesToActivate(aggregatedRolloutNode));
			aggregatedPagesToDelete.addAll(PageReplicationUtil.getPagesToDelete(aggregatedRolloutNode));
			aggregatedPagesToActivate.addAll(PageReplicationUtil.getPagesToActivate(replicationNode));
			aggregatedPagesToDelete.addAll(PageReplicationUtil.getPagesToDelete(replicationNode));
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

	private void aggregateReplicateCrawl(List<Node> replicationsToCrawl, ResourceResolver rr)
			throws RepositoryException {
		if (replicationsToCrawl != null && !replicationsToCrawl.isEmpty()) {
			log.error("Girlscouts Page Replicator: Aggregating " + replicationsToCrawl.size()
					+ " nodes to activate and crawl.");
			Node aggregatedRolloutNode = getAggregateDateRolloutNode(rr);
			aggregatedRolloutNode.setProperty(PARAM_CRAWL, Boolean.TRUE);
			aggregatedRolloutNode.setProperty(PARAM_DELAY, Boolean.TRUE);
			aggregatedRolloutNode.setProperty(PARAM_STATUS, STATUS_CREATED);
			aggregatedRolloutNode.getSession().save();
			for (Node activationNode : replicationsToCrawl) {
				aggregate(activationNode, aggregatedRolloutNode);
			}
			aggregatedRolloutNode.getSession().save();
			processReplicationNode(aggregatedRolloutNode, rr);
		}
	}

	@Deactivate
	private void deactivate(ComponentContext componentContext) {
		log.info("Girlscouts Page Replication Service Deactivated.");
	}
}