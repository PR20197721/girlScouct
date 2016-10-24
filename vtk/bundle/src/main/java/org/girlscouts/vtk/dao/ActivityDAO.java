package org.girlscouts.vtk.dao;

import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.ActivitySearch;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;

public interface ActivityDAO {

	public void createActivity(User user, Troop troop, Activity activity)
			throws IllegalStateException, IllegalAccessException;

	public boolean isActivity(User user, Troop troop ,String uuid)
			throws IllegalStateException, IllegalAccessException;

	public Activity findActivity(User user, String path)
			throws IllegalStateException, IllegalAccessException;

	public boolean isActivityByPath(User user, String path)
			throws IllegalStateException, IllegalAccessException;
	
	public boolean updateActivity(User user, Troop troop, Activity activity)
			throws IllegalAccessException, IllegalStateException;

}
