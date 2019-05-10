package org.girlscouts.vtk.mapper.ocm;

import org.girlscouts.vtk.models.*;
import org.girlscouts.vtk.ocm.*;
import org.girlscouts.vtk.ocm.JcrNode;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NodeToModelMapper {

    NodeToModelMapper INSTANCE = Mappers.getMapper( NodeToModelMapper.class );

    public AchievementNode toNode(Achievement achievement);
    public Achievement toModel(AchievementNode achievementNode);

    public ActivityNode toNode(Activity activity);
    public Activity toModel(ActivityNode achievementNode);

    public AssetNode toNode(Asset asset);
    public Asset toModel(AssetNode assetNode);

    public AttendanceNode toNode(Attendance attendance);
    public Attendance toModel(AttendanceNode assetNode);

    public CalNode toNode(Cal cal);
    public Cal toModel(CalNode calNode);

    public CouncilNode toNode(Council cal);
    public Council toModel(CouncilNode calNode);

    public CouncilInfoNode toNode(CouncilInfo councilInfo);
    public CouncilInfo toModel(CouncilInfoNode CouncilInfoNode);

    public FinanceNode toNode(Finance finance);
    public Finance toModel(FinanceNode financeNode);

    public JcrCollectionHoldStringNode toNode(JcrCollectionHoldString jcrCollectionHoldString);
    public JcrCollectionHoldString toModel(JcrCollectionHoldStringNode jcrCollectionHoldStringNode);

    public JcrNode toNode(org.girlscouts.vtk.models.JcrNode jcrNode);
    public org.girlscouts.vtk.models.JcrNode toModel(JcrNode jcrNode);

    public LocationNode toNode(Location location);
    public Location toModel(LocationNode locationNode);

    public MeetingCanceledNode toNode(MeetingCanceled meetingCanceled);
    public MeetingCanceled toModel(MeetingCanceledNode meetingCanceledNode);

    public MeetingNode toNode(Meeting meeting);
    public Meeting toModel(MeetingNode meetingNode);

    public MeetingENode toNode(MeetingE meetingE);
    public MeetingE toModel(MeetingENode meetingENode);

    public MilestoneNode toNode(Milestone milestone);
    public Milestone toModel(MilestoneNode milestoneNode);

    public NoteNode toNode(Note note);
    public Note toModel(NoteNode noteNode);

    public SentEmailNode toNode(SentEmail sentEmail);
    public SentEmail toModel(SentEmailNode sentEmailNode);

    public TroopNode toNode(Troop troop);
    public Troop toModel(TroopNode troopNode);

    public YearPlanComponentNode toNode(YearPlanComponent yearPlanComponent);
    public YearPlanComponent toModel(YearPlanComponentNode yearPlanComponentNode);

    public YearPlanNode toNode(YearPlan yearPlan);
    public YearPlan toModel(YearPlanNode yearPlanNode);
}
