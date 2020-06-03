package org.girlscouts.web.cq.workflow;

import java.util.Collections;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;


@Component
@Service
public class StaggeredActivationProcess implements WorkflowProcess {
	
	@Property(value = "Activate a list of nodes specified in a special config node.")
	static final String DESCRIPTION = Constants.SERVICE_DESCRIPTION;
	@Property(value = "Girl Scouts")
	static final String VENDOR = Constants.SERVICE_VENDOR;
	@Property(value = "Girl Scouts Staggered Activation Process")
	static final String LABEL = "process.label";

	private final String MINUTES_OF_DELAY = "delay";
	private final String INTERVAL = "interval";
	private final String ACTIVATIONS = "activations";
	private static Logger log = LoggerFactory.getLogger(StaggeredActivationProcess.class);
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	
	@Reference
	private Replicator replicator;
	
	@Override
	public void execute(WorkItem item, WorkflowSession workflowSession, MetaDataMap metadataMap)
			throws WorkflowException {
		log.info("Starting to execute staggered activation");
		final WorkflowData workflowData = item.getWorkflowData();
		final String parentPath = workflowData.getPayload().toString();
		
		log.info("Parent path is: " + parentPath);
		Session session = null;
		Node activationNode = null;
		String errorMessage = null;
		if(parentPath != null){
			try {
				session = workflowSession.getSession();
				ResourceResolver resourceResolver = resourceResolverFactory
						.getResourceResolver(Collections.singletonMap("user.jcr.session", (Object) session));
				Resource activationResource = resourceResolver.getResource(parentPath);
				if(activationResource != null){
					activationNode = activationResource.adaptTo(Node.class);
					String delay = null;
					String interval = null;
					if(activationNode.hasProperty(MINUTES_OF_DELAY)){
						delay = activationNode.getProperty(MINUTES_OF_DELAY).getString();
					} else{
						return;
					}
					if(activationNode.hasProperty(INTERVAL)){
						interval = activationNode.getProperty(INTERVAL).getString();
					} else{
						return;
					}
					
					Value[] values = activationNode.getProperty(ACTIVATIONS).getValues();
					int actionCount = 0;
					int actions = Integer.parseInt(interval);
					int minutes = Integer.parseInt(delay);
					for(int i = 0; i < values.length; i++){
						if(actionCount == actions){
							log.info("Got to " + actionCount + " actions");
							actionCount = 0;
							Thread.sleep(1000 * 60 * minutes);
						}
						Value value = values[i];
						String path = value.getString();
						
						log.info(i + ". Replicating node at path: " + path);
						if(path != null && !path.isEmpty()){
							replicator.replicate(session, ReplicationActionType.ACTIVATE, path);
						}
						
						actionCount++;
					}
					
					activationNode.setProperty("state", "complete");
					activationNode.save();
				}
			} catch (LoginException e) {
				errorMessage = e.getMessage();
			} catch (RepositoryException e) {
				errorMessage = e.getMessage();
			} catch (ReplicationException e) {
					errorMessage = e.getMessage();
			} catch (InterruptedException e) {
				errorMessage = e.getMessage();
			}
			
		}
		
	}

}
