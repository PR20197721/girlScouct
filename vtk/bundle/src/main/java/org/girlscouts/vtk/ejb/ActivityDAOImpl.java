package org.girlscouts.vtk.ejb;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

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
import org.girlscouts.vtk.auth.permission.Permission;
//import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.dao.ActivityDAO;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.dao.TroopDAO;
//import org.girlscouts.vtk.dao.UserDAO;
import org.girlscouts.vtk.dao.YearPlanComponentType;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.ActivitySearch;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.JcrCollectionHoldString;
import org.girlscouts.vtk.models.Location;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.YearPlan;

@Component
@Service(value = ActivityDAO.class)
public class ActivityDAOImpl implements ActivityDAO {

	//private Session session;

	@Reference
	private SessionPool pool;

	@Activate
	void activate() {
		//this.session = pool.getSession();
	}

	//@Reference
	//private TroopDAO troopDAO;

	@Reference
	private MeetingDAO meetingDAO;
	
	

	public void createActivity(Troop user, Activity activity) {
		Session session=null;
		try {

			if (!meetingDAO.hasAccess(user, user.getCurrentTroop(),
					Permission.PERMISSION_CREATE_ACTIVITY_ID)) {
				user.setErrCode("112");
				return;
			}
			session = pool.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Troop.class);
			classes.add(Activity.class);
			classes.add(JcrCollectionHoldString.class);
			classes.add(YearPlan.class);
			classes.add(MeetingE.class);
			classes.add(Cal.class);
			classes.add(Location.class);
			classes.add(Asset.class);
			classes.add(Milestone.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);

			YearPlan plan = user.getYearPlan();
			List<Activity> activities = plan.getActivities();
			if (activities == null)
				activities = new java.util.ArrayList<Activity>();

			// add refId path
			String refId = null;
			try {
				refId = getPath(activity.getRefUid());
				if (refId != null)
					activity.setRefUid(refId);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			activities.add(activity);
			plan.setActivities(activities);

			//moved to activityUtil
			//user.getYearPlan().setAltered("true");
			//troopDAO.updateTroop(user);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if( session!=null )
					pool.closeSession(session);
			}catch(Exception ex){ex.printStackTrace();}
		}

	}

	public java.util.List<Activity> search(ActivitySearch search) {

		java.util.List activities = new java.util.ArrayList();

		for (int i = 0; i < 2; i++) {
			Activity activity = new Activity();
			activity.setName("test " + i);
			activity.setContent("ACtivity content " + i);
			activity.setDate(new java.util.Date());
			activity.setType(YearPlanComponentType.ACTIVITY);
			activity.setId("ACT" + i);

			activities.add(activity);
		}
		return activities;

	}

	public boolean isActivity(String uuid) {

		Session session =null;
		
		javax.jcr.Node node = null;
		try {
			session= pool.getSession();
			node = session.getNodeByIdentifier(uuid);

		} catch (Exception e) {
			System.err.println("isActivity:Activity not found");
		}finally{
			try{
				if( session!=null )
					pool.closeSession(session);
			}catch(Exception ex){ex.printStackTrace();}
		}

		if (node != null)
			return true;

		return false;
	}

	public void updateActivitiesCancel(String uuid) {

		if (uuid == null)
			return;

		Session session=null;
		try {
			session = pool.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Activity.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);

			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(Activity.class);
			filter.setScope("/vtk//");
			filter.addEqualTo("refUid", uuid);

			Query query = queryManager.createQuery(filter);
			java.util.List<Activity> activities = (List<Activity>) ocm
					.getObjects(query);

			if (activities != null)
				System.err.println("Found " + activities.size()
						+ "  activities matching uuid " + uuid);
			else
				System.err.println("No activities foudn for uuid " + uuid);

			for (int i = 0; i < activities.size(); i++) {
				Activity a = activities.get(i);
				if (a.getRefUid().equals(uuid)) { // "if" just to double check
					a.setCancelled("true");
					ocm.update(a);
				}
			}

			ocm.save();

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if( session!=null )
					pool.closeSession(session);
			}catch(Exception ex){ex.printStackTrace();}
		}

	}
