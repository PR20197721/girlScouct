package org.girlscouts.vtk.helpers;

import java.util.Dictionary;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

public interface ConfigListener {
	@SuppressWarnings("rawtypes")
	void updateConfig(Dictionary configs);

	
}
