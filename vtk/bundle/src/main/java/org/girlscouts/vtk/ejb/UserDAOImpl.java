package org.girlscouts.vtk.ejb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.jcr.Session;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.felix.scr.annotations.Activate;
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
import org.girlscouts.vtk.dao.ActivityDAO;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.dao.UserDAO;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.Council;
import org.girlscouts.vtk.models.JcrNode;
import org.girlscouts.vtk.models.Location;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.models.UserGlobConfig;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.models.user.User;

@Component
@Service(value=UserDAO.class)
public class UserDAOImpl implements UserDAO{

   private Session session;
    
    @Reference
    private SessionPool pool;
    
    @Reference
    private MeetingDAO meetingDAO;
    
   // @Reference
   // private ActivityDAO activityDAO;
    
    private static UserGlobConfig userGlobConfig;
    

    
    @Activate
    void activate() {
        this.session = pool.getSession();
    }
    
	public User getUser(String userId) {
		
		User user =null;
		try{
			List<Class> classes = new ArrayList<Class>();	
			classes.add(User.class); 
			classes.add(YearPlan.class); 
			classes.add(MeetingE.class);
			classes.add(Activity.class);
			classes.add(Location.class);
			classes.add(Asset.class);
			classes.add(Cal.class);
			classes.add(Milestone.class);
			
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
	
			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(User.class);
			
	        // GOOD user = (User) ocm.getObject("/content/girlscouts-vtk/users/"+ userId);
			
			//6/27/14 
		System.err.println("GET_USER_OBJ********************");	
			ocm.refresh(true);
	        user = (User) ocm.getObject(userId);
	      
	       
	       
	        if( user!=null && user.getYearPlan().getMeetingEvents()!=null){
	        	
	        	Comparator<MeetingE> comp = new BeanComparator("id");
	        	Collections.sort( user.getYearPlan().getMeetingEvents(), comp);
	        }
	        
	        /*
	        if( user.getYearPlan().getLastAssetUpdate() == null ||
	        		(user.getYearPlan().getLastAssetUpdate().before( new java.util.Date() ) && user.getYearPlan().getAssets()!=null &&  user.getYearPlan().getAssets().size()>0 ) ){
	        	
	        	java.util.List<Asset> assetsToRm = new java.util.ArrayList();
	        	
	        	
	        	java.util.Iterator itr = user.get
	        		if( user.getYearPlan().getAssets().get(i).iisCachable() )
	        			assetsToRm.add( user.getYearPlan().getAssets().get(i) );
	        	
	        	for(int i=0;i< assetsToRm.size();i++)
	        		user.getYearPlan().getAssets().remove(assetsToRm.get(i));
	        	
	        	updateUser(user);
	        	
	        }
	        */
	        
	        
	        //cancelled activity check	        
	        if( user!=null && user.getYearPlan()!=null && user.getYearPlan().getActivities() !=null && user.getYearPlan().getActivities().size()>0){
	        	//activityDAO.checkCanceledActivity(user);
	        }
	        
	        
		}catch(Exception e){e.printStackTrace();}
		
		

		
		
		return user;
	}
	
	
	public YearPlan addYearPlan( User user, String yearPlanPath ){
		
		
		if( !meetingDAO.isCurrentUserId(user, user.getCurrentUser() ) ){
			 user.setErrCode("112");
			 return null;
		 }
		
		String x=yearPlanPath;
		 YearPlan plan =null;
		try{
			
			if(!yearPlanPath.endsWith("/"))
			   yearPlanPath = yearPlanPath +"/";
			
			//path has not meetings/ TODO check if exists
			yearPlanPath += "meetings/";
			
			List<Class> classes = new ArrayList<Class>();	
			classes.add(User.class); 
			classes.add(YearPlan.class); 
			classes.add(MeetingE.class); 
			classes.add(Cal.class);
			classes.add(Milestone.class);
			
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
			QueryManager queryManager = ocm.getQueryManager();
			
			Filter filter = queryManager.createFilter(YearPlan.class);
				plan = (YearPlan) ocm.getObject(x);
			
				
			// plan = new YearPlan();
			 plan.setRefId( yearPlanPath );
			 plan.setMeetingEvents( meetingDAO.getAllEventMeetings_byPath( yearPlanPath ));
			
			 
			 //7/7/14
			 Comparator<MeetingE> comp = new BeanComparator("id");
			 Collections.sort( plan.getMeetingEvents(), comp);
			    
			 
			
			}catch(Exception e){e.printStackTrace();}
		
		return plan;
		
	}
	
