package org.girlscouts.tools.meetingimporter;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import org.apache.sling.api.resource.Resource;
/*
import org.apache.poi.ss.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.*;
*/
import org.girlscouts.vtk.models.Meeting;

public class DAMUpload {
	private static Logger log = LoggerFactory.getLogger(DAMUpload.class);

	public static void main(String[] args) throws Exception {
		DAMUpload me = new DAMUpload();
		//me.parseMeetingPlan();
		me.parseAssetLoad();
		
		//me.damUpload("asdf");
		//me.createEtcTag("test");
	}
	
	private void parseMeetingPlan() throws Exception{
		
		 FileInputStream fis = new FileInputStream("/Users/mike/Desktop/brownie/metadata.xlsx");
         Workbook workbook = WorkbookFactory.create(fis);
         
         FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

         Sheet sheet = workbook.getSheetAt(1);//workbook.getSheet("Meeting Plan data model");
for(int i=2;i<sheet.getLastRowNum();i++){
        String meetingId = getCellVal( evaluator, sheet, "A"+i );
        if( meetingId==null || meetingId.trim().equals(""))
        	break;
        
        //String meetingTitle =getCellVal( evaluator, sheet, "B"+i );
        String meetingName =getCellVal( evaluator, sheet, "B"+i );
        String level = getCellVal( evaluator, sheet, "C"+i );
        //String meetingName= getCellVal( evaluator, sheet, "D"+i );
        String meetingBlurb= getCellVal( evaluator, sheet, "D"+i );
        String cat= getCellVal( evaluator, sheet, "E"+i );
        String aids_tags= getCellVal( evaluator, sheet, "F"+i );
        String resource_tags= getCellVal( evaluator, sheet, "G"+i );
        String agenda=  getCellVal( evaluator, sheet, "H"+i );
        
        
        Meeting meeting = new Meeting();
        meeting.setId(meetingId);
        meeting.setName(meetingName);
        
        
        try{ doJcr(meeting); }catch(Exception e){e.printStackTrace();}
}

	}

	

	public void doJcr(Meeting meeting) throws Exception{
		 
		if(true) return;
	       // Connection
	        javax.jcr.Repository repository = JcrUtils.getRepository("http://localhost:4503/crx/server/");
	        
	        //Workspace Login
	        SimpleCredentials creds = new SimpleCredentials("admin", "admin".toCharArray());
	        Session session = null;
	        session = repository.login(creds, "crx.default");
	        session.getRootNode();
	        
	        
	        List<Class> classes = new ArrayList<Class>();	
	    	classes.add(Meeting.class); 
	    	
	    	Mapper mapper = new AnnotationMapperImpl(classes);
	    	ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
	    	
	    	ocm.insert(meeting);
	    	ocm.save();
	    	
	        
	}

	
	private String getCellVal(FormulaEvaluator evaluator, Sheet sheet, String field){
		
		String toRet="";
		 CellReference ref = new CellReference(field);
		 
         Row r = sheet.getRow(ref.getRow());
         if (r != null) {
            Cell c = r.getCell(ref.getCol());
            CellValue value = evaluator.evaluate( c ) ;
            
            if(value!=null)
            toRet = value.getStringValue() ;
         }
         return toRet.trim();
	}
	
	// 1 row header(s)
	private void parseAssetLoad() throws Exception{
		
		FileInputStream fis = new FileInputStream("/Users/mike/Desktop/brownie/metadata.xlsx");
        Workbook workbook = WorkbookFactory.create(fis);
        
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        Sheet sheet = workbook.getSheetAt(1);

        for(int i=2;i<=sheet.getLastRowNum()+1;i++){
        	String fileName = getCellVal( evaluator, sheet, "A"+i ).trim();
        	if( fileName==null || fileName.length()<1) break;
        	String type = getCellVal( evaluator, sheet, "B"+i );
        	String destination = getCellVal( evaluator, sheet, "C"+i );
        	String meetingId = destination.substring( destination.lastIndexOf("/")+1);
        	
        	java.util.Map metaDatas = new java.util.TreeMap();
        	metaDatas.put("name", getCellVal(evaluator, sheet, "D" + i));
        	metaDatas.put("tags", getCellVal(evaluator, sheet, "E" + i));
        	metaDatas.put("description", getCellVal(evaluator, sheet, "F" + i));
        	metaDatas.put("category", getCellVal(evaluator, sheet, "G" + i));
        	metaDatas.put("duration", getCellVal(evaluator, sheet, "H" + i));


        	/*
        	if( metaData.contains("\n") ){
        		
        	 java.util.StringTokenizer t= new java.util.StringTokenizer( metaData, "\n");
        	 while( t.hasMoreElements()){
        		String elems[] = t.nextToken().split("=");
        		metaDatas.put( elems[0].trim().toLowerCase(), elems[1].trim());
        	 }
            }else{
            	String elems[] = metaData.split("=");
            	metaDatas.put( elems[0].trim().toLowerCase(), elems[1].trim());
            	
            }
            */
        	
        	try{ 
        	    String[] destinations = destination.replaceAll("\\s+", "\n").split("\n");
        	    for (int j = 0; j < destinations.length; j++) {
        	        damUpload("/Users/mike/Desktop/brownie/assets/", fileName, metaDatas, destinations[j], type);
        	    }
        	}catch(Exception e){e.printStackTrace();}
        	
        }

	}
	
