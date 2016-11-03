package org.girlscouts.vtk.ejb;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import org.apache.commons.beanutils.BeanComparator;
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
import org.apache.sling.api.resource.ValueMap;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.dao.AssetComponentType;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.dao.YearPlanComponentType;
import org.girlscouts.vtk.models.Achievement;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.models.Attendance;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.Council;
import org.girlscouts.vtk.models.JcrCollectionHoldString;
import org.girlscouts.vtk.models.JcrNode;
import org.girlscouts.vtk.models.Location;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.Meeting2;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.models.Note;
import org.girlscouts.vtk.models.SearchTag;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.SentEmail;
import org.girlscouts.vtk.models.bean_resource;
import org.girlscouts.vtk.utils.VtkException;
import org.girlscouts.vtk.utils.VtkUtil;
import org.girlscouts.web.search.DocHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

@Component
@Service(value = MeetingDAO.class)
public class MeetingDAOImpl implements MeetingDAO {
	private final Logger log = LoggerFactory.getLogger("vtk");
	
	//public static Map<String,Long> resourceCountMap = new HashMap<String,Long>();
	public static Map resourceCountMap = new HashMap();
	public static final String RESOURCE_COUNT_MAP_AGE = "RESOURCE_COUNT_MAP_AGE";
	public static final long MAX_CACHE_AGE_MS = 3600000; // 1 hour in ms
//	public static final long MAX_CACHE_AGE_MS = 60000; // 1 minute in ms

	@Reference
	private SessionFactory sessionFactory;

	@Reference
	private QueryBuilder qBuilder;

	@Reference
	org.girlscouts.vtk.helpers.CouncilMapper councilMapper;

	@Reference
	UserUtil userUtil;

	@Activate
	void activate() {
		resourceCountMap.put(RESOURCE_COUNT_MAP_AGE, System.currentTimeMillis());
	}

