package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.ocm.MilestoneNode;
import org.girlscouts.vtk.osgi.service.GirlScoutsMilestoneOCMService;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(service = {GirlScoutsMilestoneOCMService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsMilestoneOCMServiceImpl")
public class GirlScoutsMilestoneOCMServiceImpl implements GirlScoutsMilestoneOCMService {


    private static Logger log = LoggerFactory.getLogger(GirlScoutsMilestoneOCMServiceImpl.class);

    @Reference
    private GirlScoutsOCMRepository girlScoutsOCMRepository;

    @Activate
    private void activate() {
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public Milestone create(Milestone object) {
        MilestoneNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.create(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public Milestone update(Milestone object) {
        MilestoneNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.update(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public Milestone read(String path) {
        MilestoneNode node = (MilestoneNode)girlScoutsOCMRepository.read(path);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public boolean delete(Milestone object) {
        return girlScoutsOCMRepository.delete(NodeToModelMapper.INSTANCE.toNode(object));
    }

    @Override
    public Milestone findObject(String path, Map<String, String> params) {
        return NodeToModelMapper.INSTANCE.toModel(girlScoutsOCMRepository.findObject(path, params, MilestoneNode.class));
    }

    @Override
    public List<Milestone> findObjects(String path, Map<String, String> params) {
        List<MilestoneNode> nodes = girlScoutsOCMRepository.findObjects(path, params, MilestoneNode.class);
        List<Milestone> models = new ArrayList<>();
        nodes.forEach(milestoneNode -> {models.add(NodeToModelMapper.INSTANCE.toModel(milestoneNode));});
        return models;
    }

}
