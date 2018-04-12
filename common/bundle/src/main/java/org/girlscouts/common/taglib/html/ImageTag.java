package org.girlscouts.common.taglib.html;

import java.io.IOException;
import javax.servlet.jsp.JspException;

import org.apache.sling.api.resource.Resource;

public class ImageTag extends BaseMarkupTag {
	private static final long serialVersionUID = 1L;
	
	private String relativePath;

	@Override
	public int doEndTag() throws JspException {   
		try {
			Resource imageResource = getResource().getChild(getRelativePath());
			Image image = new Image();
			
			
			pageContext.getOut().print("Path Info: " + getRequest().getPathInfo());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    return EVAL_PAGE;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}
	
}