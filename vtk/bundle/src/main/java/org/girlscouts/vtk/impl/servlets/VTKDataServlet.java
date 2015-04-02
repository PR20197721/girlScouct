package org.girlscouts.vtk.impl.servlets;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SlingServlet(
    paths = {"/bin/vtk-data"}
)
public class VTKDataServlet extends SlingSafeMethodsServlet {
    private static final long serialVersionUID = -6183598654546549731L;

    private static final String ROOT = "/vtk-data";
    private static final Logger log = LoggerFactory.getLogger(VTKDataServlet.class);
    private static final Pattern PATH_PATTERN = Pattern.compile(ROOT + "/([^/]+)/([^/\\.])+.*");

    @Override
    protected void doGet(SlingHttpServletRequest request,
            SlingHttpServletResponse response) throws ServletException,
            IOException {
        String path = request.getRequestURI();
        log.debug("Request path = " + path);
        String type = null, id = null;
        Matcher matcher = PATH_PATTERN.matcher(path);
        while (matcher.find()) {
            if (matcher.groupCount() <= 2) {
                log.error("Cannot get type and id.");
                continue;
            }
            type = matcher.group(1);
            id = matcher.group(2);
        }
        log.debug("type = " + type + ", id = " + id);
        
        if (type != null && type.equals("year-plan")) {
            forwardYearPlan(request, response, id);
        }
    }
    
    private void forwardYearPlan(SlingHttpServletRequest request, 
            SlingHttpServletResponse response, String id) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/content/girlscouts-vtk/controllers/vtk.controller.html");
        request.setAttribute("yearPlanSched", "X");
        request.setAttribute("isFirst", "1");

        dispatcher.forward(request, response);
    }
    
}
