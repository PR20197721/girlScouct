package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.ocm.MeetingNode;
import org.girlscouts.vtk.osgi.service.GirlScoutsMeetingOCMService;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(service = {GirlScoutsMeetingOCMService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsMeetingOCMServiceImpl")
public class GirlScoutsMeetingOCMServiceImpl implements GirlScoutsMeetingOCMService {
    private static Logger log = LoggerFactory.getLogger(GirlScoutsMeetingOCMServiceImpl.class);
    @Reference
    private GirlScoutsOCMRepository girlScoutsOCMRepository;

    @Activate
    private void activate() {
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public Meeting create(Meeting object) {
        MeetingNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.create(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public Meeting update(Meeting object) {
        MeetingNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.update(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public Meeting read(String path) {
        MeetingNode node = (MeetingNode) girlScoutsOCMRepository.read(path);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public boolean delete(Meeting object) {
        return girlScoutsOCMRepository.delete(NodeToModelMapper.INSTANCE.toNode(object));
    }

    @Override
    public Meeting findObject(String path, Map<String, String> params) {
        return NodeToModelMapper.INSTANCE.toModel(girlScoutsOCMRepository.findObject(path, params, MeetingNode.class));
    }

    @Override
    public List<Meeting> findObjects(String path, Map<String, String> params) {
        List<MeetingNode> nodes = girlScoutsOCMRepository.findObjects(path, params, MeetingNode.class);
        List<Meeting> models = new ArrayList<>();
        nodes.forEach(meetingNode -> {
            models.add(NodeToModelMapper.INSTANCE.toModel(meetingNode));
        });
        return models;
    }

    @Override
    public List<Meeting> findObjectsCustomQuery(String query) {
        List<MeetingNode> nodes = girlScoutsOCMRepository.findObjectsCustomQuery(query, MeetingNode.class);
        List<Meeting> models = new ArrayList<>();
        nodes.forEach(meetingNode -> {
            models.add(NodeToModelMapper.INSTANCE.toModel(meetingNode));
        });
        return models;
    }

}
