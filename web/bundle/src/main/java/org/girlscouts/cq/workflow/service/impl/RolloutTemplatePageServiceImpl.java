package org.girlscouts.cq.workflow.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.Node;
import javax.jcr.RangeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Query;

import org.girlscouts.cq.workflow.service.RolloutTemplatePageService;
import org.girlscouts.web.components.GSEmailAttachment;
import org.girlscouts.web.components.PageReplicationUtil;
import org.girlscouts.web.constants.PageReplicationConstants;
import org.girlscouts.web.service.email.GSEmailService;
import org.girlscouts.web.service.replication.PageReplicator;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.cq.wcm.msm.api.RolloutConfig;
import com.day.cq.wcm.msm.api.RolloutConfigManager;
import com.day.cq.wcm.msm.api.RolloutManager;

import org.apache.sling.settings.SlingSettingsService;

import org.apache.sling.api.resource.ResourceResolver;

/*
 * Girl Scouts Page Activator - DL
 * This process activates a queue of pages, in batches, with a timed delay between batches
 */
@Component
@Service
public class RolloutTemplatePageServiceImpl implements RolloutTemplatePageService, PageReplicationConstants, PageReplicationConstants.Email {
	@Property(value = "Roll out a page if it is the source page of a live copy, and then activate target pages.")
	static final String DESCRIPTION = Constants.SERVICE_DESCRIPTION;
	@Property(value = "Girl Scouts")
	static final String VENDOR = Constants.SERVICE_VENDOR;
	@Property(value = "Girl Scouts Roll out Service")
	static final String LABEL = "process.label";
	private static Logger log = LoggerFactory.getLogger(RolloutTemplatePageServiceImpl.class);
	
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
	
	private Map<String, Object> serviceParams;

	@Activate
	private void activate(ComponentContext context) {
		serviceParams = new HashMap<String, Object>();
		serviceParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-process-service");
		log.info("Girlscouts Rollout Service Activated.");
	}

