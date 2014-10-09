package org.girlscouts.vtk.dao;

import java.util.List;
import java.util.Set;

import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.models.Council;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.models.SearchTag;
import org.girlscouts.vtk.models.Troop;

public interface MeetingDAO {

	
	public java.util.List<Meeting> getAllMeetings(String gradeLevel); 
	public java.util.List<MeetingE> getAllEventMeetings(String yearPlanId);
	public Meeting getMeeting(String path);
	public java.util.List<MeetingE> getAllEventMeetings_byPath(String yearPlanPath);
	public java.util.List<MeetingE> getAllUsersEventMeetings(Troop user, String yearPlanId) throws IllegalStateException, IllegalAccessException;
	public Meeting createCustomMeeting(Troop user, MeetingE meetingEvent) throws IllegalAccessException;
	public Meeting createCustomMeeting(Troop user, MeetingE meetingEvent, Meeting meeting) throws IllegalAccessException;
	public Meeting addActivity(Troop user, Meeting meeting, Activity activity) throws IllegalAccessException;
	List<Meeting> search();
	public List<org.girlscouts.vtk.models.Search> getData(Troop user, String query) throws IllegalAccessException;
	public SearchTag searchA(String councilCode);
	public java.util.List<Activity> searchA1(Troop user, String lvl, String cat, String keywrd,
			java.util.Date startDate, java.util.Date endDate, String region);

	public  List<Asset> getAllResources(String path) ;
	public  Asset getAsset(String _path);
	public java.util.List<Asset> getGlobalResources( String resourceTags); // delim ';'
	
	public Meeting updateCustomMeeting(Troop user, MeetingE meetingEvent, Meeting meeting) throws IllegalAccessException;
	public Council getCouncil(String councilId);
	public java.util.List<Milestone> getCouncilMilestones(String councilCode);
	public void  saveCouncilMilestones(java.util.List<Milestone> milestones);
	public boolean isCurrentTroopId(Troop troop, String sId);
	public  boolean hasPermission(Set<Integer> myPermissionTokens, int permissionId);
	public  boolean hasPermission(Troop user, int permissionId);
	public boolean hasAccess(Troop user, String mySessionId, int permissionId);
	public java.util.Date getLastModif( Troop troop );
	public String removeLocation(Troop user, String locationName);
	public List<Asset> getAidTag_local(String tags, String meetingName);
	public List<Asset> getAidTag(String tags, String meetingName);
	public List<Asset> getResource_local(String tags, String meetingName);
	public List<Asset> getResource_global(String tags, String meetingName);
	
	//migrate script - 1time only
	public void doX(); 
}
