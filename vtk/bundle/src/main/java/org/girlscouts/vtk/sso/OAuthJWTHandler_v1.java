package org.girlscouts.vtk.sso;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class OAuthJWTHandler_v1 {

    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
    }

    public ApiConfig getOAuthConfigs(java.io.InputStream is, String email, String access_token, String communityUrl) {
        ApiConfig config = null;
        String header = "{\"alg\":\"RS256\"}";
        String claimTemplate = "'{'\"iss\": \"{0}\", \"prn\": \"{1}\", \"aud\": \"{2}\", \"exp\": \"{3}\"'}'";
        try {
            StringBuffer token = new StringBuffer();
            //Encode the JWT Header and add it to our string to sign
            token.append(Base64.encodeBase64URLSafeString(header.getBytes("UTF-8")));
            //Separate with a period
            token.append(".");
            //Create the JWT Claims Object
            String[] claimArray = new String[4];
            claimArray[0] = access_token;//"3MVG9ahGHqp.k2_yeQBSRKEBsGHrY.Gjxv0vUjeW_2Dy6AFNe_8TanHRxUQ7BZsForgy38OuJsInpyLsVtcEH";
            claimArray[1] = email; //"ana.pope@gsfuture.org.gsuat";
            claimArray[2] = communityUrl; //"https://gsuat-gsmembers.cs17.force.com/members";//http://localhost:4503/content/girlscouts-vtk/controllers/auth.sfauth.html";	 // community user
            claimArray[3] = Long.toString((System.currentTimeMillis() / 1000) + 300);
            java.util.Calendar alex = java.util.Calendar.getInstance();
            alex.add(java.util.Calendar.MINUTE, 30);
            claimArray[3] = alex.getTimeInMillis() + "";
            MessageFormat claims;
            claims = new MessageFormat(claimTemplate);
            String payload = claims.format(claimArray);
            //Add the encoded claims object
            token.append(Base64.encodeBase64URLSafeString(payload.getBytes("UTF-8")));
            //Load the private key from a keystore
            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(is, "icruise123".toCharArray());
            PrivateKey privateKey = (PrivateKey) keystore.getKey("mycert", "icruise123".toCharArray());
            //Sign the JWT Header + "." + JWT Claims Object
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(token.toString().getBytes("UTF-8"));
            String signedPayload = Base64.encodeBase64URLSafeString(signature.sign());
            //Separate with a period
            token.append(".");
            //Add the encoded signature
            token.append(signedPayload);


            CloseableHttpClient client = HttpClientBuilder.create().setSslcontext(SSLContexts.custom().useProtocol("TLSv1.2").build()).build();
            HttpPost oauthPost = new HttpPost(communityUrl + "/services/oauth2/token");//"https://gsuat-gsmembers.cs17.force.com/members/services/oauth2/token"); // community user
            List<BasicNameValuePair> parametersBody = new ArrayList<BasicNameValuePair>();
            parametersBody.add(new BasicNameValuePair("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer"));
            parametersBody.add(new BasicNameValuePair("assertion", token.toString()));
            oauthPost.setEntity(new UrlEncodedFormEntity(parametersBody, HTTP.UTF_8));

            HttpResponse response = client.execute(oauthPost);
            int code = response.getStatusLine().getStatusCode();
            JSONParser parser = new JSONParser();
            JSONObject jobj = (JSONObject) parser.parse(EntityUtils.toString(response.getEntity()));
            config = new ApiConfig();
            String accessToken = (String) jobj.get("access_token");
            config.setAccessToken(accessToken);
            config.setInstanceUrl((String) jobj.get("instance_url"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return config;
    }

}