	@Override
	public void rollout(String path) {
		if (isPublisher()) {
			return;
		}
		try {
			// N second remorse wait time in case workflow needs to be stopped.
			Thread.sleep(DEFAULT_REMORSE_WAIT_TIME);
		} catch (InterruptedException e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
		}
		ResourceResolver rr = null;
		try {
			rr = resolverFactory.getServiceResourceResolver(serviceParams);
			Session session = rr.adaptTo(Session.class);
			Resource dateRolloutRes = rr.resolve(path);
			if (!dateRolloutRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
				Node dateRolloutNode = dateRolloutRes.adaptTo(Node.class);
				String srcPath = "", templatePath = "";
				Boolean notify = false, activate = false, delay = false, useTemplate = false, newPage = false;
				Set<String> councils = null;
				Set<String> notifyCouncils = new TreeSet<String>();
				List<String> rolloutLog = new ArrayList<String>();
				try {
					dateRolloutNode.setProperty(PARAM_STATUS, STATUS_PROCESSING);
					session.save();
					try {
						srcPath = dateRolloutNode.getProperty(PARAM_SOURCE_PATH).getString();
					} catch (Exception e) {
						log.error("Girlscouts Rollout Service encountered error: ", e);
					}
					try {
						newPage = dateRolloutNode.getProperty(PARAM_NEW_PAGE).getBoolean();
					} catch (Exception e) {
						log.error("Girlscouts Rollout Service encountered error: ", e);
					}
					try {
						notify = dateRolloutNode.getProperty(PARAM_NOTIFY).getBoolean();
					} catch (Exception e) {
						log.error("Girlscouts Rollout Service encountered error: ", e);
					}
					try {
						activate = dateRolloutNode.getProperty(PARAM_ACTIVATE).getBoolean();
					} catch (Exception e) {
						log.error("Girlscouts Rollout Service encountered error: ", e);
					}
					try {
						delay = dateRolloutNode.getProperty(PARAM_DELAY).getBoolean();
					} catch (Exception e) {
						log.error("Girlscouts Rollout Service encountered error: ", e);
					}
					try {
						useTemplate = dateRolloutNode.getProperty(PARAM_USE_TEMPLATE).getBoolean();
					} catch (Exception e) {
						log.error("Girlscouts Rollout Service encountered error: ", e);
					}
					try {
						templatePath = dateRolloutNode.getProperty(PARAM_TEMPLATE_PATH).getString();
					} catch (Exception e) {
						log.error("Girlscouts Rollout Service encountered error: ", e);
					}
					if (useTemplate && (templatePath == null || templatePath.trim().length() == 0)) {
						log.error("Rollout Error - Use Template checked, but no template provided. Cancelling.");
						PageReplicationUtil.markReplicationFailed(dateRolloutNode);
						return;
					}
					councils = PageReplicationUtil.getCouncils(dateRolloutNode);
					Resource srcRes = rr.resolve(srcPath);
					if (relationManager.isSource(srcRes)) {
						Page srcPage = (Page) srcRes.adaptTo(Page.class);
						if (srcPage != null) {
							Set<String> pages = new HashSet<String>();
							if (newPage) {
								processNewLiveRelationships(councils, srcRes, pages, rolloutLog, notifyCouncils, rr);
							} else {
								processExistingLiveRelationships(councils, srcRes, pages, rolloutLog, notifyCouncils,
										rr);
							}

							if (!councils.isEmpty()) {
								int councilNameIndex = srcPath.indexOf("/", "/content/".length());
								String srcRelativePath = srcPath.substring(councilNameIndex);
								for (String council : councils) {
									notifyCouncils.add(council + srcRelativePath);
									log.error("Failed to rollout for {} council", council);
									rolloutLog.add("Failed to rollout for " + council + " council");
								}
							}
							dateRolloutNode.setProperty(PARAM_PAGES, pages.toArray(new String[pages.size()]));
							dateRolloutNode.setProperty(PARAM_NOTIFY_COUNCILS,
									notifyCouncils.toArray(new String[notifyCouncils.size()]));
							session.save();
						} else {
							log.error("Resource is not a page. Quit. " + srcPath);
							PageReplicationUtil.markReplicationFailed(dateRolloutNode);
							return;
						}
					} else {
						log.error("Not a live copy source page. Quit. " + srcPath);
						PageReplicationUtil.markReplicationFailed(dateRolloutNode);
						return;
					}
				} catch (Exception e) {
					log.error("Girlscouts Rollout Service encountered error: ", e);
					PageReplicationUtil.markReplicationFailed(dateRolloutNode);
					return;
				}
				List<String> councilNotificationLog = new ArrayList<String>();
				Boolean isTestMode = PageReplicationUtil.isTestMode(rr);
				try {
					if (notify && notifyCouncils.size() > 0) {
						sendCouncilNotifications(dateRolloutNode, councilNotificationLog, isTestMode, rr);
					} else {
						dateRolloutNode.setProperty(PARAM_COUNCIL_NOTIFICATIONS_SENT, Boolean.FALSE);
					}
				} catch (Exception e) {
					log.error("Girlscouts Rollout Service encountered error: ", e);
				}
				try {
					sendGSUSANotifications(dateRolloutNode, rolloutLog, councilNotificationLog, isTestMode, rr);
				} catch (Exception e) {
					log.error("Girlscouts Rollout Service encountered error: ", e);
				}
				try {
					if (activate) {
						log.info("Girlscouts Rollout Service: Processing Activations");
						if (delay) {
							log.info("Girlscouts Rollout Service: Activations are delayed");
							dateRolloutNode.setProperty(PARAM_STATUS, STATUS_DELAYED);
							session.save();
						} else {
							log.info("Girlscouts Rollout Service: Activations are instant, running pageReplicator");
							pageReplicator.processReplicationNode(dateRolloutNode, rr);
						}
					} else {
						log.info("Girlscouts Rollout Service: Activation is not requested, job {} completed.",
								dateRolloutNode.getName());
						dateRolloutNode.setProperty(PARAM_STATUS, STATUS_COMPLETE);
						PageReplicationUtil.archive(dateRolloutNode);
						log.info("Girlscouts Rollout Service: Rollout is archived at {}.", dateRolloutNode.getPath());
					}
				} catch (Exception e) {
					PageReplicationUtil.markReplicationFailed(dateRolloutNode);
					log.error("Girlscouts Rollout Service encountered error: ", e);
				}
			}
		} catch (LoginException e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
		} finally {
			try {
				rr.close();
			} catch (Exception e) {

			}
		}
	}

	private boolean isPublisher() {
		if (settingsService.getRunModes().contains("publish")) {
			return true;
		}
		return false;
	}

	@Deactivate
	private void deactivate(ComponentContext componentContext) {
		log.info("GS Page Rollout Service Deactivated.");
	}

	private void processNewLiveRelationships(Set<String> councils, Resource srcRes, Set<String> pagesToActivate,
			List<String> rolloutLog, Set<String> notifyCouncils, ResourceResolver rr)
			throws RepositoryException, WCMException {
		log.info("Girlscouts Rollout Service: Processing new live relationships.");
		Session session = rr.adaptTo(Session.class);
		RangeIterator relationIterator = relationManager.getLiveRelationships(srcRes.getParent(), null, null);
		while (relationIterator.hasNext()) {
			try {
				LiveRelationship parentRelation = (LiveRelationship) relationIterator.next();
				String parentPath = parentRelation.getTargetPath();
				int councilNameIndex = parentPath.indexOf("/", "/content/".length());
				String councilPath = parentPath.substring(0, councilNameIndex);
				if (councils.contains(councilPath)) {
					councils.remove(councilPath);
					rolloutLog.add("Attempting to roll out a child page of: " + parentPath);
					Resource parentResource = rr.resolve(parentPath);
					if (parentResource != null
							&& !parentResource.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
						PageManager pageManager = rr.adaptTo(PageManager.class);
						Page srcPage = (Page) srcRes.adaptTo(Page.class);
						Page copyPage = pageManager.copy(srcPage, parentPath + "/" + srcPage.getName(),
								srcPage.getName(), false, true);
						RolloutConfigManager configMgr = (RolloutConfigManager) rr.adaptTo(RolloutConfigManager.class);
						RolloutConfig gsConfig = configMgr.getRolloutConfig(GS_ROLLOUT_CONFIG);
						LiveRelationship newPageRelationship = relationManager.establishRelationship(srcPage, copyPage,
								true, false, gsConfig);
						String targetPath = newPageRelationship.getTargetPath();
						cancelInheritance(rr, copyPage.getPath());
						rolloutManager.rollout(rr, newPageRelationship, false);
						session.save();
						rolloutLog.add("Page " + copyPage.getPath() + " created");
						rolloutLog.add("Live copy established");
						if (targetPath.endsWith("/jcr:content")) {
							targetPath = targetPath.substring(0, targetPath.lastIndexOf('/'));
						}
						pagesToActivate.add(targetPath);
						rolloutLog.add("Page added to activation/cache build queue");
					} else {
						rolloutLog.add(
								"No resource can be found to serve as a suitable parent page. In order to roll out to this council, you must roll out the parent of this template page first.");
						rolloutLog.add("Will NOT rollout to this council");
						notifyCouncils.add(parentPath);
					}
				}
			} catch (Exception e) {
				log.error("Girlscouts Rollout Service encountered error: ", e);
			}
		}

	}

