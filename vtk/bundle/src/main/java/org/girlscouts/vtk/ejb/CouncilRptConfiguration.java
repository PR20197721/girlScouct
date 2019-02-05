package org.girlscouts.vtk.ejb;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Girl Scouts VTK Report Email Configuration", description = "Girl Scouts VTK Report Email Configuration")
public @interface CouncilRptConfiguration {
	
	@AttributeDefinition(name = "From:", description = "From Email Address", type = AttributeType.STRING)
	String fromEmailAddress();
	
	@AttributeDefinition(name = "To:", description = "To Email Addresses", type = AttributeType.STRING)
	String[] toEmailAddresses();

}
