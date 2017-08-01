package org.girlscouts.web.components;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
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
				if (gsActivationsNode.hasProperty(CONFIG_GROUP_SIZE)) {
					Value value = gsActivationsNode.getProperty(CONFIG_GROUP_SIZE).getValue();
					if (value != null) {
						try {
							return Integer.parseInt(value.getString());
						} catch (Exception e) {

						}
					}
				}
			}
		} catch (RepositoryException e) {

		}
		return DEFAULT_CONFIG_GROUP_SIZE;
	}

	public static int getMinutes(ResourceResolver rr) {
		try {
			String gsActivationsNodePath = GS_ACTIVATIONS_PATH;
			Resource gsActivationRes = rr.resolve(gsActivationsNodePath);
			if (!gsActivationRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
				Node gsActivationsNode = gsActivationRes.adaptTo(Node.class);
				if (gsActivationsNode.hasProperty(CONFIG_MINUTES)) {
					Value value = gsActivationsNode.getProperty(CONFIG_MINUTES).getValue();
					if (value != null) {
						try {
							return Integer.parseInt(value.getString());
						} catch (Exception e) {

						}
					}
				}
			}
		} catch (RepositoryException e) {

		}
		return DEFAULT_CONFIG_MINUTES;
	}

	public static int getCrawlDepth(ResourceResolver rr) {
		try {
			String gsActivationsNodePath = GS_ACTIVATIONS_PATH;
			Resource gsActivationRes = rr.resolve(gsActivationsNodePath);
			if (!gsActivationRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
				Node gsActivationsNode = gsActivationRes.adaptTo(Node.class);
				if (gsActivationsNode.hasProperty(CONFIG_CRAWL_DEPTH)) {
					Value value = gsActivationsNode.getProperty(CONFIG_CRAWL_DEPTH).getValue();
					if (value != null) {
						try {
							return Integer.parseInt(value.getString());
						} catch (Exception e) {

						}
					}
				}
			}
		} catch (RepositoryException e) {

		}
		return DEFAULT_CONFIG_CRAWL_DEPTH;
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
}
