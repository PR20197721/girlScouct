package org.girlscouts.gsusa.taglib.html;

import org.girlscouts.common.util.ImageUtil;
import org.girlscouts.common.wcm.foundation.Image;

public class ImageTag extends org.girlscouts.common.taglib.html.ImageTag {
	
	@Override
	public Image getImage() {
		return ImageUtil.getImage(getResourceResolver(), getResource(), getRelativePath(),
				res -> new org.girlscouts.common.wcm.foundation.gsusa.Image(res));
	}
	
}
