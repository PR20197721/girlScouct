package org.girlscouts.vtk.mapper;

import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.rest.entity.salesforce.ParentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParentEntityToTroopMapper {
    private static Logger log = LoggerFactory.getLogger(ParentEntityToTroopMapper.class);

    public Troop map(ParentEntity entity, Troop troop){
        try {

        }catch(Exception e){
            log.error("Error occurred mapping ParentEntity to Troop ", e);
        }
        return troop;
    }
}
