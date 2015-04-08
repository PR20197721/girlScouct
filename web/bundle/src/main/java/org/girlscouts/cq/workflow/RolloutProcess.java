package org.girlscouts.cq.workflow;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Node;
import javax.jcr.Value;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
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
import javax.jcr.nodetype.NoSuchNodeTypeException;

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
        	Resource scr = resourceResolver.resolve(srcPath+"/jcr:content/content/middle/par/text_1");
            Page startpage = (Page)scr.adaptTo(Page.class);
            //relationManager.addSkippedPage(srcPage,{"/jcr:content/content/middle/par/text_1"},true);
        	Collection<LiveRelationship> startRelations = relationManager.getLiveRelationships(scr,null,null,true);
            Collection<LiveRelationship> relations = relationManager.getLiveRelationships(srcPage, null, null, true);
//            for (LiveRelationship relation : startRelations) {
//                String targetPath = relation.getTargetPath();
//                relationManager.cancelRelationship(resourceResolver, relation, true);  
//
//            }

            for (LiveRelationship relation : relations) {

                String targetPath = relation.getTargetPath();
//                if(targetPath.contains("join")){
//                	String targetPath2 = targetPath.substring(0, targetPath.lastIndexOf('/'));
//                	 Resource tgtRes = resourceResolver.resolve(targetPath2);
//                     Page tgtPage = (Page)tgtRes.adaptTo(Page.class);
//                     Map<String,Page> skipmap = relationManager.getSkippedSourcePages(tgtPage);
//                     System.out.println(skipmap);
//
//
//                	Node startFunToday = (Node)resourceResolver.resolve(targetPath+"/content/middle/par/text_1").adaptTo(Node.class);
//
//                	try{               
//                		startFunToday.removeMixin("cq:LiveRelationship");
//                	}catch(NoSuchNodeTypeException e){ 
//                		startFunToday.removeMixin("cq:LiveSyncCancelled");
//                	}
//                	Resource scr = resourceResolver.resolve(targetPath+"/content/middle/par/text_1");
//                	relationManager.endRelationship(scr, true); 
//
//                }
                rolloutManager.rollout(resourceResolver, relation, false, true);
                session.save();
                // Remove jcr:content
                if (targetPath.endsWith("/jcr:content")) {
                    targetPath = targetPath.substring(0, targetPath.lastIndexOf('/'));
                }
                replicator.replicate(session, ReplicationActionType.ACTIVATE, targetPath);
            }
        }catch (WCMException e) {
            log.error("WCMException for LiveRelationshipManager");
        } catch (RepositoryException e) {
            log.error("RepositoryException for LiveRelationshipManager");
        } catch (ReplicationException e) {
            log.error("ReplicationException for LiveRelationshipManager");
        }
    }
}
