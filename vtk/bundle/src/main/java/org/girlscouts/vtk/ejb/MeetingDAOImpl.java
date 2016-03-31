package org.girlscouts.vtk.ejb;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

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
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.models.SearchTag;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.SentEmail;
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
			classes.add(MeetingE.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
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
			classes.add(MeetingE.class);
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
			}

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
			classes.add(MeetingE.class);
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
			classes.add(MeetingE.class);
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
			classes.add(MeetingE.class);
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
			classes.add(MeetingE.class);
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

	public List<Asset> getResource_local(User user, Troop troop, String tags,
			String meetingName, String meetingPath)
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
			String sql = "select jcr:title from nt:base where jcr:path like '/etc/tags/"
					+ tagStr + "/%'";
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
			String sql = "select jcr:title from nt:base where jcr:path like '/etc/tags/"
					+ councilStr + "/%'";
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
			String sql = "select region, start, end from nt:base where jcr:path like '/content/"
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
			session = sessionFactory.getSession();
			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();
			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.JCR_SQL2);
			QueryResult result = q.execute();
			for (RowIterator it = result.getRows(); it.hasNext();) {
				Row r = it.nextRow();
				Asset search = new Asset();
				search.setRefId(r.getPath()
						.replace("/jcr:content/metadata", ""));
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
					String t = r.getPath().substring(r.getPath().indexOf("."));
					t = t.substring(1, t.indexOf("/"));
					search.setDocType(t);
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
			sql = "select dc:description,dc:format, dc:title from nt:unstructured where jcr:path like '"
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
			
			String councilId = null;
			if (troop.getTroop() != null) {
				councilId = Integer.toString(troop.getTroop().getCouncilCode());
			}
			String branch = councilMapper.getCouncilBranch(councilId);
			String namespace = branch.replace("/content/", "");
			branch += "/en";
			String eventPath = "";
			try {
				eventPath = session.getProperty(
						branch + "/jcr:content/eventPath").getString();
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
				sqlTags += " contains(parent.[cq:tags], '" + namespace + ":program-level/"
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
				sqlCat += " contains( parent.[cq:tags], '" + namespace + ":categories/"
						+ t.nextToken() + "') ";
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

			String path = "/content/gateway/en/events/"
					+ VtkUtil.getCurrentGSYear() + "/%";
			if (!isTag)
				path = path + "/data";
			else
				path = path + "/jcr:content";

			String sql = "select child.register, child.address, parent.[jcr:uuid], child.start, parent.[jcr:title], child.details, child.end,child.locationLabel,child.srchdisp  from [nt:base] as parent INNER JOIN [nt:base] as child ON ISCHILDNODE(child, parent) where  (isdescendantnode (parent, ["
					+ eventPath
					+ "])) and child.start is not null and parent.[jcr:title] is not null ";
			if (keywrd != null && !keywrd.trim().equals(""))// && !isTag )
				sql += " and (contains(child.*, '" + keywrd
						+ "') or contains(parent.*, '" + keywrd + "')  )";
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
			classes.add(MeetingE.class);
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
			classes.add(MeetingE.class);
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
			classes.add(Meeting.class); // eg
										// ClassDescriptorUtils.getFullData(),
										// ClassDescriptorUtils.getMeetingMinimal()
			classes.add(Activity.class);
			classes.add(MeetingE.class);
			classes.add(Achievement.class);
			classes.add(Asset.class);
			classes.add(Attendance.class);
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

}// edn class
