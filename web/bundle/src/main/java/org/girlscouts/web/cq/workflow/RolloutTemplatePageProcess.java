package org.girlscouts.web.cq.workflow;

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
import org.girlscouts.web.components.PageReplicationUtil;
import org.girlscouts.web.constants.PageReplicationConstants;
import org.girlscouts.web.cq.workflow.service.RolloutTemplatePageService;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import javax.jcr.Value;


@Component
@Service
public class RolloutTemplatePageProcess implements WorkflowProcess, PageReplicationConstants {
	@Property(value = "Roll out a page if it is the source page of a live copy, and then activate target pages.")
	static final String DESCRIPTION = Constants.SERVICE_DESCRIPTION;
	@Property(value = "Girl Scouts")
	static final String VENDOR = Constants.SERVICE_VENDOR;
	@Property(value = "Girl Scouts Roll out Process")
	static final String LABEL = "process.label";
    private static Logger log = LoggerFactory.getLogger(RolloutTemplatePageProcess.class);
    
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	
	@Reference
	private RolloutTemplatePageService gsRolloutService;

    public void execute(WorkItem item, WorkflowSession workflowSession, MetaDataMap metadata)
            throws WorkflowException {
		log.info("Executing rollout workflow {}", metadata);
		MetaDataMap mdm = item.getWorkflowData().getMetaDataMap();
		Set<String> councils = PageReplicationUtil.getCouncils(mdm);
		if (councils != null && !councils.isEmpty()) {
			log.info("councils={}", councils);
			try {
				Session session = workflowSession.getSession();
				ResourceResolver resourceResolver = resourceResolverFactory
						.getResourceResolver(Collections.singletonMap("user.jcr.session", (Object) session));
				String srcPath = item.getWorkflowData().getPayload().toString();
				String subject = "", message = "", templatePath = "";
				Boolean useTemplate = false, delay = false, notify = false, crawl = false, activate = false,
						newPage = false;
				try {
					if (mdm.get(PARAM_NEW_PAGE) != null) {
						newPage = ((Value) mdm.get(PARAM_NEW_PAGE)).getBoolean();
					}
				} catch (Exception e) {
					log.error("Rollout Workflow encountered error: ", e);
				}
				log.info("newPage={}", newPage);
				try {
					if (mdm.get(PARAM_DELAY) != null) {
						delay = ((Value) mdm.get(PARAM_DELAY)).getBoolean();
					}
				} catch (Exception e) {
					log.error("Rollout Workflow encountered error: ", e);
				}
				log.info("delay={}", delay);
				try {
					if (mdm.get(PARAM_USE_TEMPLATE) != null) {
						useTemplate = ((Value) mdm.get(PARAM_USE_TEMPLATE)).getBoolean();
					}
				} catch (Exception e) {
					log.error("Rollout Workflow encountered error: ", e);
				}
				log.info("useTemplate={}", useTemplate);
				try {
					if (mdm.get(PARAM_TEMPLATE_PATH) != null) {
						templatePath = ((Value) mdm.get(PARAM_TEMPLATE_PATH)).getString();
					}
				} catch (Exception e) {
					log.error("Rollout Workflow encountered error: ", e);
				}
				log.info("templatePath={}", templatePath);
				try {
					if (mdm.get(PARAM_CRAWL) != null) {
						crawl = ((Value) mdm.get(PARAM_CRAWL)).getBoolean();
					}
				} catch (Exception e) {
					log.error("Rollout Workflow encountered error: ", e);
				}
				log.info("crawl={}", crawl);
				try {
					if (mdm.get(PARAM_NOTIFY) != null) {
						notify = ((Value) mdm.get(PARAM_NOTIFY)).getBoolean();
					}
				} catch (Exception e) {
					log.error("Rollout Workflow encountered error: ", e);
				}
				log.info("notify={}", notify);
				try {
					if (mdm.get(PARAM_EMAIL_SUBJECT) != null) {
						subject = ((Value) mdm.get(PARAM_EMAIL_SUBJECT)).getString();
					}
				} catch (Exception e) {
					log.error("Rollout Workflow encountered error: ", e);
				}
				log.info("subject={}", subject);
				try {
					if (mdm.get(PARAM_EMAIL_MESSAGE) != null) {
						message = ((Value) mdm.get(PARAM_EMAIL_MESSAGE)).getString();
					}
				} catch (Exception e) {
					log.error("Rollout Workflow encountered error: ", e);
				}
				log.info("message={}", message);
				try {
					if (mdm.get(PARAM_ACTIVATE) != null) {
						activate = ((Value) mdm.get(PARAM_ACTIVATE)).getBoolean();
					}
				} catch (Exception e) {
					log.error("Rollout Workflow encountered error: ", e);
				}
				log.info("activate={}", activate);
				Node dateRolloutNode = PageReplicationUtil.getDateRolloutNode(session, resourceResolver, delay);
				log.info("dateRolloutNode={}", dateRolloutNode.getPath());
				Set<String> sortedCouncils = new TreeSet<String>();
				sortedCouncils.addAll(councils);
				dateRolloutNode.setProperty(PARAM_COUNCILS, sortedCouncils.toArray(new String[sortedCouncils.size()]));
				String[] emails = PageReplicationUtil.getEmails(resourceResolver);
				dateRolloutNode.setProperty(PARAM_REPORT_EMAILS, emails);
				String[] ips1 = PageReplicationUtil.getIps(resourceResolver, 1);
				String[] ips2 = PageReplicationUtil.getIps(resourceResolver, 2);
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
							log.info("calling RolloutTemplatePageService");
							gsRolloutService.rollout(path);
						}
					}).start();
				} catch (Exception e) {
					PageReplicationUtil.markReplicationFailed(dateRolloutNode);
					log.error("Rollout Workflow encountered error: ", e);
				}
			} catch (Exception e) {
				log.error("Rollout Workflow encountered error: ", e);
			}
		}
	}
}
