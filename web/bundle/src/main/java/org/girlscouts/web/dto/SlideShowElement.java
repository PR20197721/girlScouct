package org.girlscouts.web.dto;

/*
 * DTO to serialize data from the image sizes component to the SlideShowManager.
 * Allows for smarter client-side rendering.
 */
public class SlideShowElement {
	
	public static final int BREAKPOINT_MAX_LARGE = 1120;
	public static final int BREAKPOINT_MAX_MEDIUM = 1024;
	public static final int BREAKPOINT_MAX_SMALL = 640;
	
	private String text = "", alt = "", size = "", linkUrl = "";
	private boolean forceNewWindow = false;
	private int width = BREAKPOINT_MAX_LARGE;
	
	public SlideShowElement(String text, String linkUrl, String alt, String size, boolean forceNewWindow){
		this.setText(text);
		this.setAlt(alt);
		this.setSize(size);
		this.setLinkUrl(linkUrl);
		this.setForceNewWindow(forceNewWindow);
		
		// Default to large.
		if("small".equals(size)) {
			this.setWidth(BREAKPOINT_MAX_SMALL);
		}else if("medium".equals(size)) {
			this.setWidth(BREAKPOINT_MAX_MEDIUM);
		}else {
			this.setWidth(BREAKPOINT_MAX_LARGE);
		}
		
	}
	
	public String getText(){
		return this.text;
	}
	
	public void setText(String text){
		this.text = text;
	}
	
	public String getAlt(){
		return this.alt;
	}
	
	public void setAlt(String alt){
		this.alt = alt;
	}
	
	public void setLinkUrl(String linkUrl){
		this.linkUrl = linkUrl;
	}
	
	public String getLinkUrl(){
		return this.linkUrl;
	}
	
	public String getSize(){
		return this.size;
	}
	
	public void setSize(String size){
		this.size = size;
	}
	
	public boolean isForceNewWindow(){
		return this.forceNewWindow;
	}
	
	public void setForceNewWindow(boolean forceNewWindow){
		this.forceNewWindow = forceNewWindow;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
}