package org.girlscouts.vtk.osgi.conf;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Girl Scouts VTK Manual Troop Load Service configuration", description = "Girl Scouts VTK Manual Troop Load Service configuration")
public @interface VTKTroopIdMigrationConfig {
    @AttributeDefinition(name = "Active", type = AttributeType.BOOLEAN) String active();
    @AttributeDefinition(name = "Troop Id Mapping File Path", type = AttributeType.STRING) String path();
}
