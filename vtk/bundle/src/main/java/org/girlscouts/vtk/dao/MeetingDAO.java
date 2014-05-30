package org.girlscouts.vtk.dao;

import org.girlsscout.vtk.models.Meeting;
import org.girlsscout.vtk.models.MeetingE;
import org.girlsscout.vtk.models.user.User;

public interface MeetingDAO {

	
	public java.util.List<Meeting> getAllMeetings(String yearPlanId); //not used
	public java.util.List<MeetingE> getAllEventMeetings(String yearPlanId);
	public Meeting getMeeting(String path);
	public java.util.List<MeetingE> getAllEventMeetings_byPath(String yearPlanPath);
	public java.util.List<MeetingE> getAllUsersEventMeetings(User user, String yearPlanId);
}
