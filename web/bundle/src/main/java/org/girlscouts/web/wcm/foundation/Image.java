package org.girlscouts.web.wcm.foundation;

import javax.jcr.Node;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;

public class Image extends com.day.cq.wcm.foundation.Image {
	
	private static Logger log = LoggerFactory.getLogger(Image.class);
	private static final String DEFAULT_RENDITION = "cq5dam.web.1280.1280";
	
	public Image(Resource resource) {
        super(resource);
    }
	
	protected Resource getReferencedResource(String path)
	{
		String resourcePath = getResource().getPath();

        String CONTENT_MATCH = "jcr:content/content/";
        String imageStem = resourcePath.substring(resourcePath.indexOf(CONTENT_MATCH) + CONTENT_MATCH.length());
        String imageVar = imageStem.substring(0, imageStem.indexOf("/"));
        ResourceResolver rr = getResourceResolver();
        if ("middle".equals(imageVar)) {
        	Node node = getResource().adaptTo(Node.class);
            String resourceType = "three-column-page";
            try {
            	while (!node.getPrimaryNodeType().getName().equals("cq:PageContent")) {
                    node = node.getParent();
                }
                resourceType = node.getProperty("sling:resourceType").getString();
                resourceType = resourceType.substring(resourceType.lastIndexOf("/") + 1);
            } catch (Exception e) {
                log.warn("Cannot get resourceType.");
            }
            
            if ("two-column-page".equals(resourceType)) {
                imageVar = "hero";
            } else if ("page".equals(resourceType) || "homepage".equals(resourceType)) {
                imageVar = "top";
            }
        }
        
        String[] targetRenditions = null;
    	if (this.getClass() == RetinaImage.class) {
       		targetRenditions = new String[]{"cq5dam.npd." + imageVar + "@2x", DEFAULT_RENDITION};
    	} else {
    		targetRenditions = new String[]{"cq5dam.npd." + imageVar, DEFAULT_RENDITION};
    	}

        Resource res = rr.getResource(path);
        if (res != null) {
            if (res.adaptTo(Asset.class) != null) {
                Rendition rendition = null;
                if (getCropRect() != null) {
                    rendition = ((Asset)res.adaptTo(Asset.class)).getRendition(new GSRenditionPicker(DEFAULT_RENDITION));
                } else {

                	rendition = ((Asset)res.adaptTo(Asset.class)).getRendition(new GSRenditionPicker(targetRenditions));
                }
                res = null != rendition ? (Resource)rendition.adaptTo(Resource.class) : null;
            }
        }
        return res;
	}
}
