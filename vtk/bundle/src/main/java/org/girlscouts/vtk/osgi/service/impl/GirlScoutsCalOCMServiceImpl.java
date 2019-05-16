package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.ocm.CalNode;
import org.girlscouts.vtk.osgi.service.GirlScoutsCalOCMService;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(service = {GirlScoutsCalOCMService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsCalOCMServiceImpl")
public class GirlScoutsCalOCMServiceImpl implements GirlScoutsCalOCMService {
    private static Logger log = LoggerFactory.getLogger(GirlScoutsCalOCMServiceImpl.class);
    @Reference
    private GirlScoutsOCMRepository girlScoutsOCMRepository;

    @Activate
    private void activate() {
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public Cal create(Cal object) {
        CalNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.create(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public Cal update(Cal object) {
        CalNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.update(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public Cal read(String path) {
        CalNode node = (CalNode) girlScoutsOCMRepository.read(path);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public boolean delete(Cal object) {
        return girlScoutsOCMRepository.delete(NodeToModelMapper.INSTANCE.toNode(object));
    }

    @Override
    public Cal findObject(String path, Map<String, String> params) {
        return NodeToModelMapper.INSTANCE.toModel(girlScoutsOCMRepository.findObject(path, params, CalNode.class));
    }

    @Override
    public List<Cal> findObjects(String path, Map<String, String> params) {
        List<CalNode> nodes = girlScoutsOCMRepository.findObjects(path, params, CalNode.class);
        List<Cal> models = new ArrayList<>();
        nodes.forEach(calNode -> {
            models.add(NodeToModelMapper.INSTANCE.toModel(calNode));
        });
        return models;
    }

}
