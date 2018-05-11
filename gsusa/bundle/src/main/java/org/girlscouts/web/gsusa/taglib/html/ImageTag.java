package org.girlscouts.web.gsusa.taglib.html;

import org.girlscouts.common.util.ImageUtil;
import org.girlscouts.web.wcm.foundation.Image;

public class ImageTag extends org.girlscouts.common.taglib.html.ImageTag {
	
	@Override
	public Image getImage() {
		return ImageUtil.getImage(getResourceResolver(), getResource(), getRelativePath(), res -> new org.girlscouts.web.gsusa.wcm.foundation.Image(res));
	}
	
}
