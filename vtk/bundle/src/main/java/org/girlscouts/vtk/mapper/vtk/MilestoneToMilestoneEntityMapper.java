package org.girlscouts.vtk.mapper.vtk;

import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.rest.entity.vtk.MilestoneEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MilestoneToMilestoneEntityMapper extends BaseModelToEntityMapper {
    private static Logger log = LoggerFactory.getLogger(MilestoneToMilestoneEntityMapper.class);

    public static MilestoneEntity map(Milestone milestone) {
        if (milestone != null) {
            try {
                MilestoneEntity entity = new MilestoneEntity();
                entity.setBlurb(milestone.getBlurb());
                entity.setDate(milestone.getDate());
                entity.setPath(milestone.getPath());
                entity.setShow(milestone.getShow());
                entity.setType(milestone.getType());
                entity.setUid(milestone.getUid());
                return entity;
            } catch (Exception e) {
                log.error("Error occurred mapping Milestone to MilestoneEntity ", e);
            }
        }
        return null;
    }
}
