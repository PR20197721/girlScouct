package org.girlscouts.vtk.impl.helpers;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;

@Component(
    label="Girl Scouts VTK Configuration Manager",
    description="All Girl Scouts VTK Configurations go here",
    metatype=true, 
    immediate=true
)
@Service (
    value=ConfigManager.class
)
@Properties ({
    @Property(name="OAuthUrl", label="OAuth URL", description="URL to Salesforce OAuth endpoint."),
    @Property(name="clientId", label="Client ID", description="Salesforce Client ID"),
    @Property(name="callbackUrl", label="Redirect URL", description="Callback URI that Salesforce redirects to after authentication. Usually it is our controller."),
    @Property(name="targetUrl", label="Target URL", description="Redirect to this URL if authentication succeeds. Usually it is VTK homepage.")
})
    
public class ConfigManager {
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
    
    @Modified
    private void updateConfig(ComponentContext context) {
        this.context = context;
        @SuppressWarnings("rawtypes")
        Dictionary configs = context.getProperties();
        for (ConfigListener listener : listeners) {
            listener.updateConfig(configs);
        }
    }
}
