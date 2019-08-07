package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.Location;

import java.util.List;
import java.util.Map;

public interface GirlScoutsLocationOCMService {
    Location create(Location object);

    Location update(Location object);

    Location read(String path);

    boolean delete(Location object);

    Location findObject(String path, Map<String, String> params);

    List<Location> findObjects(String path, Map<String, String> params);

}
