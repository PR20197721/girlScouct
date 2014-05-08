package org.girlscouts.vtk.model;

import java.util.Date;

/**
 * Represents an activity in the year plan.
 * e.g. go skiing, go bowling
 * 
 * @author mike
 * 
 */
public interface Activity extends YearPlanComponent {
    Date getEndDate();
    String getContent();
    Location getLocation();
}
