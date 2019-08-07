package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.Finance;

import java.util.List;
import java.util.Map;

public interface GirlScoutsFinanceOCMService {
    Finance create(Finance object);

    Finance update(Finance object);

    Finance read(String path);

    boolean delete(Finance object);

    Finance findObject(String path, Map<String, String> params);

    List<Finance> findObjects(String path, Map<String, String> params);

}
