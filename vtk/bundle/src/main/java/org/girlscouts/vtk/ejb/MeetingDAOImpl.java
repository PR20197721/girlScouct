package org.girlscouts.vtk.ejb;

import java.io.FileOutputStream;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
//import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;


import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;

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
import org.apache.jackrabbit.ocm.query.Query;
import org.apache.jackrabbit.ocm.query.QueryManager;
import org.girlscouts.vtk.dao.AssetComponentType;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.dao.UserDAO;
import org.girlscouts.vtk.dao.YearPlanComponentType;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.JcrCollectionHoldString;
import org.girlscouts.vtk.models.JcrNode;
import org.girlscouts.vtk.models.Location;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.models.SearchTag;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.YearPlanComponent;
import org.girlscouts.vtk.models.user.User;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

import com.day.cq.search.QueryBuilder;

import javax.jcr.*;




@Component
@Service(value=MeetingDAO.class)
public class MeetingDAOImpl implements MeetingDAO {

   private Session session;
   
    @Reference
    private SessionPool pool;
    
    @Reference
    private QueryBuilder qBuilder;
    
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

/*
	public java.util.List<Meeting> getAllMeetings(String yearPlanId){
		
		java.util.List<Meeting> meetings =null; //new java.util.ArrayList();
		
	
		
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
		
		
		
		
		
		
		return meetings;
	
	}
	*/
	
	
	
	
	
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
		classes.add( Asset.class);
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


public List<org.girlscouts.vtk.models.Search> getData(String _query) {
	

	List<org.girlscouts.vtk.models.Search> matched = new ArrayList<org.girlscouts.vtk.models.Search>();

		
                   
    // create query description as hash map (simplest way, same as form post)
    java.util.Map<String, String> map = new java.util.HashMap<String, String>();
   
// create query description as hash map (simplest way, same as form post)
    map.put("path", "/content/dam/girlscouts-vtk/global/aid");
   // map.put("type", "nt:unstructured");
    map.put("fulltext", _query);
   
    // map.put("group.p.and", "true"); // combine this group with OR
//map.put("group.1_relPath","metadata");
//map.put(key, value);
     
     
     //map.put("group.1_fulltext", fulltextSearchTerm);
 // map.put("group.1_fulltext.relPath", "jcr:content");
   // map.put("group.2_fulltext", fulltextSearchTerm);
   // map.put("group.2_fulltext.relPath", "jcr:content/@cq:tags");
  
    // can be done in map or with Query methods
    map.put("p.offset", "0"); // same as query.setStart(0) below
    map.put("p.limit", "20"); // same as query.setHitsPerPage(20) below
                     
    
    
    com.day.cq.search.Query query = qBuilder.createQuery(PredicateGroup.create(map), session);
    query.setStart(0);
    query.setHitsPerPage(20);
               
   SearchResult result = query.getResult();
   
   System.err.println("Res: "+ result.getHits().size() +" : "+ result.getExecutionTimeMillis() +" : "+ _query);
   for (Hit hit : result.getHits()) {
       try {
		String path = hit.getPath();
		//System.err.println( "\n\n__________"+ path +" : "+hit.getExcerpt() +" :" + hit.getTitle());
		
		if( !path.endsWith("original")) continue;
		
		Node caca = hit.getNode().getParent().getParent().getNode("metadata");
		//System.err.println(caca.getPath() );//+" : "+ caca.getProperty("jcr:mimeType"));
		
		
		   org.girlscouts.vtk.models.Search search = new org.girlscouts.vtk.models.Search();
		   
		   
		   String p= caca.getPath();
		   p= p.substring(0, p.indexOf("/jcr:content/") );
		   
		   
	       search.setPath(p);
	       search.setDesc( caca.getProperty("dc:title").getString() );
	       //search.setType( hit.getNode().getProperty("jcr:mimeType").getString()   );
	       search.setContent(  hit.getExcerpt() );
	       //search.setType(caca.getProperty("jcr:mimeType").getString());
	       matched.add(search);
		
	} catch (RepositoryException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }
   
   
  /*
    // paging metadata
    int hitsPerPage = result.getHits().size(); // 20 (set above) or lower
    long totalMatches = result.getTotalMatches();
    long offset = result.getStartIndex();
    long numberOfPages = totalMatches / 20;
                  
    //Place the results in XML to return to client
    DocumentBuilderFactory factory =     DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.newDocument();
                               
    //Start building the XML to pass back to the AEM client
    Element root = doc.createElement( "results" );
    doc.appendChild( root );
                  
    // iterating over the results
    for (Hit hit : result.getHits()) {
       String path = hit.getPath();
  
      //Create a result element
      Element resultel = doc.createElement( "result" );
      root.appendChild( resultel );
                      
      Element pathel = doc.createElement( "path" );
      pathel.appendChild( doc.createTextNode(path ) );
      resultel.appendChild( pathel );
    }
    */
    return matched;
}
public List<org.girlscouts.vtk.models.Search> getDataSQL2(String query) {
	  
	//System.err.println("SEarch q: "+ query);
	List<org.girlscouts.vtk.models.Search> matched = new ArrayList<org.girlscouts.vtk.models.Search>();
	
	try{
		javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
		//javax.jcr.query.Query q = qm.createQuery("select jcr:path, excerpt(.) from nt:resource  where jcr:path like '/content/dam/%' and  contains(., '"+ query +"~')", javax.jcr.query.Query.SQL); 
	
		
		//String sql="select * from nt:base where jcr:path like '/content/dam/girlscouts-vtk/global/aid/%' and contains(*, 'girl') and jcr:primaryType='dam:Asset'";
	
		//String sql= "select child.start, parent.[jcr:title], child.details, child.end,child.locationLabel,child.srchdisp 
		//from [nt:base] as parent INNER JOIN [nt:base] as child ON ISCHILDNODE(child, parent) where 
		//(isdescendantnode (parent, [/content/gateway/en/events/2014])) and child.start is not null and parent.[jcr:title] is not null " ;
		
		
		//String sql ="select child.[dc:title], child.[dc:format], parent.[jcr:path] , child.[dc:description] " +
		
		String sql="select parent.*, child.* , child.[dc:title], child.[dc:description] , " +
				"	child.[dc:format], parent.name, parent.[jcr:path], child.name, child.[jcr:path], parent.[jcr:uuid]," +
				"  child3.*, child3.[jcr:uuid], child3.[jcr:mimeType], child3.[excerpt(.)] "+		
				" from [nt:base] as parent " +
				" INNER JOIN [nt:base] as child on ISCHILDNODE(child, parent) " +
				" INNER JOIN [nt:resource] as child3 on ISDESCENDANTNODE(child3, parent)  " +
				" where " +
				"(isdescendantnode (parent, [/content/dam/girlscouts-vtk/global/aid]))  " +
		
				" and child.[dc:title] is not null "+
				" and (  contains(child3.*, '"+ query +"~' )) "+
				" and child3.[jcr:uuid] is not null ";
		
		
		System.err.println( sql );
		//AID search
		javax.jcr.query.Query q = qm.createQuery(sql,  javax.jcr.query.Query.JCR_SQL2);
		
		
		QueryResult result = q.execute();
   
		String str[] = result.getColumnNames();
		for(int i=0;i<str.length; i++)
			System.err.println( str[i] );
		
	
		
		 for (RowIterator it = result.getRows(); it.hasNext(); ) {
		       Row r = it.nextRow();
		       /*
		       System.err.println(r.getValue("parent.jcr:primaryType").getString() +" : "+
		    		   r.getValue("child.jcr:primaryType").getString() + " : "+
		    		   r.getValue("child1.jcr:primaryType").getString() +" : "+
		    		   r.getValue("child2.jcr:primaryType").getString() +" : "+
		    		   r.getValue("child3.jcr:primaryType").getString() +" : "
		    		   
		    		   );
		       */
		       
		       
		       
		       try{  System.err.println( r.getValue("rep:excerpt(.)").getString() ); }catch(Exception e){e.printStackTrace();}
		      try{ System.err.println(r.getNode().getName()); }catch(Exception e){}
		      try{  System.err.println("TESTPATH: "+r.getPath("parent.name") ); }catch(Exception e){}
		      try{ System.err.println("*"+ r.getValue("parent.name").getString());}catch(Exception e){}
		       try{ System.err.println( r.getValue("parent.jcr:path").getString() );}catch(Exception e){}
		       try{ System.err.println("*"+ r.getValue("child.name").getString());}catch(Exception e){}
		       try{ System.err.println( r.getValue("child.jcr:path").getString() );}catch(Exception e){}
		       try{ System.err.println("*"+ r.getPath("parent.name"));}catch(Exception e){}
		       try{ System.err.println("<"+ r.getPath("parent.jcr:path") );}catch(Exception e){}
		       try{ System.err.println("*"+ r.getPath("child.name"));}catch(Exception e){}
		       try{ System.err.println( ">"+ r.getPath("child.jcr:path") );}catch(Exception e){}
		       try{ System.err.println( ">"+ r.getValue("child.dc:title").getString() );}catch(Exception e){}
		       
		    	try{ System.err.println( ">"+ r.getValue("child3.jcr:uuid").getString() );}catch(Exception e){}
		    	
		    	try{ System.err.println( ">"+ r.getValue("child3.excerpt(.)").getString() );}catch(Exception e){}
		       // System.err.println("TATA: "+ r.getValue("excerpt(.)").getString()  );
		       
		    	
		    	
		    	
		    	
		    	
		       org.girlscouts.vtk.models.Search search = new org.girlscouts.vtk.models.Search();
		    
		      // System.err.println("PATH: "+  r.getPath() );
		       
		      // search.setPath( r.getValue("parent.jcr:path").getString() );
		       search.setPath("test");
		       search.setDesc( r.getValue("child.dc:title").getString()  );
		       search.setType( r.getValue("child.dc:format").getString()  );
		       search.setContent(  r.getValue("child.dc:description").getString());
		       search.setType(r.getValue("child3.jcr:mimeType").getString());
		       matched.add(search);
		 }
		
   
	}catch(Exception e){e.printStackTrace();}
   return matched;
	}
/*

public List<org.girlscouts.vtk.models.Search> getData(String query) {
	  
	//System.err.println("SEarch q: "+ query);
	List<org.girlscouts.vtk.models.Search> matched = new ArrayList<org.girlscouts.vtk.models.Search>();
	
	try{
		javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
		//GOOD FULL SEARCHjavax.jcr.query.Query q = qm.createQuery("select jcr:path, excerpt(.) from nt:resource  where jcr:path like '/content/dam/%' and  contains(., '"+ query +"~')", javax.jcr.query.Query.SQL); 
		
		//AID search
		javax.jcr.query.Query q = qm.createQuery("select dc:title,dc:format from nt:unstructured where jcr:path like '/content/dam/girlscouts-vtk/global/aid/%' and contains(*, '"+query+"~') order by jcr:score desc",  javax.jcr.query.Query.SQL);
		
		 		
		QueryResult result = q.execute();
   
 
   
   for (RowIterator it = result.getRows(); it.hasNext(); ) {
       Row r = it.nextRow();
       Value excerpt = r.getValue("rep:excerpt(.)");
       
       String path = r.getValue("jcr:path").getString();
       if (path != null ) {
       if(path.contains("/jcr:content") ) {
    	   path= path.substring(0, (path.indexOf("/jcr:content") ));
       }
       org.girlscouts.vtk.models.Search search = new org.girlscouts.vtk.models.Search();
       search.setPath(path);
       if (excerpt != null){
    	   search.setContent(excerpt.getString());
       }
       Value title = r.getValue("dc:title");
       if (title != null) {
    	   search.setDesc(title.getString() );
    	   
       }
       Value format = r.getValue("dc:format");
       if (format != null) {
    	   search.setType(format.getString());
       }
       matched.add(search);
       }
   }
	}catch(Exception e){e.printStackTrace();}
   return matched;
	}
	*/







public java.util.List<Asset> getAids(String tags, 
		String meetingName, String uids){
	
	java.util.List<Asset> container = new java.util.ArrayList();
	
	container.addAll( getAidTag_local( tags,  meetingName));
	container.addAll( getAidTag( tags, meetingName) );
	
	/*
	StringTokenizer t= new StringTokenizer( uids, ",");
	while( t.hasMoreElements())
		container.addAll( getAidTag_custasset(t.nextToken()) );
	*/
	
	return container;
}




private List<Asset> getAidTag(String tags, String meetingName) {
	  
	
	List<Asset> matched = new ArrayList<Asset>();
	
	try{
		
		/* getAids ????
		   List<Asset> matched_local= getAidTag_local(tags,meetingName) ;
		   matched.addAll(matched_local);
		  */
		
		if( tags==null || tags.trim().equals("")) return matched;
		
		String sql_tag="";
		java.util.StringTokenizer t= new java.util.StringTokenizer( tags, ";");
		while( t.hasMoreElements()){
			
			String tag = t.nextToken();
			sql_tag += "cq:tags like '%"+ tag +"%'"; //" contains(., '"+ tag +"')";
			
			if( t.hasMoreElements())
				sql_tag +=" or ";
		}
		
		
		String sql="";//select * from nt:unstructured where jcr:path like '/content/dam/girlscouts-vtk/global/aid/%'  and ( "+ sql_tag  +" ) ";
		//sql="select * from nt:base where jcr:primaryType='dam:Asset' and jcr:path like '/content/dam/girlscouts-vtk/global/aid/%' and ( "+ sql_tag  +" ) order by jcr:score desc";
		/*
		sql="select * from nt:base where jcr:primaryType='dam:Asset' and jcr:path like '/content/dam/girlscouts-vtk/global/aid/%' and" +
				" ("+ sql_tag +") order by jcr:score desc";
		*/
		sql="select dc:description,dc:format, dc:title from nt:unstructured where jcr:path like '/content/dam/girlscouts-vtk/global/aid/%'";
		if( !sql_tag.equals(""))
			sql+=   " and ( "+ sql_tag+" )";
		
		System.err.println( sql);
		
		javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
		javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL); 
   		
		 		
		QueryResult result = q.execute();
   
 
   
   for (RowIterator it = result.getRows(); it.hasNext(); ) {
       Row r = it.nextRow();
       //System.err.println( "PARAM: "+r.getValue("dc:format").getString() +" : "+ r.getValue("dc:description").getString());
       Value excerpt = r.getValue("jcr:path");
       
       String path = excerpt.getString();
       if( path.contains("/jcr:content") ) path= path.substring(0, (path.indexOf("/jcr:content") ));
      // System.err.println( "PATH :"+path );
    	
       
       Asset search = new Asset();
       search.setRefId(path);
       search.setType(AssetComponentType.AID);
       search.setIsCachable(true);
       //search.setContent(excerpt.getString());
       try{ search.setDescription( r.getValue("dc:description").getString() );}catch(Exception e){System.err.println("Global Aid Description missing");}
       //search.setType(r.getValue("dc:format").getString());
       try{ search.setTitle( r.getValue("dc:title").getString() );}catch(Exception e){}
       matched.add(search);
      
   }
   
   

   
   
	}catch(Exception e){e.printStackTrace();}
   return matched;
	}


private List<Asset> getAidTag_local(String tags, String meetingName) {
	  
	
	List<Asset> matched = new ArrayList<Asset>();
	
	try{
		/*
		String sql_tag="";
		java.util.StringTokenizer t= new java.util.StringTokenizer( tags, ";");
		while( t.hasMoreElements()){
			
			String tag = t.nextToken();
			sql_tag += " contains(., '"+ tag +"')";
			
			if( t.hasMoreElements())
				sql_tag +=" or ";
		}
		*/
		
		String sql="select dc:description,dc:format from nt:unstructured" +
           		"   where   jcr:path like '/content/dam/girlscouts-vtk/local/aid/Meetings/"+meetingName+"/%' ";
		
		
		//sql="select dc:description,dc:format from nt:base where  jcr:primaryType= 'dam:Asset' and jcr:path like '/content/dam/girlscouts-vtk/local/aid/Meetings/"+meetingName+"/%' ";
		
		sql="select dc:description,dc:format, dc:title  from nt:unstructured where  jcr:path like '/content/dam/girlscouts-vtk/local/aid/meetings/"+meetingName+"/%' and jcr:mixinTypes='cq:Taggable'";
		System.err.println( sql);
		
		javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
		javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL); 
   		
		 		
		QueryResult result = q.execute();
   
 
   
   for (RowIterator it = result.getRows(); it.hasNext(); ) {
       Row r = it.nextRow();
       Value excerpt = r.getValue("jcr:path");
       
       String path = excerpt.getString();
       if( path.contains("/jcr:content") ) path= path.substring(0, (path.indexOf("/jcr:content") ));
       //System.err.println( "PATH :"+path );
    		   
       Asset search = new Asset();
       search.setRefId(path);
       search.setType(AssetComponentType.AID);
       search.setIsCachable(true);
       //search.setContent(excerpt.getString());
      try{search.setDescription( r.getValue("dc:description").getString() );}catch(Exception e){}
      // try{search.setType(r.getValue("dc:format").getString());}catch(Exception e){}
      try{ search.setTitle( r.getValue("dc:title").getString() );}catch(Exception e){}
       matched.add(search);
      
   }
	}catch(Exception e){e.printStackTrace();}
   return matched;
	}





