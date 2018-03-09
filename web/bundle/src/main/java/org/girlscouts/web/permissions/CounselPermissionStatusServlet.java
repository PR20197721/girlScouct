package org.girlscouts.web.permissions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Session;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.girlscouts.web.servlets.JSONOutputServlet;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.QueryBuilder;

@SlingServlet(paths="/bin/utils/counsel_permission_status", methods="GET")
@SuppressWarnings("serial")
public class CounselPermissionStatusServlet extends JSONOutputServlet {
	private static final Logger LOGGER = LoggerFactory.getLogger(CounselPermissionStatusServlet.class);
	
	@Reference
	private QueryBuilder queryBuilder;
	
	@Override
	protected JSONObject getJson(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException{
		Session adminSession = null;
		try {
			
			ResourceResolver resourceResolver = request.getResourceResolver();
			adminSession = (Session) resourceResolver.adaptTo(Session.class);
			ContentCounselRetriever retriever = new ContentCounselRetriever(adminSession, queryBuilder);
			List<Counsel> counselList = retriever.getCounsels();
			List<CounselDTO> counselDtos = new ArrayList<>();
			for(Counsel counsel : counselList) {
				counselDtos.add(new CounselDTO(counsel));
			}
			
			JSONObject returner = new JSONObject();
			returner.put("counsels", counselDtos);
			returner.put("availableFolders", retriever.getAvailableDamFolders());
			return returner;
			
		} catch (CounselPermissionModificationException | JSONException e) {
			LOGGER.error("Unable to modify counsel access controls." + e.getMessage(), e);
			return new JSONObject();
		} 
		
	}
	
}
