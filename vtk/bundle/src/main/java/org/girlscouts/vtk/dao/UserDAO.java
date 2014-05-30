package org.girlscouts.vtk.dao;

import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.user.User;

public interface UserDAO {

	
	public User getUser(String userId);
	public YearPlan addYearPlan( User user, String yearPlanPath );
	public void updateUser(User user);
	
}
