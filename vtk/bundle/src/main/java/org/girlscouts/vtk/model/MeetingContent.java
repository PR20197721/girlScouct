package org.girlscouts.vtk.model;

import java.util.List;

/**
 * Meeting content.
 * It is either a template which is parsed from docx
 * or a troop leader customize content (mostly with her own agenda).
 * 
 * @author mike
 *
 */
public interface MeetingContent {
    /**
     * @return e.g. B2014B4
     */
    String getId();
    
    /**
     * @return e.g. Brownie
     */
    String getLevel();
    
    /**
     * @return e.g. Inventor Part One
     */
    String getName();

    /**
     * @return e.g. Descriptive phase
     */
    String getBlurb();
    
    /**
     * @return e.g. Science / Financial 
     */
    String getCategory();
    
    List<Asset> getAids();
    List<Asset> getResources();
    
    List<AgendaItem> getAgendaItems();
}
