package org.girlscouts.common.taglib.html;

import org.girlscouts.common.taglib.base.BaseTag;

public class BaseMarkupTag extends BaseTag {
	private static final long serialVersionUID = 1L;
	
	private String styleClass;

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
	
}