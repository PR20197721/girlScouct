package org.girlscouts.common.taglib.html;

import org.girlscouts.common.taglib.base.BaseTag;

public class BaseMarkupTag extends BaseTag {
	private static final long serialVersionUID = 1L;
	
	private String styleClass, style;

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}
	
	@Override
	public void release() {
		this.styleClass = null;
		this.style = null;
	}
	
}