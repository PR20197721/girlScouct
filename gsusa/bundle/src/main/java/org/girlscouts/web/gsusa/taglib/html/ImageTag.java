package org.girlscouts.web.gsusa.taglib.html;

import org.apache.sling.api.resource.Resource;
import org.girlscouts.web.wcm.foundation.Image;

@SuppressWarnings("serial")
public class ImageTag extends org.girlscouts.common.taglib.html.ImageTag {
	
	@Override
	public Image getImage() {
		Resource imageResource = getResource().getChild(getRelativePath());
		if(imageResource == null) {
			return null;
		}
		return new org.girlscouts.web.gsusa.wcm.foundation.Image(imageResource);
	}
	
}
