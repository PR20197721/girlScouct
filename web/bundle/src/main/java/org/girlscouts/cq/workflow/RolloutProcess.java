package org.girlscouts.cq.workflow;

import java.util.Collection;
import java.util.Collections;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.api.msm.LiveRelationship;
import com.day.cq.wcm.api.msm.LiveRelationshipManager;
import com.day.cq.wcm.api.msm.RolloutManager;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;

@Component
@Service
public class RolloutProcess implements WorkflowProcess {
    private static Logger log = LoggerFactory.getLogger(RolloutProcess.class);
    
    @Reference
    RolloutManager rolloutManager;

    @Reference
    private Replicator replicator;
    
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
        LiveRelationshipManager relationManager = (LiveRelationshipManager)resourceResolver.adaptTo(LiveRelationshipManager.class);
        String path = item.getWorkflowData().getPayloadType();
        
        Resource srcRes = resourceResolver.resolve(path);
        Page srcPage = (Page)srcRes.adaptTo(Page.class);
        
        if (srcPage == null) {
            log.error("Resource is not a page. Quit. " + path);
        }
        
        if (!relationManager.isSource(srcRes)) {
            log.debug("Not a live copy source page. Quit. " + path);
        }

        try {
            Collection<LiveRelationship> relations = relationManager.getLiveRelationships(srcPage, null, null, false);
            for (LiveRelationship relation : relations) {
                rolloutManager.rollout(resourceResolver, relation);
                session.save();
                String targetPath = relation.getTargetPath();
                // Remove jcr:content
                if (targetPath.endsWith("/jcr:content")) {
                    targetPath = targetPath.substring(0, targetPath.lastIndexOf('/'));
                }
                replicator.replicate(session, ReplicationActionType.ACTIVATE, targetPath);
            }
        } catch (WCMException e) {
            log.error("WCMException for LiveRelationshipManager");
        } catch (RepositoryException e) {
            log.error("RepositoryException for LiveRelationshipManager");
        } catch (ReplicationException e) {
            log.error("ReplicationException for LiveRelationshipManager");
        }
    }
}
