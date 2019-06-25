package org.girlscouts.vtk.services;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.common.components.GSEmailAttachment;
import org.girlscouts.common.osgi.service.GSEmailService;
import org.girlscouts.vtk.utils.VtkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

@SlingServlet(
        label = "Girl Scouts VTK Year Plan Reset servlet", description = "Resets troop year plan", paths = {},
        methods = { "POST" }, // Ignored if paths is set - Defaults to POST if not specified
        resourceTypes = { "girlscouts/servlets/resetyp" }, // Ignored if
        // paths is set
        selectors = {}, // Ignored if paths is set
        extensions = { "html", "htm" }  // Ignored if paths is set
)
public class ResetYearPlanServlet extends SlingAllMethodsServlet implements OptingServlet {
    private static final Logger log = LoggerFactory.getLogger(ResetYearPlanServlet.class);
    @Reference
    private transient SlingSettingsService settingsService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String troopId = VtkUtil.getTroop(session).getId();
        int councilId = VtkUtil.getUser(session).getApiConfig().getUser().getAdminCouncilId();
        String gsCurrentYear = VtkUtil.getUser(session).getCurrentYear();
        StringBuilder troopYpPath = new StringBuilder();
        troopYpPath.append("/vtk"+gsCurrentYear+"/"+councilId+"/troops/"+troopId+"/yearPlan");
        try{
            Node yearPlan = request.getResourceResolver().resolve(troopYpPath.toString()).adaptTo(Node.class);
            yearPlan.remove();
            request.getResourceResolver().adaptTo(Session.class).save();
        }catch (Exception e){
            log.error("Failed to remove year plan for this troop: "+troopYpPath.toString()+" : ",e);
        }
    }

    /** OptingServlet Acceptance Method **/
    @Override
    public final boolean accepts(SlingHttpServletRequest request) {
        /*
         * Add logic which inspects the request which determines if this servlet
         * should handle the request. This will only be executed if the
         * Service Configuration's paths/resourcesTypes/selectors accept the request.
         */

        //Verify user troop year plan reset request
        HttpSession session = request.getSession();
        if(VtkUtil.getUser(session) != null){
            return true;
        }else{
            log.error("Error sending email: Invalid user");
            return false;
        }
    }

}