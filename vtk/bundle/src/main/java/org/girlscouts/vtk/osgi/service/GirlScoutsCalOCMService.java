package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.Cal;

import java.util.List;
import java.util.Map;

public interface GirlScoutsCalOCMService {

    public Cal create(Cal object);
    public Cal update(Cal object);

    public Cal read(String path);
    public boolean delete(Cal object);

    public Cal findObject(String path, Map<String, String> params);
    public List<Cal> findObjects(String path, Map<String, String> params);

}
