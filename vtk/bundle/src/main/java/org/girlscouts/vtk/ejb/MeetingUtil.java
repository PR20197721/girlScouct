package org.girlscouts.vtk.ejb;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpSession;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.dao.ActivityDAO;
import org.girlscouts.vtk.dao.AssetComponentType;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.dao.TroopDAO;
import org.girlscouts.vtk.dao.YearPlanComponentType;
import org.girlscouts.vtk.models.Achievement;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.models.Attendance;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.JcrCollectionHoldString;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.MeetingCanceled;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.models.Note;
import org.girlscouts.vtk.models.PlanView;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.YearPlanComponent;
import org.girlscouts.vtk.models.SentEmail;
import org.girlscouts.vtk.utils.VtkException;
import org.girlscouts.vtk.utils.VtkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service(MeetingUtil.class)
public class MeetingUtil {
	private final Logger log = LoggerFactory.getLogger("vtk");
	@Reference
	TroopUtil troopUtil;

	@Reference
	MeetingDAO meetingDAO;

	@Reference
	ActivityDAO activityDAO;

	@Reference
	UserUtil userUtil;

	@Reference
	YearPlanUtil yearPlanUtil;

	@Reference
	org.girlscouts.vtk.helpers.DataImportTimestamper dataImportTimestamper;

	@Reference
	TroopDAO troopDAO;

	@Reference
	private ConnectionFactory connectionFactory;

	java.text.SimpleDateFormat FORMAT_MMddYYYY = new java.text.SimpleDateFormat(
			"MM/dd/yyyy");

	public java.util.List<MeetingE> updateMeetingPos(
			java.util.List<MeetingE> orgMeetings,
			java.util.List<Integer> newPoss) {
		
		
		java.util.List<MeetingE> newMeeting = new java.util.ArrayList<MeetingE>();// orgMeetings.size());
		try {

			for (int i = 0; i < orgMeetings.size(); i++)
				newMeeting.add(orgMeetings.get(i)); // TODO

			for (int i = 0; i < orgMeetings.size(); i++) {
				int newpos = newPoss.indexOf(i + 1);
				if(newpos==i) continue;
				MeetingE meeting = orgMeetings.get(i);
				
				meeting.setId(newpos);
				meeting.setDbUpdate(true);
				
				newMeeting.set(newpos, meeting);

			}
		} catch (Exception e) {
			log.error("ERROR : MeetingUtil.updateMeetingPos");
			newMeeting = orgMeetings;
			e.printStackTrace();
		}

		return newMeeting;

	}

	public Activity getActivity(String activityId,
			java.util.List<Activity> activities) {
		Activity _activity = null;
		for (Activity activity : activities)
			if (activity.getId().equals(activityId)) {
				_activity = activity;
				break;
			}

		return _activity;
	}

	public java.util.Map getYearPlanSched(User user, Troop troop, YearPlan plan,
			boolean meetingPlanSpecialSort) throws IllegalAccessException, VtkException {
		return getYearPlanSched(user, troop, plan, meetingPlanSpecialSort, false);
	}

	public java.util.Map getYearPlanSched(User user, Troop troop, YearPlan plan,
			boolean meetingPlanSpecialSort, boolean isLoadMeetingInfo)

			throws IllegalAccessException, VtkException {
		if( plan==null ) return new java.util.TreeMap();
		if (plan.getSchedule() != null || plan.getActivities() == null
				|| plan.getActivities().size() <= 0) {


			// set meetingInfos if isLoadMeetingInfo
			if (isLoadMeetingInfo) {
				java.util.List<MeetingE> meetingEs = plan.getMeetingEvents();
				if (meetingEs != null)
					for (int i = 0; i < meetingEs.size(); i++) {
						MeetingE meetingE = meetingEs.get(i);
						Meeting meetingInfo = yearPlanUtil.getMeeting(user,troop,
								meetingE.getRefId());
						meetingE.setMeetingInfo(meetingInfo);
					}
				plan.setMeetingEvents(meetingEs);

				
				// load meetingCanceled
				if (plan.getMeetingCanceled() != null)
					for (int i = 0; i < plan.getMeetingCanceled().size(); i++) {
					
						MeetingCanceled meetingCanceled = plan
								.getMeetingCanceled().get(i);
						Meeting meetingInfo = yearPlanUtil.getMeeting(user,troop, 
								meetingCanceled.getRefId());
						
						meetingCanceled.setMeetingInfo(meetingInfo);
					}

			}

			return getYearPlanSched(plan);
		}

		// if no sched and activ -> activ on top
		java.util.Map orgSched = getYearPlanSched(plan);
		
		java.util.Map container = null;
		if( troop !=null && troop.getSfTroopAge()!=null && 
				(troop.getSfTroopAge().toLowerCase().contains("cadette") || 
						troop.getSfTroopAge().toLowerCase().contains("senior") ||
						troop.getSfTroopAge().toLowerCase().contains("ambassador") )){
			container= new TreeMap();
		}else{			
			container= new LinkedHashMap();
		}
		
		java.util.Iterator itr = orgSched.keySet().iterator();
		while (itr.hasNext()) {
			java.util.Date date = (java.util.Date) itr.next();
			YearPlanComponent _comp = (YearPlanComponent) orgSched.get(date);
			switch (_comp.getType()) {
			case ACTIVITY:
				Activity activity = (Activity) _comp;
				container.put(date, activity);
				break;
			}
		}

		// now set meetings & etc
		itr = orgSched.keySet().iterator();
		boolean heal = false;
		while (itr.hasNext()) {
			java.util.Date date = (java.util.Date) itr.next();
			YearPlanComponent _comp = (YearPlanComponent) orgSched.get(date);

			switch (_comp.getType()) {
			case MEETINGCANCELED:
				
				MeetingCanceled meetingCanceled = (MeetingCanceled) _comp;
				
				Meeting meetingInfoCan = yearPlanUtil.getMeeting(user,troop, 
						meetingCanceled.getRefId());
					
				meetingCanceled.setMeetingInfo(meetingInfoCan);
				
				container.put(date, meetingCanceled);
				break;
			case MEETING:

				MeetingE meetingE = (MeetingE) _comp;
				if (isLoadMeetingInfo) {
					Meeting meetingInfo = yearPlanUtil.getMeeting(user,troop, 
							meetingE.getRefId());

					meetingE.setMeetingInfo(meetingInfo);
				}

				int maxLook = 0;
				while (container.containsKey(date)) {
					date = new Date(date.getTime() + 5l);
					heal = true;
					maxLook++;
					if (maxLook > 100)
						break;
				}

				container.put(date, meetingE);

				break;
			case MILESTONE:
				Milestone milestone = (Milestone) _comp;
				container.put(date, milestone);
				break;

			}
		}

		return container;

	}

