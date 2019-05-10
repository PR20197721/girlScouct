package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.Council;

import java.util.List;
import java.util.Map;

public interface GirlScoutsCouncilOCMService {

    public Council create(Council object);
    public Council update(Council object);

    public Council read(String path);
    public boolean delete(Council object);

    public Council findObject(String path, Map<String, String> params);
    public List<Council> findObjects(String path, Map<String, String> params);

}
