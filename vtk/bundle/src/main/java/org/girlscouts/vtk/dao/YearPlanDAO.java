package org.girlscouts.vtk.dao;

import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.YearPlan;

public interface YearPlanDAO {

	java.util.List<YearPlan> getAllYearPlans(User user, String ageLevel);

	public YearPlan getYearPlan(String path);

	public java.util.Date getLastModif(Troop troop);

	public java.util.Date getLastModifByOthers(Troop troop, String sessionId);
	public java.util.List<Meeting> getYearPlanJson( String yearPlanPath );

}
