package org.girlscouts.web.osgi.service;

public interface JoinVolunteerMigration {
    public void migrateJoinLink(String path, boolean dryRun, Boolean addTargetValueToHref,Boolean onlyToAddHref);
    public void migrateVolunteerLink(String path, boolean dryRun, Boolean addTargetValueToHref,Boolean onlyToAddHref);
    public void migrateRenewLink(String path, boolean dryRun, Boolean addTargetValueToHref,Boolean onlyToAddHref);

    public String getOldJoin();

    public String getNewJoin();

    public String getOldVolunteer();

    public String getNewVolunteer();

    public String getOldRenew();

    public String getNewRenew();
}