	public boolean updateUser(User user){
		
		boolean isUpdated= false;
         try{
        	 
        	 if( user==null || user.getYearPlan() ==null) { System.err.println("exiting updateUser"); return true; }
	
        	 /*
        	 java.util.Calendar cal = java.util.Calendar.getInstance();
        	 cal.setTime(new java.util.Date("1/2/1976"));
        	 user.setLastModified(cal);
        	 */
        	 user.setErrCode("111");
        	 
        	 //another user logged in
        	 if( user!=null && user.getLastModified()!=null ){
        		 /*
        		 java.util.Calendar x= java.util.Calendar.getInstance();
        		 x.add(java.util.Calendar.MINUTE, -2);
        		 
        		 System.err.println("Check: "+ x.getTime() +" MK: "+ user.getLastModified().getTime() +" :" +(user.getLastModified().after(x)) );
        		 System.err.println("Check1 isCurrentUser: "+ meetingDAO.isCurrentUserId(user, user.getCurrentUser() ) );
        		 */
        		 
        				 if( !meetingDAO.isCurrentUserId(user, user.getCurrentUser() ) ){// && user.getLastModified().after(x) ){ 
        			 
        					 //cal.setTime(new java.util.Date("1/3/1976"));
        					 //user.setLastModified(cal);
        					 user.setErrCode("112");
        					 return false;
        				 }
        	 }
        	 
        	 
			List<Class> classes = new ArrayList<Class>();	
			classes.add(User.class); 
			classes.add(YearPlan.class); 
			classes.add(MeetingE.class); 
			classes.add(Location.class);
			classes.add(Cal.class);
			classes.add(Activity.class);
			classes.add(Asset.class);
			classes.add(JcrNode.class);
			classes.add(Milestone.class);
			classes.add(Council.class);
			classes.add(org.girlscouts.vtk.models.Troop.class);
			
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
		
		
			Comparator<MeetingE> comp = new BeanComparator("id");
			Collections.sort( user.getYearPlan().getMeetingEvents(), comp);
	    
	    
			//update milestones based on councilId
			//- Depricated: info pulled to display from scafolding user.getYearPlan().setMilestones( meetingDAO.getCouncilMilestones( user.getTroop().getCouncilId() ) );
			
			
	    
	    
	   
			System.err.println("CHECKING JCR: " +ocm.objectExists( user.getPath()) );
		
			if( session.itemExists( user.getPath() )){
				System.err.println( "User updated");
				ocm.update(user);
				
			}else{
				System.err.println("cteating user");
				
				String path = "";
				StringTokenizer t= new StringTokenizer(("/"+user.getPath()).replace( "/"+user.getId(), ""), "/" );
				int i=0;
				while(t.hasMoreElements()){
					String node = t.nextToken();
					path += "/"+node ;
					System.err.println( "user cr: "+path+":"+session.itemExists( path ) );	
					if( !session.itemExists( path )){
						if( i==1 ){
							System.err.println(i +" : creating user");
							ocm.insert( new Council( path ) );
						}else
							ocm.insert( new JcrNode( path ) );
					}
					i++;
				}
				//ocm.insert( new JcrNode( user.getPath().replace( "/"+user.getId(), "") ) );
				
				System.err.println( "User created/insert");
				ocm.insert(user);
			}
System.err.println("Saving user info..."+ user.getPath() );
System.err.println( "sessionId: "+ user.getCurrentUser() );

			String old_errCode= user.getErrCode();
			java.util.Calendar old_lastModified = user.getLastModified();
			try{

				
				user.setErrCode(null);
				user.setLastModified(java.util.Calendar.getInstance());
				ocm.update(user);
				ocm.save();
				
			
				isUpdated=true;
				System.err.println("User info saved..." + user.getErrCode());	
			}catch(Exception e){
				e.printStackTrace();
				
				user.setLastModified(old_lastModified);
				user.setErrCode(old_errCode);
				
				}
			
			
			

			}catch(Exception e){
				
			
			 if( user!=null )
				System.err.println("TEST: "+user.getId() +" : "+ user.getPath() );
			 
			 e.printStackTrace();
			}
         
         	
		return isUpdated;
	}
	
	
	public void selectYearPlan(User user, String yearPlanPath, String planName){
		
		if( !meetingDAO.isCurrentUserId(user, user.getCurrentUser() ) ){
			 user.setErrCode("112");
			 return;
		 }
				
		YearPlan oldPlan = user.getYearPlan();
		YearPlan newYearPlan = addYearPlan(user, yearPlanPath);
	try{
		
		/*
		if( oldPlan==null || oldPlan.getName()==null || oldPlan.getName().equals("") ){
			YearPlan plan = new YearPlanDAOImpl().getYearPlan(yearPlanPath);
			if( plan!=null)
				newYearPlan.setName(plan.getName());
		}
		*/
		newYearPlan.setName(planName);
		
		
		if( oldPlan==null || oldPlan.getMeetingEvents()==null || oldPlan.getMeetingEvents().size()<=0 || 
				oldPlan.getSchedule()==null || oldPlan.getSchedule().getDates().equals("")){ //new user new plan, first time
			
			user.setYearPlan(newYearPlan);
			
		//if dates, copy dates to new year plan && copy/replace OLD PASSED Meetings
		}else if( oldPlan.getSchedule()!=null ){ //no dates; no past meetings to copy
			
				String oldDates  = oldPlan.getSchedule().getDates();
			
				int count=0;
				java.util.StringTokenizer t= new java.util.StringTokenizer( oldDates, ",");
				
				//if number of dates less then new meetings 
				if(  t.countTokens() < newYearPlan.getMeetingEvents().size() )
				{
					int countDates = t.countTokens();
					
					long lastDate = 0, meetingTimeDiff=99999;
					while( t.hasMoreElements()){ 
						long diff = lastDate;
						lastDate= Long.parseLong(t.nextToken());
						if(diff!=0)
								meetingTimeDiff = lastDate- diff;
						}
					
					for(int z=countDates;z<newYearPlan.getMeetingEvents().size();z++ )
							oldDates+= (lastDate+meetingTimeDiff)+",";

					oldPlan.getSchedule().setDates(oldDates);
					t= new java.util.StringTokenizer( oldDates, ",");
				}
					
				while(t.hasMoreElements()){
					long date= Long.parseLong(t.nextToken());
					
					if( count>= newYearPlan.getMeetingEvents().size() ){
						//rm all other dates
						oldPlan.getSchedule().setDates( oldPlan.getSchedule().getDates().substring(0, oldPlan.getSchedule().getDates().indexOf(""+date)  ));
						
						//TODO re write
						java.util.List<MeetingE> toBeRm= new java.util.ArrayList();
						for(int i=count;i<oldPlan.getMeetingEvents().size();i++)
							toBeRm.add(oldPlan.getMeetingEvents().get(i));
							
						
						for(int i=0;i<toBeRm.size();i++)
							oldPlan.getMeetingEvents().remove(toBeRm.get(i));
						
						
						
						
						
						
						
						
						break;
					}else if(  new java.util.Date().before( new java.util.Date(date) )){
						
						if( count>= oldPlan.getMeetingEvents().size()) // oldPlan has less meetings than new -add
							oldPlan.getMeetingEvents().add(newYearPlan.getMeetingEvents().get(count ));
						else //replace meeting refs
							oldPlan.getMeetingEvents().get(count).setRefId(  newYearPlan.getMeetingEvents().get(count ).getRefId() );
						
						oldPlan.getMeetingEvents().get(count).setCancelled("false");
					
					}
					count++;
				}
					
		}else{
			
			oldPlan.setMeetingEvents(newYearPlan.getMeetingEvents() );
			user.setYearPlan(oldPlan);
		}
	}catch(Exception e){
		e.printStackTrace();
		try{
				System.err.println("Error setting new Plan: dumping old plan and replacing with new Plan");
				user.setYearPlan(newYearPlan);
		}catch(Exception ew){ew.printStackTrace();}
	}
		//remove all custom activitites
		//user.getYearPlan().setActivities(null);
	
	
	
	//remove all PAST custom activitites
	if( user.getYearPlan().getActivities() !=null ){
		java.util.List<Activity> activityToRm= new java.util.ArrayList();
		for(int i=0;i< user.getYearPlan().getActivities().size();i++ )
			if( new java.util.Date().before( user.getYearPlan().getActivities().get(i).getDate() ) )
				activityToRm.add( user.getYearPlan().getActivities().get(i) );
		
		for(int i=0;i<activityToRm.size();i++)
			user.getYearPlan().getActivities().remove(activityToRm.get(i));
	}//end if
		
	    user.getYearPlan().setAltered("false");
		user.getYearPlan().setName(planName);
		updateUser(user);
		
	}


public void rmUser(User user){
	   try{
			
				List<Class> classes = new ArrayList<Class>();	
				classes.add(User.class); 
				classes.add(YearPlan.class); 
				classes.add(MeetingE.class); 
				classes.add(Location.class);
				classes.add(Cal.class);
				classes.add(Activity.class);
				classes.add(Asset.class);
				classes.add(Milestone.class);
				
				Mapper mapper = new AnnotationMapperImpl(classes);
				ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
				ocm.remove(user);
				ocm.save();
	   }catch(Exception e){e.printStackTrace();}
}
/*
public void selectYearPlan(User user, String yearPlanPath){
	

	
	YearPlan oldPlan = user.getYearPlan();
	YearPlan newYearPlan = addYearPlan(user, yearPlanPath);
	
	if( oldPlan==null || oldPlan.getMeetingEvents()==null || oldPlan.getMeetingEvents().size()<=0 || 
			oldPlan.getSchedule()==null || oldPlan.getSchedule().getDates().equals("")){ //new user new plan, first time
		user.setYearPlan(newYearPlan);
		updateUser(user);
		return;
	}
	
	
	
	
		java.util.List <MeetingE> oldMeetings = oldPlan.getMeetingEvents();
		java.util.List <MeetingE> newMeetings = newYearPlan.getMeetingEvents();
	
		int maxMeetingCount = oldMeetings.size();
		if( newMeetings.size()> maxMeetingCount) maxMeetingCount= newMeetings.size();
		
		int countLockedMeetings = doCountLockedMeetins( oldPlan.getSchedule().getDates() ); //first n past meetings
		
		for(int i=countLockedMeetings;i<maxMeetingCount;i++){
				
				if( oldMeetings.size()>i && newMeetins.size()>i ){ //replace meeting @i
					copyMeeting( oldMeetings, newMeetings, i );
					setCancelled("false");
				
				}else if( oldMeetings.size() > newMeetings.size() ){ //remove meeting @i - downsize
					removeMeeting( oldMeetings, i);
					removeDates(i);
				
				}else if( oldMeetings.size() < newMeetings.size() ){ //add new meeting @i 
					insertMeeting( newMeetings, oldMeetings, i);
					insertDates(i);
				}
				
			
		}
	
		user.getYearPlan().setActivities(null);			
		updateUser(user);
	
 }
 */

public void addAsset(User user, String meetingUid,  Asset asset){

	
	if( !meetingDAO.isCurrentUserId(user, user.getCurrentUser() ) ){
		 user.setErrCode("112");
		 return;
	 }
	
        java.util.List<MeetingE> meetings = user.getYearPlan().getMeetingEvents();
        for(int i=0;i<meetings.size();i++)
                        if( meetings.get(i).getUid().equals( meetingUid))
                                meetings.get(i).getAssets().add( asset );

        
        /* ???
        java.util.List <Activity> activities = user.getYearPlan().getActivities();
        for(int i=0;i<activities.size();i++)
            if( activities.get(i).getUid().equals( meetingUid))
                   activities.get(i).getAssets().add( asset );
		*/
        
        updateUser(user);

}