	/*
	public void create(String fileName)throws Exception{
		
		
		String url="http://localhost:4503/content/dam/girlscouts-vtk/global/*";
		
		HttpClient client = new DefaultHttpClient();
		//client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		 
		HttpPost        post   = new HttpPost( url );
		
		post.addHeader("Content-type", "multipart/form-data"); 
		MultipartEntity entity = new MultipartEntity( );//HttpMultipartMode.BROWSER_COMPATIBLE );
		 
		// For File parameters
		entity.addPart( "./jcr:content/renditions/original", new FileBody((( File ) new File(fileName) ),
				"application/octect-stream" ));
		 
		// For usual String parameters
		entity.addPart( "./jcr:primaryType", new StringBody( "dam:Asset", "text/plain",
		                                           Charset.forName( "UTF-8" )));
		
		entity.addPart( "./jcr:content/jcr:primaryType", new StringBody( "dam:AssetContent", "text/plain",
                Charset.forName( "UTF-8" )));
		
		entity.addPart( "./jcr:content/renditions/original@TypeHint", new StringBody( "nt:file", "text/plain",
                Charset.forName( "UTF-8" )));
		
		entity.addPart( "./jcr:content/metadata/jcr:primaryType", new StringBody( "nt:unstructured", "text/plain",
                Charset.forName( "UTF-8" )));
		
		entity.addPart( "./jcr:content/metadata/dc:title", new StringBody( "tester", "text/plain",
                Charset.forName( "UTF-8" )));
		 
		post.setEntity( entity );
		 
		
		HttpResponse response = client.execute(post) ;
		
		client.getConnectionManager().shutdown();
		
		
		
		
	}
	*/
	public void storeResource( Resource resource ) throws Exception{
		 
	
	        javax.jcr.Repository repository = JcrUtils.getRepository("http://localhost:4503/crx/server/");
	        
	        //Workspace Login
	        SimpleCredentials creds = new SimpleCredentials("admin", "admin".toCharArray());
	        Session session = null;
	        session = repository.login(creds, "crx.default");
	        session.getRootNode();
	        
	        List<Class> classes = new ArrayList<Class>();	
	    	classes.add(Resource.class); 
	    	classes.add(CollectionHoldString.class);
	    	
	    	Mapper mapper = new AnnotationMapperImpl(classes);
	    	ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper);	
	 
