package org.girlscouts.vtk.mapper.mulesoft;

import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.rest.entity.mulesoft.AffiliationsEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AffiliationsEntityToTroopMapper {

    private static Logger log = LoggerFactory.getLogger(AffiliationsEntity.class);

    public static Troop map(AffiliationsEntity entity) {
        try {
            Troop troop = new Troop();
            troop.setRole("PA");
            try {
                troop.setId(entity.getAccountId());
                troop.setTroopId(entity.getAccountId());
                troop.setSfTroopId(entity.getAccountId());
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
                troop.setSfCouncil(String.valueOf(entity.getCouncilCode()));
            } catch (Exception ex) {
                log.error("Error occurred mapping CouncilCode to Troop ", ex);
            }
            try {
                troop.setCouncilId(entity.getCouncilCode());
            } catch (Exception ex) {
                log.error("Error occurred mapping AccountCode to Troop ", ex);
            }
            try {
                troop.setGradeLevel(entity.getProgramGradeLevel());
                troop.setSfTroopAge(entity.getProgramGradeLevel());
            } catch (Exception ex) {
                log.error("Error occurred mapping GradeLevel to Troop ", ex);
            }
            try {
                troop.setParticipationCode(entity.getType());
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
