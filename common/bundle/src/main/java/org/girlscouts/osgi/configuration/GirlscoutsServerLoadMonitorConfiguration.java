package org.girlscouts.osgi.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Girl Scouts Server Load Monitor Configuration", description = "Girl Scouts Server Load Monitor Configuration")
public @interface GirlscoutsServerLoadMonitorConfiguration {

	@AttributeDefinition(name = "Regular Expression", description = "Regular expression for url pattern to be redirected to maintenance page", type = AttributeType.STRING)
	String regex();

	@AttributeDefinition(name = "Maintenance Page", description = "Path to maintenance page", type = AttributeType.STRING)
	String maintenancePage();

}
