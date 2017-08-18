package org.girlscouts.cq.workflow;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.web.components.PageActivationUtil;
import org.girlscouts.web.constants.PageActivationConstants;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import javax.jcr.Value;
import org.girlscouts.web.service.rollout.GSRolloutService;

@Component
@Service
public class RolloutProcess implements WorkflowProcess, PageActivationConstants {
	@Property(value = "Roll out a page if it is the source page of a live copy, and then activate target pages.")
	static final String DESCRIPTION = Constants.SERVICE_DESCRIPTION;
	@Property(value = "Girl Scouts")
	static final String VENDOR = Constants.SERVICE_VENDOR;
	@Property(value = "Girl Scouts Roll out Process")
	static final String LABEL = "process.label";
    private static Logger log = LoggerFactory.getLogger(RolloutProcess.class);
    
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	
	@Reference
	private GSRolloutService gsRolloutService;


    public void execute(WorkItem item, WorkflowSession workflowSession, MetaDataMap metadata)
            throws WorkflowException {
		MetaDataMap mdm = item.getWorkflowData().getMetaDataMap();
		Set<String> councils = getCouncils(mdm);
		if (councils != null && !councils.isEmpty()) {
			try {
				Session session = workflowSession.getSession();
				ResourceResolver resourceResolver = resourceResolverFactory
						.getResourceResolver(Collections.singletonMap("user.jcr.session", (Object) session));
				String srcPath = item.getWorkflowData().getPayload().toString();
				String subject = "", message = "", templatePath = "";
				Boolean useTemplate = false, delay = false, notify = false, crawl = false, activate = true,
						newPage = false;
				try {
					newPage = ((Value) mdm.get(PARAM_NEW_PAGE)).getBoolean();
				} catch (Exception e) {
					log.error("Rollout Workflow encountered error: ", e);
				}
				try {
					delay = ((Value) mdm.get(PARAM_DELAY)).getBoolean();
				} catch (Exception e) {
					log.error("Rollout Workflow encountered error: ", e);
				}
				try {
					useTemplate = ((Value) mdm.get(PARAM_USE_TEMPLATE)).getBoolean();
				} catch (Exception e) {
					log.error("Rollout Workflow encountered error: ", e);
				}
				try {
					templatePath = ((Value) mdm.get(PARAM_TEMPLATE_PATH)).getString();
				} catch (Exception e) {
					log.error("Rollout Workflow encountered error: ", e);
				}
				try {
					crawl = ((Value) mdm.get(PARAM_CRAWL)).getBoolean();
				} catch (Exception e) {
					log.error("Rollout Workflow encountered error: ", e);
				}
				try {
					notify = ((Value) mdm.get(PARAM_NOTIFY)).getBoolean();
				} catch (Exception e) {
					log.error("Rollout Workflow encountered error: ", e);
				}
				try {
					subject = ((Value) mdm.get(PARAM_EMAIL_SUBJECT)).getString();
				} catch (Exception e) {
					log.error("Rollout Workflow encountered error: ", e);
				}
				try {
					message = ((Value) mdm.get(PARAM_EMAIL_MESSAGE)).getString();
				} catch (Exception e) {
					log.error("Rollout Workflow encountered error: ", e);
				}
				try {
					activate = ((Value) mdm.get(PARAM_ACTIVATE)).getBoolean();
				} catch (Exception e) {
					log.error("Rollout Workflow encountered error: ", e);
				}
				Node dateRolloutNode = getDateRolloutNode(session, resourceResolver, delay);
				Set<String> sortedCouncils = new TreeSet<String>();
				sortedCouncils.addAll(councils);
				dateRolloutNode.setProperty(PARAM_COUNCILS, sortedCouncils.toArray(new String[sortedCouncils.size()]));
				String[] emails = PageActivationUtil.getEmails(resourceResolver);
				dateRolloutNode.setProperty(PARAM_REPORT_EMAILS, emails);
				String[] ips1 = PageActivationUtil.getIps(resourceResolver, 1);
				String[] ips2 = PageActivationUtil.getIps(resourceResolver, 2);
				dateRolloutNode.setProperty(PARAM_DISPATCHER_IPS + "1", ips1);
				dateRolloutNode.setProperty(PARAM_DISPATCHER_IPS + "2", ips2);
				dateRolloutNode.setProperty(PARAM_NEW_PAGE, newPage);
				dateRolloutNode.setProperty(PARAM_CRAWL, crawl);
				dateRolloutNode.setProperty(PARAM_DELAY, delay);
				dateRolloutNode.setProperty(PARAM_ACTIVATE, activate);
				dateRolloutNode.setProperty(PARAM_SOURCE_PATH, srcPath);
				dateRolloutNode.setProperty(PARAM_NOTIFY, notify);
				dateRolloutNode.setProperty(PARAM_USE_TEMPLATE, useTemplate);
				dateRolloutNode.setProperty(PARAM_TEMPLATE_PATH, templatePath);
				dateRolloutNode.setProperty(PARAM_EMAIL_SUBJECT, subject);
				dateRolloutNode.setProperty(PARAM_EMAIL_MESSAGE, message);
				dateRolloutNode.setProperty(PARAM_STATUS, STATUS_CREATED);
				session.save();
				final String path = dateRolloutNode.getPath();
				try {
					new Thread(new Runnable() {
						@Override
						public void run() {
							gsRolloutService.rollout(path);
						}
					}).start();
				} catch (Exception e) {
					log.error("Rollout Workflow encountered error: ", e);
				}
			} catch (Exception e) {
				log.error("Rollout Workflow encountered error: ", e);
			}
		}
    }

