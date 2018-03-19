package org.girlscouts.web.servlets;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONObject;

public abstract class JSONOutputServlet extends SlingAllMethodsServlet {

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException{
		process(request, response);
	}

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException{
		process(request, response);
	}
	
	private void process(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException{
		response.setContentType("application/json");
		response.getWriter().write(Optional.ofNullable(getJson(request, response)).orElseGet(JSONObject::new).toString());
	}
	
	protected abstract JSONObject getJson(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException;
	
}
