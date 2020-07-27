package org.girlscouts.vtk.osgi.cache;

import org.girlscouts.vtk.rest.entity.mulesoft.TroopMembersResponseEntity;

public interface MulesoftContactsResponseCache {

    boolean contains(String key);
    TroopMembersResponseEntity read(String key);
    void write(String key, TroopMembersResponseEntity entity);

}
