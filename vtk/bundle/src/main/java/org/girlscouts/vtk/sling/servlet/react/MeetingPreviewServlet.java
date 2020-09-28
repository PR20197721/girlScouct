package org.girlscouts.vtk.sling.servlet.react;

import com.google.gson.Gson;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.girlscouts.vtk.mapper.vtk.ModelToRestEntityMapper;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.osgi.component.util.MeetingAidUtil;
import org.girlscouts.vtk.osgi.service.GirlScoutsMeetingOCMService;
import org.girlscouts.vtk.rest.entity.vtk.AssetEntity;
import org.girlscouts.vtk.rest.entity.vtk.MeetingEntity;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component(service = Servlet.class, property = {
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.extensions=html",
        "sling.servlet.resourceTypes=girlscouts/vtk/react/getMeetingPreview"}
        )

public class MeetingPreviewServlet extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(MeetingPreviewServlet.class);

	@Reference
	private GirlScoutsMeetingOCMService girlScoutsMeetingOCMService;

	@Reference
    private MeetingAidUtil meetingAidUtil;

	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
		String meetingPath = request.getParameter("meetingPath");
		if (null == meetingPath || meetingPath.isEmpty()) {
			logger.warn("Cannot get meeting info at {}. ", meetingPath);
		} else {
            response.setContentType("application/json");
			Meeting meetingObj = girlScoutsMeetingOCMService.read(meetingPath);
			List<Asset> meetingAids = meetingAidUtil.getTaggedMeetingAids(meetingObj);
			meetingAids.addAll(meetingAidUtil.getTaggedVideos(meetingObj));
            ResponseEntity responseObject = new ResponseEntity(meetingObj, meetingAids);
			String json = new Gson().toJson(responseObject);
            logger.debug("Returning {}", json );
			response.getWriter().write(json);
		}
	}

	class ResponseEntity{

        private MeetingEntity meeting;
        private List<AssetEntity> meetingAids;

        public ResponseEntity(Meeting meetingObj, List<Asset> meetingAids) {
            this.meeting = ModelToRestEntityMapper.INSTANCE.toEntity(meetingObj);
            if (meetingAids != null && !meetingAids.isEmpty()) {
                List<AssetEntity> entities = new ArrayList();
                for(Asset asset:meetingAids){
                    try {
                        entities.add(ModelToRestEntityMapper.INSTANCE.toEntity(asset));
                    }catch(Exception e){

                    }
                }
                this.meetingAids = entities;
            }
        }

        public MeetingEntity getMeeting() {
            return meeting;
        }

        public void setMeeting(MeetingEntity meeting) {
            this.meeting = meeting;
        }

        public List<AssetEntity> getMeetingAids() {
            return meetingAids;
        }

        public void setMeetingAids(List<AssetEntity> meetingAids) {
            this.meetingAids = meetingAids;
        }
    }
}
