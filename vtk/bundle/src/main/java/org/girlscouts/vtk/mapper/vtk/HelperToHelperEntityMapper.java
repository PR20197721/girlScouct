package org.girlscouts.vtk.mapper.vtk;

import org.girlscouts.vtk.models.Helper;
import org.girlscouts.vtk.rest.entity.vtk.HelperEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelperToHelperEntityMapper extends BaseModelToEntityMapper {

    private static Logger log = LoggerFactory.getLogger(HelperToHelperEntityMapper.class);

    public static HelperEntity map(Helper helper) {
        if (helper != null) {
            try {
                HelperEntity entity = new HelperEntity();
                entity.setAchievementCurrent(helper.getAchievementCurrent());
                entity.setAttendanceCurrent(helper.getAttendanceCurrent());
                entity.setAttendanceTotal(helper.getAttendanceTotal());
                entity.setCurrentDate(helper.getCurrentDate());
                entity.setNextDate(helper.getNextDate());
                entity.setPermissions(helper.getPermissions());
                entity.setPrevDate(helper.getPrevDate());
                entity.setSfTroopAge(helper.getSfTroopAge());
                return entity;
            } catch (Exception e) {
                log.error("Error occurred mapping Helper to HelperEntity ", e);
            }
        }
        return null;
    }
}
