package org.girlscouts.vtk.ejb;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.apache.jackrabbit.ocm.query.Filter;
import org.apache.jackrabbit.ocm.query.Query;
import org.apache.jackrabbit.ocm.query.QueryManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.girlscouts.common.search.DocHit;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.dao.AssetComponentType;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.dao.YearPlanComponentType;
import org.girlscouts.vtk.models.*;
import org.girlscouts.vtk.osgi.component.CouncilMapper;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMService;
import org.girlscouts.vtk.utils.VtkException;
import org.girlscouts.vtk.utils.VtkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Service(value = MeetingDAO.class)
public class MeetingDAOImpl implements MeetingDAO {
    public static final String RESOURCE_COUNT_MAP_AGE = "RESOURCE_COUNT_MAP_AGE";
    public static final long MAX_CACHE_AGE_MS = 3600000; // 1 hour in ms
    private static final String AID_PATHS_PROP = "aidPaths";
    private static final String RESOURCE_PATHS_PROP = "resourcePaths";
    public static Map resourceCountMap = new PassiveExpiringMap(MAX_CACHE_AGE_MS);
    private final Logger log = LoggerFactory.getLogger(MeetingDAOImpl.class);
    @Reference
    CouncilMapper councilMapper;
    @Reference
    UserUtil userUtil;
    @Reference
    private ResourceResolverFactory resolverFactory;
    private Map<String, Object> resolverParams = new HashMap<String, Object>();
    @Reference
    private QueryBuilder qBuilder;
    @Reference
    private GirlScoutsOCMService girlScoutsOCMService;

    @Activate
    void activate() {
        this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
        resourceCountMap.put(RESOURCE_COUNT_MAP_AGE, System.currentTimeMillis());
    }