private List<Asset> getAidTag_custasset(String uid) {
	  
	
	List<Asset> matched = new ArrayList<Asset>();
	
	try{
		
		
		
		String sql= "select jcr:path from nt:base where jcr:path like '/vtk/111/troop-1a/assets/%' and refId='"+ uid +"'";
		
		javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
		javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL); 
   		
		 		
		QueryResult result = q.execute();
   
 
   
   for (RowIterator it = result.getRows(); it.hasNext(); ) {
       Row r = it.nextRow();
       Value excerpt = r.getValue("jcr:path");
       
       String path = excerpt.getString() +"/custasset";
      // if( path.contains("/jcr:content") ) path= path.substring(0, (path.indexOf("/jcr:content") ));
       //System.err.println( "PATH :"+path );
    		   
       Asset search = new Asset();
       search.setRefId(path);
       search.setIsCachable(true);
       //search.setContent(excerpt.getString());
        matched.add(search);
      
   }
	}catch(Exception e){e.printStackTrace();}
   return matched;
	}



@SuppressWarnings("unchecked")
public net.fortuna.ical4j.model.Calendar yearPlanCal(User user )throws Exception{
	 
	 java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(user.getYearPlan());
		
	  //String calFile = "/Users/akobovich/mycalendar.ics";
	  
	 
 
 //Creating a new calendar
 net.fortuna.ical4j.model.Calendar calendar = new net.fortuna.ical4j.model.Calendar();
 calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
 calendar.getProperties().add(Version.VERSION_2_0);
 calendar.getProperties().add(CalScale.GREGORIAN);
 
	  
	  
	  
	  java.util.Iterator itr = sched.keySet().iterator();
	  while( itr.hasNext() ){
		  
		  
		  
		  java.util.Date dt= (java.util.Date) itr.next();
		  YearPlanComponent _comp= (YearPlanComponent) sched.get(dt);
		  
		  Calendar cal = java.util.Calendar.getInstance();
		  cal.setTime(dt);
		  
		  String desc= "";
		  
		  switch( _comp.getType() ){
				case ACTIVITY :
					desc = ((Activity) _comp).getName();
					break;
				
				case MEETING :
					Meeting meetingInfo =  getMeeting(  ((MeetingE) _comp).getRefId() );
					desc = meetingInfo.getName();		
					break;
			}       	
		  
		  
		
	  
	  /*
	  //Creating an event
	  //java.util.Calendar cal = java.util.Calendar.getInstance();
	  //cal.set(java.util.Calendar.MONTH, java.util.Calendar.DECEMBER);
	  //cal.set(java.util.Calendar.DAY_OF_MONTH, 25);

	  VEvent christmas = new VEvent(new net.fortuna.ical4j.model.Date(cal.getTime()), desc);
	  
	  // initialise as an all-day event..
	  //christmas.getProperties().getProperty(net.fortuna.ical4j.model.Property.DTSTART).getParameters().add(net.fortuna.ical4j.model.parameter.Value.DATE);
	  
	  UidGenerator uidGenerator = new UidGenerator("1");
	  christmas.getProperties().add(uidGenerator.generateUid());

	  calendar.getComponents().add(christmas);
	  */
	  
	  
	  final List events = new ArrayList();
		/*
	  for (Iterator i = minutesList.iterator(); i.hasNext();) {
			final TimeSheetEntry entry = (TimeSheetEntry) i.next();
			*/
			
			
			final VEvent event = new VEvent(new DateTime(cal.getTime()), desc);
			//event.getProperties().add(new DtEnd(new DateTime(entry.getEndTime())));
			event.getProperties().add(new Description(desc));
			
			UidGenerator uidGenerator = new UidGenerator("1");
			event.getProperties().add(uidGenerator.generateUid());
//System.err.println("CAL: "+ cal.getTime() +" : "+ desc );
			events.add(event);
			
			
		//}
		calendar.getComponents().addAll(events);
	  
	  
	 
	  }//end while
	  return calendar;
}





