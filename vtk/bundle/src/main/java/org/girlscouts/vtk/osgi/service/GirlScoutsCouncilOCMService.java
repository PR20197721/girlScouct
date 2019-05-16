package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.Council;

import java.util.List;
import java.util.Map;

public interface GirlScoutsCouncilOCMService {
    Council create(Council object);

    Council update(Council object);

    Council read(String path);

    boolean delete(Council object);

    Council findObject(String path, Map<String, String> params);

    List<Council> findObjects(String path, Map<String, String> params);

}
