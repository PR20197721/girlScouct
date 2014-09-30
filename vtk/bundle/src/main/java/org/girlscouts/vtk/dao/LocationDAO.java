package org.girlscouts.vtk.dao;

import org.girlscouts.vtk.models.Troop;

public interface LocationDAO {
	
	public void removeLocation( Troop user, String name );
}