//resources

public java.util.List<Asset> getResources(String tags, 
		String meetingName, String uids){
	
	java.util.List<Asset> container = new java.util.ArrayList();
	
	container.addAll( getResource_local( tags,  meetingName));
	container.addAll( getResource_global( tags, meetingName) );
	
	
	return container;
}




private List<Asset> getResource_global(String tags, String meetingName) {
	  
	
	List<Asset> matched = new ArrayList<Asset>();
	
	try{
		
		/* getResources ???
		 List<Asset> matched_local= getAidTag_local(tags,meetingName) ;
		   matched.addAll(matched_local);
	   */
		   
		  if( tags==null || tags.equals("")) return matched;
		
		String sql_tag="";
		java.util.StringTokenizer t= new java.util.StringTokenizer( tags, ";");
		while( t.hasMoreElements()){
			
			String tag = t.nextToken();
			sql_tag += "cq:tags like '%"+ tag +"%'"; 
			
			if( t.hasMoreElements())
				sql_tag +=" or ";
		}
		
		
		String sql="";
		
		sql="select dc:description,dc:format, dc:title from nt:unstructured where jcr:path like '/content/dam/girlscouts-vtk/global/resource/%'  ";
		if(!sql_tag.equals(""))
			sql+=" and ( "+ sql_tag+" )";
		
		System.err.println( sql);
		
		javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
		javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL); 
   		
		 		
		QueryResult result = q.execute();
   
 
   
   for (RowIterator it = result.getRows(); it.hasNext(); ) {
       Row r = it.nextRow();
       Value excerpt = r.getValue("jcr:path");
       
       String path = excerpt.getString();
       if( path.contains("/jcr:content") ) path= path.substring(0, (path.indexOf("/jcr:content") ));

       Asset search = new Asset();
       search.setRefId(path);
       search.setIsCachable(true);
       search.setType(AssetComponentType.RESOURCE);
       try{ search.setDescription( r.getValue("dc:description").getString() );}catch(Exception e){}
       try{ search.setTitle( r.getValue("dc:title").getString() );}catch(Exception e){}
       matched.add(search);
      
   }
   
 
	  
   
   
	}catch(Exception e){e.printStackTrace();}
   return matched;
	}


