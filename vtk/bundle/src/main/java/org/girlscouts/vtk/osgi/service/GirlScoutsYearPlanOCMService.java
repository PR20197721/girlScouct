package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.YearPlan;

import java.util.List;
import java.util.Map;

public interface GirlScoutsYearPlanOCMService {
    YearPlan create(YearPlan object);

    YearPlan update(YearPlan object);

    YearPlan read(String path);

    boolean delete(YearPlan object);

    YearPlan findObject(String path, Map<String, String> params);

    List<YearPlan> findObjects(String path, Map<String, String> params);

}
