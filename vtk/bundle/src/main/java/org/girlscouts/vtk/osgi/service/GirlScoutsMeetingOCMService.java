package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.Meeting;

import java.util.List;
import java.util.Map;

public interface GirlScoutsMeetingOCMService {

    public Meeting create(Meeting object);
    public Meeting update(Meeting object);

    public Meeting read(String path);
    public boolean delete(Meeting object);

    public Meeting findObject(String path, Map<String, String> params);
    public List<Meeting> findObjects(String path, Map<String, String> params);

}
