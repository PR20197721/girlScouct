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
import org.girlscouts.vtk.dao.ActivityDAO;
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

	public void createActivity(User user, Activity activity) {
		
		try{
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
			
			ocm.update(user);
			ocm.save();
	        
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

}
