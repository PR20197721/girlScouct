package org.girlscouts.common.osgi.component;

import org.apache.sling.api.resource.Resource;

import com.day.cq.dam.api.Asset;

public interface GirlscoutsImagePathProvider {

	public String getImagePath(String path, String rendition);

	public String getImagePath(Asset asset, String rendition);

	public String getImagePathByLocation(String path, Resource resource);

}
