package org.girlscouts.web.dataimport.impl;

import java.io.Reader;

import org.girlscouts.web.dataimport.DataImporter;
import org.girlscouts.web.dataimport.DataImporterFactory;

public class DataImporterFactoryImpl implements DataImporterFactory {

    public DataImporter getDataImporter(String type, Reader reader) {
	if ("csv".equalsIgnoreCase(type)) {
	    return new CsvDataImporter(reader);
	}
	return null;
    }
}