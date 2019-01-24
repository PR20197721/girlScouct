package org.girlscouts.common.dataimport.impl;

import java.io.Reader;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.common.dataimport.DataImporter;
import org.girlscouts.common.dataimport.DataImporterFactory;
import org.girlscouts.common.exception.GirlScoutsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service(value = DataImporterFactory.class)
@Properties({
		@Property(name = "service.pid", value = "org.girlscouts.common.dataimport.dataimportfactory", propertyPrivate = false),
	@Property(name = "service.description", value = "Girl Scouts data import service", propertyPrivate = false),
	@Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate = false) })
public class DataImporterFactoryImpl implements DataImporterFactory {
    private static Logger log = LoggerFactory
	    .getLogger(DataImporterFactoryImpl.class);

    public DataImporter getDataImporter(String type, Reader reader,
	    ResourceResolver rr, String confPath, String destPath) {
	try {
	    if ("csv".equalsIgnoreCase(type)) {
		return new CsvDataImporter(reader, rr, confPath, destPath);
	    }
	} catch (GirlScoutsException e) {
	    log.error("Cannot get data import for type " + type + ". Reason: "
		    + e.getReason());
	}
	return null;
    }

    public DataImporter getDataImporter(String type, Reader reader) {
	// TODO Auto-generated method stub
	return null;
    }
}