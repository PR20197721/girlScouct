package org.girlscouts.vtk.mapper;

import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.rest.entity.salesforce.TroopEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TroopEntityToTroopMapper {

    private static Logger log = LoggerFactory.getLogger(TroopEntityToTroopMapper.class);

    public Troop map(TroopEntity entity){
        try {
            Troop troop = new Troop();
            try {
                troop.setTroopId(entity.getSfParentId());
            }catch(Exception ex){
                log.error("Error occurred mapping sfParentId to Troop ", ex);
            }
            try {
                troop.setTroopName(entity.getParentEntity().getTroopName());
            }catch(Exception ex){
                log.error("Error occurred mapping TroopName to Troop ", ex);
            }
            try {
                troop.setCouncilCode(entity.getParentEntity().getCouncilCode());
            }catch(Exception ex){
                log.error("Error occurred mapping CouncilCode to Troop ", ex);
            }
            try {
                troop.setCouncilId(entity.getParentEntity().getAccountCode());
            }catch(Exception ex){
                log.error("Error occurred mapping AccountCode to Troop ", ex);
            }
            try {
                troop.setGradeLevel(entity.getParentEntity().getGradeLevel());
            }catch(Exception ex){
                log.error("Error occurred mapping GradeLevel to Troop ", ex);
            }
            try {
                troop.setRole(entity.getJobCode());
            }catch(Exception ex){
                log.error("Error occurred mapping JobCode to Troop ", ex);
            }
            try {
                troop.setRole(entity.getJobCode());
            }catch(Exception ex){
                log.error("Error occurred mapping JobCode to Troop ", ex);
            }
            return troop;
        }catch(Exception e){
            log.error("Error occurred mapping TroopEntity to Troop ", e);
        }
        return null;
    }
}
