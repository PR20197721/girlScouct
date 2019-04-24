package org.girlscouts.vtk.dao;

import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Finance;
import org.girlscouts.vtk.models.FinanceConfiguration;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.utils.VtkException;

import java.util.Map;

public interface TroopDAO {

	public Troop getTroop(User user, String councilId, String troopId) throws IllegalAccessException, VtkException;

	public Troop getTroop_byPath(User user, String troopPath) throws IllegalAccessException;

	public YearPlan addYearPlan1(User user, Troop troop, String yearPlanPath) throws IllegalAccessException;

	public boolean updateTroop(User user, Troop troop) throws IllegalAccessException, VtkException;

	public void rmTroop(Troop troop) throws IllegalAccessException;

	public Finance getFinances(Troop troop, int qtr, String currentYear);

	public void setFinances(Troop troop, int qtr, String currentYear, Map<String, String[]> params);
	
	public FinanceConfiguration getFinanceConfiguration(Troop troop, String currentYear);
	
	public void setFinanceConfiguration(Troop troop, String currentYear, String income, String expenses, String period, String recipient);
	
	public boolean removeActivity(User user, Troop troop, Activity activity) throws IllegalAccessException ;
	
	public boolean removeMeeting(User user, Troop troop, MeetingE meeting) throws IllegalAccessException;
	
	public boolean removeAsset(User user, Troop troop, Asset asset) throws IllegalAccessException ;
	
	public boolean removeMeetings(User user, Troop troop) throws IllegalAccessException;
	
	public void removeDemoTroops();

	public Map getArchivedYearPlans(User user , Troop troop);

	public boolean isArchivedYearPlan( User user , Troop troop, String year);

}
