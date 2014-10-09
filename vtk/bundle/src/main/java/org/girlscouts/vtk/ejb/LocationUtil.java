package org.girlscouts.vtk.ejb;

import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.dao.TroopDAO;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Location;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.YearPlanComponent;

@Component
@Service(LocationUtil.class)
public class LocationUtil {
    @Reference
    TroopUtil troopUtil;
    
    @Reference
    private MeetingDAO meetingDAO;

	public void setLocationAllMeetings( Troop user, String locationPath ) throws java.lang.IllegalAccessException{
		
		if( !meetingDAO.hasAccess(user, user.getCurrentTroop(), Permission.PERMISSION_EDIT_MEETING_ID ) ){
			 user.setErrCode("112");
			 return;
		 }
		
		YearPlan plan= user.getYearPlan();
		List <MeetingE> meetings = plan.getMeetingEvents();
		for(int i=0;i<meetings.size();i++){
			
			MeetingE meeting = meetings.get(i);
			meeting.setLocationRef(locationPath);
			
		}
		
		
		troopUtil.updateTroop(user);
		
	}
	
  public void setMeetingLocation( Troop user, MeetingE meeting, String locationPath ){
		
		//TODO
	}
  
  public void setLocation(Troop user, Location location) throws java.lang.IllegalAccessException{
	  
	  if( !meetingDAO.hasAccess(user, user.getCurrentTroop() , Permission.PERMISSION_EDIT_MEETING_ID) ){
			 user.setErrCode("112");
			 return;
		 }
	  
		YearPlan plan = user.getYearPlan();
		java.util.List <Location> locations = plan.getLocations();
		
		
		boolean isFirst =false;
		if(locations==null){ locations= new java.util.ArrayList<Location>();  }
		if( locations.size()<=0) isFirst=true;
		
		locations.add( location );
		
		Location newLoc =  locations.get(locations.size()-1) ;
		
		plan.setLocations(locations);
		
		
		user.setYearPlan(plan);
		//plan.setAltered("true");
		troopUtil.updateTroop(user);
		
		
		
				
				
				
		
  }
  public void changeLocation(Troop user, String dates, String locationRef) throws java.lang.IllegalAccessException{
	  
	  if( !meetingDAO.hasAccess(user, user.getCurrentTroop() , Permission.PERMISSION_EDIT_MEETING_ID) ){
			 user.setErrCode("112");
			 return;
		 }
	  
	  java.util.List <String> processedMeetings =new java.util.ArrayList();
	 	
		java.util.List <Activity> activities = user.getYearPlan().getActivities();
		java.util.List <MeetingE> meetings = user.getYearPlan().getMeetingEvents();
		
		java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(user.getYearPlan());
	java.util.Iterator itr= sched.keySet().iterator();
	while(itr.hasNext() ){
		
		java.util.Date date= (java.util.Date) itr.next();
		if( dates.indexOf( "|"+date+"|")!=-1 ){
			
			
			YearPlanComponent comp = sched.get(date);
			switch( comp.getType()){
				case MEETING:
					MeetingE _meeting = (MeetingE) comp;
					for(int i=0;i<meetings.size();i++)
						if( meetings.get(i).getPath().equals(_meeting.getPath())){
								meetings.get(i).setLocationRef(locationRef);
								processedMeetings.add(meetings.get(i).getPath());
						}
								
					break;
				case ACTIVITY:
					Activity _activity = (Activity) comp;
					for(int i=0;i<activities.size();i++)
						if( activities.get(i).getPath().equals( _activity.getPath()))
							activities.get(i).setLocationRef(locationRef);
					break;
			}
			
		}
			
		
	}
	
	
		//go through every meeting and if not on list but location set to locationRef set to null. unchecked from this location
	    for(int i=0;i<meetings.size();i++){
	    	if( meetings.get(i).getLocationRef()!=null && 
	    			meetings.get(i).getLocationRef().equals( locationRef ) &&
	    			!processedMeetings.contains(meetings.get(i).getPath()) )
	    		meetings.get(i).setLocationRef("");
	    	
	    }
		
	troopUtil.updateTroop(user);
		
		
		
	  
  }
  
  
  
  
  
	public void setLocationAllEmpty( Troop user, String locationName )throws java.lang.IllegalAccessException{
		
		if( !meetingDAO.hasAccess(user, user.getCurrentTroop(), Permission.PERMISSION_EDIT_MEETING_ID ) ){
			 user.setErrCode("112");
			 return;
		 }
		
		YearPlan plan= user.getYearPlan();
		
		
		Location location = null;
		for(int i=0;i<user.getYearPlan().getLocations().size();i++)
			if( user.getYearPlan().getLocations().get(i).getName().equals( locationName) )
				{location= user.getYearPlan().getLocations().get(i); break;}
		
				
		
		List <MeetingE> meetings = plan.getMeetingEvents();
		
		if( location!=null)
		 for(int i=0;i<meetings.size();i++){
			
			MeetingE meeting = meetings.get(i);
			if( meeting.getLocationRef()==null || meeting.getLocationRef().equals(""))
				meeting.setLocationRef(location.getPath());
			
		}
		
		
		troopUtil.updateTroop(user);
		
	}
	
	public void removeLocation(Troop user, String locationName) {
		try {
			String locationToRmPath = meetingDAO.removeLocation(user,
					locationName);
			YearPlan plan = user.getYearPlan();

			// update every refLoc & set 2 null
			java.util.List<MeetingE> meetings = plan.getMeetingEvents();
			for (int i = 0; i < meetings.size(); i++) {
				if (meetings.get(i).getLocationRef() != null
						&& meetings.get(i).getLocationRef()
								.equals(locationToRmPath)) {
					meetings.get(i).setLocationRef("");
				}
			}
			troopUtil.updateTroop(user);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
