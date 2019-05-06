package org.girlscouts.vtk.osgi.service.impl;

import org.osgi.service.component.ComponentContext;

import java.util.Dictionary;

public class BasicGirlScoutsService {
    protected ComponentContext context;

    protected String getConfig(String property) {
        if (this.context != null) {
            Dictionary properties = this.context.getProperties();
            return String.valueOf(properties.get(property));
        } else {
            return "";
        }
    }
}
