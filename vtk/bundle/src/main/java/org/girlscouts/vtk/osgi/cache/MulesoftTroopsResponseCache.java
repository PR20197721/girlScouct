package org.girlscouts.vtk.osgi.cache;

import org.girlscouts.vtk.rest.entity.mulesoft.TroopInfoResponseEntity;

public interface MulesoftTroopsResponseCache {

    boolean contains(String key);
    TroopInfoResponseEntity read(String key);
    void write(String key, TroopInfoResponseEntity entity);

}
