package org.girlsscout.vtk.ejb;

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
import org.girlsscout.vtk.dao.LocationDAO;
import org.girlsscout.vtk.models.Activity;
import org.girlsscout.vtk.models.JcrCollectionHoldString;
import org.girlsscout.vtk.models.Location;
import org.girlsscout.vtk.models.MeetingE;
import org.girlsscout.vtk.models.YearPlan;
import org.girlsscout.vtk.models.user.User;

public class LocationDAOImpl implements LocationDAO{

	

	@Override
	public void removeLocation(User user, String locationName) {
		
		try{
			System.err.println("Rm loc: "+ locationName);
			Session session = getConnection();
			List<Class> classes = new ArrayList<Class>();	
			classes.add(User.class);
			classes.add(Activity.class);
			classes.add(JcrCollectionHoldString.class);
			classes.add(YearPlan.class);
			classes.add(MeetingE.class);
			classes.add(Location.class);
			
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
		
			
			YearPlan plan = user.getYearPlan();
			List<Location> locations = plan.getLocations();
			for(int i=0;i<locations.size();i++){
				Location location = locations.get(i);
				if( location.getName().equals(locationName)){
				
					ocm.remove(location);
					ocm.save();
					
					locations.remove(location);
					
					break;
				}
			}
			ocm.update(user);
			ocm.save();
	        
			}catch(Exception e){e.printStackTrace();}
		
		
		
		
	}
	
	
	


	private Session getConnection() throws Exception{
		
		
		// Connection
        Repository repository = JcrUtils.getRepository("http://localhost:4502/crx/server/");
        
        //Workspace Login
        SimpleCredentials creds = new SimpleCredentials("admin", "admin".toCharArray());
        Session session = null;
        session = repository.login(creds, "crx.default");
		return session;
	}
	

}
