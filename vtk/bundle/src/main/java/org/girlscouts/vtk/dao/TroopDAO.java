package org.girlscouts.vtk.dao;

import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.UserGlobConfig;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.Asset;

public interface TroopDAO {

	
	public Troop getTroop(String councilId, String troopId);
	public Troop getTroop_byPath(String troopPath);
	
	public YearPlan addYearPlan( Troop troop, String yearPlanPath ) throws IllegalAccessException;
	public boolean updateTroop(Troop troop) throws IllegalAccessException;
	public void selectYearPlan(Troop troop, String yearPlanPath, String name) throws IllegalAccessException;
	public void rmTroop(Troop troop) throws IllegalAccessException;
	public void addAsset(Troop troop, String meetingUid,  Asset asset) throws IllegalAccessException;
	
	public UserGlobConfig getUserGlobConfig();
	public void updateUserGlobConfig();
	//public java.util.List getTroops();
	public void logout(Troop troop) throws IllegalAccessException;
	//public Troop createTroop(String councilId, String troopId);
	
}