private List<Asset> getResource_local(String tags, String meetingName) {
	  
	
	List<Asset> matched = new ArrayList<Asset>();
	
	try{
	
		
		String sql="select dc:description,dc:format from nt:unstructured" +
           		"   where   jcr:path like '/content/dam/girlscouts-vtk/local/resource/meetings/"+meetingName+"/%' ";
		
		
		
		sql="select dc:description,dc:format, dc:title  from nt:unstructured where  jcr:path like '/content/dam/girlscouts-vtk/local/resource/meetings/"+meetingName+"/%' and jcr:mixinTypes='cq:Taggable'";
		System.err.println( sql);
		
		javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
		javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL); 
   		
		 		
		QueryResult result = q.execute();
   
 
   
   for (RowIterator it = result.getRows(); it.hasNext(); ) {
       Row r = it.nextRow();
       Value excerpt = r.getValue("jcr:path");
       
       String path = excerpt.getString();
       if( path.contains("/jcr:content") ) path= path.substring(0, (path.indexOf("/jcr:content") ));
       //System.err.println( "PATH :"+path );
    		   
       Asset search = new Asset();
       search.setRefId(path);
       search.setIsCachable(true);
       search.setType(AssetComponentType.RESOURCE);
       //search.setContent(excerpt.getString());
      try{search.setDescription( r.getValue("dc:description").getString() );}catch(Exception e){}
      try{search.setTitle( r.getValue("dc:title").getString() );}catch(Exception e){}
      // try{search.setType(r.getValue("dc:format").getString());}catch(Exception e){}
       matched.add(search);
      
   }
	}catch(Exception e){e.printStackTrace();}
   return matched;
	}
