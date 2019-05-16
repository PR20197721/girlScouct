package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.CouncilInfo;

import java.util.List;
import java.util.Map;

public interface GirlScoutsCouncilInfoOCMService {
    CouncilInfo create(CouncilInfo object);

    CouncilInfo update(CouncilInfo object);

    CouncilInfo read(String path);

    boolean delete(CouncilInfo object);

    CouncilInfo findObject(String path, Map<String, String> params);

    List<CouncilInfo> findObjects(String path, Map<String, String> params);

}
