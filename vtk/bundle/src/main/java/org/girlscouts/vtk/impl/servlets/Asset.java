package org.girlscouts.vtk.impl.servlets;



import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFactory;
//import javax.mail.Part;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import javax.servlet.http.*;



import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.rmi.ServerException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.util.Base64;

import org.apache.sling.api.resource.ResourceResolverFactory;

import com.day.cq.commons.jcr.JcrUtil;


/*
@SlingServlet(
   resourceTypes = "sling/servlet/default",
   selectors = "hello",
   extensions = "js",
   methods = "GET"
)
*/

@Component(
		    label="vtk upload assets",
		    description="vtk upload assets",
		    metatype=true, 
		    immediate=true
		)
		@Service
		@Properties ({
		    @Property(propertyPrivate=true, name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
		    @Property(propertyPrivate=true, name = "sling.servlet.selectors", value = "asset"),
		    @Property(propertyPrivate=true, name = "sling.servlet.extensions", value = "html"),
		    @Property(propertyPrivate=true, name = "sling.servlet.methods", value = "POST")
		})


		public class Asset extends SlingAllMethodsServlet {

	 
		 
	@Reference
	private ResourceResolverFactory resolverFactory; 
	 
		 
		
		    
		    
		    @Override
		     protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServerException, IOException {
		    	//System.err.println("00-1");
		    	
		    	  ResourceResolver resourceResolver = null;
		      try {
		      final boolean isMultipart = org.apache.commons.fileupload.servlet.ServletFileUpload.isMultipartContent(request);
		
		      PrintWriter out = null;
		      
		        out = response.getWriter();
		        
		       // System.err.println("11");
		        
		        if (isMultipart) {
		        	
		        //System.err.println("22");	
		          final java.util.Map<String, org.apache.sling.api.request.RequestParameter[]> params = request.getRequestParameterMap();
		          
		        //  System.err.println("33 "+ params.entrySet().size() );
		        ///  System.err.println("33.1: "+ request.getParameter("aType"));
		          for (final java.util.Map.Entry<String, org.apache.sling.api.request.RequestParameter[]> pairs : params.entrySet()) {
		         
		        	//System.err.println("4422");
		        	
		        	
		        	final String k = pairs.getKey();
		            final org.apache.sling.api.request.RequestParameter[] pArr = pairs.getValue();
		            final org.apache.sling.api.request.RequestParameter param = pArr[0];
		            final InputStream stream = param.getInputStream();
		            if (param.isFormField()) {
		            	
		            	String t=   org.apache.commons.fileupload.util.Streams.asString(stream);
		             // System.err.println("Form field " + k + " with value "+ t);//org.apache.commons.fileupload.util.Streams.asString(stream) + " detected.");
		          
		            
		            if( k.equals("custasset") ){
		            	
		            	
		            	
		            	/*
		            	 byte[] caca=null;
		            	InputStream in = null;
		                FileOutputStream fos = null;
		                try {
		                    HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper(request);
		                    InputStream is = wrappedRequest.getInputStream();
		                    java.io.StringWriter writer = new java.io.StringWriter();
		                    IOUtils.copy(is, writer, "UTF-8");
		                    String imageString = writer.toString();
		                    imageString = imageString.substring("data:image/png;base64,"
		                            .length());
		                    byte[] contentData = imageString.getBytes();
		                   caca = Base64.decodeBase64(contentData);
		                   
		                    String imgName = ReloadableProps
		                            .getProperty("local.image.save.path")
		                            + String.valueOf(System.currentTimeMillis()) + ".png";
		                    fos = new FileOutputStream(imgName);
		                    fos.write(decodedData);
		                
		               
		                } catch (Exception e) {
		                    e.printStackTrace();
		                    String loggerMessage = "Upload image failed : ";
		                    //CVAException.printException(loggerMessage + e.getMessage());
		                } finally {
		                    if (in != null) {
		                        in.close();
		                    }
		                    if (fos != null) {
		                        fos.close();
		                    }
		                }
		            	*/
		            	
		            //	System.err.println("test: "+ t );
		            	//byte[] caca = t.getBytes(Charset.forName("UTF-8"));
		            	
		               // java.util.BASE64Decoder de=new BASE64Decoder();
		             
		               byte[] caca= Base64.decodeBase64(t);
		//System.err.println("BYTSIZE: "+caca.length);
		               InputStream inn = new ByteArrayInputStream(caca);
		                
		
		                resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);            
			              Session session = resourceResolver.adaptTo(Session.class);            
			              reverseReplicateBinary(session, request.getParameter("loc"), request.getParameter("id"),            
			                      inn,
			                      request.getParameter("assetDesc"),  request.getParameter("owner") ,  request.getParameter("id")); 
			          
		                
		            }
		              
		            
		            } else {
		             // System.err.println("File field " + k + " with file name " + param.getFileName() + " detected.");
		              
		              //OutputStream os = new FileOutputStream("/vtk/");
		              //org.apache.commons.net.io.Util.copyStream(stream, os);
		              
		              resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);            
		              Session session = resourceResolver.adaptTo(Session.class);  
		              
		              String loc =request.getParameter("loc");
		              String name=request.getParameter("id");
		              if( request.getParameter("newvalue")!=null){
		            	  loc= "/content/dam/girlscouts-vtk/local/icon/meetings";
		            	  name=name+".png";
		            	  if( request.getParameter("newvalue")!=null){ 
		            		  //out.println("<img src=\""+ loc +"/"+ name +"\"/>");
		            		 // out.println("<script>location.reload();</script>");
		            	   }
		              }
		              
		              reverseReplicateBinary(session, loc, name,            
		                      stream,
		                      request.getParameter("assetDesc"),  request.getParameter("owner") ,  request.getParameter("id")); 
		          
		            }
		            
		             
		          }
		        }else{
		        	
		        	
		        	
		        	 final java.util.Map<String, org.apache.sling.api.request.RequestParameter[]> params = request.getRequestParameterMap();
			         
		        	for (final java.util.Map.Entry<String, org.apache.sling.api.request.RequestParameter[]> pairs : params.entrySet()) {
				         
			        	//System.err.println("4466");
			        	
			        	
			        	final String k = pairs.getKey();
			            final org.apache.sling.api.request.RequestParameter[] pArr = pairs.getValue();
			        
		        	//System.err.println("Not multipart...");
		        	 final org.apache.sling.api.request.RequestParameter param = pArr[0];
		        	 final InputStream stream = param.getInputStream();
		        	 if (param.isFormField()) {
			              //System.err.println("Form field " + k + " with value " + org.apache.commons.fileupload.util.Streams.asString(stream) + " detected.");
			            } else {
			              //System.err.println("File field " + k + " with file name " + param.getFileName() + " detected.");
			            
			              resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);            
			              Session session = resourceResolver.adaptTo(Session.class);            
			              reverseReplicateBinary(session, request.getParameter("loc"), request.getParameter("id"),            
		                      stream,
		                  request.getParameter("assetDesc"),  request.getParameter("owner") ,  request.getParameter("id")); 
			            }
			            }
		        	
		        	
		        	
		        	
		        	
		              
		        }
		      }
		      
		         catch (Exception e) {
		             e.printStackTrace();
		         }
		      
		      
		      //response.sendRedirect( "/content/girlscouts-vtk/en/vtk.planView.html?elem="+ request.getParameter("me"));
		    
		      
		      
