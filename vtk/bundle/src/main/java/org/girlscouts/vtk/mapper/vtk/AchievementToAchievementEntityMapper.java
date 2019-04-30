package org.girlscouts.vtk.mapper.vtk;

import org.girlscouts.vtk.models.Achievement;
import org.girlscouts.vtk.rest.entity.vtk.AchievementEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AchievementToAchievementEntityMapper extends BaseModelToEntityMapper {

    private static Logger log = LoggerFactory.getLogger(AchievementToAchievementEntityMapper.class);

    public static AchievementEntity map(Achievement achievement){
        if(achievement != null) {
            try {
                AchievementEntity entity = new AchievementEntity();
                entity.setId(achievement.getId());
                entity.setPath(achievement.getPath());
                entity.setTotal(achievement.getTotal());
                entity.setUsers(achievement.getUsers());
                return entity;
            } catch (Exception e) {
                log.error("Error occurred mapping Achievement to AchievementEntity ", e);
            }
        }
        return null;
    }
}
