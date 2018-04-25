package org.girlscouts.web.gsusa.component.rightrailcarousel;

import java.util.Arrays;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;

public class RightRailCarouselItem {
	private String label, link, imagePath;
	private boolean newWindow = false;
	
	public static RightRailCarouselItem fromLegacyString(String legacyString) {
		
		RightRailCarouselItem returner = new RightRailCarouselItem();
		if(StringUtils.isBlank(legacyString)) {
			return null;
		}
		
		List<String> attrList = Arrays.asList(legacyString.split("\\|\\|\\|"));
		if(attrList.size() > 0) {
			returner.setLabel(attrList.get(0));
		}
		if(attrList.size() > 1) {
			returner.setLink(attrList.get(1));
		}
		if(attrList.size() > 2) {			
			returner.setNewWindow(Boolean.valueOf(attrList.get(2)));
		}
		if(attrList.size() > 3) {
			returner.setImagePath(attrList.get(3));
		}
		
		return returner;
	}

	// TODO: Generic version of this.
	public static RightRailCarouselItem fromNode(Node node) {
		RightRailCarouselItem returner = new RightRailCarouselItem();
		if(node == null) {
			return null;
		}
		try {
			if(node.hasProperty("label")) {
				returner.setLabel(node.getProperty("label").getString());
			}
			if(node.hasProperty("link")) {
				returner.setLink(node.getProperty("link").getString());
			}
			if(node.hasProperty("imagePath")) {
				returner.setImagePath(node.getProperty("imagePath").getString());
			}
			if(node.hasProperty("label")) {
				returner.setLabel(node.getProperty("label").getString());
			}
			if(node.hasProperty("newWindow")) {
				returner.setNewWindow(node.getProperty("newWindow").getBoolean());
			}
		} catch (RepositoryException e) {}
		
		return returner;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getLink() {
		return link;
	}
	
	public void setLink(String link) {
		this.link = link;
	}
	
	public String getImagePath() {
		return imagePath;
	}
	
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
	public boolean isNewWindow() {
		return newWindow;
	}
	
	public void setNewWindow(boolean newWindow) {
		this.newWindow = newWindow;
	}
		
}
