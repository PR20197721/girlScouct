package org.girlscouts.vtk.dao;

import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.UserGlobConfig;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.Asset;

public interface TroopDAO {

	
	public Troop getTroop(String councilId, String troopId);
	public Troop getTroop_byPath(String troopPath);
	
	public YearPlan addYearPlan( Troop troop, String yearPlanPath );
	public boolean updateTroop(Troop troop);
	public void selectYearPlan(Troop troop, String yearPlanPath, String name);
	public void rmTroop(Troop troop);
	public void addAsset(Troop troop, String meetingUid,  Asset asset);
	
	public UserGlobConfig getUserGlobConfig();
	public void updateUserGlobConfig();
	public java.util.List getTroops();
	public void logout(Troop troop);
	public Troop createTroop(String councilId, String troopId);
	
}
