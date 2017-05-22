package org.girlscouts.vtk.impl.servlets;

	import java.io.ByteArrayInputStream;
	import java.io.IOException;
	import java.io.InputStream;
	import java.io.PrintWriter;
	import javax.jcr.Node;
	import javax.jcr.RepositoryException;
	import javax.jcr.Session;
	import javax.jcr.ValueFactory;
	import org.apache.felix.scr.annotations.*;
	import org.apache.sling.api.SlingHttpServletRequest;
	import org.apache.sling.api.SlingHttpServletResponse;
	import org.apache.sling.api.resource.ResourceResolver;
	import org.apache.sling.api.servlets.SlingAllMethodsServlet;
	import java.rmi.ServerException;
	import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.vtk.ejb.TroopUtil;
import org.girlscouts.vtk.ejb.YearPlanUtil;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.utils.VtkUtil;
	import com.day.cq.commons.jcr.JcrUtil;
	import org.codehaus.jackson.map.ObjectMapper;

	@Component(metatype = true, immediate = true)
	@Service
	@Properties({
		@Property(propertyPrivate = true, name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
		@Property(propertyPrivate = true, name = "sling.servlet.selectors", value = "vtkyearplan"),
		@Property(propertyPrivate = true, name = "sling.servlet.extensions", value = "html"),
		@Property(propertyPrivate = true, name = "sling.servlet.methods", value = { "POST", "GET" }),
		@Property(name="label", value="Girl Scouts VTK Year Plan Service"),
		@Property(name="description", value="Girl Scouts VTK year plan service")
	})
	public class VtkYearPlan extends SlingAllMethodsServlet{

		@Reference
		private ResourceResolverFactory resolverFactory;

		@Reference
		YearPlanUtil yearPlanUtil;
		
		@Override
		protected void doGet(SlingHttpServletRequest request,
				SlingHttpServletResponse response) throws ServerException,
				IOException {
			String yearPlanPath = request.getParameter("ypp");
			if( yearPlanPath ==null || "".trim().equals(yearPlanPath)){
				//TODO error code here 
				return;
			}
			
			YearPlan yearPlan =yearPlanUtil.getYearPlanJson( yearPlanPath );
			ObjectMapper mapper = new ObjectMapper();
			response.setCharacterEncoding("utf8");
	        response.setContentType("application/json"); 
			response.getWriter().write((mapper.writeValueAsString(yearPlan)));
		}

		@Override
		protected void doPost(SlingHttpServletRequest request,
				SlingHttpServletResponse response) throws ServerException,
				IOException {

			
		}

		
	}

	
	
