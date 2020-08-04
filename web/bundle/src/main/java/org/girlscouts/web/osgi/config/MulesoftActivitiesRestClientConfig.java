package org.girlscouts.web.osgi.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "MuleSoft Activities Rest client configuration", description = "MuleSoft Activities Rest client configuration")
public @interface MulesoftActivitiesRestClientConfig {
    @AttributeDefinition(name = "MuleSoft Activities rest service url", type = AttributeType.STRING) String restEndPoint();
    @AttributeDefinition(name = "MuleSoft Activities rest service client id", type = AttributeType.STRING) String client_id();
    @AttributeDefinition(name = "MuleSoft Activities rest service client secret", type = AttributeType.STRING) String client_secret();
}