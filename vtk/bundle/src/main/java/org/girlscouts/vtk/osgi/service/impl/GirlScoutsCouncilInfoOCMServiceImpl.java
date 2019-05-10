package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;
import org.girlscouts.vtk.models.CouncilInfo;
import org.girlscouts.vtk.ocm.CouncilInfoNode;
import org.girlscouts.vtk.osgi.service.GirlScoutsCouncilInfoOCMService;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(service = {GirlScoutsCouncilInfoOCMService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsCouncilInfoOCMServiceImpl")
public class GirlScoutsCouncilInfoOCMServiceImpl implements GirlScoutsCouncilInfoOCMService {


    private static Logger log = LoggerFactory.getLogger(GirlScoutsCouncilInfoOCMServiceImpl.class);

    @Reference
    private GirlScoutsOCMRepository girlScoutsOCMRepository;

    @Activate
    private void activate() {
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public CouncilInfo create(CouncilInfo object) {
        CouncilInfoNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.create(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public CouncilInfo update(CouncilInfo object) {
        CouncilInfoNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.update(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public CouncilInfo read(String path) {
        CouncilInfoNode node = (CouncilInfoNode)girlScoutsOCMRepository.read(path);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public boolean delete(CouncilInfo object) {
        return girlScoutsOCMRepository.delete(NodeToModelMapper.INSTANCE.toNode(object));
    }

    @Override
    public CouncilInfo findObject(String path, Map<String, String> params) {
        return NodeToModelMapper.INSTANCE.toModel(girlScoutsOCMRepository.findObject(path, params, CouncilInfoNode.class));
    }

    @Override
    public List<CouncilInfo> findObjects(String path, Map<String, String> params) {
        List<CouncilInfoNode> nodes = girlScoutsOCMRepository.findObjects(path, params, CouncilInfoNode.class);
        List<CouncilInfo> models = new ArrayList<>();
        nodes.forEach(CouncilInfoNode -> {models.add(NodeToModelMapper.INSTANCE.toModel(CouncilInfoNode));});
        return models;
    }

}