	private Node getDateRolloutNode(Session session, ResourceResolver resourceResolver, boolean delay)
			throws RepositoryException {
		Node dateRolloutNode = null;
		Resource etcRes = resourceResolver.resolve("/etc");
		Node etcNode = etcRes.adaptTo(Node.class);
		Node activationsNode = null;
		Node activationTypeNode = null;
		String date = PageActivationUtil.getDateRes();
		if (etcNode.hasNode(PAGE_ACTIVATIONS_NODE)) {
			activationsNode = etcNode.getNode(PAGE_ACTIVATIONS_NODE);
		} else {
			activationsNode = etcNode.addNode(PAGE_ACTIVATIONS_NODE);
		}
		if (delay) {
			if (activationsNode.hasNode(DELAYED_NODE)) {
				activationTypeNode = activationsNode.getNode(DELAYED_NODE);
			} else {
				activationTypeNode = activationsNode.addNode(DELAYED_NODE);
				session.save();
			}
		} else {

			if (activationsNode.hasNode(INSTANT_NODE)) {
				activationTypeNode = activationsNode.getNode(INSTANT_NODE);
			} else {
				activationTypeNode = activationsNode.addNode(INSTANT_NODE);
				session.save();
			}
		}
		if (activationTypeNode.hasNode(date)) {
			dateRolloutNode = activationTypeNode.getNode(date);
		} else {
			dateRolloutNode = activationTypeNode.addNode(date);
			session.save();
		}
		return dateRolloutNode;
	}

	private Set<String> getCouncils(MetaDataMap mdm) {
		Set<String> councils = new HashSet<String>();
		try {
			Value[] values = (Value[]) mdm.get(PARAM_COUNCILS);
			if (values != null && values.length > 0) {
				for (Value value : values) {
					councils.add(value.getString().trim());
				}
			}
		} catch (Exception e) {
			log.error("Rollout Workflow encountered error: ", e);
			try {
				Value singleValue = (Value) mdm.get(PARAM_COUNCILS);
				if (singleValue != null) {
					councils.add(singleValue.getString().trim());
				}
			} catch (Exception e1) {
				log.error("Rollout Workflow encountered error: ", e);
			}
		}
		return councils;
	}
}
