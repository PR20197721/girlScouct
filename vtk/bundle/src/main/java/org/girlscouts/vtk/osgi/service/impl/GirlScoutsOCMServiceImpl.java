package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;
import org.girlscouts.vtk.models.*;
import org.girlscouts.vtk.models.JcrNode;
import org.girlscouts.vtk.ocm.*;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.query.QueryResult;
import java.util.List;
import java.util.Map;

@Component(service = {GirlScoutsOCMService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsOCMServiceImpl")
public class GirlScoutsOCMServiceImpl implements GirlScoutsOCMService {


    private static Logger log = LoggerFactory.getLogger(GirlScoutsOCMServiceImpl.class);

    @Reference
    private GirlScoutsOCMRepository girlScoutsOCMRepository;

    @Activate
    private void activate() {
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public Achievement create(Achievement object) {
        return (Achievement) girlScoutsOCMRepository.create((AchievementNode)object.toNode()).toModel();
    }

    @Override
    public Achievement update(Achievement object) {
        return (Achievement) girlScoutsOCMRepository.update((AchievementNode)object.toNode()).toModel();
    }

    @Override
    public Activity create(Activity object) {
        return (Activity) girlScoutsOCMRepository.create((ActivityNode)object.toNode()).toModel();
    }

    @Override
    public Activity update(Activity object) {
        return (Activity) girlScoutsOCMRepository.update((ActivityNode)object.toNode()).toModel();
    }

    @Override
    public Asset create(Asset object) {
        return (Asset) girlScoutsOCMRepository.create((AssetNode)object.toNode()).toModel();
    }

    @Override
    public Asset update(Asset object) {
        return (Asset) girlScoutsOCMRepository.update((AssetNode)object.toNode()).toModel();
    }

    @Override
    public Attendance create(Attendance object) {
        return (Attendance) girlScoutsOCMRepository.create((AttendanceNode)object.toNode()).toModel();
    }

    @Override
    public Attendance update(Attendance object) {
        return (Attendance) girlScoutsOCMRepository.update((AttendanceNode)object.toNode()).toModel();
    }

    @Override
    public Cal create(Cal object) {
        return (Cal) girlScoutsOCMRepository.create((CalNode)object.toNode()).toModel();
    }

    @Override
    public Cal update(Cal object) {
        return (Cal) girlScoutsOCMRepository.update((CalNode)object.toNode()).toModel();
    }

    @Override
    public Finance create(Finance object) {
        return (Finance) girlScoutsOCMRepository.create((FinanceNode)object.toNode()).toModel();
    }

    @Override
    public Finance update(Finance object) {
        return (Finance) girlScoutsOCMRepository.update((FinanceNode)object.toNode()).toModel();
    }

    @Override
    public JcrCollectionHoldString create(JcrCollectionHoldString object) {
        return (JcrCollectionHoldString) girlScoutsOCMRepository.create((JcrCollectionHoldStringNode)object.toNode()).toModel();
    }

    @Override
    public JcrCollectionHoldString update(JcrCollectionHoldString object) {
        return (JcrCollectionHoldString) girlScoutsOCMRepository.update((JcrCollectionHoldStringNode)object.toNode()).toModel();
    }

    @Override
    public JcrNode create(JcrNode object) {
        return (JcrNode) girlScoutsOCMRepository.create((org.girlscouts.vtk.ocm.JcrNode)object.toNode()).toModel();
    }

    @Override
    public JcrNode update(JcrNode object) {
        return (JcrNode) girlScoutsOCMRepository.update((org.girlscouts.vtk.ocm.JcrNode)object.toNode()).toModel();
    }

    @Override
    public Location create(Location object) {
        return (Location) girlScoutsOCMRepository.create((LocationNode)object.toNode()).toModel();
    }

    @Override
    public Location update(Location object) {
        return (Location) girlScoutsOCMRepository.update((LocationNode)object.toNode()).toModel();
    }

    @Override
    public MeetingCanceled create(MeetingCanceled object) {
        return (MeetingCanceled) girlScoutsOCMRepository.create((MeetingCanceledNode)object.toNode()).toModel();
    }

    @Override
    public MeetingCanceled update(MeetingCanceled object) {
        return (MeetingCanceled) girlScoutsOCMRepository.update((MeetingCanceledNode)object.toNode()).toModel();
    }

    @Override
    public MeetingE create(MeetingE object) {
        return (MeetingE) girlScoutsOCMRepository.create((MeetingENode)object.toNode()).toModel();
    }

    @Override
    public MeetingE update(MeetingE object) {
        return (MeetingE) girlScoutsOCMRepository.update((MeetingENode)object.toNode()).toModel();
    }

    @Override
    public Meeting create(Meeting object) {
        return (Meeting) girlScoutsOCMRepository.create((MeetingNode)object.toNode()).toModel();
    }

    @Override
    public Meeting update(Meeting object) {
        return (Meeting) girlScoutsOCMRepository.update((MeetingNode)object.toNode()).toModel();
    }

    @Override
    public Milestone create(Milestone object) {
        return (Milestone) girlScoutsOCMRepository.create((MilestoneNode)object.toNode()).toModel();
    }

    @Override
    public Milestone update(Milestone object) {
        return (Milestone) girlScoutsOCMRepository.update((MilestoneNode)object.toNode()).toModel();
    }

    @Override
    public Note create(Note object) {
        return (Note) girlScoutsOCMRepository.create((NoteNode)object.toNode()).toModel();
    }

    @Override
    public Note update(Note object) {
        return (Note) girlScoutsOCMRepository.update((NoteNode)object.toNode()).toModel();
    }

    @Override
    public SentEmail create(SentEmail object) {
        return (SentEmail) girlScoutsOCMRepository.create((SentEmailNode)object.toNode()).toModel();
    }

    @Override
    public SentEmail update(SentEmail object) {
        return (SentEmail) girlScoutsOCMRepository.update((SentEmailNode)object.toNode()).toModel();
    }

    @Override
    public Troop create(Troop object) {
        return (Troop) girlScoutsOCMRepository.create((TroopNode)object.toNode()).toModel();
    }

    @Override
    public Troop update(Troop object) {
        return (Troop) girlScoutsOCMRepository.update((TroopNode)object.toNode()).toModel();
    }

    @Override
    public YearPlanComponent create(YearPlanComponent object) {
        return (YearPlanComponent) girlScoutsOCMRepository.create((YearPlanComponentNode)object.toNode()).toModel();
    }

    @Override
    public YearPlanComponent update(YearPlanComponent object) {
        return (YearPlanComponent) girlScoutsOCMRepository.update((YearPlanComponentNode)object.toNode()).toModel();
    }

    @Override
    public YearPlan create(YearPlan object) {
        return (YearPlan) girlScoutsOCMRepository.create((YearPlanNode)object.toNode()).toModel();
    }

    @Override
    public YearPlan update(YearPlan object) {
        return (YearPlan) girlScoutsOCMRepository.update((YearPlanNode)object.toNode()).toModel();
    }

    @Override
    public <M extends JcrNode> M read(String path, Class<M> returnType) {
        return returnType.cast(girlScoutsOCMRepository.read(path).toModel());
    }

    @Override
    public <M extends JcrNode> boolean delete(M object) {
        return girlScoutsOCMRepository.delete(NodeToModelMapper.INSTANCE.toNode(object));
    }

    @Override
    public <T extends MappableToNode> T findObject(String path, Map<String, String> params, Class<T> clazz) {
        try {
            final T t = clazz.newInstance();
            t.toNode().getClass();
            return girlScoutsOCMRepository.findObject(path, params, t.toNode().getClass()).toModel();
        } catch (InstantiationException e) {
            log.error("Error Occurred: ", e);
        } catch (IllegalAccessException e) {
            log.error("Error Occurred: ", e);
        }

    }

    @Override
    public <T extends JcrNode> List<T> findObjects(String path, Map<String, String> params, Class<T> clazz) {
        return null;
    }

}
