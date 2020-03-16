package org.girlscouts.vtk.osgi.component.util;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.girlscouts.vtk.models.*;
import org.girlscouts.vtk.osgi.component.dao.AssetComponentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

@Component
@Service(MeetingAidUtil.class)
public class MeetingAidUtil {
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Reference
    private ResourceResolverFactory resolverFactory;
    private Map<String, Object> resolverParams = new HashMap<String, Object>();

    @Activate
    void activate() {
        this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
    }

    public List<Asset> getMeetingAids(Meeting meeting, MeetingE meetingEvent) {
        List<Asset> meetingAids = new ArrayList<>();
        List<Asset> distinctMeetingAids = new ArrayList<>();
        try {
            meetingAids.addAll(getTaggedMeetingAids(meeting));
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
        try {
            meetingAids.addAll(getAddedAssets(meetingEvent));
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
        return distinctMeetingAids;
    }

    private List<Asset> getTaggedMeetingAids(Meeting meeting) {
        List<Asset> meetingAids = new ArrayList<>();
        if (meeting != null) {
            ResourceResolver rr = null;
            try {
                rr = resolverFactory.getServiceResourceResolver(resolverParams);
                TagManager tagManager = rr.adaptTo(TagManager.class);
                Tag searchByTag = tagManager.resolve("/etc/tags/vtkcontent/meetings/"+meeting.getLevel().toLowerCase()+"/"+meeting.getId().toLowerCase());
                if(searchByTag != null)  {
                    Session session = rr.adaptTo(Session.class);
                    QueryManager qm = session.getWorkspace().getQueryManager();
                    String sql = "SELECT s.* FROM [nt:unstructured] AS s WHERE ISDESCENDANTNODE([/content/dam/girlscouts-vtk/meeting-aids]) AND CONTAINS(s.[cq:tags], '"+searchByTag.getTagID()+"')";
                    Query q = qm.createQuery(sql, Query.JCR_SQL2);
                    log.debug("Executing JCR query: " + sql);
                    QueryResult result = q.execute();
                    for (RowIterator it = result.getRows(); it.hasNext(); ) {
                        try {
                            Row r = it.nextRow();
                            Resource aidResource = rr.resolve(r.getPath());
                            if ("dam:Asset".equals(aidResource.getResourceType())) {
                                Resource metadata = aidResource.getChild("jcr:content/metadata");
                                if (metadata != null) {
                                    Asset asset = new Asset();
                                    Node props = metadata.adaptTo(Node.class);
                                    asset.setRefId(aidResource.getPath());
                                    if (props.hasProperty("dc:isOutdoorRelated")) {
                                        asset.setIsOutdoorRelated(props.getProperty("dc:isOutdoorRelated").getBoolean());
                                    } else {
                                        asset.setIsOutdoorRelated(false);
                                    }
                                    if (props.hasProperty("dc:isGlobalRelated")) {
                                        asset.setIsGlobalRelated(props.getProperty("dc:isGlobalRelated").getBoolean());
                                    } else {
                                        asset.setIsGlobalRelated(false);
                                    }
                                    asset.setIsCachable(true);
                                    asset.setType("AID");
                                    asset.setDescription(props.getProperty("dc:description").getString());
                                    asset.setTitle(props.getProperty("dc:title").getString());
                                    meetingAids.add(asset);
                                }
                            }
                        } catch (Exception e) {
                            log.error("Exception occurred: ", e);
                        }
                    }
                }else{
                    log.debug("No tag found for /etc/tags/vtkcontent/meetings/"+meeting.getLevel().toLowerCase()+"/"+meeting.getId().toLowerCase());
                }
            }catch(Exception e){
                log.error("Exception occurred: ", e);
            } finally {
            try {
                if (rr != null) {
                    rr.close();
                }
            } catch (Exception e) {
                log.error("Exception is thrown closing resource resolver: ", e);
            }
        }
        }
        return meetingAids;
    }

    private List<Asset> getAddedAssets(MeetingE meetingEvent) {
        List<Asset> assets = new ArrayList<Asset>();
        if (meetingEvent != null) {
            ResourceResolver rr = null;
            try {
                rr = resolverFactory.getServiceResourceResolver(resolverParams);
                Resource addedAssets = rr.getResource(meetingEvent.getPath()+"/assets");
                if(addedAssets != null && !ResourceUtil.isNonExistingResource(addedAssets)){
                    Iterator<Resource> it = addedAssets.listChildren();
                    while(it.hasNext()){
                        Resource addedAsset = it.next();
                        Asset asset = getAsset(addedAsset.getValueMap().get("refId", String.class));
                        if(asset != null) {
                            assets.add(asset);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error Occurred: ", e);
            } finally {
                try {
                    if (rr != null) {
                        rr.close();
                    }
                } catch (Exception e) {
                    log.error("Exception is thrown closing resource resolver: ", e);
                }
            }
        }
        return assets;
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    private List<Asset> getAidsByTags(Meeting meeting) {
        List<Asset> assets = new ArrayList<Asset>();
        if (meeting != null) {
            ResourceResolver rr = null;
            try {
                rr = resolverFactory.getServiceResourceResolver(resolverParams);
                Resource libraryMeeting = rr.getResource(meeting.getPath());
                Node meetingNode = libraryMeeting.adaptTo(Node.class);
                if (meetingNode.hasProperty("aidPaths")) {
                    Value[] assetPaths = meetingNode.getProperty("aidPaths").getValues();
                    for (int i = 0; i < assetPaths.length; i++) {
                        String assetPath = assetPaths[i].getString();
                        assets.addAll(getAssetsFromPath(assetPath, AssetComponentType.AID));
                    }
                }
            } catch (Exception e) {
                log.error("Error Occurred: ", e);
            } finally {
                try {
                    if (rr != null) {
                        rr.close();
                    }
                } catch (Exception e) {
                    log.error("Exception is thrown closing resource resolver: ", e);
                }
            }
        }
        return assets;
    }

    private List<Asset> searchAidsByTags(Meeting meeting) {
        List<Asset> matched = new ArrayList<Asset>();
        String tags = meeting.getAidTags();
        if (tags != null && !tags.trim().equals("")) {
            ResourceResolver rr = null;
            try {
                rr = resolverFactory.getServiceResourceResolver(resolverParams);
                Session session = rr.adaptTo(Session.class);
                QueryManager qm = session.getWorkspace().getQueryManager();
                String sql = buildAidsQuery(tags);
                Query q = qm.createQuery(sql, Query.SQL);
                log.debug("Executing JCR query: " + sql);
                QueryResult result = q.execute();
                for (RowIterator it = result.getRows(); it.hasNext(); ) {
                    try {
                        Row r = it.nextRow();
                        Value excerpt = r.getValue("jcr:path");
                        String path = excerpt.getString();
                        if (path.contains("/jcr:content")) {
                            path = path.substring(0, (path.indexOf("/jcr:content")));
                        }
                        Asset asset = new Asset();
                        asset.setRefId(path);
                        asset.setPath(path);
                        asset.setType(AssetComponentType.AID);
                        asset.setIsCachable(true);
                        try {
                            asset.setDescription(r.getValue("dc:description").getString());
                        } catch (Exception e) {
                            log.error("Global Aid Description missing");
                        }
                        try {
                            asset.setTitle(r.getValue("dc:title").getString());
                        } catch (Exception e) {
                        }
                        try {
                            asset.setIsOutdoorRelated(r.getValue("dc:isOutdoorRelated").getBoolean());
                        } catch (Exception e) {
                        }
                        try {
                            asset.setIsGlobalRelated(r.getValue("dc:isGlobalRelated").getBoolean());
                        } catch (Exception e) {
                        }
                        matched.add(asset);
                    } catch (Exception e) {
                        log.error("Error Occurred: ", e);
                    }
                }
            } catch (Exception e) {
                log.error("Error Occurred: ", e);
            } finally {
                try {
                    if (rr != null) {
                        rr.close();
                    }
                } catch (Exception e) {
                    log.error("Exception is thrown closing resource resolver: ", e);
                }
            }
            return matched;
        }
        return matched;
    }

    private List<Asset> searchResourcesByTags(Meeting meeting) {
        List<Asset> matched = new ArrayList<Asset>();
        String tags = meeting.getAidTags();
        if (tags != null && !tags.trim().equals("")) {
            ResourceResolver rr = null;
            try {
                rr = resolverFactory.getServiceResourceResolver(resolverParams);
                Session session = rr.adaptTo(Session.class);
                QueryManager qm = session.getWorkspace().getQueryManager();
                String sql = buildResourcesQuery(tags);
                Query q = qm.createQuery(sql, Query.SQL);
                log.debug("Executing JCR query: " + sql);
                QueryResult result = q.execute();
                for (RowIterator it = result.getRows(); it.hasNext(); ) {
                    try {
                        Row r = it.nextRow();
                        Value excerpt = r.getValue("jcr:path");
                        String path = excerpt.getString();
                        if (path.contains("/jcr:content")) {
                            path = path.substring(0, (path.indexOf("/jcr:content")));
                        }
                        Asset asset = new Asset();
                        asset.setRefId(path);
                        asset.setPath(path);
                        asset.setIsCachable(true);
                        asset.setType(AssetComponentType.RESOURCE);
                        try {
                            asset.setDescription(r.getValue("dc:description").getString());
                        } catch (Exception e) {
                        }
                        try {
                            asset.setTitle(r.getValue("dc:title").getString());
                        } catch (Exception e) {
                        }
                        matched.add(asset);
                    } catch (Exception e) {
                        log.error("Error Occurred: ", e);
                    }
                }
            } catch (Exception e) {
                log.error("Error Occurred: ", e);
            } finally {
                try {
                    if (rr != null) {
                        rr.close();
                    }
                } catch (Exception e) {
                    log.error("Exception is thrown closing resource resolver: ", e);
                }
            }
            return matched;
        }
        return matched;
    }

    private String buildAidsQuery(String tags) {
        String sql;
        String sql_tag = "";
        java.util.StringTokenizer t = new java.util.StringTokenizer(tags, ";");
        while (t.hasMoreElements()) {
            String tag = t.nextToken();
            sql_tag += "cq:tags like '%" + tag + "%'";
            if (t.hasMoreElements()) {
                sql_tag += " or ";
            }
        }
        sql = "select dc:description,dc:format, dc:title, dc:isOutdoorRelated, dc:isGlobalRelated from nt:unstructured where isdescendantnode( '/content/dam/girlscouts-vtk/global/aid/%')";
        if (!sql_tag.equals("")) {
            sql += " and ( " + sql_tag + " )";
        }
        return sql;
    }

    private String buildResourcesQuery(String tags) {
        String sql;
        String sql_tag = "";
        java.util.StringTokenizer t = new java.util.StringTokenizer(tags, ";");
        while (t.hasMoreElements()) {
            String tag = t.nextToken();
            sql_tag += "cq:tags like '%" + tag + "%'";
            if (t.hasMoreElements()) {
                sql_tag += " or ";
            }
        }
        sql = "select dc:description,dc:format, dc:title, dc:isOutdoorRelated, dc:isGlobalRelated from nt:unstructured where isdescendantnode( '/content/dam/girlscouts-vtk/global/resource/%')";
        if (!sql_tag.equals("")) {
            sql += " and ( " + sql_tag + " )";
        }
        return sql;
    }

    private List<Asset> getLocalAssets(Meeting libraryMeeting, AssetComponentType type) {
        List<Asset> assets = new ArrayList<Asset>();
        try {
            // First, respect the "aidPaths" or "resourcePaths" field in the
            // meeting. This field has multiple values.
            switch (type) {
                case AID:
                    if (libraryMeeting.getAidPaths() != null) {
                        for (String assetPath : libraryMeeting.getAidPaths()) {
                            log.debug("Asset Path = " + assetPath);
                            assets.addAll(getAssetsFromPath(assetPath, type));
                        }
                    }
                    break;
                case RESOURCE:
                    if (libraryMeeting.getResourcePaths() != null) {
                        for (String assetPath : libraryMeeting.getResourcePaths()) {
                            log.debug("Asset Path = " + assetPath);
                            assets.addAll(getAssetsFromPath(assetPath, type));
                        }
                    }
                    break;
            }
            // Then, generate an "expected" path, check if there is overrides
            // e.g. /content/dam/girlscouts-vtk2014/local/aid/B14B01
            String typeString;
            switch (type) {
                case AID:
                    typeString = "aid";
                    break;
                case RESOURCE:
                    typeString = "resource";
                    break;
                default:
                    typeString = "aid";
            }
            String schoolYear = Integer.toString(VtkUtil.getCurrentGSYear());
            String meetingCode = libraryMeeting.getPath().substring(libraryMeeting.getPath().lastIndexOf("/") + 1);
            if (meetingCode != null && meetingCode.contains("_")) {
                meetingCode = meetingCode.substring(0, meetingCode.indexOf("_"));
            }
            String rootPath = "/content/dam/girlscouts-vtk" + schoolYear + "/local/" + typeString + "/meetings/" + meetingCode;
            assets.addAll(getAssetsFromPath(rootPath, type));
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return assets;
    }

    private List<Asset> getAssetsFromPath(String rootPath, AssetComponentType type) {
        List<Asset> assets = new ArrayList<Asset>();
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Resource rootPathResource = rr.resolve(rootPath);
            if(rootPathResource != null && !ResourceUtil.isNonExistingResource(rootPathResource)){
                Iterator <Resource> assetResources = rootPathResource.listChildren();
                while(assetResources.hasNext()){
                    try {
                        Asset asset = getAsset(assetResources.next().getPath());
                        assets.add(asset);
                    }catch(Exception e){
                        log.error("Error Occurred: ", e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Cannot get assets for meeting: " + rootPath + ". Root cause was: " + e.getMessage());
        } finally {
            try {
                if (rr != null) {
                    rr.close();
                }
            } catch (Exception e) {
                log.error("Exception is thrown closing resource resolver: ", e);
            }
        }
        return assets;
    }

    public Asset getAsset(String path) {
        Asset asset = null;
        try {
            if (path != null && path.contains("metadata/")) {
                path = path.replace("metadata/", "");
            }
            ResourceResolver rr = null;
            try {
                rr = resolverFactory.getServiceResourceResolver(resolverParams);
                Resource aidResource = rr.resolve(path);
                if ("dam:Asset".equals(aidResource.getResourceType())) {
                    Resource metadata = aidResource.getChild("jcr:content/metadata");
                    if (metadata != null) {
                        asset = new Asset();
                        Node props = metadata.adaptTo(Node.class);
                        asset.setRefId(aidResource.getPath());
                        if (props.hasProperty("dc:isOutdoorRelated")) {
                            asset.setIsOutdoorRelated(props.getProperty("dc:isOutdoorRelated").getBoolean());
                        } else {
                            asset.setIsOutdoorRelated(false);
                        }
                        if (props.hasProperty("dc:isGlobalRelated")) {
                            asset.setIsGlobalRelated(props.getProperty("dc:isGlobalRelated").getBoolean());
                        } else {
                            asset.setIsGlobalRelated(false);
                        }
                        asset.setIsCachable(true);
                        asset.setType("AID");
                        asset.setDescription(props.getProperty("dc:description").getString());
                        asset.setTitle(props.getProperty("dc:title").getString());
                    }
                }
            } catch (Exception e) {
                log.error("Error Occurred: ", e);
            } finally {
                try {
                    if (rr != null) {
                        rr.close();
                    }
                } catch (Exception e) {
                    log.error("Exception is thrown closing resource resolver: ", e);
                }
            }
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return asset;
    }
}
