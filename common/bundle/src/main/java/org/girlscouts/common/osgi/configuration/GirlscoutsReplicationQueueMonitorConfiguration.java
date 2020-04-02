package org.girlscouts.common.osgi.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Girl Scouts Replication Queue Monitor Configuration", description = "Girl Scouts Replication Queue Monitor Configuration")
public @interface GirlscoutsReplicationQueueMonitorConfiguration {

	@AttributeDefinition(name = "Email Addresses", description = "Max wait time notification recipents")
	String[] emailAddresses();

	@AttributeDefinition(name = "Schedule Cron Expression", description = "Replication Queue Monitor frequency", type = AttributeType.STRING)
	String scheduler_expression();
	
	@AttributeDefinition(name = "Send notifications", description = "Replication Queue Monitor recipents", type = AttributeType.BOOLEAN)
	boolean sendNotifications();
}
