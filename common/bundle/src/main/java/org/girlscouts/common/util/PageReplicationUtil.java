package org.girlscouts.common.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import javax.jcr.Node;
import javax.jcr.RangeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.RowIterator;
import javax.jcr.query.Row;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.common.constants.PageReplicationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.cq.workflow.metadata.MetaDataMap;

public class PageReplicationUtil implements PageReplicationConstants {

	private static Logger log = LoggerFactory.getLogger(PageReplicationUtil.class);

	public static String[] getIps(ResourceResolver rr, int group) {
		log.info("Getting dispatcher IPs for group {} ", group);
		try {
			String gsActivationsNodePath = PAGE_ACTIVATIONS_PATH;
			Resource gsActivationRes = rr.resolve(gsActivationsNodePath);
			if (!gsActivationRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
				Node gsActivationsNode = gsActivationRes.adaptTo(Node.class);
				if (gsActivationsNode.hasProperty("ips" + group)) {
					Value[] values = gsActivationsNode.getProperty("ips" + group).getValues();
					String[] toReturn = new String[values.length];
					for (int i = 0; i < values.length; i++) {
						toReturn[i] = values[i].getString();
					}
					return toReturn;
				}
			}
		} catch (RepositoryException e) {
			log.error("PageReplicationUtil encountered error: ", e);
		}
		return new String[0];
	}

	public static int getGroupSize(ResourceResolver rr) {
		log.info("Getting activation group size ");
		try {
			String gsActivationsNodePath = PAGE_ACTIVATIONS_PATH;
			Resource gsActivationRes = rr.resolve(gsActivationsNodePath);
			if (!gsActivationRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
				Node gsActivationsNode = gsActivationRes.adaptTo(Node.class);
				if (gsActivationsNode.hasProperty(PARAM_GROUP_SIZE)) {
					String value = gsActivationsNode.getProperty(PARAM_GROUP_SIZE).getString();
					if (value != null) {
						try {
							return Integer.parseInt(value);
						} catch (Exception e) {
							log.error("PageReplicationUtil encountered error: ", e);
						}
					}
				}
			}
		} catch (RepositoryException e) {
			log.error("PageReplicationUtil encountered error: ", e);
		}
		return DEFAULT_PARAM_GROUP_SIZE;
	}

	public static int getMinutes(ResourceResolver rr) {
		log.info("Getting activation pause in minutes ");
		try {
			String gsActivationsNodePath = PAGE_ACTIVATIONS_PATH;
			Resource gsActivationRes = rr.resolve(gsActivationsNodePath);
			if (!gsActivationRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
				Node gsActivationsNode = gsActivationRes.adaptTo(Node.class);
				if (gsActivationsNode.hasProperty(PARAM_MINUTES)) {
					String value = gsActivationsNode.getProperty(PARAM_MINUTES).getString();
					if (value != null) {
						try {
							return Integer.parseInt(value);
						} catch (Exception e) {
							log.error("PageReplicationUtil encountered error: ", e);
						}
					}
				}
			}
		} catch (RepositoryException e) {
			log.error("PageReplicationUtil encountered error: ", e);
		}
		return DEFAULT_PARAM_MINUTES;
	}

	public static int getCrawlDepth(ResourceResolver rr) {
		log.info("Getting crawl depth ");
		try {
			String gsActivationsNodePath = PAGE_ACTIVATIONS_PATH;
			Resource gsActivationRes = rr.resolve(gsActivationsNodePath);
			if (!gsActivationRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
				Node gsActivationsNode = gsActivationRes.adaptTo(Node.class);
				if (gsActivationsNode.hasProperty(PARAM_CRAWL_DEPTH)) {
					String value = gsActivationsNode.getProperty(PARAM_CRAWL_DEPTH).getString();
					if (value != null) {
						try {
							return Integer.parseInt(value);
						} catch (Exception e) {
							log.error("PageReplicationUtil encountered error: ", e);
						}
					}
				}
			}
		} catch (RepositoryException e) {
			log.error("PageReplicationUtil encountered error: ", e);
		}
		return DEFAULT_PARAM_CRAWL_DEPTH;
	}

