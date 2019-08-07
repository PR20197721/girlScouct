package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.ocm.TroopNode;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.girlscouts.vtk.osgi.service.GirlScoutsTroopOCMService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(service = {GirlScoutsTroopOCMService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsTroopOCMServiceImpl")
public class GirlScoutsTroopOCMServiceImpl implements GirlScoutsTroopOCMService {
    private static Logger log = LoggerFactory.getLogger(GirlScoutsTroopOCMServiceImpl.class);
    @Reference
    private GirlScoutsOCMRepository girlScoutsOCMRepository;

    @Activate
    private void activate() {
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public Troop create(Troop object) {
        TroopNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.create(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public Troop update(Troop object) {
        TroopNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.update(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public Troop read(String path) {
        TroopNode node = (TroopNode) girlScoutsOCMRepository.read(path);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public boolean delete(Troop object) {
        return girlScoutsOCMRepository.delete(NodeToModelMapper.INSTANCE.toNode(object));
    }

    @Override
    public Troop findObject(String path, Map<String, String> params) {
        return NodeToModelMapper.INSTANCE.toModel(girlScoutsOCMRepository.findObject(path, params, TroopNode.class));
    }

    @Override
    public List<Troop> findObjects(String path, Map<String, String> params) {
        List<TroopNode> nodes = girlScoutsOCMRepository.findObjects(path, params, TroopNode.class);
        List<Troop> models = new ArrayList<>();
        nodes.forEach(troopNode -> {
            models.add(NodeToModelMapper.INSTANCE.toModel(troopNode));
        });
        return models;
    }

}
