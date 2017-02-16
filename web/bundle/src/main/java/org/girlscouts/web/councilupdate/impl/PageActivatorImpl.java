package org.girlscouts.web.councilupdate.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.RowIterator;
import javax.jcr.version.VersionException;
import javax.mail.internet.InternetAddress;

import org.girlscouts.web.councilupdate.CacheThread;
import org.girlscouts.web.councilupdate.PageActivator;
import org.girlscouts.web.councilupdate.PageActivator;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.mail.HtmlEmail;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;

import com.day.cq.commons.Externalizer;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;

import org.apache.sling.settings.SlingSettingsService;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.osgi.OsgiUtil;

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
	@Property(name = "scheduler.runOn", value="SINGLE",propertyPrivate=true),
	@Property(name = "pagespath", label="Path to queued pages, dispatcher IPs"),
	@Property(name = "groupsize", label="Group size", description="Default is 1"),
	@Property(name = "minutes", label="Minutes to wait", description="Default is 30")
})

public class PageActivatorImpl implements Runnable, PageActivator{
	
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
	public static final String PAGEPATH = "pagespath";
	public static final String GROUP_SIZE = "groupsize";
	public static final String MINUTES = "minutes";
	private static HashMap<String, TreeSet<String>> currentBatch, builtCouncils, toBuild;
	private static long lastBatchTime;
	private static TreeSet<String> unmapped;
	
	private String pagesPath;
	private int groupSize;
	private int minutes;
	private Map<String, Map<String, Exception>> errors;
	private ComponentContext context;
	
	@Activate
	private void activate(ComponentContext context) {
		@SuppressWarnings("rawtypes")
		Dictionary configs = context.getProperties();
		this.pagesPath=OsgiUtil.toString(configs.get(PAGEPATH), null);
		this.groupSize=OsgiUtil.toInteger(configs.get(GROUP_SIZE), 1);
		this.minutes=OsgiUtil.toInteger(configs.get(MINUTES), 30);
		this.context = context;
		this.lastBatchTime = -1;
		this.unmapped = new TreeSet<String>();
		try {
			rr= resolverFactory.getAdministrativeResourceResolver(null);
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}
	
	public String getConfig(String key) {
		@SuppressWarnings("rawtypes")
		Dictionary configs = context.getProperties();
		return (String) configs.get(key);
	}
	
	public HashMap<String,TreeSet<String>> getToBuild(){
		return toBuild;
	}
	
	public HashMap<String, TreeSet<String>> getBuiltCouncils(){
		return builtCouncils;
	}
	
	public HashMap<String, TreeSet<String>> getCurrentBatch(){
		return currentBatch;
	}
	
	public long getLastBatchTime(){
		return lastBatchTime;
	}
	
	public TreeSet<String> getUnmapped(){
		return unmapped;
	}
	
	public void run() {
		if (isPublisher()) {
			return;
		}
		
		long begin = System.currentTimeMillis();
		
        Session session = rr.adaptTo(Session.class);
		Resource pagesRes = rr.resolve(pagesPath);
		if(pagesRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)){
			return;
		}
		Resource dateRes = rr.resolve(pagesRes.getPath() + "/" + getDateRes());
		Node dateNode = null;
		Node pageNode = pagesRes.adaptTo(Node.class);
		
		try{
			if(pageNode.hasProperty("inProgress") && pageNode.getProperty("inProgress").getString().equals("true")){
				log.info("GS page Activator - Process already running");
				return;
			}else{
				pageNode.setProperty("inProgress", "true");
				session.save();
			}
		}catch(Exception e){
			log.error("GS Page Activator - Failed to check if process in progress already");
			return;
		}
		
		Boolean crawl = true;
		String type = "";
		try{
			if(pageNode.hasProperty("type")){
				if(pageNode.getProperty("type").getString().equals("ipa-nc")){
					crawl = false;
				}
				type = pageNode.getProperty("type").getString();
			}
		}catch(Exception e){
			log.error("GS Page Activator - Could not determine crawl property. Defaulting to crawl");
		}
		
		if(dateRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)){
			try {
				dateNode = pageNode.addNode(getDateRes());
			} catch (Exception e) {
				log.error("GS Page Activator - Couldn't create date node");
				e.printStackTrace();
				return;
			}
		}else{
			dateNode = dateRes.adaptTo(Node.class);
		}
		
		Node reportNode = null;
		try{
			if(dateNode.hasNode("report")){
				reportNode = dateNode.getNode("report");
			}else{
				reportNode = dateNode.addNode("report","nt:unstructured");
			}
			reportNode.setProperty("process","GS Page Activator");
		}catch(Exception e){
			log.error("GS Page Activator - Failed to create or retrieve report node");
			e.printStackTrace();
			return;
		}
		String status = "";
		
		String [] pages = new String[0];
		try {
			pages = getPages(pageNode);
		} catch (Exception e) {
			status = status + "failed to get initial page count | ";
			log.error("GS Page Activator - failed to get initial page count");
			e.printStackTrace();
			return;
		}
		
