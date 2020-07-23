package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.rest.entity.salesforce.ContactsInfoResponseEntity;
import org.girlscouts.vtk.rest.entity.salesforce.TroopInfoResponseEntity;
import org.girlscouts.vtk.rest.entity.salesforce.TroopLeadersInfoResponseEntity;
import org.girlscouts.vtk.rest.entity.salesforce.UserInfoResponseEntity;

public interface MulesoftFileClient {
    UserInfoResponseEntity getUserInfo(ApiConfig apiConfig);

    UserInfoResponseEntity getUserInfoById(ApiConfig apiConfig, String userId);

    TroopInfoResponseEntity getTroopInfoByUserId(ApiConfig apiConfig, String userId);

    ContactsInfoResponseEntity getContactsByTroopId(ApiConfig apiConfig, String sfTroopId);

    TroopLeadersInfoResponseEntity getTroopLeaderInfoByTroopId(ApiConfig apiConfig, String sfTroopId);

    TroopInfoResponseEntity getServiceUnitManagerTroops();

    ContactsInfoResponseEntity getServiceUnitManagerContacts();

    UserInfoResponseEntity getIndependentRegisteredMember();
}
