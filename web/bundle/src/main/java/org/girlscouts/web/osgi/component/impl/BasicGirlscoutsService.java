package org.girlscouts.web.osgi.component.impl;

import org.osgi.service.component.ComponentContext;

import java.util.Dictionary;

public class BasicGirlscoutsService {
    protected ComponentContext context;

    protected String[] getConfig(String property) {
        if (this.context != null) {
            Dictionary properties = this.context.getProperties();
            return (String [])(properties.get(property));
        } else {
            return null;
        }
    }
}
