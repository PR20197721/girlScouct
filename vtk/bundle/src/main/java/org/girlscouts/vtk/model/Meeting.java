package org.girlscouts.vtk.model;

import java.util.List;

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
    
    // Set new content. Keep the date
    void setMeetingContent(MeetingContent content);
}
