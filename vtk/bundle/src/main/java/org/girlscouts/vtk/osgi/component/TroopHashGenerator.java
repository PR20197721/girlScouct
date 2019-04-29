package org.girlscouts.vtk.helpers;

import org.girlscouts.vtk.models.Troop;

public interface TroopHashGenerator {
    String hash(String troopId);
    String hash(Troop troop);
    String getPath(String troopId);
    String getPath(Troop troop);
    String getBase();
}