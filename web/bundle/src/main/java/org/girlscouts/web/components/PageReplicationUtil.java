package org.girlscouts.web.components;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.RowIterator;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.web.constants.PageReplicationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;

public class PageReplicationUtil implements PageReplicationConstants {

	private static Logger log = LoggerFactory.getLogger(PageReplicationUtil.class);

	public static String[] getIps(ResourceResolver rr, int group) {
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
			log.error("PageActivationUtil encountered error: ", e);
		}
		return new String[0];
	}

	public static int getGroupSize(ResourceResolver rr) {
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
							log.error("PageActivationUtil encountered error: ", e);
						}
					}
				}
			}
		} catch (RepositoryException e) {
			log.error("PageActivationUtil encountered error: ", e);
		}
		return DEFAULT_PARAM_GROUP_SIZE;
	}

	public static int getMinutes(ResourceResolver rr) {
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
							log.error("PageActivationUtil encountered error: ", e);
						}
					}
				}
			}
		} catch (RepositoryException e) {
			log.error("PageActivationUtil encountered error: ", e);
		}
		return DEFAULT_PARAM_MINUTES;
	}

	public static int getCrawlDepth(ResourceResolver rr) {
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
							log.error("PageActivationUtil encountered error: ", e);
						}
					}
				}
			}
		} catch (RepositoryException e) {
			log.error("PageActivationUtil encountered error: ", e);
		}
		return DEFAULT_PARAM_CRAWL_DEPTH;
	}

	public static String[] getEmails(ResourceResolver rr) throws Exception {
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
			log.error("PageActivationUtil encountered error: ", e);
			return "";
		}
	}

	public static String getTemplateSubject(String templatePath, ResourceResolver rr) {
		try {
			Resource templateResource = rr.resolve(templatePath);
			Resource contentResource = templateResource.getChild("jcr:content");
			ValueMap contentProps = ResourceUtil.getValueMap(contentResource);
			return contentProps.get("jcr:title", "GSUSA Rollout Notification");
		} catch (Exception e) {
			log.error("PageActivationUtil encountered error: ", e);
			return "";
		}
	}

	public static Set<String> getCouncils(Node n) throws RepositoryException {
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
			dateRolloutNode.setProperty(PARAM_STATUS, STATUS_FAILED);
			dateRolloutNode.getSession().save();
		} catch (RepositoryException e) {
			log.error("PageActivationUtil encountered error: ", e);
		}
	}

	public static void markReplicationComplete(Node dateRolloutNode) {
		try {
			dateRolloutNode.setProperty(PARAM_STATUS, STATUS_COMPLETE);
			dateRolloutNode.getSession().save();
		} catch (RepositoryException e) {
			log.error("PageActivationUtil encountered error: ", e);
		}
	}

	public static void archive(Node dateRolloutNode) {
		try {
			Node parent = dateRolloutNode.getParent();
			if (!parent.hasNode(COMPLETED_NODE)) {
				parent.addNode(COMPLETED_NODE);
			}
			Session session = dateRolloutNode.getSession();
			session.move(dateRolloutNode.getPath(),
					parent.getPath() + "/" + COMPLETED_NODE + "/" + dateRolloutNode.getName());
			session.save();
		} catch (RepositoryException e) {
			log.error("PageActivationUtil encountered error: ", e);
		}
	}

	public static String getURL(String path) {
		if (path.endsWith("/jcr:content")) {
			path = path.substring(0, path.lastIndexOf('/'));
		}
		return path + ".html";
	}

	public static String getRealUrl(String path, ValueMap vm) {
		if (path.endsWith("/jcr:content")) {
			path = path.substring(0, path.lastIndexOf('/'));
		}
		if (vm.containsKey("domain") && !vm.get("domain").equals("")) {
			try {
				String pagePath = path.substring(path.indexOf("/", path.indexOf("/", path.indexOf("/") + 1) + 1),
						path.length());
				return vm.get("domain") + pagePath + ".html";
			} catch (Exception e) {
				log.error("PageActivationUtil encountered error: ", e);
			}
		}
		return (path + ".html").replaceFirst("/content/([^/]+)", "https://www.$1.org");
	}

	public static String getBranch(String path) throws WCMException {
		Matcher matcher = BRANCH_PATTERN.matcher(path);
		if (matcher.find()) {
			return matcher.group();
		} else {
			throw new WCMException("Cannot get branch: " + path);
		}
	}

	public static List<String> getGsusaEmails(ResourceResolver rr) {
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
			log.error("PageActivationUtil encountered error: ", e);
			try {
				String singleValue = vm.get(PARAM_REPORT_EMAILS, String.class);
				if (singleValue != null && !singleValue.isEmpty()) {
					toAddresses.add(singleValue);
				}
			} catch (Exception e1) {
				log.error("PageActivationUtil encountered error: ", e1);
			}
		}
		return toAddresses;
	}

	public static Boolean isTestMode(ResourceResolver rr) {
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
			log.error("PageActivationUtil encountered error: ", e);
		}
		return isTestMode;
	}
	
	public static String getCouncilUrl(ResourceResolver rr, SlingSettingsService settingsService, String path){
		String councilUrl = "";
		if (path != null && path.trim().length() > 0) {
			try {
				String mappingPath, homepagePath;
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
				Resource pageRes = rr.resolve(path);
				Page pagePage = pageRes.adaptTo(Page.class);
				Page homePage = pagePage.getAbsoluteParent(2);
				homepagePath = homePage.getPath() + ".html";
				councilUrl = homepagePath.replaceAll("\\.html", "");
				Session session = rr.adaptTo(Session.class);
				QueryManager qm = session.getWorkspace().getQueryManager();
				String query = "SELECT [sling:match] FROM [sling:Mapping] as s WHERE ISDESCENDANTNODE(s,'" + mappingPath
						+ "') AND [sling:internalRedirect]='" + homepagePath + "'";
				Query q = qm.createQuery(query, Query.JCR_SQL2);
				QueryResult result = q.execute();
				RowIterator rowIt = result.getRows();
				String toReturn = rowIt.nextRow().getValue("sling:match").getString();
				if (toReturn.endsWith("/$")) {
					toReturn = toReturn.substring(0, toReturn.length() - 2);
				}
				councilUrl = toReturn;
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}else{
			councilUrl = "page not found";
		}
		return councilUrl;
	}

	public static String generateCouncilNotification(String srcPagePath, String targetPagePath, ValueMap vm,
			String message,
			ResourceResolver rr, SlingSettingsService settingsService) {
		String srcPagePathUrl = getURL(srcPagePath);
		String councilAuthorPagePath = getURL(targetPagePath);
		String councilLivePagePath = getCouncilUrl(rr, settingsService, targetPagePath) + "/"
				+ councilAuthorPagePath.replaceAll("/content/.+?/", "");
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
					log.error("PageActivationUtil encountered error: email1=" + email1, e);
				}
				try {
					email2 = content.getProperty("email2").getString();
					if (email2 != null && !email2.isEmpty()) {
						toAddresses.add(email2);
					}
				} catch (Exception e) {
					log.error("PageActivationUtil encountered error: email2=" + email2, e);
				}
			}
		} catch (RepositoryException e1) {
			log.error("PageActivationUtil encountered error: ", e1);
		}
		return toAddresses;
	}

	public static List<String> getReportEmails(ResourceResolver rr) throws RepositoryException {
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
			log.error("PageActivationUtil encountered error: ", e);
			try {
				String singleValue = vm.get(PARAM_REPORT_EMAILS, String.class);
				if (singleValue != null) {
					toAddresses.add(singleValue);
				}
			} catch (Exception e1) {
				log.error("PageActivationUtil encountered error: ", e1);
			}
		}
		return toAddresses;
	}

	public static String getEnvironment(ResourceResolver rr) throws RepositoryException {
		String env = "";
		try {
			Resource gsActivationsRes = rr.resolve(PAGE_ACTIVATIONS_PATH);
			ValueMap vm = ResourceUtil.getValueMap(gsActivationsRes);
			env = vm.get(PARAM_ENVIRONMENT, String.class);
		} catch (Exception e) {
			log.error("PageActivationUtil encountered error: ", e);
		}
		return env;
	}

	public static String getDateRes() {
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_NODE_FMT);
		String dateString = sdf.format(today);
		return dateString;
	}
}