//System.err.println("Pathddd:"+		      request.getParameter("loc"));
			if( request.getParameter("loc")!=null && request.getParameter("loc").contains("/tmp/import/assets") )
		      response.sendRedirect("http://localhost:4503/content/girlscouts-vtk/en/vtk.admin.previewImportMeeting.html?id="+ request.getParameter("id"));
		 
		    
		    }
		    
		    
		    private void reverseReplicateBinary(Session session, String parentPath, String name, InputStream is,
		    			String desc, String owner, String id)
		            throws RepositoryException {        
		            ValueFactory valueFactory = session.getValueFactory();        
		            //Node parent = session.getNode(parentPath); 
		         //  Node page = JcrUtils.getOrCreateUniqueByPath(parent, name, "cq:Page");
		            //Node page = JcrUtil.createPath(parentPath, "nt:unstructured", session);
		            
		            //System.err.println("TEST: "+ parentPath);
		            
		            Node page = JcrUtil.createPath(parentPath, "nt:unstructured", "nt:unstructured", session, true);
		           // System.err.println("___PAGE: "+ (page==null) +" : "+ (name==null) +" :" +name);
		            //Node jcrContent = page.addNode("jcr:content", "cq:PageContent");   
		            
		            
		         //  System.err.println( "Isnode: "+page.isNodeType( name ) );
		            
		            
		            Node file =null;
		           if( page.hasNode(name) )
		        	   file =page.getNode(name);
		           else{
		           
		            
		            	try{ 
		            		file= page.addNode(name, "nt:file");  
		            
		            	}catch(javax.jcr.ItemExistsException ex){
		            		file =page.getNode(name);
		            	ex.printStackTrace();
		            	}
		           }
		           
		            //file.setProperty("owner", owner);
		            //file.setProperty("description", desc);
		            //file.setProperty("id", id);
		           // file.setProperty("createTime", new java.util.Date()+"");
		            
		            
		            
		            Node resource = null;
		            if( file.hasNode("jcr:content")) {
		            	resource = file.getNode("jcr:content") ;
		            	resource.remove();
		            }
		            resource =file.addNode("jcr:content", "nt:resource");  
		            
		            resource.setProperty("jcr:data", valueFactory.createBinary(is)); 
		           // resource.setProperty("jcr:mimeType", "application/octet-stream");
		            
		            session.save(); 
		            //jcrContent.setProperty("cq:lastModified", Calendar.getInstance());        
		           // jcrContent.setProperty("cq:lastModifiedBy", session.getUserID());        
		            //jcrContent.setProperty("cq:distribute", false);        
		            session.save();        
		        }    
		 
	}
		 
	

