package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.mapper.ParentEntityToTroopMapper;
import org.girlscouts.vtk.mapper.TroopEntityToTroopMapper;
import org.girlscouts.vtk.mapper.UserInfoResponseEntityToUserMapper;
import org.girlscouts.vtk.models.Contact;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.osgi.conf.GirlScoutsSalesForceServiceConfig;
import org.girlscouts.vtk.osgi.service.GirlScoutsSalesForceFileClient;
import org.girlscouts.vtk.osgi.service.GirlScoutsSalesForceRestClient;
import org.girlscouts.vtk.osgi.service.GirlScoutsSalesForceService;
import org.girlscouts.vtk.rest.entity.salesforce.*;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Component(service = {GirlScoutsSalesForceService.class }, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsSalesForceServiceImpl")
@Designate(ocd = GirlScoutsSalesForceServiceConfig.class)
public class GirlScoutsSalesForceServiceImpl extends BasicGirlScoutsService implements GirlScoutsSalesForceService {

    @Reference
    GirlScoutsSalesForceRestClient sfRestClient;

    @Reference
    GirlScoutsSalesForceFileClient sfFileClient;

    private ComponentContext context;

    private int demoCouncilCode;
    private boolean isLoadFromFile;

    private static Logger log = LoggerFactory.getLogger(GirlScoutsSalesForceServiceImpl.class);

    @Activate
    private void activate(ComponentContext context) {
        this.context = context;
        this.isLoadFromFile = Boolean.parseBoolean(getConfig("isLoadFromFile"));
        try {
            this.demoCouncilCode = Integer.parseInt(getConfig("demoCouncilCode"));
        }catch(Exception e){
            log.error("Exception is thrown parsint demo council code", e);
        }
        log.info("Girl Scouts VTK SalesForce Service Activated.");
    }

    @Override
    public ApiConfig getApiConfig(String accessToken) {
        ApiConfig apiConfig = null;
        try{
            JWTAuthEntity jwtAuthEntity = sfRestClient.getJWTAuth(accessToken);
            apiConfig = new ApiConfig();
            apiConfig.setAccessToken(jwtAuthEntity.getAccessToken());
            apiConfig.setInstanceUrl(jwtAuthEntity.getInstanceUrl());
            return apiConfig;
        }catch(Exception e){
            log.error("Error occurred: ", e);
        }
        StringBuffer token = new StringBuffer();

        return null;
    }

    @Override
    public User getUser(ApiConfig apiConfig) {
        User user = new User();
        try {
            UserInfoResponseEntity userInfoResponseEntity = null;
            if (!apiConfig.isDemoUser() && !isLoadFromFile) {
                userInfoResponseEntity = sfRestClient.getUserInfo(apiConfig);
            }else{
                userInfoResponseEntity = sfFileClient.getUserInfo(apiConfig);
            }
            UserInfoResponseEntityToUserMapper mapper = new UserInfoResponseEntityToUserMapper();
            mapper.map(userInfoResponseEntity, user);
            addMoreInfo(apiConfig, userInfoResponseEntity, user);
        }catch(Exception e){
            log.error("Error occurred: ", e);
        }
        return user;
    }

    @Override
    public User getUserById(ApiConfig apiConfig, String userId) {
        User user = new User();
        try{
            user.setApiConfig(apiConfig);
            UserInfoResponseEntity userInfoResponseEntity = sfRestClient.getUserInfoById(apiConfig, userId);
            UserInfoResponseEntityToUserMapper mapper =  new UserInfoResponseEntityToUserMapper();
            mapper.map(userInfoResponseEntity, user);
            addMoreInfo(apiConfig, userInfoResponseEntity, user);
        }catch(Exception e){
            log.error("Error occurred: ", e);
        }
        return user;
    }

    @Override
    public List<Troop> getTroopInfoByUserId(ApiConfig apiConfig, String userId) {
        List<Troop> troops = null;
        try{
            TroopInfoResponseEntity troopInfoResponseEntity = sfRestClient.getTroopInfoByUserId(apiConfig, userId);
            if(troopInfoResponseEntity != null){
                TroopEntityToTroopMapper mapper = new TroopEntityToTroopMapper();
                TroopEntity[] entities = troopInfoResponseEntity.getTroops();
                if(entities != null){
                    for(TroopEntity entity:entities){
                        Troop troop = new Troop();
                        troops.add(mapper.map(entity, troop));
                    }
                }
            }
        }catch(Exception e){
            log.error("Error occurred: ", e);
        }
        return troops;
    }

    @Override
    public List<Contact> getContactsByTroopId(ApiConfig apiConfig, String sfTroopId) {
        List<Contact> contacts = null;
        try{
            ContactsInfoResponseEntity contactsInfoResponseEntity = sfRestClient.getContactsByTroopId(apiConfig, sfTroopId);
        }catch(Exception e){
            log.error("Error occurred: ", e);
        }
        return contacts;
    }

    @Override
    public List<Contact> getTroopLeaderInfoByTroopId(ApiConfig apiConfig, String sfTroopId) {
        List<Contact> contacts = null;
        try{
            TroopLeadersInfoResponseEntity troopLeadersInfoResponseEntity = sfRestClient.getTroopLeaderInfoByTroopId(apiConfig, sfTroopId);
        }catch(Exception e){
            log.error("Error occurred: ", e);
        }
        return contacts;
    }

    private void addMoreInfo(ApiConfig apiConfig, UserInfoResponseEntity userInfoResponseEntity, User user) {
        if(user != null && userInfoResponseEntity != null) {
            if (apiConfig.isDemoUser()) {
                user.setAdminCouncilId(demoCouncilCode);
            }
            ParentEntity[] campsTroops = userInfoResponseEntity.getCamps();
            setTroopsForUser(apiConfig, user, campsTroops);
            apiConfig.setTroops(user.getTroops());
            apiConfig.setUserId(user.getSfUserId());
            apiConfig.setUser(user);
            user.setApiConfig(apiConfig);
        }
    }

    private void setTroopsForUser(ApiConfig apiConfig, User user, ParentEntity[] campsTroops) {
        List<Troop> parentTroops = new ArrayList<Troop>();
        if(campsTroops != null && campsTroops.length > 0){
            ParentEntityToTroopMapper mapper = new ParentEntityToTroopMapper();
            for(ParentEntity entity: campsTroops){
                Troop troop = new Troop();
                parentTroops.add(mapper.map(entity, troop));
            }
        }
        List<Troop> additionalTroops = getTroopInfoByUserId(apiConfig, user.getSfUserId());
        user.setTroops(mergeTroops(parentTroops, additionalTroops));
    }

    private List<Troop> mergeTroops(List<Troop> A, List<Troop> B) {
        if ((A == null && B == null) || (A.size() <= 0 && B.size() <= 0)) {
            return new ArrayList();
        }
        if ((A == null || A.size() <= 0) && (B != null)) {
            return B;
        }
        if ((B == null || B.size() <= 0) && A != null) {
            return A;
        }
        List<Troop> troopDiff = getTroopsNotInA(A, B);
        A.addAll(troopDiff);
        for (int i = 0; i < A.size(); i++) {
            Troop troop = A.get(i);
            for (int y = 0; y < B.size(); y++) {
                Troop _troop = B.get(y);
                if (_troop.getTroopId().equals(troop.getTroopId())) {
                    try {
                        if ("DP".equals(_troop.getRole()) && "PA".equals(troop.getRole())) {
                            troop.setRole("DP");
                        }
                    } catch (Exception e) {
                        log.error("Error occurred while merging troops for user ", e);
                    }
                    troop.getPermissionTokens().addAll( _troop.getPermissionTokens());
                }
            }
        }
        return A;
    }

    private List<Troop> getTroopsNotInA(List<Troop> A, List<Troop> B) {
        List<Troop> troopDiff = new ArrayList();
        for (int i = 0; i < B.size(); i++) {
            Troop troop = B.get(i);
            boolean isFound = false;
            for (int y = 0; y < A.size(); y++) {
                Troop _troop = A.get(y);
                if (_troop.getTroopId().equals(troop.getTroopId())) {
                    isFound = true;
                    break;
                }
            }
            if (!isFound) {
                troopDiff.add(troop);
            }
        }
        return troopDiff;
    }
}
