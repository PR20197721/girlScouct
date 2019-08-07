package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.Achievement;

import java.util.List;
import java.util.Map;

public interface GirlScoutsAchievementOCMService {
    Achievement create(Achievement object);

    Achievement update(Achievement object);

    Achievement read(String path);

    boolean delete(Achievement object);

    Achievement findObject(String path, Map<String, String> params);

    List<Achievement> findObjects(String path, Map<String, String> params);

}
