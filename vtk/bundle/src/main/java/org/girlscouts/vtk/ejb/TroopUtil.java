package org.girlscouts.vtk.ejb;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import javax.jcr.Session;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.apache.jackrabbit.ocm.query.Filter;
import org.apache.jackrabbit.ocm.query.QueryManager;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.dao.ActivityDAO;
import org.girlscouts.vtk.dao.CouncilDAO;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.dao.TroopDAO;
import org.girlscouts.vtk.dao.YearPlanDAO;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.Council;
import org.girlscouts.vtk.models.JcrCollectionHoldString;
import org.girlscouts.vtk.models.Location;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.UserGlobConfig;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.SentEmail;
import org.girlscouts.vtk.utils.VtkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service(value = TroopUtil.class)
public class TroopUtil {
	private final Logger log = LoggerFactory.getLogger("vtk");
	@Reference
	TroopDAO troopDAO;

	@Reference
	ActivityDAO activityDAO;

	@Reference
	private UserUtil userUtil;

	@Reference
	CouncilDAO councilDAO;

	@Reference
	MeetingDAO meetingDAO;

	@Reference
	YearPlanDAO yearPlanDAO;

	@Reference
	private YearPlanUtil yearPlanUtil;

	public Troop getTroop(User user, String councilId, String troopId)
			throws IllegalAccessException {

		Troop troop = null;
		troop = troopDAO.getTroop(user, councilId, troopId);
		if (troop == null)
			return troop;
		if (troop != null && troop.getYearPlan() != null
				&& troop.getYearPlan().getMeetingEvents() != null) {

			Comparator<MeetingE> comp = new BeanComparator("id");
			Collections.sort(troop.getYearPlan().getMeetingEvents(), comp);

			for (int i = 0; i < troop.getYearPlan().getMeetingEvents().size(); i++) {
				troop.getYearPlan().getMeetingEvents().get(i).setId(i);
			}

		}

		if (troop.getYearPlan() == null)
			return troop;

		yearPlanUtil.checkCanceledActivity(user, troop);

		if (troop.getYearPlan() != null
				&& troop.getYearPlan().getCalFreq() == null)
			troop.getYearPlan().setCalFreq("biweekly");

		
		doDbReset(troop);
		
		System.err.println("tata Troop reset...");
		
		/*
		//TODO
		troop.getYearPlan().getSchedule().setUpdated(false);
		java.util.List<MeetingE> _meetingsE = troop.getYearPlan().getMeetingEvents();
		for(int i=0;i<_meetingsE.size();i++)
			_meetingsE.get(i).setUpdated(false);
		*/
		
		return troop;

	}
	
	
	
	private void doDbReset(Troop troop){
			if( troop!=null){
				troop.setDbUpdate(false);
				if( troop.getYearPlan()!=null){
					troop.getYearPlan().setDbUpdate(false);
					
					if( troop.getYearPlan().getSchedule()!=null )
						troop.getYearPlan().getSchedule().setDbUpdate(false);
					
					if( troop.getYearPlan().getLocations()!=null ){
						for(int i=0;i<troop.getYearPlan().getLocations().size();i++)
							troop.getYearPlan().getLocations().get(i).setDbUpdate(false);
					}
					
					if( troop.getYearPlan().getActivities()!=null ){
						for(int i=0;i<troop.getYearPlan().getActivities().size();i++ ){
							troop.getYearPlan().getActivities().get(i).setDbUpdate(false);
						}
					}
					
					if( troop.getYearPlan().getMeetingEvents()!=null ){
						for(int i=0;i<troop.getYearPlan().getMeetingEvents().size();i++ ){
							troop.getYearPlan().getMeetingEvents().get(i).setDbUpdate(false);
							java.util.List<Asset> assets = troop.getYearPlan().getMeetingEvents().get(i).getAssets();
							if( assets!=null)
							 for(int y=0;y<assets.size();y++)
								assets.get(y).setDbUpdate(false);
						}
					}
					
				}
			}
			
	}

	// check if info was updated and need to pull from DB fresh copy
	public boolean isUpdated(Troop troop) {

		java.util.Date lastUpdate = yearPlanDAO.getLastModif(troop);
		if (lastUpdate != null && troop.getRetrieveTime().before(lastUpdate)) {
			troop.setRefresh(true);
			return true;
		}

		return false;

	}

