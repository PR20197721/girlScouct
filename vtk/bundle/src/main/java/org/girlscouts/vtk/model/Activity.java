package org.girlscouts.vtk.model;

import java.util.Date;

public interface Activity extends YearPlanComponent {
    Date getEndType();
    String getContent();
    Location getLocation();
}
