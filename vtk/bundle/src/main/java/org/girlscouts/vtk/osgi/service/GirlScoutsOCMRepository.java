package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.ocm.JcrNode;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public interface GirlScoutsOCMRepository {

    public <T extends JcrNode> T create(T object);
    public <T extends JcrNode> T update(T object);
    public Object read(String path);
    public <T extends JcrNode> boolean delete(T object);
    public <T extends JcrNode> T findObject(String path, Map<String, String> params, Class<T> clazz);
    public <T extends JcrNode> List<T> findObjects(String path, Map<String, String> params, Class<T> clazz);

}
