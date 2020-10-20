package org.girlscouts.vtk.osgi.service.impl;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.girlscouts.vtk.osgi.service.MulesoftRestClient;
import org.girlscouts.vtk.rest.entity.mulesoft.TroopInfoResponseEntity;
import org.girlscouts.vtk.rest.entity.mulesoft.TroopLeadersResponseEntity;
import org.girlscouts.vtk.rest.entity.mulesoft.TroopMembersResponseEntity;
import org.girlscouts.vtk.rest.entity.mulesoft.UserInfoResponseEntity;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Component(service = {MulesoftRestClient.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.MulesoftRestClientImpl")
@Designate(ocd = MulesoftRestClientImpl.Config.class)
public class MulesoftRestClientImpl extends BasicGirlScoutsService implements MulesoftRestClient {

    private static Logger log = LoggerFactory.getLogger(MulesoftRestClientImpl.class);
    private String endpoint;
    private String userInfo;
    private String troopInfo;
    private String troopLeaderInfo;
    private String contactsInfo;
    private String clientId;
    private String client_secret;

    @Activate
    private void activate(Config config) {
        this.context = context;
        this.endpoint = config.endpoint();
        this.userInfo = this.endpoint + config.userInfo();
        this.troopInfo = this.endpoint + config.troopInfo();
        this.troopLeaderInfo = this.endpoint + config.troopLeaderInfo();
        this.contactsInfo = this.endpoint + config.contactsInfo();
        this.clientId = config.clientId();
        this.client_secret = config.clientSecret();
        log.info("Girl Scouts VTK Mulesoft Rest Client Activated.");
    }

    @Override
    public UserInfoResponseEntity getUser(String gsGlobalId) {
        UserInfoResponseEntity user = null;
        String url = this.userInfo + "?USER_ID=" + gsGlobalId;
        try {
            String json = doGet(url);
            log.debug(json);
            user = new Gson().fromJson(json, UserInfoResponseEntity.class);
        } catch (Exception e) {
            log.error("Error occurred getting user info by id from salesforce ", e);
        }
        return user;
    }

    @Override
    public TroopInfoResponseEntity getTroops(String gsGlobalId) {
        log.debug("Requesting troops for user " + gsGlobalId);
        TroopInfoResponseEntity troopInfoResponseEntity = null;
        String url = this.troopInfo + "?USER_ID=" + gsGlobalId;
        try {
            String json = doGet(url);
            json = "{\"troops\":" + json + "}";//wrapping json from mulesoft which is coming back in array format.
            log.debug(json);
            troopInfoResponseEntity = new Gson().fromJson(json, TroopInfoResponseEntity.class);
        } catch (Exception e) {
            log.error("Error occurred getting troop info by user id from salesforce ", e);
        }
        return troopInfoResponseEntity;
    }

    @Override
    public TroopMembersResponseEntity getMembers(String troopId) {
        log.debug("Requesting contacts for troop " + troopId);
        TroopMembersResponseEntity contactsInfoResponseEntity = null;
        String url = this.contactsInfo + "?TROOP_ID=" + troopId;
        try {
            String json = doGet(url);
            log.debug(json);
            contactsInfoResponseEntity = new Gson().fromJson(json, TroopMembersResponseEntity.class);
        } catch (Exception e) {
            log.error("Error occurred getting troop member info by troop id from mulesoft ", e);
        }
        return contactsInfoResponseEntity;
    }

    @Override
    public TroopLeadersResponseEntity getTroopLeaders(String troopId) {
        log.debug("Requesting troop leaders for troop " + troopId);
        TroopLeadersResponseEntity troopLeadersInfoResponseEntity = null;
        String url = this.troopLeaderInfo+ "?TROOP_ID=" + troopId;
        try {
            String json = doGet(url);
            json = "{\"troopLeaders\":" + json + "}";//Fixing json from salesforce which is coming back in invalid format.
            log.debug(json);
            troopLeadersInfoResponseEntity = new Gson().fromJson(json, TroopLeadersResponseEntity.class);
        } catch (Exception e) {
            log.error("Error occurred getting troop leader info by troop id from salesforce ", e);
        }
        return troopLeadersInfoResponseEntity;
    }

    private String doGet(String url) {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClientBuilder.create().build();
            HttpGet getRequest = new HttpGet(url);
            getRequest.addHeader("accept", "application/json");
            getRequest.addHeader("client_id", this.clientId);
            getRequest.addHeader("client_secret", this.client_secret);
            log.debug("curl -X GET '" + url + "' -H 'Accept: application/json' -H 'client_id: " + this.clientId + "' -H 'client_secret:" + this.client_secret + "'");
            HttpResponse response = httpClient.execute(getRequest);
            String json = getJsonFromResponse(response);
            return json;
        } catch (Exception e) {
            log.error("Exception is thrown making a GET call to " + url, e);
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                log.error("Exception is thrown closing http client to " + url, e);
            }
        }
        return null;
    }

    private String getJsonFromResponse(HttpResponse response) throws IOException {
        if (response.getStatusLine().getStatusCode() != 200) {
            log.error("Mulesoft API status code: " + response.getStatusLine().getStatusCode() + " : " + response);
            throw new RuntimeException("Mulesoft API : HTTP error code : " + response.getStatusLine().getStatusCode());
        }
        HttpEntity entity = response.getEntity();
        entity.getContent();
        String json = EntityUtils.toString(entity);
        log.debug("Mulesoft Response: " + response);
        EntityUtils.consume(entity);
        return json;
    }

    @ObjectClassDefinition(name = "Girl Scouts VTK Mulesoft Rest client configuration", description = "Girl Scouts VTK Mulesoft Rest client configuration")
    public @interface Config {
        @AttributeDefinition(name = "Mulesoft rest endpoint url", type = AttributeType.STRING) String endpoint() default "https://api-uat.girlscoutsnetwork.org/api/sfdc/v1/ProductAccess";

        @AttributeDefinition(name = "Mulesoft user info api", type = AttributeType.STRING) String userInfo() default "/UserInfo";

        @AttributeDefinition(name = "Mulesoft troop info api", type = AttributeType.STRING) String troopInfo() default "/MyTroopData";

        @AttributeDefinition(name = "Mulesoft troop leader info api", type = AttributeType.STRING) String troopLeaderInfo() default "/DPInfo";

        @AttributeDefinition(name = "Mulesoft contacts info api", type = AttributeType.STRING) String contactsInfo() default "/TroopMembers";

        @AttributeDefinition(name = "Girl Scouts Client Id", description = "Girl Scouts Client Id", type = AttributeType.STRING) String clientId() default "ef2595a3a9224ac6a04f1fba5ed1b980";

        @AttributeDefinition(name = "Girl Scouts Client Secret", description = "Girl Scouts Client Secret", type = AttributeType.STRING) String clientSecret() default "b9eC9e82c7754768B6DCD3768093A3E4";

    }

}