	public java.util.Map getYearPlanSched(YearPlan plan) {

		if (plan == null)
			return null;

		java.util.Map<java.util.Date, YearPlanComponent> sched = null;
		try {
			sched = new java.util.TreeMap<java.util.Date, YearPlanComponent>();

			List<Activity> activities = plan.getActivities();

			java.util.List<MeetingE> meetingEs = plan.getMeetingEvents();
			if (meetingEs != null) {
				Comparator<MeetingE> comp = new BeanComparator("id");
				Collections.sort(meetingEs, comp);
			}
			if (plan.getSchedule() != null) {

				String calMeeting = plan.getSchedule().getDates();
				
				StringTokenizer t = new StringTokenizer(calMeeting, ",");
				int count = 0;
				while (t.hasMoreElements()) {
					try {
						java.util.Date dt = new java.util.Date(
								Long.parseLong((t.nextToken().replace("\n", "")
										.replace("\r", "").trim())));
						int maxLook = 0;
						if (sched.containsKey(dt)) {
							dt = new Date(dt.getTime() + 5l);
							maxLook++;
							if (maxLook > 100)
								break;
						}
						if( meetingEs.size()> count)
							sched.put(dt, meetingEs.get(count));
					} catch (Exception e) {
						e.printStackTrace();
					}
					count++;

				}

				int counter = 0;
				java.util.Iterator itr = sched.keySet().iterator();
				while (itr.hasNext()) {
					sched.put((java.util.Date) itr.next(),
							meetingEs.get(counter));
					counter++;
				}

			} else { // no dates: create 1976
				Calendar tmp = java.util.Calendar.getInstance();
				tmp.setTime(new java.util.Date("1/1/1976"));

				if (meetingEs != null)
					for (int i = 0; i < meetingEs.size(); i++) {

						sched.put(tmp.getTime(), meetingEs.get(i));
						tmp.add(java.util.Calendar.DATE, 1);
					}
			}

			if (activities != null)
				for (int i = 0; i < activities.size(); i++) {

					long tmp = activities.get(i).getDate().getTime();
					if (sched.containsKey(activities.get(i).getDate())) { // add
																			// 2
																			// sec
						tmp = tmp + TimeUnit.MILLISECONDS.toMillis(1);
					}

					sched.put(new java.util.Date(tmp), activities.get(i));

				}

			if (plan.getMeetingCanceled() != null)
				for (int i = 0; i < plan.getMeetingCanceled().size(); i++) {

					long tmp = plan.getMeetingCanceled().get(i).getDate()
							.getTime();
					if (sched.containsKey(plan.getMeetingCanceled().get(i)
							.getDate())) { // add 2 sec
						tmp = tmp + TimeUnit.MILLISECONDS.toMillis(1);
					}

					sched.put(new java.util.Date(tmp), plan
							.getMeetingCanceled().get(i));

				}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return sched;
	}

	public void changeMeetingPositions(User user, Troop troop,
			String newPositions) throws IllegalAccessException, VtkException {

		java.util.List<Integer> newMeetingSetup = new java.util.ArrayList();
		java.util.StringTokenizer t = new java.util.StringTokenizer(
				newPositions, ",");
		while (t.hasMoreElements())
			newMeetingSetup.add(Integer.parseInt(t.nextToken()));

		java.util.List<MeetingE> rearangedMeetings = null;
		try {
			rearangedMeetings = updateMeetingPos(troop.getYearPlan()
					.getMeetingEvents(), newMeetingSetup);
		} catch (Exception e) {
			e.printStackTrace();
		}

		YearPlan plan = troop.getYearPlan();
		plan.setMeetingEvents(rearangedMeetings);
		plan.setAltered("true");
		plan.setDbUpdate(true);
		troop.setYearPlan(plan);
		troopUtil.updateTroop(user, troop);

	}

	public void createCustomAgenda(User user, Troop troop, String name,
			String meetingPath, int duration, long _startTime, String txt)
			throws IllegalAccessException, VtkException {

		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_CREATE_MEETING_ID))
			throw new IllegalAccessException();
		java.util.Calendar startTime = Calendar.getInstance();
		startTime.setTimeInMillis(_startTime);
		java.util.List<MeetingE> meetings = troop.getYearPlan()
				.getMeetingEvents();
		for (int i = 0; i < meetings.size(); i++) {
			MeetingE m = meetings.get(i);
			if (m.getPath().equals(meetingPath)) {
				Meeting meeting = null;
				if (m.getRefId().contains("_"))
					meeting = meetingDAO.updateCustomMeeting(user, troop, m,
							null);
				else
					meeting = meetingDAO.createCustomMeeting(user, troop, m);

				Activity activity = new Activity();
				activity.setName(name);
				activity.setDuration(duration);
				activity.setActivityDescription(txt);
				activity.setActivityNumber(meeting.getActivities().size() + 1);
				meetingDAO.addActivity(user, troop, meeting, activity);
				Cal cal = troop.getYearPlan().getSchedule();
				if (cal != null)
					cal.addDate(startTime.getTime());
			}
		}
		troop.getYearPlan().setAltered("true");
		troopUtil.updateTroop(user, troop);
	}

	public void rmCustomActivity(User user, Troop troop, String activityPath)
			throws IllegalStateException, IllegalAccessException {
		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_RM_ACTIVITY_ID))
			throw new IllegalAccessException();
		java.util.List<Activity> activities = troop.getYearPlan()
				.getActivities();
		for (int i = 0; i < activities.size(); i++) {
			Activity activity = activities.get(i);

			if (activity.getPath().equals(activityPath)) {
				activities.remove(activity);
				troopDAO.removeActivity(user, troop, activity);
			}
		}

	}

	public void swapMeetings(User user, Troop troop, String fromPath,
			String toPath) throws java.lang.IllegalAccessException, VtkException {

		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_EDIT_MEETING_ID))
			throw new IllegalAccessException();

		java.util.List<MeetingE> meetings = troop.getYearPlan()
				.getMeetingEvents();
		for (int i = 0; i < meetings.size(); i++) {

			MeetingE meeting = meetings.get(i);
			if (meeting.getPath().equals(fromPath)) {

				if( meeting.getAssets()!=null)
				  for(int y=0;y<meeting.getAssets().size();y++)
					troopDAO.removeAsset(user, troop, meeting.getAssets().get(y));
				meeting.setRefId(toPath);
				meeting.setAssets(null);
				meeting.setLastAssetUpdate(null); // auto load assets for new
													// meeting

			}
		}

		troop.getYearPlan().setAltered("true");
		troopUtil.updateTroop(user, troop);
	}

	public void rearrangeActivity(User user, Troop troop, String meetingPath,
			String _newPoss) throws java.lang.IllegalAccessException, VtkException {

		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_EDIT_MEETING_ID))
			throw new IllegalAccessException();

		// TOREDO
		java.util.List<Integer> newPoss = new java.util.ArrayList();
		StringTokenizer t = new StringTokenizer(_newPoss, ",");
		while (t.hasMoreElements())
			newPoss.add(Integer.parseInt(t.nextToken()));

		Meeting meetingInfo = yearPlanUtil.getMeeting(user, troop, meetingPath);
		java.util.List<Activity> orgActivities = meetingInfo.getActivities();
		orgActivities = sortActivity(orgActivities);
		java.util.List<Activity> newActivity = new java.util.ArrayList<Activity>();
		for (int i = 0; i < orgActivities.size(); i++)
			newActivity.add(null);

		for (int i = 0; i < orgActivities.size(); i++) {
			Activity activity = orgActivities.get(i);
			int newpos = newPoss.indexOf(i + 1);
			activity.setActivityNumber(newpos + 1);
			newActivity.set(newpos, activity);
		}

		// save activities to meeting
		meetingInfo.setActivities(newActivity);

		// create custom meeting
		MeetingE meetingE = getMeeting(troop.getYearPlan().getMeetingEvents(),
				meetingPath);
		/*
		if (meetingE.getRefId().contains("_"))
			meetingDAO.updateCustomMeeting(user, troop, meetingE, meetingInfo);
		else
			meetingDAO.createCustomMeeting(user, troop, meetingE, meetingInfo);

		troop.getYearPlan().setAltered("true");
		troopUtil.updateTroop(user, troop);
		*/
		createOrUpdateCustomMeeting( user, troop, meetingE, meetingInfo );
	}

	public MeetingE getMeeting(java.util.List<MeetingE> meetings,
			String meetingPath) {

		for (int i = 0; i < meetings.size(); i++) {
			if (meetings.get(i).getRefId().equals(meetingPath)) {
				return meetings.get(i);
			}
		}
		return null;
	}

	public void addMeetings(User user, Troop troop, String newMeetingPath)
			throws java.lang.IllegalAccessException, VtkException {
		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_CREATE_MEETING_ID))
			throw new IllegalAccessException();

		MeetingE meeting = new MeetingE();
		meeting.setRefId(newMeetingPath);

		int maxMeetEId = 0;
		if (troop.getYearPlan().getMeetingEvents() != null)
			for (int i = 0; i < troop.getYearPlan().getMeetingEvents().size(); i++)
				if (maxMeetEId < troop.getYearPlan().getMeetingEvents().get(i)
						.getId())
					maxMeetEId = troop.getYearPlan().getMeetingEvents().get(i)
							.getId();
		meeting.setId(maxMeetEId + 1);
		meeting.setDbUpdate(true);
		if (troop.getYearPlan().getMeetingEvents() == null)
			troop.getYearPlan().setMeetingEvents(new java.util.ArrayList());
		troop.getYearPlan().getMeetingEvents().add(meeting);
	
		if (troop.getYearPlan().getSchedule() != null ) {
			java.util.List<java.util.Date> sched = VtkUtil
					.getStrCommDelToArrayDates(troop.getYearPlan()
							.getSchedule().getDates());

			long newDate = new java.util.Date().getTime()+5000;
			if( !troop.getYearPlan().getSchedule().getDates().trim().equals("") )
			  newDate= new CalendarUtil().getNextDate(VtkUtil
					.getStrCommDelToArrayStr(troop.getYearPlan()
							.getCalExclWeeksOf()), sched.get(sched.size() - 1)
					.getTime(), troop.getYearPlan().getCalFreq(), false);
			sched.add(new java.util.Date(newDate));
			troop.getYearPlan().getSchedule()
					.setDates(VtkUtil.getArrayDateToLongComDelim(sched));
		}

		troop.getYearPlan().setAltered("true");
		troopUtil.updateTroop(user, troop);

	}

	public void rmAgenda(User user, Troop troop, String agendaPathToRm,
			String fromMeetingPath) throws java.lang.IllegalAccessException, VtkException {
		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_REMOVE_MEETING_ID))
			throw new IllegalAccessException();

		for (int i = 0; i < troop.getYearPlan().getMeetingEvents().size(); i++) {

			if (troop.getYearPlan().getMeetingEvents().get(i).getPath()
					.equals(fromMeetingPath)) {

				MeetingE meeting = troop.getYearPlan().getMeetingEvents()
						.get(i);
				Meeting meetingInfo = meetingDAO.getMeeting(user, troop,
						meeting.getRefId());
				List<Activity> activities = meetingInfo.getActivities();
				for (int y = 0; y < activities.size(); y++) {
					if (activities.get(y).getPath().equals(agendaPathToRm)) {
						activities.remove(y);
						Comparator<Activity> comp = new org.apache.commons.beanutils.BeanComparator(
								"activityNumber");
						Collections.sort(activities, comp);
						for (int ii = 0; ii < activities.size(); ii++)
							activities.get(ii).setActivityNumber(ii + 1);
						meetingDAO.createCustomMeeting(user, troop, meeting,
								meetingInfo);
						troopUtil.updateTroop(user, troop);
						return;
					}

				}

			}

		}
	}

	public void editAgendaDuration(User user, Troop troop, int duration,
			String activityPath, String meetingPath)
			throws java.lang.IllegalAccessException, VtkException {

		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_EDIT_MEETING_ID))
			throw new IllegalAccessException();

		for (int i = 0; i < troop.getYearPlan().getMeetingEvents().size(); i++) {

			if (troop.getYearPlan().getMeetingEvents().get(i).getPath()
					.equals(meetingPath)) {

				MeetingE meeting = troop.getYearPlan().getMeetingEvents()
						.get(i);
				Meeting meetingInfo = meetingDAO.getMeeting(user, troop,
						meeting.getRefId());
				List<Activity> activities = meetingInfo.getActivities();
				for (int y = 0; y < activities.size(); y++) {
					if (activities.get(y).getPath().equals(activityPath)) {
						Activity activity = activities.get(y);
						activity.setDuration(duration);
						meetingDAO.createCustomMeeting(user, troop, meeting,
								meetingInfo);
						troop.getYearPlan().setAltered("true");
						troopUtil.updateTroop(user, troop);
						return;
					}
				}
			}
		}
	}

	public void reverAgenda(User user, Troop troop, String meetingPath)
			throws java.lang.IllegalAccessException, VtkException {
		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_EDIT_MEETING_ID))
			throw new IllegalAccessException();

		MeetingE meeting = null;
		for (int i = 0; i < troop.getYearPlan().getMeetingEvents().size(); i++)
			if (troop.getYearPlan().getMeetingEvents().get(i).getPath()
					.equals(meetingPath))
				meeting = troop.getYearPlan().getMeetingEvents().get(i);
		String[] split = meeting.getRefId().split("/");
		String file = split[(split.length - 1)];
		file = file.substring(0, file.indexOf("_"));
		String ageLevel = troop.getTroop().getGradeLevel();
		try {
			ageLevel = ageLevel.substring(ageLevel.indexOf("-") + 1)
					.toLowerCase().trim();
		} catch (Exception e) {
			e.printStackTrace();
		}

		java.util.List<Meeting> __meetings = meetingDAO.getAllMeetings(user, troop,
				ageLevel);

		for (int i = 0; i < __meetings.size(); i++) {
			Meeting __meeting = __meetings.get(i);

			if (__meeting.getPath().endsWith(file)) {
				swapMeetings(user, troop, meetingPath, __meeting.getPath());
				return;
			}
		}

	}

	public void addAids(User user, Troop troop, String aidId, String meetingId,
			String assetName, String docType)
			throws java.lang.IllegalAccessException, VtkException {
		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_CREATE_MEETING_ID))
			throw new IllegalAccessException();

		java.util.List<MeetingE> meetings = troop.getYearPlan()
				.getMeetingEvents();
		for (int i = 0; i < meetings.size(); i++) {
			MeetingE meeting = meetings.get(i);
			if (meeting.getUid().equals(meetingId)) {

				Asset dbAsset = meetingDAO.getAsset(user, troop, aidId + "/");

				Asset asset = new Asset();
				asset.setRefId(aidId);
				asset.setType(AssetComponentType.AID.toString());
				asset.setTitle(assetName);
				asset.setDocType(docType);
				if (dbAsset != null) {
					asset.setDescription(dbAsset.getDescription());
					asset.setIsOutdoorRelated(dbAsset.getIsOutdoorRelated());
				}
				java.util.List<Asset> assets = meeting.getAssets();
				assets = assets == null ? new java.util.ArrayList() : assets;

				boolean isAsset = false;
				for (int y = 0; y < assets.size(); y++)
					if (assets.get(y).getRefId().equals(aidId))
						isAsset = true;

				if (isAsset) {
					return;
				}

				assets.add(asset);
				meeting.setAssets(assets);
				troopUtil.updateTroop(user, troop);
				return;
			}
		}

		java.util.List<Activity> activities = troop.getYearPlan()
				.getActivities();
		for (int i = 0; i < activities.size(); i++) {
			Activity activity = activities.get(i);
			if (activity.getUid().equals(meetingId)) {

				Asset asset = new Asset();
				asset.setRefId(aidId);
				asset.setType("AID");

				java.util.List<Asset> assets = activity.getAssets();
				assets = assets == null ? new java.util.ArrayList() : assets;
				assets.add(asset);
				activity.setAssets(assets);
				troop.getYearPlan().setAltered("true");
				troopUtil.updateTroop(user, troop);
				return;
			}
		}

	}

	public void addResource(User user, Troop troop, String aidId,
			String meetingId, String assetName, String docType)
			throws java.lang.IllegalAccessException, VtkException {
		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_CREATE_MEETING_ID))
			throw new IllegalAccessException();

		java.util.List<MeetingE> meetings = troop.getYearPlan()
				.getMeetingEvents();
		for (int i = 0; i < meetings.size(); i++) {
			MeetingE meeting = meetings.get(i);
			if (meeting.getUid().equals(meetingId)) {

				Asset asset = new Asset();
				asset.setRefId(aidId);
				asset.setType(AssetComponentType.RESOURCE.toString());
				asset.setTitle(assetName);
				asset.setDocType(docType);

				java.util.List<Asset> assets = meeting.getAssets();
				assets = assets == null ? new java.util.ArrayList() : assets;

				boolean isAsset = false;
				for (int y = 0; y < assets.size(); y++)
					if (assets.get(y).getRefId().equals(aidId))
						isAsset = true;

				if (isAsset) {
					return;
				}

				assets.add(asset);
				meeting.setAssets(assets);
				troopUtil.updateTroop(user, troop);
				return;
			}
		}

		java.util.List<Activity> activities = troop.getYearPlan()
				.getActivities();
		for (int i = 0; i < activities.size(); i++) {
			Activity activity = activities.get(i);
			if (activity.getUid().equals(meetingId)) {

				Asset asset = new Asset();
				asset.setRefId(aidId);
				asset.setType(AssetComponentType.RESOURCE.toString());

				java.util.List<Asset> assets = activity.getAssets();
				assets = assets == null ? new java.util.ArrayList() : assets;
				assets.add(asset);
				activity.setAssets(assets);
				troopUtil.updateTroop(user, troop);
				return;
			}
		}

	}

	public void rmAsset(User user, Troop troop, String aidId, String meetingId)
			throws java.lang.IllegalAccessException, VtkException {
		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_REMOVE_MEETING_ID))
			throw new IllegalAccessException();

		java.util.List<MeetingE> meetings = troop.getYearPlan()
				.getMeetingEvents();
		for (int i = 0; i < meetings.size(); i++) {
			MeetingE meeting = meetings.get(i);
			if (meeting.getUid().equals(meetingId)) {
				java.util.List<Asset> assets = meeting.getAssets();
				for (int y = 0; y < assets.size(); y++) {
					if (assets.get(y).getRefId().equals(aidId)) {
						
						troopDAO.removeAsset(user, troop, assets.get(y));
						assets.remove(y);
					}
				}
				return;
			}
		}

		java.util.List<Activity> activities = troop.getYearPlan()
				.getActivities();
		for (int i = 0; i < activities.size(); i++) {
			Activity activity = activities.get(i);
			if (activity.getUid().equals(meetingId)) {
				java.util.List<Asset> assets = activity.getAssets();
				for (int y = 0; y < assets.size(); y++) {
					if (assets.get(y).getUid().equals(aidId)) {
						assets.remove(y);
					}
				}
				troopUtil.updateTroop(user, troop);
				return;
			}
		}

	}

	public java.util.List<MeetingE> sortById(java.util.List<MeetingE> meetings) {
		Comparator<MeetingE> comp = new org.apache.commons.beanutils.BeanComparator(
				"id");
		Collections.sort(meetings, comp);
		return meetings;
	}

	public java.util.List<Activity> sortActivity(
			java.util.List<Activity> _activities) {
		try {
			Comparator<Activity> comp = new org.apache.commons.beanutils.BeanComparator(
					"activityNumber");
			Collections.sort(_activities, comp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return _activities;

	}

	public java.util.List<Activity> sortActivityByDate(
			java.util.List<Activity> _activities) {

		try {
			Comparator<Activity> comp = new org.apache.commons.beanutils.BeanComparator(
					"date");
			Collections.sort(_activities, comp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return _activities;

	}

	
	public PlanView planView(User user, Troop troop,
			javax.servlet.http.HttpServletRequest request) throws Exception {
		return planView(user, troop, request, false);
		
	}
	
	public PlanView planView(User user, Troop troop,
			javax.servlet.http.HttpServletRequest request, boolean isUpdateAssetInDb) throws Exception {	
		PlanView planView = planView1(user, troop, request);
		
		if (planView == null) {
			return null;
		}
	
		YearPlanComponent _comp = planView.getYearPlanComponent();
		if (_comp == null) {
			return null;
		}
	
		MeetingE meeting = null;
		List<Asset> _aidTags = null;
		Meeting meetingInfo = null;
		if (_comp.getType() == YearPlanComponentType.MEETING
				|| _comp.getType() == YearPlanComponentType.MEETINGCANCELED) {
			meeting = (MeetingE) _comp;
			int meetingCount = 0;
			if (_comp.getType() == YearPlanComponentType.MEETING)
				meetingCount = troop.getYearPlan().getMeetingEvents()
						.indexOf(_comp) + 1;
			else if (_comp.getType() == YearPlanComponentType.MEETINGCANCELED)
				meetingCount = troop.getYearPlan().getMeetingCanceled()
						.indexOf(_comp) + 1;
System.err.println("Kaca planViiew..."+ meeting.getRefId());			
			meetingInfo = yearPlanUtil.getMeeting(user, troop, meeting.getRefId());	
			meeting.setMeetingInfo(meetingInfo);
			java.util.List<Activity> _activities = null;
			if( meetingInfo.getActivities()!=null )
				_activities = meetingInfo.getActivities();
			
			java.util.Map<String, JcrCollectionHoldString> meetingInfoItems = meetingInfo
					.getMeetingInfo();

			boolean isLocked = false;
			if (planView.getSearchDate().before(new java.util.Date())
					&& troop.getYearPlan().getSchedule() != null)
				isLocked = true;

			boolean isCanceled = false;
			if (meeting.getCancelled() != null
					&& meeting.getCancelled().equals("true")) {
				isCanceled = true;
			}
			_aidTags = meeting.getAssets();
		
			//start
			
			
		if( _aidTags==null )
			_aidTags= new java.util.ArrayList<Asset>();
		
		// query aids cachables
		java.util.List <Asset>__aidTags = yearPlanUtil.getAids(user, troop, 
				meetingInfo.getAidTags(), meetingInfo.getId(),
				meeting.getUid(), meetingInfo.getPath());


		java.util.List <Asset>__resources = yearPlanUtil.getResources(user, troop, 
				meetingInfo.getAidTags(), meetingInfo.getId(),
				meeting.getUid(), meetingInfo.getPath());
		
		

		if( _aidTags ==null ) _aidTags= new java.util.ArrayList();
		if( __aidTags!=null ){ //add aid tags
		
			aa:for(int y=0;y<__aidTags.size();y++){

				for(int x=0;x< _aidTags.size();x++){
	
					if( _aidTags.get(x).getRefId().equals(__aidTags.get(y).getRefId())){
	
						continue aa;
					}//edn if
				}//end x
				
				_aidTags.add(__aidTags.get(y));
			}//edn y
				
			
		}
		
		

		if(__resources!=null) //add resources
			aad:for(int y=0;y<__resources.size();y++){
					for(int x=0;x< _aidTags.size();x++){
						if( _aidTags.get(x).getRefId().equals(__resources.get(y).getRefId()))
							continue aad;
					}
				_aidTags.add(__resources.get(y));

			}
				
				
			
		
		
		
		meeting.setLastAssetUpdate(new java.util.Date());
		meeting.setAssets(_aidTags);	
		
		
		
		
		
		
			//end
			
			
			int meetingLength = 0;
			for (Activity _agenda : _activities) {
				meetingLength += _agenda.getDuration();
			}

			planView.setMeetingCount(meetingCount);
			planView.setMeetingLength(meetingLength);

		}

		if (meeting != null)
			meeting.setMeetingInfo(meetingInfo);
		planView.setMeeting(meeting);
		planView.setAidTags(_aidTags);		
		return planView;
	}

	public PlanView planView1(User user, Troop troop,
			javax.servlet.http.HttpServletRequest request)
			throws IllegalAccessException, VtkException {

		PlanView planView = new PlanView();
		HttpSession session = request.getSession();

		java.util.Map<java.util.Date, YearPlanComponent> sched = null;
		if( troop.getYearPlan()!=null )
			sched = getYearPlanSched(user, troop, troop.getYearPlan(), false, false);

		if (sched == null || (sched.size() == 0)) {
			
			return null;
		}
		java.util.List<java.util.Date> dates = new java.util.ArrayList<java.util.Date>(
				sched.keySet());
		long nextDate = 0, prevDate = 0;
		java.util.Date searchDate = null;

		if ((request.getParameter("elem") != null && !request.getParameter("elem").equals("first")) || 
		    (request.getAttribute("elem") != null && !request.getAttribute("elem").equals("first"))) {
			
			String elem = request.getParameter("elem");
			if (elem == null) {
				elem = (String)request.getAttribute("elem");
			}
			searchDate = new java.util.Date(Long.parseLong(elem));
		} else if (false) {// session.getValue("VTK_planView_memoPos") !=null ){
			searchDate = new java.util.Date(
					(Long) session.getValue("VTK_planView_memoPos"));
		} else {

			if (troop.getYearPlan().getSchedule() == null)
				searchDate = (java.util.Date) sched.keySet().iterator().next();
			else {

				java.util.Iterator itr = sched.keySet().iterator();
				while (itr.hasNext()) {
					searchDate = (java.util.Date) itr.next();
					if (searchDate.after(new java.util.Date()))
						break;

				}
			}

		}

		int currInd = dates.indexOf(searchDate);
		if (dates.size() - 1 > currInd)
			nextDate = ((java.util.Date) dates.get(currInd + 1)).getTime();
		if (currInd > 0)
			prevDate = ((java.util.Date) dates.get(currInd - 1)).getTime();
		session.putValue("VTK_planView_memoPos", searchDate.getTime());
		YearPlanComponent _comp = sched.get(searchDate);
		planView.setSearchDate(searchDate);
		planView.setPrevDate(prevDate);
		planView.setNextDate(nextDate);
		planView.setCurrInd(currInd);
		planView.setYearPlanComponent(_comp);

		return planView;
	}

	public java.util.List<MeetingE> getMeetingToCancel(User user, Troop troop)
			throws IllegalAccessException, VtkException {

		java.util.List<MeetingE> meetings = new java.util.ArrayList();
		java.util.Date today = new java.util.Date();
		java.util.Map<java.util.Date, YearPlanComponent> sched = getYearPlanSched(
				user, troop, troop.getYearPlan(), false, false);
		java.util.Iterator itr = sched.keySet().iterator();

		while (itr.hasNext()) {
			java.util.Date date = (Date) itr.next();
			YearPlanComponent ypc = sched.get(date);
			if (date.after(today)
					&& ypc.getType() == YearPlanComponentType.MEETING) {
				MeetingE MEETING = (MeetingE) ypc;
				Meeting meetingInfo = yearPlanUtil.getMeeting(user, troop, 
						MEETING.getRefId());
				MEETING.setMeetingInfo(meetingInfo);
				meetings.add(MEETING);
			}
		}
		return meetings;
	}

	public boolean rmSchedDate(User user, Troop troop, long dateToRm)
			throws IllegalAccessException, VtkException {
		boolean isRemoved = false;
		String dates = troop.getYearPlan().getSchedule().getDates();
		dates = "," + dates + ",";
		dates = dates.replace("," + dateToRm + ",", ",");
		dates = dates.replace(",,", ",");
		if (dates.startsWith(","))
			dates = dates.substring(1);
		if (dates.endsWith(","))
			dates = dates.substring(0, dates.length() - 1);
		troop.getYearPlan().getSchedule().setDates(dates);

		/*
		String exclDates = troop.getYearPlan().getCalExclWeeksOf();
		exclDates = exclDates == null ? "" : exclDates;
		if (exclDates.endsWith(",") || exclDates.equals(""))
			exclDates += FORMAT_MMddYYYY.format(new java.util.Date(dateToRm))
					+ ",";
		else
			exclDates += ","
					+ FORMAT_MMddYYYY.format(new java.util.Date(dateToRm))
					+ ",";
		troop.getYearPlan().setCalExclWeeksOf(exclDates);
		*/
		troopUtil.updateTroop(user, troop);
		isRemoved = true;
		return isRemoved;
	}

	public boolean rmMeeting(User user, Troop troop, String meetingRefId)
			throws IllegalAccessException {
		boolean isRemoved = false;
		java.util.List<MeetingE> meetings = troop.getYearPlan().getMeetingEvents();
		for (int i = 0; i < meetings.size(); i++) {
			if (meetings.get(i).getRefId().equals(meetingRefId)) {
				troopDAO.removeMeeting(user, troop, meetings.get(i));
				meetings.remove(i);
			}
		}
		isRemoved = true;
		return isRemoved;
	}

	public boolean updateAttendance(User user, Troop troop,
			javax.servlet.http.HttpServletRequest request) {

		String mid = request.getParameter("mid");
		String attendances[] = null;
		if (request.getParameter("attendance") != null) {
			int i = 0;
			StringTokenizer t = new StringTokenizer(
					request.getParameter("attendance"), ",");
			while (t.hasMoreElements()) {
				if (attendances == null)
					attendances = new String[t.countTokens()];
				attendances[i] = t.nextToken();
				i++;
			}
		}
		java.util.List<org.girlscouts.vtk.models.Contact> contacts = new org.girlscouts.vtk.auth.dao.SalesforceDAO(
				troopDAO, connectionFactory).getContacts(user.getApiConfig(),
				troop.getSfTroopId());
		String path = VtkUtil.getYearPlanBase(user, troop) + troop.getSfCouncil() + "/troops/"
				+ troop.getSfTroopId() + "/yearPlan/meetingEvents/" + mid
				+ "/attendance";
		java.util.List<String> Attendances = new java.util.ArrayList<String>();
		Attendance ATTENDANCES = getAttendance(user, troop, path);
		if (ATTENDANCES == null) {
			ATTENDANCES = new Attendance();
			ATTENDANCES.setPath(path);
		}

		if (ATTENDANCES != null && ATTENDANCES.getUsers() != null) {
			StringTokenizer t = new StringTokenizer(ATTENDANCES.getUsers(), ",");
			while (t.hasMoreElements())
				Attendances.add(t.nextToken());
		}

		java.util.List<String> CURRENT_CONTACT_LIST = new java.util.ArrayList<String>();
		for (int i = 0; i < contacts.size(); i++)
			CURRENT_CONTACT_LIST.add(contacts.get(i).getId());

		// add
		if (attendances != null)
			for (int i = 0; i < attendances.length; i++) {
				if (!Attendances.contains(attendances[i]))
					Attendances.add(attendances[i]);
			}

		// rm
		java.util.List<String> attendances_toRm = new java.util.ArrayList<String>();
		for (int i = 0; i < Attendances.size(); i++) {
			String contactId = Attendances.get(i);

			boolean isExists = false;
			if (CURRENT_CONTACT_LIST.contains(contactId))
				if (attendances != null)
					for (int y = 0; y < attendances.length; y++)
						if (attendances[y].equals(contactId))
							isExists = true;

			if (!isExists)
				attendances_toRm.add(contactId);
		}

		for (int i = 0; i < attendances_toRm.size(); i++)
			Attendances.remove(attendances_toRm.get(i));

		String _attendances = "";
		if (Attendances != null)
			for (int i = 0; i < Attendances.size(); i++)
				_attendances += Attendances.get(i) + ",";

		ATTENDANCES.setUsers(_attendances);
		//count the number of contacts that are not null
		int cTotal = 0;
		for(int i=0;i<contacts.size();i++){
			if(contacts.get(i).getId()!=null){	
				cTotal++;
			}
		}
		ATTENDANCES.setTotal(cTotal);
		setAttendance(user, troop, mid, ATTENDANCES);

		return false;
	}

	public Attendance getAttendance(User user, Troop troop, String mid) {

		return meetingDAO.getAttendance(user, troop, mid);
	}

	public boolean setAttendance(User user, Troop troop, String mid,
			Attendance attendance) {
		return meetingDAO.setAttendance(user, troop, mid, attendance);
	}

	public Achievement getAchievement(User user, Troop troop, String mid) {

		return meetingDAO.getAchievement(user, troop, mid);
	}

	public boolean setAchievement(User user, Troop troop, String mid,
			Achievement achievement) {

		return meetingDAO.setAchievement(user, troop, mid, achievement);
	}

	public boolean updateAchievement(User user, Troop troop,
			javax.servlet.http.HttpServletRequest request) {

		String mid = request.getParameter("mid");
		String attendances[] = null;
		if (request.getParameter("achievement") != null) {
			int i = 0;
			StringTokenizer t = new StringTokenizer(
					request.getParameter("achievement"), ",");
			while (t.hasMoreElements()) {

				if (attendances == null)
					attendances = new String[t.countTokens()];
				attendances[i] = t.nextToken();

				i++;
			}
		}

		java.util.List<org.girlscouts.vtk.models.Contact> contacts = new org.girlscouts.vtk.auth.dao.SalesforceDAO(
				troopDAO, connectionFactory).getContacts(user.getApiConfig(),
				troop.getSfTroopId());
		String path = VtkUtil.getYearPlanBase(user, troop) + troop.getSfCouncil() + "/troops/"
				+ troop.getSfTroopId() + "/yearPlan/meetingEvents/" + mid
				+ "/achievement";
		java.util.List<String> Attendances = new java.util.ArrayList<String>();
		Achievement ATTENDANCES = getAchievement(user, troop, path);
		if (ATTENDANCES == null) {
			ATTENDANCES = new Achievement();
			ATTENDANCES.setPath(path);
		}

		if (ATTENDANCES != null && ATTENDANCES.getUsers() != null) {
			StringTokenizer t = new StringTokenizer(ATTENDANCES.getUsers(), ",");
			while (t.hasMoreElements())
				Attendances.add(t.nextToken());
		}

		java.util.List<String> CURRENT_CONTACT_LIST = new java.util.ArrayList<String>();
		for (int i = 0; i < contacts.size(); i++)
			CURRENT_CONTACT_LIST.add(contacts.get(i).getId());

		// add
		if (attendances != null)
			for (int i = 0; i < attendances.length; i++) {
				if (!Attendances.contains(attendances[i]))
					Attendances.add(attendances[i]);
			}

		// rm
		java.util.List<String> attendances_toRm = new java.util.ArrayList<String>();
		for (int i = 0; i < Attendances.size(); i++) {
			String contactId = Attendances.get(i);

			boolean isExists = false;

			if (CURRENT_CONTACT_LIST.contains(contactId))
				if (attendances != null)
					for (int y = 0; y < attendances.length; y++) {
						if (attendances[y].equals(contactId))
							isExists = true;
					}
			if (!isExists) {

				attendances_toRm.add(contactId);
			}
		}

		for (int i = 0; i < attendances_toRm.size(); i++)
			Attendances.remove(attendances_toRm.get(i));

		String _attendances = "";
		if (Attendances != null)
			for (int i = 0; i < Attendances.size(); i++)
				_attendances += Attendances.get(i) + ",";

		ATTENDANCES.setUsers(_attendances);
		ATTENDANCES.setTotal(contacts.size());
		setAchievement(user, troop, mid, ATTENDANCES);

		return false;
	}

	public void saveEmail(User user, Troop troop, String meetingId) {

		java.util.List<MeetingE> meetings = troop.getYearPlan().getMeetingEvents();
		for (int i = 0; i < meetings.size(); i++) {
			MeetingE meeting = meetings.get(i);
System.err.println("TTR1: "+meetingId);			
			if (meeting.getUid().equals(meetingId)) {
				try {
					SentEmail email = new SentEmail(troop.getSendingEmail());
					java.util.List<SentEmail> emails = meeting.getSentEmails();
System.err.println("TTR2: " + (emails ==null ? "NULL" : emails.size()));				
					emails = emails == null ? new java.util.ArrayList<SentEmail>() : emails;
					emails.add(email);
System.err.println("TTR3: " +  emails.size());
					meeting.setSentEmails(emails);
					
					if (meeting.getEmlTemplate() == null) {
						meeting.setEmlTemplate(troop.getSendingEmail().getTemplate());
					}
					meetingDAO.updateMeetingEvent(user, troop, meeting);
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		java.util.List<Activity> activities = troop.getYearPlan()
				.getActivities();
		for (int i = 0; i < activities.size(); i++) {
			Activity activity = activities.get(i);
			if (activity.getUid().equals(meetingId)) {
				try {
					SentEmail email = new SentEmail(troop.getSendingEmail());
					java.util.List<SentEmail> emails = activity.getSentEmails();
					emails = emails == null ? new java.util.ArrayList<SentEmail>()
							: emails;
					emails.add(email);

					activity.setSentEmails(emails);
					if (activity.getEmlTemplate() == null) {
						activity.setEmlTemplate(troop.getSendingEmail()
								.getTemplate());
					}
					activityDAO.updateActivity(user, troop, activity);
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void createMeetingCanceled(User user, Troop troop,
			String meetingRefId, long meetingDate)
			throws IllegalAccessException, VtkException {

		MeetingCanceled meeting = new MeetingCanceled();
		meeting.setDate(new java.util.Date(meetingDate));
		meeting.setRefId(meetingRefId);
		meeting.setCancelled("true");
		meeting.setDbUpdate(true);
		java.util.List<MeetingCanceled> meetingsCanceled = troop.getYearPlan()
				.getMeetingCanceled();
		meetingsCanceled = meetingsCanceled == null ? new java.util.ArrayList<MeetingCanceled>()
				: meetingsCanceled;
		meetingsCanceled.add(meeting);
		troop.getYearPlan().setMeetingCanceled(meetingsCanceled);
		troopDAO.updateTroop(user, troop);
	}

	public void createCustomYearPlan(User user, Troop troop, String mids)
			throws IllegalAccessException, VtkYearPlanChangeException, VtkException {
		troopUtil.selectYearPlan(user, troop, "", "Custom Year Plan");
		StringTokenizer t = new StringTokenizer(mids, ",");
		while (t.hasMoreElements())
			addMeetings(user, troop, t.nextToken());
	}

	public void rmExtraMeetingsNotOnSched(User user, Troop troop) 
			throws IllegalAccessException {
		String dates = troop.getYearPlan().getSchedule().getDates();
		StringTokenizer t = new StringTokenizer(dates, ",");
		int meetingDatesCount = t.countTokens();
		while (meetingDatesCount < troop.getYearPlan().getMeetingEvents()
				.size()) {
			rmMeeting(
					user,
					troop,
					troop.getYearPlan()
							.getMeetingEvents()
							.get(troop.getYearPlan().getMeetingEvents().size() - 1)
							.getRefId());
		}
	}

	public MeetingE getMeetingE( User user, Troop troop, String meetingEpath) throws IllegalAccessException, VtkException{
		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_VIEW_MEETING_ID))
			throw new IllegalAccessException();


		return meetingDAO.getMeetingE(user, troop, meetingEpath);
	}
	
	
	
	public java.util.List<Note> getNotes(User user, Troop troop, String path)
			throws IllegalAccessException, VtkException{
		return meetingDAO.getNotes( user, troop, path);
	}
	
	public java.util.List<Note> getNotesByMid(User user, Troop troop, String mid)
			throws IllegalAccessException, VtkException{
		MeetingE meeting = VtkUtil.findMeetingById( troop.getYearPlan().getMeetingEvents(), mid );
		return meetingDAO.getNotes( user, troop, meeting.getUid() );//meeting.getPath() );
	}
	
	
	 
	public boolean updateNote(User user, Troop troop,Note  note) throws IllegalAccessException{
		
		return meetingDAO.updateNote( user, troop, note);
	}
	
	public Note getNote(User user, Troop troop, String noteId)
			throws IllegalAccessException, VtkException{
		return meetingDAO.getNote( user, troop, noteId);
	}
	public boolean rmNote(User user, Troop troop,String  noteId) throws IllegalAccessException, VtkException{
		System.err.println("inRmNote MeetingUtil: " +noteId ); 
		return meetingDAO.rmNote(user, troop, noteId);
	}
	/*
	public java.util.List<Meeting> getMeetings(int gsYear){
		return meetingDAO.getMeetings(gsYear);
		}
		*/
	
	public void createOrUpdateCustomMeeting(User user, Troop troop, MeetingE meetingE ) throws IllegalAccessException, VtkException{
		createOrUpdateCustomMeeting( user, troop, meetingE, meetingE.getMeetingInfo());
	}
	
	public void createOrUpdateCustomMeeting(User user, Troop troop, MeetingE meetingE , Meeting meetingInfo) throws IllegalAccessException, VtkException{
				
				/*
				MeetingE meetingE = getMeeting(troop.getYearPlan().getMeetingEvents(),
						meetingPath);
						*/
				if (meetingE.getRefId().contains("_"))
					meetingDAO.updateCustomMeeting(user, troop, meetingE, meetingInfo);
				else
					meetingDAO.createCustomMeeting(user, troop, meetingE, meetingInfo);

				troop.getYearPlan().setAltered("true");
				troopUtil.updateTroop(user, troop);
	}
	
	public boolean updateActivityOutdoorStatus(User user, Troop troop, String mid, String aid, boolean isOutdoor) throws IllegalAccessException, VtkException{
	
		MeetingE meeting = VtkUtil.findMeetingById( troop.getYearPlan().getMeetingEvents(), mid );
        Activity activity = VtkUtil.findActivityByPath( meeting.getMeetingInfo().getActivities(), aid );
        return updateActivityOutdoorStatus( user, troop, meeting, activity, isOutdoor );
        
    }

	public boolean updateActivityOutdoorStatus(User user, Troop troop, MeetingE meetingE, Activity activity, boolean isOutdoor) throws IllegalAccessException, VtkException{
		boolean isUpdated= false;
		for(int i=0;i<meetingE.getMeetingInfo().getActivities().size();i++){
			if( activity.getUid().equals( meetingE.getMeetingInfo().getActivities().get(i).getUid()) ){
				meetingE.getMeetingInfo().getActivities().get(i).setIsOutdoor(isOutdoor);
				createOrUpdateCustomMeeting(user, troop, meetingE );
				isUpdated=true;
			}
		}	
		return isUpdated;
	}
	
	public Note addNote(User user, Troop troop, String mid, String message) throws java.lang.IllegalAccessException, org.girlscouts.vtk.utils.VtkException{
		if (mid == null || message == null || message.trim().equals("")) {
			return null;
		}
		MeetingE meeting = VtkUtil.findMeetingById(troop.getYearPlan().getMeetingEvents(), mid);

		java.util.List<Note> notes = meeting.getNotes();
		if (notes == null)
			notes = new java.util.ArrayList<Note>();
		Note note = new Note();
		note.setMessage(message);
		note.setCreatedByUserId(user.getApiConfig().getUser().getSfUserId());
		note.setCreatedByUserName(user.getApiConfig().getUser().getName());
		note.setCreateTime(new java.util.Date().getTime());
		note.setRefId(meeting.getUid());
		note.setPath(meeting.getPath() + "/notes/" + note.getUid());
		notes.add(note);

		meeting.setNotes(notes);

		troopUtil.updateTroop(user, troop);
		return note;

	}
	
	public boolean editNote(User user, Troop troop, String noteId, String msg)throws java.lang.IllegalAccessException, org.girlscouts.vtk.utils.VtkException{
		 boolean isEdit= false;
        
         Note note= getNote(user, troop, noteId);
         if( note!=null && msg!=null && !msg.equals("") ){
                  note.setMessage( msg );
                  isEdit= updateNote(user, troop, note);
         }//edn if

         return isEdit;
	}
	
	public void addMeetings(User user, Troop troop, String meetings[]) throws java.lang.IllegalAccessException, org.girlscouts.vtk.utils.VtkException{
        for(int i=0;i<meetings.length;i++){
            addMeetings(user, troop, meetings[i] );
        }
	}
	
	
	
}// edn class
