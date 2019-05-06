package org.girlscouts.vtk.mapper.ocm;

import org.girlscouts.vtk.models.Achievement;
import org.girlscouts.vtk.ocm.AchievementNode;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AchievementMapper {

    AchievementMapper INSTANCE = Mappers.getMapper( AchievementMapper.class );

    public AchievementNode achievementToAchievementNode(Achievement achievement);
}
