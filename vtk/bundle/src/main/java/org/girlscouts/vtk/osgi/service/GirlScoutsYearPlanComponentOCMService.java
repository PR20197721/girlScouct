package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.YearPlanComponent;

import java.util.List;
import java.util.Map;

public interface GirlScoutsYearPlanComponentOCMService {

    public YearPlanComponent create(YearPlanComponent object);
    public YearPlanComponent update(YearPlanComponent object);

    public YearPlanComponent read(String path);
    public boolean delete(YearPlanComponent object);

    public YearPlanComponent findObject(String path, Map<String, String> params);
    public List<YearPlanComponent> findObjects(String path, Map<String, String> params);

}
