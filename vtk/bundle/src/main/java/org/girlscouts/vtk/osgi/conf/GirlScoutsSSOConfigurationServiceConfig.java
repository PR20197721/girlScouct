package org.girlscouts.vtk.osgi.conf;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Girl Scouts VTK Manual Troop Load Service configuration", description = "Girl Scouts VTK Manual Troop Load Service configuration")
public @interface GirlScoutsSSOConfigurationServiceConfig {
    @AttributeDefinition(name = "API Key", type = AttributeType.STRING) String apiKey();
    @AttributeDefinition(name = "SP Name", description = "Service Provider Name", type = AttributeType.STRING) String spName();
    @AttributeDefinition(name = "Log In Path", description = "Fully qualified path to log in page", type = AttributeType.STRING) String logInPath();
    @AttributeDefinition(name = "Log Out Path", description = "Fully qualified path to log out page", type = AttributeType.STRING) String logOutPath();
    @AttributeDefinition(name = "Screen set", description = "Screen set name configured in CDC", type = AttributeType.STRING) String screenSet();
    @AttributeDefinition(name = "Session Expiration", description = "SSO session length", type = AttributeType.INTEGER) String sessionExpiration();

}
