package org.girlscouts.vtk.osgi.component;

import java.util.Dictionary;

public interface ConfigListener {
    @SuppressWarnings("rawtypes")
    void updateConfig(Dictionary configs);


}
