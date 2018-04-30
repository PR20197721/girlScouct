package org.girlscouts.web.gsusa.taglib.html;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.girlscouts.web.wcm.foundation.Image;

@SuppressWarnings("serial")
public class ImageTag extends org.girlscouts.common.taglib.html.ImageTag {
	
	@Override
	public Image getImage() {
		Resource imageResource;
		if(!StringUtils.isBlank(getRelativePath())) {
			imageResource = getResource().getChild(getRelativePath());
		}else {
			imageResource = getResource();
		}
		if(imageResource == null) {
			return null;
		}
		
		if(!getResourceResolver().isResourceType(imageResource, "foundation/components/image")){
			Node node = imageResource.adaptTo(Node.class);
			imageResource.getResourceSuperType();
			try {
				Image img = new org.girlscouts.web.gsusa.wcm.foundation.Image(getResourceResolver().resolve(node.getProperty("fileReference").getString()));
				img.set("isReferencedImage", "true");
				return img;
			}catch (RepositoryException e) {}
		}
		return new org.girlscouts.web.gsusa.wcm.foundation.Image(imageResource);
	}
	
}
