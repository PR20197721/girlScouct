package org.girlscouts.vtk.dao;

import java.util.List;

import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.user.User;

public interface MeetingDAO {

	
	public java.util.List<Meeting> getAllMeetings(String yearPlanId); //not used
	public java.util.List<MeetingE> getAllEventMeetings(String yearPlanId);
	public Meeting getMeeting(String path);
	public java.util.List<MeetingE> getAllEventMeetings_byPath(String yearPlanPath);
	public java.util.List<MeetingE> getAllUsersEventMeetings(User user, String yearPlanId);
	public Meeting createCustomMeeting(User user, MeetingE meetingEvent);
	public Meeting createCustomMeeting(User user, MeetingE meetingEvent, Meeting meeting);
	public Meeting addActivity(Meeting meeting, Activity activity);
	List<Meeting> search();
	public List<org.girlscouts.vtk.models.Search> getData(String query);
	public List<org.girlscouts.vtk.models.Search> getAidTag(String tags, String meetingName);
}
