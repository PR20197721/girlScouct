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
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.YearPlanComponent;

@Component
@Service(LocationUtil.class)
public class LocationUtil {
	
    @Reference
    TroopUtil troopUtil;
    
    @Reference
    private UserUtil userUtil;
    
    @Reference
    private MeetingDAO meetingDAO;

	public void setLocationAllMeetings( User user, Troop troop, String locationPath ) throws java.lang.IllegalAccessException{
		
		if( !userUtil.hasAccess(troop, troop.getCurrentTroop(), Permission.PERMISSION_EDIT_MEETING_ID ) ){
			 troop.setErrCode("112");
			 throw new IllegalAccessException();
		 }
		
		YearPlan plan= troop.getYearPlan();
		List <MeetingE> meetings = plan.getMeetingEvents();
		for(int i=0;i<meetings.size();i++){
			
			MeetingE meeting = meetings.get(i);
			meeting.setLocationRef(locationPath);
			
		}
		
		troopUtil.updateTroop(user, troop);
		
	}
	
  public void setMeetingLocation( Troop user, MeetingE meeting, String locationPath ){
		
		//TODO
	}
  
  public void setLocation(User user, Troop troop, Location location) throws java.lang.IllegalAccessException{
	  
	  if( !userUtil.hasAccess(troop, troop.getCurrentTroop() , Permission.PERMISSION_EDIT_MEETING_ID) ){
			 troop.setErrCode("112");
			 throw new IllegalAccessException();
		 }
	  
		YearPlan plan = troop.getYearPlan();
		java.util.List <Location> locations = plan.getLocations();
		
		//boolean isFirst =false;
		if(locations==null){ locations= new java.util.ArrayList<Location>();  }
		//if( locations.size()<=0) isFirst=true;
		
		locations.add( location );
		
		//Location newLoc =  locations.get(locations.size()-1) ;	
		plan.setLocations(locations);
		
		troop.setYearPlan(plan);
		troopUtil.updateTroop(user, troop);
		
		
		
				
				
				
		
  }
  public void changeLocation(User user, Troop troop, String dates, String locationRef) throws java.lang.IllegalAccessException{
	  
	  if( !userUtil.hasAccess(troop, troop.getCurrentTroop() , Permission.PERMISSION_EDIT_MEETING_ID) ){
			 troop.setErrCode("112");
			 //return;
			 throw new IllegalAccessException();
		 }
	  
	  java.util.List <String> processedMeetings =new java.util.ArrayList();
	 	
		java.util.List <Activity> activities = troop.getYearPlan().getActivities();
		java.util.List <MeetingE> meetings = troop.getYearPlan().getMeetingEvents();
		
		java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(troop.getYearPlan());
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
		
	troopUtil.updateTroop(user, troop);
		
		
		
	  
  }
  
  
  
  
  
	public void setLocationAllEmpty(User user,  Troop troop, String locationName )throws java.lang.IllegalAccessException{
		
		if( user!= null && ! userUtil.hasPermission(user.getPermissions(), Permission.PERMISSION_EDIT_MEETING_ID) )
			throw new IllegalAccessException();
		
		if( user!=null && !userUtil.isCurrentTroopId(troop, troop.getCurrentTroop())){
			troop.setErrCode("112");
			throw new IllegalStateException();
		}
		
		YearPlan plan= troop.getYearPlan();
		
		Location location = null;
		for(int i=0;i<troop.getYearPlan().getLocations().size();i++)
			if( troop.getYearPlan().getLocations().get(i).getName().equals( locationName) )
				{location= troop.getYearPlan().getLocations().get(i); break;}
		
		List <MeetingE> meetings = plan.getMeetingEvents();
		
		if( location!=null)
		 for(int i=0;i<meetings.size();i++){
			
			MeetingE meeting = meetings.get(i);
			if( meeting.getLocationRef()==null || meeting.getLocationRef().equals(""))
				meeting.setLocationRef(location.getPath());
			
		}
		
		
		troopUtil.updateTroop(user, troop);
		
	}
	
	public void removeLocation(User user, Troop troop, String locationName) {
		try {
			String locationToRmPath = meetingDAO.removeLocation(user, troop,
					locationName);
			YearPlan plan = troop.getYearPlan();

			// update every refLoc & set 2 null
			java.util.List<MeetingE> meetings = plan.getMeetingEvents();
			for (int i = 0; i < meetings.size(); i++) {
				if (meetings.get(i).getLocationRef() != null
						&& meetings.get(i).getLocationRef()
								.equals(locationToRmPath)) {
					meetings.get(i).setLocationRef("");
				}
			}
			troopUtil.updateTroop(user, troop);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
}
