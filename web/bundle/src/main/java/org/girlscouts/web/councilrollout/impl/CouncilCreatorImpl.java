package org.girlscouts.web.councilrollout.impl;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.web.councilrollout.CouncilCreator;
import org.girlscouts.web.dataimport.impl.DataImporterFactoryImpl;
import org.girlscouts.web.exception.GirlScoutsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service(value = CouncilCreator.class)
@Properties({
	@Property(name = "service.pid", value = "org.girlscouts.web.councilrollout.councilcreator", propertyPrivate = false),
	@Property(name = "service.description", value = "Girl Scouts council rollout service", propertyPrivate = false),
	@Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate = false) })
public class CouncilCreatorImpl implements CouncilCreator{
    private static Logger log = LoggerFactory
    	    .getLogger(CouncilCreatorImpl.class);

	public String create() throws GirlScoutsException {
		// TODO Auto-generated method stub
log.error("In here");
String test = "In Here";
return test;
}
}