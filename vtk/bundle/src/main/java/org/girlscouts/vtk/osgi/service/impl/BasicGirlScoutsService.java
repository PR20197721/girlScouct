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

    protected Boolean getBooleanConfig(String property) {
        if (this.context != null) {
            Dictionary properties = this.context.getProperties();
            return Boolean.valueOf(String.valueOf(properties.get(property)));
        } else {
            return Boolean.FALSE;
        }
    }

    protected Integer getIntegerConfig(String property) {
        Integer value = null;
        if (this.context != null) {
            Dictionary properties = this.context.getProperties();
            try{
                value = (Integer) properties.get(property);
            }catch(Exception e){

            }
        }
        return value;
    }

    protected String[] getMultiValueConfig(String property) {
        if (this.context != null) {
            Dictionary properties = this.context.getProperties();
            return (String[])properties.get(property);
        } else {
            return new String[]{};
        }
    }
}
