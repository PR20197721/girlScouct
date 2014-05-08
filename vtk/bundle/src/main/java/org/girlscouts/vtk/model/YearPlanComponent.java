package org.girlscouts.vtk.model;

import java.util.Date;

public interface YearPlanComponent {
    YearPlanComponentType getType();
    Date getDate();
    void setDate(Date date);
}
