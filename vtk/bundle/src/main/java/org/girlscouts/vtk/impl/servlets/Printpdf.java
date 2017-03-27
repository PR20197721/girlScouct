package org.girlscouts.vtk.impl.servlets;
import java.io.IOException;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;
import java.io.File;
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
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.ejb.YearPlanUtil;
import com.day.cq.commons.jcr.JcrUtil;



@Component(metatype = true, immediate = true)
@Service
@Properties({
	@Property(propertyPrivate = true, name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
	@Property(propertyPrivate = true, name = "sling.servlet.extensions", value = "pdf"),
	@Property(propertyPrivate = true, name = "sling.servlet.methods", value = "GET"),
        @Property(name="label", value="Girl Scouts VTK Print PDF Servlet"),
        @Property(name="description", value="Girl Scouts VTK Print PDF Servlet")
})
public class Printpdf extends SlingSafeMethodsServlet {

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServerException,
			IOException {
		
			try {
				String reqURI = request.getRequestURI();
				if(reqURI.startsWith("/content/girlscouts-vtk")){
					response.setHeader("Content-Disposition", "attachment;filename=\"print.pdf\"");
					response.setContentType("application/pdf");
					ServletOutputStream fout = response.getOutputStream();
					fout.print("test123");
					fout.flush();
				}
				
				else{
					response.setStatus(404);
					response.setHeader("Connection", "close");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

}
