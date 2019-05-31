package org.girlscouts.vtk.osgi.service.impl;

import com.google.gson.Gson;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.osgi.conf.GirlScoutsSalesForceFileClientConfig;
import org.girlscouts.vtk.osgi.service.GirlScoutsRepoFileIOService;
import org.girlscouts.vtk.osgi.service.GirlScoutsSalesForceFileClient;
import org.girlscouts.vtk.rest.entity.salesforce.ContactsInfoResponseEntity;
import org.girlscouts.vtk.rest.entity.salesforce.TroopInfoResponseEntity;
import org.girlscouts.vtk.rest.entity.salesforce.TroopLeadersInfoResponseEntity;
import org.girlscouts.vtk.rest.entity.salesforce.UserInfoResponseEntity;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = {GirlScoutsSalesForceFileClient.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsSalesForceFileClientImpl")
@Designate(ocd = GirlScoutsSalesForceFileClientConfig.class)
public class GirlScoutsSalesForceFileClientImpl extends BasicGirlScoutsService implements GirlScoutsSalesForceFileClient {
    private static Logger log = LoggerFactory.getLogger(GirlScoutsSalesForceFileClientImpl.class);
    @Reference
    GirlScoutsRepoFileIOService girlScoutsRepoFileIOService;
    private String localJsonPath;
    private String localDemoFolder;
    private String localDummyFolder;

    @Activate
    private void activate(ComponentContext context) {
        this.context = context;
        this.localJsonPath = getConfig("localJsonPath");
        this.localDemoFolder = getConfig("localDemoFolder");
        this.localDummyFolder = getConfig("localDummyFolder");
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public UserInfoResponseEntity getUserInfo(ApiConfig apiConfig) {
        return getUserInfoById(apiConfig, apiConfig.getUserId());
    }

    @Override
    public UserInfoResponseEntity getUserInfoById(ApiConfig apiConfig, String userId) {
        UserInfoResponseEntity user = null;
        String path = "";
        try {
            path = getPath(apiConfig, userId, "user");
            log.debug("Loading user file from " + path);
            String json = girlScoutsRepoFileIOService.readFile(path);
            log.debug(json);
            user = new Gson().fromJson(json, UserInfoResponseEntity.class);
        } catch (Exception e) {
            log.error("Error occurred getting user info by id from file at " + path, e);
        }
        return user;
    }

    @Override
    public TroopInfoResponseEntity getTroopInfoByUserId(ApiConfig apiConfig, String userId) {
        TroopInfoResponseEntity troopInfoResponseEntity = null;
        String path = "";
        try {
            path = getPath(apiConfig, userId, "troops");
            log.debug("Loading troops file from " + path);
            String json = girlScoutsRepoFileIOService.readFile(path);
            troopInfoResponseEntity = new Gson().fromJson(json, TroopInfoResponseEntity.class);
        } catch (Exception e) {
            log.error("Error occurred getting troop info by user id from repository ", e);
        }
        return troopInfoResponseEntity;
    }

    @Override
    public ContactsInfoResponseEntity getContactsByTroopId(ApiConfig apiConfig, String sfTroopId) {
        ContactsInfoResponseEntity contactsInfoResponseEntity = null;
        String path = "";
        try {
            path = getPath(apiConfig, apiConfig.getUser().getSfUserId(), "contacts");
            log.debug("Loading contacts file from " + path);
            String json = girlScoutsRepoFileIOService.readFile(path);
            log.debug(json);
            contactsInfoResponseEntity = new Gson().fromJson(json, ContactsInfoResponseEntity.class);
        } catch (Exception e) {
            log.error("Error occurred getting contacts info by troop id from repository ", e);
        }
        return contactsInfoResponseEntity;
    }

    @Override
    public TroopLeadersInfoResponseEntity getTroopLeaderInfoByTroopId(ApiConfig apiConfig, String sfTroopId) {
        TroopLeadersInfoResponseEntity troopLeadersInfoResponseEntity = null;
        String path = "";
        try {
            path = getPath(apiConfig, apiConfig.getUser().getSfUserId(), "troop_leaders");
            log.debug("Loading troop_leaders file from " + path);
            String json = girlScoutsRepoFileIOService.readFile(path);
            log.debug(json);
            troopLeadersInfoResponseEntity = new Gson().fromJson(json, TroopLeadersInfoResponseEntity.class);
        } catch (Exception e) {
            log.error("Error occurred getting troop leader info by troop id from repository ", e);
        }
        return troopLeadersInfoResponseEntity;
    }

    @Override
    public TroopInfoResponseEntity getServiceUnitManagerTroops() {
        TroopInfoResponseEntity troopInfoResponseEntity = null;
        String path = "";
        try {
            path = localJsonPath + localDummyFolder + "/su_troops.json";
            log.debug("Loading troops file from " + path);
            String json = girlScoutsRepoFileIOService.readFile(path);
            log.debug(json);
            troopInfoResponseEntity = new Gson().fromJson(json, TroopInfoResponseEntity.class);
        } catch (Exception e) {
            log.error("Error occurred getting service unit manager dummy troops from repository ", e);
        }
        return troopInfoResponseEntity;
    }

    @Override
    public ContactsInfoResponseEntity getServiceUnitManagerContacts() {
        ContactsInfoResponseEntity contactsInfoResponseEntity = null;
        String path = "";
        try {
            path = localJsonPath + localDummyFolder + "/su_contacts.json";
            log.debug("Loading contacts file from " + path);
            String json = girlScoutsRepoFileIOService.readFile(path);
            log.debug(json);
            contactsInfoResponseEntity = new Gson().fromJson(json, ContactsInfoResponseEntity.class);
        } catch (Exception e) {
            log.error("Error occurred getting service unit manager dummy contacts info from repository ", e);
        }
        return contactsInfoResponseEntity;
    }

    @Override
    public UserInfoResponseEntity getIndependentRegisteredMember() {
        UserInfoResponseEntity user = null;
        String path = "";
        try {
            path = localJsonPath + localDummyFolder + "/irm_troops.json";
            log.debug("Loading troops file from " + path);
            String json = girlScoutsRepoFileIOService.readFile(path);
            log.debug(json);
            user = new Gson().fromJson(json, UserInfoResponseEntity.class);
        } catch (Exception e) {
            log.error("Error occurred getting independent registered member dummy user from repository ", e);
        }
        return user;
    }

    private String getPath(ApiConfig apiConfig, String sfUserId,  String serviceName) {
        String path = "";
        if (!apiConfig.isDemoUser()) {
            String userFolder = sfUserId;
            if (userFolder != null) {
                userFolder = userFolder.replace(" ", "_");
            } else {
                userFolder = "no_name";
            }
            path = getConfig("localJsonPath") + "/" + userFolder + "/" + serviceName + ".json";
        } else {
            path = localJsonPath + localDemoFolder + "/" + apiConfig.getDemoUserName() + "/" + serviceName + ".json";
        }
        return path;
    }
}
