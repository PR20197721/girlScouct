package org.girlscouts.vtk.dao;

import java.util.List;

import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.SearchTag;
import org.girlscouts.vtk.models.user.User;

public interface MeetingDAO {

	
	public java.util.List<Meeting> getAllMeetings(String gradeLevel); 
	public java.util.List<MeetingE> getAllEventMeetings(String yearPlanId);
	public Meeting getMeeting(String path);
	public java.util.List<MeetingE> getAllEventMeetings_byPath(String yearPlanPath);
	public java.util.List<MeetingE> getAllUsersEventMeetings(User user, String yearPlanId);
	public Meeting createCustomMeeting(User user, MeetingE meetingEvent);
	public Meeting createCustomMeeting(User user, MeetingE meetingEvent, Meeting meeting);
	public Meeting addActivity(Meeting meeting, Activity activity);
	List<Meeting> search();
	public List<org.girlscouts.vtk.models.Search> getData(String query);
	//public List<org.girlscouts.vtk.models.Search> getAidTag(String tags, String meetingName);
	//public List<org.girlscouts.vtk.models.Search> getAidTag_custasset(String uid);
	public java.util.List<Asset> getAids(String tags, 
			String meetingName, String uids);
	
	
	
	public net.fortuna.ical4j.model.Calendar yearPlanCal(User user )throws Exception;
	public java.util.List<Asset> getResources(String tags, 
			String meetingName, String uids);
	
	public SearchTag searchA();
	public java.util.List<Activity> searchA1(User user, String lvl, String cat, String keywrd,
			java.util.Date startDate, java.util.Date endDate, String region);
	public java.util.Map<String, String> searchRegion(); //pull distinct regions
	
	
	public  List<Asset> getAllResources(String path) ;
	public  Asset getAsset(String _path);
}
