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
import org.girlscouts.vtk.dao.LocationDAO;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.JcrCollectionHoldString;
import org.girlscouts.vtk.models.Location;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.user.User;

@Component
@Service(value=LocationDAO.class)
public class LocationDAOImpl implements LocationDAO{

   private Session session;
    
    @Reference
    private SessionPool pool;
    
    @Activate
    void activate() {
        this.session = pool.getSession();
    }

	public void removeLocation(User user, String locationName) {
		
		try{
			System.err.println("Rm loc: "+ locationName);
			List<Class> classes = new ArrayList<Class>();	
			classes.add(User.class);
			classes.add(Activity.class);
			classes.add(JcrCollectionHoldString.class);
			classes.add(YearPlan.class);
			classes.add(MeetingE.class);
			classes.add(Location.class);
			classes.add(Cal.class);
			
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
		
			String locationToRmPath=null;
			YearPlan plan = user.getYearPlan();
			List<Location> locations = plan.getLocations();
			for(int i=0;i<locations.size();i++){
				Location location = locations.get(i);
				if( location.getName().equals(locationName)){
					
					ocm.remove(location);
					ocm.save();
					
					locationToRmPath= location.getPath() ;
					locations.remove(location);
					
					break;
				}
			}
			
			
			//update every refLoc & set 2 null
			java.util.List<MeetingE> meetings = plan.getMeetingEvents();
			
			//System.err.println("Meeting size: "+ meetings.size() +" :" + locationToRmPath);
			for(int i=0;i<meetings.size();i++){
				
				//System.err.println( i +" : "+meetings.get(i).getLocationRef() );
				if( meetings.get(i).getLocationRef()!=null && 
						meetings.get(i).getLocationRef().equals( locationToRmPath ) ){
						meetings.get(i).setLocationRef("");
						
						//System.err.println("rm loc Meeting "+meetings.get(i).getPath() +" : "+ meetings.get(i).getLocationRef());
				}
			}
			
			ocm.update(user);
			ocm.save();
	        
			}catch(Exception e){e.printStackTrace();}
		
		
		
		
	}
	

}
