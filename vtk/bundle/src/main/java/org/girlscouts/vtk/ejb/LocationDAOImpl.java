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
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.dao.LocationDAO;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.JcrCollectionHoldString;
import org.girlscouts.vtk.models.Location;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.utils.TroopUtil;

@Component
@Service(value=LocationDAO.class)
public class LocationDAOImpl implements LocationDAO{

    @Reference
    private SessionFactory sessionFactory;
    
    @Activate
    void activate() {}

    @Reference
    private TroopUtil troopUtil;
    
    @Reference
    private MeetingDAO meetingDAO;
    
	public void removeLocation(Troop user, String locationName) {
		Session session =null;
		try{
			
			
			if( !meetingDAO.hasAccess(user, user.getCurrentTroop(), Permission.PERMISSION_CANCEL_MEETING_ID ) ){
				 user.setErrCode("112");
				 return;
			 }
			
			
			session = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();	
			classes.add(Troop.class);
			classes.add(Activity.class);
			classes.add(JcrCollectionHoldString.class);
			classes.add(YearPlan.class);
			classes.add(MeetingE.class);
			classes.add(Location.class);
			classes.add(Cal.class);
			classes.add(Milestone.class);
			classes.add(Asset.class);
			
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
		
			String locationToRmPath=null;
			YearPlan plan = user.getYearPlan();
			List<Location> locations = plan.getLocations();
			for(int i=0;i<locations.size();i++){
				Location location = locations.get(i);
				if( location.getUid().equals(locationName)){
					
					
					ocm.remove(location);
					ocm.save();
					
					
					locationToRmPath= location.getPath() ;
					locations.remove(location);
					
					break;
				}
			}
			
			
			//update every refLoc & set 2 null
			java.util.List<MeetingE> meetings = plan.getMeetingEvents();
			
			for(int i=0;i<meetings.size();i++){
				
				if( meetings.get(i).getLocationRef()!=null && 
						meetings.get(i).getLocationRef().equals( locationToRmPath ) ){
						meetings.get(i).setLocationRef("");
						
				}
			}
			
			
			troopUtil.updateTroop(user);
			
			}catch(Exception e){e.printStackTrace();
			}finally{
				try{
					if( session!=null )
						sessionFactory.closeSession(session);
				}catch(Exception ex){ex.printStackTrace();}
			}
		
		
		
		
	}
	

}