/*
public SearchTag searchA(){
	SearchTag tags = new SearchTag();
	try{
		
		java.util.List categories = new java.util.ArrayList();
		java.util.List levels = new java.util.ArrayList();
		
		String sql="select jcr:title from nt:base where jcr:path like '/etc/tags/girlscouts/%'";
		javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
		javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL); 
			
		QueryResult result = q.execute();
		System.err.println("SEAch1: "+ (result.getRows().getSize()) );
		 for (RowIterator it = result.getRows(); it.hasNext(); ) {
		       Row r = it.nextRow();
		       System.err.println("Search path : "+ r.getPath());
		       if( r.getPath().startsWith("/etc/tags/girlscouts/categories") )
		    	   categories.add( r.getValue("jcr:title").getString() );
		       else if( r.getPath().startsWith("/etc/tags/girlscouts/program-level") )
		    	   levels.add( r.getValue("jcr:title").getString() );
		 }
		
		 tags.setCategories( categories );
		 tags.setLevels( levels );
		 
	}catch(Exception e){e.printStackTrace();}

	return tags;
}
*/



public SearchTag searchA(){
	SearchTag tags = new SearchTag();
	try{
		
		java.util.Map<String, String> categories = new java.util.TreeMap();
		java.util.Map<String, String> levels = new java.util.TreeMap();
		
		String sql="select jcr:title from nt:base where jcr:path like '/etc/tags/girlscouts/%'";
		javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
		javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL); 
			
		QueryResult result = q.execute();
		System.err.println("SEAch1: "+ (result.getRows().getSize()) );
		 for (RowIterator it = result.getRows(); it.hasNext(); ) {
		       Row r = it.nextRow();
		       System.err.println("Search path : "+ r.getPath());
		       if( r.getPath().startsWith("/etc/tags/girlscouts/categories") )
		    	   categories.put( r.getValue("jcr:title").getString(),null );
		       else if( r.getPath().startsWith("/etc/tags/girlscouts/program-level") )
		    	   levels.put( r.getValue("jcr:title").getString(), null );
		 }
		
		 tags.setCategories( categories );
		 tags.setLevels( levels );
		 tags.setRegion( searchRegion() );
		 
	}catch(Exception e){e.printStackTrace();}

	return tags;
}


