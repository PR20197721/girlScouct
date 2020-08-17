package org.girlscouts.common.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.Replicator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Component(metatype = true, label = "Girl Scouts Image Dropdown Privilege checker", description = "The privileged user can see all of the items under page editor side-panel Image Dropdown")
@Service(Servlet.class)
@Properties({ @Property(name = "sling.servlet.paths", value = "/bin/getAssetFilter.html"),
		@Property(name = "sling.servlet.methods", value = "GET"),
		@Property(name = "service.description", value = "User privilege check for Image Servlet"), 
        @Property(name="DropdownExclusionValues", value={ "Products", "Paragraphs", 
        		  "Content Fragments", "Experience Fragments", "Paragraphs", "Design Packages", 
        		  "Adaptive Forms", "Manuscript", "UGC", "Interactive Communications" })
        }
        )
public class AssetFilterPrivilegeCheck extends SlingAllMethodsServlet implements OptingServlet {
	protected static final String EXTENSION = "html";
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private List<String> privilegedGroup = new ArrayList<String>();
	protected void activate(ComponentContext componentContext) {
		logger.info("ImageSelectorPrivilegeCheck Servlet Activated.");
		privilegedGroup.add("administrators");
		privilegedGroup.add("content-analysts");
	}

	public boolean accepts(SlingHttpServletRequest request) {
		return true;
	}

	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		logger.debug("ImageSelectorPrivilegeCheck  Servlet processing.");
		JsonObject json = new JsonObject();
		boolean displayAllOptions = false;
		User currentUser = request.getResourceResolver().adaptTo(User.class);
		if (currentUser.isAdmin()) {
			displayAllOptions = true;
		} else {
			Iterator<Group> currentUserGroupsIterator = null;
			try {
				currentUserGroupsIterator = currentUser.memberOf();
				while(currentUserGroupsIterator.hasNext()) {
					Group groupCurrentUser = (Group) currentUserGroupsIterator.next();
					displayAllOptions=getAssetFiter(currentUserGroupsIterator,groupCurrentUser, displayAllOptions);
					if(displayAllOptions) {
					  break;	
					}
				}
			} catch (RepositoryException e) {
				logger.error("Error in getting the groups ", e);
			}	
		}
		json.addProperty("displayAllOptions", displayAllOptions);
		response.setStatus(SlingHttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.getWriter().write(new Gson().toJson(json));
	}
	private boolean getAssetFiter(Iterator<Group> currentUserGroupsIterator, Group groupCurrentUser, boolean privilegedUser) {
		try {
			if (privilegedGroup.contains(groupCurrentUser.getID())) {
				privilegedUser = true;
				return privilegedUser;
			}
			if (currentUserGroupsIterator.hasNext()) {
				Iterator<Group> grpParentIterator = groupCurrentUser.memberOf();
				while(grpParentIterator!=null && grpParentIterator.hasNext()) {
					Group grpParentGroup = (Group) grpParentIterator.next();
					 privilegedUser = getAssetFiter(grpParentIterator, grpParentGroup, privilegedUser);
					 if(privilegedUser) {
						 break;
					 }
				}
			}
		} catch (RepositoryException e) {
			logger.error("Error in getting the groups ", e);
		}
		return privilegedUser;
	}
}
