package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;
import org.girlscouts.vtk.models.JcrNode;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMRepository;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.query.QueryResult;
import java.util.List;
import java.util.Map;

@Component(service = {GirlScoutsOCMService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsOCMServiceImpl")
public class GirlScoutsOCMServiceImpl implements GirlScoutsOCMService {
    private static Logger log = LoggerFactory.getLogger(GirlScoutsOCMServiceImpl.class);

    @Reference
    private GirlScoutsOCMRepository girlScoutsOCMRepository;

    @Activate
    private void activate() {
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public <M extends org.girlscouts.vtk.models.JcrNode, N extends org.girlscouts.vtk.ocm.JcrNode> M create(M object) {
        //TODO need to check if path to object exists if not then create it
        /*if (!session.itemExists(troop.getPath() + "/lib/meetings/")) {
            ocm.insert(new JcrNode(troop.getPath() + "/lib"));
            ocm.insert(new JcrNode(troop.getPath() + "/lib/meetings"));
            ocm.save();
        }*/
        N node = (N) NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.create(node);
        return (M) NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public <M extends org.girlscouts.vtk.models.JcrNode, N extends org.girlscouts.vtk.ocm.JcrNode> M update(M object) {
        N node = (N) NodeToModelMapper.INSTANCE.toNode(object);
        node = girlScoutsOCMRepository.update(node);
        return (M) NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public <M extends org.girlscouts.vtk.models.JcrNode, N extends org.girlscouts.vtk.ocm.JcrNode> M read(String path) {
        N node = girlScoutsOCMRepository.read(path);
        return (M) NodeToModelMapper.INSTANCE.toModel(node);
    }

    @Override
    public <M extends org.girlscouts.vtk.models.JcrNode, N extends org.girlscouts.vtk.ocm.JcrNode> boolean delete(M object) {
        N node = (N) NodeToModelMapper.INSTANCE.toNode(object);
        return girlScoutsOCMRepository.delete(node);
    }

    @Override
    public <T extends JcrNode> T findObject(String path, Map<String, String> params, Class<T> clazz) {
        return null;
    }

    @Override
    public <T extends JcrNode> List<T> findObjects(String path, Map<String, String> params, Class<T> clazz) {
        return null;
    }

    @Override
    public QueryResult executeQuery(String query) {
        return null;
    }

    @Override
    public Node getNode(String path) {
        return null;
    }

}
