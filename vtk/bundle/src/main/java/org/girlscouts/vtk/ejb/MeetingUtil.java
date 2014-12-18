package org.girlscouts.vtk.ejb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.apache.jackrabbit.ocm.query.Filter;
import org.apache.jackrabbit.ocm.query.Query;
import org.apache.jackrabbit.ocm.query.QueryManager;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.dao.ActivityDAO;
import org.girlscouts.vtk.dao.AssetComponentType;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.dao.YearPlanComponentType;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.JcrCollectionHoldString;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.models.PlanView;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.YearPlanComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service(MeetingUtil.class)
public class MeetingUtil {
	 private final Logger log = LoggerFactory.getLogger("vtk");
    @Reference
    TroopUtil troopUtil; 
    
    @Reference
    MeetingDAO meetingDAO;
    
    @Reference
    ActivityDAO activityDAO;
	
    @Reference
    UserUtil userUtil;
    
    @Reference
    YearPlanUtil yearPlanUtil;
    
    @Reference
    org.girlscouts.vtk.helpers.DataImportTimestamper dataImportTimestamper;
    
    
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
	  }catch(Exception e){ log.error("ERROR : MeetingUtil.updateMeetingPos");newMeeting= orgMeetings; e.printStackTrace();}
	  
		return newMeeting;
	}
	
	
	
	
	
	public Activity getActivity(String activityId, java.util.List <Activity> activities){
		Activity _activity = null;
		for (Activity activity : activities)
	        if( activity.getId().equals(activityId))
	        {_activity=activity; break;}
		
		return _activity;
	}
	
	public java.util.Map getYearPlanSched(User user, YearPlan plan,boolean meetingPlanSpecialSort)throws IllegalAccessException{ return getYearPlanSched(user, plan,meetingPlanSpecialSort, false ); }
	public java.util.Map getYearPlanSched(User user, YearPlan plan,boolean meetingPlanSpecialSort, boolean isLoadMeetingInfo) throws IllegalAccessException{

		if( plan.getSchedule()!=null || plan.getActivities()==null || plan.getActivities().size()<=0 ){
			
			
			//set meetingInfos if isLoadMeetingInfo
			if( isLoadMeetingInfo ){
				System.err.println(3);
				java.util.List<MeetingE> meetingEs = plan.getMeetingEvents();
				for(int i=0;i<meetingEs.size();i++){
					MeetingE meetingE = meetingEs.get(i);
					Meeting meetingInfo = yearPlanUtil.getMeeting( user, meetingE.getRefId() );
					meetingE.setMeetingInfo(meetingInfo);
				}
				plan.setMeetingEvents(meetingEs);
			}
		
			
			return getYearPlanSched( plan );
		}
		
		
		
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
				if( isLoadMeetingInfo ){
					Meeting meetingInfo = yearPlanUtil.getMeeting( user, meetingE.getRefId() );
		System.err.println("TESTSSSSS: "+ (meetingInfo ==null) );			
		System.err.println("TESTSSSSS: "+meetingInfo.getName());
					meetingE.setMeetingInfo(meetingInfo);
				}
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
	
	
	public java.util.Map getYearPlanSched(YearPlan plan) {

		if (plan == null)
			return null;
		
		java.util.Map<java.util.Date, YearPlanComponent> sched = null;
		try {
			sched = new java.util.TreeMap<java.util.Date, YearPlanComponent>();

			List<Activity> activities = plan.getActivities();

			java.util.List<MeetingE> meetingEs = plan.getMeetingEvents();

			Comparator<MeetingE> comp = new BeanComparator("id");
			Collections.sort(meetingEs, comp);

			if (plan.getSchedule() != null) {

				String calMeeting = plan.getSchedule().getDates();
				StringTokenizer t = new StringTokenizer(calMeeting, ",");
				int count = 0;
				while (t.hasMoreElements()) {

					try {
						sched.put(
								new java.util.Date(Long.parseLong((t
										.nextToken()))), meetingEs.get(count));
					} catch (Exception e) {
						e.printStackTrace();
					}
					count++;

				}

				int counter = 0;
				java.util.Iterator itr = sched.keySet().iterator();
				while (itr.hasNext()) {
					sched.put((java.util.Date) itr.next(),
							meetingEs.get(counter));
					counter++;
				}

			} else { // no dates: create 1976
				Calendar tmp = java.util.Calendar.getInstance();
				tmp.setTime(new java.util.Date("1/1/1976"));

				for (int i = 0; i < meetingEs.size(); i++) {

					sched.put(tmp.getTime(), meetingEs.get(i));
					tmp.add(java.util.Calendar.DATE, 1);
				}
			}

			if (activities != null)
				for (int i = 0; i < activities.size(); i++) {

					sched.put(activities.get(i).getDate(), activities.get(i));
				}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return sched;
	}
	
	
	
	public void changeMeetingPositions(User user, Troop troop, String newPositions)throws IllegalAccessException{
	
//System.err.println("changeMeetingPositions");		


//System.err.println("TEST: "+userUtil.isCurrentTroopId(troop, user.getSid()));





/*
		if( !userUtil.hasAccess(troop, troop.getCurrentTroop(), Permission.PERMISSION_MOVE_MEETING_ID ) ){
			 troop.setErrCode("112");
			 throw new IllegalAccessException();
		 }
		
		*/
if( !userUtil.hasPermission(troop,  Permission.PERMISSION_MOVE_MEETING_ID ) ){
	 troop.setErrCode("112");
	 throw new IllegalAccessException();
}

		java.util.List <Integer> newMeetingSetup= new java.util.ArrayList();
		java.util.StringTokenizer t= new java.util.StringTokenizer( newPositions, ",");
		while( t.hasMoreElements() )
			newMeetingSetup.add( Integer.parseInt( t.nextToken() ));
		
		
		
		MeetingUtil meetingUtil = new MeetingUtil();
		
		
	
		java.util.List<MeetingE> rearangedMeetings = null;
		try{
			rearangedMeetings=  updateMeetingPos( troop.getYearPlan().getMeetingEvents(), newMeetingSetup);
		}catch(Exception e){e.printStackTrace();}
				
		
		YearPlan plan = troop.getYearPlan();
		plan.setMeetingEvents(rearangedMeetings);
		plan.setAltered("true");
		troop.setYearPlan(plan);
//System.err.println("calling update troop");		
		troopUtil.updateTroop(user, troop);
		
		
	}
	
	
	public void createCustomAgenda(User user, Troop troop, String name, String meetingPath, int duration, long _startTime, String txt )throws IllegalAccessException{
		
		/*
		if( !userUtil.hasAccess(troop, troop.getCurrentTroop(), Permission.PERMISSION_CREATE_MEETING_ID ) ){
			 troop.setErrCode("112");
			 throw new IllegalAccessException();
		 }
		*/
		if( troop!= null && ! userUtil.hasPermission(troop,Permission.PERMISSION_CREATE_MEETING_ID ) )
			throw new IllegalAccessException();
		
		if (!userUtil.isCurrentTroopId(troop, user.getSid())) {
			troop.setErrCode("112");
			throw new java.lang.IllegalAccessException();
		}
		
		java.util.Calendar startTime =  Calendar.getInstance();
		startTime.setTimeInMillis(  _startTime  );
		
			
		java.util.List <MeetingE> meetings = troop.getYearPlan().getMeetingEvents();
		for(int i=0;i<meetings.size();i++){
			MeetingE m= meetings.get(i);
		
			
			if( m.getPath().equals(meetingPath)){
				
				Meeting meeting =null;
				if( m.getRefId().contains("_") )
				 meeting =meetingDAO.updateCustomMeeting(user, troop, m, null);
				else
				 meeting =meetingDAO.createCustomMeeting(user, troop, m);
			
				Activity activity= new Activity();
				activity.setName(name);
				activity.setDuration(duration);
				activity.setActivityDescription(txt);
				activity.setActivityNumber( meeting.getActivities().size()	 +1 );
			
				meetingDAO.addActivity(user, troop, meeting,  activity);
				
				Cal cal = troop.getYearPlan().getSchedule();
				if( cal!=null )
					cal.addDate( startTime.getTime() );
				

				
			}
		}
		troop.getYearPlan().setAltered("true");
		troopUtil.updateTroop(user, troop);
	}
	
	public void rmCustomActivity (User user, Troop troop, String activityPath ) throws IllegalStateException, IllegalAccessException{
		/*
		if( ! userUtil.hasAccess(troop, troop.getCurrentTroop(), Permission.PERMISSION_REMOVE_MEETING_ID ) ){
			 troop.setErrCode("112");
			 throw new IllegalAccessException();
		 }
		*/
		if( user!= null && ! userUtil.hasPermission(user.getPermissions(), Permission.PERMISSION_REMOVE_MEETING_ID) )
			throw new IllegalAccessException();
		
		if( user!=null && !userUtil.isCurrentTroopId(troop, user.getSid())){
			troop.setErrCode("112");
			throw new IllegalStateException();
		}
		
		
		java.util.List <Activity> activities = troop.getYearPlan().getActivities();
		for(int i=0;i<activities.size();i++){			
			Activity activity= activities.get(i);
	//System.err.println(activity.getPath() +" : "+ activityPath);		
			if( activity.getPath().equals(activityPath) )
				activities.remove(activity);			
		}
		
		troopUtil.updateTroop(user, troop);
		
	}
	
	public    void swapMeetings(User user, Troop troop, String fromPath, String toPath) throws java.lang.IllegalAccessException{
		/*
		if( ! userUtil.hasAccess(troop, troop.getCurrentTroop()  ,Permission.PERMISSION_REPLACE_MEETING_ID)){
			 troop.setErrCode("112");
			 throw new IllegalAccessException();
		 }
		*/
		
		if( troop!= null && ! userUtil.hasPermission(troop,Permission.PERMISSION_REPLACE_MEETING_ID ) )
			throw new IllegalAccessException();
		
		if (!userUtil.isCurrentTroopId(troop, user.getSid())) {
			troop.setErrCode("112");
			throw new java.lang.IllegalAccessException();
		}
		
		
		java.util.List<MeetingE> meetings = troop.getYearPlan().getMeetingEvents();
		for(int i=0;i<meetings.size();i++){
			
			MeetingE meeting = meetings.get(i);
			if( meeting.getPath().equals( fromPath ) ){
			
				meeting.setRefId(toPath);
				meeting.setAssets(null);
				meeting.setLastAssetUpdate(null); //auto load assets for new meeting
				
			}
		}
		
		troop.getYearPlan().setAltered("true");
		troopUtil.updateTroop(user, troop);
	}

	public void rearrangeActivity(User user, Troop troop, String meetingPath, String _newPoss)throws java.lang.IllegalAccessException{
		
		/*
		if( ! userUtil.hasAccess(troop, troop.getCurrentTroop(), Permission.PERMISSION_MOVE_MEETING_ID ) ){
			 troop.setErrCode("112");
			 throw new IllegalAccessException();
		 }
		*/
		if( troop!= null && ! userUtil.hasPermission(troop,Permission.PERMISSION_MOVE_MEETING_ID ) )
			throw new IllegalAccessException();
		
		if (!userUtil.isCurrentTroopId(troop, user.getSid())) {
			troop.setErrCode("112");
	//System.err.println("rearrangeActivity-- no change");		
			throw new java.lang.IllegalAccessException();
		}
		
		//TOREDO
		java.util.List<Integer> newPoss = new java.util.ArrayList();
		StringTokenizer t= new StringTokenizer( _newPoss, ",");
		while( t.hasMoreElements())
			newPoss.add( Integer.parseInt( t.nextToken() ));
		
		//System.err.println("NewPos: "+ newPoss);
		
		Meeting meetingInfo = meetingDAO.getMeeting(user, meetingPath );
		java.util.List<Activity>orgActivities = meetingInfo.getActivities();
		orgActivities= sortActivity(orgActivities);
		java.util.List<Activity> newActivity = new java.util.ArrayList<Activity>();
		for(int i=0;i<orgActivities.size();i++) newActivity.add(null);  
		
		for(int i=0;i<orgActivities.size();i++){
			Activity activity = orgActivities.get(i);
			int newpos = newPoss.indexOf(i+1) ;
			activity.setActivityNumber(newpos+1);
			newActivity.set(newpos,  activity);
			
		}
		
		//save activities to meeting
		meetingInfo.setActivities(newActivity);
		
		//create custom meeting
		MeetingE meetingE= getMeeting(troop.getYearPlan().getMeetingEvents(), meetingPath);
		if(meetingE.getRefId().contains("_"))
			meetingDAO.updateCustomMeeting(user, troop, meetingE, meetingInfo);
		else
			meetingDAO.createCustomMeeting(user, troop, meetingE, meetingInfo);
		
		troop.getYearPlan().setAltered("true");
		troopUtil.updateTroop(user, troop);
	}
	
	
	public MeetingE getMeeting(java.util.List<MeetingE> meetings, String meetingPath){
		
		
		for(int i=0;i<meetings.size();i++){
			if( meetings.get(i).getRefId().equals( meetingPath)){
				return meetings.get(i);
			}
		}
		return null;
	}
	
	public void addMeetings(User user, Troop troop, String newMeetingPath) throws java.lang.IllegalAccessException{
		/*
		if( !userUtil.hasAccess(troop, troop.getCurrentTroop(), Permission.PERMISSION_CREATE_MEETING_ID ) ){
			 troop.setErrCode("112");
			 throw new IllegalAccessException();
		 }
		*/
		if( troop!= null && ! userUtil.hasPermission(troop,Permission.PERMISSION_CREATE_MEETING_ID ) )
			throw new IllegalAccessException();
		
		if (!userUtil.isCurrentTroopId(troop, user.getSid())) {
			troop.setErrCode("112");
			throw new java.lang.IllegalAccessException();
		}
		
		
		MeetingE meeting = new MeetingE();
		meeting.setRefId(newMeetingPath);
		
		int maxMeetEId=0;
		for(int i=0;i<troop.getYearPlan().getMeetingEvents().size();i++)
			if( maxMeetEId< troop.getYearPlan().getMeetingEvents().get(i).getId() )
				maxMeetEId= troop.getYearPlan().getMeetingEvents().get(i).getId();
		meeting.setId( maxMeetEId+1);
		
		
		
		troop.getYearPlan().getMeetingEvents().add(meeting);
		
		if( troop.getYearPlan().getSchedule()!=null ){
			
			StringTokenizer t= new StringTokenizer( troop.getYearPlan().getSchedule().getDates(), ",");
			long firstDate=Long.parseLong(t.nextToken());
			long secondDate=Long.parseLong(t.nextToken());
			long diff = secondDate- firstDate;
			
			while( t.hasMoreElements() )
				secondDate = Long.parseLong(t.nextToken());
			
			troop.getYearPlan().getSchedule().setDates( troop.getYearPlan().getSchedule().getDates() + ""+ (secondDate+diff)+","); 
			
			
		}
		troop.getYearPlan().setAltered("true");
		troopUtil.updateTroop(user, troop);
	
	}
	
	
	public void rmAgenda(User user, Troop troop, String agendaPathToRm , String fromMeetingPath  )throws java.lang.IllegalAccessException{
		/*
		if( ! userUtil.hasAccess(troop, troop.getCurrentTroop() , Permission.PERMISSION_REMOVE_MEETING_ID) ){
			 troop.setErrCode("112");
			 throw new IllegalAccessException();
		 }
		*/
		if( troop!= null && ! userUtil.hasPermission(troop,Permission.PERMISSION_REMOVE_MEETING_ID ) )
			throw new IllegalAccessException();
		
		if (!userUtil.isCurrentTroopId(troop, user.getSid())) {
			troop.setErrCode("112");
			throw new java.lang.IllegalAccessException();
		}
		
		for(int i=0;i< troop.getYearPlan().getMeetingEvents().size();i++){
			
			if( troop.getYearPlan().getMeetingEvents().get(i).getPath().equals( fromMeetingPath)){
				
				MeetingE meeting = troop.getYearPlan().getMeetingEvents().get(i);
				Meeting meetingInfo = meetingDAO.getMeeting( user, meeting.getRefId() );
				List<Activity> activities = meetingInfo.getActivities();
				for(int y=0;y<activities.size();y++){
					
					if( activities.get(y).getPath().equals( agendaPathToRm ) ){
						
						activities.remove(y);
						meetingDAO.createCustomMeeting(user, troop, meeting, meetingInfo);
						troopUtil.updateTroop(user, troop);
						return;
					}
						
				}
				
			}
				
		}
	}
	
	public void editAgendaDuration(User user, Troop troop, int duration, String activityPath, String meetingPath)throws java.lang.IllegalAccessException{
	
		/*
		if( ! userUtil.hasAccess(troop, troop.getCurrentTroop() , Permission.PERMISSION_EDIT_MEETING_ID) ){
			 troop.setErrCode("112");
			 throw new IllegalAccessException();
		 }
		 */
		if( troop!= null && ! userUtil.hasPermission(troop,Permission.PERMISSION_EDIT_MEETING_ID ) )
			throw new IllegalAccessException();
		
		if (!userUtil.isCurrentTroopId(troop, user.getSid())) {
			troop.setErrCode("112");
			throw new java.lang.IllegalAccessException();
		}
		
		
        for(int i=0;i< troop.getYearPlan().getMeetingEvents().size();i++){
			
			if( troop.getYearPlan().getMeetingEvents().get(i).getPath().equals( meetingPath)){
				
				MeetingE meeting = troop.getYearPlan().getMeetingEvents().get(i);
				Meeting meetingInfo = meetingDAO.getMeeting( user, meeting.getRefId() );
				List<Activity> activities = meetingInfo.getActivities();
				for(int y=0;y<activities.size();y++){
					
					if( activities.get(y).getPath().equals( activityPath ) ){
						
						Activity activity = activities.get(y);
						activity.setDuration(duration);
						meetingDAO.createCustomMeeting(user,troop, meeting, meetingInfo);
						troop.getYearPlan().setAltered("true");
						troopUtil.updateTroop(user, troop);
						return;
						
					}
				}
			}
        }
						
		
	}
	
	
	

	
	public  void reverAgenda(User user, Troop troop, String meetingPath) throws java.lang.IllegalAccessException{
		 /*
		if( ! userUtil.hasAccess(troop, troop.getCurrentTroop(), Permission.PERMISSION_REPLACE_MEETING_ID ) ){
			 troop.setErrCode("112");
			 throw new IllegalAccessException();
		 }
		*/
		if( troop!= null && ! userUtil.hasPermission(troop,Permission.PERMISSION_REPLACE_MEETING_ID  ) )
			throw new IllegalAccessException();
		
		if (!userUtil.isCurrentTroopId(troop, user.getSid())) {
			troop.setErrCode("112");
			throw new java.lang.IllegalAccessException();
		}
		
		 MeetingE meeting= null;
		 for(int i=0;i<troop.getYearPlan().getMeetingEvents().size();i++)
			 if( troop.getYearPlan().getMeetingEvents().get(i).getPath().equals(meetingPath))
				  meeting =troop.getYearPlan().getMeetingEvents().get(i);
		 
		 String[] split = meeting.getRefId().split("/");
		 
		 		 
		 String file= split[ ( split.length -1 )];
		 
		 file= file.substring(0, file.indexOf("_"));
		
		 
		 String ageLevel=  troop.getTroop().getGradeLevel();
		 try{
			 ageLevel= ageLevel.substring( ageLevel.indexOf("-")+1).toLowerCase().trim();
		 }catch(Exception e){e.printStackTrace();}
		 
		 java.util.List< Meeting> __meetings=  meetingDAO.getAllMeetings(user,ageLevel);
		 
		 for(int i=0;i<__meetings.size();i++){
				Meeting __meeting = __meetings.get(i);
				//System.err.println("*** "+ __meeting.getPath() );
				
				if( __meeting.getPath().endsWith(file) ){
						swapMeetings(user, troop, meetingPath, __meeting.getPath());
						return;
				}
		 }
		
	}
	
	
	public void addAids(User user, Troop troop, String aidId, String meetingId, String assetName)throws java.lang.IllegalAccessException{
		/*
		if( ! userUtil.hasAccess(troop, troop.getCurrentTroop() , Permission.PERMISSION_CREATE_MEETING_ID) ){
			 troop.setErrCode("112");
			 //return;
			 throw new IllegalAccessException();
		 }
		*/
		if( troop!= null && ! userUtil.hasPermission(troop,Permission.PERMISSION_CREATE_MEETING_ID  ) )
			throw new IllegalAccessException();
		
		if (!userUtil.isCurrentTroopId(troop, user.getSid())) {
			troop.setErrCode("112");
			throw new java.lang.IllegalAccessException();
		}
		
		java.util.List<MeetingE> meetings = troop.getYearPlan().getMeetingEvents();
		for(int i=0;i<meetings.size();i++){
			MeetingE meeting = meetings.get(i);
			if( meeting.getUid().equals( meetingId)){
				
				Asset dbAsset = meetingDAO.getAsset(user, aidId+"/") ;
				
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
				//troop.getYearPlan().setAltered("true");
				troopUtil.updateTroop(user, troop);
				return;
			}
		}
		
		
		
		
		java.util.List<Activity> activities = troop.getYearPlan().getActivities();
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
				troop.getYearPlan().setAltered("true");
				troopUtil.updateTroop(user, troop);
				return;
			}
		}
		
	}
	
	
	
	public void addResource(User user, Troop troop, String aidId, String meetingId, String assetName)throws java.lang.IllegalAccessException{
		/*
		if( ! userUtil.hasAccess(troop, troop.getCurrentTroop(), Permission.PERMISSION_CREATE_MEETING_ID ) ){
			 troop.setErrCode("112");
			 //return;
			 throw new IllegalAccessException();
		 }
		*/
		if( troop!= null && ! userUtil.hasPermission(troop,Permission.PERMISSION_CREATE_MEETING_ID  ) )
			throw new IllegalAccessException();
		
		if (!userUtil.isCurrentTroopId(troop, user.getSid())) {
			troop.setErrCode("112");
			throw new java.lang.IllegalAccessException();
		}
		
		java.util.List<MeetingE> meetings = troop.getYearPlan().getMeetingEvents();
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
				//troop.getYearPlan().setAltered("true");
				troopUtil.updateTroop(user, troop);
				return;
			}
		}
		
		
		
		
		java.util.List<Activity> activities = troop.getYearPlan().getActivities();
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
				//troop.getYearPlan().setAltered("true");
				troopUtil.updateTroop(user, troop);
				return;
			}
		}
		
	}
	
	public void rmAsset(User user, Troop troop, String aidId, String meetingId)throws java.lang.IllegalAccessException{
		/*
		if( ! userUtil.hasAccess(troop, troop.getCurrentTroop(), Permission.PERMISSION_REMOVE_MEETING_ID ) ){
			 troop.setErrCode("112");
			 //return;
			 throw new IllegalAccessException();
		 }
		*/
		if( troop!= null && ! userUtil.hasPermission(troop,Permission.PERMISSION_REMOVE_MEETING_ID   ) )
			throw new IllegalAccessException();
		
		if (!userUtil.isCurrentTroopId(troop, user.getSid())) {
			troop.setErrCode("112");
			throw new java.lang.IllegalAccessException();
		}
		
		java.util.List<MeetingE> meetings = troop.getYearPlan().getMeetingEvents();
		for(int i=0;i<meetings.size();i++){
			MeetingE meeting = meetings.get(i);
			if( meeting.getUid().equals( meetingId)){
				
				java.util.List<Asset> assets= meeting.getAssets();
				
				for(int y=0;y<assets.size();y++){
					if( assets.get(y).getRefId().equals( aidId)) {
						assets.remove(y);
					}
				}
				//troop.getYearPlan().setAltered("true");
				troopUtil.updateTroop(user, troop);
				return;
			}
		}
		
		
		
		java.util.List<Activity> activities = troop.getYearPlan().getActivities();
		for(int i=0;i<activities.size();i++){
			Activity activity = activities.get(i);
			if( activity.getUid().equals( meetingId)){
				
				java.util.List<Asset> assets= activity.getAssets();
				
				
				for(int y=0;y<assets.size();y++){
					
					if( assets.get(y).getUid().equals( aidId)) {
						assets.remove(y);
					}
				}
				
				//troop.getYearPlan().setAltered("true");
				troopUtil.updateTroop(user, troop);
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
	
	
	public PlanView planView( User user, Troop troop, javax.servlet.http.HttpServletRequest request ) throws Exception{
		
		
		PlanView planView = planView1(  user,  troop,  request);
		/*
		PlanView planView = new PlanView();
		HttpSession session= request.getSession();
		
		//java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(troop.getYearPlan(), false);
		
		java.util.Map <java.util.Date,  YearPlanComponent> sched = getYearPlanSched(troop.getYearPlan(), false);
		if( sched==null || (sched.size()==0)){System.err.println( "You must first select a year plan."); return null;}
		java.util.List<java.util.Date> dates =new java.util.ArrayList<java.util.Date>(sched.keySet());
		long nextDate=0, prevDate=0;
		java.util.Date searchDate= null;

		if( request.getParameter("elem") !=null ) {
			searchDate = new java.util.Date( Long.parseLong(  request.getParameter("elem")  ) );	
		}else if( session.getValue("VTK_planView_memoPos") !=null ){
			searchDate= new java.util.Date( (Long)session.getValue("VTK_planView_memoPos")  );			
		} else {
			
			if( troop.getYearPlan().getSchedule()==null)
				searchDate = (java.util.Date) sched.keySet().iterator().next();
			else{
		
			  java.util.Iterator itr = sched.keySet().iterator();
			  while( itr.hasNext() ){
				searchDate= (java.util.Date)itr.next();
				if( searchDate.after( new java.util.Date() ) )
				break;
		
			  }
		    }
		
		}

		int currInd =dates.indexOf(searchDate);
	        int meetingCount = currInd+1;

		if( dates.size()-1 > currInd )
			nextDate = ((java.util.Date)dates.get(currInd+1)).getTime();
		if( currInd>0 )
			prevDate = ((java.util.Date)dates.get(currInd-1)).getTime();	
		session.putValue("VTK_planView_memoPos", searchDate.getTime());
	    YearPlanComponent _comp= sched.get(searchDate);
	    */
		 YearPlanComponent _comp= planView.getYearPlanComponent();
		if( _comp ==null ){
			/*
			%><span class="error">
			A co-leader has made changes to the schedule of the Year Plan that affect this meeting. 
			<a href="/content/girlscouts-vtk/en/vtk.plan.html">Click here</a> to go to the Year Plan view to see this changes and access the updated version of this meeting.
			</span><% 
			*/
			System.err.println("_comp is null");
			return null;
		}

	    MeetingE meeting = null;
		List<Asset> _aidTags = null;
		Meeting meetingInfo = null;

		
		

		if ( _comp.getType() == YearPlanComponentType.MEETING) {
			meeting = (MeetingE) _comp;
			meetingInfo = yearPlanUtil.getMeeting( user, meeting.getRefId() );
			
			meeting.setMeetingInfo(meetingInfo);
			
			java.util.List <Activity> _activities = meetingInfo.getActivities();
			java.util.Map<String, JcrCollectionHoldString> meetingInfoItems=  meetingInfo.getMeetingInfo();

			boolean isLocked=false;
			//if(searchDate.before( new java.util.Date() ) && troop.getYearPlan().getSchedule()!=null ) isLocked= true;
			if(planView.getSearchDate().before( new java.util.Date() ) && troop.getYearPlan().getSchedule()!=null ) isLocked= true;

			boolean isCanceled =false;
			if( meeting.getCancelled()!=null && meeting.getCancelled().equals("true")){
				isCanceled  = true;
			}

			_aidTags = meeting.getAssets();

			java.util.Date sysAssetLastLoad = // sling.getService(org.girlscouts.vtk.helpers.DataImportTimestamper.class).getTimestamp(); //SYSTEM QUERY
					dataImportTimestamper.getTimestamp();
			
			if(meeting.getLastAssetUpdate()==null || meeting.getLastAssetUpdate().before(sysAssetLastLoad) ){

			_aidTags = _aidTags ==null ? new java.util.ArrayList() : _aidTags;

			//rm cachables
			java.util.List aidToRm= new java.util.ArrayList();
			for(int i=0;i<_aidTags.size();i++){
				if( _aidTags.get(i).getIsCachable() )
					aidToRm.add( _aidTags.get(i));
			}

			for(int i=0;i<aidToRm.size();i++)
				_aidTags.remove( aidToRm.get(i));

			//query aids cachables
			 java.util.List __aidTags =  yearPlanUtil.getAids(user, meetingInfo.getAidTags(), meetingInfo.getId(), meeting.getUid());

			//merge lists aids
			_aidTags.addAll( __aidTags );

			//query resources cachables
			java.util.List __resources =  yearPlanUtil.getResources(user, meetingInfo.getResources(), meetingInfo.getId(), meeting.getUid());

			//merge lists resources
			_aidTags.addAll( __resources );

			meeting.setLastAssetUpdate( new java.util.Date() );
			meeting.setAssets( _aidTags);
			troopUtil.updateTroop(user, troop);
		
		}
		
	  }
		
		
		
		meeting.setMeetingInfo(meetingInfo);
		planView.setMeeting(meeting);
		planView.setAidTags( _aidTags );
		/*
		planView.setSearchDate(searchDate);
		planView.setPrevDate(prevDate);
		planView.setNextDate(nextDate);
		planView.setCurrInd(currInd);
		planView.setMeetingCount(meetingCount);
		planView.setYearPlanComponent(_comp);
		*/
		
		
		
		return planView;
	}

	public PlanView planView1( User user, Troop troop, javax.servlet.http.HttpServletRequest request) throws IllegalAccessException{
		
		PlanView planView = new PlanView();
		HttpSession session= request.getSession();
		
		java.util.Map <java.util.Date,  YearPlanComponent> sched = getYearPlanSched(user, troop.getYearPlan(), false, false);
		if( sched==null || (sched.size()==0)){System.err.println( "You must first select a year plan."); return null;}
		java.util.List<java.util.Date> dates =new java.util.ArrayList<java.util.Date>(sched.keySet());
		long nextDate=0, prevDate=0;
		java.util.Date searchDate= null;

		if( request.getParameter("elem") !=null ) {
			searchDate = new java.util.Date( Long.parseLong(  request.getParameter("elem")  ) );	
		}else if( session.getValue("VTK_planView_memoPos") !=null ){
			searchDate= new java.util.Date( (Long)session.getValue("VTK_planView_memoPos")  );			
		} else {
			
			if( troop.getYearPlan().getSchedule()==null)
				searchDate = (java.util.Date) sched.keySet().iterator().next();
			else{
		
			  java.util.Iterator itr = sched.keySet().iterator();
			  while( itr.hasNext() ){
				searchDate= (java.util.Date)itr.next();
				if( searchDate.after( new java.util.Date() ) )
				break;
		
			  }
		    }
		
		}

		int currInd =dates.indexOf(searchDate);
	        int meetingCount = currInd+1;

		if( dates.size()-1 > currInd )
			nextDate = ((java.util.Date)dates.get(currInd+1)).getTime();
		if( currInd>0 )
			prevDate = ((java.util.Date)dates.get(currInd-1)).getTime();	
		session.putValue("VTK_planView_memoPos", searchDate.getTime());
	    YearPlanComponent _comp= sched.get(searchDate);
	 
	    
	    
		planView.setSearchDate(searchDate);
		planView.setPrevDate(prevDate);
		planView.setNextDate(nextDate);
		planView.setCurrInd(currInd);
		planView.setMeetingCount(meetingCount);
		planView.setYearPlanComponent(_comp);
		
	    return planView;
	}

}
