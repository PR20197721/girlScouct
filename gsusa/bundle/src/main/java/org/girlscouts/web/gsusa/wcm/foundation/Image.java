package org.girlscouts.web.gsusa.wcm.foundation;

import org.apache.sling.api.resource.Resource;
import org.girlscouts.web.wcm.foundation.GSRenditionPicker;
import org.girlscouts.web.wcm.foundation.RetinaImage;

import com.day.cq.dam.api.RenditionPicker;

import java.util.Optional;

/*
 * Overrides base by supplying a different rendition picker to translate to GSUSA style renditions.
 */
public class Image extends org.girlscouts.web.wcm.foundation.Image {

	public Image(Resource resource) {
		super(resource);
	}

	@Override
	public RenditionPicker getRenditionPicker(Optional<String> imageVar) {
	    	if (getCropRect() != null || !imageVar.isPresent()) {
            return new GSRenditionPicker(DEFAULT_RENDITION);
        } else {
            String[] targetRenditions = null;
	    	    	if (this.getClass().equals(RetinaImage.class)) {
    	       		targetRenditions = new String[]{"cq5dam.npd." + imageVar.get() + "@2x", DEFAULT_RENDITION};
	    	    	} else {
	    	    		targetRenditions = new String[]{"cq5dam.npd." + imageVar.get(), DEFAULT_RENDITION};
	    	    	}
        		return new GSRenditionPicker(targetRenditions);
        }
	}
}
