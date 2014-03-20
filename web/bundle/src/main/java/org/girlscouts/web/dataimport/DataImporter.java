package org.girlscouts.web.dataimport;

import org.girlscouts.web.dataimport.impl.CsvDataImporter.LineError;
import org.girlscouts.web.exception.GirlScoutsException;

public interface DataImporter {
    static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SX";
    static String TMP_ROOT = "/tmp/gs-data-import";

    LineError[] doDryRun() throws GirlScoutsException;
    String[] doImport() throws GirlScoutsException;
}
