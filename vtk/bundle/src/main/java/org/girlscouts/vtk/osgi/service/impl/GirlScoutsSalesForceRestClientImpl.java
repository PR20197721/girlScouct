package org.girlscouts.vtk.osgi.service.impl;

import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.osgi.conf.GirlScoutsSalesForceRestClientConfig;
import org.girlscouts.vtk.osgi.service.GirlScoutsSalesForceRestClient;
import org.girlscouts.vtk.rest.entity.salesforce.*;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Component(service = {GirlScoutsSalesForceRestClient.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsSalesForceRestClientImpl")
@Designate(ocd = GirlScoutsSalesForceRestClientConfig.class)
public class GirlScoutsSalesForceRestClientImpl extends BasicGirlScoutsService implements GirlScoutsSalesForceRestClient {
    private static Logger log = LoggerFactory.getLogger(GirlScoutsSalesForceRestClientImpl.class);
    private String sfRestAPI;
    private String sfUserInfoUrl;
    private String sfTroopInfoUrl;
    private String sfTroopLeaderInfoUrl;
    private String sfContactsInfoUrl;
    private String sfJWTURL;
    private String gsCertificate;
    private String clientId;

    @Activate
    private void activate(ComponentContext context) {
        this.context = context;
        this.sfRestAPI = getConfig("sfRestAPI");
        this.sfUserInfoUrl = this.sfRestAPI + getConfig("sfUserInfoURI");
        this.sfTroopInfoUrl = this.sfRestAPI + getConfig("sfTroopInfoURI");
        this.sfTroopLeaderInfoUrl = this.sfRestAPI + getConfig("sfTroopLeaderInfoURI");
        this.sfContactsInfoUrl = this.sfRestAPI + getConfig("sfContactsInfoURI");
        this.sfJWTURL = this.sfRestAPI + getConfig("sfJWTURI");
        this.gsCertificate = getConfig("gsCertificate");
        this.clientId = getConfig("clientId");
        log.info("Girl Scouts VTK SalesForce Rest Client Activated.");
    }

    @Override
    public UserInfoResponseEntity getUserInfo(ApiConfig apiConfig) {
        return getUserInfoById(apiConfig, apiConfig.getUserId());
    }

    @Override
    public UserInfoResponseEntity getUserInfoById(ApiConfig apiConfig, String userId) {
        UserInfoResponseEntity user = null;
        String url = this.sfUserInfoUrl + "?USER_ID=" + userId;
        String token = apiConfig.getAccessToken();
        try {
            String json = doGet(url, token);
            log.debug(json);
            user = new Gson().fromJson(json, UserInfoResponseEntity.class);
        } catch (Exception e) {
            log.error("Error occurred getting user info by id from salesforce ", e);
        }
        return user;
    }

    @Override
    public TroopInfoResponseEntity getTroopInfoByUserId(ApiConfig apiConfig, String userId) {
        TroopInfoResponseEntity troopInfoResponseEntity = null;
        String url = this.sfTroopInfoUrl + "?userId=" + userId;
        String token = apiConfig.getAccessToken();
        try {
            String json = doGet(url, token);
            json = "{\"troops\":" + json + "}";//Fixing json from salesforce which is coming back in invalid format.
            log.debug(json);
            troopInfoResponseEntity = new Gson().fromJson(json, TroopInfoResponseEntity.class);
        } catch (Exception e) {
            log.error("Error occurred getting troop info by user id from salesforce ", e);
        }
        return troopInfoResponseEntity;
    }

    @Override
    public ContactsInfoResponseEntity getContactsByTroopId(ApiConfig apiConfig, String sfTroopId) {
        ContactsInfoResponseEntity contactsInfoResponseEntity = null;
        String url = this.sfContactsInfoUrl + "?troopId=" + sfTroopId;
        String token = apiConfig.getAccessToken();
        try {
            String json = doGet(url, token);
            log.debug(json);
            contactsInfoResponseEntity = new Gson().fromJson(json, ContactsInfoResponseEntity.class);
        } catch (Exception e) {
            log.error("Error occurred getting contacts info by troop id from salesforce ", e);
        }
        return contactsInfoResponseEntity;
    }

    @Override
    public TroopLeadersInfoResponseEntity getTroopLeaderInfoByTroopId(ApiConfig apiConfig, String sfTroopId) {
        TroopLeadersInfoResponseEntity troopLeadersInfoResponseEntity = null;
        String url = this.sfTroopLeaderInfoUrl + "?Troop_ID=" + sfTroopId;
        String token = apiConfig.getAccessToken();
        try {
            String json = doGet(url, token);
            json = "{\"troopLeaders\":" + json + "}";//Fixing json from salesforce which is coming back in invalid format.
            log.debug(json);
            troopLeadersInfoResponseEntity = new Gson().fromJson(json, TroopLeadersInfoResponseEntity.class);
        } catch (Exception e) {
            log.error("Error occurred getting troop leader info by troop id from salesforce ", e);
        }
        return troopLeadersInfoResponseEntity;
    }

    @Override
    public JWTAuthEntity getJWTAuth(String accessToken) {
        byte[] data = Base64.decodeBase64(gsCertificate);
        ByteArrayInputStream is = new ByteArrayInputStream(data);
        String email = accessToken.substring(accessToken.indexOf("@") + 1);
        JWTAuthEntity jwtAuthEntity = null;
        try {
            String token = generateJWTAuthToken(is, email);
            String json = doJWTAuth(token);
            log.debug(json);
            jwtAuthEntity = new Gson().fromJson(json, JWTAuthEntity.class);
        } catch (Exception e) {
            log.error("Error occurred getting contacts info by troop id from salesforce ", e);
        }
        return jwtAuthEntity;
    }

    private String doGet(String url, String token) {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClientBuilder.create().build();
            HttpGet getRequest = new HttpGet(url);
            getRequest.addHeader("accept", "application/json");
            getRequest.addHeader("Authorization", "OAuth " + token);
            log.debug("curl -X GET '" + url + "' -H 'Accept: application/json' -H 'Authorization: OAuth " + token + "'");
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

    private String doJWTAuth(String token) {
        CloseableHttpClient httpClient = null;
        try {
            CloseableHttpClient client = HttpClientBuilder.create().setSslcontext(SSLContexts.custom().useProtocol("TLSv1.2").build()).build();
            HttpPost postRequest = new HttpPost(this.sfJWTURL);
            postRequest.addHeader("accept", "application/json");
            List<BasicNameValuePair> parametersBody = new ArrayList<BasicNameValuePair>();
            parametersBody.add(new BasicNameValuePair("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer"));
            parametersBody.add(new BasicNameValuePair("assertion", token));
            postRequest.setEntity(new UrlEncodedFormEntity(parametersBody, StandardCharsets.UTF_8));
            HttpResponse response = client.execute(postRequest);
            return getJsonFromResponse(response);
        } catch (Exception e) {
            log.error("Exception is thrown making a JWTAuth call:", e);
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                log.error("Exception is thrown closing http client to " + this.sfJWTURL, e);
            }
        }
        return null;
    }

    private String getJsonFromResponse(HttpResponse response) throws IOException {
        if (response.getStatusLine().getStatusCode() != 200) {
            log.error("Salesforce API status code: " + response.getStatusLine().getStatusCode() + " : " + response);
            throw new RuntimeException("Salesforce API : HTTP error code : " + response.getStatusLine().getStatusCode());
        }
        HttpEntity entity = response.getEntity();
        entity.getContent();
        String json = EntityUtils.toString(entity);
        log.debug("SalesForce Response: " + response);
        EntityUtils.consume(entity);
        return json;
    }

    private String generateJWTAuthToken(InputStream is, String email) {
        StringBuffer token = new StringBuffer();
        try {
            String header = "{\"alg\":\"RS256\"}";
            String claimTemplate = "'{'\"iss\": \"{0}\", \"prn\": \"{1}\", \"aud\": \"{2}\", \"exp\": \"{3}\"'}'";
            //Encode the JWT Header and add it to our string to sign
            token.append(Base64.encodeBase64URLSafeString(header.getBytes(StandardCharsets.UTF_8)));
            //Separate with a period
            token.append(".");
            //Create the JWT Claims Object
            String[] claimArray = new String[4];
            claimArray[0] = this.clientId;//"3MVG9ahGHqp.k2_yeQBSRKEBsGHrY.Gjxv0vUjeW_2Dy6AFNe_8TanHRxUQ7BZsForgy38OuJsInpyLsVtcEH";
            claimArray[1] = email; //"ana.pope@gsfuture.org.gsuat";
            claimArray[2] = this.sfRestAPI; //"https://gsuat-gsmembers.cs17.force.com/members";//http://localhost:4503/content/girlscouts-vtk/controllers/auth.sfauth.html";	 // community user
            Calendar cal = Calendar.getInstance();
            cal.add(java.util.Calendar.MINUTE, 30);
            claimArray[3] = String.valueOf(cal.getTimeInMillis());
            MessageFormat claims;
            claims = new MessageFormat(claimTemplate);
            String payload = claims.format(claimArray);
            //Add the encoded claims object
            token.append(Base64.encodeBase64URLSafeString(payload.getBytes(StandardCharsets.UTF_8)));
            //Load the private key from a keystore
            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(is, "icruise123".toCharArray());
            PrivateKey privateKey = (PrivateKey) keystore.getKey("mycert", "icruise123".toCharArray());
            //Sign the JWT Header + "." + JWT Claims Object
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(token.toString().getBytes(StandardCharsets.UTF_8));
            String signedPayload = Base64.encodeBase64URLSafeString(signature.sign());
            //Separate with a period
            token.append(".");
            //Add the encoded signature
            token.append(signedPayload);
        } catch (Exception e) {
            log.error("Error occurred generating token for JWTAuth call to SalesForce: ", e);
        }
        return token.toString();
    }
}