	public UserGlobConfig getUserGlobConfig(){ 
		
		if( true){ //userGlobConfig ==null )
			loadUserGlobConfig();
			
			if( userGlobConfig ==null ){
				
				createUserGlobConfig();
					loadUserGlobConfig();
				}
		}
		return userGlobConfig ;
	}
	
	
	

	public void loadUserGlobConfig() {
		
		userGlobConfig = new UserGlobConfig();
		
		try{
			List<Class> classes = new ArrayList<Class>();	
			classes.add(UserGlobConfig.class); 
				
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
		
			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(UserGlobConfig.class);
		
			userGlobConfig = (UserGlobConfig) ocm.getObject("/vtk/global-settings");
			
			
			System.err.println("test : "+ (userGlobConfig==null) );
			
			//System.err.println("test : "+ (userGlobConfig.getVacationDates() +" : " + userGlobConfig.getVacationDates()) );
		     
		}catch(Exception e){e.printStackTrace();}
		
		
	}
	
	public void createUserGlobConfig() {
		
		 try{
				
				List<Class> classes = new ArrayList<Class>();	
				classes.add(UserGlobConfig.class);
				
				Mapper mapper = new AnnotationMapperImpl(classes);
				ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
			
				
				 UserGlobConfig userGlobConfig = new UserGlobConfig();
				
				 ocm.insert(userGlobConfig);
			
				 ocm.save();
				 
				
				}catch(Exception e){e.printStackTrace();}
			
	}
	