	    	if( ocm.objectExists( resource.getPath() ) )
	    		ocm.update(resource);
	    	else{
	    		
	    		ocm.insert(resource);
	    	}
	    	ocm.save();
	    	
	    	
	        
	}
	
	private void damUpload(String path, String fileName, java.util.Map metaDatas, String destination, String type) {
		
	
		
		try{
		    
		String uploadFileName = fileName.replaceAll(" ", "-");
		
		HttpClient httpclient = new DefaultHttpClient();
			
			
	    httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

	    
	    HttpPost httppost = null;
	    if( destination.toLowerCase().trim().contains("/global") ) {
	    	httppost = new HttpPost( "http://localhost:4503/content/dam/girlscouts-vtk/global/"+ type.toLowerCase().trim() +"/"+java.net.URLEncoder.encode(uploadFileName));
	    } else {
	        destination = destination.trim();
	        String[] destinationParts = destination.split("/");
	        if (destinationParts.length == 3) {
	            destination = "/" + destinationParts[1].toLowerCase() + "/" + destinationParts[2].toUpperCase();
	        }
	        if (type.toLowerCase().trim().equals("icon")) {
	            httppost = new HttpPost( "http://localhost:4503/content/dam/girlscouts-vtk/local/"+ type.toLowerCase().trim() +""+ destination +".png");
	        } else {
	            httppost = new HttpPost( "http://localhost:4503/content/dam/girlscouts-vtk/local/"+ type.toLowerCase().trim() +""+ destination +"/"+java.net.URLEncoder.encode(uploadFileName));
	        }
	    }
	    
	    String basic_auth = new String(Base64.encodeBase64(( "admin:admin" ).getBytes()));
	    httppost.addHeader("Authorization", "Basic " + basic_auth);

	    
	    File file = new File(path+ fileName);
	    if (!file.exists()) {
	    	log.error(fileName + " NOT FOUND.");
	        return;
	    }

	    MultipartEntity entity = new MultipartEntity();
	    ContentBody cbFile = new FileBody(file);//, "image/jpeg");
	    entity.addPart("./jcr:content/renditions/original", cbFile);


	    entity.addPart( "./jcr:primaryType", new StringBody( "dam:Asset", "text/plain",
                Charset.forName( "UTF-8" )));

entity.addPart( "./jcr:content/jcr:primaryType", new StringBody( "dam:AssetContent", "text/plain",
Charset.forName( "UTF-8" )));

entity.addPart( "./jcr:content/renditions/original@TypeHint", new StringBody( "nt:file", "text/plain", Charset.forName( "UTF-8" )));
entity.addPart( "./jcr:content/metadata/jcr:primaryType", new StringBody( "nt:unstructured", "text/plain",Charset.forName( "UTF-8" )));
entity.addPart( "./jcr:content/metadata/jcr:mixinTypes", new StringBody( "cq:Taggable", "text/plain",Charset.forName( "UTF-8" )));

entity.addPart( "./jcr:content/renditions/jcr:primaryType", new StringBody( "nt:folder", "text/plain",Charset.forName( "UTF-8" )));

entity.addPart( "./jcr:content/metadata/dc:title", new StringBody( ((String)metaDatas.get("name")).trim(), "text/plain",
Charset.forName( "UTF-8" )));

if( metaDatas.get("description")!=null )
	entity.addPart( "./jcr:content/metadata/dc:description", new StringBody( (String)metaDatas.get("description"), "text/plain",
			Charset.forName( "UTF-8" )));

if (!((String)metaDatas.get("tags")).isEmpty()) {
    entity.addPart( "./jcr:content/metadata/cq:tags", new StringBody( "girlscouts-vtk:tag/"+((String)metaDatas.get("tags")).trim().toLowerCase().replaceAll(" ", "-"), "text/plain",
    Charset.forName( "UTF-8" )));
}
if (!((String)metaDatas.get("category")).isEmpty()) {
    entity.addPart( "./jcr:content/metadata/cq:tags", new StringBody( "girlscouts-vtk:category/"+((String)metaDatas.get("category")).trim().toLowerCase().replaceAll(" ", "-"), "text/plain",
    Charset.forName( "UTF-8" )));
}
	    


if ((String)metaDatas.get("tags")!=null )
    createEtcTag((String)metaDatas.get("tags"));
	    
	    httppost.setEntity(entity);
	    HttpResponse response = httpclient.execute(httppost);
	    HttpEntity resEntity = response.getEntity();

	    if (resEntity != null) {
	      resEntity.consumeContent();
	    }

	    httpclient.getConnectionManager().shutdown();
		}catch(Exception e){e.printStackTrace();}
		
		log.info(fileName + " uploaded successfully.");
	}
	
	
	private void createEtcTag( String tag )throws Exception{
		
		String dir = "/etc/tags/girlscouts-vtk/tag/";
		
		        javax.jcr.Repository repository = JcrUtils.getRepository("http://localhost:4503/crx/server/");
		        
		        //Workspace Login
		        SimpleCredentials creds = new SimpleCredentials("admin", "admin".toCharArray());
		        Session session = null;
		        session = repository.login(creds, "crx.default");
		        Node root = session.getRootNode();
		        
		        
		        if( !session.nodeExists(dir+tag.toLowerCase().trim().replace(" ", "-")) ){
		        	
		        	Node assets = session.getNode(dir);
		        	
		        	//create tag
		        	Node resNode = assets.addNode (tag.toLowerCase().trim().replace(" ", "-"), "cq:Tag");
		        	resNode.setProperty("jcr:title", tag);
		        	resNode.setProperty("sling:resourceType", "cq/tagging/components/tag");
		        	
		        	session.save();
		        }        
		
	}
	
}
