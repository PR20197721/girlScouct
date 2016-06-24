package org.girlscouts.vtk.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.girlscouts.vtk.models.Achievement;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.models.Attendance;
import org.girlscouts.vtk.models.Council;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.models.SearchTag;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.SentEmail;
import org.girlscouts.vtk.models.bean_resource;
import org.girlscouts.vtk.utils.VtkException;


public interface MeetingDAO {

	public java.util.List<Meeting> getAllMeetings(User user, Troop troop, String gradeLevel)
			throws IllegalAccessException;

	public java.util.List<MeetingE> getAllEventMeetings(User user,Troop troop,
			String yearPlanId) throws IllegalAccessException;

	public Meeting getMeeting(User user, Troop troop,String path)
			throws IllegalAccessException, VtkException;

	public java.util.List<MeetingE> getAllEventMeetings_byPath(User user,Troop troop,
			String yearPlanPath) throws IllegalAccessException;

	public java.util.List<MeetingE> getAllUsersEventMeetings(User user,
			Troop troop, String yearPlanId) throws IllegalStateException,
			IllegalAccessException;

	public Meeting createCustomMeeting(User user, Troop troop,
			MeetingE meetingEvent) throws IllegalAccessException;

	public Meeting createCustomMeeting(User user, Troop troop,
			MeetingE meetingEvent, Meeting meeting)
			throws IllegalAccessException;

	public Meeting addActivity(User user, Troop troop, Meeting meeting,
			Activity activity) throws IllegalAccessException;

	List<Meeting> search();

	public List<org.girlscouts.vtk.models.Search> getData(User user,
			Troop troop, String query) throws IllegalAccessException;

	public SearchTag searchA(User user, Troop troop, String councilCode)
			throws IllegalAccessException;

	public java.util.List<Activity> searchA1(User user, Troop troop,
			String lvl, String cat, String keywrd, java.util.Date startDate,
			java.util.Date endDate, String region)
			throws IllegalAccessException;

	public List<Asset> getAllResources(User user, Troop troop, String path)
			throws IllegalAccessException;

	public Asset getAsset(User user, Troop troop,String _path)
			throws IllegalAccessException;

	public java.util.List<Asset> getGlobalResources(String resourceTags); // delim
																			// ';'

	public Meeting updateCustomMeeting(User user, Troop troop,
			MeetingE meetingEvent, Meeting meeting)
			throws IllegalAccessException;

	public Council getCouncil(User user, Troop troop, String councilId)
			throws IllegalAccessException;

	public java.util.List<Milestone> getCouncilMilestones(String councilCode);

	public void saveCouncilMilestones(java.util.List<Milestone> milestones);

	public String removeLocation(User user, Troop troop, String locationName)
			throws IllegalAccessException;

	public List<Asset> getAidTag_local(User user, Troop troop, String tags,
			String meetingName, String meetingPath) throws IllegalAccessException;

	public List<Asset> getAidTag(User user,Troop troop, String tags, String meetingName)
			throws IllegalAccessException;

	public List<Asset> getResource_local(User user, Troop troop,
			String meetingName, String meetingPath) throws IllegalAccessException;

	public List<Asset> getResource_global(User user,Troop troop, String tags,
			String meetingName) throws IllegalAccessException;

	public Attendance getAttendance(User user, Troop troop, String mid);

	public boolean setAttendance(User user, Troop troop, String mid,
			Attendance attendance);

	public Achievement getAchievement(User user, Troop troop, String mid);

	public boolean setAchievement(User user, Troop troop, String mid,
			Achievement a);	
	
	public boolean updateMeetingEvent(User user, Troop troop, MeetingE meeting)
			throws IllegalAccessException, IllegalStateException;
	
	public MeetingE getMeetingE(User user, Troop troop,String path)
			throws IllegalAccessException, VtkException ;
	
	public int getAllResourcesCount(User user, Troop troop,String path)
			throws IllegalAccessException ;
		
	public int getAssetCount(User user, Troop troop, String _path)
			throws IllegalAccessException ;
	public int getCountLocalMeetingAidsByLevel(User user, Troop troop, String _path)
			throws IllegalAccessException;
	
	public Collection<bean_resource> getResourceData(User user, Troop troop, String _path)
			throws IllegalAccessException ;
	public int getMeetingCount(User user, Troop troop, String path) throws IllegalAccessException;
	public int	getVtkAssetCount(User user, Troop troop, String path)throws IllegalAccessException;
	public java.util.List<Meeting> getAllMeetings(User user, Troop troop) throws IllegalAccessException;
}
