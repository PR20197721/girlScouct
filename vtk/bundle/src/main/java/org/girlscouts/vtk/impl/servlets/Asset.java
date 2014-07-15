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
		       
		    	
		    	System.err.println("Asset servlet");
		    	
		    	  ResourceResolver resourceResolver = null;
		      try
		      {
		      final boolean isMultipart = org.apache.commons.fileupload.servlet.ServletFileUpload.isMultipartContent(request);
		
		  System.err.println("IsMultpart :"+isMultipart );    
		      PrintWriter out = null;
		      
		        out = response.getWriter();
		        if (isMultipart) {
		          final java.util.Map<String, org.apache.sling.api.request.RequestParameter[]> params = request.getRequestParameterMap();
		          for (final java.util.Map.Entry<String, org.apache.sling.api.request.RequestParameter[]> pairs : params.entrySet()) {
		            final String k = pairs.getKey();
		            final org.apache.sling.api.request.RequestParameter[] pArr = pairs.getValue();
		            final org.apache.sling.api.request.RequestParameter param = pArr[0];
		            final InputStream stream = param.getInputStream();
		            if (param.isFormField()) {
		              out.println("Form field " + k + " with value " + org.apache.commons.fileupload.util.Streams.asString(stream) + " detected.");
		            } else {
		              out.println("File field " + k + " with file name " + param.getFileName() + " detected.");
		              
		              //OutputStream os = new FileOutputStream("/vtk/");
		              //org.apache.commons.net.io.Util.copyStream(stream, os);
		              
		              resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);            
		              Session session = resourceResolver.adaptTo(Session.class);            
		              reverseReplicateBinary(session, request.getParameter("loc"), request.getParameter("id"),            
		                      stream,
		                      request.getParameter("assetDesc"),  request.getParameter("owner") ,  request.getParameter("id")); 
		            }
		          }
		        }else{
		        	out.println("Not multipart...");
		        }
		      }
		      
		         catch (Exception e) {
		             e.printStackTrace();
		         }
		      
		      
		      response.sendRedirect( "/content/girlscouts-vtk/en/vtk.planView.html?elem="+ request.getParameter("me"));
	
		    System.err.println("dne asset");
		    }
		    
		    
		    private void reverseReplicateBinary(Session session, String parentPath, String name, InputStream is,
		    			String desc, String owner, String id)
		            throws RepositoryException {        
		            ValueFactory valueFactory = session.getValueFactory();        
		            //Node parent = session.getNode(parentPath); 
		         //  Node page = JcrUtils.getOrCreateUniqueByPath(parent, name, "cq:Page");
		            //Node page = JcrUtil.createPath(parentPath, "nt:unstructured", session);
		            
		            System.err.println("TEST: "+ parentPath);
		            
		            Node page = JcrUtil.createPath(parentPath, "nt:unstructured", "nt:unstructured", session, true);
		            
		            //Node jcrContent = page.addNode("jcr:content", "cq:PageContent");        
		            Node file = page.addNode(name, "nt:file");  
		            //file.setProperty("owner", owner);
		            //file.setProperty("description", desc);
		            //file.setProperty("id", id);
		           // file.setProperty("createTime", new java.util.Date()+"");
		            
		            
		            Node resource = file.addNode("jcr:content", "nt:resource");        
		            resource.setProperty("jcr:data", valueFactory.createBinary(is)); 
		            
		            session.save(); 
		            //jcrContent.setProperty("cq:lastModified", Calendar.getInstance());        
		           // jcrContent.setProperty("cq:lastModifiedBy", session.getUserID());        
		            //jcrContent.setProperty("cq:distribute", false);        
		            session.save();        
		        }    
		 
	}
		 
	

