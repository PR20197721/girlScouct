package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.YearPlan;

import java.util.List;
import java.util.Map;

public interface GirlScoutsYearPlanOCMService {

    public YearPlan create(YearPlan object);
    public YearPlan update(YearPlan object);

    public YearPlan read(String path);
    public boolean delete(YearPlan object);

    public YearPlan findObject(String path, Map<String, String> params);
    public List<YearPlan> findObjects(String path, Map<String, String> params);

}
