package org.girlscouts.vtk.osgi.conf;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Girl Scouts VTK Manual Troop Load Service configuration", description = "Girl Scouts VTK Manual Troop Load Service configuration")
public @interface GirlScoutsManualTroopLoadServiceConfig {
    @AttributeDefinition(name = "Path to SalesForce Data file", description = "Path to SalesForce Data file", type = AttributeType.STRING) String localDataPath();

    @AttributeDefinition(name = "Active", description = "Active", type = AttributeType.BOOLEAN) boolean active();

}
