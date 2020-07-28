package org.girlscouts.vtk.osgi.service.impl;

import com.google.gson.Gson;
import org.girlscouts.vtk.osgi.conf.MulesoftFileClientConfig;
import org.girlscouts.vtk.osgi.service.GirlScoutsRepoFileIOService;
import org.girlscouts.vtk.osgi.service.MulesoftFileClient;
import org.girlscouts.vtk.rest.entity.mulesoft.TroopInfoResponseEntity;
import org.girlscouts.vtk.rest.entity.mulesoft.TroopLeadersResponseEntity;
import org.girlscouts.vtk.rest.entity.mulesoft.TroopMembersResponseEntity;
import org.girlscouts.vtk.rest.entity.mulesoft.UserInfoResponseEntity;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = {MulesoftFileClient.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.MulesoftFileClientImpl")
@Designate(ocd = MulesoftFileClientConfig.class)
public class MulesoftFileClientImpl extends BasicGirlScoutsService implements MulesoftFileClient {
    private static Logger log = LoggerFactory.getLogger(MulesoftFileClientImpl.class);
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
    public UserInfoResponseEntity getUser(String gsGlobalId, Boolean isDemo) {
        UserInfoResponseEntity user = null;
        String path = "";
        try {
            path = getPath(gsGlobalId, isDemo,  "user");
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
    public TroopInfoResponseEntity getTroops(String gsGlobalId, Boolean isDemo) {
        TroopInfoResponseEntity troopInfoResponseEntity = null;
        String path = "";
        try {
            path = getPath(gsGlobalId, isDemo, "troops");
            log.debug("Loading troops file from " + path);
            String json = girlScoutsRepoFileIOService.readFile(path);
            troopInfoResponseEntity = new Gson().fromJson(json, TroopInfoResponseEntity.class);
        } catch (Exception e) {
            log.error("Error occurred getting troop info by user id from repository ", e);
        }
        return troopInfoResponseEntity;
    }

    @Override
    public TroopMembersResponseEntity getMembers(String sfTroopId, Boolean isDemo) {
        TroopMembersResponseEntity contactsInfoResponseEntity = null;
        String path = "";
        try {
            path = getPath(sfTroopId, isDemo, "contacts");
            log.debug("Loading contacts file from " + path);
            String json = girlScoutsRepoFileIOService.readFile(path);
            log.debug(json);
            contactsInfoResponseEntity = new Gson().fromJson(json, TroopMembersResponseEntity.class);
        } catch (Exception e) {
            log.error("Error occurred getting contacts info by troop id from repository ", e);
        }
        return contactsInfoResponseEntity;
    }

    @Override
    public TroopLeadersResponseEntity getTroopLeaders(String sfTroopId, Boolean isDemo) {
        TroopLeadersResponseEntity troopLeadersInfoResponseEntity = null;
        String path = "";
        try {
            path = getPath(sfTroopId, isDemo, "troop_leaders");
            log.debug("Loading troop_leaders file from " + path);
            String json = girlScoutsRepoFileIOService.readFile(path);
            log.debug(json);
            troopLeadersInfoResponseEntity = new Gson().fromJson(json, TroopLeadersResponseEntity.class);
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
    public TroopMembersResponseEntity getServiceUnitManagerContacts() {
        TroopMembersResponseEntity contactsInfoResponseEntity = null;
        String path = "";
        try {
            path = localJsonPath + localDummyFolder + "/su_contacts.json";
            log.debug("Loading contacts file from " + path);
            String json = girlScoutsRepoFileIOService.readFile(path);
            log.debug(json);
            contactsInfoResponseEntity = new Gson().fromJson(json, TroopMembersResponseEntity.class);
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

    private String getPath(String id,  Boolean isDemo, String serviceName) {
        if (!isDemo) {
            return getConfig("localJsonPath") + "/" + id + "/" + serviceName + ".json";
        } else {
            return localJsonPath + localDemoFolder + "/" + id + "/" + serviceName + ".json";
        }
    }
}
