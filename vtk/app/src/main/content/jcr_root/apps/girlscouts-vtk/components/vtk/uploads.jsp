
<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@ page import="org.apache.commons.fileupload.*" %>
<%@ page import="org.apache.commons.fileupload.disk.*" %>
<%@ page import="org.apache.commons.fileupload.servlet.*" %>
<%@ page import="org.apache.commons.io.output.*" %>

<%
   File file ;
   int maxFileSize = 5000 * 1024;
   int maxMemSize = 5000 * 1024;
   ServletContext context = pageContext.getServletContext();
   String filePath = context.getInitParameter("file-upload");

   // Verify the content type
   String contentType = request.getContentType();
   if ((contentType.indexOf("multipart/form-data") >= 0)) {

      DiskFileItemFactory factory = new DiskFileItemFactory();
      // maximum size that will be stored in memory
      factory.setSizeThreshold(maxMemSize);
      // Location to save data that is larger than maxMemSize.
      factory.setRepository(new File("c:\\temp"));

      // Create a new file upload handler
      ServletFileUpload upload = new ServletFileUpload(factory);
      // maximum file size to be uploaded.
      upload.setSizeMax( maxFileSize );
      try{ 
         // Parse the request to get file items.
         List fileItems = upload.parseRequest(request);

         // Process the uploaded file items
         Iterator i = fileItems.iterator();

         out.println("<html>");
         out.println("<head>");
         out.println("<title>JSP File upload</title>");  
         out.println("</head>");
         out.println("<body>");
         while ( i.hasNext () ) 
         {
            FileItem fi = (FileItem)i.next();
            if ( !fi.isFormField () )	
            {
            // Get the uploaded file parameters
            String fieldName = fi.getFieldName();
            String fileName = fi.getName();
            boolean isInMemory = fi.isInMemory();
            long sizeInBytes = fi.getSize();
            // Write the file
            if( fileName.lastIndexOf("\\") >= 0 ){
            file = new File( filePath + 
            fileName.substring( fileName.lastIndexOf("\\"))) ;
            }else{
            file = new File( filePath + 
            fileName.substring(fileName.lastIndexOf("\\")+1)) ;
            }
            fi.write( file ) ;
            out.println("Uploaded Filename: " + filePath + 
            fileName + "<br>");
            }
         }
         out.println("</body>");
         out.println("</html>");
      }catch(Exception ex) {
         ex.printStackTrace();
      }
   }else{
      out.println("<html>");
      out.println("<head>");
      out.println("<title>Servlet upload</title>");  
      out.println("</head>");
      out.println("<body>");
      out.println("<p>No file uploaded</p>"); 
      out.println("</body>");
      out.println("</html>");
   }
%>

<%! 
/*
private void damUpload(String path, String fileName, java.util.Map metaDatas, String destination, String type) {
		
	
		
		try{
			
		HttpClient httpclient = new DefaultHttpClient();
			
			
	    httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

	    
	    HttpPost httppost = null;
	    if( destination.toLowerCase().trim().contains("/global") )
	    	httppost = new HttpPost( "http://localhost:4503/content/dam/girlscouts-vtk/global/"+ type.toLowerCase().trim() +"/"+java.net.URLEncoder.encode(fileName));
	    else
	    	httppost = new HttpPost( "http://localhost:4503/content/dam/girlscouts-vtk/local/"+ type.toLowerCase().trim() +""+ destination+"/"+java.net.URLEncoder.encode(fileName));
	    
	    String basic_auth = new String(Base64.encodeBase64(( "admin:admin" ).getBytes()));
	    httppost.addHeader("Authorization", "Basic " + basic_auth);

	    
	    File file = new File(path+ fileName);

	    MultipartEntity entity = new MultipartEntity();
	    ContentBody cbFile = new FileBody(file);//, "image/jpeg");
	    entity.addPart("./jcr:content/renditions/original", cbFile);


	    entity.addPart( "./jcr:primaryType", new StringBody( "dam:Asset", "text/plain",
                Charset.forName( "UTF-8" )));

entity.addPart( "./jcr:content/jcr:primaryType", new StringBody( "dam:AssetContent", "text/plain",
Charset.forName( "UTF-8" )));

entity.addPart( "./jcr:content/renditions/original@TypeHint", new StringBody( "nt:file", "text/plain", Charset.forName( "UTF-8" )));
entity.addPart( "./jcr:content/metadata/jcr:primaryType", new StringBody( "nt:unstructured", "text/plain",Charset.forName( "UTF-8" )));

entity.addPart( "./jcr:content/renditions/jcr:primaryType", new StringBody( "nt:folder", "text/plain",Charset.forName( "UTF-8" )));

entity.addPart( "./jcr:content/metadata/dc:title", new StringBody( (String)metaDatas.get("name"), "text/plain",
Charset.forName( "UTF-8" )));

if( metaDatas.get("description")!=null )
	entity.addPart( "./jcr:content/metadata/dc:description", new StringBody( (String)metaDatas.get("description"), "text/plain",
			Charset.forName( "UTF-8" )));

entity.addPart( "./jcr:content/metadata/cq:tags", new StringBody( "girlscouts-vtk:asset/"+(String)metaDatas.get("tags"), "text/plain",
Charset.forName( "UTF-8" )));
	    


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
	}
}


private void createEtcTag( String tag )throws Exception{
	
	String dir = "/etc/tags/girlscouts-vtk/asset/";
	
	        javax.jcr.Repository repository = JcrUtils.getRepository("http://localhost:4503/crx/server/");
	        
	        //Workspace Login
	        SimpleCredentials creds = new SimpleCredentials("admin", "admin".toCharArray());
	        Session session = null;
	        session = repository.login(creds, "crx.default");
	        Node root = session.getRootNode();
	        
	        
	        if( !session.nodeExists(dir+tag.toLowerCase().trim().replace(" ", "-")) ){
	        	
	        	Node assets = session.getNode(dir);
	        	
	        	//create tag
	        	Node resNode = assets.addNode (tag.toLowerCase().trim().replace(" ", "-"));
	        	resNode.setProperty("jcr:title", tag);
	        	
	        	session.save();
	        }        
	
}
*/
	%>
