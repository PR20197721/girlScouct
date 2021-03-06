package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.ocm.MeetingENode;
import org.girlscouts.vtk.osgi.service.GirlScoutsMeetingEOCMService;
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

@Component(service = {GirlScoutsMeetingEOCMService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsMeetingEOCMServiceImpl")
public class GirlScoutsMeetingEOCMServiceImpl implements GirlScoutsMeetingEOCMService {
    private static Logger log = LoggerFactory.getLogger(GirlScoutsMeetingEOCMServiceImpl.class);
    @Reference
    private GirlScoutsOCMRepository girlScoutsOCMRepository;
    @Reference
    private GirlScoutsMeetingOCMService girlScoutsMeetingOCMService;

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
        MeetingE meetingE = NodeToModelMapper.INSTANCE.toModel(node);
        if(meetingE != null && meetingE.getMeetingInfo() == null){
            String refId = meetingE.getRefId();
            if(refId != null) {
                meetingE.setMeetingInfo(girlScoutsMeetingOCMService.read(refId));
            }
        }
        return meetingE;
    }

    @Override
    public boolean delete(MeetingE object) {
        return girlScoutsOCMRepository.delete(NodeToModelMapper.INSTANCE.toNode(object));
    }

    @Override
    public MeetingE findObject(String path, Map<String, String> params) {
        MeetingE meetingE = NodeToModelMapper.INSTANCE.toModel(girlScoutsOCMRepository.findObject(path, params, MeetingENode.class));
        if(meetingE.getMeetingInfo() == null){
            meetingE.setMeetingInfo(girlScoutsMeetingOCMService.read(meetingE.getRefId()));
        }
        return meetingE;
    }

    @Override
    public List<MeetingE> findObjects(String path, Map<String, String> params) {
        List<MeetingENode> nodes = girlScoutsOCMRepository.findObjects(path, params, MeetingENode.class);
        List<MeetingE> models = new ArrayList<>();
        nodes.forEach(meetingENode -> {
            MeetingE meetingE = NodeToModelMapper.INSTANCE.toModel(meetingENode);
            if(meetingE.getMeetingInfo() == null){
                meetingE.setMeetingInfo(girlScoutsMeetingOCMService.read(meetingE.getRefId()));
            }
            models.add(meetingE);
        });
        return models;
    }

}
