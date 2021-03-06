package org.girlscouts.tools.meetingimporter;

/*************************************************************
 * IMPORTANT: There is a known issue for this uploader.
 * When you upload to the authoring, for some assets,
 * the "update asset" workflow will try to create "dc:title"
 * as String[] instead of String. 
 * 
 * SOLUTION: load the asset twice
 * In the first round, load all the binaries.
 * In the second round, correct the metadata.
**************************************************************/

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

public class DocumentUpload {
    private int round = 2;
	
	public static void main(String[] args) throws Exception {
		DocumentUpload me = new DocumentUpload();
		//me.parseMeetingPlan();
		for (; me.round <= 2; me.round++) {
		    me.parseDocuments();
		    me.round++;
		}
		
		//me.damUpload("asdf");
		//me.createEtcTag("test");
	}
	
	public void doJcr(Meeting meeting) throws Exception{
		 
	       // Connection
	        javax.jcr.Repository repository = JcrUtils.getRepository("http://localhost:4502/crx/server/");
	        
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
         
         // Skip mdash
         // TODO: use some library to skip all of them
         toRet = toRet.replaceAll("—", "&mdash;");
         
         return toRet.trim();
	}
	
	// 1 row header(s)
	private void parseDocuments() throws Exception{
		
		FileInputStream fis = new FileInputStream("/Users/mike/Desktop/documents/documents.xlsx");
        Workbook workbook = WorkbookFactory.create(fis);
        
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        Sheet sheet = workbook.getSheetAt(0);

        for(int i=2;i<=sheet.getLastRowNum()+1;i++){
        	String fileName = getCellVal( evaluator, sheet, "A"+i ).trim();
        	if( fileName==null || fileName.length()<1) break;
        	java.util.Map metaDatas = new java.util.TreeMap();
        	metaDatas.put("name", getCellVal(evaluator, sheet, "B" + i));
        	metaDatas.put("tags", getCellVal(evaluator, sheet, "C" + i));
        	metaDatas.put("category", getCellVal(evaluator, sheet, "D" + i));
        	metaDatas.put("description", getCellVal(evaluator, sheet, "E" + i));

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
        	    documentUpload("/Users/mike/Desktop/documents/documents/", fileName, metaDatas);
        	}catch(Exception e){e.printStackTrace();}
        	
        }

	}
	
	public void storeResource( Resource resource ) throws Exception{
		 
	
	        javax.jcr.Repository repository = JcrUtils.getRepository("http://localhost:4502/crx/server/");
	        
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
	
	private void documentUpload(String path, String fileName, java.util.Map metaDatas) {
		
	    if (fileName.startsWith("/")) {
	        return;
	    }
		
		try{
		    
		String uploadFileName = fileName.replaceAll(" ", "-");
		
		HttpClient httpclient = new DefaultHttpClient();
			
			
	    httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

	    HttpPost httppost = new HttpPost( "http://localhost:4502/content/dam/nc-coastal-pines-images-/forms-and-documents-/" + fileName);
	    
	    String basic_auth = new String(Base64.encodeBase64(( "admin:admin" ).getBytes()));
	    httppost.addHeader("Authorization", "Basic " + basic_auth);

	    File file = new File(path+ fileName);
	    if (!file.exists()) {
	        return;
	    }

	    MultipartEntity entity = new MultipartEntity();
	    ContentBody cbFile = new FileBody(file);//, "image/jpeg");
	    if (round == 1) {
	        entity.addPart("./jcr:content/renditions/original", cbFile);
	    }

	    entity.addPart( "./jcr:primaryType", new StringBody( "dam:Asset", "text/plain",
                Charset.forName( "UTF-8" )));

entity.addPart( "./jcr:content/jcr:primaryType", new StringBody( "dam:AssetContent", "text/plain",
Charset.forName( "UTF-8" )));

if (round == 1) {
    entity.addPart( "./jcr:content/renditions/original@TypeHint", new StringBody( "nt:file", "text/plain", Charset.forName( "UTF-8" )));
}
entity.addPart( "./jcr:content/metadata/jcr:primaryType", new StringBody( "nt:unstructured", "text/plain",Charset.forName( "UTF-8" )));
entity.addPart( "./jcr:content/metadata/jcr:mixinTypes", new StringBody( "cq:Taggable", "text/plain",Charset.forName( "UTF-8" )));

entity.addPart( "./jcr:content/renditions/jcr:primaryType", new StringBody( "nt:folder", "text/plain",Charset.forName( "UTF-8" )));

entity.addPart( "./jcr:content/metadata/dc:title", new StringBody( ((String)metaDatas.get("name")).trim(), "text/plain", Charset.forName( "UTF-8" )));
entity.addPart( "./jcr:content/metadata/jcr:title", new StringBody( ((String)metaDatas.get("name")).trim(), "text/plain", Charset.forName( "UTF-8" )));

if( metaDatas.get("description")!=null )
	entity.addPart( "./jcr:content/metadata/dc:description", new StringBody( (String)metaDatas.get("description"), "text/plain",
			Charset.forName( "UTF-8" )));

if (!((String)metaDatas.get("tags")).isEmpty()) {
    String[] tags = ((String)metaDatas.get("tags")).split(",");
    for (String tag : tags) {
        tag = tag.trim();
        entity.addPart( "./jcr:content/metadata/cq:tags", new StringBody( "girlscoutsnccp:forms_documents/"+tag.trim().toLowerCase().replaceAll(" ", "-").replaceAll(",", "").replaceAll("/", ""), "text/plain",
        Charset.forName( "UTF-8" )));
    }
}
if (!((String)metaDatas.get("category")).isEmpty()) {
    String[] categories = ((String)metaDatas.get("category")).split(",");
    for (String category : categories) {
        category = category.trim();
        entity.addPart( "./jcr:content/metadata/cq:tags", new StringBody( "girlscoutsnccp:forms_documents/"+category.trim().toLowerCase().replaceAll(" ", "-").replaceAll(",", "").replaceAll("/", ""), "text/plain",
                Charset.forName( "UTF-8" )));
    }
}

entity.addPart( "./jcr:content/metadata/cq:tags@TypeHint", new StringBody( "String[]", "text/plain", Charset.forName( "UTF-8" )));
	    


if ((String)metaDatas.get("category")!=null )
    createEtcTag((String)metaDatas.get("category"));
	    
	    httppost.setEntity(entity);
	    HttpResponse response = httpclient.execute(httppost);
	    HttpEntity resEntity = response.getEntity();

	    if (resEntity != null) {
	      resEntity.consumeContent();
	    }

	    httpclient.getConnectionManager().shutdown();
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	private void createEtcTag( String tagStr )throws Exception{
		
		String dir = "/etc/tags/girlscoutsnccp/forms_documents/";
		
		        javax.jcr.Repository repository = JcrUtils.getRepository("http://localhost:4502/crx/server/");
		        
		        //Workspace Login
		        SimpleCredentials creds = new SimpleCredentials("admin", "admin".toCharArray());
		        Session session = null;
		        session = repository.login(creds, "crx.default");
		        Node root = session.getRootNode();
		        
		        String[] tags = tagStr.split(",");
		        for (String tag :tags) {
		            tag.trim();
    		        if( !session.nodeExists(dir+tag.toLowerCase().trim().replace(" ", "-").replaceAll(",", "").replaceAll("/", "")) ){
    		        	
    		        	Node assets = session.getNode(dir);
    		        	
    		        	//create tag
    		        	Node resNode = assets.addNode (tag.toLowerCase().trim().replace(" ", "-").replaceAll(",", "").replaceAll("/", ""));
    		        	resNode.setProperty("jcr:title", tag);
    		        	
    		        	session.save();
    		        }        
		        }
	}
	
}
