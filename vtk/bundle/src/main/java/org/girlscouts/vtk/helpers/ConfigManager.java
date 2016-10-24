package org.girlscouts.vtk.helpers;

public interface ConfigManager {
	void register(ConfigListener listener);

	String getConfig(String key);
	
	String[] getCouncilMapping();
}
