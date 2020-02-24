package org.girlscouts.common.osgi.component;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.common.constants.TrashcanConstants;
import org.girlscouts.common.exception.GirlScoutsException;
import org.girlscouts.common.util.TrashcanUtil;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.util.GregorianCalendar;

@Component(property = {Constants.SERVICE_DESCRIPTION + "=Girl Scouts Trashcan Workflow Process Step", Constants.SERVICE_VENDOR + "=Girl Scouts USA", "process.label=Girl Scouts Trashcan Workflow Process Step"})
public class TrashcanWorkflowProcessStep implements WorkflowProcess, TrashcanConstants {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        try {
            String payloadPath = workItem.getWorkflowData().getPayload().toString();
            log.debug("Executing Trashcan Step for " + payloadPath);
            String trashPath = workItem.getWorkflowData().getMetaDataMap().get(TRASH_PATH_PROP_NAME,String.class);
            ResourceResolver rr = workflowSession.adaptTo(ResourceResolver.class);
            try {
                Resource payloadResource = rr.resolve(payloadPath);
                if (payloadResource != null && !payloadResource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                    if (!TrashcanUtil.isPublished(payloadResource)) {
                        if (!TrashcanUtil.hasReferences(payloadResource)) {
                            if (!TrashcanUtil.isLiveCopy(payloadResource)) {
                                boolean isAsset = payloadResource.isResourceType(com.day.cq.dam.api.DamConstants.NT_DAM_ASSET);
                                if (isAsset || !TrashcanUtil.hasChildren(payloadResource)) {
                                    moveToTrashcan(rr, payloadPath, trashPath);
                                    workflowSession.complete(workItem, workflowSession.getRoutes(workItem,false).get(0));
                                }
                            }
                        }
                    }
                } else {
                    throw new GirlScoutsException(new Exception(), "Item at path " + payloadPath + " doesn't exist");
                }
            } catch (Exception e) {
                log.error("Error occurred: ", e);
                workflowSession.terminateWorkflow(workItem.getWorkflow());
            } finally {
                if (rr != null) {
                    try {
                        rr.close();
                    } catch (Exception e2) {
                        log.error("Error closing resource resolver", e2);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error occurred: ", e);
            workflowSession.terminateWorkflow(workItem.getWorkflow());
        }
    }

    private void moveToTrashcan(ResourceResolver rr, String payloadPath, String trashPath) throws RepositoryException{
        log.debug("Moving "+payloadPath+" to "+trashPath);
        Session session = rr.adaptTo(Session.class);
        session.move(payloadPath, trashPath);
        Resource movedItemContent = rr.resolve(trashPath + "/jcr:content");
        if (movedItemContent != null || !movedItemContent.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
            Node movedNodeContent = movedItemContent.adaptTo(Node.class);
            String restorePath = payloadPath.substring(0, payloadPath.lastIndexOf("/"));
            Value value = session.getValueFactory().createValue(restorePath, PropertyType.PATH);
            movedNodeContent.setProperty(RESTORE_PATH_PROP_NAME, value);
            movedNodeContent.setProperty(RESTORE_NAME_PROP_NAME, payloadPath.substring(payloadPath.lastIndexOf("/")+1));
            movedNodeContent.setProperty(MOVE_DATE_PROP_NAME, new GregorianCalendar());
            session.save();
            log.debug("Successfully moved "+payloadPath+" to "+movedNodeContent.getPath());
        }
    }
}
