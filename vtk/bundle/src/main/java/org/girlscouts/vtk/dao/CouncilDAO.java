package org.girlscouts.vtk.dao;

import org.girlscouts.vtk.models.Council;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.utils.VtkException;

import java.util.List;

public interface CouncilDAO {

	public Council findCouncil(User user, String path) throws IllegalAccessException, VtkException;

	public Council createCouncil(User user, Troop troop) throws IllegalAccessException, VtkException;

	public Council getOrCreateCouncil(User user, Troop troop) throws IllegalAccessException, VtkException;
	
	public List<Milestone> getCouncilMilestones(User user, Troop troop) throws IllegalAccessException;

	public void updateCouncilMilestones(User user, List<Milestone> milestones, Troop troop) throws IllegalAccessException;
	
	public void GSMonthlyRpt();

	public void GSMonthlyDetailedRpt(String year);
	
	public void GSRptCouncilPublishFinance();
	
	

}
