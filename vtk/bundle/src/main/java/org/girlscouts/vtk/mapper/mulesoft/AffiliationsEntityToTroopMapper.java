package org.girlscouts.vtk.mapper.mulesoft;

import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.rest.entity.mulesoft.AffiliationsEntity;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
            try {
                String startDate = entity.getStartDate();
                if(startDate != null) {
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
                    DateTime dt = formatter.parseDateTime(startDate);
                    troop.setStartDate(dt);
                }
            } catch (Exception ex) {
                log.error("Error occurred mapping start date to Troop ", ex);
            }
            try {
                String endDate = entity.getEndDate();
                if(endDate != null) {
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
                    DateTime dt = formatter.parseDateTime(endDate);
                    troop.setEndDate(dt);
                }
            } catch (Exception ex) {
                log.error("Error occurred mapping start date to Troop ", ex);
            }
            return troop;
        } catch (Exception e) {
            log.error("Error occurred mapping ParentEntity to Troop ", e);
        }
        return null;
    }
}
