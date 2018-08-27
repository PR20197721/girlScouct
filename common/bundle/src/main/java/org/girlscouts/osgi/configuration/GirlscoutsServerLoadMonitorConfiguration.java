package org.girlscouts.osgi.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Girl Scouts Server Load Monitor Configuration", description = "Girl Scouts Server Load Monitor Configuration")
public @interface GirlscoutsServerLoadMonitorConfiguration {

	@AttributeDefinition(name = "Max Load", description = "Server load at which maintenance page redirect activated", type = AttributeType.INTEGER)
	int maxLoad();

	@AttributeDefinition(name = "Alert Load", description = "Server load at which alert email is sent", type = AttributeType.INTEGER)
	int alertLoad();

	@AttributeDefinition(name = "Recovery load", description = "Server load at which maintenance page redirect is removed", type = AttributeType.INTEGER)
	int recoverLoad();

	@AttributeDefinition(name = "Regular Expression", description = "Regular expression for url pattern to be redirected to maintenance page", type = AttributeType.STRING)
	String regex();

	@AttributeDefinition(name = "Maintenance Page", description = "Path to maintenance page", type = AttributeType.STRING)
	String maintenancePage();

	@AttributeDefinition(name = "Mapping Path", description = "Path to redirect mapping", type = AttributeType.STRING)
	String mappingPath();

	@AttributeDefinition(name = "Send notifications", description = "High Server load notification recipents", type = AttributeType.BOOLEAN)
	boolean sendNotifications();

	@AttributeDefinition(name = "Email Addresses", description = "High Server load notification recipents")
	String[] emailAddresses();

	@AttributeDefinition(name = "Schedule Cron Expression", description = "Server Load Monitor frequency", type = AttributeType.STRING)
	String scheduler_expression();
}
