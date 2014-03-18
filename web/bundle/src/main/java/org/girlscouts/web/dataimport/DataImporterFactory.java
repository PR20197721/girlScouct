package org.girlscouts.web.dataimport;

import java.io.Reader;

public interface DataImporterFactory {
    public DataImporter getDataImporter(String type, Reader reader);
}
