package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.Milestone;

import java.util.List;
import java.util.Map;

public interface GirlScoutsMilestoneOCMService {

    public Milestone create(Milestone object);
    public Milestone update(Milestone object);

    public Milestone read(String path);
    public boolean delete(Milestone object);

    public Milestone findObject(String path, Map<String, String> params);
    public List<Milestone> findObjects(String path, Map<String, String> params);

}
