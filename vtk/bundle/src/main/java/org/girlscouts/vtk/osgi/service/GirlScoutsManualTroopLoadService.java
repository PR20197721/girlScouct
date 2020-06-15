package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.Troop;

import java.util.List;

public interface GirlScoutsManualTroopLoadService {

    List<Troop> loadTroops(String userId);

}
