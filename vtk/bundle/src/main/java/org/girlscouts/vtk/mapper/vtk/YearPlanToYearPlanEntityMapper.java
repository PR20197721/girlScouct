package org.girlscouts.vtk.mapper.vtk;

import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.rest.entity.vtk.YearPlanEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YearPlanToYearPlanEntityMapper extends BaseModelToEntityMapper {
    private static Logger log = LoggerFactory.getLogger(YearPlanToYearPlanEntityMapper.class);

    public static YearPlanEntity map(YearPlan yearPlan) {
        if (yearPlan != null) {
            try {
                YearPlanEntity entity = new YearPlanEntity();
                entity.setActivities(mapActivities(yearPlan.getActivities()));
                entity.setAltered(yearPlan.getAltered());
                entity.setCalExclWeeksOf(yearPlan.getCalExclWeeksOf());
                entity.setCalFreq(yearPlan.getCalFreq());
                entity.setCalStartDate(yearPlan.getCalStartDate());
                entity.setDbUpdate(yearPlan.isDbUpdate());
                entity.setDesc(yearPlan.getDesc());
                entity.setHelper(HelperToHelperEntityMapper.map(yearPlan.getHelper()));
                entity.setId(yearPlan.getId());
                entity.setLocations(mapLocations(yearPlan.getLocations()));
                entity.setMeetingCanceled(mapMeetingsCanceled(yearPlan.getMeetingCanceled()));
                entity.setMeetingEvents(mapMeetingEvents(yearPlan.getMeetingEvents()));
                entity.setMilestones(mapMilestones(yearPlan.getMilestones()));
                entity.setName(yearPlan.getName());
                entity.setPath(yearPlan.getPath());
                entity.setRefId(yearPlan.getRefId());
                entity.setResources(yearPlan.getResources());
                entity.setSchedule(CalToCalEntityMapper.map(yearPlan.getSchedule()));
                return entity;
            } catch (Exception e) {
                log.error("Error occurred mapping YearPlan to YearPlanEntity ", e);
            }
        }
        return null;
    }
}
