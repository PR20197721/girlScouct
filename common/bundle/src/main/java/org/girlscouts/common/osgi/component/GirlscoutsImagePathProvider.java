package org.girlscouts.common.osgi.component;

import com.day.cq.dam.api.Asset;

public interface GirlscoutsImagePathProvider {

	public String getImagePath(String path, String rendition);

	public String getImagePath(Asset asset, String rendition);

}
