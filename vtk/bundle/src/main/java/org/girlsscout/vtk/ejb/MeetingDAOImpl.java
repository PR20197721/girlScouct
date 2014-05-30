package org.girlsscout.vtk.ejb;

import java.util.ArrayList;
import java.util.List;

//import javax.jcr.query.Query;
//import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.apache.jackrabbit.ocm.query.Filter;
import org.apache.jackrabbit.ocm.query.Query;
import org.apache.jackrabbit.ocm.query.QueryManager;

import org.girlsscout.vtk.dao.MeetingDAO;

import org.girlsscout.vtk.models.Activity;
import org.girlsscout.vtk.models.JcrCollectionHoldString;
import org.girlsscout.vtk.models.Meeting;
import org.girlsscout.vtk.models.MeetingE;
import org.girlsscout.vtk.models.YearPlan;
import org.girlsscout.vtk.models.user.User;

public class MeetingDAOImpl implements MeetingDAO {

	

	//by planId
public java.util.List<MeetingE> getAllEventMeetings(String yearPlanId){
		
		java.util.List<MeetingE> meetings =null; 
		
		
		
		try{
			Session session = getConnection();
			List<Class> classes = new ArrayList<Class>();	
			classes.add(MeetingE.class); 
			
			
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
		
		/*
			MeetingE m= new MeetingE();
			m.setPath("/content/girlscouts-vtk/yearPlanTemplates/yearplan2014/brownie/yearPlan/meetings/meeting15");
			m.setRefId("test");
			ocm.insert(m);
			ocm.save();
			*/
			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(MeetingE.class);
			
	      
	        filter.setScope(  "/content/girlscouts-vtk/yearPlanTemplates/yearplan2014/brownie/yearPlan"+yearPlanId+"/meetings/");
	      
	        Query query = queryManager.createQuery(filter);
	        
	        
	        //System.err.println( "test: "+ocm.getObjects(query).size());
	        
	         meetings = (List<MeetingE> ) ocm.getObjects(query);
	      
	        
	        
			//System.err.println("MeetingEvents: "+ meetings.size());
			
			}catch(Exception e){e.printStackTrace();}
		
		
		return meetings;
}




//by plan path
public java.util.List<MeetingE> getAllEventMeetings_byPath(String yearPlanPath){
	
	java.util.List<MeetingE> meetings =null; 
	
	
	
	try{
		Session session = getConnection();
		List<Class> classes = new ArrayList<Class>();	
		classes.add(MeetingE.class); 
		
		
		Mapper mapper = new AnnotationMapperImpl(classes);
		ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
	

		QueryManager queryManager = ocm.getQueryManager();
		Filter filter = queryManager.createFilter(MeetingE.class);
		
      
        filter.setScope(  yearPlanPath);
      
        Query query = queryManager.createQuery(filter);
        
        
         meetings = (List<MeetingE> ) ocm.getObjects(query);
      
        
        
		//System.err.println("MeetingEvents by path: "+ meetings.size() +" : "+ yearPlanPath);
		
		}catch(Exception e){e.printStackTrace();}
	
	
	return meetings;
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
	
	
	
	public java.util.List<Meeting> getAllMeetings(String yearPlanId){
		
		java.util.List<Meeting> meetings =null; //new java.util.ArrayList();
		
/*
		meetings= new java.util.ArrayList();
		for(int i=0;i<15;i++){
			
			Meeting meeting = new Meeting();
			meeting.setId(""+(i+1));
			meeting.setName("meeting name"+ (i+1));
		
			meetings.add(meeting);
			
		}
		if(true) return meetings;
	*/	
		
		try{
			Session session = getConnection();
			List<Class> classes = new ArrayList<Class>();	
			classes.add(Meeting.class); 
			classes.add(Activity.class);
			classes.add(JcrCollectionHoldString.class);
			
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
		
		
			
			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(Meeting.class);
			
	       // filter.setScope(  "/content/girlscouts-vtk/meetings/");
	        filter.setScope(  "/content/girlscouts-vtk/meetings/myyearplan/brownie/");
	       
	        Query query = queryManager.createQuery(filter);
	         meetings = (List<Meeting> ) ocm.getObjects(query);
	      
	        
	        
			//System.err.println("Meetings: "+ meetings.size());
			
			}catch(Exception e){e.printStackTrace();}
		
		
		
		
		
		/*
		for(int i=1;i<15;i++){
			
			Meeting meeting = new Meeting();
			meeting.setId(""+(i+1));
			meeting.setName("meeting name"+ (i+1));
		
			meetings.add(meeting);
			
		}
		*/
		return meetings;
	
	}
	
	
	public static void main(String []args){
		
		new MeetingDAOImpl().search();
	}
	
	
	
public Meeting getMeeting(String path){
		
		Meeting meeting =null; 
		
		
		
		try{
			Session session = getConnection();
			List<Class> classes = new ArrayList<Class>();	
			 
			classes.add(Meeting.class); 
			classes.add(Activity.class);
			classes.add(JcrCollectionHoldString.class);
			
			
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
		
	
			meeting = (Meeting)ocm.getObject(path);
	        
			//System.err.println("Meeting: "+ ( meeting==null) );
			
			}catch(Exception e){e.printStackTrace();}
		
		
		return meeting;
}





//get all event meetings for users plan
public java.util.List<MeetingE> getAllUsersEventMeetings(User user, String yearPlanId){
	
	//TOD yearPLanId ????
	
	java.util.List<MeetingE> meetings =null; 
	
	
	
	try{
		Session session = getConnection();
		List<Class> classes = new ArrayList<Class>();	
		classes.add(MeetingE.class); 
		
		Mapper mapper = new AnnotationMapperImpl(classes);
		ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
	
		QueryManager queryManager = ocm.getQueryManager();
		Filter filter = queryManager.createFilter(MeetingE.class);
		
        //filter.setScope(  "/content/girlscouts-vtk/yearPlanTemplates/yearplan2014/brownie/yearPlan"+yearPlanId+"/meetings/");
      filter.setScope("/content/girlscouts-vtk/users/"+ user.getId() +"/yearPlan/meetingEvents/");
        Query query = queryManager.createQuery(filter);
        
         meetings = (List<MeetingE> ) ocm.getObjects(query);
      
		}catch(Exception e){e.printStackTrace();}
	
	
	return meetings;
}



//get all event meetings for users plan
public Meeting createCustomMeeting(User user, MeetingE meetingEvent){ return createCustomMeeting(user, meetingEvent, null); }
public Meeting createCustomMeeting(User user, MeetingE meetingEvent, Meeting meeting){
	
	//Meeting meeting =null;
	try{
		Session session = getConnection();
		List<Class> classes = new ArrayList<Class>();	
		classes.add(MeetingE.class); 
		classes.add(Meeting.class);
		classes.add(Activity.class);
		classes.add(JcrCollectionHoldString.class);
		
		Mapper mapper = new AnnotationMapperImpl(classes);
		ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	

		if( meeting==null )
		    meeting = getMeeting(meetingEvent.getRefId());
	
		//String newPath = meetingEvent.getPath()+"/"+meeting.getId()+"_"+Math.random();
		String newPath = user.getPath()+"/lib/meetings/"+meeting.getId()+"_"+Math.random();
		System.err.println("New Custom Meet path : "+ newPath );
		
		meetingEvent.setRefId(newPath);
		meeting.setPath(newPath);
		
		ocm.insert(meeting);
		ocm.update(meetingEvent);
		ocm.save();
		
	}catch(Exception e){e.printStackTrace();}
	
	return meeting;
	
}


public Meeting addActivity(Meeting meeting, Activity activity){
	
	
	java.util.List <Activity> activities = meeting.getActivities();
	activities.add( activity);
	meeting.setActivities(activities);
	
	try{
	Session session = getConnection();
	List<Class> classes = new ArrayList<Class>();	
	classes.add(MeetingE.class); 
	classes.add(Meeting.class);
	classes.add(Activity.class);
	classes.add(JcrCollectionHoldString.class);
	
	Mapper mapper = new AnnotationMapperImpl(classes);
	ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	

	ocm.update(meeting);
	ocm.save();
	
	}catch(Exception e){e.printStackTrace();}
	
	
	return meeting;
	
}


public List< Meeting> search(){
	
	List< Meeting> meetings=null;
	try{
		Session session = getConnection();
		List<Class> classes = new ArrayList<Class>();	
		classes.add(Meeting.class); 
		classes.add(Activity.class);
		classes.add(JcrCollectionHoldString.class);
		
		Mapper mapper = new AnnotationMapperImpl(classes);
		ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
	
	
		QueryManager queryManager = ocm.getQueryManager();
		Filter filter = queryManager.createFilter(Meeting.class);
		
      
        filter.setScope(  "/content/girlscouts-vtk/meetings/myyearplan/brownie/");
        Query query = queryManager.createQuery(filter);
        
        
        meetings = (List<Meeting> ) ocm.getObjects(query);
      
        
        
		System.err.println("Meeting search: "+ meetings.size());
		
		}catch(Exception e){e.printStackTrace();}
	
	return meetings;
	
	
}




}//edn class
