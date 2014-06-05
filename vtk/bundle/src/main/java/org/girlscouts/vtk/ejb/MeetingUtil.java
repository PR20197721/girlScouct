package org.girlscouts.vtk.ejb;

import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.dao.UserDAO;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.YearPlanComponent;
import org.girlscouts.vtk.models.user.User;

@Component
@Service(MeetingUtil.class)
public class MeetingUtil {
    
    @Reference
    UserDAO userDAO;
    
    @Reference
    MeetingDAO meetingDAO;
	
	public java.util.List<MeetingE> updateMeetingPos(java.util.List<MeetingE> orgMeetings, java.util.List <Integer> newPoss){
		

		
		java.util.List<MeetingE> newMeeting = new java.util.ArrayList<MeetingE>();//orgMeetings.size());
		for(int i=0;i<orgMeetings.size();i++) newMeeting.add(null); //TODO 
		
		for(int i=0;i<orgMeetings.size();i++){
			
			MeetingE meeting = orgMeetings.get(i);
			int newpos = newPoss.indexOf(i+1) ;
			newMeeting.set(newpos,  meeting);
			
		}
		
		return newMeeting;
	}
	
	
	
	
	
	public Activity getActivity(String activityId, java.util.List <Activity> activities){
		Activity _activity = null;
		for (Activity activity : activities)
	        if( activity.getId().equals(activityId))
	        {_activity=activity; break;}
		
		return _activity;
	}
	
	
	
	public java.util.Map getYearPlanSched(YearPlan plan){
		
		java.util.Map <java.util.Date,  YearPlanComponent> sched = 
				new java.util.TreeMap<java.util.Date,YearPlanComponent>();
		
		
		List <Activity> activities = plan.getActivities();
		

		if( activities!=null)
		  for(int i=0;i<activities.size();i++){

			sched.put(activities.get(i).getDate(), activities.get(i));
		  }
		
		
		
	java.util.List <MeetingE> meetingEs = plan.getMeetingEvents();
	if( plan.getSchedule() !=null ){
		String calMeeting= plan.getSchedule().getDates();
		StringTokenizer t= new StringTokenizer( calMeeting, ",");
		int count=0;
		while( t.hasMoreElements()){
			
			sched.put( new java.util.Date( Long.parseLong( (t.nextToken() ) ) ) , meetingEs.get(count));
			count++;
		}
	}else{ //no dates: create 1976
		Calendar tmp= java.util.Calendar.getInstance();
		tmp.setTime(new java.util.Date("1/1/1976"));
		
		for(int i=0;i<meetingEs.size();i++){
			
			sched.put( tmp.getTime(), meetingEs.get(i));
			tmp.add( java.util.Calendar.DATE, 1);
		}
	}
		
		
		
		
		
		
		return sched;
	}
	
	
	
	public void changeMeetingPositions(User user, String newPositions){
		
		java.util.List <Integer> newMeetingSetup= new java.util.ArrayList();
		java.util.StringTokenizer t= new java.util.StringTokenizer( newPositions, ",");
		while( t.hasMoreElements() )
			newMeetingSetup.add( Integer.parseInt( t.nextToken() ));
		
		MeetingUtil meetingUtil = new MeetingUtil();
		
		
		java.util.List<MeetingE> rearangedMeetings = 
		           meetingUtil.updateMeetingPos( user.getYearPlan().getMeetingEvents(), newMeetingSetup);

		
		
		YearPlan plan = user.getYearPlan();
		plan.setMeetingEvents(rearangedMeetings);
		user.setYearPlan(plan);
		
		userDAO.updateUser(user);
		
		
	}
	
	
	public void createCustomAgenda(User user, String name, String meetingPath, int duration, long _startTime, String txt ){
		
		java.util.Calendar startTime =  Calendar.getInstance();
		startTime.setTimeInMillis(  _startTime  );
		
			
		java.util.List <MeetingE> meetings = user.getYearPlan().getMeetingEvents();
		for(int i=0;i<meetings.size();i++){
			MeetingE m= meetings.get(i);
			if( m.getPath().equals(meetingPath)){
				
				Meeting meeting =meetingDAO.createCustomMeeting(user, m);
				
				Activity activity= new Activity();
				activity.setName(name);
				activity.setDuration(duration);
				activity.setActivityDescription(txt);
			
				meetingDAO.addActivity( meeting,  activity);
				
				Cal cal = user.getYearPlan().getSchedule();
				cal.addDate( startTime.getTime() );
				

				
			}
		}
		userDAO.updateUser(user);
	}
	
	public void rmCustomActivity (User user, String activityPath ){
		
		
		java.util.List <Activity> activities = user.getYearPlan().getActivities();
		for(int i=0;i<activities.size();i++){
			
			Activity activity= activities.get(i);
			if( activity.getPath().equals(activityPath) )
				activities.remove(activity);
			
		}
		
		userDAO.updateUser(user);
		
	}
	