	private void processExistingLiveRelationships(Set<String> submittedCouncils, Resource sourcePageResource, Set<String> pagesToActivate,
			List<String> rolloutLog, Set<String> notifyCouncils, ResourceResolver rr)
			throws RepositoryException, WCMException {
		log.info("Girlscouts Rollout Service: Processing existing live relationships.");
		Set<String> srcComponents = getComponents(sourcePageResource);
		Map<String, Map<String, String>> sourceComponentRelationsByCouncil = getComponentRelationsByCouncil(
				sourcePageResource, srcComponents, rolloutLog, rr);
		Set<String> processedRelationCouncils = new HashSet<String>();
		for (String councilPath : submittedCouncils) {
			log.info("Girlscouts Rollout Service: Looking up live relationships in {}", councilPath);
			RangeIterator relationsIterator = relationManager.getLiveRelationships(sourcePageResource, councilPath,
					null);
			while (relationsIterator.hasNext()) {
				try {
					LiveRelationship relation = (LiveRelationship) relationsIterator.next();
					String relationPagePath = relation.getTargetPath();
					rolloutLog.add("Attempting to roll out to: " + relationPagePath);
					Resource relationPageResource = rr.resolve(relationPagePath);
					log.info("Girlscouts Rollout Service: Attempting to roll out to: {}", relationPagePath);
					if (relationPageResource != null
							&& !relationPageResource.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
						processedRelationCouncils.add(councilPath);
						if (isPageInheritanceBroken(relationPageResource, rolloutLog)) {
							notifyCouncils.add(relationPagePath);
							rolloutLog.add("The page " + relationPagePath
									+ " has Break Inheritance checked off. Will not roll out");
							log.info(
									"Girlscouts Rollout Service: The page {} has Break Inheritance checked off. Will not roll out",
									relationPagePath);
						} else {
							validateRolloutConfig(sourcePageResource, relationPageResource);
							Map<String, Set<String>> relationComponents = categorizeRelationComponents(
									relationPageResource, rolloutLog, rr);
							Set<String> componentsToDelete = getComponentsToDelete(sourceComponentRelationsByCouncil,
									relationComponents, councilPath);
							Set<String> componentsToRollout = getComponentsToRollout(sourceComponentRelationsByCouncil,
									relationComponents, srcComponents, councilPath);
							if (relationComponents.get(RELATION_CANC_INHERITANCE_COMPONENTS).size() > 0) {
								notifyCouncils.add(relationPagePath);
							}
							log.info("Girlscouts Rollout Service: Deleting components for {}, nodes {}.",
									relationPagePath, componentsToDelete);
							deleteComponents(rr, rolloutLog, componentsToDelete);
							rolloutLog.add("Deleted components at " + relationPagePath);
							log.info("Girlscouts Rollout Service: Deleted components for {}.", relationPagePath);
							rolloutComponents(sourcePageResource, rolloutLog, relationPagePath, componentsToRollout);
							updatePageTitle(sourcePageResource, relationPageResource);
							pagesToActivate.add(relationPagePath);
							rolloutLog.add("Page added to activation queue");
							log.info("Girlscouts Rollout Service: Page added to activation queue");
						}
					} else {
						log.info("Girlscouts Rollout Service: Resource {} not found.", relationPagePath);
						log.info("Girlscouts Rollout Service: Will NOT rollout to this page");
						rolloutLog.add("Resource " + relationPagePath + " not found.");
						rolloutLog.add("Will NOT rollout to this page");
					}
				} catch (Exception e) {
					log.error("Girlscouts Rollout Service encountered error: ", e);
				}
			}
		}
		submittedCouncils.removeAll(processedRelationCouncils);
	}

	private void deleteComponents(ResourceResolver rr, List<String> rolloutLog, Set<String> componentsToDelete) {
		for (String component : componentsToDelete) {
			log.info("Girlscouts Rollout Service: Deleting node {}", component);
			try {
				rr.delete(rr.resolve(component));
				rr.commit();
				log.info("Girlscouts Rollout Service: Node {} deleted successfully", component);
			} catch (PersistenceException e) {
				log.error("Girlscouts Rollout Service encountered error: Unable to delete {} ", component, e);
			}
		}
	}

