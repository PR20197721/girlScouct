package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.*;
import org.girlscouts.vtk.ocm.MappableToModel;

import javax.jcr.Node;
import javax.jcr.query.QueryResult;
import java.util.List;
import java.util.Map;

public interface GirlScoutsOCMService {

    public Achievement create(Achievement object);
    public Achievement update(Achievement object);

    public Activity create(Activity object);
    public Activity update(Activity object);

    public Asset create(Asset object);
    public Asset update(Asset object);

    public Attendance create(Attendance object);
    public Attendance update(Attendance object);

    public Cal create(Cal object);
    public Cal update(Cal object);

    public Finance create(Finance object);
    public Finance update(Finance object);

    public JcrCollectionHoldString create(JcrCollectionHoldString object);
    public JcrCollectionHoldString update(JcrCollectionHoldString object);

    public JcrNode create(JcrNode object);
    public JcrNode update(JcrNode object);

    public Location create(Location object);
    public Location update(Location object);

    public MeetingCanceled create(MeetingCanceled object);
    public MeetingCanceled update(MeetingCanceled object);

    public MeetingE create(MeetingE object);
    public MeetingE update(MeetingE object);

    public Meeting create(Meeting object);
    public Meeting update(Meeting object);

    public Milestone create(Milestone object);
    public Milestone update(Milestone object);

    public Note create(Note object);
    public Note update(Note object);

    public SentEmail create(SentEmail object);
    public SentEmail update(SentEmail object);

    public Troop create(Troop object);
    public Troop update(Troop object);

    public YearPlanComponent create(YearPlanComponent object);
    public YearPlanComponent update(YearPlanComponent object);

    public YearPlan create(YearPlan object);
    public YearPlan update(YearPlan object);

    public  <M extends JcrNode> M read(String path, Class<M> returnType);
    public <M extends JcrNode> boolean delete(M object);

    public <T extends JcrNode> T findObject(String path, Map<String, String> params, Class<T> clazz);
    public <T extends JcrNode> List<T> findObjects(String path, Map<String, String> params, Class<T> clazz);
}
