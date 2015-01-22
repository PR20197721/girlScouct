package org.girlscouts.vtk.ejb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpSession;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.dao.ActivityDAO;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.dao.TroopDAO;
import org.girlscouts.vtk.dao.YearPlanDAO;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.models.SearchTag;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.YearPlanComponent;

@Component
@Service(value = YearPlanUtil.class)
public class YearPlanUtil {

	@Reference
	TroopDAO troopDAO;

	@Reference
	ActivityDAO activityDAO;

	@Reference
	YearPlanDAO yearPlanDAO;

	@Reference
	private MeetingDAO meetingDAO;

	@Reference
	private UserUtil userUtil;

	public void createActivity(User user, Troop troop, Activity activity)
			throws java.lang.IllegalAccessException {

		activityDAO.createActivity(user, troop, activity);
		troop.getYearPlan().setAltered("true");
		troopDAO.updateTroop(user, troop);
	}

	public void checkCanceledActivity(User user, Troop troop)
			throws java.lang.IllegalAccessException {

		if (troop == null || troop.getYearPlan() == null
				|| troop.getYearPlan().getActivities() == null
				|| troop.getYearPlan().getActivities().size() == 0)
			return;

		java.util.List<Activity> activity2Cancel = new java.util.ArrayList<Activity>();

		java.util.List<Activity> activities = troop.getYearPlan()
				.getActivities();
		for (int i = 0; i < activities.size(); i++) {

			if (!activities.get(i).getIsEditable()
					&& !(activities.get(i).getCancelled() != null && activities
							.get(i).getCancelled().equals("true"))
					&& !activityDAO.isActivityByPath(user, activities.get(i)
							.getRefUid())) {

				activities.get(i).setCancelled("true"); // org
				activity2Cancel.add(activities.get(i));
				// troopDAO.updateTroop(user, troop);
			}
		}

		/*
		 * if need to remove canceled activ auto - uncomment for (Activity a :
		 * activity2Cancel){
		 * 
		 * if (activities.contains(a)){
		 * 
		 * activities.remove(a); } }
		 */

		if (activity2Cancel != null && activity2Cancel.size() > 0)
			troopDAO.updateTroop(user, troop);
	}

	public List<YearPlan> getAllYearPlans(User user, String ageLevel) {
		return yearPlanDAO.getAllYearPlans(user, ageLevel);
	}

	public YearPlan getYearPlan(String path) {
		return yearPlanDAO.getYearPlan(path);
	}

	public java.util.List<Asset> getAids(User user, String tags,
			String meetingName, String uids) throws IllegalAccessException {
		java.util.List<Asset> container = new java.util.ArrayList();
		container.addAll(meetingDAO.getAidTag_local(user, tags, meetingName));
		container.addAll(meetingDAO.getAidTag(user, tags, meetingName));

		return container;
	}

