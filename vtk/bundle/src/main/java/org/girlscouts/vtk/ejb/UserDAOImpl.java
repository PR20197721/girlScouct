package org.girlscouts.vtk.ejb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.dao.UserDAO;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.JcrNode;
import org.girlscouts.vtk.models.Location;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Milestone;
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
    
    @Activate
    void activate() {
        this.session = pool.getSession();
    }
    
	public User getUser(String userId) {
		
		
		System.err.println("getUser: "+ userId);
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
	        user = (User) ocm.getObject(userId);
		      
	       
	        if( user!=null && user.getYearPlan().getMeetingEvents()!=null){
	        	
	        	//System.err.println("Sorting meetings pull");
	        	Comparator<MeetingE> comp = new BeanComparator("id");
	        	Collections.sort( user.getYearPlan().getMeetingEvents(), comp);
	        }
	        
	        
	       // System.err.println("User: "+ (user==null));
			
	        
	        
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
		}catch(Exception e){e.printStackTrace();}
		
		

		
		
		return user;
	}
	
	
	public YearPlan addYearPlan( User user, String yearPlanPath ){
		
		 YearPlan plan =null;
		try{
			
			//System.err.println("Adding yearPlans to user: "+ yearPlanPath);
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
			
			 plan = new YearPlan();
			 plan.setRefId( yearPlanPath );
			 plan.setMeetingEvents( meetingDAO.getAllEventMeetings_byPath( yearPlanPath ));
			
			 
			 //7/7/14
			 Comparator<MeetingE> comp = new BeanComparator("id");
			 Collections.sort( plan.getMeetingEvents(), comp);
			    
			 
			
			}catch(Exception e){e.printStackTrace();}
		
		return plan;
		
	}
	
	public void updateUser(User user){
		
		
         try{
			
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
			
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
		System.err.println("User update: "+ user.getPath() );
			
		
		
		Comparator<MeetingE> comp = new BeanComparator("id");
	    Collections.sort( user.getYearPlan().getMeetingEvents(), comp);
	    
	    
	    
		System.err.println("CHECKING JCR: " +ocm.objectExists( user.getPath()) );
			if( session.itemExists( user.getPath() )){
				System.err.println( "User updated");
				ocm.update(user);
			}else{
				
				String path = "";
				StringTokenizer t= new StringTokenizer(("/"+user.getPath()).replace( "/"+user.getId(), ""), "/" );
				while(t.hasMoreElements()){
					String node = t.nextToken();
					path += "/"+node ;
					
					if( !session.itemExists( path ))
						ocm.insert( new JcrNode( path ) );
					
				}
				//ocm.insert( new JcrNode( user.getPath().replace( "/"+user.getId(), "") ) );
				
				System.err.println( "User created/insert");
				ocm.insert(user);
			}
			 ocm.save();
			 
			
			}catch(Exception e){e.printStackTrace();}
		
	}
	
	
	public void selectYearPlan(User user, String yearPlanPath, String planName){
		

				
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
			
			    System.err.println("New YP size:" +newYearPlan.getMeetingEvents().size() +" : "+ oldPlan.getMeetingEvents().size() ); 
				String oldDates  = oldPlan.getSchedule().getDates();
			
				int count=0;
				java.util.StringTokenizer t= new java.util.StringTokenizer( oldDates, ",");
				
				//if number of dates less then new meetings 
				if(  t.countTokens() < newYearPlan.getMeetingEvents().size() )
				{
					int countDates = t.countTokens();
					System.err.println("b4 adding dates " +oldDates);
					
					long lastDate = 0, meetingTimeDiff=99999;
					while( t.hasMoreElements()){ 
						long diff = lastDate;
						lastDate= Long.parseLong(t.nextToken());
						if(diff!=0)
								meetingTimeDiff = lastDate- diff;
						}
					
					for(int z=countDates;z<newYearPlan.getMeetingEvents().size();z++ )
							oldDates+= (lastDate+meetingTimeDiff)+",";
					System.err.println("b4 after dates " +oldDates);
					oldPlan.getSchedule().setDates(oldDates);
					t= new java.util.StringTokenizer( oldDates, ",");
				}
					
				while(t.hasMoreElements()){
				System.err.println(count);
					long date= Long.parseLong(t.nextToken());
					
					if( count>= newYearPlan.getMeetingEvents().size() ){
						
						System.err.println("b4: "+ oldPlan.getSchedule().getDates() );
						
						//rm all other dates
						oldPlan.getSchedule().setDates( oldPlan.getSchedule().getDates().substring(0, oldPlan.getSchedule().getDates().indexOf(""+date)  ));
						
						System.err.println("AFter: "+ oldPlan.getSchedule().getDates() );
				
						
						
						//TODO re write
						java.util.List<MeetingE> toBeRm= new java.util.ArrayList();
						for(int i=count;i<oldPlan.getMeetingEvents().size();i++)
							toBeRm.add(oldPlan.getMeetingEvents().get(i));
							
						
						for(int i=0;i<toBeRm.size();i++)
							oldPlan.getMeetingEvents().remove(toBeRm.get(i));
						
						
						
						
						
						
						
						
						break;
					}else if(  new java.util.Date().before( new java.util.Date(date) )){
						
						System.err.println( "Subst old new  :"+new java.util.Date(date));
						
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
		
		System.err.println("REM ACTIV: "+ activityToRm.size() );
		
		for(int i=0;i<activityToRm.size();i++)
			user.getYearPlan().getActivities().remove(activityToRm.get(i));
	}//end if
		
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
}//ednclass
