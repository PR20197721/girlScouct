package org.girlscouts.web.osgi.component.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Girl Scouts VTK settings provider Configuration", description = "Girl Scouts VTK settings provider Configuration")
public @interface GirlscoutsVtkConfigProviderConfiguration {

	@AttributeDefinition(name = "Regular Expression", description = "Regular expression for url pattern to be redirected to maintenance page", type = AttributeType.STRING)
	String helloUrl();

	@AttributeDefinition(name = "Callback URL", description = "Callback URL", type = AttributeType.STRING)
	String callbackUrl();

	@AttributeDefinition(name = "Community URL", description = "Community URL", type = AttributeType.STRING)
	String communityUrl();

	@AttributeDefinition(name = "is Demo Site", description = "Is Demo Site?", type = AttributeType.BOOLEAN)
	String isDemoSite();
}
