package org.girlscouts.vtk.ejb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
//import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.apache.jackrabbit.ocm.query.Filter;
import org.apache.jackrabbit.ocm.query.Query;
import org.apache.jackrabbit.ocm.query.QueryManager;
import org.apache.sling.api.resource.ValueMap;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.auth.permission.PermissionConstants;
import org.girlscouts.vtk.dao.AssetComponentType;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.dao.YearPlanComponentType;
import org.girlscouts.vtk.helpers.CouncilMapper;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Asset;
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
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.YearPlanComponent;
import org.girlscouts.web.search.DocHit;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

@Component
@Service(value = MeetingDAO.class)
public class MeetingDAOImpl implements MeetingDAO {

	@Reference
	private SessionFactory sessionFactory;

	@Reference
	private QueryBuilder qBuilder;

	@Reference
	org.girlscouts.vtk.helpers.CouncilMapper councilMapper;

	@Activate
	void activate() {
	}

	// by planId
	public java.util.List<MeetingE> getAllEventMeetings(String yearPlanId) {

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
			filter.setScope("/content/girlscouts-vtk/yearPlanTemplates/yearplan2014/brownie/yearPlan"
					+ yearPlanId + "/meetings/");
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
	public java.util.List<MeetingE> getAllEventMeetings_byPath(
			String yearPlanPath) {
		java.util.List<MeetingE> meetings = null;
		Session session = null;
		try {
			List<Class> classes = new ArrayList<Class>();
			classes.add(MeetingE.class);
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

	public Meeting getMeeting(String path) {

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
	public java.util.List<MeetingE> getAllUsersEventMeetings(Troop user,
			String yearPlanId) {
		Session session = null;
		java.util.List<MeetingE> meetings = null;
		if (!hasPermission(user, Permission.PERMISSION_VIEW_MEETING_ID))
			return meetings;
		try {
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(MeetingE.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(MeetingE.class);
			filter.setScope("/content/girlscouts-vtk/users/" + user.getId()
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
	public Meeting createCustomMeeting(Troop user, MeetingE meetingEvent) {
		return createCustomMeeting(user, meetingEvent, null);
	}

	public Meeting createCustomMeeting(Troop user, MeetingE meetingEvent,
			Meeting meeting) {
		Session session = null;
		try {

			if (!this.hasAccess(user, user.getCurrentTroop(),
					PermissionConstants.PERMISSION_CREATE_MEETING_ID)) { // 091514
				user.setErrCode("112");
				return null;
			}
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(MeetingE.class);
			classes.add(Meeting.class);
			classes.add(Activity.class);
			classes.add(JcrCollectionHoldString.class);
			classes.add(JcrNode.class);
			classes.add(Asset.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);

			if (meeting == null)
				meeting = getMeeting(meetingEvent.getRefId());

			String newPath = user.getPath() + "/lib/meetings/"
					+ meeting.getId() + "_" + Math.random();
			if (!session.itemExists(user.getPath() + "/lib/meetings/")) {
				ocm.insert(new JcrNode(user.getPath() + "/lib"));
				ocm.insert(new JcrNode(user.getPath() + "/lib/meetings"));
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

	public Meeting updateCustomMeeting(Troop user, MeetingE meetingEvent,
			Meeting meeting) {

		Session session = null;
		try {

			if (!hasAccess(user, user.getCurrentTroop(),
					Permission.PERMISSION_UPDATE_MEETING_ID)) {
				user.setErrCode("112");
				return null;
			}

			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(MeetingE.class);
			classes.add(Meeting.class);
			classes.add(Activity.class);
			classes.add(JcrCollectionHoldString.class);
			classes.add(JcrNode.class);
			classes.add(Asset.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);

			if (meeting == null)
				meeting = getMeeting(meetingEvent.getRefId());

			String newPath = meetingEvent.getRefId();// user.getPath()+"/lib/meetings/"+meeting.getId()+"_"+Math.random();

			if (!session.itemExists(user.getPath() + "/lib/meetings/")) {
				ocm.insert(new JcrNode(user.getPath() + "/lib"));
				ocm.insert(new JcrNode(user.getPath() + "/lib/meetings"));
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

	public Meeting addActivity(Troop user, Meeting meeting, Activity activity) {

		if (!hasAccess(user, user.getCurrentTroop(),
				Permission.PERMISSION_CREATE_ACTIVITY_ID)) {
			user.setErrCode("112");
			return null;
		}

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

			filter.setScope("/content/girlscouts-vtk/meetings/myyearplan/brownie/");
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

	public java.util.List<String> doSpellCheck(String word) throws Exception {

		Session session = null;
		java.util.List<String> suggest = new java.util.ArrayList();

		try {
			session = sessionFactory.getSession();
			javax.jcr.query.QueryManager qm = (javax.jcr.query.QueryManager) session
					.getWorkspace().getQueryManager();

			javax.jcr.query.Query query = qm
					.createQuery(
							"SELECT rep:spellcheck() FROM nt:base WHERE jcr:path = '/content/dam/' AND SPELLCHECK('"
									+ word + "')", javax.jcr.query.Query.SQL);

			RowIterator rows = query.execute().getRows();
			// the above query will always return the root node no matter what
			// string we check
			Row r = rows.nextRow();
			// get the result of the spell checking

			Value v = r.getValue("rep:spellcheck()");
			if (v == null) {
				// no suggestion returned, the spelling is correct or the spell
				// checker
				// does not know how to correct it.
			} else {
				String suggestion = v.getString();
				suggest.add(suggestion);
			}

			Value values[] = r.getValues();
			for (int i = 0; i < values.length; i++) {
				suggest.add(values[i].getString());
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
		return suggest;
	}

	public List<org.girlscouts.vtk.models.Search> getData(Troop user,
			String _query) {
		Session session = null;

		List<org.girlscouts.vtk.models.Search> matched = null;
		if (!hasPermission(user, Permission.PERMISSION_SEARCH_MEETING_ID))
			return matched;

		final String RESOURCES_PATH = "resources";
		String councilId = null;
		if (user.getTroop() != null) {
			councilId = Integer.toString(user.getTroop().getCouncilCode());
		}
		String branch = councilMapper.getCouncilBranch(councilId);
		String resourceRootPath = branch + "/en/" + RESOURCES_PATH;
		matched = new ArrayList<org.girlscouts.vtk.models.Search>();
		try {
			session = sessionFactory.getSession();
			java.util.Map<String, String> map = new java.util.HashMap<String, String>();
			map.put("fulltext", _query);
			map.put("group.2_path", "/content/dam/girlscouts-vtk/global/aid");
			map.put("group.1_path", resourceRootPath);
			map.put("group.p.or", "true"); // combine this group with OR
			map.put("p.offset", "0"); // same as query.setStart(0) below
			map.put("p.limit", "2000"); // same as query.setHitsPerPage(20)
										// below
			com.day.cq.search.Query query = qBuilder.createQuery(
					PredicateGroup.create(map), session);
			query.setExcerpt(true);
			java.util.Map<String, org.girlscouts.vtk.models.Search> unq = new java.util.TreeMap();
			SearchResult result = query.getResult();

			for (Hit hit : result.getHits()) {
				try {
					String path = hit.getPath();

					java.util.Map<String, String> caca = hit.getExcerpts();
					java.util.Iterator itr = caca.keySet().iterator();
					while (itr.hasNext()) {
						String str = (String) itr.next();
						String str1 = caca.get(str);
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

	public java.util.List<Asset> getAids(String tags, String meetingName,
			String uids) {
		java.util.List<Asset> container = new java.util.ArrayList();
		container.addAll(getAidTag_local(tags, meetingName));
		container.addAll(getAidTag(tags, meetingName));
		return container;
	}

	private List<Asset> getAidTag(String tags, String meetingName) {
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
					System.err.println("Global Aid Description missing");
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

	private List<Asset> getAidTag_local(String tags, String meetingName) {

		List<Asset> matched = new ArrayList<Asset>();
		Session session = null;
		try {
			String sql = "select dc:description,dc:format, dc:title  from nt:unstructured where  jcr:path like '/content/dam/girlscouts-vtk/local/aid/meetings/"
					+ meetingName + "/%' and jcr:mixinTypes='cq:Taggable'";
			session = sessionFactory.getSession();
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

	private List<Asset> getAidTag_custasset(String uid) {

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

	@SuppressWarnings("unchecked")
	public net.fortuna.ical4j.model.Calendar yearPlanCal(Troop user)
			throws Exception {

		java.util.Map<java.util.Date, YearPlanComponent> sched = new MeetingUtil()
				.getYearPlanSched(user.getYearPlan());
		if (!this.hasPermission(user, Permission.PERMISSION_VIEW_MEETING_ID))
			return null;
		net.fortuna.ical4j.model.Calendar calendar = new net.fortuna.ical4j.model.Calendar();
		calendar.getProperties().add(
				new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
		calendar.getProperties().add(Version.VERSION_2_0);
		calendar.getProperties().add(CalScale.GREGORIAN);
		java.util.Iterator itr = sched.keySet().iterator();
		while (itr.hasNext()) {
			java.util.Date dt = (java.util.Date) itr.next();
			YearPlanComponent _comp = (YearPlanComponent) sched.get(dt);

			Calendar cal = java.util.Calendar.getInstance();
			cal.setTime(dt);

			String desc = "", location = "";

			switch (_comp.getType()) {
			case ACTIVITY:
				Activity a = ((Activity) _comp);
				location = (a.getLocationName() == null ? "" : a
						.getLocationName());
				location += " "
						+ (a.getLocationAddress() == null ? "" : a
								.getLocationAddress().replace("\r", ""));
				desc = ((Activity) _comp).getName();
				break;

			case MEETING:
				Meeting meetingInfo = getMeeting(((MeetingE) _comp).getRefId());
				desc = meetingInfo.getName();
				location = getLocation(user,
						((MeetingE) _comp).getLocationRef());
				break;
			}

			final List events = new ArrayList();
			final VEvent event = new VEvent(new DateTime(cal.getTime()), desc);
			event.getProperties().add(new Description(desc));
			if (location != null)
				event.getProperties()
						.add(new net.fortuna.ical4j.model.property.Location(
								location));

			UidGenerator uidGenerator = new UidGenerator("1");
			event.getProperties().add(uidGenerator.generateUid());
			events.add(event);

			calendar.getComponents().addAll(events);

		}// end while
		return calendar;
	}

	public java.util.List<Asset> getResources(String tags, String meetingName,
			String uids) {
		java.util.List<Asset> container = new java.util.ArrayList();
		container.addAll(getResource_local(tags, meetingName));
		container.addAll(getResource_global(tags, meetingName));
		return container;
	}

	private List<Asset> getResource_global(String tags, String meetingName) {

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

	private List<Asset> getResource_local(String tags, String meetingName) {

		List<Asset> matched = new ArrayList<Asset>();
		Session session = null;
		try {
			String sql = "select dc:description,dc:format, dc:title  from nt:unstructured where  jcr:path like '/content/dam/girlscouts-vtk/local/resource/meetings/"
					+ meetingName + "/%' and jcr:mixinTypes='cq:Taggable'";
			session = sessionFactory.getSession();
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

	public SearchTag searchA(String councilCode) {

		String councilStr = councilMapper.getCouncilBranch(councilCode);
		councilStr = councilStr.replace("/content/", "");
		Session session = null;
		SearchTag tags = new SearchTag();
		try {
			session = sessionFactory.getSession();
			java.util.Map<String, String> regionsMain = searchRegion(councilStr);
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

			if ((categories == null || categories.size() == 0)
					&& (levels == null || levels.size() == 0)) {
				try {
					SearchTag defaultTags = getDefaultTags();
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
				levels.remove("program level");
			}

			tags.setCategories(categories);
			tags.setLevels(levels);
			tags.setRegion(searchRegion(councilStr));

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

	public SearchTag getDefaultTags() {

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
				levels.remove("program level");
			}

			tags.setCategories(categories);
			tags.setLevels(levels);
			tags.setRegion(searchRegion(councilStr));

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

	public java.util.List<Activity> searchA2(Troop user, String tags,
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

			String path = "/content/gateway/en/events/2014/%";
			if (!isTag)
				path = path + "/data";
			else
				path = path + "/jcr:content";

			String councilId = null;
			if (user.getTroop() != null) {
				councilId = Integer.toString(user.getTroop().getCouncilCode());
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

	public java.util.Map<String, String> searchRegion(String councilStr) {
		java.util.Map<String, String> container = new java.util.TreeMap();
		Session session = null;
		try {
			session = sessionFactory.getSession();
			java.util.Map<String, String> categories = new java.util.TreeMap();
			java.util.Map<String, String> levels = new java.util.TreeMap();
			String sql = "select region, start, end from nt:base where jcr:path like '/content/"
					+ councilStr
					+ "/en/events-repository/%' and region is not null";
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
						System.err.println("searchRegion invalid startDate");
					}
					try {
						endDate = r.getValue("end").getDate();
					} catch (Exception e) {
						System.err.println("searchRegion invalid endDate");
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

	public java.util.List<Meeting> getAllMeetings(String gradeLevel) {
		java.util.List<Meeting> meetings = null; // new java.util.ArrayList();
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
			filter.setScope("/content/girlscouts-vtk/meetings/myyearplan/"
					+ gradeLevel + "/");
			Query query = queryManager.createQuery(filter);
			meetings = (List<Meeting>) ocm.getObjects(query);
			Comparator<Meeting> comp = new BeanComparator("position");
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

	public List<Asset> getAllResources(String _path) {
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

	public Asset getAsset(String _path) {
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

	public Council getCouncil(String councilId) {
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
			council = (Council) ocm.getObject("/vtk/" + councilId);
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

	public boolean isCurrentTroopId(Troop troop, String sId) {
		java.util.Date lastUpdate = getLastModif(troop);
		if (lastUpdate != null && troop.getRetrieveTime().before(lastUpdate)) {
			troop.setRefresh(true);
			return false;
		}
		return true;
	}

	private String getLocation(Troop user, String locationId) {

		String fmtLocation = "";
		if (locationId == null
				|| user == null
				|| !this.hasPermission(user,
						Permission.PERMISSION_VIEW_MEETING_ID))
			return fmtLocation;
		try {
			if (user != null && user.getYearPlan() != null
					&& user.getYearPlan().getLocations() != null)
				for (int i = 0; i < user.getYearPlan().getLocations().size(); i++) {
					if (user.getYearPlan().getLocations().get(i).getPath()
							.equals(locationId)) {
						String lName = user.getYearPlan().getLocations().get(i)
								.getName();
						String lAddress = user.getYearPlan().getLocations()
								.get(i).getAddress();
						fmtLocation = (lName == null ? "" : lName) + " "
								+ (lAddress == null ? "" : lAddress);
						break;
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fmtLocation;
	}

	public java.util.List<Activity> searchA1(Troop user, String tags,
			String cat, String keywrd, java.util.Date startDate,
			java.util.Date endDate, String region) {
		java.util.List<Activity> toRet = new java.util.ArrayList();
		Session session = null;
		if (!hasPermission(user, Permission.PERMISSION_SEARCH_MEETING_ID))
			return toRet;
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
				regionSql += " and LOWER(child.region) ='" + region + "'";
			}

			String path = "/content/gateway/en/events/2014/%";
			if (!isTag)
				path = path + "/data";
			else
				path = path + "/jcr:content";

			String councilId = null;
			if (user.getTroop() != null) {
				councilId = Integer.toString(user.getTroop().getCouncilCode());
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
					System.err.println("searchActivity no register url");
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

	public boolean hasPermission(java.util.Set<Integer> myPermissionTokens,
			int permissionId) {
		if (myPermissionTokens != null
				&& myPermissionTokens.contains(permissionId))
			return true;
		return false;
	}

	public boolean hasPermission(Troop user, int permissionId) {
		if (!hasPermission(user.getTroop().getPermissionTokens(), permissionId))
			return false;
		return true;
	}

	public boolean hasAccess(Troop user, String mySessionId, int permissionId) {
		if (!hasPermission(user, permissionId))
			return false;

		if (!isCurrentTroopId(user, mySessionId))
			return false;

		return true;
	}

	public void doX() {
		Session session = null;
		try {
			session = sessionFactory.getSession();
			Node vtkRootNode = session.getNode("/vtk");
			String sql = "select * from nt:unstructured where jcr:path like '/vtk/%/users/%'";
			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();
			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.SQL);
			q.setLimit(1);
			QueryResult result = q.execute();
			for (RowIterator it = result.getRows(); it.hasNext();) {
				Row r = it.nextRow();
				Value excerpt = r.getValue("jcr:path");
				StringTokenizer t = new StringTokenizer(excerpt.getString(),
						"/");
				String vtk = t.nextToken();
				String council = t.nextToken();
				String troop = t.nextToken();
				t.nextToken();
				String user = t.nextToken();
				String from = "/vtk/" + council + "/" + troop + "/users/"
						+ user;
				String to = "/vtk/" + council + "/troops/" + troop;
				String to1 = "/vtk/" + council + "/troops";
				Node x = JcrUtils.getOrCreateByPath(to1, "nt:unstructured",
						session);
				session.move(from, to);
				Node newTroop = session.getNode(to);
				newTroop.setProperty("ocm_classname",
						"org.girlscouts.vtk.models.Troop");
				session.save();
				if (true)
					return;
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

	public java.util.Date getLastModif(Troop troop) {
		Session session = null;
		java.util.Date toRet = null;
		try {
			session = sessionFactory.getSession();
			String sql = "select jcr:lastModified from nt:base where jcr:path = '"
					+ troop.getPath() + "' and jcr:lastModified is not null";
			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();
			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.SQL);
			QueryResult result = q.execute();
			for (RowIterator it = result.getRows(); it.hasNext();) {
				Row r = it.nextRow();
				toRet = new java.util.Date(r.getValue("jcr:lastModified")
						.getLong());
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

	
	public String removeLocation(Troop user, String locationName) {
		Session session =null;
		String locationToRmPath=null;
		try{
			
			
			if( !hasAccess(user, user.getCurrentTroop(), Permission.PERMISSION_CANCEL_MEETING_ID ) ){
				 user.setErrCode("112");
				 return null;
			 }
			
			
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
			ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
		
			
			YearPlan plan = user.getYearPlan();
			List<Location> locations = plan.getLocations();
			for(int i=0;i<locations.size();i++){
				Location location = locations.get(i);
				if( location.getUid().equals(locationName)){
					
					
					ocm.remove(location);
					ocm.save();
					
					
					locationToRmPath= location.getPath() ;
					locations.remove(location);
					
					break;
				}
			}
		}catch (Exception e) {
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
	
	
}// edn class