	public static String[] getEmails(ResourceResolver rr) throws Exception {
		log.info("Getting report email addresses ");
		String gsActivationsNodePath = "/etc/" + PAGE_ACTIVATIONS_NODE;
		Resource gsActivationRes = rr.resolve(gsActivationsNodePath);
		if (!gsActivationRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
			Node gsActivationsNode = gsActivationRes.adaptTo(Node.class);
			if (gsActivationsNode.hasProperty(PARAM_REPORT_EMAILS)) {
				Value[] values = gsActivationsNode.getProperty(PARAM_REPORT_EMAILS).getValues();
				String[] toReturn = new String[values.length];
				for (int i = 0; i < values.length; i++) {
					toReturn[i] = values[i].getString();
				}
				return toReturn;
			}
		}
		return new String[0];
	}

	public static String getTemplateMessage(String templatePath, ResourceResolver rr) {
		log.info("Getting template email at {} ", templatePath);
		try {
			Resource templateResource = rr.resolve(templatePath);
			Resource dataResource = templateResource.getChild("jcr:content/data");
			ValueMap templateProps = ResourceUtil.getValueMap(dataResource);
			String ret = "";
			String message = templateProps.get("content", "");
			String head = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
					+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
					+ "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
					+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
					+ "<title>Girl Scouts</title></head>";
			ret = head + "<body>" + message + "</body></html>";

			return ret;
		} catch (Exception e) {
			log.error("PageReplicationUtil encountered error: ", e);
			return "";
		}
	}

	public static String getTemplateSubject(String templatePath, ResourceResolver rr) {
		log.info("Getting template subject at {} ", templatePath);
		try {
			Resource templateResource = rr.resolve(templatePath);
			Resource contentResource = templateResource.getChild("jcr:content");
			ValueMap contentProps = ResourceUtil.getValueMap(contentResource);
			return contentProps.get("jcr:title", "GSUSA Rollout Notification");
		} catch (Exception e) {
			log.error("PageReplicationUtil encountered error: ", e);
			return "";
		}
	}

	public static Set<String> getCouncils(Node n) throws RepositoryException {
		log.info("Getting list of councils at {} ", n.getPath());
		Set<String> pages = null;
		if (n.hasProperty(PARAM_COUNCILS)) {
			pages = new HashSet<String>();
			Value[] values = n.getProperty(PARAM_COUNCILS).getValues();
			for (Value value : values) {
				pages.add(value.getString());
			}
		}
		return pages;
	}

	public static Set<String> getPagesToActivate(Node n) throws RepositoryException {
		log.info("Getting list of pages to activate at {} ", n.getPath());
		Set<String> pages = new HashSet<String>();
		if (n.hasProperty(PARAM_PAGES)) {
			pages = new HashSet<String>();
			Value[] values = n.getProperty(PARAM_PAGES).getValues();
			for (Value value : values) {
				pages.add(value.getString());
			}
		}
		return pages;
	}

	public static Set<String> getPagesToDelete(Node n) throws RepositoryException {
		log.info("Getting list of pages to delete at {} ", n.getPath());
		Set<String> pages = new HashSet<String>();
		if (n.hasProperty(PARAM_PAGES_TO_DELETE)) {
			pages = new HashSet<String>();
			Value[] values = n.getProperty(PARAM_PAGES_TO_DELETE).getValues();
			for (Value value : values) {
				pages.add(value.getString());
			}
		}
		return pages;
	}

	public static Set<String> getNotifyCouncils(Node n) throws RepositoryException {
		log.info("Getting list of councils to notify at {} ", n.getPath());
		Set<String> notifyCouncils = new HashSet<String>();
		if (n.hasProperty(PARAM_NOTIFY_COUNCILS)) {
			notifyCouncils = new HashSet<String>();
			Value[] values = n.getProperty(PARAM_NOTIFY_COUNCILS).getValues();
			for (Value value : values) {
				notifyCouncils.add(value.getString());
			}
		}
		return notifyCouncils;
	}

