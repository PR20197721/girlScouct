package org.girlscouts.vtk.mapper.vtk;

import org.girlscouts.vtk.models.*;
import org.girlscouts.vtk.rest.entity.vtk.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ModelToRestEntityMapper {
    ModelToRestEntityMapper INSTANCE = Mappers.getMapper(ModelToRestEntityMapper.class);

    AchievementEntity toEntity(Achievement achievement);

    ActivityEntity toEntity(Activity activity);

    AssetEntity toEntity(Asset asset);

    AttendanceEntity toEntity(Attendance attendance);

    CalEntity toEntity(Cal cal);

    JcrCollectionHoldStringEntity toEntity(JcrCollectionHoldString jcrCollectionHoldString);

    LocationEntity toEntity(Location location);

    MeetingCanceledEntity toEntity(MeetingCanceled meetingCanceled);

    MeetingEntity toEntity(Meeting meeting);

    MeetingEEntity toEntity(MeetingE meetingE);

    MilestoneEntity toEntity(Milestone milestone);

    NoteEntity toEntity(Note note);

    SentEmailEntity toEntity(SentEmail sentEmail);

    TroopEntity toEntity(Troop troop);

    YearPlanComponentEntity toEntity(YearPlanComponent yearPlanComponent);

    YearPlanEntity toEntity(YearPlan yearPlan);
}