	private void rolloutComponents(Resource srcRes, List<String> rolloutLog, String relationPath,
			Set<String> componentsToRollout) throws WCMException {
		log.info("Girlscouts Rollout Service: Rolling out content for {}, components {}.", relationPath,
				componentsToRollout);
		RolloutManager.RolloutParams params = new RolloutManager.RolloutParams();
		params.isDeep = false;
		params.master = srcRes.adaptTo(Page.class);
		params.targets = new String[] { relationPath };
		params.paragraphs = componentsToRollout.toArray(new String[componentsToRollout.size()]);
		params.trigger = RolloutManager.Trigger.ROLLOUT;
		params.reset = false;
		rolloutManager.rollout(params);
		rolloutLog.add("Rolled out content to " + relationPath);
		log.info("Girlscouts Rollout Service: Successfully rolled out content for {}.", relationPath);
	}

	private Map<String, Map<String, String>> getComponentRelationsByCouncil(Resource srcRes, Set<String> srcComponents,
			List<String> rolloutLog, ResourceResolver rr) {
		log.info("Girlscouts Rollout Service: Categorizing component relations by councilfor {}.", srcRes.getPath());
		final LiveRelationshipManager relationManager = rr.adaptTo(LiveRelationshipManager.class);
		Map<String, Map<String, String>> relationByCouncil = new HashMap<String, Map<String, String>>();
		if (srcComponents != null && srcComponents.size() > 0) {
			for (String srcComponent : srcComponents) {
				Resource componentRes = rr.resolve(srcComponent);
				if (componentRes != null
						&& !componentRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
					log.info("Girlscouts Rollout Service: Looking up relations for {}.", componentRes);
					rolloutLog.add("Girlscouts Rollout Service: Looking up relations for " + componentRes + ".");
					try {
						RangeIterator relationIterator = relationManager.getLiveRelationships(componentRes, null, null);
						if (relationIterator.hasNext()) {
							while (relationIterator.hasNext()) {
								LiveRelationship relation = (LiveRelationship) relationIterator.next();
								String relationPath = relation.getTargetPath();
								String councilPath = PageReplicationUtil.getCouncilName(relationPath);
								if (relationByCouncil.containsKey(councilPath)) {
									relationByCouncil.get(councilPath).put(srcComponent, relationPath);
								} else {
									Map<String, String> componentRelations = new HashMap<String, String>();
									componentRelations.put(srcComponent, relationPath);
									relationByCouncil.put(councilPath, componentRelations);
								}
							}
						}
					} catch (Exception e) {
						log.error("Girlscouts Rollout Service encountered error: ", e);
					}
				}
			}
		}
		return relationByCouncil;
	}

	private Set<String> getComponentsToDelete(
			Map<String, Map<String, String>> sourceComponentRelationsByCouncil,
			Map<String, Set<String>> relationComponentsMap, String relationCouncilPath) {
		log.info("Girlscouts Rollout Service: Looking up inherited components for {} that need to be deleted.",
				relationCouncilPath);
		Set<String> componentsToDelete = new HashSet<String>();
		Set<String> inheritedComponents = relationComponentsMap.get(RELATION_INHERITED_COMPONENTS);
		Map<String, String> componentRelations = sourceComponentRelationsByCouncil.get(relationCouncilPath);
		for (String inheritedComponent : inheritedComponents) {
			if (!componentRelations.containsValue(inheritedComponent)) {
				log.info(
						"Girlscouts Rollout Service: Inherited Component {} exist on council site, but not on source site. Qualifies to be deleted ",
						inheritedComponent);
				componentsToDelete.add(inheritedComponent);
			} else {
				log.info(
						"Girlscouts Rollout Service: Component {} doesn't need to be deleted.",
						inheritedComponent);
			}
		}
		return componentsToDelete;
	}

	private Set<String> getComponentsToRollout(
			Map<String, Map<String, String>> sourceComponentRelationsByCouncil,
			Map<String, Set<String>> relationComponentsMap, Set<String> srcComponents, String relationCouncilPath) {
		Set<String> componentsToRollout = new HashSet<String>();
		Set<String> cancelledInheritanceComponents = relationComponentsMap.get(RELATION_CANC_INHERITANCE_COMPONENTS);
		Map<String, String> componentRelationsForCouncil = sourceComponentRelationsByCouncil.get(relationCouncilPath);
		for (String srcComponent : srcComponents) {
			String relatedComponent = componentRelationsForCouncil.get(srcComponent);
			if (relatedComponent == null
					|| (relatedComponent != null && !cancelledInheritanceComponents.contains(relatedComponent))) {
				log.info("Girlscouts Rollout Service: Including component {} in rollout for {}.", srcComponent,
						relationCouncilPath);
				componentsToRollout.add(srcComponent);
			}
		}
		return componentsToRollout;
	}

