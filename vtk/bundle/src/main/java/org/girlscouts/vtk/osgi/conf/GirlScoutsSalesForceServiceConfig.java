package org.girlscouts.vtk.osgi.conf;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Girl Scouts VTK SalesForce file client configuration", description = "Girl Scouts VTK SalesForce file client configuration")
public @interface GirlScoutsSalesForceServiceConfig {

    @AttributeDefinition(name = "Load json from file", description = "Force VTK to load json from file in repository", type = AttributeType.BOOLEAN)
    boolean isLoadFromFile();

    @AttributeDefinition(name = "Demo Council Code", description = "Three digit code to be used for demo council", type = AttributeType.INTEGER)
    int demoCouncilCode();

}
