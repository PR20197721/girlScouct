package org.girlscouts.vtk.ejb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.jcr.Session;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.ocm.lock.Lock;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.apache.jackrabbit.ocm.query.Filter;
import org.apache.jackrabbit.ocm.query.Query;
import org.apache.jackrabbit.ocm.query.QueryManager;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.dao.ActivityDAO;
import org.girlscouts.vtk.dao.CouncilDAO;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.dao.TroopDAO;
import org.girlscouts.vtk.dao.TroopDAO;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.Council;
import org.girlscouts.vtk.models.JcrNode;
import org.girlscouts.vtk.models.Location;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.models.Troop;

import org.girlscouts.vtk.models.UserGlobConfig;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.Asset;

@Component
@Service(value = TroopDAO.class)
public class TroopDAOImpl implements TroopDAO {

	@Reference
	private SessionFactory sessionFactory;
	
	@Reference
	private MeetingDAO meetingDAO;
	
	@Reference
	private CouncilDAO councilDAO;
	
	@Reference
	private ActivityDAO activityDAO;
	
	private static UserGlobConfig troopGlobConfig;

	@Activate
	void activate() {}
	
	public Troop getTroop(String councilId, String troopId) {

		Session mySession =null;
		Troop troop = null;
		try {
			mySession = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Troop.class);
			classes.add(YearPlan.class);
			classes.add(MeetingE.class);
			classes.add(Activity.class);
			classes.add(Location.class);
			classes.add(Asset.class);
			classes.add(Cal.class);
			classes.add(Milestone.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);

			ocm.refresh(true);
			troop = (Troop) ocm.getObject( "/vtk/"+ councilId +"/troops/"+ troopId);
			if( troop!=null)
				troop.setRetrieveTime( new java.util.Date() );
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				sessionFactory.closeSession(mySession);
			} catch (Exception es){es.printStackTrace();}
		}

		return troop;
	}
	
	
	public Troop getTroop_byPath(String troopPath) {
		Session mySession =null;
		Troop troop = null;
		try {
			mySession = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Troop.class);
			classes.add(YearPlan.class);
			classes.add(MeetingE.class);
			classes.add(Activity.class);
			classes.add(Location.class);
			classes.add(Asset.class);
			classes.add(Cal.class);
			classes.add(Milestone.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);

			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(Troop.class);

			ocm.refresh(true);
			troop = (Troop) ocm.getObject( troopPath);

			if (troop != null && troop.getYearPlan().getMeetingEvents() != null) {

				Comparator<MeetingE> comp = new BeanComparator("id");
				Collections.sort(troop.getYearPlan().getMeetingEvents(), comp);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				sessionFactory.closeSession(mySession);
			}catch(Exception es){es.printStackTrace();}
		}

		return troop;
	}

	
	public YearPlan addYearPlan(Troop troop, String yearPlanPath) throws java.lang.IllegalAccessException {

		//permission to update
		if( troop!= null && ! meetingDAO.hasPermission(troop, Permission.PERMISSION_ADD_YEARPLAN_ID) )
			throw new IllegalAccessException();
		
		if (!meetingDAO.isCurrentTroopId(troop, troop.getCurrentTroop())) {
			troop.setErrCode("112");
			return null;
		}
		
		Session mySession =null;
		String fmtYearPlanPath = yearPlanPath;
		YearPlan plan = null;
		try {

			mySession = sessionFactory.getSession();
			if (!yearPlanPath.endsWith("/"))
				yearPlanPath = yearPlanPath + "/";

			yearPlanPath += "meetings/";

			List<Class> classes = new ArrayList<Class>();
			classes.add(Troop.class);
			classes.add(YearPlan.class);
			classes.add(MeetingE.class);
			classes.add(Cal.class);
			classes.add(Milestone.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);
			QueryManager queryManager = ocm.getQueryManager();

			Filter filter = queryManager.createFilter(YearPlan.class);
			plan = (YearPlan) ocm.getObject(fmtYearPlanPath);

			plan.setRefId(yearPlanPath);
			plan.setMeetingEvents(meetingDAO
					.getAllEventMeetings_byPath(yearPlanPath));

			Comparator<MeetingE> comp = new BeanComparator("id");
			Collections.sort(plan.getMeetingEvents(), comp);

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				sessionFactory.closeSession(mySession);
			}catch(Exception es){es.printStackTrace();}
		}

		return plan;

	}

	public boolean updateTroop(Troop troop) throws java.lang.IllegalAccessException {
		
		Session mySession =null;
		boolean isUpdated = false;
		try {
			
			if (troop == null || troop.getYearPlan() == null) {
				return true;
			}
			troop.setErrCode("111");

			
			mySession = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Troop.class);
			classes.add(YearPlan.class);
			classes.add(MeetingE.class);
			classes.add(Location.class);
			classes.add(Cal.class);
			classes.add(Activity.class);
			classes.add(Asset.class);
			classes.add(JcrNode.class);
			classes.add(Milestone.class);
			classes.add(Council.class);
			classes.add(org.girlscouts.vtk.models.Troop.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);

			Comparator<MeetingE> comp = new BeanComparator("id");
			Collections.sort(troop.getYearPlan().getMeetingEvents(), comp);

			//permission to update
			if( troop!= null && ! meetingDAO.hasPermission(troop, Permission.PERMISSION_VIEW_YEARPLAN_ID) )
				throw new IllegalAccessException();
			
			//lock
			if (troop != null && troop.getLastModified() != null) {
					if (!meetingDAO.isCurrentTroopId(troop, troop.getCurrentTroop())) {
								troop.setErrCode("112");
								//throw new IllegalStateException();
								return false;
							}
			}
		
	       if (mySession.itemExists(troop.getPath())) {
				ocm.update(troop);
			} else {
				String path = "";
				StringTokenizer t = new StringTokenizer(
						("/" + troop.getPath())
								.replace("/" + troop.getId(), ""),
						"/");
				int i = 0;
				while (t.hasMoreElements()) {
					String node = t.nextToken();
					path += "/" + node;
					if (!mySession.itemExists(path)) {
						if (i == 1) {
							ocm.insert(new Council(path));
						} else {
							ocm.insert(troop);
						}			
					}
					i++;
				}
				ocm.insert(troop);
			}

			String old_errCode = troop.getErrCode();
			java.util.Calendar old_lastModified = troop.getLastModified();
			try {
				troop.setErrCode(null);
				troop.setLastModified(java.util.Calendar.getInstance());
				ocm.update(troop);
				ocm.save();
				isUpdated = true;
			} catch (Exception e) {
				e.printStackTrace();
				troop.setLastModified(old_lastModified);
				troop.setErrCode(old_errCode);
				troop.setRefresh(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				sessionFactory.closeSession(mySession);
			}catch(Exception es){es.printStackTrace();}
		}
		return isUpdated;
	}

	public void selectYearPlan(Troop troop, String yearPlanPath, String planName) throws java.lang.IllegalAccessException{
		
		//permission to update
		if( troop!= null && ! meetingDAO.hasPermission(troop, Permission.PERMISSION_ADD_YEARPLAN_ID) )
			throw new IllegalAccessException();
		
		if (!meetingDAO.isCurrentTroopId(troop, troop.getCurrentTroop())) {
			troop.setErrCode("112");
			return;
		}

		YearPlan oldPlan = troop.getYearPlan();
		YearPlan newYearPlan = addYearPlan(troop, yearPlanPath);
		try {

			newYearPlan.setName(planName);
			if (oldPlan == null || oldPlan.getMeetingEvents() == null
					|| oldPlan.getMeetingEvents().size() <= 0
					|| oldPlan.getSchedule() == null
					|| oldPlan.getSchedule().getDates().equals("")) { 
				troop.setYearPlan(newYearPlan);
			} else if (oldPlan.getSchedule() != null) { 
				String oldDates = oldPlan.getSchedule().getDates();
				int count = 0;
				java.util.StringTokenizer t = new java.util.StringTokenizer(
						oldDates, ",");

				// if number of dates less then new meetings
				if (t.countTokens() < newYearPlan.getMeetingEvents().size()) {
					int countDates = t.countTokens();
					long lastDate = 0, meetingTimeDiff = 99999;
					while (t.hasMoreElements()) {
						long diff = lastDate;
						lastDate = Long.parseLong(t.nextToken());
						if (diff != 0)
							meetingTimeDiff = lastDate - diff;
					}
					for (int z = countDates; z < newYearPlan.getMeetingEvents()
							.size(); z++)
						oldDates += (lastDate + meetingTimeDiff) + ",";
					    oldPlan.getSchedule().setDates(oldDates);
					    t = new java.util.StringTokenizer(oldDates, ",");
				}

				while (t.hasMoreElements()) {
					long date = Long.parseLong(t.nextToken());

					if (count >= newYearPlan.getMeetingEvents().size()) {
						// rm all other dates
						oldPlan.getSchedule().setDates(
								oldPlan.getSchedule()
										.getDates()
										.substring(
												0,
												oldPlan.getSchedule()
														.getDates()
														.indexOf("" + date)));

						// TODO re write
						java.util.List<MeetingE> toBeRm = new java.util.ArrayList();
						for (int i = count; i < oldPlan.getMeetingEvents()
								.size(); i++)
							toBeRm.add(oldPlan.getMeetingEvents().get(i));

						for (int i = 0; i < toBeRm.size(); i++)
							oldPlan.getMeetingEvents().remove(toBeRm.get(i));

						break;
					} else if (new java.util.Date().before(new java.util.Date(
							date))) {

						if (count >= oldPlan.getMeetingEvents().size()) 
							oldPlan.getMeetingEvents().add(
									newYearPlan.getMeetingEvents().get(count));
						else
							// replace meeting refs
							oldPlan.getMeetingEvents()
									.get(count)
									.setRefId(
											newYearPlan.getMeetingEvents()
													.get(count).getRefId());

						oldPlan.getMeetingEvents().get(count)
								.setCancelled("false");

					}
					count++;
				}

			} else {

				oldPlan.setMeetingEvents(newYearPlan.getMeetingEvents());
				troop.setYearPlan(oldPlan);
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				System.err
						.println("Error setting new Plan: dumping old plan and replacing with new Plan");
				troop.setYearPlan(newYearPlan);
			} catch (Exception ew) {
				ew.printStackTrace();
			}
		}

		// remove all PAST custom activitites
		if (troop.getYearPlan().getActivities() != null) {
			java.util.List<Activity> activityToRm = new java.util.ArrayList();
			for (int i = 0; i < troop.getYearPlan().getActivities().size(); i++)
				if (new java.util.Date().before(troop.getYearPlan()
						.getActivities().get(i).getDate()))
					activityToRm
							.add(troop.getYearPlan().getActivities().get(i));

			for (int i = 0; i < activityToRm.size(); i++)
				troop.getYearPlan().getActivities().remove(activityToRm.get(i));
		}// end if

		troop.getYearPlan().setAltered("false");
		troop.getYearPlan().setName(planName);
		updateTroop(troop);

	}

	public void rmTroop(Troop troop)throws IllegalAccessException {
		Session mySession =null;
		try {
			
			//permission to update
			if( troop!= null && ! meetingDAO.hasPermission(troop, Permission.PERMISSION_RM_YEARPLAN_ID) )
				throw new IllegalAccessException();
			
			mySession= sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Troop.class);
			classes.add(YearPlan.class);
			classes.add(MeetingE.class);
			classes.add(Location.class);
			classes.add(Cal.class);
			classes.add(Activity.class);
			classes.add(Asset.class);
			classes.add(Milestone.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);
			ocm.remove(troop);
			ocm.save();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				sessionFactory.closeSession(mySession);
			}catch(Exception e){e.printStackTrace();}
		}
	}

	public void addAsset(Troop troop, String meetingUid, Asset asset) throws java.lang.IllegalAccessException{

		//permission to update
		if( troop!= null && ! meetingDAO.hasPermission(troop, Permission.PERMISSION_EDIT_YEARPLAN_ID) )
			throw new IllegalAccessException();
		
		if (!meetingDAO.isCurrentTroopId(troop, troop.getCurrentTroop())) {
			troop.setErrCode("112");
			return;
		}
		java.util.List<MeetingE> meetings = troop.getYearPlan()
				.getMeetingEvents();
		for (int i = 0; i < meetings.size(); i++)
			if (meetings.get(i).getUid().equals(meetingUid))
				meetings.get(i).getAssets().add(asset);

		updateTroop(troop);
	}

	public UserGlobConfig getUserGlobConfig() {

			loadUserGlobConfig();

			if (troopGlobConfig == null) {
				createUserGlobConfig();
				loadUserGlobConfig();
			}
		
		return troopGlobConfig;
	}

	public void loadUserGlobConfig() {

		troopGlobConfig = new UserGlobConfig();
		Session mySession =null;
		try {
			mySession = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(UserGlobConfig.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);

			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(UserGlobConfig.class);

			troopGlobConfig = (UserGlobConfig) ocm
					.getObject("/vtk/global-settings");

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				sessionFactory.closeSession(mySession);
			}catch(Exception es){es.printStackTrace();}
		}

	}

	public void createUserGlobConfig() {

		Session mySession =null;
		try {
			mySession= sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(UserGlobConfig.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);

			UserGlobConfig troopGlobConfig = new UserGlobConfig();
			ocm.insert(troopGlobConfig);
			ocm.save();

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				sessionFactory.closeSession(mySession);
			}catch(Exception es){es.printStackTrace();}
		}

	}

	public void updateUserGlobConfig() {
		Session mySession =null;
		try {
			mySession = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(UserGlobConfig.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);

			if (mySession.itemExists(troopGlobConfig.getPath())) {
				ocm.update(troopGlobConfig);
			} else {
				ocm.insert(troopGlobConfig);
			}
			ocm.save();

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				sessionFactory.closeSession(mySession);
			}catch(Exception es){es.printStackTrace();}
		}

	}

	public java.util.List getTroops() {
		
		Session mySession =null;
		java.util.List<Troop> troops = null;
		try {
			mySession =sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Troop.class);
			classes.add(YearPlan.class);
			classes.add(MeetingE.class);
			classes.add(Activity.class);
			classes.add(Location.class);
			classes.add(Asset.class);
			classes.add(Cal.class);
			classes.add(Milestone.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);

			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(Troop.class);
			filter.setScope("/vtk//");
			Query query = queryManager.createQuery(filter);
			troops = (java.util.List<Troop>) ocm.getObjects(query);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				sessionFactory.closeSession(mySession);
			}catch(Exception es){es.printStackTrace();}
		}
		return troops;
	}

	public void logout(Troop troop) throws java.lang.IllegalAccessException{
		if (troop == null)
			return;
 		Troop tmp_troop = getTroop_byPath(troop.getPath());
		if (tmp_troop == null)
			return;
		tmp_troop.setCurrentTroop(null);
		updateTroop(tmp_troop);
	}

	
}// ednclass

