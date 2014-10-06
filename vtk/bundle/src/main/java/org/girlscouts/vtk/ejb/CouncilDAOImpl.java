package org.girlscouts.vtk.ejb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import org.apache.jackrabbit.ocm.query.QueryManager;
import org.girlscouts.vtk.dao.CouncilDAO;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.Council;
import org.girlscouts.vtk.models.JcrNode;
import org.girlscouts.vtk.models.Location;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.YearPlan;

@Component
@Service(value = CouncilDAO.class)
public class CouncilDAOImpl implements CouncilDAO {

	//private Session session;

	@Reference
	private SessionPool pool;

	@Activate
	void activate() {
		//this.session = pool.getSession();
	}

	public Council findCouncil(String councilId) {
		Council council = null;
		Session session =null;
		try {
			session = pool.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Council.class);
			classes.add(YearPlan.class); 
			classes.add(MeetingE.class); 
			classes.add(Location.class);
			classes.add(Cal.class);
			classes.add(Activity.class);
			classes.add(Asset.class);
			classes.add(JcrNode.class);
			classes.add(Milestone.class);
			classes.add(Troop.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
			
			council = (Council) ocm.getObject("/vtk/"+ councilId);

			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if( session!=null )
					pool.closeSession(session);
			}catch(Exception ex){ex.printStackTrace();}
		}

	
		return council;
	}

	public Council createCouncil(String councilId) {
		Session session =null;
		Council council = null;
		try {
			session = pool.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Council.class);
			classes.add(YearPlan.class); 
			classes.add(MeetingE.class); 
			classes.add(Location.class);
			classes.add(Cal.class);
			classes.add(Activity.class);
			classes.add(Asset.class);
			classes.add(JcrNode.class);
			classes.add(Milestone.class);
			classes.add(Troop.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
			
			
			council = new Council("/vtk/"+ councilId);
			ocm.insert(council);
			ocm.save();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if( session!=null )
					pool.closeSession(session);
			}catch(Exception ex){ex.printStackTrace();}
		}
		return council;

	}
	
	public Council getOrCreateCouncil(String councilId){
		
		
		Council council = findCouncil(councilId);		
		if (council == null)
			council = createCouncil(councilId);
		
		return council;
	}
	
	public void updateCouncil(Council council){
		Session session = null;
		try {
			session =pool.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Council.class);
			classes.add(YearPlan.class); 
			classes.add(MeetingE.class); 
			classes.add(Location.class);
			classes.add(Cal.class);
			classes.add(Activity.class);
			classes.add(Asset.class);
			classes.add(JcrNode.class);
			classes.add(Milestone.class);
			classes.add(Troop.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(session,
					mapper);
			
			ocm.update(council);
			ocm.save();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if( session!=null )
					pool.closeSession(session);
			}catch(Exception ex){ex.printStackTrace();}
		}
	}

}