public java.util.List<Activity> searchA1(User user, String tags, String cat, String keywrd,
		java.util.Date startDate, java.util.Date endDate, String region){
	
	java.util.List<Activity> toRet= new java.util.ArrayList();
			
	try{
		
		boolean isTag=false;
		
		
		//TAGS= LVL
		String sqlTags="";
		System.err.println("lvl: "+ tags);
		if( tags.equals("|")) tags="";
		
		StringTokenizer t= new StringTokenizer( tags, "|");
		while( t.hasMoreElements()){
			sqlTags+=" contains(parent.[cq:tags], '"+ t.nextToken() +"') ";
			if( t.hasMoreElements() )
				sqlTags+=" or ";
			isTag=true;
		}
		if( isTag)
			sqlTags=" and ("+ sqlTags +" ) ";
		//END LVL
		
		
		//cat
		String sqlCat="";
		System.err.println("cat: "+ cat);
		if( cat.equals("|")) cat="";
		
	    t= new StringTokenizer( cat, "|");
		while( t.hasMoreElements()){
			sqlCat+=" contains(parent.[cq:tags], '"+ t.nextToken() +"') ";
			if( t.hasMoreElements() )
				sqlCat+=" or ";
			isTag=true;
		}
		if( !sqlCat.equals("" ))
			sqlCat=" and ("+ sqlCat +" ) ";
		//end cat
		
		
		
		
		String regionSql="";
		if( region !=null && !region.trim().equals("")) {
			regionSql += " and LOWER(Region) ='"+ region +"'";
			//isTag=true;
		}
		
		String path = "/content/gateway/en/events/2014/%";
		if( !isTag )
			path= path +"/data";
		else
			path= path +"/jcr:content";
		
		//- org SQL String sql="select start, jcr:title, details, end,locationLabel,srchdisp  from nt:base where jcr:path like '"+ path +"' " ;
		String sql= "select child.start, parent.[jcr:title], child.details, child.end,child.locationLabel,child.srchdisp  from [nt:base] as parent INNER JOIN [nt:base] as child ON ISCHILDNODE(child, parent) where  (isdescendantnode (parent, [/content/gateway/en/events/2014])) and child.start is not null and parent.[jcr:title] is not null " ;
		
		//SELECT parent.* FROM [cq:PageContent] AS parent INNER JOIN [nt:base] as child ON ISCHILDNODE(parent) WHERE ISDESCENDANTNODE(parent, [/content/grocerystore/food/])"
		


		
		if( keywrd!=null && !keywrd.trim().equals("") && !isTag )
			sql+=" and contains(child.*, '"+ keywrd+"') ";
		
		if( !isTag )
			sql+= regionSql;
		
		sql+= sqlTags;
		sql+= sqlCat;
		
		/*
		sql="select parent.* from [nt:base] as parent INNER JOIN [nt:base] as child ON ISCHILDNODE(child,parent) where" +
				" parent.[jcr:path] LIKE '/content/gateway/en/events/2014/%' and"+
				" child.Region='test' and  (contains(parent.*, '"+ keywrd+"') or contains(child.*, '"+ keywrd+"')  )";
		
		
		sql="select parent.* from [nt:unstructured] as parent INNER JOIN [nt:unstructured] as child ON ISCHILDNODE(child,parent) where" +
				" parent.[jcr:path] LIKE '/content/gateway/en/events/2014/%'";
				//and"+
				//"    (contains(parent.*, '"+ keywrd+"') or contains(child.*, '"+ keywrd+"')  )";
		
		sql="SELECT * FROM [nt:unstructured] WHERE [jcr:path] = '/content/gateway/en/events/2014/wilderness_first_aid'";
		
		
		sql="SELECT * FROM [nt:unstructured] as x WHERE (PATH() LIKE '/content/gateway/en/events/2014/wilderness_first_aid%')";
		sql="SELECT * FROM [nt:base] WHERE PATH() LIKE '/content/gateway/en/events/2014/wilderness_first_aid%'";
		*/
		
		//sql= "select * from [nt:base] as p where  (isdescendantnode (p, ["+ path +"]))  and contains(p.*, 'aid') ";
		
		System.err.println( sql );
		
		
		
		
		javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
		javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.JCR_SQL2); 
			
		int i=0;
		QueryResult result = q.execute();
	//System.err.println("SEAch main: "+ (result.getRows().getSize()) );
		 for (RowIterator it = result.getRows(); it.hasNext(); ) {
		       Row r = it.nextRow();
		      // System.err.println( r.getPath() );
		       /*
		       Value  values[] = r.getValues();
		       for(int x=0;x<values.length;x++)
		    	   System.err.println( "*** "+ values[x].getString());
		       */
		       
		        Activity activity = new Activity();
				activity.setUid("A"+ new java.util.Date().getTime() +"_"+ Math.random());
		        if( true){//!isTag){
		        	
		        	activity.setContent(r.getValue("child.details").getString());
		        	activity.setDate(r.getValue("child.start").getDate().getTime());
		        	try{ activity.setEndDate(r.getValue("child.end").getDate().getTime()); }catch(Exception e){}
		        	activity.setLocationName(r.getValue("child.locationLabel").getString());
		        	activity.setName(r.getValue("child.srchdisp").getString());
		        	
		        	activity.setName(r.getValue("parent.jcr:title").getString());
		        }else{
		        	activity.setName(r.getValue("jcr:title").getString());
		        	
		        	String sql1 = "select start, jcr:title, details, end,locationLabel,srchdisp  from nt:base where jcr:path ='"+ r.getValue("jcr:path").getString() +"/data' "+regionSql ;
		        	if( keywrd!=null && !keywrd.trim().equals(""))
		    			sql1+=" and contains(*, '"+ keywrd+"') ";
		        	System.err.println("SQL1: "+sql1);
		        	
		        	q = qm.createQuery(sql1, javax.jcr.query.Query.SQL); 
		        	QueryResult result1 = q.execute();
		        	
		        	boolean isFound=false;	
		        	for (RowIterator it1 = result1.getRows(); it1.hasNext(); ) {
		 		       Row r1 = it1.nextRow();
		 		       
		 		        isFound=true;
		 		        activity.setContent(r1.getValue("details").getString());
			        	activity.setDate(r1.getValue("start").getDate().getTime());
			        	try{activity.setEndDate(r1.getValue("end").getDate().getTime());}catch(Exception e){}
			        	activity.setLocationName(r1.getValue("locationLabel").getString());
			        	activity.setName(r1.getValue("srchdisp").getString());
		        	}
		        	if( !isFound ){ System.err.println("exiting.."); continue;  }
		        }
				activity.setType(YearPlanComponentType.ACTIVITY);
				activity.setId("ACT"+i);
				//activity.setPath( r.getPath() );
				
				//patch
			if( activity.getDate()!=null && activity.getEndDate()==null){
				activity.setEndDate(activity.getDate());
			}
				
				
				
				//filter by dates
				
				//System.err.println( startDate +" : "+ endDate);
				//System.err.println(activity.getDate() +" : "+ activity.getEndDate() );
				//System.err.println(activity.getDate().after(startDate )  ) ;
				if( startDate!=null && endDate!=null && 
						 
					      (
					    		  ( activity.getDate()!=null && activity.getDate().before(startDate ) ) ||
							(activity.getEndDate()!=null && activity.getEndDate().after(endDate))
							))
							{ 
								System.err.println("Filter byDate: "+ startDate +" : "+ activity.getDate() +" :: "+ endDate +": "+ activity.getEndDate());
								continue;
								}
				
				
				toRet.add( activity); 
				i++;
		 }
		 
	}catch(Exception e){e.printStackTrace();}
	return toRet;
}



