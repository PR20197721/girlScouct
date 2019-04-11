package org.girlscouts.vtk.osgi.service.impl;

import org.osgi.service.component.ComponentContext;

public class BasicGirlScoutsService{

    protected ComponentContext context;

    protected String getConfig(String property) {
        if (context != null) {
            return (String)context.getProperties().get(property);
        } else {
            return "";
        }
    }
}
