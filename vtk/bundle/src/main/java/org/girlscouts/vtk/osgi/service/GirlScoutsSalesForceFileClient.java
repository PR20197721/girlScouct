package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.rest.entity.salesforce.ContactsInfoResponseEntity;
import org.girlscouts.vtk.rest.entity.salesforce.TroopInfoResponseEntity;
import org.girlscouts.vtk.rest.entity.salesforce.TroopLeadersInfoResponseEntity;
import org.girlscouts.vtk.rest.entity.salesforce.UserInfoResponseEntity;

public interface GirlScoutsSalesForceFileClient {
    public UserInfoResponseEntity getUserInfo(ApiConfig apiConfig);
    public UserInfoResponseEntity getUserInfoById(ApiConfig apiConfig, String userId);
    public TroopInfoResponseEntity getTroopInfoByUserId(ApiConfig apiConfig, String userId);
    public ContactsInfoResponseEntity getContactsByTroopId(ApiConfig apiConfig, String sfTroopId);
    public TroopLeadersInfoResponseEntity getTroopLeaderInfoByTroopId(ApiConfig apiConfig, String sfTroopId);
    public TroopInfoResponseEntity getServiceUnitManagerTroops();
    public UserInfoResponseEntity getIndependentRegisteredMember();
}
