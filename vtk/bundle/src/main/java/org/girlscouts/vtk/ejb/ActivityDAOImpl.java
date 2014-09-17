package org.girlscouts.vtk.ejb;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.query.RowIterator;

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
//import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.dao.ActivityDAO;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.dao.UserDAO;
import org.girlscouts.vtk.dao.YearPlanComponentType;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.ActivitySearch;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.JcrCollectionHoldString;
import org.girlscouts.vtk.models.Location;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.user.User;

@Component
@Service(value=ActivityDAO.class)
public class ActivityDAOImpl implements ActivityDAO{

    private Session session;
    
    @Reference
    private SessionPool pool;
    
    @Activate
    void activate() {
        this.session = pool.getSession();
    }
    
    @Reference
    private UserDAO userDAO;
    
    @Reference
    private MeetingDAO meetingDAO;
    
	public void createActivity(User user, Activity activity) {
		
		try{
			
			if( !meetingDAO.isCurrentUserId(user, user.getCurrentUser() ) ){
				 user.setErrCode("112");
				 return;
			 }
			
			List<Class> classes = new ArrayList<Class>();	
			classes.add(User.class);
			classes.add(Activity.class);
			classes.add(JcrCollectionHoldString.class);
			classes.add(YearPlan.class);
			classes.add(MeetingE.class);
			classes.add(Cal.class);
			classes.add(Location.class);
			classes.add(Asset.class);
			classes.add(Milestone.class);
			
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
		/*
			activity.setPath("/content/girlscouts-vtk/meetings");
			ocm.insert(activity);
			ocm.save();
			*/
			
			YearPlan plan = user.getYearPlan();
			List <Activity> activities = plan.getActivities();
			if( activities ==null )
					activities= new java.util.ArrayList<Activity>();
			
			activities.add( activity );
			plan.setActivities(activities);
			
			user.getYearPlan().setAltered("true");
			
			
			/* 091514
			ocm.update(user);
			ocm.save();
	        */
			userDAO.updateUser(user);
			
			
			}catch(Exception e){e.printStackTrace();}
		
		
	}

	public java.util.List<Activity> search(ActivitySearch search) {
		// TODO Auto-generated method stub
		
		java.util.List activities = new java.util.ArrayList();
		
		for(int i=0;i<2;i++){
		Activity activity = new Activity();
		activity.setName("test "+i);
		activity.setContent("ACtivity content "+i);
		activity.setDate(new java.util.Date());
		activity.setType(YearPlanComponentType.ACTIVITY);
		activity.setId("ACT"+i);
		
		
		activities.add(activity);
		}
		return activities;
		
	}



public boolean isActivity( String uuid ){
	
	javax.jcr.Node node = null;
	try{
		node = session.getNodeByIdentifier(uuid);
	}catch(Exception e){e.printStackTrace();}
	
	if( node!=null )
		return true;
	
	return false;
}

public void updateActivitiesCancel( String uuid ){
	
	if( uuid ==null )return;
	
	try{
		List<Class> classes = new ArrayList<Class>();	
		classes.add(Activity.class); 
		
		
		Mapper mapper = new AnnotationMapperImpl(classes);			
		ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
	
		QueryManager queryManager = ocm.getQueryManager();
		Filter filter = queryManager.createFilter(Activity.class);
        filter.setScope(  "/vtk//");
        filter.addEqualTo("refUid", uuid);
      
        Query query = queryManager.createQuery(filter);
        java.util.List<Activity> activities = (List<Activity> ) ocm.getObjects(query);
        
     if( activities!=null)
        System.err.println( "Found " +activities.size() +"  activities matching uuid "+ uuid);
     else
    	 System.err.println("No activities foudn for uuid "+ uuid);
     
     
     	for(int i=0;i<activities.size();i++){
     		Activity a= activities.get(i);
     		if( a.getRefUid().equals(uuid) ){ //"if" just to double check
     			a.setCancelled("true");
     			ocm.update(a);
     		}
     	}
     	
     	ocm.save();
     
		}catch(Exception e){e.printStackTrace();}
	
	
}
	
	
}
