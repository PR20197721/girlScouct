package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.ocm.JcrNode;

import java.util.List;
import java.util.Map;

public interface GirlScoutsOCMRepository {
    <T extends JcrNode> T create(T object);

    <T extends JcrNode> T update(T object);

    <T extends JcrNode> Object read(String path);

    <T extends JcrNode> boolean delete(T object);

    <T extends JcrNode> T findObject(String path, Map<String, String> params, Class<T> clazz);

    <T extends JcrNode> List<T> findObjects(String path, Map<String, String> params, Class<T> clazz);

    <T extends JcrNode> List<T> findObjectsCustomQuery(String query, Class<T> clazz);

}
