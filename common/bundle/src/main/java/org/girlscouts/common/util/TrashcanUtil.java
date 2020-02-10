package org.girlscouts.common.util;

import com.day.cq.dam.api.Asset;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.commons.ReferenceSearch;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.girlscouts.common.exception.GirlScoutsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class TrashcanUtil {
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
            boolean isAsset = payloadResource.isResourceType(com.day.cq.dam.api.DamConstants.NT_DAM_ASSET);
            ReplicationStatus status = null;
            if(isAsset){
                Asset asset = payloadResource.adaptTo(Asset.class);
                status = asset.adaptTo(ReplicationStatus.class);
            }else{
                Page page = payloadResource.adaptTo(Page.class);
                status = page.adaptTo(ReplicationStatus.class);
            }
            if (status != null && status.isActivated()) {
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
}
