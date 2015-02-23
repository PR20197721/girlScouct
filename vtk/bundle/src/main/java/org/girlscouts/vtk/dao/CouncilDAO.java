package org.girlscouts.vtk.dao;

import org.girlscouts.vtk.models.Council;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.Milestone;

public interface CouncilDAO {

	public Council findCouncil(User user, String councilId)
			throws IllegalAccessException;

	public Council createCouncil(User user, String councilId)
			throws IllegalAccessException;

	public Council getOrCreateCouncil(User user, String councilId)
			throws IllegalAccessException;

	public void updateCouncil(User user, Council council)
			throws IllegalAccessException;
	
	public java.util.List<Milestone> getCouncilMilestones(String councilCode);
	
	public void updateCouncilMilestones(java.util.List<Milestone> milestones, String councilCode);

	public java.util.List<Milestone> getAllMilestones(String councilCode);

}
