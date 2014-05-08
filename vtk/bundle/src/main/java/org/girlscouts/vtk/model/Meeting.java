package org.girlscouts.vtk.model;

import java.util.List;

/**
 * Represents a meeting in a year plan instance.
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
    
    // Set new content. Keep the date
    void setMeetingContent(MeetingContent content);
}
