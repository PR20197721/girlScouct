package org.girlscouts.cq.workflow;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
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

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.cq.wcm.msm.api.RolloutManager;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import javax.jcr.Value;

@Component
@Service
public class RolloutProcess implements WorkflowProcess {
	@Property(value = "Roll out a page if it is the source page of a live copy, and then activate target pages.")
	static final String DESCRIPTION = Constants.SERVICE_DESCRIPTION;
	@Property(value = "Girl Scouts")
	static final String VENDOR = Constants.SERVICE_VENDOR;
	@Property(value = "Girl Scouts Roll out Process")
	static final String LABEL = "process.label";
    private static Logger log = LoggerFactory.getLogger(RolloutProcess.class);
    
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
        
        Boolean dontSend = false, useTemplate = false;
        String templatePath = "";
        
        try{
        	dontSend = ((Value)mdm.get("dontsend")).getBoolean();
        }catch(Exception e){}
        
        
        String message = "<p>Dear Council, </p>" +
        		"<p>It has been detected that one or more component(s) on the following page(s) has been modified by GSUSA. Please review and make any updates to content or simply reinstate the inheritance(s). If you choose to reinstate the inheritance(s) please be aware that you will be <b>discarding</b> your own changes (custom content) that have been made to this page and will <b>immediately</b> receive the new national content.</p>" +
        		"<p><b>National page URL:</b> <%template-page%></p>" +
        		"<p><b>Your page URL:</b> <%council-page%></p>" +
        		"<p>Click <a href='<%council-author-page%>'>here</a> to edit your page.</p>";
        
        String subject = "GSUSA Rollout Notification";
        
        try {
        	useTemplate = ((Value)mdm.get("useTemplate")).getBoolean();
        	templatePath = ((Value)mdm.get("template")).getString();
        	if("".equals(templatePath)){
        		System.err.println("Rollout Error - Use Template checked but no template provided. Cancelling.");
        		return;
        	}
        }catch(Exception e){}
        
        try{
        	subject = ((Value)mdm.get("subject")).getString();
        }catch(Exception e){}
        
        try {
        	if(useTemplate){
        		message = getTemplate(templatePath, resourceResolver);
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
        	Collection<LiveRelationship> relations = relationManager.getLiveRelationships(srcPage, null, null, true);
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
            	}
            	if(proceed == true){
            		rolloutManager.rollout(resourceResolver, relation, false);
            		if(!dontSend){
            			girlscoutsNotificationAction.execute(srcPage.adaptTo(Resource.class), targetResource, subject, message, relation, resourceResolver);
            		}
            		session.save();
	                String targetPath = relation.getTargetPath();
	                // Remove jcr:content
	                if (targetPath.endsWith("/jcr:content")) {
	                    targetPath = targetPath.substring(0, targetPath.lastIndexOf('/'));
	                }
	                replicator.replicate(session, ReplicationActionType.ACTIVATE, targetPath);
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
    
    public String getTemplate(String templatePath, ResourceResolver resourceResolver){
    	try{
    		Resource templateResource = resourceResolver.resolve(templatePath);
    		Resource dataResource = templateResource.getChild("jcr:content/data");
    		ValueMap templateProps = ResourceUtil.getValueMap(dataResource);
    		String message = templateProps.get("content","");
    		String head = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" + 
    				"<html xmlns=\"http://www.w3.org/1999/xhtml\">" + 
    				"<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
    				"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
    				"<title>Girl Scouts</title></head>";
    		String html = head + "<body>" + message + "</body></html>";
    		return html;
    	}catch(Exception e){
    		log.error("No valid template found");
    		e.printStackTrace();
    		return "";
    	}
    }
}
