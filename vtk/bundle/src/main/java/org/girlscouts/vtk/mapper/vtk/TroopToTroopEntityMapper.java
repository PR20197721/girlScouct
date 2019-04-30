package org.girlscouts.vtk.mapper.vtk;

import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.rest.entity.vtk.TroopEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TroopToTroopEntityMapper extends BaseModelToEntityMapper {

    private static Logger log = LoggerFactory.getLogger(TroopToTroopEntityMapper.class);

    public static TroopEntity map(Troop troop){
        if(troop != null) {
            try {
                TroopEntity entity = new TroopEntity();
                entity.setId(troop.getId());
                entity.setTroopId(troop.getTroopId());
                entity.setSfTroopId(troop.getSfTroopId());
                entity.setCouncilCode(troop.getCouncilCode());
                entity.setCouncilId(troop.getCouncilId());
                entity.setCouncilPath(troop.getCouncilPath());
                entity.setCurrentTroop(troop.getCurrentTroop());
                entity.setDbUpdate(troop.isDbUpdate());
                entity.setErrCode(troop.getErrCode());
                entity.setGradeLevel(troop.getGradeLevel());
                entity.setHash(troop.getHash());
                entity.setParticipationCode(troop.getParticipationCode());
                entity.setPath(troop.getPath());
                entity.setPermissionTokens(troop.getPermissionTokens());
                entity.setRefId(troop.getRefId());
                entity.setRefresh(troop.isRefresh());
                entity.setRetrieveTime(troop.getRetrieveTime());
                entity.setRole(troop.getRole());
                entity.setSendingEmail(EmailMeetingReminderEntityMapper.map(troop.getSendingEmail()));
                entity.setSfCouncil(troop.getSfCouncil());
                entity.setSfTroopAge(troop.getSfTroopAge());
                entity.setSfTroopName(troop.getSfTroopName());
                entity.setSfUserId(troop.getSfUserId());
                entity.setTroopName(troop.getTroopName());
                entity.setType(troop.getType());
                entity.setYearPlan(YearPlanToYearPlanEntityMapper.map(troop.getYearPlan()));
                return entity;
            } catch (Exception e) {
                log.error("Error occurred mapping Troop to TroopEntity ", e);
            }
        }
        return null;
    }
}
