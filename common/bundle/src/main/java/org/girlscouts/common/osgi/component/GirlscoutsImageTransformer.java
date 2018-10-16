package org.girlscouts.common.osgi.component;

import com.day.cq.dam.api.Asset;

public interface GirlscoutsImageTransformer {

	public String getRendition(String path, String rendition);

	public String getRendition(Asset asset, String rendition);

}
