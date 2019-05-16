package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.Cal;

import java.util.List;
import java.util.Map;

public interface GirlScoutsCalOCMService {
    Cal create(Cal object);

    Cal update(Cal object);

    Cal read(String path);

    boolean delete(Cal object);

    Cal findObject(String path, Map<String, String> params);

    List<Cal> findObjects(String path, Map<String, String> params);

}
