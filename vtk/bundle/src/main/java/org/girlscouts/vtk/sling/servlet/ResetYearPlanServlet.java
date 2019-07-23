package org.girlscouts.vtk.sling.servlet;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.osgi.component.util.VtkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


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

    @Reference
    public ResourceResolverFactory resourceFactory;

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        HttpSession session = request.getSession();
        try {
            Troop selectedTroop = (Troop) session.getAttribute("VTK_troop");
            log.debug("YEAR PLAN TO DELETE: "+selectedTroop.getPath()+"/yearPlan");
            Map<String,Object> paramMap = new HashMap<String,Object>();
            paramMap.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
            ResourceResolver adminResolver = null;
            try {
                adminResolver = resourceFactory.getServiceResourceResolver(paramMap);
                Node yearPlan = adminResolver.resolve(selectedTroop.getPath()+"/yearPlan").adaptTo(Node.class);
                LocalDateTime now = LocalDateTime.now();
                String currentTime = dtf.format(now);
                currentTime = currentTime.replaceAll("/", "-");
                currentTime = currentTime.replaceAll(" ", "T");
                currentTime = currentTime.replaceAll(":", "-");
                String newPath = yearPlan.getPath()+"-" + currentTime;
                adminResolver.adaptTo(Session.class).move(yearPlan.getPath(), newPath);
                adminResolver.adaptTo(Session.class).save();
                adminResolver.close();
            } catch (Exception e) {
                log.error("Failed to remove year plan for this troop: "+selectedTroop.getPath()+"/yearPlan"+" : ",e);
            } finally {
                try {
                    if (adminResolver != null) {
                        adminResolver.close();
                    }
                } catch (Exception e) {
                    log.error("Exception is thrown closing resource resolver: ", e);
                }
            }
        } catch (Exception e) {
            log.error("Exception occurred: ", e);
        }
    }

    @Override
    public boolean accepts(SlingHttpServletRequest request) {
        HttpSession session = request.getSession();
        if ((ApiConfig)session.getAttribute(ApiConfig.class.getName()) != null) {
            return true;
        }else{
            return false;
        }

    }
}