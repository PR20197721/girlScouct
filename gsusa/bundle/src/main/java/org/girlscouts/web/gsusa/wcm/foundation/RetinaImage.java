package org.girlscouts.web.gsusa.wcm.foundation;

import javax.jcr.Node;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.wcm.foundation.WCMRenditionPicker;

public class RetinaImage extends Image {
    private static Logger log = LoggerFactory.getLogger(RetinaImage.class);

    public RetinaImage(Resource resource) {
        super(resource);
    }
}
