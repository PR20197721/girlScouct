package org.girlscouts.web.osgi.service;

public interface WebToLeadMigration {
    void migrateWebToLeadForm(String path, boolean dryRun);
}
