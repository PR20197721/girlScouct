package org.girlscouts.vtk.ejb;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
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
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.dao.UserDAO;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.models.JcrCollectionHoldString;
import org.girlscouts.vtk.models.JcrNode;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.user.User;
//import javax.jcr.query.Query;
//import javax.jcr.query.QueryManager;
import javax.jcr.*;

@Component
@Service(value=MeetingDAO.class)
public class MeetingDAOImpl implements MeetingDAO {

   private Session session;
   
   @Reference
   private UserDAO userDAO; 
   
    @Reference
    private SessionPool pool;
    
    @Activate
    void activate() {
        this.session = pool.getSession();
    }
	

	//by planId
public java.util.List<MeetingE> getAllEventMeetings(String yearPlanId){
		
		java.util.List<MeetingE> meetings =null; 
		
		
		
		try{
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
	
	
public Meeting getMeeting(String path){
		
		Meeting meeting =null; 
		
		
		
		try{
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
		List<Class> classes = new ArrayList<Class>();	
		classes.add(MeetingE.class); 
		classes.add(Meeting.class);
		classes.add(Activity.class);
		classes.add(JcrCollectionHoldString.class);
		classes.add(JcrNode.class);
		
		Mapper mapper = new AnnotationMapperImpl(classes);
		ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	

		if( meeting==null )
		    meeting = getMeeting(meetingEvent.getRefId());
	
		//String newPath = meetingEvent.getPath()+"/"+meeting.getId()+"_"+Math.random();
		String newPath = user.getPath()+"/lib/meetings/"+meeting.getId()+"_"+Math.random();
		System.err.println("New Custom Meet path : "+ newPath );
		
		if( !session.itemExists(user.getPath()+"/lib/meetings/") ){
			ocm.insert( new JcrNode(user.getPath()+"/lib") );
			ocm.insert( new JcrNode(user.getPath()+"/lib/meetings") );
			ocm.save();
		}
	
		
	
		
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



public java.util.List<String> doSpellCheck(String word) throws Exception{
	
	
	java.util.List<String> suggest= new java.util.ArrayList();

	

	javax.jcr.query.QueryManager qm = (javax.jcr.query.QueryManager)session.getWorkspace().getQueryManager();
	

    javax.jcr.query.Query query = qm.createQuery("SELECT rep:spellcheck() FROM nt:base WHERE jcr:path = '/content/dam/' AND SPELLCHECK('"+ word +"')",  javax.jcr.query.Query.SQL);
   RowIterator rows = query.execute().getRows();
   // the above query will always return the root node no matter what string we check
   Row r = rows.nextRow();
   // get the result of the spell checking
   
   
   Value v = r.getValue("rep:spellcheck()");
   if (v == null) {
       // no suggestion returned, the spelling is correct or the spell checker
       // does not know how to correct it.
   } else {
       String suggestion = v.getString();
       suggest.add( suggestion);
       System.err.println(suggestion);
   }
   
   
   
   Value values[] = r.getValues();
   for(int i=0;i< values.length;i++){
	   System.err.println( values[i].getString());
	   suggest.add(values[i].getString());
   }
   
return suggest;
}


public List<org.girlscouts.vtk.models.Search> getData(String query) {
	  
	//System.err.println("SEarch q: "+ query);
	List<org.girlscouts.vtk.models.Search> matched = new ArrayList<org.girlscouts.vtk.models.Search>();
	
	try{
		javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
		javax.jcr.query.Query q = qm.createQuery("select jcr:path, excerpt(.) from nt:resource    where jcr:path like '/content/dam/%' and  contains(., '"+ query +"~0.8')", javax.jcr.query.Query.SQL); 
   QueryResult result = q.execute();
   
 
   
   for (RowIterator it = result.getRows(); it.hasNext(); ) {
       Row r = it.nextRow();
       Value excerpt = r.getValue("rep:excerpt(.)");
       
       org.girlscouts.vtk.models.Search search = new org.girlscouts.vtk.models.Search();
       search.setPath(r.getPath());
       search.setContent(excerpt.getString());
       
       matched.add(search);
      // System.err.println( "SEarch: "+excerpt.getString());
   }
	}catch(Exception e){e.printStackTrace();}
   return matched;
	}



public void addAsset(User user, String meetingUid,  Asset asset){
	
	java.util.List<MeetingE> meetings = user.getYearPlan().getMeetingEvents();
	for(int i=0;i<meetings.size();i++)
			if( meetings.get(i).getUid().equals( meetingUid))
				meetings.get(i).getAssets().add( asset );
				
	userDAO.updateUser(user);
	
}





}//edn class