		if(pages.length == 0){
			status = status + "No pages found in queue. Did not run | ";
		}
		
		TreeSet<String> builtPages = new TreeSet<String>();
		toBuild = new HashMap<String,TreeSet<String>>();
		builtCouncils = new HashMap<String, TreeSet<String>>();
		currentBatch = new HashMap<String, TreeSet<String>>();
		
		while(pages.length > 0){
			try {
				pages = getPages(pageNode);
				builtPages.addAll(Arrays.asList(pages));
				pageNode.setProperty("pages", new String[0]);
				session.save();
			} catch (Exception e) {
				status = status + "failed to retrieve or remove pages from page node | ";
				log.error("GS Page Activator - failed to save session upon removing pages from page node");
				break;
			}
			
			if(pages.length < 1){
				break;
			}
			
			HashMap <String, TreeSet<String>> councilMappings = new HashMap<String, TreeSet<String>>();
			try{
				councilMappings = arrangeCouncils(pages, rr);
				toBuild = new HashMap<String,TreeSet<String>>(councilMappings);
			}catch(Exception e){
				status = status + "failed to sort pages by council | ";
				log.error("GS Page Activator - failed to arrange councils");
			}
			
			Set<String> councilDomainsSet = councilMappings.keySet();
			String[] councilDomains = councilDomainsSet.toArray(new String[councilDomainsSet.size()]);
			String[] ipsGroupOne = null;
			String[] ipsGroupTwo = null;
			try{
				ipsGroupOne = getIps(1,pageNode);
			}catch(Exception e){
				log.error("GS Page Activator - failed to retrieve dispatcher 1 ips");
				status=status + "failed to retrieve any dispatcher 1 ips | ";
			}
			try{	
				ipsGroupTwo = getIps(2,pageNode);
			}catch(Exception e){
				log.error("GS Page Activator - failed to retrieve dispatcher 2 ips");
			}			
			status = status + buildCache(councilDomains, pages, councilMappings, session, ipsGroupOne, ipsGroupTwo, reportNode, crawl);	
		}
		
		long end = System.currentTimeMillis();
		
		if("".equals(status)){
			status = "Success";
		}else{
			status = status + "Errors have occurred during process. Please check error logs for details";
		}
		
		try{
			sendReportEmail(status, pageNode, begin, end, type, builtPages);
		}catch(Exception e){
			status = status + "Unable to send report email";
			log.error("Girl Scouts Page Activator - Unable to send report email");
			log.error(e.getMessage());
		}
		
