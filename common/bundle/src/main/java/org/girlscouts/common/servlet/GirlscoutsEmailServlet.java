package org.girlscouts.common.servlet;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

@SlingServlet(
        label = "Girl Scouts VTK email servlet", description = "Sends vtk email", paths = {},
        methods = { "POST" }, // Ignored if paths is set - Defaults to POST if not specified
        resourceTypes = { "girlscouts/servlets/email" }, // Ignored if
        // paths is set
        selectors = {}, // Ignored if paths is set
        extensions = { "html", "htm" }  // Ignored if paths is set
)
public class GirlscoutsEmailServlet extends SlingAllMethodsServlet implements OptingServlet {
    private static final Logger log = LoggerFactory.getLogger(GirlscoutsEmailServlet.class);
    @Reference
    private SlingSettingsService settingsService;

    @Reference
    private GSEmailService gsEmailService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        sendEmail(request);
    }

    public void sendEmail(SlingHttpServletRequest slingRequest){
        String addressList = slingRequest.getParameter("addresses");
        String[] addresses = addressList.split(",");
        ArrayList<GSEmailAttachment> attachments = new ArrayList<GSEmailAttachment>();
        int count = 1;
        try{
            while(slingRequest.getParameterMap().containsKey("file"+count)) {
                RequestParameter req_file = slingRequest.getRequestParameter("file" + count);
                String fN = slingRequest.getParameter("file" + count + "Name");
                String fT = req_file.getContentType();
                fT = fT.replaceAll("/", "_");
                fT = fT.toUpperCase();
                byte[] fB = req_file.get();
                attachments.add(new GSEmailAttachment(fN, fB, "", GSEmailAttachment.MimeType.valueOf(fT)));
                count++;
            }
        }catch (Exception e){
            log.error("Failed to parse attachment file data.... Sending without attachments", e);
            attachments.clear();
        }

        if(attachments.isEmpty() == true){
            try {
                log.debug("Send Email without attachments");
                gsEmailService.sendEmail(slingRequest.getParameter("subject"), Arrays.asList(addresses),slingRequest.getParameter("message"));
            }catch(Exception e){
                log.error("Email failed to send without attachments", e);
            }

        }else{
            try {
                log.debug("Send Email with attachments");
                gsEmailService.sendEmail(slingRequest.getParameter("subject"),Arrays.asList(addresses),slingRequest.getParameter("message"), new HashSet<GSEmailAttachment>(attachments));
            }catch(Exception e){
                log.error("Email failed to send with attachments", e);
            }
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