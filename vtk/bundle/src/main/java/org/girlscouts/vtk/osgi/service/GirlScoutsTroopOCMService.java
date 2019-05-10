package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.Troop;

import java.util.List;
import java.util.Map;

public interface GirlScoutsTroopOCMService {

    public Troop create(Troop object);
    public Troop update(Troop object);

    public Troop read(String path);
    public boolean delete(Troop object);

    public Troop findObject(String path, Map<String, String> params);
    public List<Troop> findObjects(String path, Map<String, String> params);

}
