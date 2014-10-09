package org.girlscouts.vtk.dao;

import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.UserGlobConfig;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.Asset;

public interface TroopDAO {

	
	public Troop getTroop(String councilId, String troopId);
	public Troop getTroop_byPath(String troopPath);
	public YearPlan addYearPlan1( Troop troop, String yearPlanPath ) throws IllegalAccessException;
	public boolean updateTroop(Troop troop) throws IllegalAccessException;
	public void rmTroop(Troop troop) throws IllegalAccessException;
	public UserGlobConfig getUserGlobConfig();
	public void updateUserGlobConfig();

	
}