	public static void markReplicationFailed(Node dateRolloutNode) {
		try {
			log.info("Marking replication failed at {} ", dateRolloutNode.getPath());
			dateRolloutNode.setProperty(PARAM_STATUS, STATUS_FAILED);
			dateRolloutNode.getSession().save();
		} catch (RepositoryException e) {
			log.error("PageReplicationUtil encountered error: ", e);
		}
	}

	public static void markReplicationComplete(Node dateRolloutNode) {
		try {
			log.info("Marking replication complete at {} ", dateRolloutNode.getPath());
			dateRolloutNode.setProperty(PARAM_STATUS, STATUS_COMPLETE);
			dateRolloutNode.getSession().save();
		} catch (RepositoryException e) {
			log.error("PageReplicationUtil encountered error: ", e);
		}
	}

	public static void archive(Node dateRolloutNode) {
		try {
			log.info("Archiving replication at {} ", dateRolloutNode.getPath());
			Node parent = dateRolloutNode.getParent();
			if (!parent.hasNode(COMPLETED_NODE)) {
				parent.addNode(COMPLETED_NODE);
			}
			Session session = dateRolloutNode.getSession();
			session.move(dateRolloutNode.getPath(),
					parent.getPath() + "/" + COMPLETED_NODE + "/" + dateRolloutNode.getName());
			session.save();
		} catch (RepositoryException e) {
			log.error("PageReplicationUtil encountered error: ", e);
		}
	}

	public static List<String> getGsusaEmails(ResourceResolver rr) {
		log.info("Getting gsusa report emails");
		List<String> toAddresses = new ArrayList<String>();
		Resource addressesRes = rr.resolve(GS_REPORTEMAIL_PATH);
		ValueMap vm = ResourceUtil.getValueMap(addressesRes);
		try {
			String[] addresses = vm.get(PARAM_REPORT_EMAILS, String[].class);
			if (addresses != null) {
				for (String address : addresses) {
					if (address != null && !address.isEmpty()) {
					toAddresses.add(address);
					}
				}
			}
		} catch (Exception e) {
			log.error("PageReplicationUtil encountered error: ", e);
			try {
				String singleValue = vm.get(PARAM_REPORT_EMAILS, String.class);
				if (singleValue != null && !singleValue.isEmpty()) {
					toAddresses.add(singleValue);
				}
			} catch (Exception e1) {
				log.error("PageReplicationUtil encountered error: ", e1);
			}
		}
		return toAddresses;
	}

	public static Boolean isTestMode(ResourceResolver rr) {
		log.info("Checking if running in test email mode ");
		Boolean isTestMode = Boolean.FALSE;
		try {
			Resource activationResource = rr.resolve(PAGE_ACTIVATIONS_PATH);
			if (activationResource != null) {
				Node activationNode = activationResource.adaptTo(Node.class);
				if (activationNode != null && activationNode.hasProperty(PARAM_TEST_MODE)) {
					isTestMode = activationNode.getProperty(PARAM_TEST_MODE).getBoolean();
				}
			}
		} catch (Exception e) {
			log.error("PageReplicationUtil encountered error: ", e);
		}
		return isTestMode;
	}
	
	public static String getCouncilLiveUrl(ResourceResolver rr, SlingSettingsService settingsService, String path) {
		log.info("Getting council live path url for page {}", path);
		String councilUrl = "";
		if (path != null && path.trim().length() > 0) {
			try {
				Resource pageRes = rr.resolve(path);
				if (pageRes != null && !pageRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
					Page pagePage = pageRes.adaptTo(Page.class);
					Page homePage = pagePage.getAbsoluteParent(2);
					String homepagePath = homePage.getPath();
					councilUrl = homepagePath + path + ".html";
					String mappingPath = getCouncilLiveDomain(rr, settingsService, homepagePath);
					if (!StringUtils.isBlank(mappingPath)) {
						councilUrl = mappingPath + "/" + path.replaceAll("/content/.+?/", "") + ".html";
					}
				} else {
					councilUrl = "page not found";
				}
			} catch (Exception e) {
				log.error("PageReplicationUtil encountered error: ", e);
			}
		}
		return councilUrl;
	}

