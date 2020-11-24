package org.girlscouts.vtk.sling.servlet.react.action;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.osgi.component.util.MeetingUtil;
import org.girlscouts.vtk.osgi.component.util.VtkUtil;
import org.girlscouts.vtk.sling.servlet.react.YearPlanServlet;
import org.girlscouts.vtk.sling.servlet.react.internal.VTKReactConstants;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.util.StringTokenizer;

@Component(service = Servlet.class, property = {
        Constants.SERVICE_DESCRIPTION + "=Girl Scouts VTK Change Meeting Positions Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_POST,
        "sling.servlet.extensions=html",
        "sling.servlet.resourceTypes=girlscouts/vtk/react/action/change-meeting-positions"})
public class ChangeMeetingPositionsServlet extends SlingAllMethodsServlet implements OptingServlet, VTKReactConstants {

    private static final Logger log = LoggerFactory.getLogger(YearPlanServlet.class);

    @Reference
    private VtkUtil vtkUtil;
    @Reference
    private MeetingUtil meetingUtil;

    @Activate
    private void activate() {
        log.debug("Girl Scouts VTK Change Meeting Positions Servlet activated");
    }


    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        processRequest(request, response);
    }

    private void processRequest(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        response.setContentType("application/json");
        response.addHeader("Cache-Control", "no-cache");
        response.addHeader("Cache-Control", "no-store");
        response.addHeader("Cache-Control", "must-revalidate");
        response.addHeader("pragma", "no-cache");
        response.addHeader("expires", "0");
        JsonObject json = new JsonObject();
        try {
            String order = request.getParameter("order");
            while (order.indexOf(",,") != -1) {
                order = order.replaceAll(",,", ",");
            }
            StringTokenizer t = new StringTokenizer(order, ",");
            User user = VtkUtil.getUser(request.getSession());
            boolean isViewingArchived = !user.getCurrentYear().equals(String.valueOf(vtkUtil.getCurrentGSYear()));
            if(!isViewingArchived){
                Troop troop = VtkUtil.getTroop(request.getSession());
                if (troop != null) {
                    if (troop.getYearPlan().getMeetingEvents().size() != t.countTokens()) {
                        String tmp = order;
                        if (!tmp.startsWith(",")) {
                            tmp = "," + tmp;
                        }
                        if (!tmp.endsWith(",")) {
                            tmp = tmp + ",";
                        }
                        for (int i = troop.getYearPlan().getMeetingEvents().size(); i > 0; i--) {
                            if (tmp.indexOf("," + i + ",") == -1) {
                                order = i + "," + order;
                            }
                        }
                    }
                    troop = meetingUtil.changeMeetingPositions(user, troop, order);
                    request.getSession().setAttribute("VTK_troop", troop);
                    json.addProperty("success",  Boolean.TRUE);
                }
            }else{
                json.addProperty("success",  Boolean.FALSE);
                json.addProperty("reason",  "Viewing archived troop");
            }
            response.getWriter().write(new Gson().toJson(json));
        } catch (Exception e) {
            log.error("Error occured: ", e);
        }
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
