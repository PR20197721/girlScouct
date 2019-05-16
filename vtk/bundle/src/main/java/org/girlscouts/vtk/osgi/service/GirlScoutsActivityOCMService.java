package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.Activity;

import java.util.List;
import java.util.Map;

public interface GirlScoutsActivityOCMService {
    Activity create(Activity object);

    Activity update(Activity object);

    Activity read(String path);

    boolean delete(Activity object);

    Activity findObject(String path, Map<String, String> params);

    List<Activity> findObjects(String path, Map<String, String> params);

}
