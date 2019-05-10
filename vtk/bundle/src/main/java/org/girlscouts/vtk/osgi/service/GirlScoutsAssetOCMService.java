package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.Asset;

import java.util.List;
import java.util.Map;

public interface GirlScoutsAssetOCMService {

    public Asset create(Asset object);
    public Asset update(Asset object);

    public Asset read(String path);
    public boolean delete(Asset object);

    public Asset findObject(String path, Map<String, String> params);
    public List<Asset> findObjects(String path, Map<String, String> params);

}
