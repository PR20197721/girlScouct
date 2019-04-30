package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.rest.entity.salesforce.*;

public interface GirlScoutsSalesForceRestClient {
    JWTAuthEntity getJWTAuth(String accessToken);

    UserInfoResponseEntity getUserInfo(ApiConfig apiConfig);

    UserInfoResponseEntity getUserInfoById(ApiConfig apiConfig, String userId);

    TroopInfoResponseEntity getTroopInfoByUserId(ApiConfig apiConfig, String userId);

    ContactsInfoResponseEntity getContactsByTroopId(ApiConfig apiConfig, String sfTroopId);

    TroopLeadersInfoResponseEntity getTroopLeaderInfoByTroopId(ApiConfig apiConfig, String sfTroopId);
}
