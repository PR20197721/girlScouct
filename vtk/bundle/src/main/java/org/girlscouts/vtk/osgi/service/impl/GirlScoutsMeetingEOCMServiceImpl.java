package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.ocm.MeetingENode;
import org.girlscouts.vtk.osgi.service.GirlScoutsMeetingEOCMService;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(service = {GirlScoutsMeetingEOCMService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsMeetingEOCMServiceImpl")
public class GirlScoutsMeetingEOCMServiceImpl implements GirlScoutsMeetingEOCMService {
    private static Logger log = LoggerFactory.getLogger(GirlScoutsMeetingEOCMServiceImpl.class);
    @Reference
    private GirlScoutsOCMRepository girlScoutsOCMRepository;

    @Activate
    private void activate() {
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public MeetingE create(MeetingE object) {
        MeetingENode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.create(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public MeetingE update(MeetingE object) {
        MeetingENode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.update(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public MeetingE read(String path) {
        MeetingENode node = (MeetingENode) girlScoutsOCMRepository.read(path);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public boolean delete(MeetingE object) {
        return girlScoutsOCMRepository.delete(NodeToModelMapper.INSTANCE.toNode(object));
    }

    @Override
    public MeetingE findObject(String path, Map<String, String> params) {
        return NodeToModelMapper.INSTANCE.toModel(girlScoutsOCMRepository.findObject(path, params, MeetingENode.class));
    }

    @Override
    public List<MeetingE> findObjects(String path, Map<String, String> params) {
        List<MeetingENode> nodes = girlScoutsOCMRepository.findObjects(path, params, MeetingENode.class);
        List<MeetingE> models = new ArrayList<>();
        nodes.forEach(meetingENode -> {
            models.add(NodeToModelMapper.INSTANCE.toModel(meetingENode));
        });
        return models;
    }

}
