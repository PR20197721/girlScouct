package org.girlscouts.vtk.impl.servlets;

import java.io.DataOutputStream;
import java.net.URL;
import java.util.Dictionary;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.girlscouts.vtk.auth.dao.SalesforceDAO;
import org.girlscouts.vtk.auth.dao.SalesforceDAOFactory;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.auth.models.User;
import org.girlscouts.vtk.ejb.TroopUtil;
import org.girlscouts.vtk.helpers.ConfigListener;
import org.girlscouts.vtk.helpers.ConfigManager;
import org.girlscouts.vtk.helpers.CouncilMapper;
import org.girlscouts.vtk.utils.VtkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(label = "Girl Scouts VTK Salesforce Authentication Servlet", description = "Handles OAuth Authentication with Salesforce", metatype = true, immediate = true)
@Service
@Properties({
		@Property(propertyPrivate = true, name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
		@Property(propertyPrivate = true, name = "sling.servlet.selectors", value = "sso"),
		//@Property(propertyPrivate = true, name = "sling.servlet.extensions", value = "html"),
		//@Property(propertyPrivate = true, name = "sling.servlet.methods", value = "null") })
	
		
		@Property(propertyPrivate = true, name = "sling.servlet.extensions", value = {
				"html", "xml" }),
		
		
		
		@Property(propertyPrivate = true, name = "sling.servlet.methods", value = {
		"POST", "GET" })
		 })
		
public class SSO extends SlingSafeMethodsServlet implements
		ConfigListener {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) {
		System.err.println("GET ***********");
	}

	
	public void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) {
	
	System.err.println("POST***********");
	
	}

	public void updateConfig(Dictionary configs) {
		// TODO Auto-generated method stub
		
	}

	
}
