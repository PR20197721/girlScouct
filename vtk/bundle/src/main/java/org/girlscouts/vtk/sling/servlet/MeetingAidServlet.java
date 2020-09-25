package org.girlscouts.vtk.sling.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.osgi.component.util.MeetingAidUtil;
import org.girlscouts.vtk.osgi.service.GirlScoutsMeetingOCMService;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {

		String meetingPath = request.getParameter("meetingPath");
		PrintWriter out = response.getWriter();
		if (null == meetingPath || meetingPath.isEmpty()) {
			logger.warn("Cannot get meetingPath. It was: " + meetingPath + ".");
		} else {
			Meeting meetingObj = girlScoutsMeetingOCMService.read(meetingPath);
			MeetingAidUtil meetingAidUtil = new MeetingAidUtil();
			List<Asset> meetingAids = meetingAidUtil.getTaggedMeetingAids(meetingObj);
			JSONObject meetingAidsObj = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			try {
				for (Asset asset : meetingAids) {
					JSONObject jsonObj = new JSONObject();

					jsonObj.put("title", asset.getTitle());
					jsonObj.put("description", asset.getDescription());
					jsonObj.put("path", asset.getPath());
					jsonArray.add(jsonObj);

				}
				meetingAidsObj.put("meeting-aids", jsonArray);
				out.print(meetingAidsObj);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}
}
