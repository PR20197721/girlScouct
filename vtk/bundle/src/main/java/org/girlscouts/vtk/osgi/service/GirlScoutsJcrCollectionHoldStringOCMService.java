package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.JcrCollectionHoldString;

import java.util.List;
import java.util.Map;

public interface GirlScoutsJcrCollectionHoldStringOCMService {
    JcrCollectionHoldString create(JcrCollectionHoldString object);

    JcrCollectionHoldString update(JcrCollectionHoldString object);

    JcrCollectionHoldString read(String path);

    boolean delete(JcrCollectionHoldString object);

    JcrCollectionHoldString findObject(String path, Map<String, String> params);

    List<JcrCollectionHoldString> findObjects(String path, Map<String, String> params);

}
