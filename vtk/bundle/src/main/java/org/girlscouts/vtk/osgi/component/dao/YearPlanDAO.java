package org.girlscouts.vtk.osgi.component.dao;

import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.YearPlan;

public interface YearPlanDAO {
    java.util.List<YearPlan> getAllYearPlans(User user, String ageLevel);

    YearPlan getYearPlan(String path);

    java.util.Date getLastModif(Troop troop);

    java.util.Date getLastModifByOthers(Troop troop, String sessionId);

    YearPlan getYearPlanJson(String yearPlanPath);

}
