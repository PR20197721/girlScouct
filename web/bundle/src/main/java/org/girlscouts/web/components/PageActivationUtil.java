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
}
