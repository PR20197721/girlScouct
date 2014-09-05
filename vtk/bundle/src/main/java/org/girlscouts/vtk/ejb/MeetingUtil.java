package org.girlscouts.vtk.ejb;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.dao.AssetComponentType;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.dao.UserDAO;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Milestone;
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
	  try{
		
		for(int i=0;i<orgMeetings.size();i++) newMeeting.add(orgMeetings.get(i)); //TODO BAD
		
		for(int i=0;i<orgMeetings.size();i++){
			
			MeetingE meeting = orgMeetings.get(i);
			int newpos = newPoss.indexOf(i+1) ;
			meeting.setId(newpos);
			newMeeting.set(newpos,  meeting);
			
		}
	  }catch(Exception e){ System.err.println("ERROR : MeetingUtil.updateMeetingPos");newMeeting= orgMeetings; e.printStackTrace();}
	  
		return newMeeting;
	}
	
	
	
	
	
	public Activity getActivity(String activityId, java.util.List <Activity> activities){
		Activity _activity = null;
		for (Activity activity : activities)
	        if( activity.getId().equals(activityId))
	        {_activity=activity; break;}
		
		return _activity;
	}
	
	
	public java.util.Map getYearPlanSched(YearPlan plan,boolean meetingPlanSpecialSort){
	
		if( plan.getSchedule()!=null || plan.getActivities()==null || plan.getActivities().size()<=0 )
			return getYearPlanSched( plan );
		
		
		//if no sched and activ -> activ on top
		java.util.Map orgSched = getYearPlanSched( plan );
		java.util.Map container  = new LinkedHashMap();
		java.util.Iterator itr= orgSched.keySet().iterator();
		while( itr.hasNext() ){
			java.util.Date date = (java.util.Date) itr.next();
			YearPlanComponent _comp= (YearPlanComponent)orgSched.get(date);
			
			switch( _comp.getType() ){
				case ACTIVITY :
					Activity activity = (Activity) _comp;
					container.put(date,  activity);
					break;

				
			} 	
	    }
		
		
		
		//now set meetings & etc
		itr= orgSched.keySet().iterator();
		while( itr.hasNext() ){
			java.util.Date date = (java.util.Date) itr.next();
			YearPlanComponent _comp= (YearPlanComponent) orgSched.get(date);
			
			switch( _comp.getType() ){
			case MEETING :
				
				MeetingE meetingE =(MeetingE)_comp;
				container.put(date, meetingE);
				break;
			case MILESTONE :
				Milestone milestone = (Milestone) _comp;
				container.put(date, milestone);
				break;
				
			} 		
	    }
		
		return container;
		
	}
	
	
	public java.util.Map getYearPlanSched(YearPlan plan){
		
		java.util.Map <java.util.Date,  YearPlanComponent> sched = null;
		try{
			sched=new java.util.TreeMap<java.util.Date,YearPlanComponent>();
		
		
		List <Activity> activities = plan.getActivities();
		

		
	java.util.List <MeetingE> meetingEs = plan.getMeetingEvents();
	
	
	Comparator<MeetingE> comp = new BeanComparator("id");
    Collections.sort(meetingEs, comp);
   // for (MeetingE person : meetingEs) {
   //   System.out.println("Sorted: "+person.getId());
   // }
	
	
	
	if( plan.getSchedule() !=null ){
		
		
		String calMeeting= plan.getSchedule().getDates();
		StringTokenizer t= new StringTokenizer( calMeeting, ",");
		int count=0;
		while( t.hasMoreElements()){
			
			try{
				sched.put( new java.util.Date( Long.parseLong( (t.nextToken() ) ) ) , meetingEs.get(count));
			}catch(Exception e){e.printStackTrace();}
			count++;
			
		}
		
		
		
		int counter=0;
		java.util.Iterator itr = sched.keySet().iterator();
		while( itr.hasNext()){
			sched.put( (java.util.Date)itr.next(), meetingEs.get(counter) );
			counter++;
		}
		
		
		
		
		
		
		
		
		
		
		
	}else{ //no dates: create 1976
		Calendar tmp= java.util.Calendar.getInstance();
		tmp.setTime(new java.util.Date("1/1/1976"));
		
		for(int i=0;i<meetingEs.size();i++){
			
			sched.put( tmp.getTime(), meetingEs.get(i));
			tmp.add( java.util.Calendar.DATE, 1);
		}
	}
	
	
	
	if( activities!=null)
		  for(int i=0;i<activities.size();i++){

			sched.put(activities.get(i).getDate(), activities.get(i));
		  }
	
		
		}catch(Exception e){e.printStackTrace(); return null;}
		
		
		
		
		return sched;
	}
	
	
	
	public void changeMeetingPositions(User user, String newPositions){
		
		
		
		java.util.List <Integer> newMeetingSetup= new java.util.ArrayList();
		java.util.StringTokenizer t= new java.util.StringTokenizer( newPositions, ",");
		while( t.hasMoreElements() )
			newMeetingSetup.add( Integer.parseInt( t.nextToken() ));
		
		
		
		MeetingUtil meetingUtil = new MeetingUtil();
		
		
	
		java.util.List<MeetingE> rearangedMeetings = null;
		try{
			rearangedMeetings=  updateMeetingPos( user.getYearPlan().getMeetingEvents(), newMeetingSetup);
		}catch(Exception e){e.printStackTrace();}
				
		
		YearPlan plan = user.getYearPlan();
		plan.setMeetingEvents(rearangedMeetings);
	
		plan.setAltered("true");
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
				
				Meeting meeting =null;
				if( m.getRefId().contains("_") )
				 meeting =meetingDAO.updateCustomMeeting(user, m, null);
				else
				 meeting =meetingDAO.createCustomMeeting(user, m);
				
				Activity activity= new Activity();
				activity.setName(name);
				activity.setDuration(duration);
				activity.setActivityDescription(txt);
			
				meetingDAO.addActivity( meeting,  activity);
				
				Cal cal = user.getYearPlan().getSchedule();
				if( cal!=null )
					cal.addDate( startTime.getTime() );
				

				
			}
		}
		user.getYearPlan().setAltered("true");
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
	
	public    void swapMeetings(User user, String fromPath, String toPath){
		
		java.util.List<MeetingE> meetings = user.getYearPlan().getMeetingEvents();
		for(int i=0;i<meetings.size();i++){
			
			MeetingE meeting = meetings.get(i);
			if( meeting.getPath().equals( fromPath ) ){
			
				meeting.setRefId(toPath);
				meeting.setAssets(null);
				meeting.setLastAssetUpdate(null); //auto load assets for new meeting
				
			}
		}
		
		user.getYearPlan().setAltered("true");
		userDAO.updateUser(user);
	}

	public void rearrangeActivity(User user, String meetingPath, String _newPoss){
		
		//TOREDO
		java.util.List<Integer> newPoss = new java.util.ArrayList();
		StringTokenizer t= new StringTokenizer( _newPoss, ",");
		while( t.hasMoreElements())
			newPoss.add( Integer.parseInt( t.nextToken() ));
		
		//System.err.println("NewPos: "+ newPoss);
		
		Meeting meetingInfo = meetingDAO.getMeeting(  meetingPath );
		java.util.List<Activity>orgActivities = meetingInfo.getActivities();
		orgActivities= sortActivity(orgActivities);
		java.util.List<Activity> newActivity = new java.util.ArrayList<Activity>();
		for(int i=0;i<orgActivities.size();i++) newActivity.add(null);  
		
		for(int i=0;i<orgActivities.size();i++){
			Activity activity = orgActivities.get(i);
			int newpos = newPoss.indexOf(i+1) ;
			activity.setActivityNumber(newpos+1);
			newActivity.set(newpos,  activity);
			
			//System.err.println( );
		}
		
		//save activities to meeting
		meetingInfo.setActivities(newActivity);
		
		//create custom meeting
		MeetingE meetingE= getMeeting(user.getYearPlan().getMeetingEvents(), meetingPath);
		if(meetingE.getRefId().contains("_"))
			meetingDAO.updateCustomMeeting(user, meetingE, meetingInfo);
		else
			meetingDAO.createCustomMeeting(user, meetingE, meetingInfo);
		
		user.getYearPlan().setAltered("true");
		userDAO.updateUser(user);
	}
	
	
	public MeetingE getMeeting(java.util.List<MeetingE> meetings, String meetingPath){
		
		
		for(int i=0;i<meetings.size();i++){
			if( meetings.get(i).getRefId().equals( meetingPath)){
				return meetings.get(i);
			}
		}
		return null;
	}
	
	public void addMeetings(User user, String newMeetingPath){
		
		MeetingE meeting = new MeetingE();
		meeting.setRefId(newMeetingPath);
		
		int maxMeetEId=0;
		for(int i=0;i<user.getYearPlan().getMeetingEvents().size();i++)
			if( maxMeetEId< user.getYearPlan().getMeetingEvents().get(i).getId() )
				maxMeetEId= user.getYearPlan().getMeetingEvents().get(i).getId();
		meeting.setId( maxMeetEId+1);
		
		
		
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
		user.getYearPlan().setAltered("true");
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
						meetingDAO.createCustomMeeting(user, meeting, meetingInfo);
						user.getYearPlan().setAltered("true");
						userDAO.updateUser(user);
						return;
						
					}
				}
			}
        }
						
		
	}
	
	
	

	
	public  void reverAgenda(User user, String meetingPath){
		 
		 MeetingE meeting= null;
		 for(int i=0;i<user.getYearPlan().getMeetingEvents().size();i++)
			 if( user.getYearPlan().getMeetingEvents().get(i).getPath().equals(meetingPath))
				  meeting =user.getYearPlan().getMeetingEvents().get(i);
		 
		 String[] split = meeting.getRefId().split("/");
		 
		 		 
		 String file= split[ ( split.length -1 )];
		 
		 file= file.substring(0, file.indexOf("_"));
		// file= file.substring(0, file.indexOf("-"));
		 System.err.println("REvert agenda: "+ file);
		 
		 //080514 gradeLvel browen  ** java.util.List< Meeting> __meetings=  meetingDAO.search();
		
		 
		 String ageLevel=  user.getTroop().getGradeLevel();
		 try{
			 ageLevel= ageLevel.substring( ageLevel.indexOf("-")+1).toLowerCase().trim();
		 }catch(Exception e){e.printStackTrace();}
		 
		 java.util.List< Meeting> __meetings=  meetingDAO.getAllMeetings(ageLevel);
		 
		 for(int i=0;i<__meetings.size();i++){
				Meeting __meeting = __meetings.get(i);
				System.err.println("*** "+ __meeting.getPath() );
				
				if( __meeting.getPath().endsWith(file) ){
						swapMeetings(user, meetingPath, __meeting.getPath());
						return;
				}
		 }
		 
		 
		
	}
	
	
	public void addAids(User user, String aidId, String meetingId, String assetName){
		
		
		java.util.List<MeetingE> meetings = user.getYearPlan().getMeetingEvents();
		for(int i=0;i<meetings.size();i++){
			MeetingE meeting = meetings.get(i);
			if( meeting.getUid().equals( meetingId)){
				
				Asset dbAsset = meetingDAO.getAsset(aidId+"/") ;
				
				Asset asset = new Asset();
				asset.setRefId(aidId);
				asset.setType(AssetComponentType.AID.toString());
				asset.setTitle(assetName);
				if( dbAsset !=null )
		     		asset.setDescription(dbAsset.getDescription());
				
				java.util.List<Asset> assets= meeting.getAssets();
				assets= assets ==null ? new java.util.ArrayList() : assets;
				
				
				
				
				
				boolean isAsset= false;
				for(int y=0;y<assets.size();y++)
					if( assets.get(y).getRefId().equals( aidId ))
						isAsset=true;
				
				
				if( isAsset ) {
					return;
				}
				
				
				
				
				assets.add( asset );
				meeting.setAssets( assets );
				//user.getYearPlan().setAltered("true");
				userDAO.updateUser(user);
				return;
			}
		}
		
		
		
		
		java.util.List<Activity> activities = user.getYearPlan().getActivities();
		for(int i=0;i<activities.size();i++){
			Activity activity = activities.get(i);
			if( activity.getUid().equals( meetingId)){
				
				Asset asset = new Asset();
				asset.setRefId(aidId);
				asset.setType("AID");
				
				
				java.util.List<Asset> assets= activity.getAssets();
				assets= assets ==null ? new java.util.ArrayList() : assets;
				assets.add( asset );
				activity.setAssets( assets );
				user.getYearPlan().setAltered("true");
				userDAO.updateUser(user);
				return;
			}
		}
		
	}
	
	
	
	public void addResource(User user, String aidId, String meetingId, String assetName){
		
		
		java.util.List<MeetingE> meetings = user.getYearPlan().getMeetingEvents();
		for(int i=0;i<meetings.size();i++){
			MeetingE meeting = meetings.get(i);
			if( meeting.getUid().equals( meetingId)){
				
				
				
				Asset asset = new Asset();
				asset.setRefId(aidId);
				asset.setType(AssetComponentType.RESOURCE.toString());
				asset.setTitle(assetName);
				
				
				java.util.List<Asset> assets= meeting.getAssets();
				assets= assets ==null ? new java.util.ArrayList() : assets;
				
				
				
				
				
				boolean isAsset= false;
				for(int y=0;y<assets.size();y++)
					if( assets.get(y).getRefId().equals( aidId ))
						isAsset=true;
				
				
				if( isAsset ) {
					return;
				}
				
				
				
				
				assets.add( asset );
				meeting.setAssets( assets );
				//user.getYearPlan().setAltered("true");
				userDAO.updateUser(user);
				return;
			}
		}
		
		
		
		
		java.util.List<Activity> activities = user.getYearPlan().getActivities();
		for(int i=0;i<activities.size();i++){
			Activity activity = activities.get(i);
			if( activity.getUid().equals( meetingId)){
				
				Asset asset = new Asset();
				asset.setRefId(aidId);
				asset.setType(AssetComponentType.RESOURCE.toString());
				
				
				java.util.List<Asset> assets= activity.getAssets();
				assets= assets ==null ? new java.util.ArrayList() : assets;
				assets.add( asset );
				activity.setAssets( assets );
				//user.getYearPlan().setAltered("true");
				userDAO.updateUser(user);
				return;
			}
		}
		
	}
	
	public void rmAsset(User user, String aidId, String meetingId){
		java.util.List<MeetingE> meetings = user.getYearPlan().getMeetingEvents();
		for(int i=0;i<meetings.size();i++){
			MeetingE meeting = meetings.get(i);
			if( meeting.getUid().equals( meetingId)){
				
				java.util.List<Asset> assets= meeting.getAssets();
				
				for(int y=0;y<assets.size();y++){
					if( assets.get(y).getRefId().equals( aidId)) {
						assets.remove(y);
					}
				}
				//user.getYearPlan().setAltered("true");
				userDAO.updateUser(user);
				return;
			}
		}
		
		
		
		java.util.List<Activity> activities = user.getYearPlan().getActivities();
		for(int i=0;i<activities.size();i++){
			Activity activity = activities.get(i);
			if( activity.getUid().equals( meetingId)){
				
				java.util.List<Asset> assets= activity.getAssets();
				
				
				for(int y=0;y<assets.size();y++){
					
					if( assets.get(y).getUid().equals( aidId)) {
						assets.remove(y);
					}
				}
				
				//user.getYearPlan().setAltered("true");
				userDAO.updateUser(user);
				return;
			}
		}
		
	}
	
	
	public java.util.List<MeetingE> sortById(java.util.List<MeetingE> meetings){
		Comparator<MeetingE> comp = new org.apache.commons.beanutils.BeanComparator("id");
		Collections.sort( meetings, comp);
		return meetings;
	}
	
	
	public java.util.List<Activity> sortActivity(java.util.List<Activity> _activities) {
		
		try{
			Comparator<Activity> comp = new org.apache.commons.beanutils.BeanComparator("activityNumber");
			Collections.sort( _activities, comp);
		}catch(Exception e){e.printStackTrace();}
		return _activities;
		
	}
	
	public java.util.List<Activity> sortActivityByDate(java.util.List<Activity> _activities) {
		
		try{
			Comparator<Activity> comp = new org.apache.commons.beanutils.BeanComparator("date");
			Collections.sort( _activities, comp);
		}catch(Exception e){e.printStackTrace();}
		return _activities;
		
	}
}
