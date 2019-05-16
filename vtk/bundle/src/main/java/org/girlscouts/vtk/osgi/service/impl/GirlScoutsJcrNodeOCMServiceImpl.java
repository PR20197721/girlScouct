package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;
import org.girlscouts.vtk.models.JcrNode;
import org.girlscouts.vtk.osgi.service.GirlScoutsJcrNodeOCMService;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(service = {GirlScoutsJcrNodeOCMService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsJcrNodeOCMServiceImpl")
public class GirlScoutsJcrNodeOCMServiceImpl implements GirlScoutsJcrNodeOCMService {
    private static Logger log = LoggerFactory.getLogger(GirlScoutsJcrNodeOCMServiceImpl.class);
    @Reference
    private GirlScoutsOCMRepository girlScoutsOCMRepository;

    @Activate
    private void activate() {
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public JcrNode create(JcrNode object) {
        org.girlscouts.vtk.ocm.JcrNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.create(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public JcrNode update(JcrNode object) {
        org.girlscouts.vtk.ocm.JcrNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.update(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public JcrNode read(String path) {
        org.girlscouts.vtk.ocm.JcrNode node = (org.girlscouts.vtk.ocm.JcrNode) girlScoutsOCMRepository.read(path);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public boolean delete(JcrNode object) {
        return girlScoutsOCMRepository.delete(NodeToModelMapper.INSTANCE.toNode(object));
    }

    @Override
    public JcrNode findObject(String path, Map<String, String> params) {
        return NodeToModelMapper.INSTANCE.toModel(girlScoutsOCMRepository.findObject(path, params, org.girlscouts.vtk.ocm.JcrNode.class));
    }

    @Override
    public List<JcrNode> findObjects(String path, Map<String, String> params) {
        List<org.girlscouts.vtk.ocm.JcrNode> nodes = girlScoutsOCMRepository.findObjects(path, params, org.girlscouts.vtk.ocm.JcrNode.class);
        List<JcrNode> models = new ArrayList<>();
        nodes.forEach(jcrNodeNode -> {
            models.add(NodeToModelMapper.INSTANCE.toModel(jcrNodeNode));
        });
        return models;
    }

}
