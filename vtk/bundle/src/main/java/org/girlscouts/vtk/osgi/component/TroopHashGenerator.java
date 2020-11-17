package org.girlscouts.vtk.osgi.component;

import org.girlscouts.vtk.models.Troop;

public interface TroopHashGenerator {

    String hash(Troop troop);

    String getCachePath(String troopId);

    String getBase();
}