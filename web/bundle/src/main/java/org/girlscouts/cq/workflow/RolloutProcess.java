package org.girlscouts.cq.workflow;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.Node;
import javax.jcr.RangeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.girlscouts.web.components.PageActivationUtil;
import org.girlscouts.web.constants.PageActivationConstants;
import org.girlscouts.web.councilrollout.GirlScoutsRolloutReporter;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.cq.wcm.msm.api.RolloutConfig;
import com.day.cq.wcm.msm.api.RolloutConfigManager;
import com.day.cq.wcm.msm.api.RolloutManager;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import javax.jcr.Value;
import javax.jcr.query.Query;

import org.girlscouts.web.councilupdate.PageActivator;

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
    RolloutManager rolloutManager;
    
    @Reference
    private LiveRelationshipManager relationManager;
    
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	
	@Reference
	private GirlScoutsRolloutReporter girlscoutsRolloutReporter;
	
	@Reference
	private PageActivator pa;


    public void execute(WorkItem item, WorkflowSession workflowSession, MetaDataMap metadata)
            throws WorkflowException {
		MetaDataMap mdm = item.getWorkflowData().getMetaDataMap();
		Set<String> councils = getCouncils(mdm);
		if (councils != null && councils.size() > 0) {
			try {
				Session session = workflowSession.getSession();
				ResourceResolver resourceResolver = resourceResolverFactory
						.getResourceResolver(Collections.singletonMap("user.jcr.session", (Object) session));
				String srcPath = item.getWorkflowData().getPayload().toString();
				Resource srcRes = resourceResolver.resolve(srcPath);
				if (relationManager.isSource(srcRes)) {
					Page srcPage = (Page) srcRes.adaptTo(Page.class);
					if (srcPage != null) {
						String templatePath = "";
						Boolean useTemplate = false, delay = false;
						try {
							delay = ((Value) mdm.get(PARAM_DELAY)).getBoolean();
						} catch (Exception e) {
						}
						if (useTemplate && (templatePath == null || templatePath.trim().length() == 0)) {
							log.error("Rollout Error - Use Template checked, but no template provided. Cancelling.");
							return;
						} else {
							Set<String> pagesToActivate = new HashSet<String>();
							processExistingLiveRelationships(councils, session, resourceResolver, srcRes,
									pagesToActivate);
							if (!councils.isEmpty()) {
								processNewLiveRelationships(councils, session, resourceResolver, srcRes,
										pagesToActivate);
							}
							if (!councils.isEmpty()) {
								for (String council : councils) {
									log.error("Failed to rollout processing for %s council", council);
								}
							}
							String subject = "", message = "";
							Boolean dontSend = false, crawl = false, dontActivate = true;
							try {
								useTemplate = ((Value) mdm.get(PARAM_USE_TEMPLATE)).getBoolean();
							} catch (Exception e) {
							}
							try {
								templatePath = ((Value) mdm.get(PARAM_TEMPLATE_PATH)).getString();
							} catch (Exception e) {
							}
							try {
								crawl = ((Value) mdm.get(PARAM_CRAWL)).getBoolean();
							} catch (Exception e) {
							}
							try {
								dontSend = ((Value) mdm.get(PARAM_DONT_SEND_EMAIL)).getBoolean();
							} catch (Exception e) {
							}
							try {
								subject = ((Value) mdm.get(PARAM_EMAIL_SUBJECT)).getString();
							} catch (Exception e) {
							}
							try {
								message = ((Value) mdm.get(PARAM_EMAIL_MESSAGE)).getString();
							} catch (Exception e) {
							}
							try {
								dontActivate = ((Value) mdm.get(PARAM_DONT_ACTIVATE)).getBoolean();
							} catch (Exception e) {
							}
							Node dateRolloutNode = getDateRolloutNode(session, resourceResolver, delay);
							includePagesForActivation(session, dateRolloutNode, pagesToActivate);
							String[] emails = PageActivationUtil.getEmails(resourceResolver);
							dateRolloutNode.setProperty(PARAM_REPORT_EMAILS, emails);
							String[] ips1 = PageActivationUtil.getIps(resourceResolver, 1);
							String[] ips2 = PageActivationUtil.getIps(resourceResolver, 2);
							dateRolloutNode.setProperty(PARAM_DISPATCHER_IPS + "1", ips1);
							dateRolloutNode.setProperty(PARAM_DISPATCHER_IPS + "2", ips2);
							dateRolloutNode.setProperty(PARAM_CRAWL, crawl);
							dateRolloutNode.setProperty(PARAM_DELAY, delay);
							dateRolloutNode.setProperty(PARAM_DONT_ACTIVATE, dontActivate);
							dateRolloutNode.setProperty(PARAM_SOURCE_PATH, srcPath);
							dateRolloutNode.setProperty(PARAM_DONT_SEND_EMAIL, dontSend);
							dateRolloutNode.setProperty(PARAM_USE_TEMPLATE, useTemplate);
							dateRolloutNode.setProperty(PARAM_TEMPLATE_PATH, templatePath);
							dateRolloutNode.setProperty(PARAM_EMAIL_SUBJECT, subject);
							dateRolloutNode.setProperty(PARAM_EMAIL_MESSAGE, message);
							dateRolloutNode.setProperty(PARAM_STATUS, STATUS_CREATED);
							session.save();
							if (!delay) {
								pa.process(dateRolloutNode.getPath(), session);
							}
						}
					} else {
						log.error("Resource is not a page. Quit. " + srcPath);
						return;
					}
				} else {
					log.error("Not a live copy source page. Quit. " + srcPath);
					return;
				}
			} catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
		}
    }

	private void processNewLiveRelationships(Set<String> councils, Session session, ResourceResolver resourceResolver,
			Resource srcRes, Set<String> pagesToActivate) throws RepositoryException, WCMException {
		RangeIterator relationIterator = relationManager.getLiveRelationships(srcRes.getParent(), null, null);
		while (relationIterator.hasNext()) {
			LiveRelationship relation = (LiveRelationship) relationIterator.next();
			String parentPath = relation.getTargetPath();
			Resource targetResource = resourceResolver.resolve(parentPath);
			if (!targetResource.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
				int councilNameIndex = parentPath.indexOf("/", "/content/".length());
				String councilPath = parentPath.substring(0, councilNameIndex);
				if (councils.contains(councilPath)) {
					councils.remove(councilPath);
					Boolean breakInheritance = false;
					try {
						ValueMap contentProps = ResourceUtil.getValueMap(targetResource);
						breakInheritance = contentProps.get(PARAM_BREAK_INHERITANCE, false);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (!breakInheritance) {
						PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
						Page srcPage = (Page) srcRes.adaptTo(Page.class);
						Page copyPage = pageManager.copy(srcPage,
								targetResource.getParent().getPath() + "/" + srcPage.getName(), srcPage.getName(),
								false, true);
						RolloutConfigManager configMgr = (RolloutConfigManager) resourceResolver
								.adaptTo(RolloutConfigManager.class);
						RolloutConfig gsConfig = configMgr.getRolloutConfig("/etc/msm/rolloutconfigs/gsdefault");
						LiveRelationship relationship = relationManager.establishRelationship(srcPage, copyPage, true,
								false, gsConfig);
						String targetPath = relationship.getTargetPath();
						cancelInheritance(resourceResolver, copyPage.getPath());
						rolloutManager.rollout(resourceResolver, relation, false);
						session.save();
						if (targetPath.endsWith("/jcr:content")) {
							targetPath = targetPath.substring(0, targetPath.lastIndexOf('/'));
						}
						pagesToActivate.add(targetPath);
					}
				}
			}
		}

	}

	private void processExistingLiveRelationships(Set<String> councils, Session session,
			ResourceResolver resourceResolver, Resource srcRes, Set<String> pagesToActivate)
			throws RepositoryException, WCMException {
		RangeIterator relationIterator = relationManager.getLiveRelationships(srcRes, null, null);
		while (relationIterator.hasNext()) {
			LiveRelationship relation = (LiveRelationship) relationIterator.next();
			String targetPath = relation.getTargetPath();
			Resource targetResource = resourceResolver.resolve(targetPath);
			if (!targetResource.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
				int councilNameIndex = targetPath.indexOf("/", "/content/".length());
				String councilPath = targetPath.substring(0, councilNameIndex);
				if (councils.contains(councilPath)) {
					councils.remove(councilPath);
					Boolean breakInheritance = false;
					try {
						ValueMap contentProps = ResourceUtil.getValueMap(targetResource);
						breakInheritance = contentProps.get(PARAM_BREAK_INHERITANCE, false);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (!breakInheritance) {
						rolloutManager.rollout(resourceResolver, relation, false);
						session.save();
						if (targetPath.endsWith("/jcr:content")) {
							targetPath = targetPath.substring(0, targetPath.lastIndexOf('/'));
						}
						pagesToActivate.add(targetPath);
					}
				}
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
		String date = getDateRes();
		if (etcNode.hasNode(ACTIVATIONS_NODE)) {
			activationsNode = etcNode.getNode(ACTIVATIONS_NODE);
		} else {
			activationsNode = etcNode.addNode(ACTIVATIONS_NODE);
		}
		if (delay) {
			if (activationsNode.hasNode(DELAYED_NODE)) {
				activationTypeNode = activationsNode.getNode(DELAYED_NODE);
			} else {
				activationTypeNode = activationsNode.addNode(DELAYED_NODE);
				session.save();
			}
		} else {

			if (activationsNode.hasNode(IMMEDIATE_NODE)) {
				activationTypeNode = activationsNode.getNode(IMMEDIATE_NODE);
			} else {
				activationTypeNode = activationsNode.addNode(IMMEDIATE_NODE);
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

	private void includePagesForActivation(Session session, Node dateRolloutNode, Set<String> pagesToActivate)
			throws RepositoryException {
		if (dateRolloutNode.hasProperty(PARAM_PAGES)) {
			Value[] propValues = dateRolloutNode.getProperty(PARAM_PAGES).getValues();
			for (Value value : propValues) {
				pagesToActivate.add(value.getString());
			}
		}
		Set<String> sortedPages = new TreeSet<String>();
		sortedPages.addAll(pagesToActivate);
		dateRolloutNode.setProperty(PARAM_PAGES, sortedPages.toArray(new String[sortedPages.size()]));
		session.save();
	}
    
	private Set<String> getCouncils(MetaDataMap mdm) {
		Set<String> councils = null;
		try {
			Value[] values = (Value[]) mdm.get(PARAM_COUNCILS);
			if (values != null && values.length > 0) {
				councils = new HashSet<String>();
				for (Value value : values) {
					councils.add(value.getString().trim());
				}
			}
		} catch (Exception e) {
			try {
				Value singleValue = (Value) mdm.get(PARAM_COUNCILS);
				if (singleValue != null) {
					councils.add(singleValue.getString().trim());
				}
			} catch (Exception e1) {
				System.err.println("Rollout Could Not Run - No Councils Selected");
				e1.printStackTrace();
			}
		}
		return councils;
	}

	private String getDateRes() {
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_NODE_FMT);
		String dateString = sdf.format(today);
		return dateString;
	}
    
	/**
	 * cancel the Inheritance for certain components (nodes with mixin type
	 * "cq:LiveSyncCancelled" under national template page)
	 */
	private void cancelInheritance(ResourceResolver rr, String councilPath) {
		try {
			String sql = "SELECT * FROM [cq:LiveSyncCancelled] AS s WHERE ISDESCENDANTNODE(s, '" + councilPath + "')";
			log.debug("SQL " + sql);
			for (Iterator<Resource> it = rr.findResources(sql, Query.JCR_SQL2); it.hasNext();) {
				Resource target = it.next();
				relationManager.endRelationship(target, true);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
