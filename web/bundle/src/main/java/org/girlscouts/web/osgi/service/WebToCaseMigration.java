package org.girlscouts.web.osgi.service;

public interface WebToCaseMigration {
    void migrateWebToCaseForm(String path, boolean dryRun);
}
