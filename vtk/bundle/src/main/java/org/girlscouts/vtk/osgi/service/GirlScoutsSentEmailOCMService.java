package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.SentEmail;

import java.util.List;
import java.util.Map;

public interface GirlScoutsSentEmailOCMService {
    SentEmail create(SentEmail object);

    SentEmail update(SentEmail object);

    SentEmail read(String path);

    boolean delete(SentEmail object);

    SentEmail findObject(String path, Map<String, String> params);

    List<SentEmail> findObjects(String path, Map<String, String> params);

}
