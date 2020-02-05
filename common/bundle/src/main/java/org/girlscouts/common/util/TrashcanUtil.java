package org.girlscouts.common.util;

import com.day.cq.replication.ReplicationStatus;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.commons.ReferenceSearch;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Collection;
import java.util.Iterator;

public class TrashcanUtil {
    protected final static Logger log = LoggerFactory.getLogger(TrashcanUtil.class);
    public static final String PARAM_BREAK_INHERITANCE = "breakInheritance";
    public static final String PARAM_LIVE_SYNC_CANCELLED = "cq:LiveSyncCancelled";

    public static boolean isLiveCopy(Resource payloadResource) throws RepositoryException {
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

    public static boolean isPublished(Resource payloadResource) {
        if (payloadResource != null && !payloadResource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
            boolean isAsset = payloadResource.isResourceType(com.day.cq.dam.api.DamConstants.NT_DAM_ASSET);
            if(!isAsset){
                try {
                    Page page = payloadResource.adaptTo(Page.class);
                    ReplicationStatus status = page.adaptTo(ReplicationStatus.class);
                    if (status != null) {
                        return status.isActivated();
                    }
                } catch (Exception e) {
                    log.debug(e.getMessage(), e);
                }
            }
        }
        return false;
    }

    public static boolean hasReferences(Resource payloadResource) {
        if (payloadResource != null) {
            ReferenceSearch referenceSearch = new ReferenceSearch();
            referenceSearch.setExact(true);
            referenceSearch.setHollow(true);
            referenceSearch.setMaxReferencesPerPage(-1);
            Collection<ReferenceSearch.Info> resultSet = referenceSearch.search(payloadResource.getResourceResolver(), payloadResource.getPath()).values();
            if(resultSet != null) {
                return resultSet.size() > 0;
            }
        }
        return false;
    }

    public static boolean hasChildren(Resource payloadResource) {
        if (payloadResource != null && !payloadResource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
            boolean isAsset = payloadResource.isResourceType(com.day.cq.dam.api.DamConstants.NT_DAM_ASSET);
            if(!isAsset){
                try{
                    Page page = payloadResource.adaptTo(Page.class);
                    Iterator<Page> children = page.listChildren();
                    if(children.hasNext()){
                        return true;
                    }
                }catch(Exception e){

                }
            }
        }
        return false;
    }

    public static boolean isPageInheritanceBroken(Resource targetResource) throws RepositoryException {
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
        if (breakInheritance) {
            log.info("Resource at {} has broken inheritance ", targetResource.getPath());
        } else {
            log.info("Resource at {} is inherited ", targetResource.getPath());
        }
        return breakInheritance;
    }
}
