package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.Achievement;
import org.girlscouts.vtk.models.Activity;

import java.util.List;
import java.util.Map;

public interface GirlScoutsActivityOCMService {

    public Activity create(Activity object);
    public Activity update(Activity object);

    public Activity read(String path);
    public boolean delete(Activity object);

    public Activity findObject(String path, Map<String, String> params);
    public List<Activity> findObjects(String path, Map<String, String> params);

}