	private Map<String, Set<String>> categorizeRelationComponents(Resource relationPageResource, List<String> rolloutLog,
			ResourceResolver rr) {
		log.info("Girlscouts Rollout Service: Categorizing components for {}.", relationPageResource.getPath());
		Map<String, Set<String>> relationComponents = new HashMap<String, Set<String>>();
		Set<String> customComponents = new HashSet<String>();
		Set<String> activeLiveSyncComponents = new HashSet<String>();
		Set<String> inactiveLiveSyncComponents = new HashSet<String>();
		categorizeRelationComponents(customComponents, activeLiveSyncComponents, inactiveLiveSyncComponents,
				relationPageResource);
		relationComponents.put(RELATION_CUSTOM_COMPONENTS, customComponents);
		relationComponents.put(RELATION_INHERITED_COMPONENTS, activeLiveSyncComponents);
		relationComponents.put(RELATION_CANC_INHERITANCE_COMPONENTS, inactiveLiveSyncComponents);
		return relationComponents;
	}

	private void categorizeRelationComponents(Set<String> customComponents, Set<String> activeLiveSyncComponents,
			Set<String> inactiveLiveSyncComponents, Resource resource) {
		if (resource.hasChildren()) {
			Iterator<Resource> it = resource.getChildren().iterator();
			while (it.hasNext()) {
				Resource childResource = it.next();
				log.info("Girlscouts Rollout Service: Categorizing {}.", childResource.getPath());
				try {
					if (childResource.isResourceType("wcm/msm/components/ghost")) {
						log.info("Girlscouts Rollout Service: Component {} is a deleted inherited component.",
								childResource.getPath());
						inactiveLiveSyncComponents.add(childResource.getPath());
					} else {
						Node childNode = childResource.adaptTo(Node.class);
						if (childNode.isNodeType(PARAM_LIVE_SYNC_CANCELLED)) {
							log.info("Girlscouts Rollout Service: Component {} has cancelled inheritance.",
									childResource.getPath());
							inactiveLiveSyncComponents.add(childResource.getPath());
						} else {
							if (childNode.isNodeType(PARAM_LIVE_SYNC)) {
								log.info("Girlscouts Rollout Service: Component {} is inherited.",
										childResource.getPath());
								activeLiveSyncComponents.add(childResource.getPath());
							} else {
								log.info("Girlscouts Rollout Service: Component {} is custom.",
										childResource.getPath());
								customComponents.add(childResource.getPath());
							}
						}
					}
					categorizeRelationComponents(customComponents, activeLiveSyncComponents, inactiveLiveSyncComponents,
							childResource);
				} catch (Exception e) {
					log.error("Girlscouts Rollout Service encountered error: ", e);
				}
			}
		}
	}

	private void updatePageTitle(Resource srcRes, Resource targetResource) {
		try {
			Resource srcContent = srcRes.getChild("jcr:content");
			Node srcContentNode = srcContent.adaptTo(Node.class);
			Resource targetContent = targetResource.getChild("jcr:content");
			Node targetContentNode = targetContent.adaptTo(Node.class);
			String targetTitle = srcContentNode.getProperty("jcr:title").getString();
			String sourceTitle = srcContentNode.getProperty("jcr:title").getString();
			if (!sourceTitle.equals(targetTitle)) {
				log.info("Girlscouts Rollout Service: Updating page title of {} to {}.", targetResource.getPath(),
						sourceTitle);
				targetContentNode.setProperty("jcr:title", sourceTitle);
				targetContentNode.getSession().save();
			}
		} catch (Exception e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
		}
	}

	private boolean isPageInheritanceBroken(Resource targetResource, List<String> rolloutLog)
			throws RepositoryException {
		log.info("Girlscouts Rollout Service: Checking resource at {} for broken inheritance ",
				targetResource.getPath());
		Boolean breakInheritance = false;
		try {
			ValueMap contentProps = ResourceUtil.getValueMap(targetResource);
			breakInheritance = contentProps.get(PARAM_BREAK_INHERITANCE, false);
		} catch (Exception e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
		}
		if (breakInheritance) {
			log.info("Girlscouts Rollout Service: Resource at {} has parameter breakInheritance = {}",
					targetResource.getPath(), breakInheritance);
			rolloutLog.add("Girlscouts Rollout Service: Resource at " + targetResource.getPath()
					+ " has parameter breakInheritance = " + breakInheritance);
		} else {
			Node node = targetResource.adaptTo(Node.class);
			if (node.isNodeType(PARAM_LIVE_SYNC_CANCELLED)) {
				log.info("Girlscouts Rollout Service: Resource at {} has mixin type cq:LiveSyncCancelled",
						targetResource.getPath());
				rolloutLog.add("Girlscouts Rollout Service: Resource at " + targetResource.getPath()
						+ " has mixin type cq:LiveSyncCancelled");
				breakInheritance = true;
			} else {
				Resource targetResourceContent = targetResource.getChild("jcr:content");
				try {
					ValueMap contentProps = ResourceUtil.getValueMap(targetResourceContent);
					breakInheritance = contentProps.get(PARAM_BREAK_INHERITANCE, false);
					if (breakInheritance) {
						log.info("Girlscouts Rollout Service: Resource at {} has parameter breakInheritance = {}",
								targetResourceContent.getPath(), breakInheritance);
						rolloutLog.add("Girlscouts Rollout Service: Resource at " + targetResourceContent.getPath()
								+ " has parameter breakInheritance = " + breakInheritance);
					} else {
						Node contentNode = targetResourceContent.adaptTo(Node.class);
						if (contentNode.isNodeType(PARAM_LIVE_SYNC_CANCELLED)) {
							log.info("Girlscouts Rollout Service: Resource at {} has mixin type cq:LiveSyncCancelled",
									targetResourceContent.getPath());
							rolloutLog.add("Girlscouts Rollout Service: Resource at " + targetResourceContent.getPath()
									+ " has mixin type cq:LiveSyncCancelled");
							breakInheritance = true;
						}
					}
				} catch (Exception e) {
					log.error("Girlscouts Rollout Service encountered error: ", e);
				}
			}
		}
		if (breakInheritance) {
			log.info("Girlscouts Rollout Service: Resource at {} has broken inheritance ", targetResource.getPath());
		} else {
			log.info("Girlscouts Rollout Service: Resource at {} is inherited ", targetResource.getPath());
		}
		return breakInheritance;
	}

