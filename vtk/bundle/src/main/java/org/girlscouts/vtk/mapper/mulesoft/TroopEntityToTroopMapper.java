package org.girlscouts.vtk.mapper.mulesoft;

import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.rest.entity.mulesoft.TroopEntity;
import org.girlscouts.vtk.rest.entity.mulesoft.VolunteerJobsEntity;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TroopEntityToTroopMapper {
    private static Logger log = LoggerFactory.getLogger(TroopEntityToTroopMapper.class);

    public static Troop map(TroopEntity entity) {
        log.debug("mapping troop entity: "+entity);
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
                troop.setGradeLevel(entity.getProgramGradeLevel());
                troop.setSfTroopAge(entity.getProgramGradeLevel());
            } catch (Exception ex) {
                log.error("Error occurred mapping GradeLevel to Troop ", ex);
            }
            troop.setParticipationCode("Troop");
            List<VolunteerJobsEntity> jobs = entity.getVolunteerJobs();
            if (jobs != null) {
                for (VolunteerJobsEntity jobEntity : jobs) {
                    try {
                        String endDate = jobEntity.getEndDate();
                        String startDate = jobEntity.getStartDate();
                        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
                        DateTime start = formatter.parseDateTime(startDate);
                        DateTime end = formatter.parseDateTime(endDate);
                        if (start.isBeforeNow() && end.isAfterNow()){
                            if("Troop/Program Leader".equals(jobEntity.getJobCode())){
                                troop.setRole("DP");
                            }
                            if("Finance/Administration".equals(jobEntity.getJobCode())){
                                troop.setRole("FA");
                            }
                        }
                    } catch (Exception e) {
                        log.error("Error occurred mapping {} to role ",jobEntity, e);
                    }
                }
            }
            log.debug("mapped entity to troop: "+troop);
            return troop;
        } catch (Exception e) {
            log.error("Error occurred mapping TroopEntity to Troop ", e);
        }
        return null;
    }
}
