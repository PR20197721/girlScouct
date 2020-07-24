package org.girlscouts.vtk.osgi.conf;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Girl Scouts VTK Mulesoft Rest client configuration", description = "Girl Scouts VTK Mulesoft Rest client configuration")
public @interface MulesoftRestClientConfig {
    @AttributeDefinition(name = "Mulesoft rest endpoint url", type = AttributeType.STRING) String endpoint();

    @AttributeDefinition(name = "Mulesoft user info api", type = AttributeType.STRING) String userInfo();

    @AttributeDefinition(name = "Mulesoft troop info api", type = AttributeType.STRING) String troopInfo();

    @AttributeDefinition(name = "Mulesoft troop leader info api", type = AttributeType.STRING) String troopLeaderInfo();

    @AttributeDefinition(name = "Mulesoft contacts info api", type = AttributeType.STRING) String contactsInfo();

    @AttributeDefinition(name = "Girl Scouts Client Id", description = "Girl Scouts Client Id", type = AttributeType.STRING) String client_id();

    @AttributeDefinition(name = "Girl Scouts Client Secret", description = "Girl Scouts Client Secret", type = AttributeType.STRING) String client_secret();

}
