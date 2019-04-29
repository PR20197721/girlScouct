package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.Contact;
import org.girlscouts.vtk.models.Troop;


import java.util.List;

public interface GirlScoutsSalesForceService {
    public ApiConfig getApiConfig(String token);
    public User getUser(ApiConfig apiConfig);
    public User getUserById(ApiConfig apiConfig, String userId);
    public List<Troop> getTroopInfoByUserId(ApiConfig apiConfig, String userId);
    public List<Contact> getContactsForTroop(ApiConfig apiConfig, Troop troop);
    public List<Contact> getTroopLeaderInfoByTroopId(ApiConfig apiConfig, String sfTroopId);
}
