package org.girlscouts.vtk.helpers;

import org.girlscouts.vtk.osgi.component.ConfigListener;

public interface ConfigManager {
	void register(ConfigListener listener);

	String getConfig(String key);
	
	String[] getCouncilMapping();
}
