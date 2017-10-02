package org.girlscouts.web.service.rollout.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.Node;
import javax.jcr.RangeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.web.components.PageActivationUtil;
import org.girlscouts.web.constants.PageActivationConstants;
import org.girlscouts.web.councilupdate.PageReplicator;
import org.girlscouts.web.service.email.GSEmailService;
import org.girlscouts.web.service.rollout.GSPageDeletionService;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.cq.wcm.msm.api.RolloutManager;

/*
 * Girl Scouts Page Deletion Service - DL
 * This process deletes a template page from council sites
 * This system of staggering deletions allows the dispatcher caches to rebuild during large rollouts
 */
@Component
@Service
public class GSPageDeletionServiceImpl
		implements GSPageDeletionService, PageActivationConstants, PageActivationConstants.Email {

	@Property(value = "Determine all live sync pages from submitted template page, and then deactivate and delete target pages.")
	static final String DESCRIPTION = Constants.SERVICE_DESCRIPTION;
	@Property(value = "Girl Scouts")
	static final String VENDOR = Constants.SERVICE_VENDOR;
	@Property(value = "Girl Scouts Page Deletion Service")
	static final String LABEL = "process.label";
	private static Logger log = LoggerFactory.getLogger(GSPageDeletionServiceImpl.class);

	@Reference
	private ResourceResolverFactory resolverFactory;
	@Reference
	private RolloutManager rolloutManager;
	@Reference
	private SlingSettingsService settingsService;
	@Reference
	public GSEmailService gsEmailService;
	@Reference
	private LiveRelationshipManager relationManager;
	@Reference
	private PageReplicator pageReplicator;

	private ResourceResolver rr;

	@Activate
	private void activate(ComponentContext context) {
		try {
			rr = resolverFactory.getAdministrativeResourceResolver(null);
		} catch (LoginException e) {
			log.error("Girlscouts Page Deletion Service encountered error: ", e);
		}
		log.info("Girlscouts Page Deletion Service Activated.");
	}

	@Override
	public void delete(String path) {
		if (isPublisher()) {
			return;
		}
		try {
			// N second remorse wait time in case workflow needs to be stopped.
			Thread.sleep(DEFAULT_REMORSE_WAIT_TIME);
		} catch (InterruptedException e) {
			log.error("Girlscouts Page Deletion Service encountered error: ", e);
		}
		Session session = rr.adaptTo(Session.class);
		Resource dateRolloutRes = rr.resolve(path);
		if (!dateRolloutRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
			Node dateRolloutNode = dateRolloutRes.adaptTo(Node.class);
			String srcPath = "", templatePath = "";
			Boolean notify = false, delay = false, useTemplate = false;
			Set<String> councils = null;
			Set<String> notifyCouncils = new TreeSet<String>();
			try {
				dateRolloutNode.setProperty(PARAM_STATUS, STATUS_PROCESSING);
				session.save();
				try {
					srcPath = dateRolloutNode.getProperty(PARAM_SOURCE_PATH).getString();
				} catch (Exception e) {
					log.error("Girlscouts Page Deletion Service encountered error: ", e);
				}
				try {
					notify = dateRolloutNode.getProperty(PARAM_NOTIFY).getBoolean();
				} catch (Exception e) {
					log.error("Girlscouts Page Deletion Service encountered error: ", e);
				}
				try {
					delay = dateRolloutNode.getProperty(PARAM_DELAY).getBoolean();
				} catch (Exception e) {
					log.error("Girlscouts Page Deletion Service encountered error: ", e);
				}
				try {
					useTemplate = dateRolloutNode.getProperty(PARAM_USE_TEMPLATE).getBoolean();
				} catch (Exception e) {
					log.error("Girlscouts Page Deletion Service encountered error: ", e);
				}
				try {
					templatePath = dateRolloutNode.getProperty(PARAM_TEMPLATE_PATH).getString();
				} catch (Exception e) {
					log.error("Girlscouts Page Deletion Service encountered error: ", e);
				}
				if (useTemplate && (templatePath == null || templatePath.trim().length() == 0)) {
					log.error(
							"Girlscouts Page Deletion Service encountered error - Use Template checked, but no template provided. Cancelling.");
					PageActivationUtil.markReplicationFailed(session, dateRolloutNode);
					return;
				}
				councils = PageActivationUtil.getCouncils(dateRolloutNode);
				List<String> deletionLog = new ArrayList<String>();
				Resource srcRes = rr.resolve(srcPath);
				if (relationManager.isSource(srcRes)) {
					Page srcPage = (Page) srcRes.adaptTo(Page.class);
					if (srcPage != null) {
						Set<String> pagesToDelete = new HashSet<String>();
						markLiveRelationshipsForDeletion(councils, srcRes, pagesToDelete, deletionLog, notifyCouncils);
						if (!councils.isEmpty()) {
							notifyCouncils.addAll(councils);
							for (String council : councils) {
								log.error("Failed to process deletion for {} council", council);
							}
						}
						dateRolloutNode.setProperty(PARAM_PAGES_TO_DELETE, pagesToDelete.toArray(new String[pagesToDelete.size()]));
						dateRolloutNode.setProperty(PARAM_NOTIFY_COUNCILS,
								notifyCouncils.toArray(new String[notifyCouncils.size()]));
						session.save();
					} else {
						log.error("Resource is not a page. Quit. " + srcPath);
						PageActivationUtil.markReplicationFailed(session, dateRolloutNode);
						return;
					}
				}
			} catch (Exception e) {
				log.error("Girlscouts Page Deletion Service encountered error: ", e);
				return;
			}
		}
	}

	private void markLiveRelationshipsForDeletion(Set<String> councils, Resource srcRes, Set<String> pagesToDelete,
			List<String> deletionLog, Set<String> notifyCouncils) throws RepositoryException, WCMException {
		RangeIterator relationIterator = relationManager.getLiveRelationships(srcRes, null, null);
		while (relationIterator.hasNext()) {
			LiveRelationship relation = (LiveRelationship) relationIterator.next();
			String targetPath = relation.getTargetPath();
			int councilNameIndex = targetPath.indexOf("/", "/content/".length());
			String councilPath = targetPath.substring(0, councilNameIndex);
			if (councils.contains(councilPath)) {
				deletionLog.add("Attempting to queue for deletion: " + targetPath);
				Resource targetResource = rr.resolve(targetPath);
				if (targetResource != null
						&& !targetResource.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
					if (!isPageInheritanceBroken(targetResource, deletionLog)) {
						Set<String> components = getComponents(targetResource);
						boolean notifyCouncil = isComponentsInheritanceBroken(components, councilPath, deletionLog);
						if (notifyCouncil) {
							notifyCouncils.add(targetPath);
							deletionLog.add("Page " + targetPath + " was not added to deletion queue");
						} else {
							pagesToDelete.add(targetPath);
							deletionLog.add("Page " + targetPath + " added to deletion queue");
							councils.remove(councilPath);
						}
					} else {
						notifyCouncils.add(targetPath);
						deletionLog.add("The page has Break Inheritance checked off. Will not delete");
					}
				} else {
					notifyCouncils.add(targetPath);
					deletionLog.add("Resource " + targetPath + " not found.");
					deletionLog.add("Will NOT delete this page");
				}
			}
		}
	}

	private boolean isPageInheritanceBroken(Resource targetResource, List<String> deletionLog) {
		Boolean breakInheritance = false;
		try {
			ValueMap contentProps = ResourceUtil.getValueMap(targetResource);
			breakInheritance = contentProps.get(PARAM_BREAK_INHERITANCE, false);
			deletionLog.add("Girlscouts Deletion Service: Resource at " + targetResource.getPath()
					+ " has parameter breakInheritance = true");
		} catch (Exception e) {
			log.error("Girlscouts Deletion Service encountered error: ", e);
		}
		if (!breakInheritance) {
			Resource targetResourceContent = targetResource.getChild("jcr:content");
			try {
				ValueMap contentProps = ResourceUtil.getValueMap(targetResourceContent);
				breakInheritance = contentProps.get(PARAM_BREAK_INHERITANCE, false);
				deletionLog.add("Girlscouts Deletion Service: Resource at " + targetResourceContent.getPath()
						+ " has parameter breakInheritance = true");
			} catch (Exception e) {
				log.error("Girlscouts Deletion Service encountered error: ", e);
			}
		}
		return breakInheritance;
	}


	private boolean isComponentsInheritanceBroken(Set<String> components, String councilPath, List<String> rolloutLog) {
		boolean inheritanceBroken = false;
		if (components != null && components.size() > 0) {
			Set<String> brokenComponents = new HashSet<String>();
			for (String component : components) {
				if (!isInheritanceBroken(councilPath, component, rolloutLog)) {
					inheritanceBroken = true;
					brokenComponents.add(component);
					log.error(
							"Girlscouts Rollout Service: Council {} has broken inheritance with template component at {}. Removing from RolloutParams.",
							councilPath, component);
					rolloutLog.add("Girlscouts Rollout Service: Council " + councilPath
							+ " has broken inheritance with template component at " + component + ".");
				}
			}
			components.removeAll(brokenComponents);
		}
		return inheritanceBroken;
	}

	private boolean isInheritanceBroken(String councilPath, String component, List<String> rolloutLog) {
		Resource componentRes = rr.resolve(component);
		if (componentRes != null && !componentRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
			try {
				RangeIterator relationIterator = relationManager.getLiveRelationships(componentRes, null, null);
				boolean relationShipExists = false;
				while (relationIterator.hasNext()) {
					LiveRelationship relation = (LiveRelationship) relationIterator.next();
					String relationPath = relation.getTargetPath();
					if (relationPath.startsWith(councilPath)) {
						relationShipExists = true;
						Resource targetComponentRes = rr.resolve(relationPath);
						if (targetComponentRes != null
								&& !targetComponentRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
							try {
								Node componentNode = targetComponentRes.adaptTo(Node.class);
								if (componentNode != null) {
									NodeType[] mixinTypes = componentNode.getMixinNodeTypes();
									if (mixinTypes != null && mixinTypes.length > 0) {
										for (NodeType mixinType : mixinTypes) {
											if (mixinType.isNodeType(PARAM_LIVE_SYNC_CANCELLED)) {
												log.error(
														"Girlscouts Rollout Service: Component at {} has mixinType {}",
														relationPath, mixinType.getName());
												rolloutLog.add("Girlscouts Rollout Service: Component at "
														+ relationPath + " has mixinType " + mixinType.getName());
												return true;
											}
										}
									}
								}
							} catch (RepositoryException e) {
								log.error("Girlscouts Rollout Service encountered error: ", e);
							}
						} else {
							log.error("Girlscouts Rollout Service: Component at {} is not found.", relationPath);
							rolloutLog
									.add("Girlscouts Rollout Service: Component at " + relationPath + " is not found.");
							return true;
						}
					}
				}
				if (!relationShipExists) {
					log.error(
							"Girlscouts Rollout Service: Source Site Component {} does not have live sync relationship for {}.",
							component, councilPath);
					rolloutLog.add("Girlscouts Rollout Service: Council " + councilPath
							+ " does not have live sync relationship for " + component);
					return true;
				}
			} catch (WCMException e1) {
				log.error("Girlscouts Rollout Service encountered error: ", e1);
			}
		}
		return false;
	}

	private Set<String> getComponents(Resource srcRes) {
		Set<String> components = null;
		try {
			components = new HashSet<String>();
			Resource content = srcRes.getChild("jcr:content");
			traverseNodeForComponents(content, components);
		} catch (Exception e) {
			log.error("Girlscouts Deletion Service encountered error: ", e);
		}
		return components;
	}

	private void traverseNodeForComponents(Resource srcRes, Set<String> components) {
		if (srcRes != null) {
			components.add(srcRes.getPath());
			Iterable<Resource> children = srcRes.getChildren();
			if (children != null) {
				Iterator<Resource> it = children.iterator();
				while (it.hasNext()) {
					traverseNodeForComponents(it.next(), components);
				}
			}
		}
	}

	@Deactivate
	private void deactivate(ComponentContext componentContext) {
		log.info("Girlscouts Page Deletion Service Deactivated.");
	}

	private boolean isPublisher() {
		if (settingsService.getRunModes().contains("publish")) {
			return true;
		}
		return false;
	}

}
