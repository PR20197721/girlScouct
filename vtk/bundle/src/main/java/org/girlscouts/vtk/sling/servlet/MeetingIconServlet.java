package org.girlscouts.vtk.sling.servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.osgi.service.GirlScoutsMeetingIconService;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.InputStream;
import java.io.OutputStream;

@Component(service = Servlet.class, property = {
        Constants.SERVICE_DESCRIPTION + "= Girl Scouts VTK Meeting Icon Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.resourceTypes=girlscouts/vtk/react/meeting-icon"})
//EX: http://localhost:4503/bin/vtk/v1/meetingSearch?search={%22keywords%22:%22Award%22,%22year%22:2017,%22meetingPlanType%22:%22Journey%22,%22level%22:[%22daisy%22,%22Junior%22],%22categoryTags%22:[%22Its_Your_Story_-_Tell_It,%22]}
public class MeetingIconServlet extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 1L;
    private static Logger log = LoggerFactory.getLogger(MeetingIconServlet.class);

    @Reference
    public GirlScoutsMeetingIconService girlScoutsMeetingIconService;

    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        String[] selectors = request.getRequestPathInfo().getSelectors();
        if(selectors != null && selectors.length > 0){
            ApiConfig config = null;
            try {
                config = (ApiConfig) request.getSession().getAttribute(ApiConfig.class.getName());
                if (config != null) {
                    String meetingId = selectors[0];
                    if (meetingId != null) {
                        InputStream is = girlScoutsMeetingIconService.getIconByMeetingId(meetingId);
                        if (is != null) {
                            try {
                                // it is the responsibility of the container to close output stream
                                OutputStream os = response.getOutputStream();
                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                response.setContentType("image/png");
                                while ((bytesRead = is.read(buffer)) != -1) {
                                    os.write(buffer, 0, bytesRead);
                                }
                            } catch (Exception e) {
                                log.error("Error occured: ", e);
                            }
                        }
                    }
                }else{
                    log.error("Invalid VTK user session : ");
                }
            } catch (Exception e) {
                log.error("Unable to get Api Config from session : ", e);
            }
        }
    }

}