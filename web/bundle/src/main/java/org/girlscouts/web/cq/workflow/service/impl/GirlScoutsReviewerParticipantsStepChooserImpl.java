package org.girlscouts.web.cq.workflow.service.impl;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.ParticipantStepChooser;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.Workflow;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.metadata.MetaDataMap;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = ParticipantStepChooser.class, name = "Girl Scouts Reviewer Participant Chooser", property = {
		"chooser.label = Girl Scouts Reviewer Participant Chooser" })
public class GirlScoutsReviewerParticipantsStepChooserImpl implements ParticipantStepChooser {

	@Reference
	private ResourceResolverFactory resolverFactory;
	private static final Logger log = LoggerFactory.getLogger(GirlScoutsReviewerParticipantsStepChooserImpl.class);
	private static final String VTK_RESOURCES_PATH = "/content/vtk-resources2";

	@Override
	public String getParticipant(WorkItem workItem, WorkflowSession wfSession, MetaDataMap metaDataMap)
			throws WorkflowException {
		String participant = "administrators";
		ResourceResolver rr = null;
		log.error("ParticipantsStepReviewerChooserImpl: ");
		try {
			Workflow wf = workItem.getWorkflow();
			WorkflowData workflowData = wf.getWorkflowData();
			if (workflowData.getPayloadType() == "JCR_PATH") {
				String path = workflowData.getPayload().toString();
				log.error("ParticipantsStepReviewerChooserImpl: path=" + path);
				Map<String, Object> serviceParams = new HashMap<String, Object>();
				serviceParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-process-service");
				rr = resolverFactory.getServiceResourceResolver(serviceParams);
				Resource res = rr.resolve(path);
				log.error("ParticipantsStepReviewerChooserImpl: resource=" + res);
				Node node = res.adaptTo(Node.class);
				if (node.getPath().startsWith(VTK_RESOURCES_PATH))
					path = node.getAncestor(3).getName();
				else
					path = node.getAncestor(2).getName();
				participant = path + "-reviewers";
				log.error("ParticipantsStepReviewerChooserImpl: participant=" + participant);
			}
		} catch (Exception e) {
			log.error("Error ocurred in ParticipantsStepReviewerChooserImpl: ", e);
		} finally {
			try {
				rr.close();
			} catch (Exception e2) {

			}
		}
		return participant;
	}
}