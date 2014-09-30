package org.girlscouts.vtk.dao;

import org.girlscouts.vtk.models.Council;

public interface CouncilDAO {

	public Council findCouncil(String councilId);
	public Council createCouncil(String councilId);
	public Council getOrCreateCouncil(String councilId);
	public void updateCouncil(Council council);
}
