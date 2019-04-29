package org.girlscouts.vtk.impl.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.girlscouts.vtk.helpers.TroopHashGenerator;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SlingServlet(
    paths = {"/bin/troophash"}
)
public class TroopHashServlet extends SlingSafeMethodsServlet {
    private static Logger log = LoggerFactory.getLogger(TroopHashServlet.class);
    @Reference
    TroopHashGenerator hashGenerator;

    @Override
    protected void doGet(SlingHttpServletRequest request,
            SlingHttpServletResponse response) throws ServletException,
            IOException {
        String troopId = request.getParameter("troopId");
        PrintWriter out = response.getWriter();
            response.setStatus(500);
        try {
            if (troopId == null || troopId.isEmpty()) {
                log.warn("Cannot get troopId. It was: " + troopId + ".");
                out.print(new JSONObject().put("errMsg", "Cannot get troopId.").toString());
            } else {
                out.print(new JSONObject().put("path", hashGenerator.getPath(troopId)).toString());
            }
        } catch (JSONException e) {
            log.error("Cannot generate JSON");
            throw new IOException(e);
        }
    }
    
}
