package org.girlscouts.vtk.osgi.component;

public interface ConfigManager {
	void register(ConfigListener listener);
	String getConfig(String key);
	String[] getCouncilMapping();
}
