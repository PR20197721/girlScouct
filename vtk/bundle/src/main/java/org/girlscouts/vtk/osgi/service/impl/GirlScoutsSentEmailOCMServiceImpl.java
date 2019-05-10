package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;
import org.girlscouts.vtk.models.SentEmail;
import org.girlscouts.vtk.ocm.SentEmailNode;
import org.girlscouts.vtk.osgi.service.GirlScoutsSentEmailOCMService;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(service = {GirlScoutsSentEmailOCMService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsSentEmailOCMServiceImpl")
public class GirlScoutsSentEmailOCMServiceImpl implements GirlScoutsSentEmailOCMService {


    private static Logger log = LoggerFactory.getLogger(GirlScoutsSentEmailOCMServiceImpl.class);

    @Reference
    private GirlScoutsOCMRepository girlScoutsOCMRepository;

    @Activate
    private void activate() {
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public SentEmail create(SentEmail object) {
        SentEmailNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.create(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public SentEmail update(SentEmail object) {
        SentEmailNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.update(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public SentEmail read(String path) {
        SentEmailNode node = (SentEmailNode)girlScoutsOCMRepository.read(path);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public boolean delete(SentEmail object) {
        return girlScoutsOCMRepository.delete(NodeToModelMapper.INSTANCE.toNode(object));
    }

    @Override
    public SentEmail findObject(String path, Map<String, String> params) {
        return NodeToModelMapper.INSTANCE.toModel(girlScoutsOCMRepository.findObject(path, params, SentEmailNode.class));
    }

    @Override
    public List<SentEmail> findObjects(String path, Map<String, String> params) {
        List<SentEmailNode> nodes = girlScoutsOCMRepository.findObjects(path, params, SentEmailNode.class);
        List<SentEmail> models = new ArrayList<>();
        nodes.forEach(sentEmailNode -> {models.add(NodeToModelMapper.INSTANCE.toModel(sentEmailNode));});
        return models;
    }

}
