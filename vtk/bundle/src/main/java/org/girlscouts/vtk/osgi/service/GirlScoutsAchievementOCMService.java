package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.*;

import javax.jcr.Node;
import javax.jcr.query.QueryResult;
import java.util.List;
import java.util.Map;

public interface GirlScoutsAchievementOCMService{

    public Achievement create(Achievement object);
    public Achievement update(Achievement object);

    public Achievement read(String path);
    public boolean delete(Achievement object);

    public Achievement findObject(String path, Map<String, String> params);
    public List<Achievement> findObjects(String path, Map<String, String> params);

}
