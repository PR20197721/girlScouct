package org.girlscouts.vtk.osgi.conf;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Girl Scouts VTK IRM Contacts cache configuration", description = "Girl Scouts VTK IRM Contacts cache configuration")
public @interface SalesForceIRMContactsResponseCacheConfig {
    @AttributeDefinition(name = "Enable Caching", description = "Check to enable caching", type = AttributeType.BOOLEAN) boolean isCacheEnabled();

    @AttributeDefinition(name = "Max Size", description = "Specifies the maximum number of entries the cache may contain.", type = AttributeType.INTEGER) int maxSize();

    @AttributeDefinition(name = "Expiration Period (in min)", description = "Specifies that each entry should be automatically removed from the cache once a fixed duration has elapsed after the entry's creation, or the most recent replacement of its value.", type = AttributeType.INTEGER) int expireAfter();
}
