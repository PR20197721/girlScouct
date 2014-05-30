package org.girlscouts.vtk.dao;

import org.girlsscout.vtk.models.Activity;
import org.girlsscout.vtk.models.ActivitySearch;
import org.girlsscout.vtk.models.user.User;

public interface ActivityDAO {

	public void createActivity(User user, Activity activity);
	public java.util.List<Activity> search(ActivitySearch search);
}
