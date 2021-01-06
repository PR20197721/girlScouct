package org.girlscouts.vtk.osgi.component.impl;

import org.apache.felix.scr.annotations.*;
import org.girlscouts.vtk.osgi.component.ConfigListener;
import org.girlscouts.vtk.osgi.component.ConfigManager;
import org.osgi.service.component.ComponentContext;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

@Component(label = "Girl Scouts VTK Configuration Manager", description = "All Girl Scouts VTK Configurations go here", metatype = true, immediate = true)
@Service
@Properties({

        @Property(name = "helloUrl", label = "Hello URL", description = "URL of the controller that checks if the user is signed in and say hello."),
        @Property(name = "targetUrl", label = "Target URL", description = "Redirect to this URL if authentication succeeds. Usually it is VTK homepage."),
        @Property(name = "renewUrl", label = "Renew URL", description = "URL to Renew Page for My-Troop renew button."),
        @Property(name = "councilMapping", cardinality = Integer.MAX_VALUE, label = "Council Branch Mapping", description = "Defines mappings between a council ID and a content branch. Format: council id::content branch. e.g. 12::gateway"),
        @Property(name = "baseUrl", label = "Home Url", description = "Url for current server"),
        @Property(name = "defaultBranch", label = "Default Branch", description = "Default branch if council mapping not found. e.g. girlscouts-usa"),
        @Property(name = "gsNewYear", label = "gsNewYear", description = "gsNewYear"),
        @Property(name = "vtkHolidays", cardinality = Integer.MAX_VALUE, label = "vtkHolidays", description = "vtkHolidays"),
        @Property(name = "startShowingArchiveCmd", label = "startShowingArchiveCmd", description = "startShowingArchiveCmd"),
        @Property(name = "gsFinanceYearCutoffDate", label = "gsFinanceYearCutoffDate", description = "gsFinanceYearCutoffDate"),

}) 
public class ConfigManagerImpl implements ConfigManager {
    private List<ConfigListener> listeners;
    private ComponentContext context;

    @Activate
    private void init(ComponentContext context) {
        listeners = new ArrayList<ConfigListener>();
        this.context = context;
    }

    public void register(ConfigListener listener) {
        listeners.add(listener);
        listener.updateConfig(context.getProperties());
    }

    public String getConfig(String key) {
        @SuppressWarnings("rawtypes") Dictionary configs = context.getProperties();
        return (String) configs.get(key);
    }

    public String[] getCouncilMapping() {
        Dictionary configs = context.getProperties();
        return (String[]) configs.get("councilMapping");
    }

    @Modified
    private void updateConfig(ComponentContext context) {
        this.context = context;
        @SuppressWarnings("rawtypes") Dictionary configs = context.getProperties();
        for (ConfigListener listener : listeners) {
            listener.updateConfig(configs);
        }
    }
}
