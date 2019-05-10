package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;
import org.girlscouts.vtk.models.MeetingCanceled;
import org.girlscouts.vtk.ocm.MeetingCanceledNode;
import org.girlscouts.vtk.osgi.service.GirlScoutsMeetingCancelledOCMService;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(service = {GirlScoutsMeetingCancelledOCMService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsMeetingCancelledOCMServiceImpl")
public class GirlScoutsMeetingCancelledOCMServiceImpl implements GirlScoutsMeetingCancelledOCMService {


    private static Logger log = LoggerFactory.getLogger(GirlScoutsMeetingCancelledOCMServiceImpl.class);

    @Reference
    private GirlScoutsOCMRepository girlScoutsOCMRepository;

    @Activate
    private void activate() {
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public MeetingCanceled create(MeetingCanceled object) {
        MeetingCanceledNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.create(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public MeetingCanceled update(MeetingCanceled object) {
        MeetingCanceledNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.update(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public MeetingCanceled read(String path) {
        MeetingCanceledNode node = (MeetingCanceledNode)girlScoutsOCMRepository.read(path);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public boolean delete(MeetingCanceled object) {
        return girlScoutsOCMRepository.delete(NodeToModelMapper.INSTANCE.toNode(object));
    }

    @Override
    public MeetingCanceled findObject(String path, Map<String, String> params) {
        return NodeToModelMapper.INSTANCE.toModel(girlScoutsOCMRepository.findObject(path, params, MeetingCanceledNode.class));
    }

    @Override
    public List<MeetingCanceled> findObjects(String path, Map<String, String> params) {
        List<MeetingCanceledNode> nodes = girlScoutsOCMRepository.findObjects(path, params, MeetingCanceledNode.class);
        List<MeetingCanceled> models = new ArrayList<>();
        nodes.forEach(meetingCanceledNode -> {models.add(NodeToModelMapper.INSTANCE.toModel(meetingCanceledNode));});
        return models;
    }

}
