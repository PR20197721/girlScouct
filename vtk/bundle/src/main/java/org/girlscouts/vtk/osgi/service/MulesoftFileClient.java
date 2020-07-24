package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.rest.entity.mulesoft.TroopInfoResponseEntity;
import org.girlscouts.vtk.rest.entity.mulesoft.UserInfoResponseEntity;

public interface MulesoftFileClient {

    UserInfoResponseEntity getUserInfo(String gsGlobalId, Boolean isDemo);

    TroopInfoResponseEntity getTroopInfoByUserId(String gsGlobalId, Boolean isDemo);

    ContactsInfoResponseEntity getContactsByTroopId(ApiConfig apiConfig, String sfTroopId);

    TroopLeadersInfoResponseEntity getTroopLeaderInfoByTroopId(ApiConfig apiConfig, String sfTroopId);

    TroopInfoResponseEntity getServiceUnitManagerTroops();

    ContactsInfoResponseEntity getServiceUnitManagerContacts();

    UserInfoResponseEntity getIndependentRegisteredMember();
}