	public static String getCouncilLiveDomain(ResourceResolver rr, SlingSettingsService settingsService, String path) {
		log.info("Getting council live domain for page {}", path);
		String councilDomain = "";
		if (path != null && path.trim().length() > 0) {
			try {
				Resource pageRes = rr.resolve(path);
				if (pageRes != null && !pageRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
					Page pagePage = pageRes.adaptTo(Page.class);
					Page sitePage = pagePage.getAbsoluteParent(1);
					String mappingPath = "";
					Set<String> runmodes = settingsService.getRunModes();
					if (runmodes.contains("prod")) {
						mappingPath = "/etc/map.publish.prod/http";
					} else if (runmodes.contains("uat")) {
						mappingPath = "/etc/map.publish.uat/http";
					} else if (runmodes.contains("stage")) {
						mappingPath = "/etc/map.publish.stage/http";
					} else if (runmodes.contains("dev")) {
						mappingPath = "/etc/map.publish.dev/http";
					} else if (runmodes.contains("local")) {
						mappingPath = "/etc/map.publish.local/http";
					} else {
						mappingPath = "/etc/map.publish/http";
					}
					Session session = rr.adaptTo(Session.class);
					QueryManager qm = session.getWorkspace().getQueryManager();
					String query = "SELECT [sling:match] FROM [sling:Mapping] as s WHERE ISDESCENDANTNODE(s,'"
							+ mappingPath + "') AND [sling:internalRedirect]='" + sitePage.getPath() + "/en.html'";
					Query q = qm.createQuery(query, Query.JCR_SQL2);
					QueryResult result = q.execute();
					RowIterator rowIt = result.getRows();
					if (rowIt.hasNext()) {
						Row row = rowIt.nextRow();
						Value val = row.getValue("sling:match");
						if (val != null) {
							String mapping = val.getString();
							if (mapping.endsWith("/$")) {
								mapping = mapping.substring(0, mapping.length() - 2);
							}
							councilDomain = mapping;
						}
					}
				}
			} catch (Exception e) {
				log.error("PageReplicationUtil encountered error: ", e);
			}
		}
		return councilDomain;
	}

	public static String getCouncilName(String path) {
		log.info("Getting council path for page {}", path);
		String councilName = "";
		try {
			Matcher matcher = BRANCH_PATTERN.matcher(path);
			if (matcher.find()) {
				councilName = matcher.group();
			}
		} catch (Exception e) {
			log.error("PageReplicationUtil encountered error: ", e);
		}
		return councilName;
	}

	public static String getCouncilAuthorUrl(ResourceResolver rr, String path) {
		log.info("Getting council authoring path for page {}", path);
		String councilUrl = "";
		if (path != null && path.trim().length() > 0) {
			Resource pageRes = rr.resolve(path);
			if (pageRes != null && !pageRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
				councilUrl = path + ".html";
			}
		}
		return councilUrl;
	}

	public static String generateCouncilNotification(String srcPagePath, String targetPagePath, String message,
			ResourceResolver rr, SlingSettingsService settingsService) {
		log.info("Generating council notification for page {}", targetPagePath);
		String srcPagePathUrl = srcPagePath + ".html";
		String councilAuthorPagePath = getCouncilAuthorUrl(rr, targetPagePath);
		String councilLivePagePath = getCouncilLiveUrl(rr, settingsService, targetPagePath);
		String html = message.replaceAll("<%template-page%>", srcPagePathUrl)
				.replaceAll("&lt;%template-page%&gt;", srcPagePathUrl)
				.replaceAll("<%council-page%>", councilLivePagePath)
				.replaceAll("&lt;%council-page%&gt;", councilLivePagePath)
				.replaceAll("<%council-author-page%>", councilAuthorPagePath)
				.replaceAll("&lt;%council-author-page%&gt;", "https://authornew.girlscouts.org" + councilAuthorPagePath)
				.replaceAll("<%a", "<a").replaceAll("<%/a>", "</a>").replaceAll("&lt;%a", "<a")
				.replaceAll("&lt;%/a&gt;", "</a>");
		html = html.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		return html;
	}

