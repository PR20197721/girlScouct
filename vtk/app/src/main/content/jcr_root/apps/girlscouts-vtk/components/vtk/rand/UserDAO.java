package org.girlscouts.vtk.dao;

import java.util.Set;

import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.UserGlobConfig;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.Asset;

public interface UserDAO {

	
	public Troop getUser(String userId);
	public YearPlan addYearPlan( Troop user, String yearPlanPath );
	public boolean updateUser(Troop user);
	public void selectYearPlan(Troop user, String yearPlanPath, String name);
	public void rmUser(Troop user);
	public void addAsset(Troop user, String meetingUid,  Asset asset);
	
	public UserGlobConfig getUserGlobConfig();
	public void updateUserGlobConfig();
	public java.util.List getUsers();
	public void logout(Troop user);
	
	
}
