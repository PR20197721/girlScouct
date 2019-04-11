package org.girlscouts.vtk.osgi.conf;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Girl Scouts VTK SalesForce file client configuration", description = "Girl Scouts VTK SalesForce file client configuration")
public @interface GirlScoutsSalesForceFileClientConfig {

    @AttributeDefinition(name = "Path to json directory", description = "Path to json directory in repository", type = AttributeType.STRING)
    String localJsonPath();
}