	public void autoLogin(HttpSession session) {

		java.util.Set<Integer> s = new HashSet<Integer>(
				Arrays.asList(new Integer[] { 0, 100, 200, 140, 110, 230, 10,
						400, 280, 11, 130, 12, 220, 290, 250, 260, 270, 300,
						210, 240, 120 }));

		org.girlscouts.vtk.auth.models.ApiConfig config = new org.girlscouts.vtk.auth.models.ApiConfig();
		config.setId("test");
		config.setAccessToken("test");
		config.setInstanceUrl("test");
		config.setUserId("test_user");
		config.setUser(new org.girlscouts.vtk.auth.models.User());

		java.util.List<org.girlscouts.vtk.salesforce.Troop> troops = new java.util.ArrayList();
		org.girlscouts.vtk.salesforce.Troop troop = new org.girlscouts.vtk.salesforce.Troop();
		troop.setCouncilCode(603);
		troop.setGradeLevel("1-Daisy");
		troop.setTroopId("701Z0000000gvRvIAI");
		troop.setCouncilId("603");
		troop.setTroopName("Troop00960");
		troop.setPermissionTokens(s);
		troops.add(troop);
		config.setTroops(troops);

		session.setAttribute(
				org.girlscouts.vtk.auth.models.ApiConfig.class.getName(),
				config);

		User user = new User();
		user.setApiConfig(config);
		user.setPermissions(s);
		user.setCurrentYear("2014");
		session.setAttribute(org.girlscouts.vtk.models.User.class.getName(),
				user);
	}

	public Troop createTroop(User user, String councilId, String troopId)
			throws IllegalAccessException {

		Troop troop = null;
		Council council = councilDAO.getOrCreateCouncil(user, councilId);
		if (council == null)
			return null;

		/*
		 * java.util.List<Troop> troops = council.getTroops(); if (troops ==
		 * null) troops = new java.util.ArrayList<Troop>(); troop = new
		 * Troop(troopId); troops.add(troop); council.setTroops(troops);
		 * 
		 * councilDAO.updateCouncil(user, council);
		 */
		troop = new Troop(troopId);
		troop.setPath("/vtk/" + councilId + "/troops/" + troopId);
		return troop;

	}

	public void logout(User user, Troop troop)
			throws java.lang.IllegalAccessException {

	}

