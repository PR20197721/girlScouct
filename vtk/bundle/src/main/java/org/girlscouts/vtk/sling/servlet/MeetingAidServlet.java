package org.girlscouts.vtk.sling.servlet;

import com.google.gson.Gson;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.osgi.component.util.MeetingAidUtil;
import org.girlscouts.vtk.osgi.service.GirlScoutsMeetingOCMService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Component(service = Servlet.class, property = { "sling.servlet.methods=" + HttpConstants.METHOD_GET,
		"sling.servlet.paths=" + "/bin/vtk/v1/meetingAids" })

public class MeetingAidServlet extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(MeetingAidServlet.class);

	@Reference
	private GirlScoutsMeetingOCMService girlScoutsMeetingOCMService;

	@Reference
    private MeetingAidUtil meetingAidUtil;
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
        response.setContentType("application/json");
		String meetingPath = request.getParameter("meetingPath");
		PrintWriter out = response.getWriter();
		if (null == meetingPath || meetingPath.isEmpty()) {
			logger.warn("Cannot get meetingPath. It was: " + meetingPath + ".");
		} else {
			Meeting meetingObj = girlScoutsMeetingOCMService.read(meetingPath);
			List<Asset> meetingAids = meetingAidUtil.getTaggedMeetingAids(meetingObj);
            out.print(new Gson().toJson(meetingAids));
		}
	}
}