	// by planId
	public java.util.List<MeetingE> getAllEventMeetings(User user, Troop troop,
			String yearPlanId) throws IllegalAccessException {
		if (user != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_VIEW_MEETING_ID))
			throw new IllegalAccessException();
		java.util.List<MeetingE> meetings = null;
		Session session = null;
		try {
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(MeetingE.class);classes.add(Note.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(MeetingE.class);
			filter.setScope("/content/girlscouts-vtk/yearPlanTemplates/yearplan"
					+ user.getCurrentYear()
					+ "/brownie/yearPlan"
					+ yearPlanId
					+ "/meetings/");
			Query query = queryManager.createQuery(filter);
			meetings = (List<MeetingE>) ocm.getObjects(query);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return meetings;
	}

	// by plan path
	public java.util.List<MeetingE> getAllEventMeetings_byPath(User user,
			Troop troop, String yearPlanPath) throws IllegalAccessException {

		if (user != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_VIEW_MEETING_ID))
			throw new IllegalAccessException();

		java.util.List<MeetingE> meetings = null;
		Session session = null;
		try {
			List<Class> classes = new ArrayList<Class>();
			classes.add(MeetingE.class);classes.add(Note.class);
			classes.add(Achievement.class);
			classes.add(Attendance.class);
			session = sessionFactory.getSession();
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(MeetingE.class);
			filter.setScope(yearPlanPath);
			Query query = queryManager.createQuery(filter);
			meetings = (List<MeetingE>) ocm.getObjects(query);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return meetings;
	}

	public Meeting getMeeting(User user, Troop troop, String path)
			throws IllegalAccessException, VtkException {

		if (user != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_VIEW_MEETING_ID))
			throw new IllegalAccessException();

		Meeting meeting = null;
		Session session = null;
		try {
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Meeting.class);
			classes.add(Activity.class);
			classes.add(JcrCollectionHoldString.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
		
			meeting = (Meeting) ocm.getObject(path);
	

			if (meeting != null && path != null
					&& path.contains("/lib/meetings/")) {

				Meeting globalMeetingInfo = getMeeting(
						user,
						troop,
						"/content/girlscouts-vtk/meetings/myyearplan"
								+ VtkUtil.getCurrentGSYear() + "/"
								+ meeting.getLevel().toLowerCase().trim() + "/"
								+ meeting.getId());

				if (globalMeetingInfo != null) {
					meeting.setMeetingInfo(globalMeetingInfo.getMeetingInfo());
					meeting.setIsAchievement(globalMeetingInfo
							.getIsAchievement());
				}
	
	
			try{	
				//check agenda chn to outdoor
				for(int i=0;i< globalMeetingInfo.getActivities().size();i++){
					Activity gActivity = globalMeetingInfo.getActivities().get(i);

					if( gActivity.getIsOutdoorAvailable() )
					 for(int y=0;y<meeting.getActivities().size();y++){
						Activity activity = meeting.getActivities().get(y);

						if( !activity.getIsOutdoorAvailable() && activity.getName().equals(gActivity.getName()) ){
			
							activity.setIsOutdoorAvailable(true);
							activity.setActivityDescription_outdoor( gActivity.getActivityDescription_outdoor() );
						}
					}//edn y
				}//edn i
			}catch(Exception e){e.printStackTrace();}
			}//edn if
		
		} catch (org.apache.jackrabbit.ocm.exception.IncorrectPersistentClassException ec) {
			ec.printStackTrace();
			throw new VtkException(
					"Could not complete intended action due to a server error. Code: "
							+ new java.util.Date().getTime());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return meeting;
	}

	// get all event meetings for users plan
	public java.util.List<MeetingE> getAllUsersEventMeetings(User user,
			Troop troop, String yearPlanId) throws IllegalStateException,
			IllegalAccessException {
		if (user != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_VIEW_MEETING_ID))
			throw new IllegalAccessException();
		Session session = null;
		java.util.List<MeetingE> meetings = null;

		try {
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(MeetingE.class);classes.add(Note.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(MeetingE.class);
			filter.setScope("/content/girlscouts-vtk/users/" + troop.getId()
					+ "/yearPlan/meetingEvents/");
			Query query = queryManager.createQuery(filter);
			meetings = (List<MeetingE>) ocm.getObjects(query);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return meetings;
	}

	// get all event meetings for users plan
	public Meeting createCustomMeeting(User user, Troop troop,
			MeetingE meetingEvent) throws IllegalAccessException,
			IllegalStateException {
		return createCustomMeeting(user, troop, meetingEvent, null);
	}

	public Meeting createCustomMeeting(User user, Troop troop,
			MeetingE meetingEvent, Meeting meeting)
			throws IllegalAccessException {
		Session session = null;
		try {

			if (user != null
					&& !userUtil.hasPermission(troop,
							Permission.PERMISSION_CREATE_MEETING_ID))
				throw new IllegalAccessException();

			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(MeetingE.class);classes.add(Note.class);
			classes.add(Meeting.class);
			classes.add(Activity.class);
			classes.add(JcrCollectionHoldString.class);
			classes.add(JcrNode.class);
			classes.add(Asset.class);
			classes.add(SentEmail.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
			if (meeting == null)
				meeting = getMeeting(user, troop, meetingEvent.getRefId());
			String newPath = troop.getPath() + "/lib/meetings/"
					+ meeting.getId() + "_" + Math.random();
			if (!session.itemExists(troop.getPath() + "/lib/meetings/")) {
				ocm.insert(new JcrNode(troop.getPath() + "/lib"));
				ocm.insert(new JcrNode(troop.getPath() + "/lib/meetings"));
				ocm.save();
			}
			meetingEvent.setRefId(newPath);
			meeting.setPath(newPath);
			ocm.insert(meeting);
			ocm.update(meetingEvent);
			ocm.save();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return meeting;

	}

	public Meeting updateCustomMeeting(User user, Troop troop,
			MeetingE meetingEvent, Meeting meeting)
			throws IllegalAccessException, IllegalStateException {
		Session session = null;
		try {

			if (user != null
					&& !userUtil.hasPermission(troop,
							Permission.PERMISSION_EDIT_MEETING_ID))
				throw new IllegalAccessException();

			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(MeetingE.class);classes.add(Note.class);
			classes.add(Meeting.class);
			classes.add(Activity.class);
			classes.add(JcrCollectionHoldString.class);
			classes.add(JcrNode.class);
			classes.add(Asset.class);
			classes.add(SentEmail.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);

			if (meeting == null)
				meeting = getMeeting(user, troop, meetingEvent.getRefId());

			String newPath = meetingEvent.getRefId();

			if (!session.itemExists(troop.getPath() + "/lib/meetings/")) {
				ocm.insert(new JcrNode(troop.getPath() + "/lib"));
				ocm.insert(new JcrNode(troop.getPath() + "/lib/meetings"));
				ocm.save();
			}
			meetingEvent.setRefId(newPath);
			meeting.setPath(newPath);
			ocm.update(meeting);
			ocm.update(meetingEvent);
			ocm.save();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return meeting;

	}

	public Meeting addActivity(User user, Troop troop, Meeting meeting,
			Activity activity) throws IllegalStateException,
			IllegalAccessException {

		if (user != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_ADD_ACTIVITY_ID))
			throw new IllegalAccessException();

		java.util.List<Activity> activities = meeting.getActivities();
		activities.add(activity);
		meeting.setActivities(activities);
		Session session = null;

		try {
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(MeetingE.class);classes.add(Note.class);
			classes.add(Meeting.class);
			classes.add(Activity.class);
			classes.add(JcrCollectionHoldString.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
			ocm.update(meeting);
			ocm.save();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return meeting;

	}

	public List<Meeting> search() {
		Session session = null;
		List<Meeting> meetings = null;
		try {
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Meeting.class);
			classes.add(Activity.class);
			classes.add(JcrCollectionHoldString.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(Meeting.class);
			filter.setScope("/content/girlscouts-vtk/meetings/myyearplan"
					+ VtkUtil.getCurrentGSYear() + "/brownie/");
			Query query = queryManager.createQuery(filter);
			meetings = (List<Meeting>) ocm.getObjects(query);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return meetings;

	}

	public List<org.girlscouts.vtk.models.Search> getDataSQL2(String query) {

		List<org.girlscouts.vtk.models.Search> matched = new ArrayList<org.girlscouts.vtk.models.Search>();
		Session session = null;
		try {
			session = sessionFactory.getSession();
			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();

			String sql = "select parent.*, child.* , child.[dc:title], child.[dc:description] , "
					+ "	child.[dc:format], parent.name, parent.[jcr:path], child.name, child.[jcr:path], parent.[jcr:uuid],"
					+ "  child3.*, child3.[jcr:uuid], child3.[jcr:mimeType], child3.[excerpt(.)] "
					+ " from [nt:base] as parent "
					+ " INNER JOIN [nt:base] as child on ISCHILDNODE(child, parent) "
					+ " INNER JOIN [nt:resource] as child3 on ISDESCENDANTNODE(child3, parent)  "
					+ " where "
					+ "(isdescendantnode (parent, [/content/dam/girlscouts-vtk/global/aid]))  "
					+

					" and child.[dc:title] is not null "
					+ " and (  contains(child3.*, '"
					+ query
					+ "~' )) "
					+ " and child3.[jcr:uuid] is not null ";

			// AID search
			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.JCR_SQL2);
			QueryResult result = q.execute();
			String str[] = result.getColumnNames();
			for (RowIterator it = result.getRows(); it.hasNext();) {
				Row r = it.nextRow();
				org.girlscouts.vtk.models.Search search = new org.girlscouts.vtk.models.Search();
				search.setPath("test");
				search.setDesc(r.getValue("child.dc:title").getString());
				search.setType(r.getValue("child.dc:format").getString());
				search.setContent(r.getValue("child.dc:description")
						.getString());
				search.setType(r.getValue("child3.jcr:mimeType").getString());
				matched.add(search);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return matched;
	}

	public List<Asset> getAidTag(User user, Troop troop, String tags,
			String meetingName) throws IllegalAccessException {

		if (user != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_LOGIN_ID))
			throw new IllegalAccessException();

		List<Asset> matched = new ArrayList<Asset>();



		Session session = null;
		try {
			session = sessionFactory.getSession();
			if (tags == null || tags.trim().equals(""))
				return matched;

			String sql_tag = "";
			java.util.StringTokenizer t = new java.util.StringTokenizer(tags,
					";");
			while (t.hasMoreElements()) {
				String tag = t.nextToken();
				sql_tag += "cq:tags like '%" + tag + "%'";
				if (t.hasMoreElements())
					sql_tag += " or ";
			}

			String sql = "";
			sql = "select dc:description,dc:format, dc:title from nt:unstructured where jcr:path like '/content/dam/girlscouts-vtk/global/aid/%'";
			if (!sql_tag.equals(""))
				sql += " and ( " + sql_tag + " )";

			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();
			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.SQL);
			QueryResult result = q.execute();

			for (RowIterator it = result.getRows(); it.hasNext();) {
				Row r = it.nextRow();
				Value excerpt = r.getValue("jcr:path");
				String path = excerpt.getString();
				if (path.contains("/jcr:content"))
					path = path.substring(0, (path.indexOf("/jcr:content")));

				Asset search = new Asset();
				search.setRefId(path);
				search.setType(AssetComponentType.AID);
				search.setIsCachable(true);
				try {
					search.setDescription(r.getValue("dc:description")
							.getString());
				} catch (Exception e) {
					log.error("Global Aid Description missing");
				}
				try {
					search.setTitle(r.getValue("dc:title").getString());
				} catch (Exception e) {
				}
				matched.add(search);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return matched;
	}

	public List<Asset> getAidTag_local(User user, Troop troop, String tags,
			String meetingName, String meetingPath)
			throws IllegalAccessException {

		if (user != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_LOGIN_ID))
			throw new IllegalAccessException();

		return getLocalAssets(meetingName, meetingPath, AssetComponentType.AID);
	}

	private static final String AID_PATHS_PROP = "aidPaths";
	private static final String RESOURCE_PATHS_PROP = "resourcePaths";

	private List<Asset> getLocalAssets(String meetingName, String meetingPath,
			AssetComponentType type) {
		List<Asset> assets = new ArrayList<Asset>();

		Session session = null;
		try {
			session = sessionFactory.getSession();

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
				Value[] assetPaths = meetingNode.getProperty(pathProp)
						.getValues();
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
			String rootPath = getSchoolYearDamPath() + "/local/" + typeString
					+ "/meetings/" + meetingName;

			if (session.nodeExists(rootPath)) {
				assets.addAll(getAssetsFromPath(rootPath, type, session));
			}
		} catch (Exception e) {
			log.error("Error getting local assets for meeting: " + meetingName
					+ ". Root cause was: " + e.getMessage());
		} finally {
			try {
				if (session != null) {
					sessionFactory.closeSession(session);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return assets;
	}

	private List<Asset> getAidTag_custasset(User user, Troop troop, String uid)
			throws IllegalAccessException {

		if (user != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_LOGIN_ID))
			throw new IllegalAccessException();

		List<Asset> matched = new ArrayList<Asset>();
		Session session = null;
		try {
			session = sessionFactory.getSession();
			String sql = "select jcr:path from nt:base where jcr:path like '/vtk/111/troop-1a/assets/%' and refId='"
					+ uid + "'";
			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();
			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.SQL);
			QueryResult result = q.execute();
			for (RowIterator it = result.getRows(); it.hasNext();) {
				Row r = it.nextRow();
				Value excerpt = r.getValue("jcr:path");
				String path = excerpt.getString() + "/custasset";
				Asset search = new Asset();
				search.setRefId(path);
				search.setIsCachable(true);
				matched.add(search);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return matched;
	}

	public List<Asset> getResource_global(User user, Troop troop, String tags,
			String meetingName) throws IllegalAccessException {

		if (user != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_LOGIN_ID))
			throw new IllegalAccessException();

		List<Asset> matched = new ArrayList<Asset>();
		Session session = null;
		try {
			session = sessionFactory.getSession();
			if (tags == null || tags.equals(""))
				return matched;

			String sql_tag = "";
			java.util.StringTokenizer t = new java.util.StringTokenizer(tags,
					";");
		
			while (t.hasMoreElements()) {
				String tag = t.nextToken();
				sql_tag += "cq:tags like '%" + tag + "%'";
				if (t.hasMoreElements())
					sql_tag += " or ";
			}

			String sql = "select dc:description,dc:format, dc:title from nt:unstructured where jcr:path like '/content/dam/girlscouts-vtk/global/resource/%'  ";
			if (!sql_tag.equals(""))
				sql += " and ( " + sql_tag + " )";

			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();
			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.SQL);
			QueryResult result = q.execute();

			for (RowIterator it = result.getRows(); it.hasNext();) {
				Row r = it.nextRow();
				Value excerpt = r.getValue("jcr:path");

				String path = excerpt.getString();
				if (path.contains("/jcr:content"))
					path = path.substring(0, (path.indexOf("/jcr:content")));

				Asset search = new Asset();
				search.setRefId(path);
				search.setIsCachable(true);
				search.setType(AssetComponentType.RESOURCE);
			
				try {
					search.setDescription(r.getValue("dc:description")
							.getString());
				} catch (Exception e) {
				}
				try {
					search.setTitle(r.getValue("dc:title").getString());
				} catch (Exception e) {
				}
				matched.add(search);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return matched;
	}

	private List<Asset> getAssetsFromPath(String rootPath,
			AssetComponentType type, Session session) {
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
					asset.setIsCachable(true);
					asset.setType(type);

					asset.setDescription(props.getProperty("dc:description")
							.getString());
					asset.setTitle(props.getProperty("dc:title").getString());

					assets.add(asset);
				} catch (Exception e) {
					if (node != null) {
						log.warn("Cannot get asset " + node.getPath());
					}
					log.warn("Cannot get asset. rootPath = " + rootPath
							+ ". Root cause was: " + e.getMessage());
				}
			}
		} catch (Exception e) {
			log.error("Cannot get assets for meeting: " + rootPath
					+ ". Root cause was: " + e.getMessage());
		}
		return assets;
	}

	//public List<Asset> getResource_local(User user, Troop troop, String tags, String meetingName, String meetingPath)
	public List<Asset> getResource_local(User user, Troop troop, String meetingName, String meetingPath)
			throws IllegalAccessException {

		if (user != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_LOGIN_ID))
			throw new IllegalAccessException();

		return getLocalAssets(meetingName, meetingPath,
				AssetComponentType.RESOURCE);
	}

	private String getSchoolYearDamPath() {
		String schoolYear = Integer.toString(VtkUtil.getCurrentGSYear());
		String path = "/content/dam/girlscouts-vtk" + schoolYear;
		return path;
	}

	public SearchTag searchA(User user, Troop troop, String councilCode)
			throws IllegalAccessException {

		if (user != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_LOGIN_ID))
			throw new IllegalAccessException();

		String councilStr = councilMapper.getCouncilBranch(councilCode);
		councilStr = councilStr.replace("/content/", "");
		Session session = null;
		SearchTag tags = new SearchTag();
		try {
			session = sessionFactory.getSession();
			String tagStr = councilStr;
			try{
				Node homepage = session.getNode("/content/" + councilStr + "/en/jcr:content");
				if(homepage != null){
					if(homepage.hasProperty("event-cart")){
						if("true".equals(homepage.getProperty("event-cart").getString())){
							tagStr = "sf-activities";
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			java.util.Map<String, String> regionsMain = searchRegion(user,
					troop, councilStr);
			java.util.Map<String, String> categories = new java.util.TreeMap();
			java.util.Map<String, String> levels = new java.util.TreeMap();
			String sql = "select jcr:title from nt:base where type='cq:Tag' and jcr:path like '/etc/tags/"+ tagStr + "/%'";
			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();
			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.SQL);
			QueryResult result = q.execute();
			for (RowIterator it = result.getRows(); it.hasNext();) {
				Row r = it.nextRow();

				if (r.getPath().startsWith(
						"/etc/tags/" + tagStr + "/categories")) {
					String elem = r.getValue("jcr:title").getString();
					categories.put(r.getNode().getName(), elem);
				} else if (r.getPath().startsWith(
						"/etc/tags/" + tagStr + "/program-level")) {
					String elem = r.getValue("jcr:title").getString();
					levels.put(r.getNode().getName(), elem);
				}

			}

			if ((categories == null || categories.size() == 0)
					&& (levels == null || levels.size() == 0)) {
				try {
					SearchTag defaultTags = getDefaultTags(user, troop);
					if (regionsMain != null && regionsMain.size() > 0)
						defaultTags.setRegion(regionsMain);
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
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return tags;
	}

	public SearchTag getDefaultTags(User user, Troop troop)
			throws IllegalAccessException {

		if (user != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_LOGIN_ID))
			throw new IllegalAccessException();

		Session session = null;
		String councilStr = "girlscouts";
		SearchTag tags = new SearchTag();
		try {
			session = sessionFactory.getSession();
			java.util.Map<String, String> categories = new java.util.TreeMap();
			java.util.Map<String, String> levels = new java.util.TreeMap();
			//String sql = "select jcr:title from nt:base where jcr:path like '/etc/tags/" + councilStr + "/%'";
			String sql = "select jcr:title from nt:base where type='cq:Tag' and jcr:path like '/etc/tags/" + councilStr + "%'";
			
			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();
			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.SQL);
			QueryResult result = q.execute();
			for (RowIterator it = result.getRows(); it.hasNext();) {
				Row r = it.nextRow();
				if (r.getPath().startsWith(
						"/etc/tags/" + councilStr + "/categories")) {
					String elem = r.getValue("jcr:title").getString();
					if (elem != null)
						elem = elem.toLowerCase().replace("_", "")
								.replace("/", "");
					categories.put(elem, null);
				} else if (r.getPath().startsWith(
						"/etc/tags/" + councilStr + "/program-level")) {
					String elem = r.getValue("jcr:title").getString();
					if (elem != null)
						elem = elem.toLowerCase().replace("_", "")
								.replace("/", "");
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
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return tags;
	}

	public java.util.List<Activity> searchA2(Troop troop, String tags,
			String cat, String keywrd, java.util.Date startDate,
			java.util.Date endDate, String region) {

		java.util.List<Activity> toRet = new java.util.ArrayList();
		Session session = null;
		try {
			session = sessionFactory.getSession();
			boolean isTag = false;
			String sqlTags = "";
			if (tags.equals("|"))
				tags = "";
			StringTokenizer t = new StringTokenizer(tags, "|");
			while (t.hasMoreElements()) {
				sqlTags += " contains(parent.[cq:tags], 'program-level/"
						+ t.nextToken() + "') ";
				if (t.hasMoreElements())
					sqlTags += " or ";
				isTag = true;
			}
			if (isTag)
				sqlTags = " and (" + sqlTags + " ) ";

			String sqlCat = "";
			if (cat.equals("|"))
				cat = "";
			t = new StringTokenizer(cat, "|");
			while (t.hasMoreElements()) {
				sqlCat += " contains(parent.[cq:tags], 'categories/"
						+ t.nextToken() + "') ";
				if (t.hasMoreElements())
					sqlCat += " or ";
				isTag = true;
			}
			if (!sqlCat.equals(""))
				sqlCat = " and (" + sqlCat + " ) ";

			String regionSql = "";
			if (region != null && !region.trim().equals("")) {
				regionSql += " and LOWER(region) ='" + region + "'";
			}

			String path = "/content/gateway/en/events/"
					+ VtkUtil.getCurrentGSYear() + "/%";
			if (!isTag)
				path = path + "/data";
			else
				path = path + "/jcr:content";

			String councilId = null;
			if (troop.getTroop() != null) {
				councilId = Integer.toString(troop.getTroop().getCouncilCode());
			}
			String branch = councilMapper.getCouncilBranch(councilId);

			branch += "/en";
			String eventPath = "";
			try {
				eventPath = session.getProperty(
						branch + "/jcr:content/eventPath").getString();
			} catch (Exception e) {
				e.printStackTrace();
			}

			String sql = "select child.address, parent.[jcr:uuid], child.start, parent.[jcr:title], child.details, child.end,child.locationLabel,child.srchdisp  from [nt:base] as parent INNER JOIN [nt:base] as child ON ISCHILDNODE(child, parent) where  (isdescendantnode (parent, ["
					+ eventPath
					+ "])) and child.start is not null and parent.[jcr:title] is not null ";

			if (keywrd != null && !keywrd.trim().equals(""))// && !isTag )
				sql += " and (contains(child.*, '" + keywrd
						+ "') or contains(parent.*, '" + keywrd + "')  )";

			if (!isTag)
				sql += regionSql;

			sql += sqlTags;
			sql += sqlCat;
			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();
			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.JCR_SQL2);

			int i = 0;
			QueryResult result = q.execute();
			for (RowIterator it = result.getRows(); it.hasNext();) {
				Row r = it.nextRow();

				Value v[] = r.getValues();

				Activity activity = new Activity();
				activity.setUid("A" + new java.util.Date().getTime() + "_"
						+ Math.random());

				activity.setContent(r.getValue("child.details").getString());
				activity.setDate(r.getValue("child.start").getDate().getTime());
				try {
					activity.setEndDate(r.getValue("child.end").getDate()
							.getTime());
				} catch (Exception e) {
				}

				if ((activity.getDate().before(new java.util.Date()) && activity
						.getEndDate() == null)
						|| (activity.getEndDate() != null && activity
								.getEndDate().before(new java.util.Date()))) {
					continue;
				}
				activity.setLocationName(r.getValue("child.locationLabel")
						.getString());
				try {
					activity.setLocationAddress(r.getValue("child.address")
							.getString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				activity.setName(r.getValue("child.srchdisp").getString());
				activity.setName(r.getValue("parent.jcr:title").getString());
				activity.setType(YearPlanComponentType.ACTIVITY);
				activity.setId("ACT" + i);

				// patch
				if (activity.getDate() != null && activity.getEndDate() == null) {
					activity.setEndDate(activity.getDate());
				}
				activity.setIsEditable(false);
				try {
					activity.setRefUid(r.getValue("parent.jcr:uuid")
							.getString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (startDate != null && endDate != null)
					if ((startDate.after(endDate))
							|| activity.getEndDate().before(startDate)
							|| ((activity.getDate().before(startDate) || activity
									.getDate().after(startDate)) && activity
									.getDate().after(endDate))
							|| (activity.getEndDate().before(startDate) && (activity
									.getEndDate().after(endDate) || activity
									.getEndDate().after(endDate)))) {
						continue;
					}

				toRet.add(activity);
				i++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return toRet;
	}

	public java.util.Map<String, String> searchRegion(User user, Troop troop,
			String councilStr) throws IllegalAccessException {

		if (user != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_LOGIN_ID))
			throw new IllegalAccessException();

		java.util.Map<String, String> container = new java.util.TreeMap();
		Session session = null;
		Node homepage = null;
		String repoStr = councilStr + "/en/events-repository";
		try {
			session = sessionFactory.getSession();
			try{
				homepage = session.getNode(councilStr + "/en/jcr:content");
				if(homepage != null){
					if(homepage.hasProperty("eventPath")){
						repoStr = homepage.getProperty("eventPath").getString();
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			java.util.Map<String, String> categories = new java.util.TreeMap();
			java.util.Map<String, String> levels = new java.util.TreeMap();
			String sql = "select region, start, end from cq:Page where jcr:path like '/content/"
					+ repoStr
					+ "/%' and region is not null";
			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();
			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.SQL);
			QueryResult result = q.execute();
			for (RowIterator it = result.getRows(); it.hasNext();) {
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
					if (endDate != null && endDate.before(now))
						continue;
					else if (endDate == null && startDate.before(now))
						continue;
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (!container.containsKey(elem))
					container.put(elem, null);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return container;
	}

	public java.util.List<Meeting> getAllMeetings(User user, Troop troop,
			String gradeLevel) throws IllegalAccessException {

		if (user != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_VIEW_MEETING_ID))
			throw new IllegalAccessException();

		java.util.List<Meeting> meetings = null;
		Session session = null;
		try {
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Meeting.class);
			classes.add(Activity.class);
			classes.add(JcrCollectionHoldString.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(Meeting.class);
			filter.setScope("/content/girlscouts-vtk/meetings/myyearplan"
					+ VtkUtil.getCurrentGSYear() + "/" + gradeLevel + "/");
			Query query = queryManager.createQuery(filter);
			meetings = (List<Meeting>) ocm.getObjects(query);

			Comparator<Meeting> comp = new BeanComparator("position");
	
			if (meetings != null)
				Collections.sort(meetings, comp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return meetings;

	}

	public List<Asset> getAllResources(User user, Troop troop, String _path)
			throws IllegalAccessException {

		if (user != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_LOGIN_ID))
			throw new IllegalAccessException();

		List<Asset> matched = new ArrayList<Asset>();
		Session session = null;
		try {
			String sql = "select [dc:description], [dc:format], [dc:title], [jcr:mimeType], [jcr:path] "
					+ " from [nt:unstructured] as parent where "
					+ " (isdescendantnode (parent, ["
					+ _path
					+ "])) and [cq:tags] is not null";
;			
			session = sessionFactory.getSession();
			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();
			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.JCR_SQL2);
			QueryResult result = q.execute();
			for (RowIterator it = result.getRows(); it.hasNext();) {
				Row r = it.nextRow();
				Asset search = new Asset();
				search.setRefId(r.getPath().replace("/jcr:content/metadata", ""));
				search.setIsCachable(true);
				search.setType(AssetComponentType.RESOURCE);
				try {
					search.setDescription(r.getValue("dc:description")
							.getString());
				} catch (Exception e) {
				}
				try {
					search.setTitle(r.getValue("dc:title").getString());
				} catch (Exception e) {
				}
				try {
		
					if( r.getPath().indexOf(".")!=-1 ){
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
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return matched;
	}

	public Asset getAsset(User user, Troop troop, String _path)
			throws IllegalAccessException {

		if (user != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_LOGIN_ID))
			throw new IllegalAccessException();

		Asset search = null;
		Session session = null;
		try {
			session = sessionFactory.getSession();
			String sql = "";
			if (_path != null && _path.contains("metadata/"))
				_path = _path.replace("metadata/", "");
			sql = "select dc:description,dc:format, dc:title,isOutdoorRelated from nt:unstructured where jcr:path like '"
					+ _path + "%' and cq:tags is not null";
			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();
			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.SQL);
			QueryResult result = q.execute();
			for (RowIterator it = result.getRows(); it.hasNext();) {
				Row r = it.nextRow();
				Value excerpt = r.getValue("jcr:path");
				String path = excerpt.getString();
				if (path.contains("/jcr:content"))
					path = path.substring(0, (path.indexOf("/jcr:content")));
				search = new Asset();
				search.setRefId(path);
				search.setIsCachable(true);
				search.setType(AssetComponentType.RESOURCE);
				try {
					search.setDescription(r.getValue("dc:description")
							.getString());
				} catch (Exception e) {
				}
				try {
					search.setTitle(r.getValue("dc:title").getString());
				} catch (Exception e) {
				}
				try {
					search.setIsOutdoorRelated(r.getValue("isOutdoorRelated").getBoolean());
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return search;
	}

	public java.util.List<Asset> getGlobalResources(String resourceTags) {
		java.util.List<Asset> toRet = new java.util.ArrayList();
		if (resourceTags == null || resourceTags.equals(""))
			return toRet;
		Session session = null;
		try {
			session = sessionFactory.getSession();
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

			com.day.cq.search.Query query = qBuilder.createQuery(
					PredicateGroup.create(map), session);

			query.setStart(0);
			query.setHitsPerPage(100);
			SearchResult result = query.getResult();
			for (Hit hit : result.getHits()) {
				try {
					String path = hit.getPath();
					if (!path.endsWith("metadata"))
						continue;

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
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return toRet;
	}

	public Council getCouncil(User user, Troop troop, String councilId)
			throws IllegalAccessException {
		// TODO Permission.PERMISSION_VIEW_MEETING_ID

		Session session = null;
		Council council = null;
		try {
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Council.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(Troop.class);
			council = (Council) ocm.getObject(VtkUtil.getYearPlanBase(user,
					null) + councilId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return council;
	}

	public java.util.List<Milestone> getCouncilMilestones(String councilCode) {

		String councilStr = councilMapper.getCouncilBranch(councilCode);
		councilStr = councilStr.replace("/content/", "");
		Session session = null;
		java.util.List<Milestone> milestones = null;
		try {
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Milestone.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(Milestone.class);
			filter.setScope("/content/" + councilStr + "//");
			Query query = queryManager.createQuery(filter);
			milestones = (java.util.List<Milestone>) ocm.getObjects(query);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return milestones;
	}

	public void saveCouncilMilestones(java.util.List<Milestone> milestones) {

		Session session = null;
		try {
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Milestone.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
			for (int i = 0; i < milestones.size(); i++)
				ocm.update(milestones.get(i));
			ocm.save();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public java.util.List<Activity> searchA1(User user, Troop troop,
			String tags, String cat, String keywrd, java.util.Date startDate,
			java.util.Date endDate, String region)
			throws IllegalAccessException, IllegalStateException {
		
	
		java.util.List<Activity> toRet = new java.util.ArrayList();
		Session session = null;

		if (!userUtil.hasPermission(troop,
				Permission.PERMISSION_VIEW_MEETING_ID))
			throw new IllegalAccessException();

		try {
			session = sessionFactory.getSession();

			String councilStr = councilMapper.getCouncilBranch(troop.getSfCouncil());	 
			if (councilStr==null || councilStr.trim().equals("") ) councilStr= "/content/gateway";
			
			String councilId = null;
			if (troop.getTroop() != null) {
				councilId = Integer.toString(troop.getTroop().getCouncilCode());
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
			
			try{
				Node homepage = session.getNode(branch + "/jcr:content");
				if(homepage != null){
					if(homepage.hasProperty("event-cart")){
						if("true".equals(homepage.getProperty("event-cart").getString())){
							namespace = "sf-activities";
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
			boolean isTag = false;
			String sqlTags = "";
			if (tags.equals("|"))
				tags = "";
			StringTokenizer t = new StringTokenizer(tags, "|");
			while (t.hasMoreElements()) {
				sqlTags += " parent.[cq:tags] like '%" + namespace + ":program-level/" + t.nextToken() + "%' ";
				if (t.hasMoreElements())
					sqlTags += " or ";
				isTag = true;
			}
			if (isTag)
				sqlTags = " and (" + sqlTags + " ) ";
			String sqlCat = "";
			if (cat.equals("|"))
				cat = "";
			t = new StringTokenizer(cat, "|");
			while (t.hasMoreElements()) {
				sqlCat += " parent.[cq:tags] like '%" + namespace + ":categories/" + t.nextToken() + "%' ";
				if (t.hasMoreElements())
					sqlCat += " or ";
				isTag = true;
			}
			if (!sqlCat.equals(""))
				sqlCat = " and (" + sqlCat + " ) ";

			String regionSql = "";
			if (region != null && !region.trim().equals("")) {
				regionSql += " and LOWER(child.region) ='" + region + "'";
			}


			String path = councilStr + "/en/events/"
					+ VtkUtil.getCurrentGSYear() + "/%";
			if (!isTag)
				path = path + "/data";
			else
				path = path + "/jcr:content";

			String sql = "select child.register, child.address, parent.[jcr:uuid], child.start, parent.[jcr:title], child.details, child.end,child.locationLabel,child.srchdisp  from [nt:base] as parent INNER JOIN [nt:base] as child ON ISCHILDNODE(child, parent) where  (isdescendantnode (parent, ["
					+ eventPath
					+ "])) and child.start is not null and parent.[jcr:title] is not null ";
			
			
			
			/*
			if (keywrd != null && !keywrd.trim().equals(""))// && !isTag )
				sql += " and (contains(child.*, '" + keywrd
						+ "') or contains(parent.*, '" + keywrd + "')  )";
			*/				
			if (keywrd != null && !keywrd.trim().equals(""))
				sql += " and ( child.* like '%" + keywrd + "%' " +
						" or parent.* like '%" + keywrd + "%'  )";
			
			
			
			sql += regionSql;
			sql += sqlTags;
			sql += sqlCat;
			
//select child.register, child.address, parent.[jcr:uuid], child.start, parent.[jcr:title], child.details, child.end,child.locationLabel,child.srchdisp  from [nt:base] as parent INNER JOIN [nt:base] as child ON ISCHILDNODE(child, parent) where  (isdescendantnode (parent, [/content/girlscoutshh/en/sf-events-repository])) and child.start is not null and parent.[jcr:title] is not null  and ( child.* like '%Earth Day%'  or parent.* like '%Earth Day%'  )
			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();
			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.JCR_SQL2);
			int i = 0;
			QueryResult result = q.execute();
			for (RowIterator it = result.getRows(); it.hasNext();) {
				Row r = it.nextRow();
				Value v[] = r.getValues();
		
				Activity activity = new Activity();
				activity.setUid("A" + new java.util.Date().getTime() + "_"
						+ Math.random());
				activity.setContent(r.getValue("child.details").getString());

				// convert to EST
				// TODO: All VTK date is based on server time zone, which is
				// eastern now.
				// Event dates in councils may be in a different time zone.
				// For a temp solution, always force it to eastern time.
				// e.g. For Texas, 2014-11-06T09:00:00.000-06:00 will be forced
				// to
				// 2014-11-06T09:00:00.000-05:00
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss.SSS");
				try {
					String eventStartDateStr = r.getValue("child.start")
							.getString();
					Date eventStartDate = dateFormat.parse(eventStartDateStr);
					activity.setDate(eventStartDate);
					String eventEndDateStr = r.getValue("child.end")
							.getString();
					Date eventEndDate = dateFormat.parse(eventEndDateStr);
					activity.setEndDate(eventEndDate);
				} catch (Exception e) {
				}
				// TODO: end of hacking timezone

				if ((activity.getDate().before(new java.util.Date()) && activity
						.getEndDate() == null)
						|| (activity.getEndDate() != null && activity
								.getEndDate().before(new java.util.Date()))) {
					
					continue;
				}
				activity.setLocationName(r.getValue("child.locationLabel")
						.getString());
				try {
					activity.setLocationAddress(r.getValue("child.address")
							.getString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				activity.setName(r.getValue("child.srchdisp").getString());
				activity.setName(r.getValue("parent.jcr:title").getString());
				activity.setType(YearPlanComponentType.ACTIVITY);
				activity.setId("ACT" + i);
				if (activity.getDate() != null && activity.getEndDate() == null) {
					activity.setEndDate(activity.getDate());
				}
				activity.setIsEditable(false);
				try {
					activity.setRefUid(r.getValue("parent.jcr:uuid")
							.getString());
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					activity.setRegisterUrl(r.getValue("child.register")
							.getString());
				} catch (Exception e) {
					log.error("searchActivity no register url");
				}

				if (startDate != null && endDate != null) {
					startDate.setHours(0);
					endDate.setHours(23);

					if (activity.getDate() != null
							&& activity.getDate().after(startDate)
							&& activity.getDate().before(endDate))
						;
					else {
						continue;
					}
				}

				toRet.add(activity);
				i++;
			
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		return toRet;
	}

	public void updateCustMeetingPlansRef(java.util.List<String> meetings,
			String path) {
		Session session = null;
		try {
			session = sessionFactory.getSession();
			for (int i = 0; i < meetings.size(); i++) {
				String meetingPath = meetings.get(i);

				Node x = session.getNode(meetingPath);
				String orgPath = x.getProperty("refId").getString();

				log.debug("orgPath : " + orgPath);
				String newPath = path + ""
						+ orgPath.substring(orgPath.indexOf("/lib/"));
				log.debug("Changing cust meeting path from " + path + " to: "
						+ newPath);
				x.setProperty("refId", newPath);
				session.save();

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	public java.util.List<String> getCustMeetings(String path) {
		java.util.List<String> toRet = new java.util.ArrayList<String>();
		Session session = null;
		try {
			session = sessionFactory.getSession();
			String sql = "select * from nt:unstructured where jcr:path like '"
					+ path
					+ "/%' and ocm_classname ='org.girlscouts.vtk.models.MeetingE' and refId like '%/users/%_%' ";
			log.debug("SQL cust" + sql);
			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();

			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.SQL);
			QueryResult result = q.execute();
			for (RowIterator it = result.getRows(); it.hasNext();) {
				Row r = it.nextRow();
				Value excerpt = r.getValue("jcr:path");
				toRet.add(excerpt.getString());
				log.debug("Adding meeting: " + excerpt.getString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return toRet;
	}

	public String removeLocation(User user, Troop troop, String locationName)
			throws IllegalAccessException, IllegalStateException {
		Session session = null;
		String locationToRmPath = null;
		try {
			if (user != null
					&& !userUtil.hasPermission(troop,
							Permission.PERMISSION_EDIT_MEETING_ID))
				throw new IllegalAccessException();
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Troop.class);
			classes.add(Activity.class);
			classes.add(JcrCollectionHoldString.class);
			classes.add(YearPlan.class);
			classes.add(MeetingE.class);classes.add(Note.class);
			classes.add(Location.class);
			classes.add(Cal.class);
			classes.add(Milestone.class);
			classes.add(Asset.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
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
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return locationToRmPath;
	}

	public Attendance getAttendance(User user, Troop troop, String mid) {
		Attendance attendance = null;
		Session session = null;
		try {
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Attendance.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
			attendance = (Attendance) ocm.getObject(mid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return attendance;
	}

	public boolean setAttendance(User user, Troop troop, String mid,
			Attendance attendance) {
		Session session = null;
		try {
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Attendance.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
			if (!session.itemExists(attendance.getPath()))
				ocm.insert(attendance);
			ocm.update(attendance);
			ocm.save();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return false;
	}

	public Achievement getAchievement(User user, Troop troop, String mid) {
		Achievement attendance = null;
		Session session = null;
		try {
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Achievement.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
			QueryManager queryManager = ocm.getQueryManager();
			attendance = (Achievement) ocm.getObject(mid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return attendance;
	}

	public boolean setAchievement(User user, Troop troop, String mid,
			Achievement Achievement) {
		Session session = null;
		try {
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Achievement.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
			if (!session.itemExists(Achievement.getPath()))
				ocm.insert(Achievement);
			ocm.update(Achievement);
			ocm.save();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return false;
	}

	public boolean updateMeetingEvent(User user, Troop troop, MeetingE meeting)
			throws IllegalAccessException, IllegalStateException {
		Session session = null;
		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_EDIT_MEETING_ID))
			throw new IllegalAccessException();

		try {
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(MeetingE.class);classes.add(Note.class);
			classes.add(JcrCollectionHoldString.class);
			classes.add(Asset.class);
			classes.add(SentEmail.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
			ocm.update(meeting);
			ocm.save();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return false;
	}

	public MeetingE getMeetingE(User user, Troop troop, String path)
			throws IllegalAccessException, VtkException {
		if (user != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_VIEW_MEETING_ID))
			throw new IllegalAccessException();
		MeetingE meetingE = null;
		Session session = null;
		try {
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Meeting.class); 
			classes.add(Activity.class);
			classes.add(MeetingE.class);classes.add(Note.class);
			classes.add(Achievement.class);
			classes.add(Asset.class);
			classes.add(Attendance.class);
			classes.add(SentEmail.class);
			
			classes.add(JcrCollectionHoldString.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);

			meetingE = (MeetingE) ocm.getObject(path);

		} catch (org.apache.jackrabbit.ocm.exception.IncorrectPersistentClassException ec) {
			ec.printStackTrace();
			throw new VtkException(
					"Could not complete intended action due to a server error. Code: "
							+ new java.util.Date().getTime());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return meetingE;
	}

	public int getAllResourcesCount(User user, Troop troop, String _path)
			throws IllegalAccessException {
		if (user != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_LOGIN_ID))
			throw new IllegalAccessException();
		int count = 0;
		List<Asset> matched = new ArrayList<Asset>();
		Session session = null;
		try {
			String sql = "select [dc:description], [dc:format], [dc:title], [jcr:mimeType], [jcr:path] "
					+ " from [nt:unstructured] as parent where "
					+ " (isdescendantnode (parent, ["
					+ _path
					+ "])) and [cq:tags] is not null";
			session = sessionFactory.getSession();
			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();
			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.JCR_SQL2);
			QueryResult result = q.execute();
			count = (int) result.getRows().getSize();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		return count;
	}

	public List<org.girlscouts.vtk.models.Search> getData(User user,
			Troop troop, String _query) throws IllegalAccessException {
		java.util.List data = null, data1 = null, data2 = null, data3 = null;
		try {
			data1 = getDataItem(user, troop, _query, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			data2 = getDataItem(user, troop, _query,
					"/content/dam/girlscouts-vtk/global/aid");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			data3 = getDataItem(user, troop, _query,
					"/content/dam/girlscouts-vtk/global/resource");
		} catch (Exception e) {
			e.printStackTrace();
		}

		data = new java.util.ArrayList();

		if (data1 != null)
			data.addAll(data1);

		if (data2 != null)
			data.addAll(data2);

		if (data3 != null)
			data.addAll(data3);

		return data;

	}

	public List<org.girlscouts.vtk.models.Search> getDataItem_old020316(User user,

	Troop troop, String _query, String PATH) throws IllegalAccessException {

		if (user != null

		&& !userUtil.hasPermission(troop,

		Permission.PERMISSION_LOGIN_ID))

			throw new IllegalAccessException();

		Session session = null;

		List<org.girlscouts.vtk.models.Search> matched = null;
		final String RESOURCES_PATH = "resources";
		String councilId = null;
		if (troop.getTroop() != null) {
			councilId = Integer.toString(troop.getTroop().getCouncilCode());
		}

		String branch = councilMapper.getCouncilBranch(councilId);

		String resourceRootPath = branch + "/en/" + RESOURCES_PATH;

		if (PATH == null) {
			PATH = resourceRootPath;
		}
		matched = new ArrayList<org.girlscouts.vtk.models.Search>();

		try {

			session = sessionFactory.getSession();

			java.util.Map<String, String> map = new java.util.HashMap<String, String>();
			map.put("fulltext", _query);
			map.put("path", PATH);
			//map.put("p.limit", "1");




	com.day.cq.search.Query query = qBuilder.createQuery(
					PredicateGroup.create(map), session);
	
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

					if (search.getPath().toLowerCase().contains("/aid/"))

						search.setAssetType(AssetComponentType.AID);

					if (unq.containsKey(search.getPath())) {

						if (search.getContent() != null

						&& !search.getContent().trim().equals("")) {

							org.girlscouts.vtk.models.Search _search = unq

							.get(search.getPath());

							if (_search.getContent() == null

							|| _search.getContent().trim().equals(""))

								unq.put(search.getPath(), search);

						}

					} else

						unq.put(search.getPath(), search);

				} catch (RepositoryException e) {

					e.printStackTrace();

				}

			}

			java.util.Iterator itr = unq.keySet().iterator();

			while (itr.hasNext())

				matched.add(unq.get(itr.next()));

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			try {

				if (session != null)

					sessionFactory.closeSession(session);

			} catch (Exception ex) {

				ex.printStackTrace();

			}

		}

		return matched;

	}
	public List<org.girlscouts.vtk.models.Search> getDataItem(User user,

			Troop troop, String _query, String PATH) throws IllegalAccessException {
		
				if (user != null

				&& !userUtil.hasPermission(troop,

				Permission.PERMISSION_LOGIN_ID))

					throw new IllegalAccessException();

				Session session = null;

				List<org.girlscouts.vtk.models.Search> matched = null;
				final String RESOURCES_PATH = "resources";
				String councilId = null;
				if (troop.getTroop() != null) {
					councilId = Integer.toString(troop.getTroop().getCouncilCode());
				}

				String branch = councilMapper.getCouncilBranch(councilId);

				String resourceRootPath = branch + "/en/" + RESOURCES_PATH;

				if (PATH == null) {
					PATH = resourceRootPath;
				}
				matched = new ArrayList<org.girlscouts.vtk.models.Search>();
				try {

					session = sessionFactory.getSession();

					java.util.Map<String, String> map = new java.util.HashMap<String, String>();
					map.put("fulltext", _query);
					map.put("path", PATH);
					map.put("type", "dam:Asset");
					//map.put("p.limit", "1");
		
			com.day.cq.search.Query query = qBuilder.createQuery(
							PredicateGroup.create(map), session);
			
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

							if (search.getPath().toLowerCase().contains("/aid/"))

								search.setAssetType(AssetComponentType.AID);

							if (unq.containsKey(search.getPath())) {

								if (search.getContent() != null

								&& !search.getContent().trim().equals("")) {

									org.girlscouts.vtk.models.Search _search = unq

									.get(search.getPath());

									if (_search.getContent() == null

									|| _search.getContent().trim().equals(""))

										unq.put(search.getPath(), search);

								}

							} else

								unq.put(search.getPath(), search);

						} catch (RepositoryException e) {

							e.printStackTrace();

						}

					}

					java.util.Iterator itr = unq.keySet().iterator();

					while (itr.hasNext())

						matched.add(unq.get(itr.next()));

				} catch (Exception e) {

					e.printStackTrace();

				} finally {

					try {

						if (session != null)

							sessionFactory.closeSession(session);

					} catch (Exception ex) {

						ex.printStackTrace();

					}

				}

				return matched;

			}

	
	
	public int getAssetCount(User user, Troop troop, String path) throws IllegalAccessException {
		int count = 0;
		if(path == null || "".equals(path)) {
			return 0;
		}
		Session session = null;
		try {
			String sql = "select [jcr:path] "
					+ " from [dam:Asset] as s   where "
					+ " (isdescendantnode (s, ["
					+ path
					+ "]))";

			session = sessionFactory.getSession();
			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();
			
			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.JCR_SQL2);
			
			QueryResult result = q.execute();
			
			NodeIterator itr = result.getNodes();
			while(itr.hasNext()){
				itr.next() ;
			
				count++;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null) {
					sessionFactory.closeSession(session);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		return count;
	}
	

	
	
	public int getCountLocalMeetingAidsByLevel(User user, Troop troop, String _path)
			throws IllegalAccessException { 
		
		int count = 0;
		if( troop==null || troop.getTroop()==null || troop.getTroop().getGradeLevel()==null) return 0;
		
		String level = troop.getTroop().getGradeLevel().toLowerCase();
		if (level.contains("-")) {
			level = level.split("-")[1];
		}
		
		Session session = null;
		try {
			String sql = "select [dc:description], [dc:format], [dc:title], [jcr:mimeType], [jcr:path] "
					+ " from [nt:unstructured] as parent where "
					+ " (isdescendantnode (parent, ["
					+ _path
					+ "])) and [cq:tags] is not null";

			session = sessionFactory.getSession();
			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();
			
			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.JCR_SQL2);
			
			QueryResult result = q.execute();
			NodeIterator itr = result.getNodes();
			while(itr.hasNext()){
				Node node = (Node)itr.next() ;
			
				if( node.getPath().toLowerCase().contains( ("meetings/" + level.charAt(0)).toLowerCase() ) ) 
						count++;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null) {
					sessionFactory.closeSession(session);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		return count;
	}
	
	
	public java.util.Collection<bean_resource> getResourceData(User user, Troop troop, String _path)
			throws IllegalAccessException { 
		
		int count = 0;
		
		if (resourceCountMap.containsKey(_path)) {
			// it contains the key
			if(System.currentTimeMillis() - ((Long)resourceCountMap.get(RESOURCE_COUNT_MAP_AGE)).longValue() < MAX_CACHE_AGE_MS) {
				// return cache
				
				return (java.util.Collection<bean_resource>)resourceCountMap.get(_path);
			}
		}
		
		java.util.Map <String, bean_resource>dictionary =null;
		Session session = null;
		try {
			
			String sql = "SELECT [jcr:path], [jcr:title] FROM [cq:PageContent] AS s WHERE ISDESCENDANTNODE(s, ["+ _path +"])";

			session = sessionFactory.getSession();
			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();
			
			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.JCR_SQL2);
			
			java.util.Map <String, String>categoryDictionary = new java.util.TreeMap<String, String>();
			java.util.Map <String, java.util.List<String>>container = new java.util.TreeMap();
			dictionary = new java.util.TreeMap<String, bean_resource>();
			
			QueryResult result = q.execute();
			NodeIterator itr = result.getNodes();
			while(itr.hasNext()){
				Node node = (Node)itr.next() ;
				String path = node.getPath();
				
				String pathUri = path.replace(_path, "");
	
				String[] nodes = pathUri.split("/");
			
			
			if( nodes.length==4){
			
				bean_resource beanResource = new bean_resource();
				beanResource.setPath(path.replace("/jcr:content", ""));
				beanResource.setTitle( node.getProperty("jcr:title").getString() );
				beanResource.setNodeUri( nodes[2] );
				beanResource.setCategory( nodes[1] );
				dictionary.put( nodes[1] + "|" + nodes[2], beanResource);
				
			}
			
			
			    if( nodes.length<=2 || nodes[2].equals("jcr:content") ){
			    	
			    	categoryDictionary.put(nodes[1],node.getProperty("jcr:title").getString() );
			    	continue;	
			    }
			
				java.util.List list =  container.get( nodes[1] + "|" + nodes[2]);
				if( list ==null )
					list= new java.util.ArrayList<String>();
				
				if( nodes.length > 3 && !nodes[3].equals("jcr:content"))
					list.add( nodes[3] );
				
				container.put( nodes[1] + "|" + nodes[2], list); 
				
			}
			
			
			java.util.Iterator _itr = container.keySet().iterator();
			while( _itr.hasNext() ){
				String title=  (String) _itr.next();
				java.util.List <String>links = container.get( title );
				
				bean_resource  resource = dictionary.get( title );
				resource.setItemCount(links.size());
				resource.setCategoryDisplay( categoryDictionary.get( resource.getCategory()) );
				
			}
			
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null) {
					sessionFactory.closeSession(session);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		
				
		resourceCountMap.put(_path, dictionary.values());
		resourceCountMap.put(RESOURCE_COUNT_MAP_AGE, System.currentTimeMillis());

		
		return dictionary.values();
	}
	
	public int getMeetingCount(User user, Troop troop, String path) throws IllegalAccessException {
		
		int count = 0;
		if(path == null || "".equals(path)) {
			return 0;
		}
		
		if (resourceCountMap.containsKey(path)) {
			// it contains the key
			if(System.currentTimeMillis() - ((Long)resourceCountMap.get(RESOURCE_COUNT_MAP_AGE)).longValue() < MAX_CACHE_AGE_MS) {
				// return cache
			
				return ((Integer)resourceCountMap.get(path)).intValue();
			}
		}
		
		
		Session session = null;
		try {
			String sql = "select * from nt:base where jcr:path like '"+path+"%' and ocm_classname='org.girlscouts.vtk.models.Meeting'";

			session = sessionFactory.getSession();
			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();
			
			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.SQL);
			
			QueryResult result = q.execute();
			//count = (int) result.getNodes().getSize();
			Iterator itr= result.getNodes();
			while( itr.hasNext() ){
					itr.next();
					count++;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null) {
					sessionFactory.closeSession(session);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		
				
		resourceCountMap.put(path, count);
		resourceCountMap.put(RESOURCE_COUNT_MAP_AGE, System.currentTimeMillis());
		return count;
	}
	
	

public int getVtkAssetCount(User user, Troop troop, String path) throws IllegalAccessException {
		if (resourceCountMap.containsKey(path)) {
			// it contains the key
			if(System.currentTimeMillis() - ((Long)resourceCountMap.get(RESOURCE_COUNT_MAP_AGE)).longValue() < MAX_CACHE_AGE_MS) {
				// return cache
			
				return ((Long)resourceCountMap.get(path)).intValue();
			}
		}
			
		long count = 0;
		if(path == null || "".equals(path)) {
			return 0;
		}
		Session session = null;
		try {
			String sql = "select [jcr:path]  from [nt:unstructured] as s   where  (isdescendantnode (s, ["+path+"])) and [cq:tags] is not null";

			session = sessionFactory.getSession();
			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();
			
			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.JCR_SQL2);
			
			QueryResult result = q.execute();
			count = (long) result.getNodes().getSize();
				
			resourceCountMap.put(path, count);
			resourceCountMap.put(RESOURCE_COUNT_MAP_AGE, System.currentTimeMillis());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null) {
					sessionFactory.closeSession(session);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		return (int)count;
	}
	

public java.util.List<Meeting> getAllMeetings(User user, Troop troop) throws IllegalAccessException {

	if (user != null
			&& !userUtil.hasPermission(troop,
					Permission.PERMISSION_VIEW_MEETING_ID))
		throw new IllegalAccessException();

	java.util.List<Meeting> meetings = null;
	Session session = null;
	try {
		session = sessionFactory.getSession();
		List<Class> classes = new ArrayList<Class>();
		classes.add(Meeting.class);
		//classes.add(Activity.class);
		//classes.add(Meeting2.class);
		//classes.add(JcrCollectionHoldString.class);
		Mapper mapper = new AnnotationMapperImpl(classes);
		
		
		
		
String xmlDescriptor = 


"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<!DOCTYPE jackrabbit-ocm PUBLIC \"-//The Apache Software Foundation//DTD Jackrabbit OCM 1.5//EN\" "+
		"\"http://jackrabbit.apache.org/dtd/jackrabbit-ocm-1.5.dtd\">"+
"<jackrabbit-ocm>"+
"<class-descriptor className=\"org.girlscouts.vtk.models.Meeting\" jcrType=\"nt:unstructured\">"+
"<field-descriptor fieldName=\"path\" path=\"true\" />"+
"<field-descriptor fieldName=\"id\" jcrName=\"id\" />"+
"<field-descriptor fieldName=\"level\" jcrName=\"level\"/>"+
"<field-descriptor fieldName=\"position\" jcrName=\"position\"/>"+
"<field-descriptor fieldName=\"name\" jcrName=\"name\"/>"+
"<field-descriptor fieldName=\"blurb\" jcrName=\"blurb\"/>"+
"<field-descriptor fieldName=\"cat\" jcrName=\"cat\"/>"+
"<field-descriptor fieldName=\"catTags\" jcrName=\"catTags\"/>"+
"<field-descriptor fieldName=\"meetingPlanType\" jcrName=\"meetingPlanType\"/>"+


"</class-descriptor></jackrabbit-ocm>";
InputStream[] in = new InputStream[1];
in[0]=IOUtils.toInputStream(xmlDescriptor, "UTF-8");
		 
		 
		 
		ObjectContentManager ocm = new ObjectContentManagerImpl(session, in);//(session,mapper);
		QueryManager queryManager = ocm.getQueryManager();
		Field field = new Meeting().getClass().getDeclaredField("activities");
		org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection anno = field.getAnnotation(org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection.class);
		Filter filter = queryManager.createFilter(Meeting.class);
		filter.setScope("/content/girlscouts-vtk/meetings/myyearplan"+ VtkUtil.getCurrentGSYear() + "//");
		Query query = queryManager.createQuery(filter);
		meetings = (List<Meeting>) ocm.getObjects(query);
		Comparator<Meeting> comp = new BeanComparator("position");
		if (meetings != null)
			Collections.sort(meetings, comp);
	
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		try {
			if (session != null)
				sessionFactory.closeSession(session);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	return meetings;
	

}


//changed to SQL . problem could be with performance. A. cant use path in the filter. Path contains nodes starting with numeric value ex: council 999. Solution need to impl new indexes. Could be problem. Temp solution impl sql see getNotes 
public java.util.List<Note> getNotes_OCM(User user, Troop troop, String path)
		throws IllegalAccessException, VtkException {

if (user != null && !userUtil.hasPermission(troop,
		Permission.PERMISSION_CREATE_MEETING_ID))
throw new IllegalAccessException();

	
	java.util.List<Note> notes= null;
	Session session = null;
	try {
		List<Class> classes = new ArrayList<Class>();
		classes.add(MeetingE.class);classes.add(Note.class);
		classes.add(Achievement.class);
		classes.add(Attendance.class);
		session = sessionFactory.getSession();
		Mapper mapper = new AnnotationMapperImpl(classes);
		ObjectContentManager ocm = new ObjectContentManagerImpl(session,
				mapper);

		QueryManager queryManager = ocm.getQueryManager();
		Filter filter = queryManager.createFilter(Note.class);
		filter.setScope(VtkUtil.getYearPlanBase(user, troop) +"/" );
		filter.addEqualTo("refId", path );
		Query query = queryManager.createQuery(filter);
		notes = (List<Note>) ocm.getObjects(query);
			
		
		//sort
		java.util.Comparator<Note> comp = new org.apache.commons.beanutils.BeanComparator(
				"createTime");
		Collections.sort(notes, comp);
		Collections.reverse(notes);
		
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		try {
			if (session != null)
				sessionFactory.closeSession(session);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	

	return notes;
}

public boolean updateNote(User user, Troop troop,Note  note) throws IllegalAccessException {
	
	boolean isRm= false;
	if (user != null
			&& !userUtil.hasPermission(troop,
					Permission.PERMISSION_CREATE_MEETING_ID))
		throw new IllegalAccessException();
	
	if ( !user.getApiConfig().getUser().getSfUserId().equals( note.getCreatedByUserId()  ))
		throw new IllegalAccessException();
	
	Session session = null;
	try {
		session = sessionFactory.getSession();
		List<Class> classes = new ArrayList<Class>();
		classes.add(MeetingE.class);
		classes.add(Note.class);
		
		Mapper mapper = new AnnotationMapperImpl(classes);
		ObjectContentManager ocm = new ObjectContentManagerImpl(session,
				mapper);
		
			
		if (!session.itemExists(note.getPath()))
			ocm.insert(note); // y ??
		else
			ocm.update(note);
		ocm.save();
		isRm=true;
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		try {
			if (session != null)
				sessionFactory.closeSession(session);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	return isRm;
	
}


public boolean rmNote(User user, Troop troop,Note  note) throws IllegalAccessException {
System.err.println("inRmNote start " );
	boolean isRm= false;
	if (user != null
			&& !userUtil.hasPermission(troop,
					Permission.PERMISSION_CREATE_MEETING_ID))
		throw new IllegalAccessException();
	
	Session session = null;
	try {
		session = sessionFactory.getSession();
		List<Class> classes = new ArrayList<Class>();
		classes.add(MeetingE.class);
		classes.add(Note.class);
		
		Mapper mapper = new AnnotationMapperImpl(classes);
		ObjectContentManager ocm = new ObjectContentManagerImpl(session,
				mapper);
System.err.println("inRmNote chking..." );	
		if ( user.getApiConfig().getUser().getSfUserId().equals( note.getCreatedByUserId()  )){//session.itemExists(note.getPath())){
System.err.println("inRmNote yes" );
			ocm.remove(note);
			ocm.save();
			isRm= true;
		}else{
System.err.println("inRmNote no" );	
			throw new IllegalAccessException();
		}
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		try {
			if (session != null)
				sessionFactory.closeSession(session);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	return isRm;
	
}


public boolean rmNote(User user, Troop troop, String  noteId) throws IllegalAccessException {

	System.err.println("inRmNote x1 : "+ noteId);
	boolean isRm= false;
	if (user != null
			&& !userUtil.hasPermission(troop,
					Permission.PERMISSION_CREATE_MEETING_ID))
		throw new IllegalAccessException();
	
	System.err.println("inRmNote x2 : "+ noteId);
	Note note= getNote(user, troop, noteId);
	System.err.println("inRmNote x3 : "+ (note==null) );
	if( note!=null ){
		System.err.println("inRmNote x4 : found note : "+ user.getApiConfig().getUser().getSfUserId() +" : "+ note.getCreatedByUserId());
		//check if note belongs to logged-in user
		if( user.getApiConfig().getUser().getSfUserId().equals( note.getCreatedByUserId() )  ){
			System.err.println("inRmNote x4 : same user");
			rmNote(user, troop, note );
			isRm=true;
		}else{
			throw new IllegalAccessException();
		}
	}
	return isRm;
}



// method to get meeting note. Issue with using jcr:path. path starts with numeric such as /council 999, which creates conflict.
//Solution: need to create indexes. for now temp create method using SQL
public Note getNote_OCM(User user, Troop troop, String nid)
		throws IllegalAccessException {
	
	if (user != null
			&& !userUtil.hasPermission(troop,
					Permission.PERMISSION_CREATE_MEETING_ID))
		throw new IllegalAccessException();
	
	Note note= null;
	Session session = null;
	try {
		List<Class> classes = new ArrayList<Class>();
		classes.add(MeetingE.class);classes.add(Note.class);
		classes.add(Achievement.class);
		classes.add(Attendance.class);
		session = sessionFactory.getSession();
		Mapper mapper = new AnnotationMapperImpl(classes);
		ObjectContentManager ocm = new ObjectContentManagerImpl(session,
				mapper);
		QueryManager queryManager = ocm.getQueryManager();
		Filter filter = queryManager.createFilter(Note.class);
		filter.setScope(VtkUtil.getYearPlanBase(user, troop) +"/" );
	
		filter.addEqualTo("uid", nid );
		Query query = queryManager.createQuery(filter);
		note = (Note) ocm.getObject(query);


	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		try {
			if (session != null)
				sessionFactory.closeSession(session);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	return note;
}


//see comments from method getNote_OCM
public Note getNote(User user, Troop troop, String nid)
		throws IllegalAccessException {
	
	if (user != null
			&& !userUtil.hasPermission(troop,
					Permission.PERMISSION_CREATE_MEETING_ID))
		throw new IllegalAccessException();
	
	Note note= null;
	Session session = null;
	try {
		session = sessionFactory.getSession();
		javax.jcr.query.QueryManager qm = session.getWorkspace()
				.getQueryManager();
		String sql ="select message,createTime,createdByUserId,createdByUserName,refId,uid from nt:unstructured where  ocm_classname='org.girlscouts.vtk.models.Note' and jcr:path like '"+ troop.getYearPlan().getPath() +"%/notes/"+nid+"'";
		
		javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL);
		QueryResult result = q.execute();
		String str[] = result.getColumnNames();
		for (RowIterator it = result.getRows(); it.hasNext();) {
			Row r = it.nextRow();
			note = new Note();
			Value excerpt = r.getValue("jcr:path");
			String path = excerpt.getString();
	
			note.setPath(path);
			note.setMessage( r.getValue("message").getString() );
			note.setUid(  r.getValue("uid").getString()  );
			note.setCreateTime( r.getValue("createTime").getLong() );
			note.setCreatedByUserName( r.getValue("createdByUserName").getString() );
			note.setCreatedByUserId( r.getValue("createdByUserId").getString() );
			note.setRefId( r.getValue("refId").getString() );
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		try {
			if (session != null)
				sessionFactory.closeSession(session);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	return note;
}



//see comments from method getNotes_OCM
public java.util.List<Note> getNotes(User user, Troop troop, String refId)
		throws IllegalAccessException {

	if (user != null
			&& !userUtil.hasPermission(troop,
					Permission.PERMISSION_CREATE_MEETING_ID))
		throw new IllegalAccessException();
	
	java.util.List<Note> notes = new java.util.ArrayList<Note>();
	Session session = null;
	try {
		session = sessionFactory.getSession();
		javax.jcr.query.QueryManager qm = session.getWorkspace()
				.getQueryManager();
		String sql ="select message,createTime,createdByUserId,createdByUserName,refId,uid from nt:unstructured where  ocm_classname='org.girlscouts.vtk.models.Note'  and jcr:path like '"+ troop.getYearPlan().getPath() +"/meetingEvents/"+ refId +"%'";	
		javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL);
		QueryResult result = q.execute();
		String str[] = result.getColumnNames();
		for (RowIterator it = result.getRows(); it.hasNext();) {
		  try{
			Row r = it.nextRow();
			Note note = new Note();
			Value excerpt = r.getValue("jcr:path");
			String path = excerpt.getString();

			note.setPath(path);
			note.setMessage( r.getValue("message").getString() );
			note.setUid(  r.getValue("uid").getString()  );
			note.setCreateTime( r.getValue("createTime").getLong() );
			note.setCreatedByUserName( r.getValue("createdByUserName").getString() );
			note.setCreatedByUserId( r.getValue("createdByUserId").getString() );
			note.setRefId( r.getValue("refId").getString() );
			notes.add( note );
		  }catch(Exception e){e.printStackTrace();}
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		try {
			if (session != null)
				sessionFactory.closeSession(session);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	return notes;
}

/*
public java.util.List<Meeting> getMeetings(int gsYear)  {
		
	
		java.util.List<Meeting> meetings =new java.util.ArrayList();
		Session session = null;
		try {
			session = sessionFactory.getSession();
			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();
			String sql ="select cat, level from nt:unstructured where ocm_classname='org.girlscouts.vtk.models.Meeting' and jcr:path like '/content/girlscouts-vtk/meetings/myyearplan"+gsYear+"%'";
			javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL);
			QueryResult result = q.execute();
			String str[] = result.getColumnNames();
			for (RowIterator it = result.getRows(); it.hasNext();) {
				Row r = it.nextRow();
				Value excerpt = r.getValue("jcr:path");
				String path = excerpt.getString();
				Meeting meeting  = new Meeting();
				meeting.setCat(r.getValue("cat").getString());
				meeting.setLevel(r.getValue("level").getString());
				meeting.setMeetingType("THE_TYPE");
				
				meetings.add(meeting);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return meetings;
	

}
*/	
}// edn class