		try{
			reportNode.setProperty("overallStatus", status);
			reportNode.setProperty("pagesProcessed", builtPages.toArray(new String[builtPages.size()]));
			pageNode.setProperty("inProgress","false");
			session.save();
			log.info("GS Page Activator - Process completed with status: " + status);
		}catch(Exception e){
			e.printStackTrace();
		}
		toBuild = new HashMap<String,TreeSet<String>>();
		builtCouncils = new HashMap<String, TreeSet<String>>();
		currentBatch = new HashMap<String, TreeSet<String>>();
		unmapped = new TreeSet<String>();
	}
	
	private void sendReportEmail(String status, Node pageNode, long startTime, long endTime, String type, TreeSet<String> builtPages) throws Exception{
		ArrayList<String> emails = new ArrayList<String>();
		if(pageNode.hasProperty("emails")){
			Value[] values = pageNode.getProperty("emails").getValues();
			for(Value v : values){
				emails.add(v.toString());
			}
		}else{
			status = status + "No email address property found | ";
			return;
		}
		if(emails.size() < 1){
			status = status + "No email addresses found | ";
			return;
		}
		HtmlEmail email = new HtmlEmail();
		ArrayList<InternetAddress> emailRecipients = new ArrayList<InternetAddress>();
		for(String s : emails){
			emailRecipients.add(new InternetAddress(s));
		}
		email.setTo(emailRecipients);
		email.setSubject("Girl Scouts Activation Process Report");
		String html = "<p>The Girl Scouts Activation Process has just finished running.</p>";
		if(type.equals("dpa")){
			html = html + "<p>It was of type - Scheduled Activation</p>";
		}else if(type.equals("ipa-c")){
			html = html + "<p>It was of type - Immediate Activation with Crawl</p>";
		}else if(type.equals("ipa-nc")){
			html = html + "<p>It was of type - Immediate Activation without Crawl</p>";
		}else{
			html = html + "<p>The type of activation process could not be determined</p>";
		}
		
		html = html + "<p>Pages Activated:</p><ul>";
		for(String page : builtPages){
			html = html + "<li>" + page + "</li>";
		}
		html = html + "</ul>";
		
		long timeDiff = (endTime - startTime)/60000;
		html = html + "<p>The entire process took approximately " + timeDiff + " minutes to complete</p>";
		
		email.setHtmlMsg(html);
		MessageGateway<HtmlEmail> messageGateway = messageGatewayService.getGateway(HtmlEmail.class);
		messageGateway.send(email);
	}
	
	private String buildCache(String[] councilDomains, String[] pages, HashMap<String, TreeSet<String>> councilMappings, Session session, String[] ipsGroupOne, String[] ipsGroupTwo, Node reportNode, Boolean crawl){
		String toReturn = "";
		Boolean firstLoop = true;
		for(int i = 0; i < councilDomains.length; i+=groupSize){
			if(!firstLoop){
				try {
					Thread.sleep(minutes * 60 * 1000);
				} catch (InterruptedException e) {
					log.error("GS Page Activator - could not sleep");
					toReturn = toReturn + "Staggering Failed - process (including activations) cancelled prematurely | ";
					break;
				}
			}else{
				firstLoop=false;
			}
			for(int k = i; k<i+groupSize; k++){
				currentBatch.put(councilDomains[k], councilMappings.get(councilDomains[k]));
				toBuild.remove(councilDomains[k]);
			}
			for(int k = i; k<i+groupSize; k++){
				long startTime = System.currentTimeMillis();
				String domain;
				if(k > pages.length){
					break;
				}else{
					domain = councilDomains[k];
				}
				try{
					TreeSet<String> pagesToActivate = councilMappings.get(domain);
					for(String pageToActivate : pagesToActivate){
						replicator.replicate(session, ReplicationActionType.ACTIVATE, pageToActivate);
					}   
			        if(crawl){
				        for(int l=0; l<ipsGroupOne.length; l++){
				        	Thread dispatcherIPOneThread = null;
				        	Thread dispatcherIPTwoThread = null;
				        	if(ipsGroupOne[l] != null){
				        		Runnable dispatcherIPOneRunnable = new CacheThread("/", domain, ipsGroupOne[l], "");
				        		dispatcherIPOneThread = new Thread(dispatcherIPOneRunnable, "dispatcherGroupOneThread" + l);
				        		dispatcherIPOneThread.start();
				        	}
				        	if(ipsGroupTwo != null && ipsGroupTwo.length >= l+1 && ipsGroupTwo[l] != null){
				        		Runnable dispatcherIPTwoRunnable = new CacheThread("/", domain, ipsGroupTwo[l], "");
				        		dispatcherIPTwoThread = new Thread(dispatcherIPTwoRunnable, "dispatcherGroupTwoThread" + l);
				        		dispatcherIPTwoThread.start();
				        	}
				        	if(dispatcherIPOneThread != null){
				        		dispatcherIPOneThread.join();
				        	}
				        	if(dispatcherIPTwoThread != null){
				        		dispatcherIPTwoThread.join();
				        	}
				        }
			        }
			        
			        builtCouncils.put(domain,councilMappings.get(domain));

				}catch(Exception e){
					log.error("An error occurred while processing: " + domain);
					try{
						toReturn = toReturn + "Cache may not have built correctly for " + domain + " | ";
						Node detailedReportNode = reportNode.addNode(domain, "nt:unstructured");
						detailedReportNode.setProperty("message", e.getMessage());
					}catch(Exception e1){
						log.error("GS Page Activator - An exception occurred while creating error node");
						log.error(e.getMessage());
						toReturn = toReturn + "Detailed cache report node could not be created | ";
						continue;
					}
				}
				currentBatch = new HashMap<String,TreeSet<String>>();
				long endTime = System.currentTimeMillis();
				lastBatchTime = ((endTime - startTime)/1000);
			}
		}
		
		return toReturn;
	}
	
	private boolean isPublisher() {
		if (settingsService.getRunModes().contains("publish")) {
			return true;
        }
		return false;
	}
	
	private String getDateRes(){
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HHmmss");
		String dateString = sdf.format(today);
		return dateString;
	}
	
	private String[] getPages(Node n) throws Exception{
		if(n.hasProperty("pages")){
			Value[] values = n.getProperty("pages").getValues();
			String[] pages = new String[values.length];
			for(int i=0; i<values.length; i++){
				pages[i] = values[i].getString();
			}
			return pages;
		}else{
			return new String[0];
		}
	}
	
	private HashMap<String, TreeSet<String>> arrangeCouncils(String[] pages, ResourceResolver rr) throws Exception{
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
			mappingPath = "/etc/map.publish.prod/httpd";
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
	
	private String[] getIps(int group, Node pageNode) throws Exception{
		if(pageNode.hasProperty("ips" + group)){
			Value[] values = pageNode.getProperty("ips" + group).getValues();
			String[] toReturn = new String[values.length];
			for(int i=0; i<values.length; i++){
				toReturn[i] = values[i].getString();
			}
			return toReturn;
		}
	return new String[0];
	}
	
	@Deactivate
	private void deactivate(ComponentContext componentContext) {
		log.info("GS Page Activation Service Deactivated.");
	}
	
}