package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.SentEmail;

import java.util.List;
import java.util.Map;

public interface GirlScoutsSentEmailOCMService {

    public SentEmail create(SentEmail object);
    public SentEmail update(SentEmail object);

    public SentEmail read(String path);
    public boolean delete(SentEmail object);

    public SentEmail findObject(String path, Map<String, String> params);
    public List<SentEmail> findObjects(String path, Map<String, String> params);

}
