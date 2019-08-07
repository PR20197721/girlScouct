package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;
import org.girlscouts.vtk.models.Location;
import org.girlscouts.vtk.ocm.LocationNode;
import org.girlscouts.vtk.osgi.service.GirlScoutsLocationOCMService;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(service = {GirlScoutsLocationOCMService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsLocationOCMServiceImpl")
public class GirlScoutsLocationOCMServiceImpl implements GirlScoutsLocationOCMService {
    private static Logger log = LoggerFactory.getLogger(GirlScoutsLocationOCMServiceImpl.class);
    @Reference
    private GirlScoutsOCMRepository girlScoutsOCMRepository;

    @Activate
    private void activate() {
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public Location create(Location object) {
        LocationNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.create(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public Location update(Location object) {
        LocationNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.update(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public Location read(String path) {
        LocationNode node = (LocationNode) girlScoutsOCMRepository.read(path);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public boolean delete(Location object) {
        return girlScoutsOCMRepository.delete(NodeToModelMapper.INSTANCE.toNode(object));
    }

    @Override
    public Location findObject(String path, Map<String, String> params) {
        return NodeToModelMapper.INSTANCE.toModel(girlScoutsOCMRepository.findObject(path, params, LocationNode.class));
    }

    @Override
    public List<Location> findObjects(String path, Map<String, String> params) {
        List<LocationNode> nodes = girlScoutsOCMRepository.findObjects(path, params, LocationNode.class);
        List<Location> models = new ArrayList<>();
        nodes.forEach(locationNode -> {
            models.add(NodeToModelMapper.INSTANCE.toModel(locationNode));
        });
        return models;
    }

}
