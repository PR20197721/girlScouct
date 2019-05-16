package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;
import org.girlscouts.vtk.models.Achievement;
import org.girlscouts.vtk.ocm.AchievementNode;
import org.girlscouts.vtk.osgi.service.GirlScoutsAchievementOCMService;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(service = {GirlScoutsAchievementOCMService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsAchievementOCMServiceImpl")
public class GirlScoutsAchievementOCMServiceImpl implements GirlScoutsAchievementOCMService {
    private static Logger log = LoggerFactory.getLogger(GirlScoutsAchievementOCMServiceImpl.class);
    @Reference
    private GirlScoutsOCMRepository girlScoutsOCMRepository;

    @Activate
    private void activate() {
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public Achievement create(Achievement object) {
        AchievementNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.create(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public Achievement update(Achievement object) {
        AchievementNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.update(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public Achievement read(String path) {
        AchievementNode node = (AchievementNode) girlScoutsOCMRepository.read(path);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public boolean delete(Achievement object) {
        return girlScoutsOCMRepository.delete(NodeToModelMapper.INSTANCE.toNode(object));
    }

    @Override
    public Achievement findObject(String path, Map<String, String> params) {
        return NodeToModelMapper.INSTANCE.toModel(girlScoutsOCMRepository.findObject(path, params, AchievementNode.class));
    }

    @Override
    public List<Achievement> findObjects(String path, Map<String, String> params) {
        List<AchievementNode> nodes = girlScoutsOCMRepository.findObjects(path, params, AchievementNode.class);
        List<Achievement> models = new ArrayList<>();
        nodes.forEach(achievementNode -> {
            models.add(NodeToModelMapper.INSTANCE.toModel(achievementNode));
        });
        return models;
    }

}
