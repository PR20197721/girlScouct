package org.girlscouts.vtk.osgi.cache;

import org.girlscouts.vtk.rest.entity.salesforce.ContactsInfoResponseEntity;

public interface MulesoftContactsResponseCache {

    boolean contains(String key);
    ContactsInfoResponseEntity read(String key);
    void write(String key, ContactsInfoResponseEntity entity);

}
