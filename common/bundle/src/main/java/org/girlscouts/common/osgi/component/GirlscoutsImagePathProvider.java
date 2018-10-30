package org.girlscouts.common.osgi.component;

import org.apache.sling.api.resource.Resource;

import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.foundation.Image;

public interface GirlscoutsImagePathProvider {

	public String getImagePath(String path, String rendition);

	public String getImagePath(Asset asset, String rendition);

	public String getImagePathByLocation(Image image);

	public String getImagePathByLocation(Resource resource);

}
