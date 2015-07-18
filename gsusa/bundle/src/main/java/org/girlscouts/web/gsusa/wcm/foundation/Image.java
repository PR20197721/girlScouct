package org.girlscouts.web.gsusa.wcm.foundation;

import org.apache.sling.api.resource.Resource;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;

public class Image extends com.day.cq.wcm.foundation.Image {
    public Image(Resource resource) {
        super(resource);
    }
    
    protected Resource getReferencedResource(String path) {
        System.out.println("MZMZ my own image");
        Resource res = getResourceResolver().getResource(path);
        if (res != null) {
            if (res.adaptTo(Asset.class) != null) {
                Rendition rendition = ((Asset)res.adaptTo(Asset.class)).getRendition(new GSRenditionPicker("cq5dam.web.1280.1280"));
                res = null != rendition ? (Resource)rendition.adaptTo(Resource.class) : null;
            }
        }
        System.out.println("res path = " + res.getPath());
        return res;
    }
}
