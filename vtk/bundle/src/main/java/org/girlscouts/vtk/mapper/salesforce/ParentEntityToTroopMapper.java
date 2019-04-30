package org.girlscouts.vtk.mapper.salesforce;

import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.rest.entity.salesforce.ParentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParentEntityToTroopMapper {
    private static Logger log = LoggerFactory.getLogger(ParentEntityToTroopMapper.class);

    public static Troop map(ParentEntity entity) {
        try {
            Troop troop = new Troop();
            troop.setRole("PA");
            try {
                troop.setId(entity.getSfId());
                troop.setTroopId(entity.getSfId());
                troop.setSfTroopId(entity.getSfId());
            } catch (Exception ex) {
                log.error("Error occurred mapping sfParentId to Troop ", ex);
            }
            try {
                troop.setTroopName(entity.getTroopName());
                troop.setSfTroopName(entity.getTroopName());
            } catch (Exception ex) {
                log.error("Error occurred mapping TroopName to Troop ", ex);
            }
            try {
                troop.setCouncilCode(entity.getCouncilCode());
                troop.setSfCouncil(String.valueOf(entity.getCouncilCode()));
            } catch (Exception ex) {
                log.error("Error occurred mapping CouncilCode to Troop ", ex);
            }
            try {
                troop.setCouncilId(entity.getAccountCode());
            } catch (Exception ex) {
                log.error("Error occurred mapping AccountCode to Troop ", ex);
            }
            try {
                troop.setGradeLevel(entity.getGradeLevel());
                troop.setSfTroopAge(entity.getGradeLevel());
            } catch (Exception ex) {
                log.error("Error occurred mapping GradeLevel to Troop ", ex);
            }
            try {
                troop.setParticipationCode(entity.getParticipationCode());
            } catch (Exception ex) {
                log.error("Error occurred mapping ParticipationCode to Troop ", ex);
            }
            return troop;
        } catch (Exception e) {
            log.error("Error occurred mapping ParentEntity to Troop ", e);
        }
        return null;
    }
}
