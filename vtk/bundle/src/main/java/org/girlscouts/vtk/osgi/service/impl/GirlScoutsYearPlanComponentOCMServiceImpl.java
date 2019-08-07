package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;
import org.girlscouts.vtk.models.YearPlanComponent;
import org.girlscouts.vtk.ocm.YearPlanComponentNode;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.girlscouts.vtk.osgi.service.GirlScoutsYearPlanComponentOCMService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(service = {GirlScoutsYearPlanComponentOCMService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsYearPlanComponentOCMServiceImpl")
public class GirlScoutsYearPlanComponentOCMServiceImpl implements GirlScoutsYearPlanComponentOCMService {
    private static Logger log = LoggerFactory.getLogger(GirlScoutsYearPlanComponentOCMServiceImpl.class);
    @Reference
    private GirlScoutsOCMRepository girlScoutsOCMRepository;

    @Activate
    private void activate() {
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public YearPlanComponent create(YearPlanComponent object) {
        YearPlanComponentNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.create(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public YearPlanComponent update(YearPlanComponent object) {
        YearPlanComponentNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.update(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public YearPlanComponent read(String path) {
        YearPlanComponentNode node = (YearPlanComponentNode) girlScoutsOCMRepository.read(path);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public boolean delete(YearPlanComponent object) {
        return girlScoutsOCMRepository.delete(NodeToModelMapper.INSTANCE.toNode(object));
    }

    @Override
    public YearPlanComponent findObject(String path, Map<String, String> params) {
        return NodeToModelMapper.INSTANCE.toModel(girlScoutsOCMRepository.findObject(path, params, YearPlanComponentNode.class));
    }

    @Override
    public List<YearPlanComponent> findObjects(String path, Map<String, String> params) {
        List<YearPlanComponentNode> nodes = girlScoutsOCMRepository.findObjects(path, params, YearPlanComponentNode.class);
        List<YearPlanComponent> models = new ArrayList<>();
        nodes.forEach(yearPlanComponentNode -> {
            models.add(NodeToModelMapper.INSTANCE.toModel(yearPlanComponentNode));
        });
        return models;
    }

}
