package org.girlscouts.gsusa.services;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@SlingServlet(
        label = "GSUSA ResourceType servlet", description = "Gets resourceType property for Article Tile and Dynamic Tag Carousel", paths = {},
        methods = { "GET" }, // Ignored if paths is set - Defaults to POST if not specified
        resourceTypes = { "gsusa/servlets/resourcetype" }, // Ignored if
        // paths is set
        selectors = {}, // Ignored if paths is set
        extensions = { "html", "htm" }  // Ignored if paths is set
)
public class ResourceTypeServlet extends SlingAllMethodsServlet implements OptingServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {



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
