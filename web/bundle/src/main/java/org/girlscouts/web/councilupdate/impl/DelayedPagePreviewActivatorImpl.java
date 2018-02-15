package org.girlscouts.web.councilupdate.impl;

import java.text.SimpleDateFormat;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Date;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.Value;

import org.girlscouts.web.councilupdate.DelayedPagePreviewActivator;
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

import com.day.cq.replication.AgentIdFilter;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationOptions;
import com.day.cq.replication.Replicator;
import org.apache.sling.settings.SlingSettingsService;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.osgi.OsgiUtil;

@Component(
		metatype = true, 
		immediate = true,
		label = "Girl Scouts Delayed Page Preview Activation Service", 
		description = "Activates pages at night to make cache-clearing interfere less with production sites" 
		)
@Service(value = {Runnable.class, DelayedPagePreviewActivator.class})
@Properties({
	@Property(name = "service.description", value = "Girl Scouts Delayed Preview Activation Service",propertyPrivate=true),
	@Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate=true), 
	@Property(name = "scheduler.expression", label="scheduler.expression", description="cron expression"),
	@Property(name = "scheduler.concurrent", boolValue=false, propertyPrivate=true),
	@Property(name = "scheduler.runOn", value="SINGLE",propertyPrivate=true),
	@Property(name = "pagespath", label="Path to queued pages")
})

public class DelayedPagePreviewActivatorImpl implements Runnable, DelayedPagePreviewActivator{
	
	private static Logger log = LoggerFactory.getLogger(DelayedPagePreviewActivatorImpl.class);
	@Reference
	private ResourceResolverFactory resolverFactory;
	@Reference 
	private Replicator replicator;
	@Reference
    private SlingSettingsService settingsService;
	
	private ResourceResolver rr;
	//configuration fields
	public static final String PAGEPATH = "pagespath";
	
	private String pagesPath;
	private Map<String, Map<String, Exception>> errors;
	
	@Activate
	private void activate(ComponentContext context) {
		@SuppressWarnings("rawtypes")
		Dictionary configs = context.getProperties();
		this.pagesPath=OsgiUtil.toString(configs.get(PAGEPATH), null);
		try {
			Map<String, Object> serviceParams = new HashMap<String, Object>();
			serviceParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-process-service");
			rr = resolverFactory.getServiceResourceResolver(serviceParams);
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
			log.error("Delayed pages node not found");
			return;
		}
		
		Resource dateRes = getDateRes(pagesRes);
		if(dateRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)){
			log.info("No pages today for activation");
			return;
		}
		
		Node pageNode = dateRes.adaptTo(Node.class);
		String [] pages = getPages(pageNode);
		if(pages.length < 1){
			log.info("No expired users found for deletion today");
			return;
		}
        Session session = rr.adaptTo(Session.class);
		String pageString = "";
		String status = "Success";
		Node reportNode = null;
		
		try{
			if(pageNode.hasNode("report")){
				reportNode = pageNode.getNode("report");
			}else{
				reportNode = pageNode.addNode("report","nt:unstructured");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		for(String s : pages){
			try{
        	    String agentId = "publishpreview";
        	    
        	    AgentIdFilter filter = new AgentIdFilter(agentId);

        	    ReplicationOptions opts = new ReplicationOptions();
        	    opts.setFilter(filter);
				pageString = s;        
				replicator.replicate(session, ReplicationActionType.ACTIVATE, pageString, opts);
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
	
	private Resource getDateRes(Resource r){
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = sdf.format(today);
		Resource dateRes = rr.resolve(r.getPath() + "/" + dateString);
		return dateRes;
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
		log.info("Delayed Page Preview Activation Service Deactivated.");
	}
	
}