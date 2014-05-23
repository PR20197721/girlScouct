package org.girlscouts.vtk.impl.filters;

import java.util.Dictionary;

public interface ConfigListener {
    @SuppressWarnings("rawtypes")
    void updateConfig(Dictionary configs);
}
