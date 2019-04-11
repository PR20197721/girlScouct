package org.girlscouts.vtk.mapper;

import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.rest.entity.salesforce.TroopEntity;

public class TroopEntityToTroopMapper {
    public Troop map(TroopEntity entity, Troop troop){

        return troop;
    }
}
