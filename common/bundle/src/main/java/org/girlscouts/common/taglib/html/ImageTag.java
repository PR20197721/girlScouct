package org.girlscouts.common.taglib.html;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;
import org.girlscouts.common.util.ImageUtil;
import org.girlscouts.common.wcm.foundation.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.ImageResource;

@SuppressWarnings("serial")
public class ImageTag extends BaseMarkupTag {
	private static Logger log = LoggerFactory.getLogger(Image.class);
	
	private String relativePath, selector, suffix, href, description, alt, title;
	private Boolean newWindow = false;

	@Override
	public int doEndTag() throws JspException {   
		
		Image image = getImage();
		if(image == null) {
			return EVAL_PAGE;
		}
		image.setSelector(Optional.ofNullable(getSelector()).orElse(".img"));
    		image.setIsInUITouchMode(false);

	    // Passed properties.
		Optional.ofNullable(getStyleClass()).ifPresent(image::addCssClass);
		Optional.ofNullable(getHref()).ifPresent(href -> image.set(ImageResource.PN_LINK_URL, href));
		Optional.ofNullable(getTitle()).ifPresent(image::setTitle);
		Optional.ofNullable(getSuffix()).ifPresent(image::setSuffix);
		Optional.ofNullable(getDescription()).ifPresent(image::setDescription);
		Optional.ofNullable(getAlt()).ifPresent(image::setAlt);
	    image.loadStyleData(getCurrentStyle());
		try {
			pageContext.getOut().write(writeWithAttributes(image));
		} catch (IOException e) {
			log.error("Unable to write image", e);
		}
	    return EVAL_PAGE;
	}
	
	public String writeWithAttributes(Image image) {
		String output;
		try {
			output = image.getString();
		}catch(NullPointerException npe) {
			// Image was empty / null.
			npe.printStackTrace();
			return "";
		}
		if(getNewWindow()) {
			output = output.replace("<a ", "<a target=\"_blank\"");
		}
		if(StringUtils.isNotBlank(getStyleId())) {
			output = output.replace("<img ", "<img id=\"" + getStyleId() + "\" ");
		}
		if(StringUtils.isNotBlank(getAriaLabel())) {
			output = output.replace("<img ", "<img aria-label=\"" + getAriaLabel() + "\" ");
		}
		if("true".equals(image.get("isReferencedImage"))) {
			output = output.replace("cq-image-placeholder", "");
		}
		return output;
	}
	
	// Overide with specific Image implementations.
	public Image getImage() {
		return ImageUtil.getImage(getResourceResolver(), getResource(), getRelativePath());
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public void release() {
		super.release();
		this.relativePath = null;
		this.selector = null;
		this.suffix = null;
		this.newWindow = false;
		this.href = null;
		this.description = null;
		this.alt = null;
	}
	
}