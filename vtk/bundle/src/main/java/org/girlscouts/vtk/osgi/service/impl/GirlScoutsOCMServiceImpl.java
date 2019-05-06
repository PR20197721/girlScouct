package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.models.JcrNode;
import org.girlscouts.vtk.osgi.service.GirlScoutsOCMService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.query.QueryResult;
import java.util.List;
import java.util.Map;

@Component(service = {GirlScoutsOCMService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsOCMServiceImpl")
public class GirlScoutsOCMServiceImpl implements GirlScoutsOCMService {
    private static Logger log = LoggerFactory.getLogger(GirlScoutsOCMServiceImpl.class);

    @Activate
    private void activate() {
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public <T extends JcrNode> T create(T object) {
        //TODO need to check if path to object exists if not then create it
        /*if (!session.itemExists(troop.getPath() + "/lib/meetings/")) {
            ocm.insert(new JcrNode(troop.getPath() + "/lib"));
            ocm.insert(new JcrNode(troop.getPath() + "/lib/meetings"));
            ocm.save();
        }*/
        return read(object.getPath());
    }

    @Override
    public <T extends JcrNode> T update(T object) {
        return read(object.getPath());
    }

    @Override
    public <T extends JcrNode> T read(String path) {
        T object = null;
        return object;
    }

    @Override
    public <T extends JcrNode> boolean delete(T object) {
        boolean isRemoved = false;
        return isRemoved;
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

}
