package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.JcrNode;

import java.util.List;
import java.util.Map;

public interface GirlScoutsJcrNodeOCMService {
    JcrNode create(JcrNode object);

    JcrNode update(JcrNode object);

    JcrNode read(String path);

    boolean delete(JcrNode object);

    JcrNode findObject(String path, Map<String, String> params);

    List<JcrNode> findObjects(String path, Map<String, String> params);

}