	public void updateUserGlobConfig() {
		
		 try{
				
				List<Class> classes = new ArrayList<Class>();	
				classes.add(UserGlobConfig.class);
				
				Mapper mapper = new AnnotationMapperImpl(classes);
				ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
			
				if( session.itemExists( userGlobConfig.getPath() )){
					
					ocm.update(userGlobConfig);
				}else{
					
					ocm.insert(userGlobConfig);
				}
				 ocm.save();
				 
				
				}catch(Exception e){e.printStackTrace();}
			
	}
	
	
	public java.util.List getUsers(){
		java.util.List<User> users=null;
		try{
			
			List<Class> classes = new ArrayList<Class>();	
			classes.add(User.class); 
			classes.add(YearPlan.class); 
			classes.add(MeetingE.class);
			classes.add(Activity.class);
			classes.add(Location.class);
			classes.add(Asset.class);
			classes.add(Cal.class);
			classes.add(Milestone.class);
			
			
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
		
			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(User.class);
			
			filter.setScope("/vtk//");
			
				
			Query query = queryManager.createQuery(filter);
			users  =(java.util.List<User>) ocm.getObjects(query);
			System.err.println( "users: "+ (users.size()) );
		      
		}catch(Exception e){e.printStackTrace();}
		return users;
	}

	
	
	public void logout(User user){
		if(user ==null) return;
		User tmp_user= getUser(user.getPath());
		//tmp_user.setTroop(user.getTroop());
		if( tmp_user ==null )return;
		tmp_user.setCurrentUser(null);
		updateUser( tmp_user );
	}
	
	public boolean hasPermission(Set<Integer> myPermissionTokens, int permissionId){
		if( myPermissionTokens!=null && myPermissionTokens.contains(permissionId) )
			return true;
		
		return false;
	}
	
}//ednclass