	public static List<String> getCouncilEmails(Node homepage) {
		List<String> toAddresses = new ArrayList<String>();
		try {
			log.info("Getting council email addresses from {}", homepage.getPath());
			if (homepage.hasNode("jcr:content")) {
				Node content = homepage.getNode("jcr:content");
				String email1 = null;
				String email2 = null;
				try {
					email1 = content.getProperty("email1").getString();
					if (email1 != null && !email1.isEmpty()) {
						toAddresses.add(email1);
					}
				} catch (Exception e) {
					log.error("PageReplicationUtil encountered error: email1=" + email1, e);
				}
				try {
					email2 = content.getProperty("email2").getString();
					if (email2 != null && !email2.isEmpty()) {
						toAddresses.add(email2);
					}
				} catch (Exception e) {
					log.error("PageReplicationUtil encountered error: email2=" + email2, e);
				}
			}
		} catch (RepositoryException e1) {
			log.error("PageReplicationUtil encountered error: ", e1);
		}
		return toAddresses;
	}

	public static List<String> getReportEmails(ResourceResolver rr) throws RepositoryException {
		log.info("Getting report email addresses");
		List<String> toAddresses = new ArrayList<String>();
		Resource addressesRes = rr.resolve(PAGE_ACTIVATIONS_PATH);
		ValueMap vm = ResourceUtil.getValueMap(addressesRes);
		try {
			String[] addresses = vm.get(PARAM_REPORT_EMAILS, String[].class);
			if (addresses != null) {
				for (String address : addresses) {
					if (address != null && !address.isEmpty()) {
						toAddresses.add(address);
					}
				}
			}
		} catch (Exception e) {
			log.error("PageReplicationUtil encountered error: ", e);
			try {
				String singleValue = vm.get(PARAM_REPORT_EMAILS, String.class);
				if (singleValue != null) {
					toAddresses.add(singleValue);
				}
			} catch (Exception e1) {
				log.error("PageReplicationUtil encountered error: ", e1);
			}
		}
		return toAddresses;
	}

	public static String getEnvironment(ResourceResolver rr) throws RepositoryException {
		log.info("Getting environment");
		String env = "";
		try {
			Resource gsActivationsRes = rr.resolve(PAGE_ACTIVATIONS_PATH);
			ValueMap vm = ResourceUtil.getValueMap(gsActivationsRes);
			env = vm.get(PARAM_ENVIRONMENT, String.class);
		} catch (Exception e) {
			log.error("PageReplicationUtil encountered error: ", e);
		}
		return env;
	}

