package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.models.Asset;

import java.util.List;
import java.util.Map;

public interface GirlScoutsAssetOCMService {
    Asset create(Asset object);

    Asset update(Asset object);

    Asset read(String path);

    boolean delete(Asset object);

    Asset findObject(String path, Map<String, String> params);

    List<Asset> findObjects(String path, Map<String, String> params);

}
