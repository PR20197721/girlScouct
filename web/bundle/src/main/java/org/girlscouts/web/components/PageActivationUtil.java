package org.girlscouts.web.components;

import java.util.HashSet;
import java.util.Set;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.girlscouts.web.constants.PageActivationConstants;

public class PageActivationUtil implements PageActivationConstants {

	public static String[] getIps(ResourceResolver rr, int group) {
		try {
			String gsActivationsNodePath = GS_ACTIVATIONS_PATH;
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

		}
		return new String[0];
	}

	public static int getGroupSize(ResourceResolver rr) {
		try {
			String gsActivationsNodePath = GS_ACTIVATIONS_PATH;
			Resource gsActivationRes = rr.resolve(gsActivationsNodePath);
			if (!gsActivationRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
				Node gsActivationsNode = gsActivationRes.adaptTo(Node.class);
				if (gsActivationsNode.hasProperty(PARAM_GROUP_SIZE)) {
					String value = gsActivationsNode.getProperty(PARAM_GROUP_SIZE).getString();
					if (value != null) {
						try {
							return Integer.parseInt(value);
						} catch (Exception e) {

						}
					}
				}
			}
		} catch (RepositoryException e) {

		}
		return DEFAULT_PARAM_GROUP_SIZE;
	}

	public static int getMinutes(ResourceResolver rr) {
		try {
			String gsActivationsNodePath = GS_ACTIVATIONS_PATH;
			Resource gsActivationRes = rr.resolve(gsActivationsNodePath);
			if (!gsActivationRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
				Node gsActivationsNode = gsActivationRes.adaptTo(Node.class);
				if (gsActivationsNode.hasProperty(PARAM_MINUTES)) {
					String value = gsActivationsNode.getProperty(PARAM_MINUTES).getString();
					if (value != null) {
						try {
							return Integer.parseInt(value);
						} catch (Exception e) {

						}
					}
				}
			}
		} catch (RepositoryException e) {

		}
		return DEFAULT_PARAM_MINUTES;
	}

	public static int getCrawlDepth(ResourceResolver rr) {
		try {
			String gsActivationsNodePath = GS_ACTIVATIONS_PATH;
			Resource gsActivationRes = rr.resolve(gsActivationsNodePath);
			if (!gsActivationRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
				Node gsActivationsNode = gsActivationRes.adaptTo(Node.class);
				if (gsActivationsNode.hasProperty(PARAM_CRAWL_DEPTH)) {
					String value = gsActivationsNode.getProperty(PARAM_CRAWL_DEPTH).getString();
					if (value != null) {
						try {
							return Integer.parseInt(value);
						} catch (Exception e) {

						}
					}
				}
			}
		} catch (RepositoryException e) {

		}
		return DEFAULT_PARAM_CRAWL_DEPTH;
	}

	public static String[] getEmails(ResourceResolver rr) throws Exception {
		String gsActivationsNodePath = "/etc/" + ACTIVATIONS_NODE;
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
			e.printStackTrace();
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
			e.printStackTrace();
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
		Set<String> pages = null;
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
		} catch (RepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static void archive(Node dateRolloutNode, Session session) {
		try {
			Node parent = dateRolloutNode.getParent();
			if (!parent.hasNode(COMPLETED_NODE)) {
				parent.addNode(COMPLETED_NODE);
			}
			session.move(dateRolloutNode.getPath(),
					parent.getPath() + "/" + COMPLETED_NODE + "/" + dateRolloutNode.getName());
			session.save();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
}
