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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = Servlet.class, property = {
        Constants.SERVICE_DESCRIPTION + "=Girl Scouts VTK Update Meeting Location Details Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_POST,
        "sling.servlet.extensions=html",
        "sling.servlet.selectors=add",
        "sling.servlet.selectors=edit",
        "sling.servlet.resourceTypes=girlscouts/vtk/react/action/update-meeting-location"})
public class UpdateMeetingLocationDetails extends SlingAllMethodsServlet implements OptingServlet{

    private static final Logger log = LoggerFactory.getLogger(UpdateMeetingLocationDetails.class);


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
    }

    private void processEditRequest(SlingHttpServletRequest request, SlingHttpServletResponse response) {
    	log.debug("inside process edit request" + request.getParameterNames());	
		/*
		 * String meetingPath = request.getParameter("meetingPath"); String locationName
		 * = request.getParameter("locName"); String locationAddress =
		 * request.getParameter("locAddress");
		 */
    	String meetingPath = "/vtk2020/999/troops/701G0000000uQzUIAU/yearPlan/meetingEvents/M1600764961709_0.24883613555956507";
    	String locationName = "Vl";
    	String locationAddress = "676767";
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
    
    private void processAddRequest(SlingHttpServletRequest request, SlingHttpServletResponse response) {
    	log.debug("inside process add request" + request.getParameterNames());
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
