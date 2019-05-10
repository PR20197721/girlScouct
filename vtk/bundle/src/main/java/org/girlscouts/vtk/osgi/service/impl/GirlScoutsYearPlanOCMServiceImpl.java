package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.ocm.YearPlanNode;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.girlscouts.vtk.osgi.service.GirlScoutsYearPlanOCMService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(service = {GirlScoutsYearPlanOCMService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsYearPlanOCMServiceImpl")
public class GirlScoutsYearPlanOCMServiceImpl implements GirlScoutsYearPlanOCMService {


    private static Logger log = LoggerFactory.getLogger(GirlScoutsYearPlanOCMServiceImpl.class);

    @Reference
    private GirlScoutsOCMRepository girlScoutsOCMRepository;

    @Activate
    private void activate() {
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public YearPlan create(YearPlan object) {
        YearPlanNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.create(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public YearPlan update(YearPlan object) {
        YearPlanNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.update(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public YearPlan read(String path) {
        YearPlanNode node = (YearPlanNode)girlScoutsOCMRepository.read(path);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public boolean delete(YearPlan object) {
        return girlScoutsOCMRepository.delete(NodeToModelMapper.INSTANCE.toNode(object));
    }

    @Override
    public YearPlan findObject(String path, Map<String, String> params) {
        return NodeToModelMapper.INSTANCE.toModel(girlScoutsOCMRepository.findObject(path, params, YearPlanNode.class));
    }

    @Override
    public List<YearPlan> findObjects(String path, Map<String, String> params) {
        List<YearPlanNode> nodes = girlScoutsOCMRepository.findObjects(path, params, YearPlanNode.class);
        List<YearPlan> models = new ArrayList<>();
        nodes.forEach(yearPlanNode -> {models.add(NodeToModelMapper.INSTANCE.toModel(yearPlanNode));});
        return models;
    }

}
