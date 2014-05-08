package org.girlscouts.vtk.model;

/**
 * Represents an agenda item in a meeting.
 * @author mike
 *
 */
public interface AgendaItem {
    String getName();
    
    /**
     * @return duration in minutes
     */
    int getDuration();
    
    /**
     * @return agenda item detail in HTML
     */
    String getContent();
}
