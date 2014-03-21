package org.girlscouts.web.dataimport;

import org.girlscouts.web.exception.GirlScoutsException;

public interface DataImporter {
    static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SZ";
    static String TMP_ROOT = "/tmp/data-import/csv";

    String[] doDryRun() throws GirlScoutsException;
    String[] doImport() throws GirlScoutsException;
    String getDryRunPath() throws GirlScoutsException;
}
