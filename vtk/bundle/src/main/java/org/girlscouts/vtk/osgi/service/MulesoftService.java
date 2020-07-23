package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.models.Contact;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;

import java.util.List;

public interface MulesoftService {

    User getUserDetails(String gsGlobalId);

    List<Troop> getTroopInfoByUserId(ApiConfig apiConfig, String userId);

    List<Contact> getContactsForTroop(ApiConfig apiConfig, Troop troop);

    List<Contact> getTroopLeaderInfoByTroopId(ApiConfig apiConfig, String sfTroopId);
}
