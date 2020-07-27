package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.models.Contact;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;

import java.util.List;

public interface MulesoftService {

    User getUser(org.apache.jackrabbit.api.security.user.User user);

    List<Troop> getTroops(User user);

    List<Contact> getContactsForTroop(Troop troop);

    List<Contact> getTroopLeaderInfoByTroopId(ApiConfig apiConfig, String sfTroopId);

    ApiConfig getApiConfig(org.apache.jackrabbit.api.security.user.User user);
}
