package org.girlscouts.vtk.model;

import java.util.List;

public interface MeetingContent {
    // B2014B4
    String getId();
    // Brownie
    String getLevel();
    // Inventor Part One
    String getName();
    // Descriptive phase
    String getBlurb();
    // Science / Financial 
    String getCategory();
    
    List<Asset> getAids();
    List<Asset> getResources();
    
    List<AgendaItem> getAgendaItems();
}
