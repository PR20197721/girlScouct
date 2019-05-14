package org.girlscouts.vtk.mapper.vtk;

import org.girlscouts.vtk.models.*;
import org.girlscouts.vtk.rest.entity.vtk.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ModelToRestEntityMapper {

    ModelToRestEntityMapper INSTANCE = Mappers.getMapper( ModelToRestEntityMapper.class );

    public AchievementEntity toEntity(Achievement achievement);

    public ActivityEntity toEntity(Activity activity);

    public AssetEntity toEntity(Asset asset);

    public AttendanceEntity toEntity(Attendance attendance);

    public CalEntity toEntity(Cal cal);

    public JcrCollectionHoldStringEntity toEntity(JcrCollectionHoldString jcrCollectionHoldString);

    public LocationEntity toEntity(Location location);

    public MeetingCanceledEntity toEntity(MeetingCanceled meetingCanceled);

    public MeetingEntity toEntity(Meeting meeting);

    public MeetingEEntity toEntity(MeetingE meetingE);

    public MilestoneEntity toEntity(Milestone milestone);

    public NoteEntity toEntity(Note note);

    public SentEmailEntity toEntity(SentEmail sentEmail);

    public TroopEntity toEntity(Troop troop);

    public YearPlanComponentEntity toEntity(YearPlanComponent yearPlanComponent);

    public YearPlanEntity toEntity(YearPlan yearPlan);
}