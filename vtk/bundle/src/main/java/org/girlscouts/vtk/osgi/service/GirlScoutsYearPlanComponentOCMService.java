package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.YearPlanComponent;

import java.util.List;
import java.util.Map;

public interface GirlScoutsYearPlanComponentOCMService {
    YearPlanComponent create(YearPlanComponent object);

    YearPlanComponent update(YearPlanComponent object);

    YearPlanComponent read(String path);

    boolean delete(YearPlanComponent object);

    YearPlanComponent findObject(String path, Map<String, String> params);

    List<YearPlanComponent> findObjects(String path, Map<String, String> params);

}