/*
	public void checkCanceledActivity(Troop user) {

		if (user == null || user.getYearPlan() == null
				|| user.getYearPlan().getActivities() == null
				|| user.getYearPlan().getActivities().size() == 0)
			return;

		java.util.List<Activity> activity2Cancel = new java.util.ArrayList<Activity>();

		java.util.List<Activity> activities = user.getYearPlan()
				.getActivities();
		for (int i = 0; i < activities.size(); i++) {

			if (!(activities.get(i).getCancelled() != null && activities.get(i)
					.getCancelled().equals("true")))
				if (!isActivityByPath(activities.get(i).getRefUid())) {
					activities.get(i).setCancelled("true"); // org
					activity2Cancel.add(activities.get(i));
					troopDAO.updateTroop(user);
				}
		}

		for (Activity a : activity2Cancel)
			if (activities.contains(a))
				activities.remove(a);
	}
*/
	private String getPath(String uuid) {

		String path = null;
		javax.jcr.Node node = null;
		Session session =null;
		try {
			session = pool.getSession();
			node = session.getNodeByIdentifier(uuid);
			if (node != null)
				path = node.getPath().replace("/jcr:content", "");
		} catch (Exception e) {
			System.err.println("isActivity:Activity not found");
		}finally{
			try{
				if( session!=null )
					pool.closeSession(session);
			}catch(Exception ex){ex.printStackTrace();}
		}

		return path;
	}

	public boolean isActivityByPath(String path) {

		Session session = null;
		boolean isActivity = true;
		try {
			
			String sql = "select jcr:path from nt:base where jcr:path = '"
					+ path + "'";
			
			session = pool.getSession();
			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();
			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.SQL);
			QueryResult result = q.execute();

			boolean isFound = false;
			for (RowIterator it = result.getRows(); it.hasNext();) {
				Row r = it.nextRow();
				Value excerpt = r.getValue("jcr:path");
				if (excerpt.getString().equals(path)) {
					isActivity = true;
					isFound = true;
				} else
					isFound = false;

			}
			if (!isFound)
				isActivity = false;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if( session!=null )
					pool.closeSession(session);
			}catch(Exception ex){ex.printStackTrace();}
		}

		return isActivity;
	}

	public Activity findActivity(String path) {
		Activity activity = null;
		Session session =null;
		try {

			String sql = "select child.register, child.address, parent.[jcr:uuid], child.start, parent.[jcr:title], child.details, child.end,child.locationLabel,child.srchdisp  from [nt:base] as parent INNER JOIN [nt:base] as child ON ISCHILDNODE(child, parent) where  (isdescendantnode (parent, ['"
					+ path
					+ "'])) and child.start is not null and parent.[jcr:title] is not null ";

			System.err.println(sql);
			session = pool.getSession();
			javax.jcr.query.QueryManager qm = session.getWorkspace()
					.getQueryManager();
			javax.jcr.query.Query q = qm.createQuery(sql,
					javax.jcr.query.Query.JCR_SQL2);

			int i = 0;
			QueryResult result = q.execute();

			for (RowIterator it = result.getRows(); it.hasNext();) {
				Row r = it.nextRow();
System.err.println("Found activvvvv");
				Value v[] = r.getValues();

				activity = new Activity();
				activity.setUid("A" + new java.util.Date().getTime() + "_"
						+ Math.random());

				activity.setContent(r.getValue("child.details").getString());
				activity.setDate(r.getValue("child.start").getDate().getTime());
				try {
					activity.setEndDate(r.getValue("child.end").getDate()
							.getTime());
				} catch (Exception e) {
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
					activity.setRefUid(path);
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					activity.setRegisterUrl(r.getValue("child.register")
							.getString());
				} catch (Exception e) {
				}
				i++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if( session!=null )
					pool.closeSession(session);
			}catch(Exception ex){ex.printStackTrace();}
		}

		return activity;
	}

}
