package org.girlscouts.vtk.dao;

import org.girlscouts.vtk.models.Council;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.utils.VtkException;

public interface CouncilDAO {

	public Council findCouncil(User user, String councilId)
			throws IllegalAccessException, VtkException;

	public Council createCouncil(User user, String councilId)
			throws IllegalAccessException, VtkException;

	public Council getOrCreateCouncil(User user, String councilId)
			throws IllegalAccessException, VtkException;

	public void updateCouncil(User user, Council council)
			throws IllegalAccessException;
	
	public java.util.List<Milestone> getCouncilMilestones(User user, String councilCode)
			throws IllegalAccessException;	
	public void updateCouncilMilestones(User user, java.util.List<Milestone> milestones, String councilCode)
			throws IllegalAccessException;
	
	public void GSMonthlyRpt();

}
