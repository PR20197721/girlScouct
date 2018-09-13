package org.girlscouts.gsusa.components.rssfeed;

import java.util.Arrays;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;

public class RssFeedPathItem {

	private String path;
	private boolean page, subDir;
	
	public static RssFeedPathItem fromLegacyString(String legacyString) {
		
		RssFeedPathItem returner = new RssFeedPathItem();
		if(StringUtils.isBlank(legacyString)) {
			return null;
		}
		
		List<String> attrList = Arrays.asList(legacyString.split("\\|\\|\\|"));
		if(attrList.size() > 0) {
			returner.setPath(attrList.get(0));
		}
		if(attrList.size() > 1) {
			returner.setPage(Boolean.valueOf(attrList.get(1)));
		}
		if(attrList.size() > 2) {			
			returner.setSubDir(Boolean.valueOf(attrList.get(2)));
		}
		
		return returner;
	}

	// TODO: Generic version of this.
	public static RssFeedPathItem fromNode(Node node) {
		RssFeedPathItem returner = new RssFeedPathItem();
		if(node == null) {
			return null;
		}
		try {
			if(node.hasProperty("path")) {
				returner.setPath(node.getProperty("path").getString());
			}
			if(node.hasProperty("page")) {
				returner.setPage(node.getProperty("page").getBoolean());
			}
			if(node.hasProperty("subDir")) {
				returner.setSubDir(node.getProperty("subDir").getBoolean());
			}
		} catch (RepositoryException e) {}
		
		return returner;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isPage() {
		return page;
	}

	public void setPage(boolean page) {
		this.page = page;
	}

	public boolean isSubDir() {
		return subDir;
	}

	public void setSubDir(boolean subDir) {
		this.subDir = subDir;
	}

}
