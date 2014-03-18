package org.girlscouts.web.dataimport.impl;

import java.io.Reader;

import org.girlscouts.web.dataimport.DataImporter;

public class CsvDataImporter implements DataImporter {
    private Reader reader;

    public CsvDataImporter(Reader reader) {
	this.reader = reader;
    }
}