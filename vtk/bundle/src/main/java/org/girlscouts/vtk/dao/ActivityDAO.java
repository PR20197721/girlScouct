package org.girlscouts.vtk.dao;

import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.ActivitySearch;
import org.girlscouts.vtk.models.Troop;

public interface ActivityDAO {

	public void createActivity(Troop user, Activity activity);
	public java.util.List<Activity> search(ActivitySearch search);
	public void updateActivitiesCancel( String uuid );
	public boolean isActivity( String uuid );
	public void checkCanceledActivity(Troop user);
	
}
