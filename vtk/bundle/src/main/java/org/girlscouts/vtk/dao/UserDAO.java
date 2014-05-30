package org.girlscouts.vtk.dao;

import org.girlsscout.vtk.models.YearPlan;
import org.girlsscout.vtk.models.user.User;

public interface UserDAO {

	
	public User getUser(String userId);
	public YearPlan addYearPlan( User user, String yearPlanPath );
	public void updateUser(User user);
	
}
