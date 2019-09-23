package org.girlscouts.common.osgi.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Girl Scouts Email Service Configuration", description = "Girl Scouts Email Service Configuration")
public @interface GSEmailServiceConfig {

    @AttributeDefinition(name = "Min Thread pool size for sending finance emails", description = "Min Thread pool size for sending finance emails", type = AttributeType.INTEGER)
    int minThreadPoolSize();

    @AttributeDefinition(name = "Max Thread pool size for sending finance emails", description = "Max Thread pool size for sending finance emails", type = AttributeType.INTEGER)
    int maxThreadPoolSize();

}
