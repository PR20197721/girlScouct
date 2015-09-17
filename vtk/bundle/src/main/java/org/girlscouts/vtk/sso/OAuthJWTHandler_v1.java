package org.girlscouts.vtk.sso;

import org.apache.commons.codec.binary.Base64;
import javax.xml.soap.SOAPException;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.json.simple.parser.*;
import org.json.simple.*;

import java.io.*; 
import java.security.*; 
import java.text.MessageFormat;  
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OAuthJWTHandler_v1 {
	
	 @SuppressWarnings("deprecation")
	public static void main(String[] args) {}
public ApiConfig doIt(java.io.InputStream is, String email, String access_token, String communityUrl ){
	//Security.addProvider(new com.sun.crypto.provider.SunJCE());
	ApiConfig config=null;
	 
		    String header = "{\"alg\":\"RS256\"}";
		    String claimTemplate = "'{'\"iss\": \"{0}\", \"prn\": \"{1}\", \"aud\": \"{2}\", \"exp\": \"{3}\"'}'";

		    try {
		      StringBuffer token = new StringBuffer();
		      //Encode the JWT Header and add it to our string to sign
		      token.append(Base64.encodeBase64URLSafeString(header.getBytes("UTF-8")));

		      //Separate with a period
		      token.append(".");
		      
System.err.println("access_token "+ access_token);
		      //Create the JWT Claims Object
		      String[] claimArray = new String[4];
		      claimArray[0] = access_token;//"3MVG9ahGHqp.k2_yeQBSRKEBsGHrY.Gjxv0vUjeW_2Dy6AFNe_8TanHRxUQ7BZsForgy38OuJsInpyLsVtcEH";

		      claimArray[1] = email; //"ana.pope@gsfuture.org.gsuat";
		      claimArray[2] = communityUrl; //"https://gsuat-gsmembers.cs17.force.com/members";//http://localhost:4503/content/girlscouts-vtk/controllers/auth.sfauth.html";	 // community user
		      claimArray[3] = Long.toString( ( System.currentTimeMillis()/1000 ) + 300);
		      
//System.err.println("testTata now: "+ System.currentTimeMillis() );	
//System.err.println("testTata sending: "+ ( ( System.currentTimeMillis()/1000 ) + 300) );
java.util.Calendar alex = java.util.Calendar.getInstance();
alex.add(java.util.Calendar.MINUTE, 30);
//System.err.println( "testTata 30m: "+ alex.getTimeInMillis() );
claimArray[3] =alex.getTimeInMillis()+"";

		      MessageFormat claims;
		      claims = new MessageFormat(claimTemplate);
		      String payload = claims.format(claimArray);
		      //System.out.println("test11111: "+payload);
		      //Add the encoded claims object
		      token.append(Base64.encodeBase64URLSafeString(payload.getBytes("UTF-8")));

		      //Load the private key from a keystore
		      KeyStore keystore = KeyStore.getInstance("JKS");

		      //GOOD keystore.load(new FileInputStream("/Users/akobovich/Desktop/mycert.jks"), "icruise123".toCharArray());
		      keystore.load(is, "icruise123".toCharArray());
			    
		       
		      PrivateKey privateKey = (PrivateKey) keystore.getKey("mycert", "icruise123".toCharArray());
		      //System.err.println("tata: " + (privateKey==null));  
		      //Sign the JWT Header + "." + JWT Claims Object
		      Signature signature = Signature.getInstance("SHA256withRSA");
		      signature.initSign(privateKey);
		      signature.update(token.toString().getBytes("UTF-8"));
		      String signedPayload = Base64.encodeBase64URLSafeString(signature.sign());

		      //Separate with a period
		      token.append(".");

		      //Add the encoded signature
		      token.append(signedPayload);
		     
		      System.out.println(token.toString());
		      DefaultHttpClient client = new DefaultHttpClient();
		      HttpParams params = client.getParams();
		      HttpClientParams.setCookiePolicy(params, CookiePolicy.RFC_2109);
		      params.setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 30000);
		      HttpPost oauthPost = new HttpPost(communityUrl+"/services/oauth2/token");//"https://gsuat-gsmembers.cs17.force.com/members/services/oauth2/token"); // community user
		      List<BasicNameValuePair> parametersBody = new ArrayList<BasicNameValuePair>();
		      parametersBody.add(new BasicNameValuePair("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer"));
		      parametersBody.add(new BasicNameValuePair("assertion", token.toString()));
		      System.out.println("Parameters "+parametersBody.toString());
		      oauthPost.setEntity(new UrlEncodedFormEntity(parametersBody, HTTP.UTF_8));		
		      System.out.println("Request "+oauthPost);
		      
		      HttpResponse response = client.execute(oauthPost);
		      System.out.println("test Response "+response.toString());
		      int code = response.getStatusLine().getStatusCode();
		      JSONParser parser=new JSONParser();
		      JSONObject jobj = (JSONObject) parser.parse(EntityUtils.toString(response.getEntity()));
		      System.out.println("Jarray is "+jobj);
		      System.out.println("set is "+jobj.keySet());
		   
		      String accessToken = (String)jobj.get("access_token");
		      System.out.println("Access token is "+accessToken);
		      System.out.println("successful....");		
		      
		      
		      
		      
		      
		        config = new ApiConfig();
				config.setAccessToken((String)jobj.get("access_token"));
				config.setInstanceUrl((String)jobj.get("instance_url"));
				config.setWebServicesUrl((String)jobj.get("sfdc_community_url"));
				String refreshTokenStr = null;
				try {
					refreshTokenStr = (String)jobj.get("refresh_token");
				} catch (Exception npe) {
					// skip refresh token
					
				}
				/*
				String id = (String)jobj.get("id");
				config.setId(id);
				config.setUserId(id.substring(id.lastIndexOf("/") + 1));
				if (refreshTokenStr != null) {
					config.setRefreshToken(refreshTokenStr);
				}
				//config.setCallbackUrl(callbackUrl);
				//config.setClientId(clientId);
				//config.setClientSecret(clientSecret);
				//config.setOAuthUrl(OAuthUrl);
		      */
		      
				  
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		    return   config;
}

}

