package org.girlscouts.web.osgi.component.impl;

import org.apache.felix.scr.annotations.Activate;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.web.osgi.component.GirlscoutsVtkConfigProvider;
import org.osgi.service.component.ComponentContext;
import org.girlscouts.vtk.helpers.ConfigManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = {
		GirlscoutsVtkConfigProvider.class }, immediate = true, name = "org.girlscouts.common.osgi.component.impl.GirlscoutsVtkConfigProviderImpl")
public class GirlscoutsVtkConfigProviderImpl implements GirlscoutsVtkConfigProvider {

	private static final Logger log = LoggerFactory.getLogger(GirlscoutsVtkConfigProviderImpl.class);

	@Reference
	protected ConfigManager configManager;

	@Activate
	private void activate() {
		log.info("Girl Scouts Image Path Provider Activated.");
	}

	@Override
	public String getConfig(String property) {
		if (configManager != null) {
			return configManager.getConfig(property);
		} else {
			return "";
		}

	}

	@Deactivate
	private void deactivate(ComponentContext context) {
		log.info("Girl Scouts Image Path Provider Deactivated.");
	}

}
