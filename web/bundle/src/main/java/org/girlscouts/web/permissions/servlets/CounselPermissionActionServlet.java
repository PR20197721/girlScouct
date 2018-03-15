package org.girlscouts.web.permissions.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.jcr.Session;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.web.permissions.ContentCounselRetriever;
import org.girlscouts.web.permissions.CounselPermissionModificationException;
import org.girlscouts.web.permissions.CounselPermissionTool;
import org.girlscouts.web.permissions.CounselPermissionUpdate;
import org.girlscouts.web.permissions.dto.CounselPermissionUpdateDTO;
import org.girlscouts.web.servlets.JSONOutputServlet;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.QueryBuilder;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public abstract class CounselPermissionActionServlet extends JSONOutputServlet {
	private static final Logger LOGGER = LoggerFactory.getLogger(CounselPermissionActionServlet.class);
		
	@Override
	protected JSONObject getJson(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException{
		try {
			ResourceResolver resourceResolver = request.getResourceResolver();
			Session adminSession = (Session) resourceResolver.adaptTo(Session.class);

			List<CounselPermissionUpdate> updates = Optional.ofNullable(getUpdates()).orElseGet(ArrayList::new);
			
			if(updates.size() > 0) {
				// Get the input and tools.
				CounselPermissionUpdateDTO counselPermissionData = new Gson().fromJson(request.getParameter("inputData"), CounselPermissionUpdateDTO.class);
				CounselPermissionTool permissionTool = new CounselPermissionTool(adminSession, getInjectedQueryBuilder(), counselPermissionData.getReviewerGroupName(), counselPermissionData.getAuthorGroupName());
				
				// Make the update(s).
				for(CounselPermissionUpdate update : updates) {
					try {
						update.update(counselPermissionData, permissionTool);
					} catch (CounselPermissionModificationException e) {
						LOGGER.error("Unable to perform counsel permission action." + e.getMessage(), e);
					} 
				}
			}
			
			// Query back the updated data.
			return new ContentCounselRetriever(adminSession, getInjectedQueryBuilder()).toJson();
			
		} catch (CounselPermissionModificationException e) {
			LOGGER.error("Unable to perform any counsel permission action." + e.getMessage(), e);
			return new JSONObject();
		} 
	}
	
	public abstract List<CounselPermissionUpdate> getUpdates();
	
	public abstract QueryBuilder getInjectedQueryBuilder();
	
}
