package org.girlscouts.vtk.dao;

import org.girlscouts.vtk.models.YearPlan;

public interface YearPlanDAO {
	 
	java.util.List <YearPlan> getAllYearPlans(String ageLevel);
	java.util.List <YearPlan> test();
	public YearPlan getYearPlan(String path) ;
	 
}
