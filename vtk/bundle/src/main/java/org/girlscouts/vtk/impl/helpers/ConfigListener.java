package org.girlscouts.vtk.impl.helpers;

import java.util.Dictionary;

public interface ConfigListener {
    @SuppressWarnings("rawtypes")
    void updateConfig(Dictionary configs);
}