	public static String getDateRes() {
		log.info("Getting date");
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_NODE_FMT);
		String dateString = sdf.format(today);
		return dateString;
	}

	public static boolean isPageInheritanceBroken(Resource targetResource, List<String> rolloutLog)
			throws RepositoryException {
		log.info("Checking resource at {} for broken inheritance ",
				targetResource.getPath());
		Boolean breakInheritance = false;
		try {
			ValueMap contentProps = ResourceUtil.getValueMap(targetResource);
			breakInheritance = contentProps.get(PARAM_BREAK_INHERITANCE, false);
		} catch (Exception e) {
			log.error("PageReplicationUtil encountered error: ", e);
		}
		if (breakInheritance) {
			log.info("Resource at {} has parameter breakInheritance = {}",
					targetResource.getPath(), breakInheritance);
			rolloutLog.add("Girlscouts Rollout Service: Resource at " + targetResource.getPath()
					+ " has parameter breakInheritance = " + breakInheritance);
		} else {
			Node node = targetResource.adaptTo(Node.class);
			if (node.isNodeType(PARAM_LIVE_SYNC_CANCELLED)) {
				log.info("Resource at {} has mixin type cq:LiveSyncCancelled",
						targetResource.getPath());
				rolloutLog.add("Resource at " + targetResource.getPath()
						+ " has mixin type cq:LiveSyncCancelled");
				breakInheritance = true;
			} else {
				Resource targetResourceContent = targetResource.getChild("jcr:content");
				try {
					ValueMap contentProps = ResourceUtil.getValueMap(targetResourceContent);
					breakInheritance = contentProps.get(PARAM_BREAK_INHERITANCE, false);
					if (breakInheritance) {
						log.info("Resource at {} has parameter breakInheritance = {}",
								targetResourceContent.getPath(), breakInheritance);
						rolloutLog.add("Girlscouts Rollout Service: Resource at " + targetResourceContent.getPath()
								+ " has parameter breakInheritance = " + breakInheritance);
					} else {
						Node contentNode = targetResourceContent.adaptTo(Node.class);
						if (contentNode.isNodeType(PARAM_LIVE_SYNC_CANCELLED)) {
							log.info("Resource at {} has mixin type cq:LiveSyncCancelled",
									targetResourceContent.getPath());
							rolloutLog.add("Resource at " + targetResourceContent.getPath()
									+ " has mixin type cq:LiveSyncCancelled");
							breakInheritance = true;
						}
					}
				} catch (Exception e) {
					log.error("PageReplicationUtil encountered error: ", e);
				}
			}
		}
		if (breakInheritance) {
			log.info("Resource at {} has broken inheritance ", targetResource.getPath());
		} else {
			log.info("Resource at {} is inherited ", targetResource.getPath());
		}
		return breakInheritance;
	}

	public static Map<String, Map<String, String>> getComponentRelationsByCouncil(Set<String> submittedCouncils,
			Resource srcRes,
			Set<String> srcComponents, List<String> rolloutLog, ResourceResolver rr, String relationPagePath) {
		log.info("Categorizing component relations by council for {}", srcRes.getPath());
		final LiveRelationshipManager relationManager = rr.adaptTo(LiveRelationshipManager.class);
		Map<String, Map<String, String>> relationByCouncil = new HashMap<String, Map<String, String>>();
		if (srcComponents != null && srcComponents.size() > 0) {
			for (String srcComponent : srcComponents) {
				Resource componentRes = rr.resolve(srcComponent);
				if (componentRes != null
						&& !componentRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
					log.info("Looking up relations for {}", componentRes.getPath());
					rolloutLog.add("Looking up relations for " + componentRes.getPath());
					for (String council : submittedCouncils) {
						try {
							RangeIterator relationIterator = relationManager.getLiveRelationships(componentRes,
									relationPagePath,
									null);
							if (relationIterator.hasNext()) {
								while (relationIterator.hasNext()) {
									LiveRelationship relation = (LiveRelationship) relationIterator.next();
									String relationPath = relation.getTargetPath();
									log.info("relation = {}", relationPath);
									if (relationByCouncil.containsKey(council)) {
										relationByCouncil.get(council).put(relationPath, srcComponent);
									} else {
										Map<String, String> componentRelations = new HashMap<String, String>();
										componentRelations.put(relationPath, srcComponent);
										relationByCouncil.put(council, componentRelations);
									}
								}
							}
						} catch (Exception e) {
							log.error("PageReplicationUtil encountered error: ", e);
						}
					}
				}
			}
		}
		return relationByCouncil;
	}

	public static Map<String, Set<String>> categorizeRelationComponents(Resource relationPageResource,
			List<String> rolloutLog, ResourceResolver rr) {
		log.info("Categorizing components for {}.", relationPageResource.getPath());
		Map<String, Set<String>> relationComponents = new HashMap<String, Set<String>>();
		Set<String> customComponents = new HashSet<String>();
		Set<String> activeLiveSyncComponents = new HashSet<String>();
		Set<String> inactiveLiveSyncComponents = new HashSet<String>();
		categorizeRelationComponents(customComponents, activeLiveSyncComponents, inactiveLiveSyncComponents,
				relationPageResource.getChild("jcr:content"), true);
		relationComponents.put(RELATION_CUSTOM_COMPONENTS, customComponents);
		relationComponents.put(RELATION_INHERITED_COMPONENTS, activeLiveSyncComponents);
		relationComponents.put(RELATION_CANC_INHERITANCE_COMPONENTS, inactiveLiveSyncComponents);
		return relationComponents;
	}

	private static void categorizeRelationComponents(Set<String> customComponents, Set<String> activeLiveSyncComponents,
			Set<String> inactiveLiveSyncComponents, Resource resource, boolean isParentInherited) {
		try {
			if (resource != null && resource.hasChildren()) {
				Iterator<Resource> it = resource.getChildren().iterator();
				while (it.hasNext()) {
					Resource childResource = it.next();
					Node node = childResource.adaptTo(Node.class);
					if ("nt:unstructured".equals(node.getPrimaryNodeType().getName())) {
						try {
							if (!isParentInherited) {
								log.info(
										"Component {} has cancelled inheritance because parent has cancelled inheritance.",
										childResource.getPath());
								inactiveLiveSyncComponents.add(childResource.getPath());
								categorizeRelationComponents(customComponents, activeLiveSyncComponents,
										inactiveLiveSyncComponents, childResource, false);
							} else {
								if (childResource.isResourceType("wcm/msm/components/ghost")) {
									log.info("Component {} is a deleted inherited component.",
											childResource.getPath());
									inactiveLiveSyncComponents.add(childResource.getPath());
									categorizeRelationComponents(customComponents, activeLiveSyncComponents,
											inactiveLiveSyncComponents, childResource, false);
								} else {
									Node childNode = childResource.adaptTo(Node.class);
									if (childNode.isNodeType(PARAM_LIVE_SYNC_CANCELLED) && !isParsys(childNode)) {
										log.info("Component {} has cancelled inheritance.",
												childResource.getPath());
										inactiveLiveSyncComponents.add(childResource.getPath());
										categorizeRelationComponents(customComponents, activeLiveSyncComponents,
												inactiveLiveSyncComponents, childResource, false);
									} else {
										if (childNode.isNodeType(PARAM_LIVE_SYNC)) {
											log.info("Component {} is inherited.", childResource.getPath());
											activeLiveSyncComponents.add(childResource.getPath());
											categorizeRelationComponents(customComponents, activeLiveSyncComponents,
													inactiveLiveSyncComponents, childResource, true);
										} else {
											log.info("Component {} is custom.", childResource.getPath());
											customComponents.add(childResource.getPath());
										}
									}
								}
							}
						} catch (Exception e) {
							log.error("PageReplicationUtil encountered error: ", e);
						}
					} else {
						log.info("Skipping node {} since its not of type nt:unstructured.",
								childResource.getPath());
						categorizeRelationComponents(customComponents, activeLiveSyncComponents,
								inactiveLiveSyncComponents, childResource, true);
					}
				}
			}
		} catch (RepositoryException e) {
			log.error("PageReplicationUtil encountered error: ", e);
		}
	}

	private static boolean isParsys(Node childNode) {
		try {
			String resourceType = childNode.getProperty("sling:resourceType").getString();
			return resourceType.equals("girlscouts-common/components/styled-parsys")
					|| resourceType.equals("girlscouts/components/styled-parsys")
					|| resourceType.equals("foundation/components/parsys");
		} catch (Exception e) {
			log.error("PageReplicationUtil encountered error: ", e);
		}
		return false;
	}

	public static Node getDateRolloutNode(Session session, ResourceResolver resourceResolver, boolean delay)
			throws RepositoryException {
		Node dateRolloutNode = null;
		Resource etcRes = resourceResolver.resolve("/etc");
		Node etcNode = etcRes.adaptTo(Node.class);
		Node activationsNode = null;
		Node activationTypeNode = null;
		String date = PageReplicationUtil.getDateRes();
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

	public static Set<String> getCouncils(MetaDataMap mdm) {
		Set<String> councils = new HashSet<String>();
		try {
			Value[] values = (Value[]) mdm.get(PARAM_COUNCILS);
			if (values != null && values.length > 0) {
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
				log.error("PageReplicationUtil encountered error: ", e);
			}
		}
		return councils;
	}

	public static Set<String> getComponentsToDelete(Map<String, String> sourceToTargetComponentRelations,
			Map<String, Set<String>> relationComponentsMap, String relationCouncilPath) {
		log.info("Looking up inherited components for {} that need to be deleted.", relationCouncilPath);
		Set<String> componentsToDelete = new HashSet<String>();
		Set<String> inheritedComponents = relationComponentsMap.get(RELATION_INHERITED_COMPONENTS);
		for (String inheritedComponent : inheritedComponents) {
			try {
				if (!sourceToTargetComponentRelations.containsValue(inheritedComponent)) {
					log.info(
							"Inherited Component {} exist on council site, but not on source site. Qualifies to be deleted ",
							inheritedComponent);
					componentsToDelete.add(inheritedComponent);
				}
			} catch (Exception e) {
				log.error("PageReplicationUtil encountered error: ", e);
			}
		}
		return componentsToDelete;
	}

	public static Set<String> getComponentsToRollout(Map<String, String> sourceToTargetComponentRelations,
			Map<String, Set<String>> relationComponentsMap, String relationCouncilPath) {
		log.info("Looking up source components for {} that need to be rolled out.", relationCouncilPath);
		Set<String> componentsToRollout = new HashSet<String>();
		Set<String> cancelledInheritanceComponents = relationComponentsMap.get(RELATION_CANC_INHERITANCE_COMPONENTS);
		Set<String> srcComponents = sourceToTargetComponentRelations.keySet();
		for (String srcComponent : srcComponents) {
			try {
				String relatedComponent = sourceToTargetComponentRelations.get(srcComponent);
				if (relatedComponent == null || !cancelledInheritanceComponents.contains(relatedComponent)) {
					log.info("Including component {} in rollout for {}.", srcComponent, relatedComponent);
					componentsToRollout.add(srcComponent);
				}
			} catch (Exception e) {
				log.error("PageReplicationUtil encountered error: ", e);
			}
		}
		return componentsToRollout;
	}

	public static Map<String, String> getComponentRelationsByPage(Set<String> srcComponents, String relationPagePath,
			List<String> rolloutLog, ResourceResolver rr) {
		log.info("Listing component relations for {}", relationPagePath);
		final LiveRelationshipManager relationManager = rr.adaptTo(LiveRelationshipManager.class);
		Map<String, String> componentRelationsByPage = new HashMap<String, String>();
		if (srcComponents != null && srcComponents.size() > 0) {
			for (String srcComponent : srcComponents) {
				Resource componentRes = rr.resolve(srcComponent);
				if (componentRes != null
						&& !componentRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
					log.info("Looking up relations for {}", componentRes.getPath());
					rolloutLog.add("Looking up relations for " + componentRes.getPath());
					try {
						RangeIterator relationIterator = relationManager.getLiveRelationships(componentRes,
								relationPagePath, null);
						if (relationIterator.hasNext()) {
							while (relationIterator.hasNext()) {
								LiveRelationship relation = (LiveRelationship) relationIterator.next();
								String relationPath = relation.getTargetPath();
								log.info("{} inherits from {}", relationPath, componentRes.getPath());
								componentRelationsByPage.put(srcComponent, relationPath);
							}
						} else {
							componentRelationsByPage.put(srcComponent, null);
							log.info("no inherited components for {}", componentRes.getPath());
						}
					} catch (Exception e) {
						log.error("PageReplicationUtil encountered error: ", e);
					}
				}
			}
		}
		return componentRelationsByPage;
	}

	public static Set<String> getComponents(Resource srcRes) {
		log.info("PageReplicationUtil : Gathering all components under {}", srcRes.getPath());
		Set<String> components = new HashSet<String>();
		try {
			traverseNodeForComponents(srcRes.getChild("jcr:content"), components);
		} catch (Exception e) {
			log.error("PageReplicationUtil encountered error: ", e);
		}
		return components;
	}

	private static void traverseNodeForComponents(Resource resource, Set<String> components) {
		log.info("PageReplicationUtil : traversing {}", resource.getPath());
		if (resource != null && resource.hasChildren()) {
			Iterator<Resource> it = resource.getChildren().iterator();
			while (it.hasNext()) {
				Resource childResource = it.next();
				Node node = childResource.adaptTo(Node.class);
				try {
					if ("nt:unstructured".equals(node.getPrimaryNodeType().getName())) {
						log.info("Adding source node {} to rollout.", childResource.getPath());
						components.add(childResource.getPath());
					} else {
						log.info("Skipping node {} since it's not of type nt:unstructured.", childResource.getPath());
					}
					traverseNodeForComponents(childResource, components);
				} catch (RepositoryException e) {
					log.error("PageReplicationUtil encountered error: ", e);
				}
			}
		}
	}
}
