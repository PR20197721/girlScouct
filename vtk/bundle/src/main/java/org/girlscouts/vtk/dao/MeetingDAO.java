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
	public java.util.List<MeetingE> getAllUsersEventMeetings(Troop user, String yearPlanId);
	public Meeting createCustomMeeting(Troop user, MeetingE meetingEvent);
	public Meeting createCustomMeeting(Troop user, MeetingE meetingEvent, Meeting meeting);
	public Meeting addActivity(Troop user, Meeting meeting, Activity activity);
	List<Meeting> search();
	public List<org.girlscouts.vtk.models.Search> getData(Troop user, String query);
	//public List<org.girlscouts.vtk.models.Search> getAidTag(String tags, String meetingName);
	//public List<org.girlscouts.vtk.models.Search> getAidTag_custasset(String uid);
	public java.util.List<Asset> getAids(String tags, 
			String meetingName, String uids);
	
	
	
	public net.fortuna.ical4j.model.Calendar yearPlanCal(Troop user )throws Exception;
	public java.util.List<Asset> getResources(String tags, 
			String meetingName, String uids);
	
	public SearchTag searchA(String councilCode);
	public java.util.List<Activity> searchA1(Troop user, String lvl, String cat, String keywrd,
			java.util.Date startDate, java.util.Date endDate, String region);
	//public java.util.Map<String, String> searchRegion(); //pull distinct regions
	
	
	public  List<Asset> getAllResources(String path) ;
	public  Asset getAsset(String _path);
	public java.util.List<Asset> getGlobalResources( String resourceTags); // delim ';'
	
	public Meeting updateCustomMeeting(Troop user, MeetingE meetingEvent, Meeting meeting);
	public Council getCouncil(String councilId);
	public java.util.List<Milestone> getCouncilMilestones(String councilCode);
	public void  saveCouncilMilestones(java.util.List<Milestone> milestones);
	
	public boolean isCurrentTroopId(Troop troop, String sId);
	public  boolean hasPermission(Set<Integer> myPermissionTokens, int permissionId);
	public  boolean hasPermission(Troop user, int permissionId);
	public boolean hasAccess(Troop user, String mySessionId, int permissionId);
	public void doX();
	public java.util.Date getLastModif( Troop troop );
}
