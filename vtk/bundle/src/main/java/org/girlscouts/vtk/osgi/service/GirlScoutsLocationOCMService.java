package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.Location;

import java.util.List;
import java.util.Map;

public interface GirlScoutsLocationOCMService {

    public Location create(Location object);
    public Location update(Location object);

    public Location read(String path);
    public boolean delete(Location object);

    public Location findObject(String path, Map<String, String> params);
    public List<Location> findObjects(String path, Map<String, String> params);

}
