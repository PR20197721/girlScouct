package org.girlscouts.vtk.dao;

import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.YearPlan;

public interface YearPlanDAO {
	 
	java.util.List <YearPlan> getAllYearPlans(String ageLevel);
	public YearPlan getYearPlan(String path) ;
	public java.util.Date getLastModif(Troop troop);
	public java.util.Date getLastModifByOthers(Troop troop, String sessionId);
	 
}
