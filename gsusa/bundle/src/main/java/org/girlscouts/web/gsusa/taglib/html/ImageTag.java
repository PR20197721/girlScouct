package org.girlscouts.web.gsusa.taglib.html;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

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
		if(imageResource != null && !"foundation/components/image".equals(imageResource.getResourceType())) {
			Node node = imageResource.adaptTo(Node.class);
			try {
				Image img = new org.girlscouts.web.gsusa.wcm.foundation.Image(getResourceResolver().resolve(node.getProperty("fileReference").getString()));
				img.set("isReferencedImage", "true");
				return img;
			}catch (RepositoryException e) {e.printStackTrace();}
		}
		return new org.girlscouts.web.gsusa.wcm.foundation.Image(imageResource);
	}
	
}
