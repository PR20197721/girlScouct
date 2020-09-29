package org.girlscouts.vtk.osgi.component.util;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import org.apache.commons.lang.StringUtils;
import com.google.gson.Gson;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.*;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.MeetingE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.query.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
        List<Asset> distinctMeetingAids = new ArrayList<>();
        if(meeting != null && meetingEvent != null) {
        List<Asset> meetingAids = new ArrayList<>();
        try {
            meetingAids.addAll(getTaggedMeetingAids(meeting, "meeting-aids"));
            meetingAids.addAll(getTaggedMeetingAids(meeting, "additional-resources"));
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
        try {
            meetingAids.addAll(getAddedAssets(meetingEvent, "assets"));
            meetingAids.addAll(getAddedAssets(meetingEvent, "additionalResources"));
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
        log.debug("Returning "+meetingAids.size()+ " meeting aids");
        log.error("GET MEETING AIDS - {}", new Gson().toJson(meetingAids));
            List<Asset> validMeetingAids =  new ArrayList<>();
            if (meetingAids != null && meetingAids.size() > 1) {
                for (Asset asset : meetingAids) {
                    if (asset != null && asset.getRefId() != null && !"".equals(asset.getRefId().trim())) {
                        validMeetingAids.add(asset);
                    }
                }
                try {
                    distinctMeetingAids = validMeetingAids.stream().filter(distinctByKey(Asset::getRefId)).collect(Collectors.toList());
                } catch (Exception e) {
                    log.error("Error occurred: ", e);
                }
            }
        }
        return distinctMeetingAids;
    }

    public List<Asset> getTaggedVideos(Meeting meeting) {
        List<Asset> meetingAids = new ArrayList<>();
        String pathToVideos = "/content/vtkcontent/en/resources/volunteer-aids/vtkvideos/jcr:content/content/middle/par";
        if (meeting != null) {
            ResourceResolver rr = null;
            try {
                rr = resolverFactory.getServiceResourceResolver(resolverParams);
                Resource videosRepo = rr.resolve(pathToVideos);
                if(!ResourceUtil.isNonExistingResource(videosRepo)){
                    Iterator it = videosRepo.listChildren();
                    while (it.hasNext()) {
                       Resource video = (Resource)it.next();
                       ValueMap vm = video.getValueMap();
                       String meetingIds = vm.get("meetingid", String.class);
                       if(!StringUtils.isBlank(meetingIds) && meetingIds.contains(meeting.getId())){
                           Asset asset = new Asset();
                           asset.setRefId(vm.get("url", String.class));
                           asset.setTitle(vm.get("name", String.class));
                           asset.setType("AID");
                           asset.setDocType("movie");
                           meetingAids.add(asset);
                       }
                    }
                }
            } catch (Exception e) {
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
    private List<Asset> getTaggedMeetingAids(Meeting meeting, String path) {
        List<Asset> meetingAids = new ArrayList<>();
        if (meeting != null) {
            ResourceResolver rr = null;
            try {
                rr = resolverFactory.getServiceResourceResolver(resolverParams);
                TagManager tagManager = rr.adaptTo(TagManager.class);
                Tag searchByTag = tagManager.resolve("/etc/tags/vtkcontent/meetings/" + meeting.getLevel().toLowerCase() + "/" + meeting.getId().toLowerCase());
                if (searchByTag != null) {
                    Session session = rr.adaptTo(Session.class);
                    QueryManager qm = session.getWorkspace().getQueryManager();
                    String sql = "SELECT s.* FROM [dam:Asset] AS s WHERE ISDESCENDANTNODE([/content/dam/girlscouts-vtk/"+path+"]) AND CONTAINS(s.[jcr:content/metadata/cq:tags], '" + searchByTag.getTagID() + "')";
                    Query q = qm.createQuery(sql, Query.JCR_SQL2);
                    log.debug("Executing JCR query: " + sql);
                    QueryResult result = q.execute();
                    log.debug("returned " + result.getRows().getSize() + " results");
                    for (RowIterator it = result.getRows(); it.hasNext(); ) {
                        try {
                            Row r = it.nextRow();
                            Resource aidResource = rr.resolve(r.getPath());
                            log.debug("building vtk meeting aid object from " + aidResource.getPath());
                            if ("dam:Asset".equals(aidResource.getResourceType())) {
                                Resource metadata = aidResource.getChild("jcr:content/metadata");
                                if (metadata != null) {
                                    Asset asset = new Asset();
                                    Node props = metadata.adaptTo(Node.class);
                                    asset.setRefId(aidResource.getPath());
                                    if(aidResource.getPath().contains(".pdf") || aidResource.getPath().contains(".PDF")){
                                        asset.setDocType("pdf");
                                    }
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
                                    if (props.hasProperty("dc:isVirtualRelated")) {
                                        asset.setIsVirtualRelated(props.getProperty("dc:isVirtualRelated").getBoolean());
                                    } else {
                                        asset.setIsVirtualRelated(false);
                                    }
                                    asset.setIsCachable(true);
                                    asset.setType("AID");
                                    asset.setDescription(props.hasProperty("dc:description") ? props.getProperty("dc:description").getString() : "" );
                                    asset.setTitle(props.getProperty("dc:title").getString());
                                    asset.setSection(path);
                                    meetingAids.add(asset);
                                }
                            }
                        } catch (Exception e) {
                            log.error("Exception occurred: ", e);
                        }
                    }
                } else {
                    log.debug("No tag found for /etc/tags/vtkcontent/meetings/" + meeting.getLevel().toLowerCase() + "/" + meeting.getId().toLowerCase());
                }
            } catch (Exception e) {
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
    private List<Asset> getAddedAssets(MeetingE meetingEvent, String path) {
        List<Asset> assets = new ArrayList<Asset>();
        if (meetingEvent != null) {
            ResourceResolver rr = null;
            try {
                rr = resolverFactory.getServiceResourceResolver(resolverParams);
                Resource addedAssets = rr.getResource(meetingEvent.getPath()+"/"+path);
                if(addedAssets != null && !ResourceUtil.isNonExistingResource(addedAssets)){
                    Iterator<Resource> it = addedAssets.listChildren();
                    while(it.hasNext()){
                        Resource addedAsset = it.next();
                        String assetPath = "additionalResources".equals(path) ? addedAsset.getPath() : addedAsset.getValueMap().get("refId", String.class);
                        Asset asset = getAsset(assetPath);
                        if (asset != null) {
                            if (path.equals("assets")) asset.setSection("meeting-aids");
                            else if (path.equals("additionalResources")) asset.setSection("additional-resources");
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

    private boolean getBoolProp(Node node, String name) {
        boolean returnValue = false;
        try {
			if (node.hasProperty(name) && node.getProperty(name) != null) {
			    returnValue = node.getProperty(name).getBoolean();
			}
		} catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return returnValue;
    }

    private String getStringProp(Node node, String name) {
        String returnValue = "";
        try {
			if (node.hasProperty(name) && node.getProperty(name) != null) {
			    returnValue = node.getProperty(name).getString();
			}
		} catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return returnValue;
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
                        asset.setIsOutdoorRelated(getBoolProp(props, "dc:isOutdoorRelated"));
                        asset.setIsGlobalRelated(getBoolProp(props, "dc:isGlobalRelated"));
                        asset.setIsVirtualRelated(getBoolProp(props, "dc:isVirtualRelated"));
                        asset.setIsCachable(true);
                        asset.setType("AID");
                        asset.setDescription(getStringProp(props, "dc:description"));
                        asset.setTitle(getStringProp(props, "dc:title"));
                        asset.setUid(getStringProp(props, "dc:uid"));
                    }
                } else {
                    asset = new Asset();
                    Node props = aidResource.adaptTo(Node.class);
                    asset.setRefId(getStringProp(props, "refId"));
                    asset.setIsOutdoorRelated(getBoolProp(props, "isOutdoorRelated"));
                    asset.setIsGlobalRelated(getBoolProp(props, "isGlobalRelated"));
                    asset.setIsVirtualRelated(getBoolProp(props, "isVirtualRelated"));
                    asset.setIsCachable(false);
                    asset.setType("AID");
                    asset.setDescription(getStringProp(props, "description"));
                    asset.setTitle(getStringProp(props, "title"));
                    asset.setDocType(getStringProp(props, "docType"));
                    asset.setUid(getStringProp(props, "uid"));
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

    public Asset mapLinkNodeToAsset(Node node) {
        if (node == null) return null;
        Asset asset = new Asset();
        String url = getStringProp(node, "url");
        Pattern pattern = Pattern.compile("(youtu\\.be|youtube.com|vimeo.com)\\/");
        Matcher matcher = pattern.matcher(url);
        String docType = matcher.find() ? "movie" : "link";

        asset.setRefId(url);
        asset.setIsOutdoorRelated(getBoolProp(node, "isOutdoorRelated"));
        asset.setIsGlobalRelated(getBoolProp(node, "isGlobalRelated"));
        asset.setIsVirtualRelated(getBoolProp(node, "isVirtualRelated"));
        asset.setIsCachable(false);
        asset.setType("AID");
        asset.setDescription(getStringProp(node, "desc"));
        asset.setTitle(getStringProp(node, "name"));
        asset.setDocType(docType);
        return asset;
    }

    public List<Asset> getLinkAidsForMeeting(String path, String meetingId, String section) {
        log.debug("Getting link assets for path={} meetingId={}, section={}", path, meetingId, section);
        List<Asset> assets = new ArrayList<>();
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Resource aidResource = rr.resolve(path);
            Iterator<Resource> it = aidResource.listChildren();
            while(it.hasNext()) {
                Node aidNode = it.next().adaptTo(Node.class);
                String meetingIds = getStringProp(aidNode, "meetingid");
                log.debug("Checking meetingId={} in meetingIds={}", meetingId, meetingIds);
                if (meetingIds.contains(meetingId)) {
                    Asset linkAsset = mapLinkNodeToAsset(aidNode);
                    linkAsset.setSection(section);
                    assets.add(linkAsset);
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
        return assets;
    }
}
