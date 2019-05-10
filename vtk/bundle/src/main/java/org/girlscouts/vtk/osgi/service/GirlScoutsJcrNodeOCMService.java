package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.JcrNode;

import java.util.List;
import java.util.Map;

public interface GirlScoutsJcrNodeOCMService {

    public JcrNode create(JcrNode object);
    public JcrNode update(JcrNode object);

    public JcrNode read(String path);
    public boolean delete(JcrNode object);

    public JcrNode findObject(String path, Map<String, String> params);
    public List<JcrNode> findObjects(String path, Map<String, String> params);

}
