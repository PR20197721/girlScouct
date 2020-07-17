package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.Meeting;

import java.util.List;
import java.util.Map;

public interface GirlScoutsMeetingOCMService {
    Meeting create(Meeting object);

    Meeting update(Meeting object);

    Meeting read(String path);

    boolean delete(Meeting object);

    Meeting findObject(String path, Map<String, String> params);

    List<Meeting> findObjects(String path, Map<String, String> params);

    List<Meeting> findObjectsCustomQuery(String query);

}
