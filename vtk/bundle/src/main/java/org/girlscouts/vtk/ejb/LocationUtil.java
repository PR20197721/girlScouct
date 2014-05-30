package org.girlscouts.vtk.ejb;

import java.util.List;

import org.girlsscout.vtk.models.Activity;
import org.girlsscout.vtk.models.Location;
import org.girlsscout.vtk.models.MeetingE;
import org.girlsscout.vtk.models.YearPlan;
import org.girlsscout.vtk.models.YearPlanComponent;
import org.girlsscout.vtk.models.user.User;

public class LocationUtil {

	public void setLocationAllMeetings( User user, String locationPath ){
		
		YearPlan plan= user.getYearPlan();
		List <MeetingE> meetings = plan.getMeetingEvents();
		for(int i=0;i<meetings.size();i++){
			
			MeetingE meeting = meetings.get(i);
			meeting.setLocationRef(locationPath);
			
		}
		
		
		new UserDAOImpl().updateUser(user);
		
	}
	
  public void setMeetingLocation( User user, MeetingE meeting, String locationPath ){
		
		//TODO
	}
  
  public static void setLocation(User user, Location location){
	  
		YearPlan plan = user.getYearPlan();
		java.util.List <Location> locations = plan.getLocations();
		if(locations==null) locations= new java.util.ArrayList<Location>();
		locations.add( location );
		plan.setLocations(locations);
		user.setYearPlan(plan);
		
		new UserDAOImpl().updateUser(user);
		
  }
  public static void changeLocation(User user, String dates, String locationRef){
	  
	  
	 	
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
						if( meetings.get(i).getPath().equals(_meeting.getPath()))
								meetings.get(i).setLocationRef(locationRef);
					
					
							
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
		new UserDAOImpl().updateUser(user);
		
		
		
	  
  }
}
