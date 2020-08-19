package org.girlscouts.common.osgi.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Girl Scouts Web To Lead Configuration Service")
public @interface WebToLeadConfig {
    @AttributeDefinition(name = "Organization ID") String oid() default "00DG0000000leqU";

    @AttributeDefinition(name = "Form submit path") String apiURL() default "https://pub.s6.exacttarget.com/3la2yxuyhd1";
}
