package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.rest.entity.salesforce.*;

import java.util.List;

public interface GirlScoutsSalesForceRestClient {
    public JWTAuthEntity getJWTAuth(String accessToken);
    public UserInfoResponseEntity getUserInfo(ApiConfig apiConfig);
    public UserInfoResponseEntity getUserInfoById(ApiConfig apiConfig, String userId);
    public TroopInfoResponseEntity getTroopInfoByUserId(ApiConfig apiConfig, String userId);
    public ContactsInfoResponseEntity getContactsByTroopId(ApiConfig apiConfig, String sfTroopId);
    public TroopLeadersInfoResponseEntity getTroopLeaderInfoByTroopId(ApiConfig apiConfig, String sfTroopId);
}
