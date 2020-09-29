package org.girlscouts.vtk.osgi.component.util;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import org.apache.commons.lang.StringUtils;
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
            meetingAids.addAll(getTaggedMeetingAids(meeting));
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
        try {
            meetingAids.addAll(getAddedAssets(meetingEvent));
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
        log.debug("Returning "+meetingAids.size()+ " meeting aids");
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
    public List<Asset> getTaggedMeetingAids(Meeting meeting) {
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
                    String sql = "SELECT s.* FROM [dam:Asset] AS s WHERE ISDESCENDANTNODE([/content/dam/girlscouts-vtk/meeting-aids]) AND CONTAINS(s.[jcr:content/metadata/cq:tags], '" + searchByTag.getTagID() + "')";
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
                        if (props.hasProperty("dc:isVirtualRelated")) {
                            asset.setIsVirtualRelated(props.getProperty("dc:isVirtualRelated").getBoolean());
                        } else {
                            asset.setIsVirtualRelated(false);
                        }
                        asset.setIsCachable(true);
                        asset.setType("AID");
                        asset.setDescription(props.hasProperty("dc:description") ? props.getProperty("dc:description").getString() : "" );
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
