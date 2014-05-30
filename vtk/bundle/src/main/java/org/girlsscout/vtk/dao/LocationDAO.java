package org.girlsscout.vtk.dao;

import org.girlsscout.vtk.models.Location;
import org.girlsscout.vtk.models.user.User;

public interface LocationDAO {
	
	public void removeLocation( User user, String name );
}
