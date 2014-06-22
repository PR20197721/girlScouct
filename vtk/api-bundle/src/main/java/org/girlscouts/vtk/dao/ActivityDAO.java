package org.girlscouts.vtk.dao;

import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.ActivitySearch;
import org.girlscouts.vtk.models.user.User;

public interface ActivityDAO {

	public void createActivity(User user, Activity activity);
	public java.util.List<Activity> search(ActivitySearch search);
}