	private void validateRolloutConfig(Resource srcRes, Resource targetResource) {
		log.info("Girlscouts Rollout Service : Validating rollout config for {}", targetResource.getPath());
		try {
			Node page = targetResource.adaptTo(Node.class);
			Node srcPage = srcRes.adaptTo(Node.class);
			if (!isGSRolloutConfig(page)) {
				log.info("Girlscouts Rollout Service : Setting girlscouts rollout config for {}",
						targetResource.getPath());
				Node liveSyncNode = null;
				if (getLiveSyncNode(page) != null) {
					liveSyncNode = getLiveSyncNode(page);
				} else {
					liveSyncNode = createLiveSyncNode(page, srcPage);
				}
				liveSyncNode.setProperty(PARAM_CQ_MASTER, srcPage.getPath());
				liveSyncNode.setProperty(PARAM_CQ_IS_DEEP, Boolean.TRUE);
				liveSyncNode.setProperty(PARAM_CQ_ROLLOUT_CONFIG, new String[] { GS_ROLLOUT_CONFIG });
				liveSyncNode.getSession().save();

			}
		} catch (RepositoryException e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
		}
	}

	private Node createLiveSyncNode(Node page, Node srcPage) {
		try {
			Node contentNode = page.getNode("jcr:content");
			if (contentNode != null) {
				Node liveSyncNode = contentNode.addNode("cq:LiveSyncConfig");
				liveSyncNode.setPrimaryType(PARAM_CQ_LIVE_SYNC_CONFIG);
				return liveSyncNode;
			}
		} catch (RepositoryException e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
		}
		return null;
	}

	private Set<String> getComponents(Resource srcRes) {
		log.info("Girlscouts Rollout Service : Gathering all components under {}", srcRes.getPath());
		Set<String> components = null;
		try {
			components = new HashSet<String>();
			Resource content = srcRes.getChild("jcr:content");
			traverseNodeForComponents(content, components);
		} catch (Exception e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
		}
		return components;
	}

