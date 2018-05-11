package org.girlscouts.common.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.web.wcm.foundation.Image;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.function.Function;

public class ImageUtil {

	public static Image getImage(ResourceResolver resourceResolver, Resource resource, String relativePath){
		return getImage(resourceResolver, resource, relativePath, res -> new Image(res));
	}

	public static Image getImage(ResourceResolver resourceResolver, Resource resource, String relativePath, Function<Resource, Image> resourceImageCreator){
		Resource imageResource;
		if(!StringUtils.isBlank(relativePath)) {
			imageResource =resource.getChild(relativePath);
		}else {
			imageResource = resource;
		}
		if(imageResource == null) {
			return null;
		}

		if(!resourceResolver.isResourceType(imageResource, "foundation/components/image")){
			Node node = imageResource.adaptTo(Node.class);
			try {
				Image img = resourceImageCreator.apply(resourceResolver.resolve(node.getProperty("fileReference").getString()));
				img.set("isReferencedImage", "true");
				return img;
			}catch (RepositoryException e) {}
		}

		return resourceImageCreator.apply(imageResource);
	}

}
