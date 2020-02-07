package org.girlscouts.web.osgi.component.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Girl Scouts DNS Mapping Configuration", description = "Girl Scouts DNS Mapping Configuration")
public @interface GirlscoutsDnsProviderConfig {
    @AttributeDefinition(name = "Get Council DNS mapping", description = "Get Council DNS mapping to council names", type = AttributeType.STRING) String[] dnsMapping();
}
