package org.girlscouts.vtk.mapper.mulesoft;

import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.rest.entity.mulesoft.TroopEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TroopEntityToTroopMapper {
    private static Logger log = LoggerFactory.getLogger(TroopEntityToTroopMapper.class);

    public static Troop map(TroopEntity entity) {
        try {
            Troop troop = new Troop();
            try {
                troop.setTroopId(entity.getId());
                troop.setId(entity.getId());
                troop.setSfTroopId(entity.getId());
            } catch (Exception ex) {
                log.error("Error occurred mapping sfId to Troop ", ex);
            }
            try {
                troop.setSfParentId(entity.getParentAccount().getId());
            } catch (Exception ex) {
                log.error("Error occurred mapping sfParentId to Troop ", ex);
            }
            try {
                troop.setTroopName(entity.getName());
                troop.setSfTroopName(entity.getName());
            } catch (Exception ex) {
                log.error("Error occurred mapping TroopName to Troop ", ex);
            }
            try {
                troop.setCouncilCode(entity.getCouncilCode());
                troop.setSfCouncil(entity.getCouncilCode());
                troop.setCouncilId(entity.getCouncilCode());
            } catch (Exception ex) {
                log.error("Error occurred mapping CouncilCode to Troop ", ex);
            }
            try {
                troop.setGradeLevel(entity.getParentEntity().getGradeLevel());
                troop.setSfTroopAge(entity.getParentEntity().getGradeLevel());
            } catch (Exception ex) {
                log.error("Error occurred mapping GradeLevel to Troop ", ex);
            }
            try {
                troop.setRole(entity.getJobCode());
            } catch (Exception ex) {
                log.error("Error occurred mapping JobCode to Troop ", ex);
            }
            try {
                troop.setParticipationCode(entity.getParentEntity().getParticipationCode());
            } catch (Exception ex) {
                log.error("Error occurred mapping Participation Code to Troop ", ex);
            }
            return troop;
        } catch (Exception e) {
            log.error("Error occurred mapping TroopEntity to Troop ", e);
        }
        return null;
    }
}
