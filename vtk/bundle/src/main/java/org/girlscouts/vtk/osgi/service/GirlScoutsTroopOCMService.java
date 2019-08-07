package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.Troop;

import java.util.List;
import java.util.Map;

public interface GirlScoutsTroopOCMService {
    Troop create(Troop object);

    Troop update(Troop object);

    Troop read(String path);

    boolean delete(Troop object);

    Troop findObject(String path, Map<String, String> params);

    List<Troop> findObjects(String path, Map<String, String> params);

}
