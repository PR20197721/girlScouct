package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;

import java.util.List;

public interface GirlScoutsManualTroopLoadService {

    public List<Troop> loadTroops(User user);

    public boolean isActive();

}
