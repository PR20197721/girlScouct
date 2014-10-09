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
//import org.apache.jackrabbit.ocm.query.Query;
//import org.apache.jackrabbit.ocm.query.QueryManager;
import org.girlscouts.vtk.dao.YearPlanDAO;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.models.YearPlan;
//import javax.jcr.query.Query;
//import javax.jcr.query.QueryManager;
//import javax.jcr.query.QueryManager;

@Component
@Service(value=YearPlanDAO.class)
public class YearPlanDAOImpl implements YearPlanDAO{

   //private Session session;
    
    @Reference
    private SessionFactory sessionFactory;
    
    @Activate
    void activate() {
       // this.session = sessionFactory.getSession();
    }
	
	//public List<YearPlan> test() {
	public List<YearPlan> getAllYearPlans(String ageLevel) {
		java.util.List <YearPlan> yearPlans =null;
		Session session =null;
		try{
			session = sessionFactory.getSession();
		List<Class> classes = new ArrayList<Class>();	
		classes.add(YearPlan.class); 
		classes.add(Meeting.class); 
		classes.add(Cal.class);
		
		Mapper mapper = new AnnotationMapperImpl(classes);
		ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
	
		
//ageLevel=("brownie");
		QueryManager queryManager = ocm.getQueryManager();
		Filter filter = queryManager.createFilter(YearPlan.class);
		
		java.util.Calendar today=  java.util.Calendar.getInstance();
		int year= today.get(java.util.Calendar.YEAR);
		
     //-  filter.setScope(  "/content/girlscouts-vtk/meetings/myyearplan/");
		//-filter.setScope(  "/content/girlscouts-vtk/yearPlans/yearplan2014/brownie/");
		//----filter.setScope(  "/content/girlscouts-vtk/yearPlans/yearplan"+year+"/"+ageLevel+"/");
		filter.setScope("/content/girlscouts-vtk/yearPlanTemplates/yearplan"+year+"/"+ageLevel+"/");
		
        Query query = queryManager.createQuery(filter);
        
      yearPlans = ( List <YearPlan>)  ocm.getObjects(query);
       // List<Object> o = (List<Object>) ocm.getObjects(query);
        // System.err.println("tes: "+ (o ==null) +" : "+o.size() +" : "+(o.get(0)==null)  );
        // System.err.println( "Fopund YEARPL: "+( yearPlans.size() ) +" : "+ yearPlans.get(0).getName()  );
		
		
        
		}catch(Exception e){e.printStackTrace();
		}finally{
			try{
				if( session!=null )
					sessionFactory.closeSession(session);
			}catch(Exception ex){ex.printStackTrace();}
		}
		return yearPlans;
	}
		
	
	
	public List<YearPlan> test() {
	//public List<YearPlan> getAllYearPlans() {
		java.util.List plans= new java.util.ArrayList();
		
		for(int i=0;i<4;i++){
			YearPlan plan = new YearPlan();
			plan.setDesc("Descript "+i);
			plan.setId("YRP"+i);
			plan.setName("Name "+i);
			plans.add(plan);
		}
		
		return plans;
	}

	
	
	
	
	
	
		public YearPlan getYearPlan(String path) {
			YearPlan yearPlan =null;
			Session session = null;
			try{
			List<Class> classes = new ArrayList<Class>();	
			classes.add(YearPlan.class); 
			classes.add(Meeting.class); 
			classes.add(Cal.class);

			session =sessionFactory.getSession();
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
		
			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(YearPlan.class);
			
		
	        Query query = queryManager.createQuery(filter);
	        
	   yearPlan =(YearPlan)  ocm.getObject(path);
	       
	        
			}catch(Exception e){e.printStackTrace();
			}finally{
				try{
					if( session!=null )
						sessionFactory.closeSession(session);
				}catch(Exception ex){ex.printStackTrace();}
			}
			return yearPlan;
		}
			
}