public java.util.Map<String, String> searchRegion(){
	java.util.Map<String, String> container = new java.util.TreeMap();
	try{
		
		java.util.Map<String, String> categories = new java.util.TreeMap();
		java.util.Map<String, String> levels = new java.util.TreeMap();
		
		String sql="select Region from nt:base where jcr:path like '/content/gateway/en/events/2014/%' and Region is not null";
		javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
		javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL); 
			
		QueryResult result = q.execute();
		
		 for (RowIterator it = result.getRows(); it.hasNext(); ) {
		       Row r = it.nextRow();
		       String elem= r.getValue("Region").getString() ;
		       elem= elem.trim().toLowerCase();
		       
		       if( !container.containsKey(elem) )
		    	  container.put( elem, null);
		 }
		
		
		 
	}catch(Exception e){e.printStackTrace();}

	return container;
}




public java.util.List<Meeting> getAllMeetings(String gradeLevel){
	
	java.util.List<Meeting> meetings =null; //new java.util.ArrayList();
	

	
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
        filter.setScope(  "/content/girlscouts-vtk/meetings/myyearplan/"+gradeLevel+"/");
       
        Query query = queryManager.createQuery(filter);
         meetings = (List<Meeting> ) ocm.getObjects(query);
      
         Comparator<Meeting> comp = new BeanComparator("position");
 	    Collections.sort( meetings, comp);
 		
		
		}catch(Exception e){e.printStackTrace();}
	
	
	
	
	
	
	return meetings;

}




