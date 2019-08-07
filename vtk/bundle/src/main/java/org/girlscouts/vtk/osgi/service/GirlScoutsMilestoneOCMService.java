package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.Milestone;

import java.util.List;
import java.util.Map;

public interface GirlScoutsMilestoneOCMService {
    Milestone create(Milestone object);

    Milestone update(Milestone object);

    Milestone read(String path);

    boolean delete(Milestone object);

    Milestone findObject(String path, Map<String, String> params);

    List<Milestone> findObjects(String path, Map<String, String> params);

}
