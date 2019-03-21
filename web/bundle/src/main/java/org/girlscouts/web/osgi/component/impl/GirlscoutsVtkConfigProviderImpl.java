package org.girlscouts.web.osgi.component.impl;

import org.apache.felix.scr.annotations.Activate;
import org.girlscouts.web.osgi.component.configuration.GirlscoutsVtkConfigProviderConfiguration;
import org.girlscouts.web.osgi.component.GirlscoutsVtkConfigProvider;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = {
		GirlscoutsVtkConfigProvider.class }, immediate = true, name = "org.girlscouts.common.osgi.component.impl.GirlscoutsVtkConfigProvider")
@Designate(ocd = GirlscoutsVtkConfigProviderConfiguration.class)
public class GirlscoutsVtkConfigProviderImpl implements GirlscoutsVtkConfigProvider {

	private static final Logger log = LoggerFactory.getLogger(GirlscoutsVtkConfigProviderImpl.class);

	private GirlscoutsVtkConfigProviderConfiguration config;
	private ComponentContext context;

	@Activate
	private void activate(ComponentContext context, GirlscoutsVtkConfigProviderConfiguration config) {
		this.config = config;
		this.context = context;
		log.info("Girl Scouts VTK Config Provider Activated.");
	}

	@Override
	public String getConfig(String property) {
		if (context != null) {
			return (String)context.getProperties().get(property);
		} else {
			return "";
		}

	}

	@Deactivate
	private void deactivate(ComponentContext context) {
		log.info("Girl Scouts VTK Config Provider Deactivated.");
	}

}
