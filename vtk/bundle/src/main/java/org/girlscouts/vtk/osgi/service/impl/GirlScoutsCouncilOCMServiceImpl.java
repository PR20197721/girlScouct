package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;
import org.girlscouts.vtk.models.Council;
import org.girlscouts.vtk.ocm.CouncilNode;
import org.girlscouts.vtk.osgi.service.GirlScoutsCouncilOCMService;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(service = {GirlScoutsCouncilOCMService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsCouncilOCMServiceImpl")
public class GirlScoutsCouncilOCMServiceImpl implements GirlScoutsCouncilOCMService {
    private static Logger log = LoggerFactory.getLogger(GirlScoutsCouncilOCMServiceImpl.class);
    @Reference
    private GirlScoutsOCMRepository girlScoutsOCMRepository;

    @Activate
    private void activate() {
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public Council create(Council object) {
        CouncilNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.create(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public Council update(Council object) {
        org.girlscouts.vtk.ocm.CouncilNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.update(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public Council read(String path) {
        org.girlscouts.vtk.ocm.CouncilNode node = (org.girlscouts.vtk.ocm.CouncilNode) girlScoutsOCMRepository.read(path);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public boolean delete(Council object) {
        return girlScoutsOCMRepository.delete(NodeToModelMapper.INSTANCE.toNode(object));
    }

    @Override
    public Council findObject(String path, Map<String, String> params) {
        return NodeToModelMapper.INSTANCE.toModel(girlScoutsOCMRepository.findObject(path, params, CouncilNode.class));
    }

    @Override
    public List<Council> findObjects(String path, Map<String, String> params) {
        List<CouncilNode> nodes = girlScoutsOCMRepository.findObjects(path, params, CouncilNode.class);
        List<Council> models = new ArrayList<>();
        nodes.forEach(councilNode -> {
            models.add(NodeToModelMapper.INSTANCE.toModel(councilNode));
        });
        return models;
    }

}
