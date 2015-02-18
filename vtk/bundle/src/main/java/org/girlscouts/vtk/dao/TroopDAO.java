package org.girlscouts.vtk.dao;

import org.girlscouts.vtk.models.Finance;
import org.girlscouts.vtk.models.FinanceConfiguration;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.UserGlobConfig;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.Asset;

public interface TroopDAO {

	public Troop getTroop(User user, String councilId, String troopId)
			throws IllegalAccessException;

	public Troop getTroop_byPath(User user, String troopPath)
			throws IllegalAccessException;

	public YearPlan addYearPlan1(User user, Troop troop, String yearPlanPath)
			throws IllegalAccessException;

	public boolean updateTroop(User user, Troop troop)
			throws IllegalAccessException;

	public void rmTroop(Troop troop) throws IllegalAccessException;

	public UserGlobConfig getUserGlobConfig();

	public void updateUserGlobConfig();

	public Finance getFinanaces(Troop troop, int qtr);

	public void setFinances(Troop troop, int qtr, java.util.Map<String, String[]> params);
	
	public FinanceConfiguration getFinanceConfiguration(Troop troop);
	
	public void setFinanceConfiguration(Troop troop, String income, String expenses);

}
