package org.girlscouts.web.osgi.component.impl;

import org.apache.felix.scr.annotations.Activate;
import org.girlscouts.web.osgi.component.GirlscoutsVtkConfigProvider;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;

@Component(service = {
		GirlscoutsVtkConfigProvider.class }, immediate = true, name = "org.girlscouts.web.osgi.component.impl.GirlscoutsVtkConfigProvider")
@Designate(ocd = GirlscoutsVtkConfigProviderImpl.Config.class)
public class GirlscoutsVtkConfigProviderImpl implements GirlscoutsVtkConfigProvider {

	private static final Logger log = LoggerFactory.getLogger(GirlscoutsVtkConfigProviderImpl.class);
	private ComponentContext context;

	@Activate
	private void activate(ComponentContext context) {
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

    @Override
    public String[] getCouncilMapping() {
        Dictionary configs = context.getProperties();
        return (String[]) configs.get("councilMapping");
    }

    @Deactivate
	private void deactivate(ComponentContext context) {
		log.info("Girl Scouts VTK Config Provider Deactivated.");
	}

    @ObjectClassDefinition(name = "Girl Scouts VTK settings provider Configuration", description = "Girl Scouts VTK settings provider Configuration")
    public @interface Config {

        @AttributeDefinition(name = "Hello Url", description = "Hello Url", type = AttributeType.STRING)
        String helloUrl();

        @AttributeDefinition(name = "VTK Log In URL", type = AttributeType.STRING)
        String loginUrl();

        @AttributeDefinition(name = "VTK Log Out URL", type = AttributeType.STRING)
        String logoutUrl();

        @AttributeDefinition(name = "is Demo Site", description = "Is Demo Site?", type = AttributeType.BOOLEAN)
        String isDemoSite();

        @AttributeDefinition(name = "Council Mapping", description = "Defines mappings between a council ID and a content branch. Format: council id::content branch. e.g. 12::gateway")
        String[] councilMapping();
    }

}
