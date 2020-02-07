package org.girlscouts.common.osgi.component;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
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
import java.util.GregorianCalendar;

@Component(property = {Constants.SERVICE_DESCRIPTION + "=Girl Scouts Trashcan Restore Workflow Process Step", Constants.SERVICE_VENDOR + "=Girl Scouts USA", "process.label=Girl Scouts Trashcan Restore Workflow Process Step"})
public class TrashcanRestoreWorkflowProcessStep implements WorkflowProcess, TrashcanConstants {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        try {
            String payloadPath = workItem.getWorkflowData().getPayload().toString();
            log.debug("Executing Restore Trashcan Step for " + payloadPath);
            ResourceResolver rr = workflowSession.adaptTo(ResourceResolver.class);
            try {
                Resource payloadResource = rr.resolve(payloadPath);
                if (payloadResource != null && !payloadResource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                    Resource payloadResourceContent = rr.resolve(payloadPath + "/jcr:content");
                    ValueMap props = payloadResourceContent.getValueMap();
                    if (props.get(RESTORE_PATH_PROP_NAME) != null) {
                        restoreFromTrashcan(payloadResource);
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

    private void restoreFromTrashcan(Resource payloadResource) throws RepositoryException {
        log.debug("Restoring "+payloadResource.getPath());
        Resource trashedContent = payloadResource.getChild("jcr:content");
        ValueMap props = trashedContent.getValueMap();
        String destinationPath = props.get(RESTORE_PATH_PROP_NAME).toString();
        ResourceResolver rr = payloadResource.getResourceResolver();
        Resource destinationResource = rr.resolve(destinationPath);
        boolean isAsset = payloadResource.isResourceType(com.day.cq.dam.api.DamConstants.NT_DAM_ASSET);
        if (!destinationResource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
            log.debug("Destination already exist "+destinationPath);
            int count = 1;
            while (!destinationResource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                if (isAsset) {
                    destinationPath = destinationPath.substring(0, destinationPath.lastIndexOf(".")) + "-" + count + destinationPath.substring(destinationPath.lastIndexOf("."));
                    destinationResource = rr.resolve(destinationPath);
                } else {
                    destinationResource = rr.resolve(destinationPath+ "-" + count);
                }
                count++;
            }
            log.debug("Renamed destination to "+destinationPath);
        }
        Session session = rr.adaptTo(Session.class);
        session.move(payloadResource.getPath(), destinationResource.getPath());
        if (trashedContent != null || !trashedContent.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
            Node movedNodeContent = trashedContent.adaptTo(Node.class);
            movedNodeContent.setProperty(RESTORE_PATH_PROP_NAME, (Value) null);
            movedNodeContent.setProperty(MOVE_DATE_PROP_NAME, (Value) null);
            session.save();
            log.debug("Successfully restored "+payloadResource.getPath()+" to "+destinationPath);
        }
    }
}
