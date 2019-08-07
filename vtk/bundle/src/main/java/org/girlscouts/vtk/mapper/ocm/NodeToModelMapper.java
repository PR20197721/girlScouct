package org.girlscouts.vtk.mapper.ocm;

import org.girlscouts.vtk.models.*;
import org.girlscouts.vtk.ocm.JcrNode;
import org.girlscouts.vtk.ocm.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NodeToModelMapper {
    NodeToModelMapper INSTANCE = Mappers.getMapper(NodeToModelMapper.class);

    AchievementNode toNode(Achievement achievement);

    Achievement toModel(AchievementNode achievementNode);

    ActivityNode toNode(Activity activity);

    @Mapping(source = "type", target = "type", ignore = true)
    Activity toModel(ActivityNode achievementNode);

    AssetNode toNode(Asset asset);

    Asset toModel(AssetNode assetNode);

    AttendanceNode toNode(Attendance attendance);

    Attendance toModel(AttendanceNode assetNode);

    CalNode toNode(Cal cal);

    Cal toModel(CalNode calNode);

    CouncilNode toNode(Council cal);

    Council toModel(CouncilNode calNode);

    CouncilInfoNode toNode(CouncilInfo councilInfo);

    CouncilInfo toModel(CouncilInfoNode CouncilInfoNode);

    FinanceNode toNode(Finance finance);

    Finance toModel(FinanceNode financeNode);

    JcrCollectionHoldStringNode toNode(JcrCollectionHoldString jcrCollectionHoldString);

    JcrCollectionHoldString toModel(JcrCollectionHoldStringNode jcrCollectionHoldStringNode);

    JcrNode toNode(org.girlscouts.vtk.models.JcrNode jcrNode);

    org.girlscouts.vtk.models.JcrNode toModel(JcrNode jcrNode);

    LocationNode toNode(Location location);

    Location toModel(LocationNode locationNode);

    MeetingCanceledNode toNode(MeetingCanceled meetingCanceled);

    @Mapping(source = "type", target = "type", ignore = true)
    MeetingCanceled toModel(MeetingCanceledNode meetingCanceledNode);

    MeetingNode toNode(Meeting meeting);

    @Mapping(source = "type", target = "type", ignore = true)
    Meeting toModel(MeetingNode meetingNode);

    MeetingENode toNode(MeetingE meetingE);

    @Mapping(source = "type", target = "type", ignore = true)
    MeetingE toModel(MeetingENode meetingENode);

    MilestoneNode toNode(Milestone milestone);

    @Mapping(source = "type", target = "type", ignore = true)
    Milestone toModel(MilestoneNode milestoneNode);

    NoteNode toNode(Note note);

    Note toModel(NoteNode noteNode);

    SentEmailNode toNode(SentEmail sentEmail);

    SentEmail toModel(SentEmailNode sentEmailNode);

    TroopNode toNode(Troop troop);

    Troop toModel(TroopNode troopNode);

    YearPlanComponentNode toNode(YearPlanComponent yearPlanComponent);

    YearPlanComponent toModel(YearPlanComponentNode yearPlanComponentNode);

    YearPlanNode toNode(YearPlan yearPlan);

    YearPlan toModel(YearPlanNode yearPlanNode);
}
