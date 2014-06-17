package org.girlscouts.vtk.auth.dao;

import java.io.InputStreamReader;
import java.net.URLDecoder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.auth.models.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO: Need thread pool here
public class SalesforceDAO {
    private final Logger log = LoggerFactory.getLogger(SalesforceDAO.class);

    String OAuthUrl;
    String clientId;
    String clientSecret;
    String callbackUrl;

    public User getUser(ApiConfig config) {
        User user = new User();

        HttpClient httpclient = new HttpClient();
        GetMethod get = new GetMethod(config.getInstanceUrl() + "/services/data/v20.0/query");

        get.setRequestHeader("Authorization", "OAuth " + config.getAccessToken());

        // set the SOQL as a query param
        NameValuePair[] params = new NameValuePair[1];

        /*
        params[0] = new NameValuePair("q", "SELECT ID,name,email, phone, mobilephone, homephone,otherPhone, AssistantPhone from User where id='" 
                    + config.getUserId() + "' limit 1");
        */
        params[0] = new NameValuePair("q", "SELECT ID,name,email, phone, mobilephone, ContactId  from User where id='" 
                + config.getUserId() + "' limit 1");
        
        
        get.setQueryString(params);

        try {
            httpclient.executeMethod(get);
            log.debug(get.getStatusCode() + " : " + get.getResponseBodyAsString());
    
            if (get.getStatusCode() == HttpStatus.SC_OK) {
                try {
                    JSONObject response = new JSONObject(
                                          new JSONTokener(
                                          new InputStreamReader(
                                          get.getResponseBodyAsStream())));
                    log.debug("Query response: " + response.toString(2));
    
                    JSONArray results = response.getJSONArray("records");
    
                    // Always use the last record
                    int current = results.length() - 1;
                    try{
                    user.setName(results.getJSONObject(current).getString("Name"));
                  //  user.setEmail(results.getJSONObject(current).getString("Email"));
                    user.setContactId(results.getJSONObject(current).getString("ContactId"));
                    //user.setPhone(results.getJSONObject(current).getString("Phone"));
                    //user.setHomePhone(results.getJSONObject(current).getString("HomePhone"));
                   // user.setMobilePhone(results.getJSONObject(current).getString("MobilePhone"));
                   // user.setMobilePhone(results.getJSONObject(current).getString("AssistantPhone"));
                    }catch(Exception e){e.printStackTrace();}
                    
                    return user;
                } catch (JSONException e) {
                    log.error("JSON Parse exception: " + e.toString());
                }
            } else {
                log.error("Return status not OK: " + get.getStatusCode() + " " + get.getResponseBodyAsString());
            }
        } catch (Exception e) {
            log.error("Error executing HTTP GET when getting the user: " + e.toString());
        } finally {
            get.releaseConnection();
        }
        return null;
    }

    public ApiConfig doAuth(String code) {
        try {
            code = URLDecoder.decode(code, "UTF-8");
        } catch (Exception e) {
            log.error("Error decoding the code. Left it as is.");
        }

        HttpClient httpclient = new HttpClient();

        String tokenUrl = OAuthUrl + "/services/oauth2/token";

        PostMethod post = new PostMethod(tokenUrl);
        post.addParameter("code", code);
        post.addParameter("grant_type", "authorization_code");
        post.addParameter("client_id", clientId);
        post.addParameter("client_secret", clientSecret);
        post.addParameter("redirect_uri", callbackUrl);

        try {
            httpclient.executeMethod(post);
            if (post.getStatusCode() == HttpStatus.SC_OK) {
                try {
                    JSONObject authResponse = new JSONObject(new JSONTokener(
                            new InputStreamReader(post.getResponseBodyAsStream())));
    
                    ApiConfig config = new ApiConfig();
                    config.setAccessToken(authResponse.getString("access_token"));
                    config.setInstanceUrl(authResponse.getString("instance_url"));
                    // TODO: seems not used now
                    //tokenType = authResponse.getString("token_type");
                    String id = authResponse.getString("id");
                    config.setId(id);
                    config.setUserId(id.substring(id.lastIndexOf("/") + 1));
                    //config.setRefreshToken(authResponse.getString("refresh_token"));
                    return config;
                } catch (JSONException e) {
                    log.error("JSON Parse exception: " + e.toString());
                }
            } else {
                log.error("Return status not OK: " + post.getStatusCode() + " " + post.getResponseBodyAsString());
            }
        } catch (Exception e) {
            log.error("Error executing HTTP POST when authenticating: " + e.toString());
        } finally {
            post.releaseConnection();
        }
        return null;
    }
    
// TODO: Cleanup later
//    public java.util.Map<String, String> getContactEmailList(ApiConfig apiConfig) {
//
//        /*
//         * apiConfig= new ApiConfig(); apiConfig.setAccessToken(
//         * "00DZ000000MhaMd!ARwAQOmZ2MpksgBDo_LPFW7m36oHJ5DtME1OulwhgO5xK1.bGgo9qr7JFGCe7Im56V.oYtmvOqF1ziS_iMzkcvoOMMv9f9ND"
//         * ); apiConfig.setInstanceUrl("https://cs11.salesforce.com");
//         */
//
//        java.util.Map emails = new java.util.TreeMap();
//        try {
//
//            HttpClient httpclient = new HttpClient();
//            GetMethod get = new GetMethod(apiConfig.getInstanceUrl()
//                    + "/services/data/v20.0/query");
//
//            // set the token in the header
//            get.setRequestHeader("Authorization",
//                    "OAuth " + apiConfig.getAccessToken());
//
//            // set the SOQL as a query param
//            NameValuePair[] params = new NameValuePair[1];
//
//            params[0] = new NameValuePair("q", "SELECT id,Email from Contact");
//            get.setQueryString(params);
//
//            try {
//                httpclient.executeMethod(get);
//                if (get.getStatusCode() == HttpStatus.SC_OK) {
//                    // Now lets use the standard java json classes to work with
//                    // the
//                    // results
//                    try {
//                        JSONObject response = new JSONObject(new JSONTokener(
//                                new InputStreamReader(
//                                        get.getResponseBodyAsStream())));
//                        System.out.println("Query response: "
//                                + response.toString(2));
//
//                        JSONArray results = response.getJSONArray("records");
//
//                        for (int i = 0; i < results.length(); i++) {
//
//                            // Contact contact = new Contact();
//                            // contact.setId(results.getJSONObject(i).getString("Id"));
//                            // contact.setEmail(results.getJSONObject(i).getString("Email"));
//
//                            // System.err.println(">>"+(results.getJSONObject(i).get("Email")
//                            // ==null));
//                            if (!results.getJSONObject(i).get("Email")
//                                    .toString().equals("null")) {
//
//                                emails.put(
//                                        results.getJSONObject(i)
//                                                .getString("Id"),
//                                        results.getJSONObject(i).getString(
//                                                "Email"));
//                                // contact.setEmail(results.getJSONObject(i).getString("Email"));
//                                // System.err.println("*** "+ contact.getEmail()
//                                // );
//                            }
//
//                            /*
//                             * System.out.println(results.getJSONObject(i).getString
//                             * ("Id") + ", " +
//                             * results.getJSONObject(i).getString("Name") +
//                             * "\n");
//                             */
//                        }
//                        System.out.println("\n");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//
//                    }
//                }
//            } finally {
//                get.releaseConnection();
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return emails;
//    }

    
    
    
  
}