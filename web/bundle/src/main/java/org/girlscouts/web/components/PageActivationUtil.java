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

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.girlscouts.web.constants.PageActivationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.WCMException;

public class PageActivationUtil implements PageActivationConstants {

	private static Logger log = LoggerFactory.getLogger(PageActivationUtil.class);

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

	public static Set<String> getPages(Node n) throws RepositoryException {
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

	public static void markActivationFailed(Session session, Node dateRolloutNode) {
		try {
			dateRolloutNode.setProperty(PARAM_STATUS, STATUS_FAILED);
			session.save();
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
					toAddresses.add(address);
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

	public static List<String> getCouncilEmails(Node homepage) {
		List<String> toAddresses = new ArrayList<String>();
		try {
			String email1 = homepage.getProperty("email1").getString();
			String email2 = homepage.getProperty("email2").getString();
			toAddresses.add(email1);
			toAddresses.add(email2);
		} catch (Exception e) {
			log.error("PageActivationUtil encountered error: ", e);
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
					toAddresses.add(address);
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

	public static String getDateRes() {
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_NODE_FMT);
		String dateString = sdf.format(today);
		return dateString;
	}
}
