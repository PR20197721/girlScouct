package org.girlscouts.web.dataimport.impl;

import java.io.Reader;

import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.web.dataimport.DataImporter;
import org.girlscouts.web.dataimport.DataImporterFactory;
import org.girlscouts.web.exception.GirlScoutsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataImporterFactoryImpl implements DataImporterFactory {
    private static Logger log = LoggerFactory.getLogger(DataImporterFactoryImpl.class);

    public DataImporter getDataImporter(String type, Reader reader, ResourceResolver rr, String confPath, String destPath) {
	try {
	    if ("csv".equalsIgnoreCase(type)) {
		return new CsvDataImporter(reader, rr, confPath, destPath);
	    }
	} catch (GirlScoutsException e) {
	    log.error("Cannot get data import for type " + type);
	}
	return null;
    }

    public DataImporter getDataImporter(String type, Reader reader) {
	// TODO Auto-generated method stub
	return null;
    }
}