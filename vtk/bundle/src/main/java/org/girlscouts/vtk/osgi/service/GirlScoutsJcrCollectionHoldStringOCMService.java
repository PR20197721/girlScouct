package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.JcrCollectionHoldString;

import java.util.List;
import java.util.Map;

public interface GirlScoutsJcrCollectionHoldStringOCMService {

    public JcrCollectionHoldString create(JcrCollectionHoldString object);
    public JcrCollectionHoldString update(JcrCollectionHoldString object);

    public JcrCollectionHoldString read(String path);
    public boolean delete(JcrCollectionHoldString object);

    public JcrCollectionHoldString findObject(String path, Map<String, String> params);
    public List<JcrCollectionHoldString> findObjects(String path, Map<String, String> params);

}
