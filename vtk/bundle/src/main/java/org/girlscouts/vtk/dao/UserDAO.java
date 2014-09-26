package org.girlscouts.vtk.dao;

import java.util.Set;

import org.girlscouts.vtk.models.UserGlobConfig;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.user.User;
import org.girlscouts.vtk.models.Asset;

public interface UserDAO {

	
	public User getUser(String userId);
	public YearPlan addYearPlan( User user, String yearPlanPath );
	public boolean updateUser(User user);
	public void selectYearPlan(User user, String yearPlanPath, String name);
	public void rmUser(User user);
	public void addAsset(User user, String meetingUid,  Asset asset);
	
	public UserGlobConfig getUserGlobConfig();
	public void updateUserGlobConfig();
	public java.util.List getUsers();
	public void logout(User user);
	public  boolean hasPermission(Set<Integer> myPermissionTokens, int permissionId);
	
}
