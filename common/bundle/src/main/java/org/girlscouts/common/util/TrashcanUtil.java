package org.girlscouts.common.util;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.dam.api.Asset;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.commons.ReferenceSearch;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlEntry;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.girlscouts.common.constants.TrashcanConstants;
import org.girlscouts.common.exception.GirlScoutsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.*;
import java.security.Principal;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class TrashcanUtil implements TrashcanConstants {
    protected final static Logger log = LoggerFactory.getLogger(TrashcanUtil.class);
    public static final String PARAM_BREAK_INHERITANCE = "breakInheritance";
    public static final String PARAM_LIVE_SYNC_CANCELLED = "cq:LiveSyncCancelled";

    public static boolean isLiveCopy(Resource payloadResource) throws RepositoryException, GirlScoutsException {
        log.debug("Checking if "+payloadResource.getPath()+" is a live copy");
        if (payloadResource != null && !payloadResource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
            boolean isAsset = payloadResource.isResourceType(com.day.cq.dam.api.DamConstants.NT_DAM_ASSET);
            if(!isAsset){
                LiveRelationshipManager lrm = payloadResource.getResourceResolver().adaptTo(LiveRelationshipManager.class);
                if(lrm.hasLiveRelationship(payloadResource)){
                    return !isPageInheritanceBroken(payloadResource);
                }
            }
        }
        return false;
    }

    public static boolean isPublished(Resource payloadResource) throws GirlScoutsException {
        log.debug("Checking if "+payloadResource.getPath()+" is published");
        if (payloadResource != null && !payloadResource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
            ReplicationStatus status = payloadResource.adaptTo(ReplicationStatus.class);
            if (status != null && (status.isActivated() || status.isPending() || status.isPublished())) {
                throw new GirlScoutsException(new Exception(), "Item at path " + payloadResource.getPath() + " is published");
            }
        }
        return false;
    }

    public static boolean hasReferences(Resource payloadResource) throws GirlScoutsException {
        log.debug("Checking if "+payloadResource.getPath()+" has references");
        if (payloadResource != null) {
            ReferenceSearch referenceSearch = new ReferenceSearch();
            referenceSearch.setExact(true);
            referenceSearch.setHollow(true);
            referenceSearch.setMaxReferencesPerPage(-1);
            Set<String> resultSet = referenceSearch.search(payloadResource.getResourceResolver(), payloadResource.getPath()).keySet();
            if(resultSet != null && resultSet.size() > 0) {
                StringBuffer sb = new StringBuffer();
                for(String key:resultSet){
                    if(!key.equals(payloadResource.getPath())) {
                        sb.append("<li>" + key + "</li>");
                    }
                }
                if(sb.length()>0) {
                    throw new GirlScoutsException(new Exception(), "Item at path " + payloadResource.getPath() + " has references <ol>" + sb.toString()+ "</ol>");
                }
            }
        }
        return false;
    }

    public static boolean hasChildren(Resource payloadResource) throws GirlScoutsException {
        log.debug("Checking if "+payloadResource.getPath()+" has children");
        if (payloadResource != null && !payloadResource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
            boolean isAsset = payloadResource.isResourceType(com.day.cq.dam.api.DamConstants.NT_DAM_ASSET);
            if(!isAsset){
                Page page = payloadResource.adaptTo(Page.class);
                Iterator<Page> children = page.listChildren();
                if(children.hasNext()){
                    throw new GirlScoutsException(new Exception(), "Item at path " + payloadResource.getPath() + " has children");
                }
            }
        }
        return false;
    }

    public static boolean isPageInheritanceBroken(Resource targetResource) throws RepositoryException, GirlScoutsException {
        log.info("Checking resource at {} for broken inheritance ", targetResource.getPath());
        Boolean breakInheritance = false;
        try {
            ValueMap contentProps = ResourceUtil.getValueMap(targetResource);
            breakInheritance = contentProps.get(PARAM_BREAK_INHERITANCE, false);
        } catch (Exception e) {
            log.error("Encountered error: ", e);
        }
        if (breakInheritance) {
            log.info("Resource at {} has parameter breakInheritance = {}", targetResource.getPath(), breakInheritance);
        } else {
            Node node = targetResource.adaptTo(Node.class);
            if (node.isNodeType(PARAM_LIVE_SYNC_CANCELLED)) {
                log.info("Resource at {} has mixin type cq:LiveSyncCancelled", targetResource.getPath());
                breakInheritance = true;
            } else {
                Resource targetResourceContent = targetResource.getChild("jcr:content");
                try {
                    ValueMap contentProps = ResourceUtil.getValueMap(targetResourceContent);
                    breakInheritance = contentProps.get(PARAM_BREAK_INHERITANCE, false);
                    if (breakInheritance) {
                        log.info("Resource at {} has parameter breakInheritance = {}", targetResourceContent.getPath(), breakInheritance);
                    } else {
                        Node contentNode = targetResourceContent.adaptTo(Node.class);
                        if (contentNode.isNodeType(PARAM_LIVE_SYNC_CANCELLED)) {
                            log.info("Resource at {} has mixin type cq:LiveSyncCancelled", targetResourceContent.getPath());
                            breakInheritance = true;
                        }
                    }
                } catch (Exception e) {
                    log.error("Encountered error: ", e);
                }
            }
        }
        if (!breakInheritance) {
            log.info("Resource at {} is inherited ", targetResource.getPath());
            throw new GirlScoutsException(new Exception(), "Item at path " + targetResource.getPath() + " has Live Relationship");
        }
        log.info("Resource at {} has broken inheritance ", targetResource.getPath());
        return breakInheritance;
    }
    public static String getRestoreItemPath(Resource payloadResource)  throws RepositoryException{
        Resource trashedContent = payloadResource.getChild("jcr:content");
        ValueMap props = trashedContent.getValueMap();
        String restorePath = props.get(RESTORE_PATH_PROP_NAME).toString();
        return restorePath;
    }
    public static String getTrashItemPath(boolean isAsset, Resource payloadResource) throws RepositoryException {
        ResourceResolver rr = payloadResource.getResourceResolver();
        Session session = rr.adaptTo(Session.class);
        String trashcanCouncilPath = "";
        String siteName = "";
        String itemPath = payloadResource.getPath();
        if (isAsset) {
            siteName = itemPath.substring(13);
            siteName = siteName.substring(0, siteName.indexOf("/"));
            trashcanCouncilPath = ASSET_TRASHCAN_PATH + "/" + siteName;
        } else {
            siteName = itemPath.substring(9);
            siteName = siteName.substring(0, siteName.indexOf("/"));
            trashcanCouncilPath = PAGE_TRASHCAN_PATH + "/" + siteName;
        }
        Resource trashCanFolder = rr.resolve(trashcanCouncilPath);
        if (trashCanFolder == null || trashCanFolder.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
            JcrUtil.createPath(trashcanCouncilPath, JcrConstants.NT_FOLDER, session);
            log.debug("Created council trashcan at "+trashcanCouncilPath);
        }
        String originalCouncilPath = itemPath.substring(0, (itemPath.indexOf(siteName) + siteName.length()));
        setPermissions(trashcanCouncilPath, originalCouncilPath, session);
        String trashItemPath = trashcanCouncilPath + "/" + payloadResource.getName();
        Resource trashItem = getAvailableResource(isAsset, rr, trashItemPath);
        return trashItem.getPath();
    }

    private static Resource getAvailableResource(boolean isAsset, ResourceResolver rr, String path) {
        Resource resource = rr.resolve(path);
        if (!resource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
            int count = 1;
            while (!resource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                if (isAsset) {
                    path = path.substring(0, path.lastIndexOf(".")) + "-" + count + path.substring(path.lastIndexOf("."));
                    resource = rr.resolve(path);
                } else {
                    resource = rr.resolve(path + "-" + count);
                }
                count++;
            }
        }
        return resource;
    }

    private static void setPermissions(String trashcanCouncilPath, String originalCouncilPath, Session session) throws RepositoryException {
        log.debug("Sync permissions for council trashcan at "+trashcanCouncilPath+ " with "+originalCouncilPath);
        AccessControlManager acm = session.getAccessControlManager();
        JackrabbitAccessControlList sourceAcl = getAcl(acm, originalCouncilPath);
        if (sourceAcl.size() > 0) {
            JackrabbitAccessControlList targetAcl = getAcl(acm, trashcanCouncilPath);
            JackrabbitAccessControlEntry[] accessControlEntries = (JackrabbitAccessControlEntry[]) sourceAcl.getAccessControlEntries();
            for (JackrabbitAccessControlEntry ace : accessControlEntries) {
                Principal principal = ace.getPrincipal();
                Privilege[] privileges = ace.getPrivileges();
                targetAcl.addEntry(principal, privileges, ace.isAllow(), null);
            }
            acm.setPolicy(trashcanCouncilPath, targetAcl);
        }
        log.debug("Permissions for council trashcan at "+trashcanCouncilPath+ " are set");
    }

    private static JackrabbitAccessControlList getAcl(AccessControlManager acm, String path) {
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

    public static boolean restorePathExists(Resource payloadResource, String parentPath) throws GirlScoutsException, RepositoryException {
        log.debug("Checking if restore path for"+payloadResource.getPath()+" exists");
        if (payloadResource != null && !payloadResource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
            if(parentPath == null || parentPath.trim().length() == 0){
                parentPath = getRestoreItemPath(payloadResource);
            }
            ResourceResolver rr = payloadResource.getResourceResolver();
            Resource parentResource = rr.resolve(parentPath);
            if(parentResource == null || ResourceUtil.isNonExistingResource(parentResource)){
                throw new GirlScoutsException(new Exception(), "Restore location " + parentPath + " is not available.");
            }
        }
        return true;
    }

    public static String getRestoreItemName(Resource payloadResource) throws RepositoryException {
        ResourceResolver rr = payloadResource.getResourceResolver();
        boolean isAsset = payloadResource.isResourceType(com.day.cq.dam.api.DamConstants.NT_DAM_ASSET);
        String restorePath = getRestoreItemPath(payloadResource);
        Resource restoreResource = getAvailableResource(isAsset, rr, restorePath+"/"+payloadResource.getName());
        return restoreResource.getName();
    }
}
