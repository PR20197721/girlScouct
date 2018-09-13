package org.girlscouts.web.cq.workflow;

import java.util.Collections;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.commons.util.AssetReferenceSearch;
import com.day.cq.replication.ReplicationOptions;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.workflow.process.ActivatePageProcess;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;

@Component
@Service
public class AssetPublishActivateProcess implements WorkflowProcess {
	@Property(value = "Collect the Asset References of a page and activate them to Publish Instance")
	static final String DESCRIPTION = Constants.SERVICE_DESCRIPTION;
	@Property(value = "Adobe")
	static final String VENDOR = Constants.SERVICE_VENDOR;
	@Property(value = "Asset Publish")
	static final String LABEL = "process.label";

	private static final String TYPE_JCR_PATH = "JCR_PATH";

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Reference
	private Replicator replicator;

	private static final Logger log = LoggerFactory
			.getLogger(AssetPreviewActivateProcess.class);

	ActivatePageProcess activate = new ActivatePageProcess();

	public void execute(WorkItem item, WorkflowSession session, MetaDataMap arg2)
			throws WorkflowException {
		ResourceResolver resolver = null;

		try {
			resolver = this.resourceResolverFactory
					.getResourceResolver(Collections.singletonMap(
							"user.jcr.session", (Object) session.getSession()));
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		try {
			WorkflowData workflowData = item.getWorkflowData();
			if (workflowData.getPayloadType().equals(TYPE_JCR_PATH)) {
				String path = workflowData.getPayload().toString();
				Session jcrSession = session.getSession();
				Resource resource = resolver.getResource(path + "/"
						+ JcrConstants.JCR_CONTENT);
				Node node = resource.adaptTo(Node.class);
				AssetReferenceSearch assetReference = new AssetReferenceSearch(
						node, "/content/dam", resolver);
				for (Map.Entry<String, Asset> assetMap : assetReference
						.search().entrySet()) {
					Asset asset = assetMap.getValue();

					try {
						Resource assetResource = (Resource) asset
								.adaptTo(Resource.class);
						ReplicationStatus replStatus = null;

						replStatus = (ReplicationStatus) assetResource
								.adaptTo(ReplicationStatus.class);

						/*
						 * log.error("LAST PUBLISHED" +
						 * replStatus.NODE_PROPERTY_LAST_REPLICATION_ACTION);
						 * log.error("ASSET" + "is Delivered? " +
						 * String.valueOf(replStatus.isDelivered()));
						 * log.error("ASSET" + "is Activated? " +
						 * String.valueOf(replStatus.isActivated()));
						 */
						
						//log.error("IS NULL? " + replicator.toString());
						activateAssetToPublish(jcrSession, asset, activate);
						// replicator.replicate(jcrSession,
						// activate.getReplicationType(), asset.getPath());
					} catch (Exception e) {
						log.error(e.getMessage());

					}
				}
			}
		} catch (Exception e) {
			log.error("ERROR AT END");
		}
	}

	private void activateAssetToPublish(Session jcrSession, Asset asset,
			ActivatePageProcess activate) {
		try {
			replicator.replicate(jcrSession, activate.getReplicationType(),
					asset.getPath());
			log.error("ASSET PATH " + asset.getPath());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}