	@SuppressWarnings("unchecked")
	public net.fortuna.ical4j.model.Calendar yearPlanCal(User user, Troop troop)
			throws Exception {

		java.util.Map<java.util.Date, YearPlanComponent> sched = new MeetingUtil()
				.getYearPlanSched(troop.getYearPlan());
		if (!userUtil.hasPermission(troop,
				Permission.PERMISSION_VIEW_MEETING_ID))
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
				Meeting meetingInfo = meetingDAO.getMeeting(user,
						((MeetingE) _comp).getRefId());
				desc = meetingInfo.getName();
				location = getLocation(troop,
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

	private String getLocation(Troop user, String locationId) {

		String fmtLocation = "";
		if (locationId == null
				|| user == null
				|| !userUtil.hasPermission(user,
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

	public java.util.List<Asset> getResources(User user, String tags,
			String meetingName, String uids) throws IllegalAccessException {
		java.util.List<Asset> container = new java.util.ArrayList();
		container.addAll(meetingDAO.getResource_local(user, tags, meetingName));
		container
				.addAll(meetingDAO.getResource_global(user, tags, meetingName));
		return container;
	}

	public java.util.List<Milestone> getCouncilMilestones(String councilCode) {
		return meetingDAO.getCouncilMilestones(councilCode);
	}

	public Meeting getMeeting(User user, String path)
			throws IllegalAccessException {

		Meeting meeting = meetingDAO.getMeeting(user, path);
		/*
		 * //sort agendas; set activNum java.util.List<Activity> activities =
		 * meeting.getActivities(); for(int i=0;i<activities.size();i++){
		 * activities.get(i).setActivityNumber(i+1); }
		 */

		return meeting;
	}

	public List<org.girlscouts.vtk.models.Search> getData(User user,
			Troop troop, String query) throws IllegalAccessException {
		return meetingDAO.getData(user, troop, query);
	}

	public java.util.List<Meeting> getAllMeetings(User user, String gradeLevel)
			throws IllegalAccessException {
		return meetingDAO.getAllMeetings(user, gradeLevel);
	}

	public java.util.List<MeetingE> getAllEventMeetings_byPath(User user,
			String yearPlanPath) throws IllegalAccessException {
		return meetingDAO.getAllEventMeetings_byPath(user,
				yearPlanPath.endsWith("/") ? yearPlanPath : yearPlanPath + "/");
	}

	public SearchTag searchA(User user, String councilCode)
			throws IllegalAccessException {
		return meetingDAO.searchA(user, councilCode);
	}

	public java.util.List<Asset> getGlobalResources(String resourceTags) {
		return meetingDAO.getGlobalResources(resourceTags);
	}

	public Meeting createCustomMeeting(User user, Troop troop,
			MeetingE meetingEvent) throws IllegalAccessException {
		return meetingDAO.createCustomMeeting(user, troop, meetingEvent);
	}

	public Meeting updateCustomMeeting(User user, Troop troop,
			MeetingE meetingEvent, Meeting meeting)
			throws IllegalAccessException {
		return meetingDAO.updateCustomMeeting(user, troop, meetingEvent,
				meeting);
	}

	public void saveCouncilMilestones(java.util.List<Milestone> milestones) {
		meetingDAO.saveCouncilMilestones(milestones);
	}

	public java.util.List<Activity> searchA1(User user, Troop troop,
			String lvl, String cat, String keywrd, java.util.Date startDate,
			java.util.Date endDate, String region)
			throws IllegalAccessException {
		return meetingDAO.searchA1(user, troop, lvl, cat, keywrd, startDate,
				endDate, region);
	}

	public List<Asset> getAllResources(User user, String path)
			throws IllegalAccessException {
		return meetingDAO.getAllResources(user, path);
	}

	public Meeting createCustomMeeting(User user, Troop troop,
			MeetingE meetingEvent, Meeting meeting)
			throws IllegalAccessException {
		return meetingDAO.createCustomMeeting(user, troop, meetingEvent,
				meeting);
	}

	public void createCustActivity(User user, Troop troop,
			java.util.List<org.girlscouts.vtk.models.Activity> activities,
			String activityId) throws IllegalAccessException {

		// java.util.List <org.girlscouts.vtk.models.Activity> activities =
		// (java.util.List
		// <org.girlscouts.vtk.models.Activity>)session.getValue("vtk_search_activity");
		for (int i = 0; i < activities.size(); i++) {
			if (activities.get(i).getUid().equals(activityId)) {
				createActivity(user, troop, activities.get(i));
				break;
			}
		}

	}

	public boolean isYearPlanAltered(User user, Troop troop) {
		if (troop.getYearPlan() != null) {
			if (troop.getYearPlan().getAltered() != null
					&& troop.getYearPlan().getAltered().equals("true")) {

				return true;
			}
		}
		return false;
	}

	public void search(User user, Troop troop,
			javax.servlet.http.HttpServletRequest request) {

		try {
			java.util.Date startDate = null, endDate = null;
			if (request.getParameter("startDate") != null
					&& !request.getParameter("startDate").equals("")) {
				startDate = new java.util.Date(
						request.getParameter("startDate"));
			}
			if (request.getParameter("endDate") != null
					&& !request.getParameter("endDate").equals("")) {
				endDate = new java.util.Date(request.getParameter("endDate"));
			}
			java.util.List activities = searchA1(user, troop,
					request.getParameter("lvl"), request.getParameter("cat"),
					request.getParameter("keywrd"), startDate, endDate,
					request.getParameter("region"));

			HttpSession session = request.getSession();
			session.putValue("vtk_search_activity", activities);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}// edn class
