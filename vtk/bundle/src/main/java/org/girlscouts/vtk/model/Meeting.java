package org.girlscouts.vtk.model;

import java.util.List;

/**
 * A meeting in a year plan instance.
 * This is a decorator of a {@link MeetingContent}
 * which adds fields like data, isCancelled and location.
 * 
 * @author mike
 *
 */
public interface Meeting extends YearPlanComponent {
    // proxies to MeetingContent 
    String getId();
    String getLevel();
    String getName();
    String getBlurb();
    String getCategory();
    List<Asset> getAids();
    List<Asset> getResources();
    List<AgendaItem> getAgendaItems();
    
    boolean isCancelled();
    void cancel();
    void resume();
    
    Location getLocation();
    void setLocation(Location location);
    
    /**
     * <b>Wipes out</b> the current meeting content and sets a new one. 
     * 
     * @param content   new meeting content.
     */
    void setMeetingContent(MeetingContent content);
    
    /**
     * @return a list of assets that the troop leader uploaded.
     *         e.g. photos of the meeting.
     */
    List<Asset> getUploads();

    /**
     * @return a list of archived reminder emails of this meeting.
     */
    List<EmailArchive> getReminders();
    
    /**
     * @return a list of archived summary emails of this meeting.
     */
    List<EmailArchive> getSummaries();
}
