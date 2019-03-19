package org.girlscouts.web.services;


import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.common.components.GSEmailAttachment;
import org.girlscouts.common.osgi.service.GSEmailService;
import org.girlscouts.vtk.utils.VtkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

@SlingServlet(
        label = "Girl Scouts Web Search Servlet", description = "Set search placeholder", paths = {},
        methods = {"POST" }, // Ignored if paths is set - Defaults to POST if not specified
        resourceTypes = { "girlscouts/servlets/search" }, // Ignored if
        // paths is set
        selectors = {}, // Ignored if paths is set
        extensions = { "html", "htm" }  // Ignored if paths is set
)
public class GSSearchServlet extends SlingAllMethodsServlet implements OptingServlet {
    private static final Logger log = LoggerFactory.getLogger(GSSearchServlet.class);
    @Reference
    private SlingSettingsService settingsService;

    @Reference
    private GSEmailService gsEmailService;


    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        String keywords = request.getParameter("searchKeywords");
        String path = request.getParameter("searchPath");
        ResourceResolver rr = request.getResourceResolver();
        Node searchNode = rr.resolve(path).adaptTo(Node.class);
        try{
            searchNode.setProperty("placeholder-text",keywords);
        }catch (Exception e){
            log.error("Error setting search placeholder: ", e);
        }
        return;
    }
    /** OptingServlet Acceptance Method **/
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