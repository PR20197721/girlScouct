package org.girlscouts.web.wcm.foundation;

import org.apache.sling.api.resource.Resource;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;

public class Image extends com.day.cq.wcm.foundation.Image {
	public Image(Resource resource) {
        super(resource);
    }
	
	protected Resource getReferencedResource(String path)
	  {
	    Resource res = getResourceResolver().getResource(path);
	    if (res != null) {
	      if (res.adaptTo(Asset.class) != null)
	      {
	    	Asset asset = (Asset)res.adaptTo(Asset.class);
	        Rendition rendition = asset.getRendition(new GSRenditionPicker());
	        res = null != rendition ? (Resource)rendition.adaptTo(Resource.class) : null;
	      }
	    }
        
	    return res;
	  }
}
