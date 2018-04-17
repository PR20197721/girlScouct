package org.girlscouts.web.video.util;

import java.util.Optional;

public enum VIDEO_TYPE { 
	YOUTUBE, VIMEO, NONE;
			
	public static VIDEO_TYPE detect(String url) {
		url = Optional.ofNullable(url).orElse("");
		
		// Needs to be "youtu" to account for "youtu.be" links
		if (url.indexOf("youtu") != -1) {
			return YOUTUBE;
		}else if(url.indexOf("vimeo") != -1) {
			return VIMEO;
		}else {
			return NONE;
		}
	}
	
}