	public void addAsset(User user, Troop troop, String meetingUid, Asset asset)
			throws java.lang.IllegalAccessException,
			java.lang.IllegalStateException {

		// permission to update
		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_EDIT_YEARPLAN_ID))
			throw new IllegalAccessException();

		if (!userUtil.isCurrentTroopId(troop, user.getSid())) {
			troop.setErrCode("112");
			throw new IllegalStateException();
			// return;
		}
		java.util.List<MeetingE> meetings = troop.getYearPlan()
				.getMeetingEvents();
		for (int i = 0; i < meetings.size(); i++)
			if (meetings.get(i).getUid().equals(meetingUid))
				meetings.get(i).getAssets().add(asset);

		troopDAO.updateTroop(user, troop);

	}

	
	public void selectYearPlan_vtk1(User user, Troop troop, String yearPlanPath,
			String planName) throws java.lang.IllegalAccessException, VtkYearPlanChangeException {
System.err.println("tata selecteYearPlan start");




		// permission to update
		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_ADD_YEARPLAN_ID))
			throw new IllegalAccessException();

		if (!userUtil.isCurrentTroopId(troop, user.getSid())) {
			troop.setErrCode("112");
			return;
		}

		YearPlan oldPlan = troop.getYearPlan();
		
		if(oldPlan!=null && oldPlan.getMeetingEvents()!=null)
    		for(int i=0;i<oldPlan.getMeetingEvents().size();i++)
				System.err.println("tata oldPlan:"+((MeetingE)oldPlan.getMeetingEvents().get(i)).getRefId());
		
		
		
		YearPlan newYearPlan = addYearPlan(user, troop, yearPlanPath);// troopDAO.addYearPlan1(troop,

		
		
		// yearPlanPath);
		for(int i=0;i<newYearPlan.getMeetingEvents().size();i++)
			System.err.println("tata newPlan: pos: "+((MeetingE)newYearPlan.getMeetingEvents().get(i)).getId() +" : "+((MeetingE)newYearPlan.getMeetingEvents().get(i)).getRefId());
		
		
		
		
		System.err.println("tata selecteYearPlan start 1");
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
	System.err.println("tata chk lastDate: "+ new java.util.Date(lastDate) +" : "+ meetingTimeDiff);				
					for (int z = countDates; z < newYearPlan.getMeetingEvents().size(); z++){
						if(!oldDates.endsWith(",")) 
							oldDates+=",";
						oldDates +=  (lastDate + meetingTimeDiff) + ",";
	System.err.println("tata adding sched date: "+ new java.util.Date((lastDate + meetingTimeDiff))  +" : "+ meetingTimeDiff);					
	System.err.println("tata new sched : "+ oldDates);
					}
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

						oldPlan.getMeetingEvents().get(count).setCancelled("false");

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
				// log.e("Error setting new Plan: dumping old plan and replacing with new Plan");
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
		
		/*
		java.util.List<MeetingE> m = troop.getYearPlan().getMeetingEvents();
		for(int i=0;i<m.size();i++){
			m.get(i).setDbUpdate(true);
			
			if( m.get(i).getMeetingInfo()!=null )
				System.err.println("tata m:"+m.get(i).getMeetingInfo().getName());
		}
		*/
		for(int i=0;i<troop.getYearPlan().getMeetingEvents().size();i++){
			System.err.println("tata newPlan pos "+ ((MeetingE) troop.getYearPlan().getMeetingEvents().get(i)).getId() +" :"+((MeetingE) troop.getYearPlan().getMeetingEvents().get(i)).getRefId() + " : "+
					((MeetingE) troop.getYearPlan().getMeetingEvents().get(i)).isDbUpdate() );
			//xtroopDAO.removeMeetings(user, troop, troop.getYearPlan().getMeetingEvents());
		}
		
		//if(oldPlan!=null && oldPlan.getMeetingEvents()!=null)
    	//	for(int i=0;i<oldPlan.getMeetingEvents().size();i++)
		troopDAO.removeMeetings(user, troop);
		
		
		for(int i=0;i<troop.getYearPlan().getMeetingEvents().size();i++){
			System.err.println("tata newPlanXXX pos "+ ((MeetingE) troop.getYearPlan().getMeetingEvents().get(i)).getId() +" :"+((MeetingE) troop.getYearPlan().getMeetingEvents().get(i)).getRefId() + " : "+
					((MeetingE) troop.getYearPlan().getMeetingEvents().get(i)).isDbUpdate() );
			
		}
	System.err.println("tataXX: "+ troop.getYearPlan().getSchedule());	
		if( troop.getYearPlan().getSchedule()!=null){
System.err.println("tata2b2b2 :"+ (troop.getYearPlan().getMeetingEvents().size() > oldPlan.getMeetingEvents().size()));			
			if( oldPlan.getMeetingEvents()!= null && troop.getYearPlan().getMeetingEvents().size() > oldPlan.getMeetingEvents().size() ){
	System.err.println("tata1c1");			
				java.util.Calendar test = java.util.Calendar.getInstance();
				test.setTimeInMillis( new java.util.Date("2/2/2016").getTime() );
				for(int i=0;i< (troop.getYearPlan().getMeetingEvents().size()- oldPlan.getMeetingEvents().size() ) ;i++){
					//add date to sched
					troop.getYearPlan().getSchedule().setDates(troop.getYearPlan().getSchedule().getDates() + ", " + test.getTimeInMillis() +",");
	System.err.println("tata ,.... "+ troop.getYearPlan().getSchedule().getDates());				
					test.add(java.util.Calendar.DATE, 7);
					
					
				}
			}
			troop.getYearPlan().getSchedule().setDbUpdate(true);
		}
		
		
		
		troopDAO.updateTroop(user, troop);
		System.err.println("tata selecteYearPlan end ************ ");
	}

	
	
	
	public boolean updateTroop(User user, Troop troop)
			throws java.lang.IllegalAccessException,
			java.lang.IllegalAccessException {

		return troopDAO.updateTroop(user, troop);

	}

	public void rmTroop(Troop troop) throws java.lang.IllegalAccessException,
			java.lang.IllegalAccessException {
		troopDAO.rmTroop(troop);
	}

	public UserGlobConfig getUserGlobConfig() {
		return troopDAO.getUserGlobConfig();
	}

	public YearPlan addYearPlan(User user, Troop troop, String yearPlanPath)
			throws java.lang.IllegalAccessException,
			java.lang.IllegalAccessException {
		YearPlan plan = null;
		try {
			plan = troopDAO.addYearPlan1(user, troop, yearPlanPath);
			plan.setRefId(yearPlanPath);

			plan.setMeetingEvents(yearPlanUtil.getAllEventMeetings_byPath(user,
					yearPlanPath.endsWith("/meetings/") ? yearPlanPath
							: (yearPlanPath + "/meetings/")));
			
			Comparator<MeetingE> comp = new BeanComparator("id");
			Collections.sort(plan.getMeetingEvents(), comp);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return plan;

	}

	public void reLogin(User user, Troop troop, String troopId,
			HttpSession session) throws IllegalAccessException {

		if (troopId == null || troopId.trim().equals("")) {
			log.error("loginAs invalid.abort");
			return;
		}

		java.util.List<org.girlscouts.vtk.salesforce.Troop> troops = user
				.getApiConfig().getTroops();
		session.setAttribute("USER_TROOP_LIST", troops);

		org.girlscouts.vtk.salesforce.Troop newTroop = null;
		for (int i = 0; i < troops.size(); i++) {
			if (troops.get(i).getTroopId().equals(troopId)) {
				newTroop = troops.get(i);
			}
		}

		Troop new_troop = getTroop(user, newTroop.getCouncilCode() + "",
				troopId);

		if (new_troop == null) {
			new_troop = createTroop(user, "" + newTroop.getCouncilCode(),
					troopId);

		}
		new_troop.setTroop(newTroop);
		new_troop.setSfTroopId(new_troop.getTroop().getTroopId());
		new_troop.setSfUserId(user.getApiConfig().getUserId());
		new_troop.setSfTroopName(new_troop.getTroop().getTroopName());
		new_troop.setSfTroopAge(new_troop.getTroop().getGradeLevel());
		new_troop.setSfCouncil(new_troop.getTroop().getCouncilCode() + "");

		// logout multi troop
		logout(user, troop);

		session.setAttribute("VTK_troop", new_troop);
		session.putValue("VTK_planView_memoPos", null);
		session.setAttribute("vtk_cachable_contacts", null);
		// new_troop.setCurrentTroop( session.getId() );
		// updateTroop(user, new_troop);

	}

	public String bindAssetToYPC(User user, Troop troop, String bindAssetToYPC,
			String _ypcId, String _assetDesc, String _assetTitle)
			throws IllegalAccessException {

		String vtkErr = "";
		String assetId = bindAssetToYPC;
		String ypcId = _ypcId;
		String assetDesc = java.net.URLDecoder.decode(_assetDesc);
		String assetTitle = java.net.URLDecoder.decode(_assetTitle);
		java.util.List<MeetingE> meetings = troop.getYearPlan()
				.getMeetingEvents();
		for (int i = 0; i < meetings.size(); i++) {
			if (meetings.get(i).getUid().equals(ypcId)) {
				Asset asset = new Asset();
				asset.setIsCachable(false);
				asset.setRefId(assetId);
				asset.setDescription(assetDesc);
				asset.setTitle(assetTitle);

				java.util.List<Asset> assets = meetings.get(i).getAssets();
				assets = assets == null ? new java.util.ArrayList() : assets;

				assets.add(asset);

				meetings.get(i).setAssets(assets);

				boolean isUsrUpd = updateTroop(user, troop);

				if (!isUsrUpd)
					vtkErr += vtkErr
							.concat("Warning: You last change was not saved.");

				return vtkErr;
			}
		}

		java.util.List<Activity> activities = troop.getYearPlan()
				.getActivities();
		if (activities != null) {
			for (int i = 0; i < activities.size(); i++) {
				if (activities.get(i).getUid().equals(ypcId)) {
					Asset asset = new Asset();
					asset.setIsCachable(false);
					asset.setRefId(assetId);
					asset.setDescription(assetDesc);
					asset.setTitle(assetTitle);

					java.util.List<Asset> assets = activities.get(i)
							.getAssets();
					assets = assets == null ? new java.util.ArrayList()
							: assets;

					assets.add(asset);

					activities.get(i).setAssets(assets);

					boolean isUsrUpd = updateTroop(user, troop);
					if (!isUsrUpd)
						vtkErr += vtkErr
								.concat("Warning: You last change was not saved.");

					return vtkErr;
				}
			}

		}
		return vtkErr;
	}

	public String editCustActivity(User user, Troop troop,
			javax.servlet.http.HttpServletRequest request)
			throws IllegalAccessException, ParseException {
		java.text.SimpleDateFormat dateFormat4 = new java.text.SimpleDateFormat(
				"MM/dd/yyyy hh:mm a");
		String vtkErr = "";
		java.util.List<Activity> activities = troop.getYearPlan()
				.getActivities();
		Activity activity = null;
		for (int i = 0; i < activities.size(); i++)
			if (activities.get(i).getUid()
					.equals(request.getParameter("editCustActivity"))) {
				activity = activities.get(i);
				break;
			}
		double cost = org.girlscouts.vtk.utils.VtkUtil
				.convertObjectToDouble(request
						.getParameter("newCustActivity_cost"));
		activity.setCost(cost);
		activity.setContent(request.getParameter("newCustActivity_txt"));
		activity.setDate(dateFormat4.parse(request
				.getParameter("newCustActivity_date")
				+ " "
				+ request.getParameter("newCustActivity_startTime")
				+ " "
				+ request.getParameter("newCustActivity_startTime_AP")));
		activity.setEndDate(dateFormat4.parse(request
				.getParameter("newCustActivity_date")
				+ " "
				+ request.getParameter("newCustActivity_endTime")
				+ " "
				+ request.getParameter("newCustActivity_endTime_AP")));
		activity.setName(request.getParameter("newCustActivity_name"));
		activity.setLocationName(request.getParameter("newCustActivityLocName"));
		activity.setLocationAddress(request
				.getParameter("newCustActivityLocAddr"));

		boolean isUsrUpd = updateTroop(user, troop);
		if (!isUsrUpd)
			vtkErr += vtkErr.concat("Warning: You last change was not saved.");

		return vtkErr;
	}

	public void impersonate(User user, Troop nothing, String councilCode,
			String troopId, HttpSession session) throws IllegalAccessException {

		Troop new_troop = getTroop(user, councilCode + "", troopId);

		new_troop.setTroop(nothing.getTroop());
		new_troop.setSfTroopId(new_troop.getTroop().getTroopId());
		new_troop.setSfUserId(user.getApiConfig().getUserId());
		new_troop.setSfTroopName(new_troop.getTroop().getTroopName());
		new_troop.setSfTroopAge(new_troop.getTroop().getGradeLevel());
		new_troop.setSfCouncil(new_troop.getTroop().getCouncilCode() + "");

		// logout multi troop
		// -logout(user, nothing);

		session.setAttribute("VTK_troop", new_troop);
		session.putValue("VTK_planView_memoPos", null);
		new_troop.setCurrentTroop(session.getId());
	}
	
	
	private Cal selectYearPlan_newSched(User user, Troop troop, String yearPlanPath) throws java.lang.IllegalAccessException, VtkYearPlanChangeException {

		Cal cal = new Cal();
		// permission to update
		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_ADD_YEARPLAN_ID))
			throw new IllegalAccessException();

		if (!userUtil.isCurrentTroopId(troop, user.getSid())) {
			troop.setErrCode("112");
			return null;
		}

		YearPlan oldPlan = troop.getYearPlan();
		YearPlan newYearPlan = addYearPlan(user, troop, yearPlanPath);
		
		if( oldPlan.getSchedule() == null ) return null;
		
		int currMeetingCount = 0;
		if( newYearPlan.getMeetingEvents()!=null )
			currMeetingCount = newYearPlan.getMeetingEvents().size();
		
		java.util.List<java.util.Date> oldSched = VtkUtil.getStrCommDelToArrayDates(oldPlan.getSchedule().getDates());
		
		//fever meetings
		if( oldSched.size() > currMeetingCount )
			for(int i= (oldSched.size()-1); i>= currMeetingCount ; i--){
				if(oldSched.get(i).after(new java.util.Date() ) )
					oldSched.remove(i); //rm last meeting if in future
				else
					throw new org.girlscouts.vtk.ejb.VtkYearPlanChangeException("Can not change year plan. Dates in the past can not be changed ("+ oldSched.get(i) +")");
			}
		
		//more meetings
		if( oldSched.size() < currMeetingCount ){
			
			//meeting freq dates
			String calFreq= oldPlan.getCalFreq();
			if( calFreq==null || calFreq.trim().equals("") ) calFreq= "biweekly";
			
			//add meeting dates
			for(int i=oldSched.size();i < currMeetingCount;i ++ ){
				long newDate = new CalendarUtil().getNextDate(VtkUtil.getStrCommDelToArrayStr( oldPlan.getCalExclWeeksOf() ), oldSched.get(oldSched.size()-1).getTime(), oldPlan.getCalFreq(), false);
				oldSched.add( new java.util.Date(newDate) );
			}
		}
		cal.setDates( VtkUtil.getArrayDateToLongComDelim( oldSched ) );
		cal.setDbUpdate(true);
		return cal; 
	}
	
	private java.util.List<MeetingE> selectYearPlan_newMeetingPlan(User user, Troop troop, YearPlan newYearPlan) throws java.lang.IllegalAccessException {

		// permission to update
		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_ADD_YEARPLAN_ID))
			throw new IllegalAccessException();

		if (!userUtil.isCurrentTroopId(troop, user.getSid())) {
			troop.setErrCode("112");
			return null;
		}

		YearPlan oldPlan = troop.getYearPlan();
		//YearPlan newYearPlan = addYearPlan(user, troop, yearPlanPath);
		
		
		
		 //SORT Meetings - new
		 newYearPlan.setMeetingEvents( VtkUtil.sortMeetingsById(newYearPlan.getMeetingEvents() ));
		
		if( oldPlan==null || oldPlan.getSchedule() == null ) return newYearPlan.getMeetingEvents();
		
		//SORT MEETINGS -old
		 oldPlan.setMeetingEvents( VtkUtil.sortMeetingsById(oldPlan.getMeetingEvents()) );
		 
		 
		//get Number Of Past meetings
		int numOfPastMeetings=0;
		java.util.List< java.util.Date> dates = VtkUtil.getStrCommDelToArrayDates( oldPlan.getSchedule().getDates() );
		for(int i=0;i< dates.size();i++)
			if( dates.get(i).before( new java.util.Date() ) )
				numOfPastMeetings++;
		//overwrite first numOfPastMeetings from oldPlan to newPlan; ASSUMING NEW & OLD Meetings are sorted!!!
		if( newYearPlan.getMeetingEvents().size()>= numOfPastMeetings) //if newYearPlan has at least numOfPastMeetings-> overwrite
		 for(int i=0; i< numOfPastMeetings;i++ ){
			 newYearPlan.getMeetingEvents().set(i, oldPlan.getMeetingEvents().get(i));
		 }
		
		return VtkUtil.setToDbUpdate( newYearPlan.getMeetingEvents() );
	}
	
	
	
	
	public void selectYearPlan(User user, Troop troop, String yearPlanPath, String planName) throws java.lang.IllegalAccessException, VtkYearPlanChangeException {
		// permission to update
		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_ADD_YEARPLAN_ID))
			throw new IllegalAccessException();

		if (!userUtil.isCurrentTroopId(troop, user.getSid())) {
			troop.setErrCode("112");
			return;
		}

		YearPlan oldPlan = troop.getYearPlan();
		/*
		if(oldPlan!=null && oldPlan.getMeetingEvents()!=null)
    		for(int i=0;i<oldPlan.getMeetingEvents().size();i++)
				System.err.println("tatax oldPlan:"+((MeetingE)oldPlan.getMeetingEvents().get(i)).getRefId());
*/
		YearPlan newYearPlan = addYearPlan(user, troop, yearPlanPath);// troopDAO.addYearPlan1(troop,

		if( oldPlan!=null)
			if(oldPlan.getName().equals(planName)) {//reset current yearplan
				if(!userUtil.hasPermission(troop,Permission.PERMISSION_RM_YEARPLAN_ID))
					throw new IllegalAccessException();
				Cal cal = new Cal();
				cal.setDbUpdate(true);
				troop.getYearPlan().setSchedule(cal);//set to cal with dates equals to null to remove the schedule
			}
			else
				troop.getYearPlan().setSchedule( selectYearPlan_newSched( user,  troop,  yearPlanPath ) );	
		else
			troop.setYearPlan(newYearPlan);
		
		if(yearPlanPath!=null && !yearPlanPath.equals("") )
			troop.getYearPlan().setMeetingEvents( selectYearPlan_newMeetingPlan( user, troop, newYearPlan) );			
		else
			troop.getYearPlan().setMeetingEvents(null);
		if( oldPlan!=null){
			troopDAO.removeMeetings(user, troop);
			
			//rm activities
			if(troop.getYearPlan().getActivities()!=null )
			 for(int i=0;i< troop.getYearPlan().getActivities().size();i++)
				troopDAO.removeActivity(user, troop, troop.getYearPlan().getActivities().get(i));
		}
		
		troop.getYearPlan().setAltered("false");
		troop.getYearPlan().setName(planName);
		troop.getYearPlan().setDbUpdate(true);
		troopDAO.updateTroop(user, troop);
	}
}// end class

