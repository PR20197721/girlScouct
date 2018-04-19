package org.girlscouts.common.taglib.html;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.jsp.JspException;

import org.apache.sling.api.resource.Resource;
import org.girlscouts.web.wcm.foundation.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.ImageResource;
import com.day.cq.wcm.foundation.Placeholder;

@SuppressWarnings("serial")
public class ImageTag extends BaseMarkupTag {
	private static Logger log = LoggerFactory.getLogger(Image.class);
	
	private String relativePath, selector, suffix, href, description, alt;
	private Boolean newWindow = false;

	@Override
	public int doEndTag() throws JspException {   
		
		Image image = getImage();
		if(image == null) {
			return EVAL_PAGE;
		}
		
		// Common properties.
	    image.setIsInUITouchMode(Placeholder.isAuthoringUIModeTouch(getRequest()));
	    image.loadStyleData(getCurrentStyle());
		
	    // Passed properties.
		image.addCssClass(getStyleClass());
		image.setSelector(Optional.ofNullable(getSelector()).orElse(".img"));
		//Optional.ofNullable(getHref()).ifPresent(image::setHref);
		Optional.ofNullable(getHref()).ifPresent(href -> image.set(ImageResource.PN_LINK_URL, href));
		
		Optional.ofNullable(getSuffix()).ifPresent(image::setSuffix);
		Optional.ofNullable(getDescription()).ifPresent(image::setDescription);
		Optional.ofNullable(getAlt()).ifPresent(image::setAlt);
		try {
			if(getNewWindow()) {
				pageContext.getOut().write(image.getString().replace("<a ", "<a target=\"_blank\""));
			}else {
				image.draw(pageContext.getOut());
			}
		} catch (IOException e) {
			log.error("Unable to write image", e);
		}
	    return EVAL_PAGE;
	}
	
	// Overide with specific Image implementations.
	public Image getImage() {
		Resource imageResource = getResource().getChild(getRelativePath());
		if(imageResource == null) {
			return null;
		}
		return new Image(imageResource);
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public Boolean getNewWindow() {
		return newWindow;
	}

	public void setNewWindow(Boolean newWindow) {
		this.newWindow = newWindow;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	@Override
	public void release() {
		this.relativePath = null;
		this.selector = null;
		this.suffix = null;
		this.newWindow = false;
		this.href = null;
		this.description = null;
		this.alt = null;
	}
	
}