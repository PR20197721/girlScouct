package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.Finance;

import java.util.List;
import java.util.Map;

public interface GirlScoutsFinanceOCMService {

    public Finance create(Finance object);
    public Finance update(Finance object);

    public Finance read(String path);
    public boolean delete(Finance object);

    public Finance findObject(String path, Map<String, String> params);
    public List<Finance> findObjects(String path, Map<String, String> params);

}
