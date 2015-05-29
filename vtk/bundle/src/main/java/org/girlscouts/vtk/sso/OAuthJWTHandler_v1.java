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
public void doIt(){

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
		      claimArray[0] = "3MVG9GiqKapCZBwHKlrZlQzDydYIMVImlAqkN6xOQASZjR1gpCDTFSdBAHismLIq003WzHDWbw_bne9NY6WGE";
		      claimArray[1] = "appled.strudel@gmail.com";
		      claimArray[2] = "http://localhost:4503/content/girlscouts-vtk/controllers/auth.sfauth.html";	 // community user
		      claimArray[3] = Long.toString( ( System.currentTimeMillis()/1000 ) + 300);
		      MessageFormat claims;
		      claims = new MessageFormat(claimTemplate);
		      String payload = claims.format(claimArray);
		      System.out.println("Paylnad "+payload);
		      //Add the encoded claims object
		      token.append(Base64.encodeBase64URLSafeString(payload.getBytes("UTF-8")));

		      //Load the private key from a keystore
		      KeyStore keystore = KeyStore.getInstance("JKS");
		      keystore.load(new FileInputStream("/Users/akobovich/keystore.jks"), "icruise123".toCharArray());
		      PrivateKey privateKey = (PrivateKey) keystore.getKey("server", "icruise123".toCharArray());
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
		      HttpPost oauthPost = new HttpPost("https://gsuat-gsmembers.cs11.force.com/members/services/oauth2/token"); // community user
		      List<BasicNameValuePair> parametersBody = new ArrayList<BasicNameValuePair>();
		      parametersBody.add(new BasicNameValuePair("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer"));
		      parametersBody.add(new BasicNameValuePair("assertion", token.toString()));
		      System.out.println("Parameters "+parametersBody.toString());
		      oauthPost.setEntity(new UrlEncodedFormEntity(parametersBody, HTTP.UTF_8));		
		      System.out.println("Request "+oauthPost);
		      
		      HttpResponse response = client.execute(oauthPost);
		      System.out.println("Response "+response.toString());
		      int code = response.getStatusLine().getStatusCode();
		      JSONParser parser=new JSONParser();
		      JSONObject jobj = (JSONObject) parser.parse(EntityUtils.toString(response.getEntity()));
		      System.out.println("Jarray is "+jobj);
		      System.out.println("set is "+jobj.keySet());
		   
		     String accessToken = (String)jobj.get("access_token");
		     System.out.println("Access token is "+accessToken);
		      System.out.println("successful....");		
		      
		   // Get user info using JWT access token 
		      String instanceURL = (String)jobj.get("instance_url");
		      List<BasicNameValuePair> qsList = new ArrayList<BasicNameValuePair>();
		        qsList.add(new BasicNameValuePair("q", "select name, title from contact limit 10"));		      
		      String queryString = URLEncodedUtils.format(qsList, HTTP.UTF_8);
		      String url = instanceURL+"/services/data/v29.0/query/?"+queryString;
		      System.out.println("Request URL is "+url);
		      HttpGet userInfoRequest = new HttpGet(url);
		      userInfoRequest.setHeader("Authorization", "Bearer "+accessToken);
		        HttpResponse userInfoResponse = client.execute(userInfoRequest);		      
			      int ccode = userInfoResponse.getStatusLine().getStatusCode();
		
			      System.out.println("success" + ccode);
			      BufferedReader br = new BufferedReader(new InputStreamReader(userInfoResponse.getEntity().getContent()));
			      String readline;
			      while ((readline=br.readLine())!=null)
			    	  System.out.println(readline);
			      br.close();
			  
				  
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		  }
}

