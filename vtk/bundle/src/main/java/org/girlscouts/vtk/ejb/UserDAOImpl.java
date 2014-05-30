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
			classes.add(Cal.class);
			
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
		
			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(User.class);
			
	      
	        filter.setScope(  "/content/girlscouts-vtk/users/");
	      
	        Query query = queryManager.createQuery(filter);
	        
	        
	        user = (User) ocm.getObject(query);
	      
	        
	        System.err.println("User: "+ user==null);
			
			}catch(Exception e){e.printStackTrace();}
		
		

		
		
		return user;
	}
	
	
public static void main(String args[]){
	
	userDAO.getUser("5");
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
		
			 ocm.update(user);
			 ocm.save();
			 
			
			}catch(Exception e){e.printStackTrace();}
		
	}
	
	
	public void selectYearPlan(User user, String yearPlanPath){
		

				
		YearPlan oldPlan = user.getYearPlan();
		YearPlan newYearPlan = userDAO.addYearPlan(user, yearPlanPath);
		
		
		
		
		//if dates, copy dates to new year plan && copy/replace OLD PASSED Meetings
		if( oldPlan.getSchedule()!=null ){ //no dates; no past meetings to copy
			
			   
				String oldDates  = oldPlan.getSchedule().getDates();
				
				int count=0;
				java.util.StringTokenizer t= new java.util.StringTokenizer( oldDates, ",");
				while(t.hasMoreElements()){
				
					long date= Long.parseLong(t.nextToken());
					if( new java.util.Date().after( new java.util.Date(date) )){
				
						java.util.List <MeetingE> newMeetings = newYearPlan.getMeetingEvents();
						oldPlan.getMeetingEvents().get(count).setRefId(  newYearPlan.getMeetingEvents().get(count ).getRefId() );
						
					
					}
					count++;
				}
					
		}else{
			
			oldPlan.setMeetingEvents(newYearPlan.getMeetingEvents() );
			user.setYearPlan(oldPlan);
		}
		
		//remove all custom activitites
		user.getYearPlan().setActivities(null);
		
		 userDAO.updateUser(user);
		
	}

}
