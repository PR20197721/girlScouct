package org.girlscouts.vtk.helpers;

import java.util.Dictionary;

public interface ConfigListener {
	@SuppressWarnings("rawtypes")
	void updateConfig(Dictionary configs);
}
