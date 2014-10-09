package org.girlscouts.vtk.ejb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
	MeetingDAO meetingDAO;

	public void createActivity(Troop user, Activity activity) throws java.lang.IllegalAccessException{

		activityDAO.createActivity(user, activity);
		user.getYearPlan().setAltered("true");
		troopDAO.updateTroop(user);
	}

	public void checkCanceledActivity(Troop user) throws java.lang.IllegalAccessException{

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
				if (!activityDAO
						.isActivityByPath(activities.get(i).getRefUid())) {
					activities.get(i).setCancelled("true"); // org
					activity2Cancel.add(activities.get(i));
					troopDAO.updateTroop(user);
				}
		}

		for (Activity a : activity2Cancel)
			if (activities.contains(a))
				activities.remove(a);
	}

	
	public List<YearPlan> getAllYearPlans(String ageLevel){
		return yearPlanDAO.getAllYearPlans(ageLevel);
	}
	
	public YearPlan getYearPlan(String path){
		return  getYearPlan(path);
	}
	
	public java.util.List<Asset> getAids(String tags, String meetingName,
			String uids) {
		java.util.List<Asset> container = new java.util.ArrayList();
		container.addAll(meetingDAO.getAidTag_local(tags, meetingName));
		container.addAll(meetingDAO.getAidTag(tags, meetingName)); 
		
		return container;
	}
	
	@SuppressWarnings("unchecked")
	public net.fortuna.ical4j.model.Calendar yearPlanCal(Troop user)
			throws Exception {

		java.util.Map<java.util.Date, YearPlanComponent> sched = new MeetingUtil()
				.getYearPlanSched(user.getYearPlan());
		if (! meetingDAO.hasPermission(user, Permission.PERMISSION_VIEW_MEETING_ID))
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
				Meeting meetingInfo = meetingDAO.getMeeting(((MeetingE) _comp).getRefId());
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
	
	private String getLocation(Troop user, String locationId) {

		String fmtLocation = "";
		if (locationId == null
				|| user == null
				|| !meetingDAO.hasPermission(user,
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
	
	public java.util.List<Asset> getResources(String tags, String meetingName,
			String uids) {
		java.util.List<Asset> container = new java.util.ArrayList();
		container.addAll(meetingDAO.getResource_local(tags, meetingName));
		container.addAll(meetingDAO.getResource_global(tags, meetingName));
		return container;
	}
	
	public java.util.List<Milestone> getCouncilMilestones(String councilCode){
		return meetingDAO.getCouncilMilestones( councilCode);
	}
	
	public Meeting getMeeting(String path){
		return meetingDAO.getMeeting(path);
	}
	
	public List<org.girlscouts.vtk.models.Search> getData(Troop user, String query){
		return meetingDAO.getData(user, query);
	}
	
	public java.util.List<Meeting> getAllMeetings(String gradeLevel){
		return meetingDAO.getAllMeetings(gradeLevel);
	}
	
	public java.util.List<MeetingE> getAllEventMeetings_byPath(String yearPlanPath){
		return meetingDAO.getAllEventMeetings_byPath( yearPlanPath);
	}
	public SearchTag searchA(String councilCode){
		return meetingDAO.searchA( councilCode);
	}
	
	public java.util.List<Asset> getGlobalResources( String resourceTags){
		return meetingDAO.getGlobalResources( resourceTags);
	}
	
	public Meeting createCustomMeeting(Troop user, MeetingE meetingEvent){
		return meetingDAO.createCustomMeeting( user, meetingEvent);
	}
	
	public Meeting updateCustomMeeting(Troop user, MeetingE meetingEvent, Meeting meeting){
		return meetingDAO.updateCustomMeeting( user,  meetingEvent,  meeting);
	}
	
	public void  saveCouncilMilestones(java.util.List<Milestone> milestones){
		 meetingDAO.saveCouncilMilestones( milestones);
	}
	
	public java.util.List<Activity> searchA1(Troop user, String lvl, String cat, String keywrd,
			java.util.Date startDate, java.util.Date endDate, String region){
		return searchA1( user,  lvl,  cat,  keywrd, startDate, endDate,  region);
	}
	
	public  List<Asset> getAllResources(String path) {
		return getAllResources( path) ;
	}
	
	public Meeting createCustomMeeting(Troop user, MeetingE meetingEvent, Meeting meeting){
		return  createCustomMeeting( user,  meetingEvent,  meeting );
	}
}
