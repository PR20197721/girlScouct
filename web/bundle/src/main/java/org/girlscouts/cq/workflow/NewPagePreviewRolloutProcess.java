package org.girlscouts.cq.workflow;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.mail.HtmlEmail;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.girlscouts.web.councilrollout.GirlScoutsNotificationAction;
import org.girlscouts.web.councilrollout.impl.GirlScoutsNotificationActionImpl;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.AgentIdFilter;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationOptions;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.cq.wcm.msm.api.RolloutConfig;
import com.day.cq.wcm.msm.api.RolloutConfigManager;
import com.day.cq.wcm.msm.api.RolloutManager;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import javax.jcr.Value;
import javax.jcr.query.Query;

@Component
@Service
public class NewPagePreviewRolloutProcess implements WorkflowProcess {
	@Property(value = "Attempt to locate a page within the council that is a live copy of the parent of the current source page, and then create a live copy there and activate to preview.")
	static final String DESCRIPTION = Constants.SERVICE_DESCRIPTION;
	@Property(value = "Girl Scouts")
	static final String VENDOR = Constants.SERVICE_VENDOR;
	@Property(value = "Girl Scouts New Page Preview Roll out Process")
	static final String LABEL = "process.label";
    private static Logger log = LoggerFactory.getLogger(NewPagePreviewRolloutProcess.class);
    
    @Reference
    RolloutManager rolloutManager;

    @Reference
    private Replicator replicator;
    
    @Reference
    private LiveRelationshipManager relationManager;
    
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	
	@Reference
	private GirlScoutsNotificationAction girlscoutsNotificationAction;