public  List<Asset> getAllResources(String _path) {
	  
	
	List<Asset> matched = new ArrayList<Asset>();
	
	try{
		
		
		String sql="";
		
		sql="select [dc:description], [dc:format], [dc:title], [jcr:mimeType], [jcr:path] " +
				" from [nt:unstructured] as parent where " +
				" (isdescendantnode (parent, ["+ _path +"])) and [cq:tags] is not null";
		System.err.println( sql);
		
		javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
		javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.JCR_SQL2); 
   		
		 		
		QueryResult result = q.execute();
   
 
   
   for (RowIterator it = result.getRows(); it.hasNext(); ) {
       Row r = it.nextRow();
      // Value excerpt = r.getValue("jcr:path");
       
       //String path = excerpt.getString();
       //if( path.contains("/jcr:content") ) path= path.substring(0, (path.indexOf("/jcr:content") ));

       //System.err.println("** "+ r.getValue("jcr:path").getString() + " : "+ r.getPath());
      
       
       Asset search = new Asset();
       search.setRefId(r.getPath().replace("/jcr:content/metadata", ""));
       search.setIsCachable(true);
       search.setType(AssetComponentType.RESOURCE);
       try{ search.setDescription( r.getValue("dc:description").getString() );}catch(Exception e){}
       try{ search.setTitle( r.getValue("dc:title").getString() );}catch(Exception e){}
       
       //search.setDocType( r.getValue("jcr:mimeType").getString() );
       try{
    	   String t= r.getPath().substring( r.getPath().indexOf(".") );
    	   t= t.substring(1, t.indexOf("/"));
    	   search.setDocType( t );
       }catch(Exception e){e.printStackTrace();}
       
       
       matched.add(search);
      
   }
   
   
	}catch(Exception e){e.printStackTrace();}
   return matched;
	}

public  Asset getAsset(String _path) {
	  
	Asset search=null;
	
try{
		
		System.err.println("getAsset: "+ _path );
		String sql="";
		
		if( _path!=null && _path.contains("metadata/") )
			_path = _path.replace( "metadata/", "");
		
		sql="select dc:description,dc:format, dc:title from nt:unstructured where jcr:path like '"+ _path +"%' and cq:tags is not null";
		/*
		sql="select dc:description,dc:format, dc:title from nt:unstructured where (isdescendantnode (parent, ["+ _path +"])) " +
				" and [cq:tags] is not null";
		*/
		System.err.println( sql);
		
		javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
		javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL); 
   		
		 		
		QueryResult result = q.execute();
   
 
   System.err.println("test: "+ result.getRows().getSize());
   for (RowIterator it = result.getRows(); it.hasNext(); ) {
       Row r = it.nextRow();
       Value excerpt = r.getValue("jcr:path");
       
       String path = excerpt.getString();
       if( path.contains("/jcr:content") ) path= path.substring(0, (path.indexOf("/jcr:content") ));

        search = new Asset();
       search.setRefId(path);
       search.setIsCachable(true);
       search.setType(AssetComponentType.RESOURCE);
       try{ search.setDescription( r.getValue("dc:description").getString() );}catch(Exception e){}
       try{ search.setTitle( r.getValue("dc:title").getString() );}catch(Exception e){}
       
      
   }
}catch(Exception e){e.printStackTrace();}
return search;
}



}//edn class
