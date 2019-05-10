package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.ocm.AssetNode;
import org.girlscouts.vtk.osgi.service.GirlScoutsAssetOCMService;
import org.girlscouts.vtk.osgi.service.GirlScoutsAssetOCMService;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(service = {GirlScoutsAssetOCMService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsAssetOCMServiceImpl")
public class GirlScoutsAssetOCMServiceImpl implements GirlScoutsAssetOCMService {


    private static Logger log = LoggerFactory.getLogger(GirlScoutsAssetOCMServiceImpl.class);

    @Reference
    private GirlScoutsOCMRepository girlScoutsOCMRepository;

    @Activate
    private void activate() {
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public Asset create(Asset object) {
        AssetNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.create(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public Asset update(Asset object) {
        AssetNode node = NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.update(node);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public Asset read(String path) {
        AssetNode node = (AssetNode)girlScoutsOCMRepository.read(path);
        return NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public boolean delete(Asset object) {
        return girlScoutsOCMRepository.delete(NodeToModelMapper.INSTANCE.toNode(object));
    }

    @Override
    public Asset findObject(String path, Map<String, String> params) {
        return (Asset) girlScoutsOCMRepository.findObject(path, params, AssetNode.class).toModel();
    }

    @Override
    public List<Asset> findObjects(String path, Map<String, String> params) {
        List<AssetNode> nodes = girlScoutsOCMRepository.findObjects(path, params, AssetNode.class);
        List<Asset> models = new ArrayList<>();
        nodes.forEach(AssetNode -> {models.add((Asset)AssetNode.toModel());});
        return models;
    }

}
