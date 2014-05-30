package org.girlscouts.vtk.models.api;

import java.util.Date;

/**
 * An activity in the year plan.
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
