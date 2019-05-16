package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;
import org.girlscouts.vtk.models.Finance;
import org.girlscouts.vtk.ocm.FinanceNode;
import org.girlscouts.vtk.osgi.service.GirlScoutsFinanceOCMService;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(service = {GirlScoutsFinanceOCMService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsFinanceOCMServiceImpl")
public class GirlScoutsFinanceOCMServiceImpl implements GirlScoutsFinanceOCMService {
    private static Logger log = LoggerFactory.getLogger(GirlScoutsFinanceOCMServiceImpl.class);
    @Reference
    private GirlScoutsOCMRepository girlScoutsOCMRepository;

    @Activate
    private void activate() {
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public Finance create(Finance object) {
        FinanceNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.create(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public Finance update(Finance object) {
        FinanceNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.update(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public Finance read(String path) {
        FinanceNode node = (FinanceNode) girlScoutsOCMRepository.read(path);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public boolean delete(Finance object) {
        return girlScoutsOCMRepository.delete(NodeToModelMapper.INSTANCE.toNode(object));
    }

    @Override
    public Finance findObject(String path, Map<String, String> params) {
        return NodeToModelMapper.INSTANCE.toModel(girlScoutsOCMRepository.findObject(path, params, FinanceNode.class));
    }

    @Override
    public List<Finance> findObjects(String path, Map<String, String> params) {
        List<FinanceNode> nodes = girlScoutsOCMRepository.findObjects(path, params, FinanceNode.class);
        List<Finance> models = new ArrayList<>();
        nodes.forEach(financeNode -> {
            models.add(NodeToModelMapper.INSTANCE.toModel(financeNode));
        });
        return models;
    }

}
