package org.girlscouts.vtk.osgi.service.impl;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.osgi.conf.GirlScoutsSalesForceFileClientConfig;
import org.girlscouts.vtk.osgi.service.GirlScoutsRepoFileIOService;
import org.girlscouts.vtk.osgi.service.GirlScoutsSalesForceFileClient;
import org.girlscouts.vtk.osgi.service.GirlScoutsSalesForceRestClient;
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

import java.util.HashMap;
import java.util.Map;

@Component(service = {GirlScoutsSalesForceFileClient.class }, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsSalesForceFileClientImpl")
@Designate(ocd = GirlScoutsSalesForceFileClientConfig.class)
public class GirlScoutsSalesForceFileClientImpl extends BasicGirlScoutsService implements GirlScoutsSalesForceFileClient {

    @Reference
    GirlScoutsRepoFileIOService girlScoutsRepoFileIOService;

    private static Logger log = LoggerFactory.getLogger(GirlScoutsSalesForceFileClientImpl.class);

    private String localJsonPath;

    private String localDemoFolder;


    @Activate
    private void activate(ComponentContext context) {
        this.context = context;
        this.localJsonPath = getConfig("localJsonPath");
        this.localDemoFolder = getConfig("localDemoFolder");
        log.info(this.getClass().getName()+" activated.");
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
            path = getPath(apiConfig, "user");
            log.debug("Loading user file from "+path);
            String json = girlScoutsRepoFileIOService.readFile(path);
            user = new Gson().fromJson(json, UserInfoResponseEntity.class);
        } catch (Exception e) {
            log.error("Error occurred getting user info by id from file at "+path,e);
        }
        return user;
    }

    @Override
    public TroopInfoResponseEntity getTroopInfoByUserId(ApiConfig apiConfig, String userId) {
        TroopInfoResponseEntity troopInfoResponseEntity = null;
        String path = "";
        try {
            path = getPath(apiConfig, "troops");
            log.debug("Loading troops file from "+path);
            String json = girlScoutsRepoFileIOService.readFile(path);
            troopInfoResponseEntity = new Gson().fromJson(json, TroopInfoResponseEntity.class);
        } catch (Exception e) {
            log.error("Error occurred getting troop info by user id from salesforce ",e);
        }
        return troopInfoResponseEntity;
    }

    @Override
    public ContactsInfoResponseEntity getContactsByTroopId(ApiConfig apiConfig, String sfTroopId) {
        ContactsInfoResponseEntity contactsInfoResponseEntity = null;
        String path = "";
        try {
            path = getPath(apiConfig, "contacts");
            log.debug("Loading contacts file from "+path);
            String json = girlScoutsRepoFileIOService.readFile(path);
            contactsInfoResponseEntity = new Gson().fromJson(json, ContactsInfoResponseEntity.class);
        } catch (Exception e) {
            log.error("Error occurred getting contacts info by troop id from salesforce ",e);
        }
        return contactsInfoResponseEntity;
    }

    @Override
    public TroopLeadersInfoResponseEntity getTroopLeaderInfoByTroopId(ApiConfig apiConfig, String sfTroopId) {
        TroopLeadersInfoResponseEntity troopLeadersInfoResponseEntity = null;
        String path = "";
        try {
            path = getPath(apiConfig, "troop_leaders");
            log.debug("Loading troop_leaders file from "+path);
            String json = girlScoutsRepoFileIOService.readFile(path);
            troopLeadersInfoResponseEntity = new Gson().fromJson(json, TroopLeadersInfoResponseEntity.class);
        } catch (Exception e) {
            log.error("Error occurred getting troop leader info by troop id from salesforce ",e);
        }
        return troopLeadersInfoResponseEntity;
    }

    private String getPath(ApiConfig apiConfig, String serviceName) {
        String path = "";
        if(!apiConfig.isDemoUser()) {
            String userFolder = apiConfig.getUser().getFirstName();
            String councilFolder = String.valueOf(apiConfig.getUser().getTroops().get(0).getCouncilCode());
            if(userFolder != null){
                userFolder = userFolder.replace(" ","_");
            }else{
                userFolder = "no_name";
            }
            userFolder += "_" + apiConfig.getUser().getSfUserId();
            if(councilFolder == null || councilFolder.length() == 0){
                councilFolder = "no_council";
            }
            path = getConfig("localJsonPath") + "/" + councilFolder + "/" + userFolder + "/" + serviceName + ".json";
        }else{
            path = localJsonPath+localDemoFolder + "/" + apiConfig.getDemoUserName() + "/" + serviceName + ".json";
        }
        return path;
    }
}
