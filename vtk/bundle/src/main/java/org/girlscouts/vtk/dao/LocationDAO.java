package org.girlscouts.vtk.dao;

import org.girlscouts.vtk.models.Location;
import org.girlscouts.vtk.models.user.User;

public interface LocationDAO {
	
	public void removeLocation( User user, String name );
}