	public void swapMeetings(User user, String fromPath, String toPath){
		
		java.util.List<MeetingE> meetings = user.getYearPlan().getMeetingEvents();
		for(int i=0;i<meetings.size();i++){
			
			MeetingE meeting = meetings.get(i);
			if( meeting.getPath().equals( fromPath ) ){
			
				meeting.setRefId(toPath);
				
			}
		}
		
		
		userDAO.updateUser(user);
	}

	public void rearrangeActivity(User user, String meetingPath, String _newPoss){
		
		//TOREDO
		java.util.List<Integer> newPoss = new java.util.ArrayList();
		StringTokenizer t= new StringTokenizer( _newPoss, ",");
		while( t.hasMoreElements())
			newPoss.add( Integer.parseInt( t.nextToken() ));
		
		
		System.err.println( "rearrangeActivity "+ meetingPath +" : "+ newPoss);
		Meeting meetingInfo = meetingDAO.getMeeting(  meetingPath );
		java.util.List<Activity>orgActivities = meetingInfo.getActivities();
		java.util.List<Activity> newActivity = new java.util.ArrayList<Activity>();
		for(int i=0;i<orgActivities.size();i++) newActivity.add(null);  
		
		for(int i=0;i<orgActivities.size();i++){
			Activity activity = orgActivities.get(i);
			int newpos = newPoss.indexOf(i+1) ;
			
			
	System.err.println(" NewPos: "+ newpos +" :" + (i+1));
	
	
			newActivity.set(newpos,  activity);
		}
		
		//save activities to meeting
		meetingInfo.setActivities(newActivity);
		
		//create custom meeting
		MeetingE meetingE= getMeeting(user.getYearPlan().getMeetingEvents(), meetingPath);
		meetingDAO.createCustomMeeting(user, meetingE, meetingInfo);
		
	}
	
	
	public MeetingE getMeeting(java.util.List<MeetingE> meetings, String meetingPath){
		
		
		for(int i=0;i<meetings.size();i++){
			if( meetings.get(i).getRefId().equals( meetingPath)){
				System.err.println("getMeet" + meetingPath +" : "+ meetings.get(i).getRefId());
				return meetings.get(i);
			}
		}
		return null;
	}
	
	public void addMeetings(User user, String newMeetingPath){
		
		MeetingE meeting = new MeetingE();
		meeting.setRefId(newMeetingPath);
		
		user.getYearPlan().getMeetingEvents().add(meeting);
		
		if( user.getYearPlan().getSchedule()!=null ){
			
			StringTokenizer t= new StringTokenizer( user.getYearPlan().getSchedule().getDates(), ",");
			//Date first = new Date( Integer.parseInt(t.nextToken()));
			//Date second = new Date( Integer.parseInt(t.nextToken()));
			//int dayDiff = Days.daysBetween(new DateTime(Long.parseLong(t.nextToken())),new DateTime(Long.parseLong(t.nextToken())) ).getDays();
			
			long firstDate=Long.parseLong(t.nextToken());
			long secondDate=Long.parseLong(t.nextToken());
			long diff = secondDate- firstDate;
			
			while( t.hasMoreElements() )
				secondDate = Long.parseLong(t.nextToken());
			
			user.getYearPlan().getSchedule().setDates( user.getYearPlan().getSchedule().getDates() + ""+ (secondDate+diff)+","); 
			
			
		}
		userDAO.updateUser(user);
	
	}
	
	
	public void rmAgenda(User user, String agendaPathToRm , String fromMeetingPath  ){
		
		for(int i=0;i< user.getYearPlan().getMeetingEvents().size();i++){
			
			if( user.getYearPlan().getMeetingEvents().get(i).getPath().equals( fromMeetingPath)){
				
				MeetingE meeting = user.getYearPlan().getMeetingEvents().get(i);
				Meeting meetingInfo = meetingDAO.getMeeting(  meeting.getRefId() );
				List<Activity> activities = meetingInfo.getActivities();
				for(int y=0;y<activities.size();y++){
					
					if( activities.get(y).getPath().equals( agendaPathToRm ) ){
						
						activities.remove(y);
						meetingDAO.createCustomMeeting(user, meeting, meetingInfo);
						userDAO.updateUser(user);
						return;
					}
						
				}
				
			}
				
		}
	}
	
	public void editAgendaDuration(User user, int duration, String activityPath, String meetingPath){
	
        for(int i=0;i< user.getYearPlan().getMeetingEvents().size();i++){
			
			if( user.getYearPlan().getMeetingEvents().get(i).getPath().equals( meetingPath)){
				
				MeetingE meeting = user.getYearPlan().getMeetingEvents().get(i);
				Meeting meetingInfo = meetingDAO.getMeeting(  meeting.getRefId() );
				List<Activity> activities = meetingInfo.getActivities();
				for(int y=0;y<activities.size();y++){
					
					if( activities.get(y).getPath().equals( activityPath ) ){
						
						Activity activity = activities.get(y);
						activity.setDuration(duration);
						System.err.println("Changing duration to "+ duration);
						meetingDAO.createCustomMeeting(user, meeting, meetingInfo);
						userDAO.updateUser(user);
						return;
						
					}
				}
			}
        }
						
		
	}
	
}
