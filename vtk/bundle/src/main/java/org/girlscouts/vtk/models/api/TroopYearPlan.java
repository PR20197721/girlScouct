package org.girlscouts.vtk.models.api;

import java.util.Date;
import java.util.List;

/**
 * A year plan instance of a particular troop.
 * 
 * @author mike
 *
 */
public interface TroopYearPlan extends YearPlan {
    /**
     * @return a troop ID.
     */
    String getTroopId();
    
    /**
     * @return a list of {@link Location}
     */
    List<Location> getLocations();
    
    /**
     * A convenient method to fetch archived reminder email of this troop.
     * 
     * @param  start   start date (inclusive) <code>null</code> if not specified
     * @param  end     end date (inclusive) <code>null</code> if not specified
     * @return archived reminder emails
     */
    List<EmailArchive> getEmailArchives(EmailArchiveType type, Date start, Date end);
    
    /**
     * Convenient method for #getReminders(EmailArchiveType, null, null);
     */
    List<EmailArchive> getEmailArchives(EmailArchiveType type);
}
