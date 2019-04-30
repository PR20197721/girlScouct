package org.girlscouts.vtk.mapper.vtk;

import org.girlscouts.vtk.models.YearPlanComponent;
import org.girlscouts.vtk.rest.entity.vtk.YearPlanComponentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YearPlanComponentToYearPlanComponentEntityMapper {

    private static Logger log = LoggerFactory.getLogger(YearPlanComponentToYearPlanComponentEntityMapper.class);

    public static YearPlanComponentEntity map(YearPlanComponent yearPlanComponent){
        if(yearPlanComponent != null) {
            try {
                YearPlanComponentEntity entity = new YearPlanComponentEntity();
                entity.setDate(yearPlanComponent.getDate());
                entity.setType(yearPlanComponent.getType());
                entity.setUid(yearPlanComponent.getUid());
                return entity;
            } catch (Exception e) {
                log.error("Error occurred mapping YearPlanComponent to YearPlanComponentEntity ", e);
            }
        }
        return null;
    }
}
