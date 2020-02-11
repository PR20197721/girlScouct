package org.girlscouts.common.osgi.component;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.girlscouts.common.constants.TrashcanConstants;
import org.girlscouts.common.exception.GirlScoutsException;
import org.girlscouts.common.util.TrashcanUtil;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;

@Component(property = {Constants.SERVICE_DESCRIPTION + "=Girl Scouts Trashcan Restore Workflow Process Step", Constants.SERVICE_VENDOR + "=Girl Scouts USA", "process.label=Girl Scouts Trashcan Restore Workflow Process Step"})
public class TrashcanRestoreWorkflowProcessStep implements WorkflowProcess, TrashcanConstants {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        try {
            String payloadPath = workItem.getWorkflowData().getPayload().toString();
            log.debug("Executing Restore Trashcan Step for " + payloadPath);
            String restorePath = workItem.getWorkflowData().getMetaDataMap().get("restorePath",String.class);
            ResourceResolver rr = workflowSession.adaptTo(ResourceResolver.class);
            try {
                Resource payloadResource = rr.resolve(payloadPath);
                if (payloadResource != null && !payloadResource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                    Resource payloadResourceContent = rr.resolve(payloadPath + "/jcr:content");
                    ValueMap props = payloadResourceContent.getValueMap();
                    if (props.get(RESTORE_PATH_PROP_NAME) != null) {
                        restoreFromTrashcan(rr, payloadPath, restorePath);
                        workflowSession.complete(workItem, workflowSession.getRoutes(workItem,false).get(0));
                    } else {
                        throw new GirlScoutsException(new Exception(), "Item at path " + payloadPath + " is missing " + RESTORE_PATH_PROP_NAME + " property.");
                    }
                } else {
                    throw new GirlScoutsException(new Exception(), "Item at path " + payloadPath + " doesn't exist");
                }
            }catch(Exception e){
                log.error("Error occurred: ", e);
                workflowSession.terminateWorkflow(workItem.getWorkflow());
            }finally {
                if(rr != null){
                    try{
                        rr.close();
                    }catch(Exception e2){
                        log.error("Error closing resource resolver", e2);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error occurred: ", e);
            workflowSession.terminateWorkflow(workItem.getWorkflow());
        }
    }

    private void restoreFromTrashcan(ResourceResolver rr, String payloadPath, String restorePath) throws RepositoryException {
        log.debug("Restoring "+payloadPath);
        Session session = rr.adaptTo(Session.class);
        session.move(payloadPath, restorePath);
        Resource restoredResource = rr.resolve(restorePath);
        if (restoredResource != null || !restoredResource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
            Node movedNodeContent = restoredResource.adaptTo(Node.class);
            movedNodeContent.setProperty(RESTORE_PATH_PROP_NAME, (Value) null);
            movedNodeContent.setProperty(MOVE_DATE_PROP_NAME, (Value) null);
            session.save();
            log.debug("Successfully restored "+payloadPath+" to "+restorePath);
        }
    }
}
