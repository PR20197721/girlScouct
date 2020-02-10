package org.girlscouts.common.osgi.component;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
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
import javax.jcr.security.*;
import java.security.Principal;
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

    private void moveToTrashcan(Resource payloadResource, boolean isAsset) throws RepositoryException{
        log.debug("Moving "+payloadResource.getPath()+" to trashcan");
        String itemPath = payloadResource.getPath();
        String trashcanCouncilPath = "";
        String trashcanItemPath = "";
        String siteName = "";
        if (isAsset) {
            siteName = itemPath.substring(13);
            siteName = siteName.substring(0, siteName.indexOf("/"));
            trashcanCouncilPath = ASSET_TRASHCAN_PATH + "/" + siteName;
        } else {
            siteName = itemPath.substring(9);
            siteName = siteName.substring(0, siteName.indexOf("/"));
            trashcanCouncilPath = PAGE_TRASHCAN_PATH + "/" + siteName;
        }
        trashcanItemPath = trashcanCouncilPath + "/" + payloadResource.getName();
        ResourceResolver rr = payloadResource.getResourceResolver();
        Resource trashCanFolder = rr.resolve(trashcanCouncilPath);
        Session session = rr.adaptTo(Session.class);
        if (trashCanFolder == null || trashCanFolder.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
            JcrUtil.createPath(trashcanCouncilPath, JcrConstants.NT_FOLDER, session);
            log.debug("Created council trashcan at "+trashcanCouncilPath);
        }
        String originalCouncilPath = itemPath.substring(0, (itemPath.indexOf(siteName) + siteName.length()));
        setPermissions(trashcanCouncilPath, originalCouncilPath, session);
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
            log.debug("Successfully moved "+payloadResource.getPath()+" to "+movedNodeContent.getPath());
        }
    }

    private void setPermissions(String trashcanCouncilPath, String originalCouncilPath, Session session) throws RepositoryException {
        log.debug("Sync permissions for council trashcan at "+trashcanCouncilPath+ " with "+originalCouncilPath);
        AccessControlManager acm = session.getAccessControlManager();
        JackrabbitAccessControlList sourceAcl = getAcl(acm, originalCouncilPath);
        if (sourceAcl.size() > 0) {
            JackrabbitAccessControlList targetAcl = getAcl(acm, trashcanCouncilPath);
            AccessControlEntry[] accessControlEntries = sourceAcl.getAccessControlEntries();
            for (AccessControlEntry ace : accessControlEntries) {
                Principal principal = ace.getPrincipal();
                Privilege[] privileges = ace.getPrivileges();
                targetAcl.addEntry(principal, privileges, true, null);
            }
            acm.setPolicy(trashcanCouncilPath, targetAcl);
        }
        log.debug("Permissions for council trashcan at "+trashcanCouncilPath+ " are set");
    }

    private JackrabbitAccessControlList getAcl(AccessControlManager acm, String path) {
        try {
            log.debug("Looking up current ACLs at "+path);
            AccessControlPolicyIterator app = acm.getApplicablePolicies(path);
            while (app.hasNext()) {
                AccessControlPolicy pol = app.nextAccessControlPolicy();
                if (pol instanceof JackrabbitAccessControlList) {
                    return (JackrabbitAccessControlList) pol;
                }
            }
            // no found...check if present
            for (AccessControlPolicy pol : acm.getPolicies(path)) {
                if (pol instanceof JackrabbitAccessControlList) {
                    return (JackrabbitAccessControlList) pol;
                }
            }
        } catch (RepositoryException e) {
            log.warn("Error while retrieving ACL for {}: {}", path, e.toString());
        }
        return null;
    }
}
