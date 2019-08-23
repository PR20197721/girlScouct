package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;
import org.girlscouts.vtk.models.JcrCollectionHoldString;
import org.girlscouts.vtk.ocm.JcrCollectionHoldStringNode;
import org.girlscouts.vtk.osgi.service.GirlScoutsJcrCollectionHoldStringOCMService;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(service = {GirlScoutsJcrCollectionHoldStringOCMService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsJcrCollectionHoldStringOCMServiceImpl")
public class GirlScoutsJcrCollectionHoldStringOCMServiceImpl implements GirlScoutsJcrCollectionHoldStringOCMService {
    private static Logger log = LoggerFactory.getLogger(GirlScoutsJcrCollectionHoldStringOCMServiceImpl.class);
    @Reference
    private GirlScoutsOCMRepository girlScoutsOCMRepository;

    @Activate
    private void activate() {
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public JcrCollectionHoldString create(JcrCollectionHoldString object) {
        JcrCollectionHoldStringNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.create(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public JcrCollectionHoldString update(JcrCollectionHoldString object) {
        JcrCollectionHoldStringNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.update(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public JcrCollectionHoldString read(String path) {
        JcrCollectionHoldStringNode node = (JcrCollectionHoldStringNode) girlScoutsOCMRepository.read(path);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public boolean delete(JcrCollectionHoldString object) {
        return girlScoutsOCMRepository.delete(NodeToModelMapper.INSTANCE.toNode(object));
    }

    @Override
    public JcrCollectionHoldString findObject(String path, Map<String, String> params) {
        return NodeToModelMapper.INSTANCE.toModel(girlScoutsOCMRepository.findObject(path, params, JcrCollectionHoldStringNode.class));
    }

    @Override
    public List<JcrCollectionHoldString> findObjects(String path, Map<String, String> params) {
        List<JcrCollectionHoldStringNode> nodes = girlScoutsOCMRepository.findObjects(path, params, JcrCollectionHoldStringNode.class);
        List<JcrCollectionHoldString> models = new ArrayList<>();
        nodes.forEach(jcrCollectionHoldStringNode -> {
            models.add(NodeToModelMapper.INSTANCE.toModel(jcrCollectionHoldStringNode));
        });
        return models;
    }

}
