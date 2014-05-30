package org.girlsscout.vtk.dao;

import org.girlsscout.vtk.models.YearPlan;

public interface YearPlanDAO {
	 
	java.util.List <YearPlan> getAllYearPlans(String ageLevel);
	java.util.List <YearPlan> test();
	 
}
