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
import javax.servlet.ServletOutputStream;
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
import org.apache.sling.api.resource.LoginException;
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
//import org.girlscouts.vtk.auth.models.User;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.ejb.YearPlanUtil;

import com.day.cq.commons.jcr.JcrUtil;

/*
 @SlingServlet(
 resourceTypes = "sling/servlet/default",
 selectors = "hello",
 extensions = "js",
 methods = "GET"
 )
 */

@Component(label = "vtk upload tet", description = "vtk upload test", metatype = true, immediate = true)
@Service
@Properties({
		@Property(propertyPrivate = true, name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
		@Property(propertyPrivate = true, name = "sling.servlet.extensions", value = "ics"),
		@Property(propertyPrivate = true, name = "sling.servlet.methods", value = "GET") })
public class Cal extends SlingSafeMethodsServlet {

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	YearPlanUtil yearPlanUtil;

	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServerException,
			IOException {

		// MeetingDAO meetingDAO = resourceResolver.adaptTo(MeetingDAO.class);
				User user = ((org.girlscouts.vtk.models.User) request.getSession()
						.getAttribute(org.girlscouts.vtk.models.User.class.getName()));

				Troop troop = (Troop) request.getSession().getValue("VTK_troop");
				
				
		response.setHeader("Content-Disposition",
				"attachment;filename=\""+ troop.getYearPlan().getName()+".ics\"");
		response.setContentType("text/calendar");

		

		// MeetingDAO meetingDAO = sling.getService(MeetingDAO.class);
		try {
			net.fortuna.ical4j.model.Calendar calendar = yearPlanUtil
					.yearPlanCal(user, troop);

			// java.io.FileOutputStream fout = new
			// java.io.FileOutputStream("mycalendar.ics");
			ServletOutputStream fout = response.getOutputStream();

			net.fortuna.ical4j.data.CalendarOutputter outputter = new net.fortuna.ical4j.data.CalendarOutputter();
			outputter.output(calendar, fout);
			fout.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
