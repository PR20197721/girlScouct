package org.girlscouts.vtk.sling.servlet.react.action;

import java.util.Arrays;
import java.util.List;

import javax.jcr.Node;
import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.girlscouts.vtk.models.Location;
import org.girlscouts.vtk.osgi.service.GirlScoutsLocationOCMService;

@Component(service = Servlet.class, property = {
        Constants.SERVICE_DESCRIPTION + "=Girl Scouts VTK Update Meeting Location Details Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_POST,
        "sling.servlet.extensions=html",
        "sling.servlet.selectors=add",
        "sling.servlet.selectors=edit",
        "sling.servlet.selectors=remove",
        "sling.servlet.resourceTypes=girlscouts/vtk/react/action/update-meeting-location"})
public class UpdateMeetingLocationDetails extends SlingAllMethodsServlet implements OptingServlet{

    private static final Logger log = LoggerFactory.getLogger(UpdateMeetingLocationDetails.class);
    @Reference
    GirlScoutsLocationOCMService girlScoutsLocationOCMService;

    @Activate
    private void activate() {
        log.debug("Girl Scouts VTK Update Meeting Location Servlet activated");
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
    	String action[] = request.getRequestPathInfo().getSelectors();
    	log.debug("actionValue::"+action[0]);
    	List<String> list = Arrays.asList(action);
        if (list.contains("add"))
        	processAddRequest(request, response);
        if (list.contains("edit"))
        	processEditRequest(request, response);
        if (list.contains("remove"))
        	processRemoveRequest(request, response);
    }

    private void processEditRequest(SlingHttpServletRequest request, SlingHttpServletResponse response) {
    	log.debug("inside process edit request" + request.getParameterNames());	
		
		  String meetingPath = request.getParameter("meetingPath"); 
		  String locationName = request.getParameter("locName");
		  String locationAddress = request.getParameter("locAddress");
		 
		/*
		 * String meetingPath =
		 * "/vtk2020/999/troops/701G0000000uQzUIAU/yearPlan/meetingEvents/M1600764961709_0.24883613555956507";
		 * String locationName = "Vel"; String locationAddress = "676767";
		 */
    	log.debug("meetingPath:"+ meetingPath + "locationName:" + locationName + "locationAddress:" + locationAddress);
    	Resource resource = request.getResourceResolver().resolve(meetingPath);
    	if (resource != null && !ResourceUtil.isNonExistingResource(resource)) {
    		try {
	    		Node meetingNode = resource.adaptTo(Node.class);
	    		String locRefPath = meetingNode.hasProperty("locationRef") ? meetingNode.getProperty("locationRef").getString() : "";
	    		Resource locationResource = request.getResourceResolver().getResource(locRefPath);
	    		if (locationResource != null) {
	    			Node locationNode = locationResource.adaptTo(Node.class);
	    			locationNode.setProperty("name", locationName);
	    			locationNode.setProperty("address", locationAddress);
	    			locationNode.save();
	    		}
	    		
    		} catch(Exception e) {
    			log.error("Error in fetching the location details:" + e.getMessage());
    		}
    		
    	}
    	else {
    		log.info("Meeting path doesn't exist");
    	}
    }
    
    private String processAddRequest(SlingHttpServletRequest request, SlingHttpServletResponse response) {
    	log.debug("inside process add request");
    	/*String yearPlanPath = "/vtk2020/999/troops/701G0000000uQzaIAE/yearPlan";
    	String meetingPath = "/vtk2020/999/troops/701G0000000uQzaIAE/yearPlan/meetingEvents/M1600766350382_0.8493933392039076";
    	String locationName = "Vell";
    	String locationAddress = "676766";*/
    	String yearPlanPath = request.getParameter("yrPlanPath");
    	String meetingPath = request.getParameter("meetingPath"); 
		String locationName = request.getParameter("locName");
		String locationAddress = request.getParameter("locAddress");
		log.debug("yearPlanPath::"+yearPlanPath+"meetingPath:"+ meetingPath + "locationName:" + locationName + "locationAddress:" + locationAddress);
    	if (yearPlanPath !=null && meetingPath != null && locationName != null && locationAddress != null ) {
			Resource yearPlanResource = request.getResourceResolver().resolve(yearPlanPath);
	    	Location loc = new Location();
	    	log.debug("locRandomId::"+loc.getUid());
	    	if (!ResourceUtil.isNonExistingResource(yearPlanResource)) {
	    		try {
		    		Node yearPlanNode = yearPlanResource.adaptTo(Node.class);
		    		Node locationsNode = yearPlanNode.hasNode("locations") ? yearPlanNode.getNode("locations") : yearPlanNode.addNode("locations");
		    		Node locationNode = locationsNode.addNode(loc.getUid());
		    		String locationPath = locationNode.getPath();
		    		locationNode.setProperty("name", locationName);
		    		locationNode.setProperty("address", locationAddress);
		    		locationNode.setProperty("uid", loc.getUid());
		    		locationNode.save();
		    		Resource meetingResource = request.getResourceResolver().resolve(meetingPath);
		        	if (!ResourceUtil.isNonExistingResource(meetingResource)) {
		    	    		Node meetingNode = meetingResource.adaptTo(Node.class);
		    	    		meetingNode.setProperty("locationRef", locationPath);
		    	    		meetingNode.save();
		        	}
		        	else {
		        		log.info("Meeting resource doesn't exist");
		        		//return "Meeting resource doesn't exist";
		        		response.getWriter().write("");
		        	}	        	
	    		} catch(Exception e) {
	    			log.error("Error in adding the location node details:" + e.getMessage());
	    		}
	    	}
	    	else {
	    		log.info("Year plan resource doesn't exist");
	    		return "Year plan resource doesn't exist";
	    	}
    	return "Location added Successfully.";
    	}
    	else {
    		log.debug("Request parameters are null.");
    		return "Request parameters are null.";
    	}
    }

    private boolean processRemoveRequest(SlingHttpServletRequest request, SlingHttpServletResponse response) {
    	log.debug("inside process remove request");	
    	String meetingPath = request.getParameter("meetingPath"); 
    	try {
	    	Resource meetingResource = request.getResourceResolver().resolve(meetingPath);
	    	if (meetingResource != null && !ResourceUtil.isNonExistingResource(meetingResource)) {
		    		Node meetingNode = meetingResource.adaptTo(Node.class);
		    		if (meetingNode.hasProperty("locationRef")) {
		    			String locRefPath = meetingNode.getProperty("locationRef").getString();
		    			Resource locationResource = request.getResourceResolver().resolve(locRefPath);
		    			if (!ResourceUtil.isNonExistingResource(locationResource)) {
		    				locationResource.adaptTo(Node.class).remove();
		    				log.debug("location node removed");
		    			}
		    			meetingNode.getProperty("locationRef").remove();
		    			meetingNode.save();
		    		}
		    }
	    	else {
	    		log.info("Meeting path doesn't exist");
	    		return false;
	    	}
	    } catch(Exception e) {
    		log.error("Error in removing the location details."+ e.getMessage());
    	}
		return true;
    }
    
    /**
     * OptingServlet Acceptance Method
     **/
    @Override
    public final boolean accepts(SlingHttpServletRequest request) {
        /*
         * Add logic which inspects the request which determines if this servlet
         * should handle the request. This will only be executed if the
         * Service Configuration's paths/resourcesTypes/selectors accept the request.
         */
        return true;
    }
}
