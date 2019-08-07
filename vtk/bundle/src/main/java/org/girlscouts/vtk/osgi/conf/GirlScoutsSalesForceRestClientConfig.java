package org.girlscouts.vtk.osgi.conf;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Girl Scouts VTK SalesForce Rest client configuration", description = "Girl Scouts VTK SalesForce Rest client configuration")
public @interface GirlScoutsSalesForceRestClientConfig {
    @AttributeDefinition(name = "SalesForce rest service url", description = "SalesForce rest service url", type = AttributeType.STRING) String sfRestAPI();

    @AttributeDefinition(name = "SalesForce user info service endpoint", description = "SalesForce user info service endpoint", type = AttributeType.STRING) String sfUserInfoURI();

    @AttributeDefinition(name = "SalesForce troop info service endpoint", description = "SalesForce troop info service endpoint", type = AttributeType.STRING) String sfTroopInfoURI();

    @AttributeDefinition(name = "SalesForce troop leader info service endpoint", description = "SalesForce troop leader info service endpoint", type = AttributeType.STRING) String sfTroopLeaderInfoURI();

    @AttributeDefinition(name = "SalesForce contacts info service endpoint", description = "SalesForce contacts info service endpoint", type = AttributeType.STRING) String sfContactsInfoURI();

    @AttributeDefinition(name = "SalesForce JWT AUTH service endpoint", description = "SalesForce JWT AUTH service endpoint", type = AttributeType.STRING) String sfJWTURI();

    @AttributeDefinition(name = "Girl Scouts Certificate", description = "Girl Scouts Certificate", type = AttributeType.STRING) String gsCertificate();

    @AttributeDefinition(name = "Girl Scouts Client Id", description = "Girl Scouts Client Id", type = AttributeType.STRING) String clientId();

}
