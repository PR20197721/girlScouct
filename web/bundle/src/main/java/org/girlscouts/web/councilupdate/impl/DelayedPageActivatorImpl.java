package org.girlscouts.web.councilupdate.impl;

import java.text.SimpleDateFormat;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.Map;
import java.util.Date;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.girlscouts.web.councilupdate.CacheThread;
import org.girlscouts.web.councilupdate.DelayedPageActivator;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.Replicator;
import org.apache.sling.settings.SlingSettingsService;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.osgi.OsgiUtil;

@Component(
		metatype = true, 
		immediate = true,
		label = "Girl Scouts Delayed Page Activation Service", 
		description = "Activates pages at night to make cache-clearing interfere less with production sites" 
		)
@Service(value = {Runnable.class, DelayedPageActivator.class})
@Properties({
	@Property(name = "service.description", value = "Girl Scouts Delayed Activation Service",propertyPrivate=true),
	@Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate=true), 
	@Property(name = "scheduler.expression", label="scheduler.expression", description="cron expression"),
	@Property(name = "scheduler.concurrent", boolValue=false, propertyPrivate=true),
	@Property(name = "scheduler.runOn", value="SINGLE",propertyPrivate=true),
	@Property(name = "pagespath", label="Path to queued pages, dispatcher IPs"),
	@Property(name = "groupsize", label="Group size", description="Default is 1"),
	@Property(name = "minutes", label="Minutes to wait", description="Default is 30")
})

public class DelayedPageActivatorImpl implements Runnable, DelayedPageActivator{
	
	private static Logger log = LoggerFactory.getLogger(DelayedPageActivatorImpl.class);
	@Reference
	private ResourceResolverFactory resolverFactory;
	@Reference 
	private Replicator replicator;
	@Reference
    private SlingSettingsService settingsService;
	
	private ResourceResolver rr;
	//configuration fields
	public static final String PAGEPATH = "pagespath";
	public static final String GROUP_SIZE = "groupsize";
	public static final String MINUTES = "minutes";
	
	private String pagesPath;
	private int groupSize;
	private int minutes;
	private Map<String, Map<String, Exception>> errors;
	
	@Activate
	private void activate(ComponentContext context) {
		@SuppressWarnings("rawtypes")
		Dictionary configs = context.getProperties();
		this.pagesPath=OsgiUtil.toString(configs.get(PAGEPATH), null);
		this.groupSize=OsgiUtil.toInteger(configs.get(GROUP_SIZE), 1);
		this.minutes=OsgiUtil.toInteger(configs.get(MINUTES), 30);
		try {
			rr= resolverFactory.getAdministrativeResourceResolver(null);
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		//TODO: Change the following if case so that it uses policy = ConfigurationPolicy.REQUIRE to check for publishing servers 
		//More info: http://aemfaq.blogspot.com/search/label/runmode
		//http://stackoverflow.com/questions/19292933/creating-osgi-bundles-with-different-services
		if (isPublisher()) {
			return;
		}
		Resource pagesRes = rr.resolve(pagesPath);
		if(pagesRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)){
			return;
		}
		
		Resource dateRes = rr.resolve(pagesRes.getPath() + "/" + getDateRes());
		Node dateNode = null;
		Node pageNode = pagesRes.adaptTo(Node.class);
		
		if(dateRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)){
			try {
				dateNode = pageNode.addNode(getDateRes());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			dateNode = dateRes.adaptTo(Node.class);
		}
		
		String [] pages = getPages(pageNode);
		if(pages.length < 1){
			log.info("No pages for activation today");
			return;
		}
        Session session = rr.adaptTo(Session.class);
		String pageString = "";
		String status = "Success";
		Node reportNode = null;
		try{
			if(dateNode.hasNode("report")){
				reportNode = dateNode.getNode("report");
			}else{
				reportNode = dateNode.addNode("report","nt:unstructured");
			}
			reportNode.setProperty("process","Delayed Page Activation");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		for(int i = 0; i < pages.length; i+=groupSize){
			for(int k = i; k<i+groupSize; k++){
				String s;
				if(k > pages.length){
					break;
				}else{
					s = pages[k];
				}
				try{
					pageString = s;        
			        //replicator.replicate(session, ReplicationActionType.ACTIVATE, pageString);
			        Runnable testRunnable = new CacheThread("/", "http://uat.girlscouts.org", "52.72.80.194", "");
			        Thread testThread = new Thread(testRunnable, "testThread");
			        System.out.println("Starting thread");
			        testThread.start();
			        try{
			        	testThread.join();
			        }catch(Exception e){
			        	e.printStackTrace();
			        }
			        System.out.println("First thread finished");
				}catch(Exception e){
					log.error("An error occurred while deleting user: " + pageString);
					try{
						status = "Completed with errors";
						Node detailedReportNode = reportNode.addNode(s, "nt:unstructured");
						detailedReportNode.setProperty("message", e.getMessage());
					}catch(Exception e1){
						log.error("Delayed Page Activator - An exception occurred while creating error node");
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
			}
			
			
			try {
				Thread.sleep(minutes * 60 * 1000);
				System.out.println("Done Sleeping");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
		try{
			reportNode.setProperty("status", status);
			session.save();
		}catch(Exception e){
			e.printStackTrace();
		}
		
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
	
	private String[] getPages(Node n){
		try{
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
		}catch(Exception e){
			e.printStackTrace();
			return new String[0];
		}
	}
	
	@Deactivate
	private void deactivate(ComponentContext componentContext) {
		rr.close();
		log.info("Delayed Page Activation Service Deactivated.");
	}
	
}