package org.girlscouts.common.taglib.html;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.jsp.JspException;

import org.apache.sling.api.resource.Resource;
import org.girlscouts.web.wcm.foundation.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageTag extends BaseMarkupTag {
	
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(Image.class);
	
	private String relativePath, selector;

	@Override
	public int doEndTag() throws JspException {   
		Resource imageResource = getResource().getChild(getRelativePath());
		if(imageResource == null) {
			return EVAL_PAGE;
		}
		Image image = new Image(imageResource);
		image.addCssClass(getStyleClass());
		image.setSelector(Optional.ofNullable(getSelector()).orElse(".img"));
		
		try {
			image.draw(pageContext.getOut());
		} catch (IOException e) {
			log.error("Unable to write image", e);
		}
	    return EVAL_PAGE;
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
	
}