	private void traverseNodeForComponents(Resource srcRes, Set<String> components) {
		log.info("Girlscouts Rollout Service : traversing {}", srcRes.getPath());
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

	/**
	 * cancel the Inheritance for certain components (nodes with mixin type
	 * "cq:LiveSyncCancelled" under national template page)
	 */
	private void cancelInheritance(ResourceResolver rr, String councilPath) {
		try {
			String sql = "SELECT * FROM [cq:LiveSyncCancelled] AS s WHERE ISDESCENDANTNODE(s, '" + councilPath + "')";
			log.error("Girlscouts Rollout Service SQL2: " + sql);
			for (Iterator<Resource> it = rr.findResources(sql, Query.JCR_SQL2); it.hasNext();) {
				Resource target = it.next();
				log.error("Girlscouts Rollout Service : End relationship " + target.getPath());
				relationManager.endRelationship(target, true);
			}
		} catch (Exception e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
		}
	}

	private void sendGSUSANotifications(Node dateRolloutNode, List<String> rolloutLog,
			List<String> councilNotificationLog, Boolean isTestMode, ResourceResolver rr) {
		try {
			log.info("Girlscouts Rollout Service : Sending GSUSA notifications for {} rollout. isTestMode={}",
					dateRolloutNode.getPath(), isTestMode);
			Set<String> councils = null;
			String councilNotificationSubject = DEFAULT_ROLLOUT_NOTIFICATION_SUBJECT;
			StringBuffer html = new StringBuffer();
			html.append(DEFAULT_REPORT_HEAD);
			html.append("<body>");
			html.append("<p>" + DEFAULT_ROLLOUT_REPORT_SUBJECT + "</p>");
			html.append("<p>" + DEFAULT_REPORT_GREETING + "</p>");
			html.append("<p>" + DEFAULT_REPORT_INTRO + "</p>");
			Date runtime = new Date();
			html.append("<p>The workflow was run on " + runtime.toString() + ".</p>");
			String message = "", templatePath = "", srcPath = "";
			Boolean notify = false, useTemplate = false, delay = false, activate = true;

			try {
				notify = dateRolloutNode.getProperty(PARAM_NOTIFY).getBoolean();
			} catch (Exception e) {
				log.error("Girlscouts Rollout Service encountered error: ", e);
			}
			try {
				activate = dateRolloutNode.getProperty(PARAM_ACTIVATE).getBoolean();
			} catch (Exception e) {
				log.error("Girlscouts Rollout Service encountered error: ", e);
			}
			try {
				delay = dateRolloutNode.getProperty(PARAM_DELAY).getBoolean();
			} catch (Exception e) {
				log.error("Girlscouts Rollout Service encountered error: ", e);
			}
			try {
				useTemplate = dateRolloutNode.getProperty(PARAM_USE_TEMPLATE).getBoolean();
			} catch (Exception e) {
				log.error("Girlscouts Rollout Service encountered error: ", e);
			}
			try {
				if (useTemplate) {
					councilNotificationSubject = PageReplicationUtil.getTemplateSubject(templatePath, rr);
				} else {
					councilNotificationSubject = dateRolloutNode.getProperty(PARAM_EMAIL_SUBJECT).getString();
				}
			} catch (Exception e) {
				log.error("Girlscouts Rollout Service encountered error: ", e);
			}
			try {
				if (useTemplate) {
					message = PageReplicationUtil.getTemplateMessage(templatePath, rr);
				} else {
					message = dateRolloutNode.getProperty(PARAM_EMAIL_MESSAGE).getString();
				}
			} catch (Exception e) {
				log.error("Girlscouts Rollout Service encountered error: ", e);
			}
			try {
				templatePath = dateRolloutNode.getProperty(PARAM_TEMPLATE_PATH).getString();
			} catch (Exception e) {
				log.error("Girlscouts Rollout Service encountered error: ", e);
			}
			try {
				srcPath = dateRolloutNode.getProperty(PARAM_SOURCE_PATH).getString();
			} catch (Exception e) {
				log.error("Girlscouts Rollout Service encountered error: ", e);
			}
			try {
				councils = PageReplicationUtil.getCouncils(dateRolloutNode);
			} catch (Exception e) {
				log.error("Girlscouts Rollout Service encountered error: ", e);
				return;
			}
			html.append("<p>This workflow will " + (notify ? "" : "not ") + "send emails to councils.</p>");
			html.append("<p>This workflow will " + (activate ? "" : "not ") + "activate pages upon completion.</p>");
			if (activate) {
				html.append("<p>This workflow will " + (delay ? "" : "not ")
						+ "delay the page activations until tonight.</p>");
			}
			if (useTemplate) {
				html.append("<p>An email template is in use. The path to the template is " + templatePath + "</p>");
			}
			html.append("<p>The email subject is " + councilNotificationSubject + "</p>");
			html.append("<p>The email message is: " + message + "</p>");
			html.append("<p>The following councils have been selected:</p>");
			for (String council : councils) {
				html.append("<p>" + council + "</p>");
			}
			html.append("<p><b>Processing:</b><br>Source Page:" + srcPath + "</p>");
			for (String log : rolloutLog) {
				html.append("<p>" + log + "</p>");
			}

			StringBuffer logData = new StringBuffer();
			logData.append("The following is the process log for council notifications:\n\n");
			if (councilNotificationLog != null && !councilNotificationLog.isEmpty()) {
				for (String log : councilNotificationLog) {
					logData.append(log + "\n");
				}
			}
			List<String> gsusaEmails = PageReplicationUtil.getGsusaEmails(rr);
			Set<GSEmailAttachment> attachments = new HashSet<GSEmailAttachment>();
			String fileName = DEFAULT_ROLLOUT_REPORT_ATTACHMENT + "_" + dateRolloutNode.getName();
			GSEmailAttachment attachment = new GSEmailAttachment(fileName, logData.toString(), null,
					GSEmailAttachment.MimeType.TEXT_PLAIN);
			attachments.add(attachment);
			String reportSubject = "(" + PageReplicationUtil.getEnvironment(rr) + ") " + DEFAULT_ROLLOUT_REPORT_SUBJECT;
			if (isTestMode) {
				gsusaEmails = PageReplicationUtil.getReportEmails(rr);
				gsEmailService.sendEmail(reportSubject, gsusaEmails, html.toString(), attachments);
			} else {
				gsEmailService.sendEmail(reportSubject, gsusaEmails, html.toString(), attachments);
			}
		} catch (Exception e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
		}
	}

	private void sendCouncilNotifications(Node dateRolloutNode, List<String> councilNotificationLog, Boolean isTestMode,
			ResourceResolver rr) {
		Set<String> notifyCouncils = new TreeSet<String>();
		String subject = DEFAULT_ROLLOUT_NOTIFICATION_SUBJECT;
		String message = DEFAULT_ROLLOUT_NOTIFICATION_MESSAGE, templatePath = "", srcPath = "";
		Boolean notify = false, useTemplate = false;
		try {
			log.info("Girlscouts Rollout Service : Sending Council notifications for {} rollout. isTestMode={}",
					dateRolloutNode.getPath(), isTestMode);
			notify = dateRolloutNode.getProperty(PARAM_NOTIFY).getBoolean();
			if (notify) {
				try {
					useTemplate = dateRolloutNode.getProperty(PARAM_USE_TEMPLATE).getBoolean();
				} catch (Exception e) {
					log.error("Girlscouts Rollout Service encountered error: ", e);
				}
				try {
					if (useTemplate) {
						subject = PageReplicationUtil.getTemplateSubject(templatePath, rr);
					} else {
						subject = dateRolloutNode.getProperty(PARAM_EMAIL_SUBJECT).getString();
					}
				} catch (Exception e) {
					log.error("Girlscouts Rollout Service encountered error: ", e);
				}
				try {
					if (useTemplate) {
						message = PageReplicationUtil.getTemplateMessage(templatePath, rr);
					} else {
						message = dateRolloutNode.getProperty(PARAM_EMAIL_MESSAGE).getString();
					}
				} catch (Exception e) {
					log.error("Girlscouts Rollout Service encountered error: ", e);
				}
				try {
					templatePath = dateRolloutNode.getProperty(PARAM_TEMPLATE_PATH).getString();
				} catch (Exception e) {
					log.error("Girlscouts Rollout Service encountered error: ", e);
				}
				try {
					srcPath = dateRolloutNode.getProperty(PARAM_SOURCE_PATH).getString();
				} catch (Exception e) {
					log.error("Girlscouts Rollout Service encountered error: ", e);
				}
				try {
					notifyCouncils.addAll(PageReplicationUtil.getNotifyCouncils(dateRolloutNode));
				} catch (Exception e) {
					log.error("Girlscouts Rollout Service encountered error: ", e);
					return;
				}
				Resource source = rr.resolve(srcPath);
				if (!source.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
					if (notifyCouncils != null && !notifyCouncils.isEmpty()) {
						for (String targetPath : notifyCouncils) {
							if (message != null && message.trim().length() > 0) {
								String pathToCouncilSite = null;
								List<String> toAddresses = null;
								try {
									pathToCouncilSite = PageReplicationUtil.getCouncilName(targetPath);
									// get the email addresses configured in
									// page properties of the council's homepage
									Page homepage = rr.resolve(pathToCouncilSite + "/en").adaptTo(Page.class);
									toAddresses = PageReplicationUtil.getCouncilEmails(homepage.adaptTo(Node.class));
									log.error("Girlscouts Rollout Service: sending email to " + pathToCouncilSite.substring(9)
											+ " emails:" + toAddresses.toString());
									String body = PageReplicationUtil.generateCouncilNotification(srcPath, targetPath,
											message, rr, settingsService);
									if (isTestMode) {
										councilNotificationLog.add("Notification is running in test mode!");
										councilNotificationLog.add("Replacing " + toAddresses.toString());
										toAddresses = PageReplicationUtil.getReportEmails(rr);
										councilNotificationLog.add("with " + toAddresses.toString());
										gsEmailService.sendEmail(subject, toAddresses, body);
									} else {
										gsEmailService.sendEmail(subject, toAddresses, body);
									}
									councilNotificationLog.add("Notification for " + pathToCouncilSite.substring(9)
											+ " council sent to emails:" + toAddresses.toString());
									councilNotificationLog.add("Notification Email Body: \n" + body);
									try {
										dateRolloutNode.setProperty(PARAM_COUNCIL_NOTIFICATIONS_SENT, Boolean.TRUE);
										dateRolloutNode.getSession().save();
									} catch (RepositoryException e1) {
										log.error("Girlscouts Rollout Service encountered error: ", e1);
									}
								} catch (Exception e) {
									log.error("Girlscouts Rollout Service encountered error: ", e);
									try {
										dateRolloutNode.setProperty(PARAM_COUNCIL_NOTIFICATIONS_SENT, Boolean.FALSE);
										dateRolloutNode.getSession().save();
									} catch (RepositoryException e1) {
										log.error("Girlscouts Rollout Service encountered error: ", e1);
									}
									councilNotificationLog
											.add("Failed to send notification for " + String.valueOf(pathToCouncilSite)
													+ " council to emails:" + String.valueOf(toAddresses));
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("Girlscouts Rollout Service encountered error: ", e);
		}
	}

	private Boolean isGSRolloutConfig(Node pageNode) {
		boolean isValid = false;
		if (pageNode != null) {
			Node liveSync = getLiveSyncNode(pageNode);
			if (liveSync != null) {
				try {
					if (liveSync.hasProperty("cq:rolloutConfigs")) {
						Value[] configs = liveSync.getProperty("cq:rolloutConfigs").getValues();
						if (configs != null && configs.length > 0) {
							for (Value config : configs) {
								if (GS_ROLLOUT_CONFIG.equals(config.getString())) {
									isValid = true;
								}
							}
						}
					}
				} catch (RepositoryException e) {
					log.error("Girlscouts Rollout Service encountered error: ", e);
				}
			} else {
				isValid = isGSRolloutConfig(getAncestorWithLiveSync(pageNode));
			}
		}
		return isValid;
	}

	private Node getLiveSyncNode(Node node) {
		try {
			if (node.hasNode("jcr:content/cq:LiveSyncConfig")) {
				return node.getNode("jcr:content/cq:LiveSyncConfig");
			}
		} catch (Exception e) {
		}
		return null;
	}

	private Node getAncestorWithLiveSync(Node node) {
		try {
			Node parentNode = node.getParent();
			if (parentNode != null) {
				if (getLiveSyncNode(parentNode) != null) {
					return parentNode;
				} else {
					return getAncestorWithLiveSync(parentNode);
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
}