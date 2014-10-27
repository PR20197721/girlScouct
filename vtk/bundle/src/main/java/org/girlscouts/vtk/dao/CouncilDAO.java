package org.girlscouts.vtk.dao;

import org.girlscouts.vtk.models.Council;
import org.girlscouts.vtk.models.User;

public interface CouncilDAO {

	public Council findCouncil(User user, String councilId) throws IllegalAccessException;
	public Council createCouncil(User user, String councilId) throws IllegalAccessException;
	public Council getOrCreateCouncil(User user, String councilId) throws IllegalAccessException;
	public void updateCouncil(User user, Council council) throws IllegalAccessException;
}