    // by planId
    public List<MeetingE> getAllEventMeetings(User user, Troop troop, String yearPlanId) throws IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_VIEW_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        List<MeetingE> meetings = null;
        String path = "/content/girlscouts-vtk/yearPlanTemplates/yearplan" + user.getCurrentYear() + "/brownie/yearPlan" + yearPlanId + "/meetings/";
        meetings = girlScoutsOCMService.findObjects(path, null, MeetingE.class);
        return meetings;
    }

    // by plan path
    public List<MeetingE> getAllEventMeetings_byPath(User user, Troop troop, String yearPlanPath) throws IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_VIEW_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        List<MeetingE> meetings = girlScoutsOCMService.findObjects(yearPlanPath, null, MeetingE.class);
        return meetings;
    }

    public Meeting getMeeting(User user, Troop troop, String path) throws IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_VIEW_MEETING_ID) && !userUtil.hasPermission(troop, Permission.PERMISSION_VIEW_REPORT_ID)) {
            throw new IllegalAccessException();
        }
        Meeting meeting = girlScoutsOCMService.read(path);
        if (meeting != null && path != null && path.contains("/lib/meetings/")) {
            Meeting globalMeetingInfo = getMeeting(user, troop, "/content/girlscouts-vtk/meetings/myyearplan" + VtkUtil.getCurrentGSYear() + "/" + meeting.getLevel().toLowerCase().trim() + "/" + meeting.getId());
            if (globalMeetingInfo != null) {
                meeting.setMeetingInfo(globalMeetingInfo.getMeetingInfo());
                meeting.setIsAchievement(globalMeetingInfo.getIsAchievement());
            }
        }//edn if
        return meeting;
    }

    // get all event meetings for users plan
    public java.util.List<MeetingE> getAllUsersEventMeetings(User user, Troop troop, String yearPlanId) throws IllegalStateException, IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_VIEW_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        String path = "/content/girlscouts-vtk/users/" + troop.getId() + "/yearPlan/meetingEvents/";
        List<MeetingE> meetings = girlScoutsOCMService.findObjects(path, null, MeetingE.class);
        return meetings;
    }

    // get all event meetings for users plan
    public Meeting createCustomMeeting(User user, Troop troop, MeetingE meetingEvent) throws IllegalAccessException, IllegalStateException {
        return createCustomMeeting(user, troop, meetingEvent, null);
    }

    public Meeting createCustomMeeting(User user, Troop troop, MeetingE meetingEvent, Meeting meeting) throws IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_CREATE_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        if (meeting == null) {
            meeting = getMeeting(user, troop, meetingEvent.getRefId());
        }
        String newPath = troop.getPath() + "/lib/meetings/" + meeting.getId() + "_" + Math.random();
        if (meetingEvent.getRefId().contains("/lib/meetings/")) {
            newPath = meetingEvent.getRefId();
            girlScoutsOCMService.delete(meeting);
        }
        meetingEvent.setRefId(newPath);
        meeting.setPath(newPath);
        meeting = girlScoutsOCMService.create(meeting);
        girlScoutsOCMService.update(meetingEvent);
        return meeting;

    }

    public Meeting updateCustomMeeting(User user, Troop troop, MeetingE meetingEvent, Meeting meeting) throws IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID)) {
            throw new IllegalAccessException();
        }

        if (meeting == null) {
            meeting = getMeeting(user, troop, meetingEvent.getRefId());
        }
        String newPath = meetingEvent.getRefId();
        meetingEvent.setRefId(newPath);
        meeting.setPath(newPath);
        meeting = girlScoutsOCMService.update(meeting);
        girlScoutsOCMService.update(meetingEvent);
        return meeting;

    }

    public Meeting addActivity(User user, Troop troop, Meeting meeting, Activity activity) throws IllegalStateException, IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_ADD_ACTIVITY_ID)) {
            throw new IllegalAccessException();
        }
        List<Activity> activities = meeting.getActivities();
        activities.add(activity);
        meeting.setActivities(activities);
        meeting = girlScoutsOCMService.update(meeting);
        return meeting;
    }

    public List<Asset> getAidTag(User user, Troop troop, String tags, String meetingName) throws IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_LOGIN_ID)) {
            throw new IllegalAccessException();
        }
        List<Asset> matched = new ArrayList<Asset>();
        try {
            if (tags == null || tags.trim().equals("")) {
                return matched;
            }
            String sql_tag = "";
            java.util.StringTokenizer t = new java.util.StringTokenizer(tags, ";");
            while (t.hasMoreElements()) {
                String tag = t.nextToken();
                sql_tag += "cq:tags like '%" + tag + "%'";
                if (t.hasMoreElements()) {
                    sql_tag += " or ";
                }
            }
            String sql = "";
            sql = "select dc:description,dc:format, dc:title, dc:isOutdoorRelated from nt:unstructured where isdescendantnode( '/content/dam/girlscouts-vtk/global/aid/%')";
            if (!sql_tag.equals("")) {
                sql += " and ( " + sql_tag + " )";
            }
            QueryResult result = girlScoutsOCMService.executeQuery(sql);
            for (RowIterator it = result.getRows(); it.hasNext(); ) {
                try {
                Row r = it.nextRow();
                Value excerpt = r.getValue("jcr:path");
                String path = excerpt.getString();
                if (path.contains("/jcr:content")) {
                    path = path.substring(0, (path.indexOf("/jcr:content")));
                }
                Asset search = new Asset();
                search.setRefId(path);
                search.setType(AssetComponentType.AID);
                search.setIsCachable(true);
                try {
                    search.setDescription(r.getValue("dc:description").getString());
                } catch (Exception e) {
                    log.error("Global Aid Description missing");
                }
                try {
                    search.setTitle(r.getValue("dc:title").getString());
                } catch (Exception e) {
                }
                try {
                    search.setIsOutdoorRelated(r.getValue("dc:isOutdoorRelated").getBoolean());
                } catch (Exception e) {
                }
                matched.add(search);
                } catch (Exception e) {
                    log.error("Error Occurred: ", e);
                }
            }
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return matched;
    }

    public List<Asset> getAidTag_local(User user, Troop troop, String tags, String meetingName, String meetingPath) throws IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_LOGIN_ID)) {
            throw new IllegalAccessException();
        }
        return getLocalAssets(meetingName, meetingPath, AssetComponentType.AID);
    }

    private List<Asset> getLocalAssets(String meetingName, String meetingPath, AssetComponentType type) {
        List<Asset> assets = new ArrayList<Asset>();
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            // First, respect the "aidPaths" or "resourcePaths" field in the
            // meeting. This field has multiple values.
            Node meetingNode = session.getNode(meetingPath);
            String pathProp;
            switch (type) {
                case AID:
                    pathProp = AID_PATHS_PROP;
                    break;
                case RESOURCE:
                    pathProp = RESOURCE_PATHS_PROP;
                    break;
                default:
                    pathProp = AID_PATHS_PROP;
            }
            if (meetingNode.hasProperty(pathProp)) {
                Value[] assetPaths = meetingNode.getProperty(pathProp).getValues();
                for (int i = 0; i < assetPaths.length; i++) {
                    String assetPath = assetPaths[i].getString();
                    log.debug("Asset Path = " + assetPath);
                    assets.addAll(getAssetsFromPath(assetPath, type, session));
                }
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
            String rootPath = getSchoolYearDamPath() + "/local/" + typeString + "/meetings/" + meetingName;
            if (session.nodeExists(rootPath)) {
                assets.addAll(getAssetsFromPath(rootPath, type, session));
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

    public List<Asset> getResource_global(User user, Troop troop, String tags, String meetingName) throws IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_LOGIN_ID)) {
            throw new IllegalAccessException();
        }
        List<Asset> matched = new ArrayList<Asset>();
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            if (tags == null || tags.equals("")) {
                return matched;
            }
            String sql_tag = "";
            java.util.StringTokenizer t = new java.util.StringTokenizer(tags, ";");
            while (t.hasMoreElements()) {
                String tag = t.nextToken();
                sql_tag += "cq:tags like '%" + tag + "%'";
                if (t.hasMoreElements()) {
                    sql_tag += " or ";
                }
            }
            String sql = "select dc:description,dc:format, dc:title from nt:unstructured where isdescendantnode( '/content/dam/girlscouts-vtk/global/resource/%' ) ";
            if (!sql_tag.equals("")) {
                sql += " and ( " + sql_tag + " )";
            }
            javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
            javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL);
            QueryResult result = q.execute();
            for (RowIterator it = result.getRows(); it.hasNext(); ) {
                Row r = it.nextRow();
                Value excerpt = r.getValue("jcr:path");
                String path = excerpt.getString();
                if (path.contains("/jcr:content")) {
                    path = path.substring(0, (path.indexOf("/jcr:content")));
                }
                Asset search = new Asset();
                search.setRefId(path);
                search.setIsCachable(true);
                search.setType(AssetComponentType.RESOURCE);
                try {
                    search.setDescription(r.getValue("dc:description").getString());
                } catch (Exception e) {
                }
                try {
                    search.setTitle(r.getValue("dc:title").getString());
                } catch (Exception e) {
                }
                matched.add(search);

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

    private List<Asset> getAssetsFromPath(String rootPath, AssetComponentType type, Session session) {
        List<Asset> assets = new ArrayList<Asset>();
        try {
            if (!session.nodeExists(rootPath)) {
                return assets;
            }
            Node rootNode = session.getNode(rootPath);
            NodeIterator iter = rootNode.getNodes();
            while (iter.hasNext()) {
                Node node = null;
                try {
                    node = iter.nextNode();
                    if (!node.hasNode("jcr:content")) {
                        continue;
                    }
                    Node props = node.getNode("jcr:content/metadata");
                    Asset asset = new Asset();
                    asset.setRefId(node.getPath());
                    if (props.hasProperty("dc:isOutdoorRelated")) {
                        asset.setIsOutdoorRelated(props.getProperty("dc:isOutdoorRelated").getBoolean());
                    } else {
                        asset.setIsOutdoorRelated(false);
                    }
                    asset.setIsCachable(true);
                    asset.setType(type);
                    asset.setDescription(props.getProperty("dc:description").getString());
                    asset.setTitle(props.getProperty("dc:title").getString());
                    assets.add(asset);
                } catch (Exception e) {
                    if (node != null) {
                        log.warn("Cannot get asset " + node.getPath());
                    }
                    log.warn("Cannot get asset. rootPath = " + rootPath + ". Root cause was: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Cannot get assets for meeting: " + rootPath + ". Root cause was: " + e.getMessage());
        }
        return assets;
    }

    //public List<Asset> getResource_local(User user, Troop troop, String tags, String meetingName, String meetingPath)
    public List<Asset> getResource_local(User user, Troop troop, String meetingName, String meetingPath) throws IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_LOGIN_ID)) {
            throw new IllegalAccessException();
        }
        return getLocalAssets(meetingName, meetingPath, AssetComponentType.RESOURCE);
    }

    private String getSchoolYearDamPath() {
        String schoolYear = Integer.toString(VtkUtil.getCurrentGSYear());
        String path = "/content/dam/girlscouts-vtk" + schoolYear;
        return path;
    }

    public SearchTag searchA(User user, Troop troop, String councilCode) throws IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_LOGIN_ID)) {
            throw new IllegalAccessException();
        }
        String councilStr = councilMapper.getCouncilBranch(councilCode);
        councilStr = councilStr.replace("/content/", "");
        Session session = null;
        SearchTag tags = new SearchTag();
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            String tagStr = councilStr;
            try {
                Node homepage = session.getNode("/content/" + councilStr + "/en/jcr:content");
                if (homepage != null) {
                    if (homepage.hasProperty("event-cart")) {
                        if ("true".equals(homepage.getProperty("event-cart").getString())) {
                            tagStr = "sf-activities";
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            java.util.Map<String, String> regionsMain = searchRegion(user, troop, councilStr);
            java.util.Map<String, String> categories = new java.util.TreeMap();
            java.util.Map<String, String> levels = new java.util.TreeMap();
            String sql = "select jcr:title from cq:Tag where ISDESCENDANTNODE( '/etc/tags/" + tagStr + "')";
            javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
            javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL);
            QueryResult result = q.execute();
            for (RowIterator it = result.getRows(); it.hasNext(); ) {
                Row r = it.nextRow();
                if (r.getPath().startsWith("/etc/tags/" + tagStr + "/categories")) {
                    String elem = r.getValue("jcr:title").getString();
                    categories.put(r.getNode().getName(), elem);
                } else if (r.getPath().startsWith("/etc/tags/" + tagStr + "/program-level")) {
                    String elem = r.getValue("jcr:title").getString();
                    levels.put(r.getNode().getName(), elem);
                }

            }
            if ((categories == null || categories.size() == 0) && (levels == null || levels.size() == 0)) {
                try {
                    SearchTag defaultTags = getDefaultTags(user, troop);
                    if (regionsMain != null && regionsMain.size() > 0) {
                        defaultTags.setRegion(regionsMain);
                    }
                    return defaultTags;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (categories != null) {
                categories.remove("Categories");
                categories.remove("categories");
            }
            if (levels != null) {
                levels.remove("Program Level");
                levels.remove("program-level");
                levels.remove("program level");
            }
            tags.setCategories(categories);
            tags.setLevels(levels);
            tags.setRegion(searchRegion(user, troop, councilStr));

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
        return tags;
    }

    public SearchTag getDefaultTags(User user, Troop troop) throws IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_LOGIN_ID)) {
            throw new IllegalAccessException();
        }
        Session session = null;
        String councilStr = "girlscouts";
        SearchTag tags = new SearchTag();
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            java.util.Map<String, String> categories = new java.util.TreeMap();
            java.util.Map<String, String> levels = new java.util.TreeMap();
            String sql = "select jcr:title from cq:Tag where isdescendantnode( '/etc/tags/" + councilStr + "%')";
            javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
            javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL);
            QueryResult result = q.execute();
            for (RowIterator it = result.getRows(); it.hasNext(); ) {
                Row r = it.nextRow();
                if (r.getPath().startsWith("/etc/tags/" + councilStr + "/categories")) {
                    String elem = r.getValue("jcr:title").getString();
                    if (elem != null) {
                        elem = elem.toLowerCase().replace("_", "").replace("/", "");
                    }
                    categories.put(elem, null);
                } else if (r.getPath().startsWith("/etc/tags/" + councilStr + "/program-level")) {
                    String elem = r.getValue("jcr:title").getString();
                    if (elem != null) {
                        elem = elem.toLowerCase().replace("_", "").replace("/", "");
                    }
                    levels.put(elem, null);
                }
            }
            if (categories != null) {
                categories.remove("Categories");
                categories.remove("categories");
            }
            if (levels != null) {
                levels.remove("Program Level");
                levels.remove("program-level");
                levels.remove("program level");
            }
            tags.setCategories(categories);
            tags.setLevels(levels);
            tags.setRegion(searchRegion(user, troop, councilStr));
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
        return tags;
    }

    public java.util.List<Activity> searchA2(Troop troop, String tags, String cat, String keywrd, java.util.Date startDate, java.util.Date endDate, String region) {
        java.util.List<Activity> toRet = new java.util.ArrayList();
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            boolean isTag = false;
            String sqlTags = "";
            if (tags.equals("|")) {
                tags = "";
            }
            StringTokenizer t = new StringTokenizer(tags, "|");
            while (t.hasMoreElements()) {
                sqlTags += " contains(parent.[cq:tags], 'program-level/" + t.nextToken() + "') ";
                if (t.hasMoreElements()) {
                    sqlTags += " or ";
                }
                isTag = true;
            }
            if (isTag) {
                sqlTags = " and (" + sqlTags + " ) ";
            }
            String sqlCat = "";
            if (cat.equals("|")) {
                cat = "";
            }
            t = new StringTokenizer(cat, "|");
            while (t.hasMoreElements()) {
                sqlCat += " contains(parent.[cq:tags], 'categories/" + t.nextToken() + "') ";
                if (t.hasMoreElements()) {
                    sqlCat += " or ";
                }
                isTag = true;
            }
            if (!sqlCat.equals("")) {
                sqlCat = " and (" + sqlCat + " ) ";
            }
            String regionSql = "";
            if (region != null && !region.trim().equals("")) {
                regionSql += " and LOWER(region) ='" + region + "'";
            }
            String path = "/content/gateway/en/events/" + VtkUtil.getCurrentGSYear() + "/%";
            if (!isTag) {
                path = path + "/data";
            } else {
                path = path + "/jcr:content";
            }
            String councilId = null;
            if (troop != null) {
                councilId = troop.getCouncilCode();
            }
            String branch = councilMapper.getCouncilBranch(councilId);
            branch += "/en";
            String eventPath = "";
            try {
                eventPath = session.getProperty(branch + "/jcr:content/eventPath").getString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String sql = "select child.address, parent.[jcr:uuid], child.start, parent.[jcr:title], child.details, child.end,child.locationLabel,child.srchdisp  from [nt:base] as parent INNER JOIN [nt:base] as child ON ISCHILDNODE(child, parent) where  (isdescendantnode (parent, [" + eventPath + "])) and child.start is not null and parent.[jcr:title] is not null ";
            if (keywrd != null && !keywrd.trim().equals(""))// && !isTag )
            {
                sql += " and (contains(child.*, '" + keywrd + "') or contains(parent.*, '" + keywrd + "')  )";
            }
            if (!isTag) {
                sql += regionSql;
            }
            sql += sqlTags;
            sql += sqlCat;
            javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
            javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.JCR_SQL2);
            int i = 0;
            QueryResult result = q.execute();
            for (RowIterator it = result.getRows(); it.hasNext(); ) {
                Row r = it.nextRow();
                Value[] v = r.getValues();
                Activity activity = new Activity();
                activity.setUid("A" + new java.util.Date().getTime() + "_" + Math.random());
                activity.setContent(r.getValue("child.details").getString());
                activity.setDate(r.getValue("child.start").getDate().getTime());
                try {
                    activity.setEndDate(r.getValue("child.end").getDate().getTime());
                } catch (Exception e) {
                }
                if ((activity.getDate().before(new java.util.Date()) && activity.getEndDate() == null) || (activity.getEndDate() != null && activity.getEndDate().before(new java.util.Date()))) {
                    continue;
                }
                activity.setLocationName(r.getValue("child.locationLabel").getString());
                try {
                    activity.setLocationAddress(r.getValue("child.address").getString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //activity.setName(r.getValue("child.srchdisp").getString());
                activity.setName(r.getValue("parent.jcr:title").getString());
                activity.setType(YearPlanComponentType.ACTIVITY);
                activity.setId("ACT" + i);
                // patch
                if (activity.getDate() != null && activity.getEndDate() == null) {
                    activity.setEndDate(activity.getDate());
                }
                activity.setIsEditable(false);
                try {
                    activity.setRefUid(r.getValue("parent.jcr:uuid").getString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (startDate != null && endDate != null) {
                    if ((startDate.after(endDate)) || activity.getEndDate().before(startDate) || ((activity.getDate().before(startDate) || activity.getDate().after(startDate)) && activity.getDate().after(endDate)) || (activity.getEndDate().before(startDate) && (activity.getEndDate().after(endDate) || activity.getEndDate().after(endDate)))) {
                        continue;
                    }
                }
                toRet.add(activity);
                i++;
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
        return toRet;
    }

    public java.util.Map<String, String> searchRegion(User user, Troop troop, String councilStr) throws IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_LOGIN_ID)) {
            throw new IllegalAccessException();
        }
        java.util.Map<String, String> container = new java.util.TreeMap();
        Session session = null;
        Node homepage = null;
        if (councilStr != null && !councilStr.startsWith("/content/")) {
            councilStr = "/content/" + councilStr;
        }
        String repoStr = councilStr + "/en/events-repository";
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            try {
                homepage = session.getNode(councilStr + "/en/jcr:content");
                if (homepage != null) {
                    if (homepage.hasProperty("eventPath")) {
                        repoStr = homepage.getProperty("eventPath").getString();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            java.util.Map<String, String> categories = new java.util.TreeMap();
            java.util.Map<String, String> levels = new java.util.TreeMap();
            String sql = "select region, start, end from cq:Page where ISDESCENDANTNODE('" + repoStr + "')  and region is not null";
            javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
            javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL);
            QueryResult result = q.execute();
            for (RowIterator it = result.getRows(); it.hasNext(); ) {
                Row r = it.nextRow();
                String elem = r.getValue("region").getString();
                elem = elem.toLowerCase();
                try {
                    java.util.Calendar startDate = null, endDate = null, now = null;
                    now = java.util.Calendar.getInstance();
                    try {
                        startDate = r.getValue("start").getDate();
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("searchRegion invalid startDate");
                    }
                    try {
                        endDate = r.getValue("end").getDate();
                    } catch (Exception e) {
                        log.error("searchRegion invalid endDate");
                    }
                    if (endDate != null && endDate.before(now)) {
                        continue;
                    } else if (endDate == null && startDate.before(now)) {
                        continue;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!container.containsKey(elem)) {
                    container.put(elem, null);
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
        return container;
    }

    public java.util.List<Meeting> getAllMeetings(User user, Troop troop, String gradeLevel) throws IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_VIEW_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        java.util.List<Meeting> meetings = null;
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            List<Class> classes = new ArrayList<Class>();
            classes.add(Meeting.class);
            classes.add(Activity.class);
            classes.add(JcrCollectionHoldString.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            QueryManager queryManager = ocm.getQueryManager();
            Filter filter = queryManager.createFilter(Meeting.class);
            filter.setScope("/content/girlscouts-vtk/meetings/myyearplan" + VtkUtil.getCurrentGSYear() + "/" + gradeLevel + "/");
            Query query = queryManager.createQuery(filter);
            meetings = (List<Meeting>) ocm.getObjects(query);
            Comparator<Meeting> comp = new BeanComparator("position");
            if (meetings != null) {
                Collections.sort(meetings, comp);
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
        return meetings;

    }

    public List<Asset> getAllResources(User user, Troop troop, String _path) throws IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_LOGIN_ID)) {
            throw new IllegalAccessException();
        }
        List<Asset> matched = new ArrayList<Asset>();
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            String sql = "select [dc:description], [dc:format], [dc:title], [jcr:mimeType], [jcr:path], [dc:isOutdoorRelated] " + " from [nt:unstructured] as parent where " + " (isdescendantnode (parent, [" + _path + "])) and [cq:tags] is not null";
            session = rr.adaptTo(Session.class);
            javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
            javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.JCR_SQL2);
            QueryResult result = q.execute();
            for (RowIterator it = result.getRows(); it.hasNext(); ) {
                Row r = it.nextRow();
                Asset search = new Asset();
                search.setRefId(r.getPath().replace("/jcr:content/metadata", ""));
                search.setIsCachable(true);
                search.setType(AssetComponentType.RESOURCE);
                try {
                    search.setDescription(r.getValue("dc:description").getString());
                } catch (Exception e) {
                }
                try {
                    search.setTitle(r.getValue("dc:title").getString());
                } catch (Exception e) {
                }
                try {
                    search.setIsOutdoorRelated(r.getValue("dc:isOutdoorRelated").getBoolean());
                } catch (Exception e) {
                }
                try {
                    if (r.getPath().indexOf(".") != -1) {
                        String t = r.getPath().substring(r.getPath().indexOf("."));
                        t = t.substring(1, t.indexOf("/"));
                        search.setDocType(t);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                matched.add(search);
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

    public Asset getAsset(User user, Troop troop, String _path) throws IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_LOGIN_ID)) {
            throw new IllegalAccessException();
        }
        Asset search = null;
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            String sql = "";
            if (_path != null && _path.contains("metadata/")) {
                _path = _path.replace("metadata/", "");
            }
            sql = "select dc:description,dc:format, dc:title,dc:isOutdoorRelated from nt:unstructured where isdescendantnode( '" + _path + "%') and cq:tags is not null";
            javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
            javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL);
            QueryResult result = q.execute();
            for (RowIterator it = result.getRows(); it.hasNext(); ) {
                Row r = it.nextRow();
                Value excerpt = r.getValue("jcr:path");
                String path = excerpt.getString();
                if (path.contains("/jcr:content")) {
                    path = path.substring(0, (path.indexOf("/jcr:content")));
                }
                search = new Asset();
                search.setRefId(path);
                search.setIsCachable(true);
                search.setType(AssetComponentType.RESOURCE);
                try {
                    search.setDescription(r.getValue("dc:description").getString());
                } catch (Exception e) {
                }
                try {
                    search.setTitle(r.getValue("dc:title").getString());
                } catch (Exception e) {
                }
                try {
                    search.setIsOutdoorRelated(r.getValue("dc:isOutdoorRelated").getBoolean());
                } catch (Exception e) {
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
        return search;
    }

    public java.util.List<Asset> getGlobalResources(String resourceTags) {
        java.util.List<Asset> toRet = new java.util.ArrayList();
        if (resourceTags == null || resourceTags.equals("")) {
            return toRet;
        }
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            java.util.Map<String, String> map = new java.util.HashMap<String, String>();
            map.put("group.p.or", "true");
            resourceTags += ";"; // if 1 tag no delim
            StringTokenizer t = new StringTokenizer(resourceTags, ";");
            int i = 1;
            while (t.hasMoreElements()) {
                map.put("group." + i + "_fulltext", t.nextToken());
                i++;
            }
            map.put("path", "/content/dam/girlscouts-vtk/global/resource");
            map.put("p.offset", "0"); // same as query.setStart(0) below
            map.put("p.limit", "100"); // same as query.setHitsPerPage(20) below
            com.day.cq.search.Query query = qBuilder.createQuery(PredicateGroup.create(map), session);
            query.setStart(0);
            query.setHitsPerPage(100);
            SearchResult result = query.getResult();
            for (Hit hit : result.getHits()) {
                try {
                    String path = hit.getPath();
                    if (!path.endsWith("metadata")) {
                        continue;
                    }
                    Asset asset = new Asset();
                    asset.setType(AssetComponentType.RESOURCE.toString());
                    asset.setTitle(new DocHit(hit).getTitle());
                    asset.setIsCachable(true);
                    asset.setRefId(new DocHit(hit).getURL());
                    toRet.add(asset);
                } catch (Exception e) {
                    e.printStackTrace();
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
        return toRet;
    }

    public Council getCouncil(User user, Troop troop, String councilId) throws IllegalAccessException {
        // TODO Permission.PERMISSION_VIEW_MEETING_ID
        Session session = null;
        Council council = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            List<Class> classes = new ArrayList<Class>();
            classes.add(Council.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            QueryManager queryManager = ocm.getQueryManager();
            Filter filter = queryManager.createFilter(Troop.class);
            council = (Council) ocm.getObject(VtkUtil.getYearPlanBase(user, null) + councilId);
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
        return council;
    }

    public java.util.List<Milestone> getCouncilMilestones(String councilCode) {
        String councilStr = councilMapper.getCouncilBranch(councilCode);
        councilStr = councilStr.replace("/content/", "");
        Session session = null;
        java.util.List<Milestone> milestones = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            List<Class> classes = new ArrayList<Class>();
            classes.add(Milestone.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            QueryManager queryManager = ocm.getQueryManager();
            Filter filter = queryManager.createFilter(Milestone.class);
            filter.setScope("/content/" + councilStr + "//");
            Query query = queryManager.createQuery(filter);
            milestones = (java.util.List<Milestone>) ocm.getObjects(query);
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
        return milestones;
    }

    public void saveCouncilMilestones(java.util.List<Milestone> milestones) {
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            List<Class> classes = new ArrayList<Class>();
            classes.add(Milestone.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            for (int i = 0; i < milestones.size(); i++) {
                ocm.update(milestones.get(i));
            }
            ocm.save();
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

    public java.util.List<Activity> searchA1(User user, Troop troop, String tags, String cat, String keywrd, java.util.Date startDate, java.util.Date endDate, String region) throws IllegalAccessException, IllegalStateException {
        java.util.List<Activity> toRet = new java.util.ArrayList();
        Session session = null;
        if (!userUtil.hasPermission(troop, Permission.PERMISSION_VIEW_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            String councilStr = councilMapper.getCouncilBranch(troop.getSfCouncil());
            if (councilStr == null || councilStr.trim().equals("")) {
                councilStr = "/content/gateway";
            }
            String councilId = null;
            if (troop != null) {
                councilId = troop.getCouncilCode();
            }
            String branch = councilStr; //councilMapper.getCouncilBranch(councilId);
            String namespace = branch.replace("/content/", "");
            branch += "/en";
            String eventPath = "";
            try {
                eventPath = session.getProperty(branch + "/jcr:content/eventPath").getString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Node homepage = session.getNode(branch + "/jcr:content");
                if (homepage != null) {
                    if (homepage.hasProperty("event-cart")) {
                        if ("true".equals(homepage.getProperty("event-cart").getString())) {
                            namespace = "sf-activities";
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            boolean isTag = false;
            String sqlTags = "";
            if (tags.equals("|")) {
                tags = "";
            }
            StringTokenizer t = new StringTokenizer(tags, "|");
            while (t.hasMoreElements()) {
                sqlTags += " s.[/jcr:content/cq:tags] like '%" + namespace + ":program-level/" + t.nextToken() + "%' ";
                if (t.hasMoreElements()) {
                    sqlTags += " or ";
                }
                isTag = true;
            }
            if (isTag) {
                sqlTags = " and (" + sqlTags + " ) ";
            }
            String sqlCat = "";
            if (cat.equals("|")) {
                cat = "";
            }
            t = new StringTokenizer(cat, "|");
            while (t.hasMoreElements()) {
                sqlCat += " s.[/jcr:content/cq:tags] like '%" + namespace + ":categories/" + t.nextToken() + "%' ";
                if (t.hasMoreElements()) {
                    sqlCat += " or ";
                }
                isTag = true;
            }
            if (!sqlCat.equals("")) {
                sqlCat = " and (" + sqlCat + " ) ";
            }
            String regionSql = "";
            if (region != null && !region.trim().equals("")) {
                regionSql += " and LOWER(/jcr:content/data/region) ='" + region + "'";
            }
            String path = councilStr + "/en/events/" + VtkUtil.getCurrentGSYear() + "/%";
            if (!isTag) {
                path = path + "/data";
            } else {
                path = path + "/jcr:content";
            }
            String sql = "select s.*  from [cq:Page] as s where  (isdescendantnode (s, [" + eventPath + "])) and s.[/jcr:content/data/start] is not null and s.[/jcr:content/jcr:title] is not null ";
            if (keywrd != null && !keywrd.trim().equals(""))// && !isTag )
            {
                sql += " and contains(s.*, '" + keywrd + "') ";
            }
            sql += regionSql;
            sql += sqlTags;
            sql += sqlCat;
            javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
            javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.JCR_SQL2);
            int i = 0;
            QueryResult result = q.execute();
            for (RowIterator it = result.getRows(); it.hasNext(); ) {
                Row r = it.nextRow();
                Node resultNode = r.getNode();
                Activity activity = new Activity();
                activity.setUid("A" + new java.util.Date().getTime() + "_" + Math.random());
                activity.setContent(resultNode.getProperty("jcr:content/data/details").getString());
                // convert to EST
                // TODO: All VTK date is based on server time zone, which is
                // eastern now.
                // Event dates in councils may be in a different time zone.
                // For a temp solution, always force it to eastern time.
                // e.g. For Texas, 2014-11-06T09:00:00.000-06:00 will be forced
                // to
                // 2014-11-06T09:00:00.000-05:00
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                try {
                    String eventStartDateStr = resultNode.getProperty("jcr:content/data/start").getString();
                    if (resultNode.getPath().contains("/sf-events-repository/")) {
                        String sfTimeZoneLabel = resultNode.hasProperty("jcr:content/data/timezone") ? resultNode.getProperty("jcr:content/data/timezone").getString() : "";
                        eventStartDateStr = VtkUtil.getSFActivityDate(eventStartDateStr, sfTimeZoneLabel);
                        if (eventStartDateStr == null) {
                            continue;
                        }

                    }
                    Date eventStartDate = dateFormat.parse(eventStartDateStr);
                    activity.setDate(eventStartDate);
                    String eventEndDateStr = resultNode.getProperty("jcr:content/data/end").getString();
                    if (resultNode.getPath().contains("/sf-events-repository/")) {
                        String sfTimeZoneLabel = resultNode.hasProperty("jcr:content/data/timezone") ? resultNode.getProperty("jcr:content/data/timezone").getString() : "";
                        eventEndDateStr = VtkUtil.getSFActivityDate(eventEndDateStr, sfTimeZoneLabel);
                    }
                    Date eventEndDate = dateFormat.parse(eventEndDateStr);
                    activity.setEndDate(eventEndDate);
                } catch (Exception e) {
                }
                // TODO: end of hacking timezone
                if ((activity.getDate().before(new java.util.Date()) && activity.getEndDate() == null) || (activity.getEndDate() != null && activity.getEndDate().before(new java.util.Date()))) {
                    continue;
                }
                try {
                    activity.setLocationName(resultNode.getProperty("jcr:content/data/locationLabel").getString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    activity.setLocationAddress(resultNode.getProperty("jcr:content/data/address").getString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                activity.setName(resultNode.getProperty("jcr:content/jcr:title").getString());
                activity.setType(YearPlanComponentType.ACTIVITY);
                activity.setId("ACT" + i);
                if (activity.getDate() != null && activity.getEndDate() == null) {
                    activity.setEndDate(activity.getDate());
                }
                activity.setIsEditable(false);
                try {
                    activity.setRefUid(resultNode.getProperty("jcr:content/jcr:uuid").getString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    activity.setRegisterUrl(resultNode.getProperty("jcr:content/data/register").getString());
                } catch (Exception e) {
                    log.error("searchActivity no register url");
                }
                if (startDate != null && endDate != null) {
                    startDate.setHours(0);
                    endDate.setHours(23);
                    if (activity.getDate() != null && (  //start date
                            activity.getDate().equals(endDate) || activity.getDate().before(endDate))) {
                    } else if (activity.getEndDate() != null && (  //end date
                            activity.getEndDate().equals(endDate) || activity.getEndDate().equals(startDate) || activity.getEndDate().before(endDate) && activity.getEndDate().after(startDate))) {
                    } else {
                        continue;
                    }

                }
                toRet.add(activity);
                i++;

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
        return toRet;
    }

    public void updateCustMeetingPlansRef(java.util.List<String> meetings, String path) {
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            for (int i = 0; i < meetings.size(); i++) {
                String meetingPath = meetings.get(i);
                Node x = session.getNode(meetingPath);
                String orgPath = x.getProperty("refId").getString();
                log.debug("orgPath : " + orgPath);
                String newPath = path + "" + orgPath.substring(orgPath.indexOf("/lib/"));
                log.debug("Changing cust meeting path from " + path + " to: " + newPath);
                x.setProperty("refId", newPath);
                session.save();

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

    public java.util.List<String> getCustMeetings(String path) {
        java.util.List<String> toRet = new java.util.ArrayList<String>();
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            String sql = "select * from nt:unstructured where jcr:path like '" + path + "/%' and ocm_classname ='org.girlscouts.vtk.models.MeetingE' and refId like '%/users/%_%' ";
            log.debug("SQL cust" + sql);
            javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
            javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL);
            QueryResult result = q.execute();
            for (RowIterator it = result.getRows(); it.hasNext(); ) {
                Row r = it.nextRow();
                Value excerpt = r.getValue("jcr:path");
                toRet.add(excerpt.getString());
                log.debug("Adding meeting: " + excerpt.getString());
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
        return toRet;
    }

    public String removeLocation(User user, Troop troop, String locationName) throws IllegalAccessException, IllegalStateException {
        Session session = null;
        String locationToRmPath = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID)) {
                throw new IllegalAccessException();
            }
            session = rr.adaptTo(Session.class);
            List<Class> classes = new ArrayList<Class>();
            classes.add(Troop.class);
            classes.add(Activity.class);
            classes.add(JcrCollectionHoldString.class);
            classes.add(YearPlan.class);
            classes.add(MeetingE.class);
            classes.add(Note.class);
            classes.add(Location.class);
            classes.add(Cal.class);
            classes.add(Milestone.class);
            classes.add(Asset.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            YearPlan plan = troop.getYearPlan();
            List<Location> locations = plan.getLocations();
            for (int i = 0; i < locations.size(); i++) {
                Location location = locations.get(i);
                if (location.getUid().equals(locationName)) {
                    ocm.remove(location);
                    ocm.save();
                    locationToRmPath = location.getPath();
                    locations.remove(location);
                    break;
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
        return locationToRmPath;
    }

    public Attendance getAttendance(User user, Troop troop, String mid) {
        Attendance attendance = null;
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            List<Class> classes = new ArrayList<Class>();
            classes.add(Attendance.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            attendance = (Attendance) ocm.getObject(mid);
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
        return attendance;
    }

    public boolean setAttendance(User user, Troop troop, String mid, Attendance attendance) {
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            List<Class> classes = new ArrayList<Class>();
            classes.add(Attendance.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            if (!session.itemExists(attendance.getPath())) {
                ocm.insert(attendance);
            }
            ocm.update(attendance);
            ocm.save();
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
        return false;
    }

    public Achievement getAchievement(User user, Troop troop, String mid) {
        Achievement attendance = null;
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            List<Class> classes = new ArrayList<Class>();
            classes.add(Achievement.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            QueryManager queryManager = ocm.getQueryManager();
            attendance = (Achievement) ocm.getObject(mid);
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
        return attendance;
    }

    public boolean setAchievement(User user, Troop troop, String mid, Achievement Achievement) {
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            List<Class> classes = new ArrayList<Class>();
            classes.add(Achievement.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            if (!session.itemExists(Achievement.getPath())) {
                ocm.insert(Achievement);
            }
            ocm.update(Achievement);
            ocm.save();
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
        return false;
    }

    public boolean updateMeetingEvent(User user, Troop troop, MeetingE meeting) throws IllegalAccessException, IllegalStateException {
        Session session = null;
        if (troop != null && !userUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            List<Class> classes = new ArrayList<Class>();
            classes.add(MeetingE.class);
            classes.add(Note.class);
            classes.add(JcrCollectionHoldString.class);
            classes.add(Asset.class);
            classes.add(SentEmail.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            ocm.update(meeting);
            ocm.save();
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
        return false;
    }

    public MeetingE getMeetingE(User user, Troop troop, String path) throws IllegalAccessException, VtkException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_VIEW_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        MeetingE meetingE = null;
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            List<Class> classes = new ArrayList<Class>();
            classes.add(Meeting.class);
            classes.add(Activity.class);
            classes.add(MeetingE.class);
            classes.add(Note.class);
            classes.add(Achievement.class);
            classes.add(Asset.class);
            classes.add(Attendance.class);
            classes.add(SentEmail.class);
            classes.add(JcrCollectionHoldString.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            meetingE = (MeetingE) ocm.getObject(path);

        } catch (org.apache.jackrabbit.ocm.exception.IncorrectPersistentClassException ec) {
            ec.printStackTrace();
            throw new VtkException("Could not complete intended action due to a server error. Code: " + new java.util.Date().getTime());

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
        return meetingE;
    }

    public int getAllResourcesCount(User user, Troop troop, String _path) throws IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_LOGIN_ID)) {
            throw new IllegalAccessException();
        }
        int count = 0;
        List<Asset> matched = new ArrayList<Asset>();
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            String sql = "select [dc:description], [dc:format], [dc:title], [jcr:mimeType], [jcr:path] " + " from [nt:unstructured] as parent where " + " (isdescendantnode (parent, [" + _path + "])) and [cq:tags] is not null";
            session = rr.adaptTo(Session.class);
            javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
            javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.JCR_SQL2);
            QueryResult result = q.execute();
            count = (int) result.getRows().getSize();
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
        return count;
    }

    public List<org.girlscouts.vtk.models.Search> getData(User user, Troop troop, String _query) throws IllegalAccessException {
        java.util.List data = null, data1 = null, data2 = null, data3 = null;
        try {
            data1 = getDataItem(user, troop, _query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            data2 = getDataItem(user, troop, _query, "/content/dam/girlscouts-vtk/global/aid");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            data3 = getDataItem(user, troop, _query, "/content/dam/girlscouts-vtk/global/resource");
        } catch (Exception e) {
            e.printStackTrace();
        }
        data = new java.util.ArrayList();
        if (data1 != null) {
            data.addAll(data1);
        }
        if (data2 != null) {
            data.addAll(data2);
        }
        if (data3 != null) {
            data.addAll(data3);
        }
        return data;

    }

    public List<org.girlscouts.vtk.models.Search> getDataItem(User user, Troop troop, String _query, String PATH) throws IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_LOGIN_ID)) {
            throw new IllegalAccessException();
        }
        Session session = null;
        List<org.girlscouts.vtk.models.Search> matched = null;
        final String RESOURCES_PATH = "resources";
        String councilId = null;
        if (troop != null) {
            councilId = troop.getCouncilCode();
        }
        String branch = councilMapper.getCouncilBranch(councilId);
        String resourceRootPath = branch + "/en/" + RESOURCES_PATH;
        if (PATH == null) {
            PATH = resourceRootPath;
        }
        matched = new ArrayList<org.girlscouts.vtk.models.Search>();
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            java.util.Map<String, String> map = new java.util.HashMap<String, String>();
            map.put("fulltext", _query);
            map.put("path", PATH);
            map.put("type", "dam:Asset");
            com.day.cq.search.Query query = qBuilder.createQuery(PredicateGroup.create(map), session);
            query.setExcerpt(true);
            java.util.Map<String, org.girlscouts.vtk.models.Search> unq = new java.util.TreeMap();
            SearchResult result = query.getResult();
            for (Hit hit : result.getHits()) {
                try {
                    String path = hit.getPath();
                    java.util.Map<String, String> exc = hit.getExcerpts();
                    java.util.Iterator itr = exc.keySet().iterator();
                    while (itr.hasNext()) {
                        String str = (String) itr.next();
                        String str1 = exc.get(str);
                    }
                    ValueMap vp = hit.getProperties();
                    itr = vp.keySet().iterator();
                    DocHit dh = new DocHit(hit);
                    org.girlscouts.vtk.models.Search search = new org.girlscouts.vtk.models.Search();
                    search.setPath(dh.getURL());
                    search.setDesc(dh.getTitle());
                    search.setContent(dh.getExcerpt());
                    search.setSubTitle(dh.getDescription());
                    search.setAssetType(AssetComponentType.RESOURCE);
                    if (search.getPath().toLowerCase().contains("/aid/")) {
                        search.setAssetType(AssetComponentType.AID);
                    }
                    if (unq.containsKey(search.getPath())) {
                        if (search.getContent() != null && !search.getContent().trim().equals("")) {
                            org.girlscouts.vtk.models.Search _search = unq.get(search.getPath());
                            if (_search.getContent() == null || _search.getContent().trim().equals("")) {
                                unq.put(search.getPath(), search);
                            }

                        }

                    } else {
                        unq.put(search.getPath(), search);
                    }

                } catch (RepositoryException e) {
                    e.printStackTrace();

                }

            }
            java.util.Iterator itr = unq.keySet().iterator();
            while (itr.hasNext()) {
                matched.add(unq.get(itr.next()));
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

    public int getAssetCount(User user, Troop troop, String path) throws IllegalAccessException {
        int count = 0;
        if (path == null || "".equals(path)) {
            return 0;
        }
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            String sql = "select [jcr:path] " + " from [dam:Asset] as s   where " + " (isdescendantnode (s, [" + path + "]))";
            session = rr.adaptTo(Session.class);
            javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
            javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.JCR_SQL2);
            QueryResult result = q.execute();
            NodeIterator itr = result.getNodes();
            while (itr.hasNext()) {
                itr.next();
                count++;
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
        return count;
    }

    public int getCountLocalMeetingAidsByLevel(User user, Troop troop, String _path) throws IllegalAccessException {
        int count = 0;
        if (troop == null || troop.getGradeLevel() == null) {
            return 0;
        }
        String level = troop.getGradeLevel().toLowerCase();
        if (level.contains("-")) {
            level = level.split("-")[1];
        }
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            String sql = "select [dc:description], [dc:format], [dc:title], [jcr:mimeType], [jcr:path] " + " from [nt:unstructured] as parent where " + " (isdescendantnode (parent, [" + _path + "])) and [cq:tags] is not null";
            session = rr.adaptTo(Session.class);
            javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
            javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.JCR_SQL2);
            QueryResult result = q.execute();
            NodeIterator itr = result.getNodes();
            while (itr.hasNext()) {
                Node node = (Node) itr.next();
                if (node.getPath().toLowerCase().contains(("meetings/" + level.charAt(0)).toLowerCase())) {
                    count++;
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
        return count;
    }

    public java.util.Collection<bean_resource> getResourceData(User user, Troop troop, String _path) throws IllegalAccessException {
        int count = 0;
        if (resourceCountMap.containsKey(_path)) {
            return (java.util.Collection<bean_resource>) resourceCountMap.get(_path);
        }
        java.util.Map<String, bean_resource> dictionary = null;
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            String sql = "SELECT [jcr:path], [jcr:title] FROM [cq:PageContent] AS s WHERE ISDESCENDANTNODE(s, [" + _path + "])";
            session = rr.adaptTo(Session.class);
            javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
            javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.JCR_SQL2);
            java.util.Map<String, String> categoryDictionary = new java.util.TreeMap<String, String>();
            java.util.Map<String, java.util.List<String>> container = new java.util.TreeMap();
            dictionary = new java.util.TreeMap<String, bean_resource>();
            QueryResult result = q.execute();
            NodeIterator itr = result.getNodes();
            while (itr.hasNext()) {
                Node node = (Node) itr.next();
                String path = node.getPath();
                String pathUri = path.replace(_path, "");
                String[] nodes = pathUri.split("/");
                if (nodes.length == 4) {
                    bean_resource beanResource = new bean_resource();
                    beanResource.setPath(path.replace("/jcr:content", ""));
                    beanResource.setTitle(node.getProperty("jcr:title").getString());
                    beanResource.setNodeUri(nodes[2]);
                    beanResource.setCategory(nodes[1]);
                    dictionary.put(nodes[1] + "|" + nodes[2], beanResource);

                }
                if (nodes.length <= 2 || nodes[2].equals("jcr:content")) {
                    categoryDictionary.put(nodes[1], node.getProperty("jcr:title").getString());
                    continue;
                }
                java.util.List list = container.get(nodes[1] + "|" + nodes[2]);
                if (list == null) {
                    list = new java.util.ArrayList<String>();
                }
                if (nodes.length > 3 && !nodes[3].equals("jcr:content")) {
                    list.add(nodes[3]);
                }
                container.put(nodes[1] + "|" + nodes[2], list);

            }
            java.util.Iterator _itr = container.keySet().iterator();
            while (_itr.hasNext()) {
                String title = (String) _itr.next();
                java.util.List<String> links = container.get(title);
                bean_resource resource = dictionary.get(title);
                resource.setItemCount(links.size());
                resource.setCategoryDisplay(categoryDictionary.get(resource.getCategory()));

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
        resourceCountMap.put(_path, dictionary.values());
        return dictionary.values();
    }

    public int getMeetingCount(User user, Troop troop, String path) throws IllegalAccessException {
        int count = 0;
        if (path == null || "".equals(path)) {
            return 0;
        }
        if (resourceCountMap.containsKey(path)) {
            return ((Integer) resourceCountMap.get(path)).intValue();
        }
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            String sql = "select * from nt:base where isdescendantnode( '" + path + "%' ) and ocm_classname='org.girlscouts.vtk.models.Meeting'";
            session = rr.adaptTo(Session.class);
            javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
            javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL);
            QueryResult result = q.execute();
            //count = (int) result.getNodes().getSize();
            Iterator itr = result.getNodes();
            while (itr.hasNext()) {
                itr.next();
                count++;
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
        resourceCountMap.put(path, count);
        return count;
    }

    public int getVtkAssetCount(User user, Troop troop, String path) throws IllegalAccessException {
        if (resourceCountMap.containsKey(path)) {
            return ((Long) resourceCountMap.get(path)).intValue();
        }
        long count = 0;
        if (path == null || "".equals(path)) {
            return 0;
        }
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            String sql = "select [jcr:path]  from [nt:unstructured] as s   where  (isdescendantnode (s, [" + path + "])) and [cq:tags] is not null";
            session = rr.adaptTo(Session.class);
            javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
            javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.JCR_SQL2);
            QueryResult result = q.execute();
            count = result.getNodes().getSize();
            resourceCountMap.put(path, count);
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
        return (int) count;
    }

    public java.util.List<Meeting> getAllMeetings(User user, Troop troop) throws IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_VIEW_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        java.util.List<Meeting> meetings = null;
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            List<Class> classes = new ArrayList<Class>();
            classes.add(Meeting.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            String xmlDescriptor = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<!DOCTYPE jackrabbit-ocm PUBLIC \"-//The Apache Software Foundation//DTD Jackrabbit OCM 1.5//EN\" " + "\"http://jackrabbit.apache.org/dtd/jackrabbit-ocm-1.5.dtd\">" + "<jackrabbit-ocm>" + "<class-descriptor className=\"org.girlscouts.vtk.models.Meeting\" jcrType=\"nt:unstructured\">" + "<field-descriptor fieldName=\"path\" path=\"true\" />" + "<field-descriptor fieldName=\"id\" jcrName=\"id\" />" + "<field-descriptor fieldName=\"level\" jcrName=\"level\"/>" + "<field-descriptor fieldName=\"position\" jcrName=\"position\"/>" + "<field-descriptor fieldName=\"name\" jcrName=\"name\"/>" + "<field-descriptor fieldName=\"blurb\" jcrName=\"blurb\"/>" + "<field-descriptor fieldName=\"cat\" jcrName=\"cat\"/>" + "<field-descriptor fieldName=\"catTags\" jcrName=\"catTags\"/>" + "<field-descriptor fieldName=\"catTagsAlt\" jcrName=\"catTagsAlt\"/>" + "<field-descriptor fieldName=\"meetingPlanTypeAlt\" jcrName=\"meetingPlanTypeAlt\"/>" + "<field-descriptor fieldName=\"meetingPlanType\" jcrName=\"meetingPlanType\"/>" + "<field-descriptor fieldName=\"req\" jcrName=\"req\"/>" + "<field-descriptor fieldName=\"reqTitle\" jcrName=\"reqTitle\"/>" + "</class-descriptor></jackrabbit-ocm>";
            InputStream[] in = new InputStream[1];
            in[0] = IOUtils.toInputStream(xmlDescriptor, "UTF-8");
            session = rr.adaptTo(Session.class);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, in);//(session,mapper);
            QueryManager queryManager = ocm.getQueryManager();
            Field field = new Meeting().getClass().getDeclaredField("activities");
            org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection anno = field.getAnnotation(org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection.class);
            Filter filter = queryManager.createFilter(Meeting.class);
            filter.setScope("/content/girlscouts-vtk/meetings/myyearplan" + VtkUtil.getCurrentGSYear() + "//");
            Query query = queryManager.createQuery(filter);
            meetings = (List<Meeting>) ocm.getObjects(query);
            Comparator<Meeting> comp = new BeanComparator("position");
            if (meetings != null) {
                Collections.sort(meetings, comp);
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
        return meetings;

    }

    //changed to SQL . problem could be with performance. A. cant use path in the filter. Path contains nodes starting with numeric value ex: council 999. Solution need to impl new indexes. Could be problem. Temp solution impl sql see getNotes
    public java.util.List<Note> getNotes_OCM(User user, Troop troop, String path) throws IllegalAccessException, VtkException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_CREATE_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        java.util.List<Note> notes = null;
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            List<Class> classes = new ArrayList<Class>();
            classes.add(MeetingE.class);
            classes.add(Note.class);
            classes.add(Achievement.class);
            classes.add(Attendance.class);
            session = rr.adaptTo(Session.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            QueryManager queryManager = ocm.getQueryManager();
            Filter filter = queryManager.createFilter(Note.class);
            filter.setScope(VtkUtil.getYearPlanBase(user, troop) + "/");
            filter.addEqualTo("refId", path);
            Query query = queryManager.createQuery(filter);
            notes = (List<Note>) ocm.getObjects(query);
            //sort
            java.util.Comparator<Note> comp = new org.apache.commons.beanutils.BeanComparator("createTime");
            Collections.sort(notes, comp);
            Collections.reverse(notes);
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
        return notes;
    }

    public boolean updateNote(User user, Troop troop, Note note) throws IllegalAccessException {
        boolean isRm = false;
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_CREATE_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        if (!user.getApiConfig().getUser().getSfUserId().equals(note.getCreatedByUserId())) {
            throw new IllegalAccessException();
        }
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            List<Class> classes = new ArrayList<Class>();
            classes.add(MeetingE.class);
            classes.add(Note.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            if (!session.itemExists(note.getPath())) {
                ocm.insert(note); // y ??
            } else {
                ocm.update(note);
            }
            ocm.save();
            isRm = true;
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
        return isRm;

    }

    public boolean rmNote(User user, Troop troop, Note note) throws IllegalAccessException {
        boolean isRm = false;
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_CREATE_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            List<Class> classes = new ArrayList<Class>();
            classes.add(MeetingE.class);
            classes.add(Note.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            if (user.getApiConfig().getUser().getSfUserId().equals(note.getCreatedByUserId())) {//session.itemExists(note.getPath())){
                ocm.remove(note);
                ocm.save();
                isRm = true;
            } else {
                throw new IllegalAccessException();
            }
        } catch (IllegalAccessException el) {
            throw el;
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
        return isRm;

    }

    public boolean rmNote(User user, Troop troop, String noteId) throws IllegalAccessException {
        boolean isRm = false;
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_CREATE_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        Note note = getNote(user, troop, noteId);
        if (note != null) {
            //check if note belongs to logged-in user
            if (user.getApiConfig().getUser().getSfUserId().equals(note.getCreatedByUserId())) {
                rmNote(user, troop, note);
                isRm = true;
            } else {
                throw new IllegalAccessException();
            }
        }
        return isRm;
    }

    // method to get meeting note. Issue with using jcr:path. path starts with numeric such as /council 999, which creates conflict.
    //Solution: need to create indexes. for now temp create method using SQL
    public Note getNote_OCM(User user, Troop troop, String nid) throws IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_CREATE_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        Note note = null;
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            List<Class> classes = new ArrayList<Class>();
            classes.add(MeetingE.class);
            classes.add(Note.class);
            classes.add(Achievement.class);
            classes.add(Attendance.class);
            session = rr.adaptTo(Session.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            QueryManager queryManager = ocm.getQueryManager();
            Filter filter = queryManager.createFilter(Note.class);
            filter.setScope(VtkUtil.getYearPlanBase(user, troop) + "/");
            filter.addEqualTo("uid", nid);
            Query query = queryManager.createQuery(filter);
            note = (Note) ocm.getObject(query);
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
        return note;
    }

    //see comments from method getNote_OCM
    public Note getNote(User user, Troop troop, String nid) throws IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_CREATE_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        Note note = null;
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
            String sql = "select note.message, note.createTime, note.createdByUserId, note.createdByUserName, note.refId, note.uid from [nt:base] as note where ocm_classname='org.girlscouts.vtk.models.Note' and   ISDESCENDANTNODE([" + troop.getYearPlan().getPath() + "]) and note.[uid] = '" + nid + "'";
            javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL);
            QueryResult result = q.execute();
            String[] str = result.getColumnNames();
            for (RowIterator it = result.getRows(); it.hasNext(); ) {
                Row r = it.nextRow();
                note = new Note();
                Value excerpt = r.getValue("jcr:path");
                String path = excerpt.getString();
                note.setPath(path);
                String msg = r.getValue("note.message").getString();
                note.setMessage(msg);
                note.setUid(r.getValue("note.uid").getString());
                note.setCreateTime(r.getValue("note.createTime").getLong());
                note.setCreatedByUserName(r.getValue("note.createdByUserName").getString());
                note.setCreatedByUserId(r.getValue("note.createdByUserId").getString());
                note.setRefId(r.getValue("note.refId").getString());
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
        return note;
    }

    //see comments from method getNotes_OCM
    public java.util.List<Note> getNotes(User user, Troop troop, String refId) throws IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_CREATE_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        java.util.List<Note> notes = new java.util.ArrayList<Note>();
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            Resource meetingRes = rr.resolve(troop.getYearPlan().getPath() + "/meetingEvents/" + refId);
            if (meetingRes != null) {
                Resource notesResource = meetingRes.getChild("notes");
                if (notesResource != null) {
                    Iterator<Resource> notesIt = notesResource.listChildren();
                    while (notesIt.hasNext()) {
                        Resource noteItr = notesIt.next();
                        Note note = new Note();
                        ValueMap values = noteItr.getValueMap();
                        note.setPath(noteItr.getPath());
                        note.setMessage(values.get("message", String.class));
                        note.setUid(values.get("uid", String.class));
                        note.setCreateTime(values.get("createTime", Long.class));
                        note.setCreatedByUserName(values.get("createdByUserName", String.class));
                        note.setCreatedByUserId(values.get("createdByUserId", String.class));
                        note.setRefId(values.get("refId", String.class));
                        notes.add(note);
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
        return notes;
    }

    //get all meetings with at least 1 outdoor agenda
    public Set<String> getOutdoorMeetings(User user, Troop troop) throws IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_LOGIN_ID)) {
            throw new IllegalAccessException();
        }
        Set<String> outdoorMeetings = new java.util.HashSet();
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            String sql = "select * from nt:unstructured where isdescendantnode('/content/girlscouts-vtk/meetings/myyearplan" + VtkUtil.getCurrentGSYear() + "') and outdoor=true and ocm_classname='org.girlscouts.vtk.models.Activity'";
            javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
            javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL);
            QueryResult result = q.execute();
            for (RowIterator it = result.getRows(); it.hasNext(); ) {
                Row r = it.nextRow();
                String path = r.getPath();
                String[] pathElements = path.split("/");
                if (pathElements != null && pathElements.length > 5) {
                    outdoorMeetings.add(pathElements[6]);
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
        return outdoorMeetings;
    }

    //get all meetings with at least 1 global agenda
    public Set<String> getGlobalMeetings(User user, Troop troop) throws IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_LOGIN_ID)) {
            throw new IllegalAccessException();
        }
        Set<String> globalMeetings = new java.util.HashSet();
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            String sql = "select * from nt:unstructured where isdescendantnode('/content/girlscouts-vtk/meetings/myyearplan" + VtkUtil.getCurrentGSYear() + "') and global=true and ocm_classname='org.girlscouts.vtk.models.Activity'";
            javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
            javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL);
            QueryResult result = q.execute();
            for (RowIterator it = result.getRows(); it.hasNext(); ) {
                Row r = it.nextRow();
                String path = r.getPath();
                String[] pathElements = path.split("/");
                if (pathElements != null && pathElements.length > 5) {
                    globalMeetings.add(pathElements[6]);
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
        return globalMeetings;
    }

    public List<Meeting> getMeetings(User user, Troop troop, String level) throws IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_LOGIN_ID)) {
            throw new IllegalAccessException();
        }
        Session session = null;
        List<Meeting> meetings = new ArrayList();
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            String sql = "select catTags, blurb, id, level, name, meetingPlanType, req, reqTitle from nt:unstructured where isdescendantnode('/content/girlscouts-vtk/meetings/myyearplan" + VtkUtil.getCurrentGSYear() + "')  and ocm_classname='org.girlscouts.vtk.models.Meeting' and level='" + level + "'";
            javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
            javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL);
            QueryResult result = q.execute();
            rows:
            for (RowIterator it = result.getRows(); it.hasNext(); ) {
                Row r = it.nextRow();
                String path = r.getPath();
                Meeting meeting = new Meeting();
                meeting.setBlurb(r.getValue("blurb").getString());
                meeting.setPath(path);
                meeting.setId(r.getValue("id").getString());
                meeting.setLevel(r.getValue("level").getString());
                meeting.setName(r.getValue("name").getString());
                meeting.setReq(r.getValue("req") == null ? null : r.getValue("req").getString());
                meeting.setReqTitle(r.getValue("reqTitle") == null ? null : r.getValue("reqTitle").getString());
                try {
                    meeting.setCatTags(r.getValue("catTags").getString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    meeting.setMeetingPlanType(r.getValue("meetingPlanType").getString());
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Missing prop meetingPlanType in : " + path);
                    continue rows;
                }
                meetings.add(meeting);
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
        return meetings;
    }

    public boolean removeAttendance(User user, Troop troop, Attendance attendance) {
        Session session = null;
        boolean isRm = false;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            List<Class> classes = new ArrayList<Class>();
            classes.add(Attendance.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            ocm.remove(attendance);
            ocm.save();
            isRm = true;
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
        return isRm;
    }

    public boolean removeAchievement(User user, Troop troop, Achievement achievement) {
        Session session = null;
        boolean isRm = false;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            List<Class> classes = new ArrayList<Class>();
            classes.add(Achievement.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            ocm.remove(achievement);
            ocm.save();
            isRm = true;
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
        return isRm;
    }

    public java.util.List<Meeting> getAllMeetings() throws IllegalAccessException {
        java.util.List<Meeting> meetings = null;
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            List<Class> classes = new ArrayList<Class>();
            classes.add(Meeting.class);
            classes.add(Activity.class);
            classes.add(Attendance.class);
            classes.add(JcrCollectionHoldString.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            QueryManager queryManager = ocm.getQueryManager();
            Filter filter = queryManager.createFilter(Meeting.class);
            filter.setScope("/content/girlscouts-vtk/meetings/myyearplan" + VtkUtil.getCurrentGSYear() + "//");
            Query query = queryManager.createQuery(filter);
            meetings = (List<Meeting>) ocm.getObjects(query);
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
        return meetings;

    }

}// edn class
