package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.Council;
import org.girlscouts.vtk.models.CouncilInfo;

import java.util.List;
import java.util.Map;

public interface GirlScoutsCouncilInfoOCMService {

    public CouncilInfo create(CouncilInfo object);
    public CouncilInfo update(CouncilInfo object);

    public CouncilInfo read(String path);
    public boolean delete(CouncilInfo object);

    public CouncilInfo findObject(String path, Map<String, String> params);
    public List<CouncilInfo> findObjects(String path, Map<String, String> params);

}
