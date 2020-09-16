package org.girlscouts.vtk.modifiedcheck.impl;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.girlscouts.vtk.modifiedcheck.ModifiedChecker;

import java.io.IOException;

@SlingServlet(
        resourceTypes = "sling/servlet/default",
        selectors = "expiredcheck",
        extensions = "json",
        methods = "GET"
)
// URL: /content/girlscouts-vtk/controllers/vtk.expiredcheck.json
public class ModifiedCheckerServlet extends SlingSafeMethodsServlet {
    @Reference
    protected ModifiedChecker checker;

    private static final Logger log = LoggerFactory.getLogger(ModifiedChecker.class);

    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        //TODO: use checker.isModified();
        try {

            String sid = request.getParameter("sid");
            String yearPlanId = request.getParameter("ypid");

            boolean isMod = checker.isModified(sid, yearPlanId);

            //response.getWriter().write("vtkResponse{\"usid\":\""+ sid +"\",\"yp_cng\":\""+ isMod +"\"}");
            response.getWriter().write("{\"usid\":\"" + sid + "\",\"yp_cng\":\"" + isMod + "\"}");
            log.debug("Wrote response: {}", response.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
