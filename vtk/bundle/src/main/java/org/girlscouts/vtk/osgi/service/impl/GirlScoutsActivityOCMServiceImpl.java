package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.ocm.ActivityNode;
import org.girlscouts.vtk.osgi.service.GirlScoutsActivityOCMService;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(service = {GirlScoutsActivityOCMService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsActivityOCMServiceImpl")
public class GirlScoutsActivityOCMServiceImpl implements GirlScoutsActivityOCMService {
    private static Logger log = LoggerFactory.getLogger(GirlScoutsActivityOCMServiceImpl.class);
    @Reference
    private GirlScoutsOCMRepository girlScoutsOCMRepository;

    @Activate
    private void activate() {
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public Activity create(Activity object) {
        ActivityNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.create(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public Activity update(Activity object) {
        ActivityNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.update(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public Activity read(String path) {
        ActivityNode node = (ActivityNode) girlScoutsOCMRepository.read(path);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public boolean delete(Activity object) {
        return girlScoutsOCMRepository.delete(NodeToModelMapper.INSTANCE.toNode(object));
    }

    @Override
    public Activity findObject(String path, Map<String, String> params) {
        return NodeToModelMapper.INSTANCE.toModel(girlScoutsOCMRepository.findObject(path, params, ActivityNode.class));
    }

    @Override
    public List<Activity> findObjects(String path, Map<String, String> params) {
        List<ActivityNode> nodes = girlScoutsOCMRepository.findObjects(path, params, ActivityNode.class);
        List<Activity> models = new ArrayList<>();
        nodes.forEach(activityNode -> {
            models.add(NodeToModelMapper.INSTANCE.toModel(activityNode));
        });
        return models;
    }

}
