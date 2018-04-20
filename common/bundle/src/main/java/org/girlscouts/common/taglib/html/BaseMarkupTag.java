package org.girlscouts.common.taglib.html;

import org.girlscouts.common.taglib.base.BaseTag;

public class BaseMarkupTag extends BaseTag {
	private static final long serialVersionUID = 1L;
	
	private String styleClass, styleId, style, ariaLabel;

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

	public String getAriaLabel() {
		return ariaLabel;
	}

	public void setAriaLabel(String ariaLabel) {
		this.ariaLabel = ariaLabel;
	}
	
	@Override
	public void release() {
		super.release();
		this.styleClass = null;
		this.style = null;
		this.ariaLabel = null;
		this.setStyleId(null);
	}

	public String getStyleId() {
		return styleId;
	}

	public void setStyleId(String styleId) {
		this.styleId = styleId;
	}
	
}