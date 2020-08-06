package org.girlscouts.web.osgi.component.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Girl Scouts VTK settings provider Configuration", description = "Girl Scouts VTK settings provider Configuration")
public @interface GirlscoutsVtkConfigProviderConfiguration {

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
