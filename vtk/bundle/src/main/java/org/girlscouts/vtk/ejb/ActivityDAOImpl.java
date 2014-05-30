package org.girlscouts.vtk.ejb;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.apache.jackrabbit.ocm.query.Filter;
import org.apache.jackrabbit.ocm.query.Query;
import org.apache.jackrabbit.ocm.query.QueryManager;
import org.girlsscout.vtk.dao.ActivityDAO;
import org.girlsscout.vtk.dao.YearPlanComponentType;
import org.girlsscout.vtk.models.Activity;
import org.girlsscout.vtk.models.ActivitySearch;
import org.girlsscout.vtk.models.Cal;
import org.girlsscout.vtk.models.JcrCollectionHoldString;
import org.girlsscout.vtk.models.Location;
import org.girlsscout.vtk.models.Meeting;
import org.girlsscout.vtk.models.MeetingE;
import org.girlsscout.vtk.models.YearPlan;
import org.girlsscout.vtk.models.user.User;


public class ActivityDAOImpl implements ActivityDAO{

	
	



	private Session getConnection() throws Exception{
		
		
		// Connection
        Repository repository = JcrUtils.getRepository("http://localhost:4502/crx/server/");
        
        //Workspace Login
        SimpleCredentials creds = new SimpleCredentials("admin", "admin".toCharArray());
        Session session = null;
        session = repository.login(creds, "crx.default");
		return session;
	}
	
	
	
	
	@Override
	public void createActivity(User user, Activity activity) {
		
		try{
			Session session = getConnection();
			List<Class> classes = new ArrayList<Class>();	
			classes.add(User.class);
			classes.add(Activity.class);
			classes.add(JcrCollectionHoldString.class);
			classes.add(YearPlan.class);
			classes.add(MeetingE.class);
			classes.add(Cal.class);
			classes.add(Location.class);
			
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
			
			ocm.update(user);
			ocm.save();
	        
			}catch(Exception e){e.printStackTrace();}
		
		
	}

	@Override
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

}
