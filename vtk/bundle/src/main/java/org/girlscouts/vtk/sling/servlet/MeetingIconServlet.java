package org.girlscouts.vtk.sling.servlet;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.girlscouts.vtk.osgi.service.GirlScoutsMeetingIconService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;

@Service
@SlingServlet(paths = {"/bin/vtk/v1/meetingIcon"})
//EX: http://localhost:4503/bin/vtk/v1/meetingSearch?search={%22keywords%22:%22Award%22,%22year%22:2017,%22meetingPlanType%22:%22Journey%22,%22level%22:[%22daisy%22,%22Junior%22],%22categoryTags%22:[%22Its_Your_Story_-_Tell_It,%22]}
public class MeetingIconServlet extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 1L;
    private static Logger log = LoggerFactory.getLogger(MeetingIconServlet.class);

    @Reference
    public GirlScoutsMeetingIconService girlScoutsMeetingIconService;

    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        String[] selectors = request.getRequestPathInfo().getSelectors();
        if(selectors != null && selectors.length > 0){
            String repoPath = girlScoutsMeetingIconService.getIconPathById(selectors[0]);
            if(repoPath != null) {
                RequestDispatcher dispatcher = request.getRequestDispatcher(repoPath);
                try {
                    dispatcher.forward(request, response);
                } catch (Exception e) {
                    log.error("Error occured: ",e);
                }
            }
        }
    }

}