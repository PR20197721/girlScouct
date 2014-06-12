package org.girlscouts.vtk.ejb;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Session;

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
import org.girlscouts.vtk.models.Location;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.YearPlan;
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
			classes.add(Cal.class);
			
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
		
			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(User.class);
			
	      
	        //-filter.setScope(  "/content/girlscouts-vtk/users/5");//+userId);
	        //Query query = queryManager.createQuery(filter);
	        
	        
	        user = (User) ocm.getObject("/content/girlscouts-vtk/users/"+ userId);
	      
	        
	        System.err.println("User: "+ (user==null));
			
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
			
			
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
			
			 plan = new YearPlan();
			 plan.setRefId( yearPlanPath );
			 plan.setMeetingEvents( meetingDAO.getAllEventMeetings_byPath( yearPlanPath ));
			
			 //System.out.println("**** "+ plan.getMeetingEvents().size() );
			 
			 //List<YearPlan> yearPlans = user.getYearPlans();
			 //yearPlans.add( plan);
//System.err.println("*************************** Update user: " + plan.getMeetingEvents().get(0).getRefId());			 
			 
			 /* 5/27
			 user.setYearPlan(plan); 
			 ocm.update(user);
			 ocm.save();
			 */
			 
			
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
			
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
		System.err.println("User update: "+ user.getPath() );
			
			if( session.itemExists( user.getPath() )){
				System.err.println( "User updated");
				ocm.update(user);
			}else{
				System.err.println( "User created/insert");
				ocm.insert(user);
			}
			 ocm.save();
			 
			
			}catch(Exception e){e.printStackTrace();}
		
	}
	
	
	public void selectYearPlan(User user, String yearPlanPath){
		

				
		YearPlan oldPlan = user.getYearPlan();
		YearPlan newYearPlan = addYearPlan(user, yearPlanPath);
	try{
		
		/*
		for(int i=0;i<newYearPlan.getMeetingEvents().size();i++)
			System.err.println("NYDDDd :"+newYearPlan.getMeetingEvents().get(i));
		*/
		
		if( oldPlan==null || oldPlan.getMeetingEvents()==null || oldPlan.getMeetingEvents().size()<=0){ //new user new plan, first time
			
			user.setYearPlan(newYearPlan);
			
		//if dates, copy dates to new year plan && copy/replace OLD PASSED Meetings
		}else if( oldPlan.getSchedule()!=null ){ //no dates; no past meetings to copy
			
			    System.err.println("New YP size:" +newYearPlan.getMeetingEvents().size() +" : "+ oldPlan.getMeetingEvents().size() ); 
				String oldDates  = oldPlan.getSchedule().getDates();
			
				int count=0;
				java.util.StringTokenizer t= new java.util.StringTokenizer( oldDates, ",");
				while(t.hasMoreElements()){
				System.err.println(count);
					long date= Long.parseLong(t.nextToken());
					
					if( count>= newYearPlan.getMeetingEvents().size() ){
						
						System.err.println("b4: "+ oldPlan.getSchedule().getDates() );
						
						//rm all other dates
						oldPlan.getSchedule().setDates( oldPlan.getSchedule().getDates().substring(0, oldPlan.getSchedule().getDates().indexOf(""+date)  ));
						
						System.err.println("AFter: "+ oldPlan.getSchedule().getDates() );
						
						for(int i=count;i<oldPlan.getMeetingEvents().size();i++){
						
							System.err.println("Removing meeting "+ (count) +" : "+ oldPlan.getMeetingEvents().size());
							
							oldPlan.getMeetingEvents().remove(count);
							//if( (count+1) > oldPlan.getMeetingEvents().size() ) break;
						}
						break;
					}else if(  new java.util.Date().before( new java.util.Date(date) )){
						
						System.err.println( "Subst old new  :"+new java.util.Date(date));
						
						java.util.List <MeetingE> newMeetings = newYearPlan.getMeetingEvents();
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
		user.getYearPlan().setActivities(null);
		
		updateUser(user);
		
	}

}
