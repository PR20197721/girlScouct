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
import org.girlscouts.vtk.helpers.ConfigListener;
import org.girlscouts.vtk.helpers.ConfigManager;
import org.osgi.service.component.ComponentContext;

@Component(label = "Girl Scouts VTK Configuration Manager", description = "All Girl Scouts VTK Configurations go here", metatype = true, immediate = true)
@Service
@Properties({
		@Property(name = "OAuthUrl", label = "OAuth URL", description = "URL to Salesforce OAuth endpoint."),
		@Property(name = "clientId", label = "Client ID", description = "Salesforce Client ID"),
		@Property(name = "clientSecret", label = "Client Secret", description = "Salesforce Client Secret"),
		@Property(name = "helloUrl", label = "Hello URL", description = "URL of the controller that checks if the user is signed in and say hello."),
		@Property(name = "callbackUrl", label = "Redirect URL", description = "Callback URI that Salesforce redirects to after authentication. Usually it is our controller."),
		@Property(name = "targetUrl", label = "Target URL", description = "Redirect to this URL if authentication succeeds. Usually it is VTK homepage."),
		@Property(name = "communityUrl", label = "Community URL", description = "URL to SalesForce Community Page for the button on the landing page."),
		@Property(name = "councilMapping", cardinality = Integer.MAX_VALUE, label = "Council Branch Mapping", description = "Defines mappings between a council ID and a content branch. Format: council id::content branch. e.g. 12::gateway"),
		@Property(name = "defaultBranch", label = "Default Branch", description = "Default branch if council mapping not found. e.g. girlscouts-usa") })
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
		@SuppressWarnings("rawtypes")
		Dictionary configs = context.getProperties();
		return (String) configs.get(key);
	}
	
	public String[] getCouncilMapping() {
		Dictionary configs = context.getProperties();
		return (String[]) configs.get("councilMapping");
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