    public void execute(WorkItem item, WorkflowSession workflowSession, MetaDataMap metadata)
            throws WorkflowException {
        Session session = workflowSession.getSession();
        ResourceResolver resourceResolver = null;

        try {
            resourceResolver = resourceResolverFactory.getResourceResolver(Collections.singletonMap(
                    "user.jcr.session",
                    (Object)session
            ));
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        String srcPath = item.getWorkflowData().getPayload().toString();
        MetaDataMap mdm = item.getWorkflowData().getMetaDataMap();
        Value singleValue = null;
        Value[] values = null;
        try {
            values = (Value[]) mdm.get("councils");
		} catch (Exception e) {
			try{
				singleValue = (Value) mdm.get("councils");
			}catch(Exception e1){
				System.err.println("Rollout Could Not Run - No Councils Selected");
				e1.printStackTrace();
			}
		} 
        
        Boolean dontSend = false, useTemplate = false, activate = true;
        String templatePath = "";
        
        try{
        	dontSend = ((Value)mdm.get("dontsend")).getBoolean();
        }catch(Exception e){}
        
        try{
        	activate = ((Value)mdm.get("activate")).getBoolean();
        }catch(Exception e){}
        
        String message = "<p>Dear Council, </p>" +
        		"<p>It has been detected that a new national content page has been created by GSUSA. Please review and make any updates to content.</p>" +
        		"<p><b>National page URL:</b> <%template-page%></p>" +
        		"<p><b>Your page URL:</b> <%council-page%></p>" +
        		"<p>Click <a href='<%council-author-page%>'>here</a> to edit your page.</p>";
        
        String subject = "GSUSA Rollout Notification";
        
        try {
        	useTemplate = ((Value)mdm.get("useTemplate")).getBoolean();
        	templatePath = ((Value)mdm.get("template")).getString();
        	if(useTemplate && "".equals(templatePath)){
        		System.err.println("Rollout Error - Use Template checked but no template provided. Cancelling.");
        		return;
        	}
        }catch(Exception e){}
        
        try{
        	if(useTemplate){
        		subject = getTemplate(templatePath, resourceResolver, true);
        	}else{
        		subject = ((Value)mdm.get("subject")).getString();
        	}
        }catch(Exception e){}
        
        try {
        	if(useTemplate){
        		message = getTemplate(templatePath, resourceResolver, false);
        	}else{
        		message = ((Value)mdm.get("message")).getString();
        	}	
		} catch (Exception e) {
			System.err.println("Rollout Error - Unable to Parse Message");
			e.printStackTrace();
		}

        Resource srcRes = resourceResolver.resolve(srcPath);
        Page srcPage = (Page)srcRes.adaptTo(Page.class);
        
        if (srcPage == null) {
            log.error("Resource is not a page. Quit. " + srcPath);
            return;
        }
        
        if (!relationManager.isSource(srcRes)) {
            log.error("Not a live copy source page. Quit. " + srcPath);
            return;
        }

        try {
        	List<String> existingCopies = new ArrayList<String>();
        	Collection<LiveRelationship> existingRelations = relationManager.getLiveRelationships(srcPage, null, null, true);
        	for(LiveRelationship relation : existingRelations){
        		Resource tres = resourceResolver.resolve(relation.getTargetPath());
        		if(!tres.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)){
        			Page tpage = tres.adaptTo(Page.class);
        			existingCopies.add(tpage.getAbsoluteParent(2).getPath());
        		}
        	}
        	
        	Collection<LiveRelationship> relations = relationManager.getLiveRelationships(srcPage.getParent(), null, null, true);
            for (LiveRelationship relation : relations) {
            	Resource targetResource = resourceResolver.resolve(relation.getTargetPath());
            	Boolean proceed = false;
            	if(!targetResource.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)){
            		if(values == null){
            			if(singleValue != null){
            				if(targetResource.getPath().startsWith(singleValue.getString())){
            					proceed = true;
            				}
            			}
            		}else{
	            		for(Value v : values){
	            			if(targetResource.getPath().startsWith(v.getString())){
	            				proceed = true;
	            			}
	            		}
            		}
            		
            		//Prevent creating a new live copy multiple times for one council
                	if(proceed == true){
                		for(String existingString : existingCopies){
                			if(targetResource.getPath().startsWith(existingString)){
                				proceed = false;
                			}
                		}
                	}
            	}
            	if(proceed == true){
            	    String agentId = "publishpreview";
            	    
            	    AgentIdFilter filter = new AgentIdFilter(agentId);

            	    ReplicationOptions opts = new ReplicationOptions();
            	    opts.setFilter(filter);
            		
            		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
					Page copyPage = pageManager.copy(srcPage, targetResource.getParent().getPath() + "/" + srcPage.getTitle(), srcPage.getTitle(), false, true);
					RolloutConfigManager configMgr = (RolloutConfigManager) resourceResolver.adaptTo(RolloutConfigManager.class);
					RolloutConfig gsConfig = configMgr.getRolloutConfig("/etc/msm/rolloutconfigs/gsdefault");
					LiveRelationship relationship= relationManager.establishRelationship(srcPage, copyPage, true, false, gsConfig);
					cancelInheritance(resourceResolver, copyPage.getPath());
        			
					rolloutManager.rollout(resourceResolver, relationship, false);
        			session.save();
	                String targetPath = relation.getTargetPath();
	                // Remove jcr:content
	                if (targetPath.endsWith("/jcr:content")) {
	                    targetPath = targetPath.substring(0, targetPath.lastIndexOf('/'));
	                }
	                if(activate){
	                	replicator.replicate(session, ReplicationActionType.ACTIVATE, targetPath, opts);
	                }
            		if(!dontSend){
            			girlscoutsNotificationAction.execute(srcPage.adaptTo(Resource.class), targetResource, subject, message, relation, resourceResolver);
            		}
            	}
            }
        } catch (WCMException e) {
            log.error("WCMException for LiveRelationshipManager");
        } catch (RepositoryException e) {
            log.error("RepositoryException for LiveRelationshipManager");
        } catch (ReplicationException e) {
            log.error("ReplicationException for LiveRelationshipManager");
        }
    }
    
    public String getTemplate(String templatePath, ResourceResolver resourceResolver, Boolean subject){
    	try{
    		Resource templateResource = resourceResolver.resolve(templatePath);
    		Resource dataResource = templateResource.getChild("jcr:content/data");
    		ValueMap templateProps = ResourceUtil.getValueMap(dataResource);
    		String ret = "";
    		if(subject){
    			Resource contentResource = templateResource.getChild("jcr:content");
    			ValueMap contentProps = ResourceUtil.getValueMap(contentResource);
    			ret = contentProps.get("jcr:title","GSUSA Rollout Notification");
    		}else{
	    		String message = templateProps.get("content","");
	    		String head = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" + 
	    				"<html xmlns=\"http://www.w3.org/1999/xhtml\">" + 
	    				"<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
	    				"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
	    				"<title>Girl Scouts</title></head>";
	    		ret = head + "<body>" + message + "</body></html>";
    		}
    		return ret;
    	}catch(Exception e){
    		log.error("No valid template found");
    		e.printStackTrace();
    		return "";
    	}
    }
    
	/**
	 * cancel the Inheritance for certain components
	 *  (nodes with mixin type "cq:LiveSyncCancelled" under national template page)
	 */
	private void cancelInheritance(ResourceResolver rr, String councilPath){
		try {
			String sql = "SELECT * FROM [cq:LiveSyncCancelled] AS s WHERE ISDESCENDANTNODE(s, '"
					+ councilPath + "')";
			log.debug("SQL " + sql);
			for (Iterator<Resource> it = rr.findResources(sql, Query.JCR_SQL2);it.hasNext();){
				Resource target = it.next();
                relationManager.endRelationship(target,true);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
    
}
