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
            ResourceResolver rr = workflowSession.adaptTo(ResourceResolver.class);
            try {
                Resource payloadResource = rr.resolve(payloadPath);
                if (payloadResource != null && !payloadResource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                    if (!TrashcanUtil.isPublished(payloadResource)) {
                        if (!TrashcanUtil.hasReferences(payloadResource)) {
                            if (!TrashcanUtil.isLiveCopy(payloadResource)) {
                                boolean isAsset = payloadResource.isResourceType(com.day.cq.dam.api.DamConstants.NT_DAM_ASSET);
                                if (isAsset || !TrashcanUtil.hasChildren(payloadResource)) {
                                    moveToTrashcan(payloadResource, isAsset);
                                } else {
                                    throw new GirlScoutsException(new Exception(), "Item at path " + payloadPath + " has children");
                                }
                            } else {
                                throw new GirlScoutsException(new Exception(), "Item at path " + payloadPath + " has Live Relationship");
                            }
                        } else {
                            throw new GirlScoutsException(new Exception(), "Item at path " + payloadPath + " has references");
                        }
                    } else {
                        throw new GirlScoutsException(new Exception(), "Item at path " + payloadPath + " is published");
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

    private void moveToTrashcan(Resource payloadResource, boolean isAsset) throws RepositoryException, com.day.cq.workflow.WorkflowException {
        String itemPath = payloadResource.getPath();
        String trashcanSitePath = "";
        String trashcanItemPath = "";
        String siteName = "";
        if (isAsset) {
            siteName = itemPath.substring(13);
            siteName = siteName.substring(0, siteName.indexOf("/"));
            trashcanSitePath = ASSET_TRASHCAN_PATH + "/" + siteName;
        } else {
            siteName = itemPath.substring(9);
            siteName = siteName.substring(0, siteName.indexOf("/"));
            trashcanSitePath = PAGE_TRASHCAN_PATH + "/" + siteName;
        }
        trashcanItemPath = trashcanSitePath + "/" + payloadResource.getName();
        ResourceResolver rr = payloadResource.getResourceResolver();
        Resource trashCanFolder = rr.resolve(trashcanSitePath);
        Session session = rr.adaptTo(Session.class);
        if (trashCanFolder == null || trashCanFolder.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
            Node councilTrashcanNode = JcrUtil.createPath(trashcanSitePath, JcrConstants.NT_FOLDER, session);
            String originalCouncilPath = itemPath.substring(0, (itemPath.indexOf(siteName) + siteName.length()));
            Resource permissions = rr.resolve(originalCouncilPath + "/rep:policy");
            if (permissions != null && !permissions.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                Node permissionsNode = permissions.adaptTo(Node.class);
                JcrUtil.copy(permissionsNode, councilTrashcanNode, permissionsNode.getName());
            }
            session.save();
        }
        Resource trashItem = rr.resolve(trashcanItemPath);
        if (!trashItem.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
            int count = 1;
            while (!trashItem.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                if (isAsset) {
                    trashcanItemPath = trashcanItemPath.substring(0, trashcanItemPath.lastIndexOf(".")) + "-" + count + trashcanItemPath.substring(trashcanItemPath.lastIndexOf("."));
                    trashItem = rr.resolve(trashcanItemPath);
                } else {
                    trashItem = rr.resolve(trashcanItemPath + "-" + count);
                }
                count++;
            }
            trashcanItemPath = trashItem.getPath();
        }
        session.move(itemPath, trashItem.getPath());
        Resource movedItemContent = rr.resolve(trashcanItemPath + "/jcr:content");
        if (movedItemContent != null || !movedItemContent.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
            Node movedNodeContent = movedItemContent.adaptTo(Node.class);
            Value value = session.getValueFactory().createValue(itemPath, PropertyType.PATH);
            movedNodeContent.setProperty(RESTORE_PATH_PROP_NAME, value);
            movedNodeContent.setProperty(MOVE_DATE_PROP_NAME, new GregorianCalendar());
            session.save();
        }
    }

}
