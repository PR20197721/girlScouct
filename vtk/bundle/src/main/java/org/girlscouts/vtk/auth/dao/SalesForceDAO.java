package org.girlscouts.vtk.auth.dao;

//import org.apache.commons.httpclient.methods.PostMethod;
//import org.apache.http.client.HttpClient;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.auth.models.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class SalesForceDAO {

  //  https://login.salesforce.com/services/oauth2/authorize?response_type=code&client_id=3MVG9A2kN3Bn17hsP.Ui7f1jgy.inaXzsZ4VKF6DcS.LKlCZSdhHBeH.992uouy19lQAyvfZC98jgGtM73lIu&redirect_uri=http://localhost:4502/cf#/content/testMy.html 
  //private String code="";
  private String clientId="3MVG9A2kN3Bn17hsP.Ui7f1jgy.inaXzsZ4VKF6DcS.LKlCZSdhHBeH.992uouy19lQAyvfZC98jgGtM73lIu";
  private String clientSecret="8752122745172672197";
  
  // org private String redirectUri= "http://localhost:4502/cf#/content/testMy.html";
  //private String redirectUri= "http://localhost:4502/content/testMy.html";
  private String redirectUri="http://localhost:4502/content/testLogin2/login5.html";
  
  String code="aPrxMZkm7lCkgfSJMlB6uxm.vu1.jTVRhlwG97lMQiRW1qqe_99CUDoLyaa7VtEamBomHFVHEg==";
  //String authUrl= null;
  //String accessToken=null;
  //String instanceUrl=null;
  
  
  private void doInit(){
      String environment="https://login.salesforce.com";
      try {
          
         String  authUrl = environment

                  + "/services/oauth2/authorize?response_type=code&client_id="

                  + clientId + "&redirect_uri="

                  + URLEncoder.encode(redirectUri, "UTF-8");


          HttpClient httpclient = new HttpClient();
          PostMethod post = new PostMethod(authUrl);
          httpclient.executeMethod(post);
          
         
          System.out.println("RESP auth:: "+post.getResponseBodyAsString());
      } catch (Exception e) {

          e.printStackTrace();

      }

  }
  
  
  public User getUser(ApiConfig config){
      
      User user = new User();
          /*
          System.out.print("userInfo");
          config = new ApiConfig();
          config.setId("https://login.salesforce.com/id/00Di0000000kwPvEAI/005i0000003EFOXAA4");
          config.setTokenType("Bearer");
          config.setAccessToken("00Di0000000kwPv!AREAQGUOkPfEKmVeKq.tp6Te4C.DC9PwlMeWQEt30_bjbv_uPrcz_iNGqvasLIxRrNIhJr_WPsqRfoa.FMbW1aImNZZpi.oM");
          
      config= new ApiConfig();
      config.setUserId("005i0000003EFOXAA4");
      config.setAccessToken("00Di0000000kwPv!AREAQNYQBwymLwVKlChAjMC_TlIB4OtrhYFmfNqQctC5sJl27kqi1PCzg5Q0kZailgQeGG5i9t8yIgxuFlgwa8HJiEAlxoUf");
      */
      
      
          java.util.List accounts = new java.util.ArrayList();
          try{
              //-String instanceUrl="https://na15.salesforce.com";
          HttpClient httpclient = new HttpClient();
          GetMethod get = new GetMethod(config.getInstanceUrl()
                  + "/services/data/v20.0/query");

          // set the token in the header
          get.setRequestHeader("Authorization", "OAuth " + config.getAccessToken());

          // set the SOQL as a query param
          NameValuePair[] params = new NameValuePair[1];

          params[0] = new NameValuePair("q",
                  "SELECT ID,name,email from User where id='"+config.getUserId()+ "' limit 1");
          get.setQueryString(params);

          try {
              httpclient.executeMethod(get);
              
              System.err.println(get.getStatusCode() +" : "+get.getResponseBodyAsString());
              if (get.getStatusCode() == HttpStatus.SC_OK) {
                  // Now lets use the standard java json classes to work with the
                  // results
                  try {
                      JSONObject response = new JSONObject(
                              new JSONTokener(new InputStreamReader(
                                      get.getResponseBodyAsStream())));
                      System.out.println("Query response: "
                              + response.toString(2));
  //System.err.println("display_name="+response.getString("display_Name"));
                      //user.setDisplayName(response.getString("display_Name"));
                      
                      JSONArray results = response.getJSONArray("records");

                      for (int i = 0; i < results.length(); i++) {
              //System.err.println(results.getJSONObject(i).getString("Name"));           
                          user.setName(results.getJSONObject(i).getString("Name"));
                          user.setEmail(results.getJSONObject(i).getString("Email"));
                      }   
                      
                  } catch (JSONException e) {
                      e.printStackTrace();
                      
                  }
              }
          } finally {
              get.releaseConnection();
          }
          }catch(Exception ex){ex.printStackTrace();}
      
      
      return user;
      
      
      }

  
  //public String doAuth(String code){
  public ApiConfig doAuth(String code){
      
      ApiConfig config= new ApiConfig();
      
      try{
            
             code= java.net.URLDecoder.decode(code, "UTF-8");
          }catch(Exception e){e.printStackTrace();}
      
      
      String accessToken=null;
      String environment="https://test.salesforce.com";
      HttpClient httpclient = new HttpClient();

      
       String tokenUrl = environment + "/services/oauth2/token";
       
      PostMethod post = new PostMethod(tokenUrl);
      post.addParameter("code", code);
      post.addParameter("grant_type", "authorization_code");
      post.addParameter("client_id", clientId);
      post.addParameter("client_secret", clientSecret);
      post.addParameter("redirect_uri", redirectUri);

      try {
          httpclient.executeMethod(post);
          System.out.println("RESP:: "+post.getResponseBodyAsString());
          try {
              JSONObject authResponse = new JSONObject(
                      new JSONTokener(new InputStreamReader(
                              post.getResponseBodyAsStream())));
              System.out.println("Auth response: "
                      + authResponse.toString(2));

              accessToken = authResponse.getString("access_token");
              String instanceUrl = authResponse.getString("instance_url");
              
              config.setAccessToken(accessToken);
              config.setInstanceUrl(instanceUrl);
              config.setTokenType(authResponse.getString("token_type"));
              config.setId(authResponse.getString("id"));
              config.setUserId(config.getId().substring(config.getId().lastIndexOf("/")+1));
              
              System.out.println("Got access token: " + accessToken);
          } catch (JSONException e) {
              e.printStackTrace();
              
          }
      }catch (Exception ex){ex.printStackTrace();
      } finally {
          post.releaseConnection();
      }
      
      return config;
  }
  
  
  /*
  public String doInit(ApiConfig apiConfig){
      String environment="https://login.salesforce.com";
      String resp="";
      try {
          
         String  authUrl = environment

                  + "/services/oauth2/authorize?response_type=code&client_id="

                  + clientId + "&redirect_uri="

                  + URLEncoder.encode(redirectUri, "UTF-8");


          HttpClient httpclient = new HttpClient();
          PostMethod post = new PostMethod(authUrl);
          httpclient.executeMethod(post);
          
         resp=post.getResponseBodyAsString();
          System.out.println("RESP auth:: "+post.getResponseBodyAsString());
      } catch (Exception e) {

          e.printStackTrace();

      }
return resp;
  }
  */
  
  
  
  
  
  
  
  
  
  /*
  private String createAccount1(String name, String instanceUrl,
          String accessToken, PrintWriter writer) throws ServletException,
          IOException {
      String accountId = null;

      HttpClient httpclient = new HttpClient();

      JSONObject account = new JSONObject();

      try {
          account.put("Name", name);
      } catch (JSONException e) {
          e.printStackTrace();
          throw new ServletException(e);
      }

      PostMethod post = new PostMethod(instanceUrl
              + "/services/data/v20.0/sobjects/Account/");

      post.setRequestHeader("Authorization", "OAuth " + accessToken);
      post.setRequestEntity(new StringRequestEntity(account.toString(),
              "application/json", null));

      try {
          httpclient.executeMethod(post);

          writer.write("HTTP status " + post.getStatusCode()
                  + " creating account\n\n");

          if (post.getStatusCode() == HttpStatus.SC_CREATED) {
              try {
                  JSONObject response = new JSONObject(new JSONTokener(
                          new InputStreamReader(
                                  post.getResponseBodyAsStream())));
                  System.out.println("Create response: "
                          + response.toString(2));

                  if (response.getBoolean("success")) {
                      accountId = response.getString("id");
                      writer.write("New record id " + accountId + "\n\n");
                  }
              } catch (JSONException e) {
                  e.printStackTrace();
                  //throw new ServletException(e);
              }
          }
      } finally {
          post.releaseConnection();
      }

      return accountId;
  }
*/
  
  
  
  public void createTest(String login){
      
       ApiConfig apiConfig = new ApiConfig();
       apiConfig.setAccessToken(login);
      
       
       try{
           
           
           String accountId = null;
                  HttpClient httpclient = new HttpClient();

                  JSONObject contact = new JSONObject();

                  try {
                      contact.put("LastName", "alexContaCT");
                      
                      //System.err.println("name:"+ name);
                  } catch (JSONException e) {
                      e.printStackTrace();
                      
                  }

                  //curl  -H 'Authorization: Bearer 00Di0000000kwPv!AREAQDuD22bGVAFqzsB_oYUD1PxDhkkVttuEYq1bi7QENY25jpOd9.OJT4cSoA65wHeYSSBUBnaGcEvMhHU1om9ewfXBhXE8' https://na15.salesforce.com/services/data/v28.0/sobjects/Account/quickActions/CreateContact/describe/
                      
                  try {
                      
                      System.err.println(contact.toString());
                      PostMethod post = new PostMethod(
                          "https://na15.salesforce.com/services/data/v28.0/sobjects/Account/quickActions/CreateContact");

                  post.setRequestHeader("Authorization", "Bearer " + apiConfig.getAccessToken());
                  post.setRequestEntity(new StringRequestEntity(contact.toString(),
                          "application/json", null));

                  
                      httpclient.executeMethod(post);
                      
                      System.err.println("creat.. contact "+ post.getStatusCode()+" :" + post.getResponseBodyAsString());
           
           
                  }catch(Exception e){e.printStackTrace();}
       }catch(Exception e){e.printStackTrace();} 
           
           /*
            * 
            * 
            * https://na15.salesforce.com/services/data/v24.0/sobjects/Merchandise__c/describe/
              HttpClient httpclient = new HttpClient();

              JSONObject update = new JSONObject();

              try {
                  update.put("Name","test");
                  //update.put("BillingCity", city);
              } catch (JSONException e) {
                  e.printStackTrace();
              }

              
              //"https://cs11.salesforce.com/services/data/v28.0/sobjects/Account/quickActions/CreateContact" 
              PostMethod patch = new PostMethod(
                      
                      "https://cs11.salesforce.com/services/data/v28.0/sobjects/services/data/v20.0/sobjects/Contact/"    
                      ) {
              };

              //patch.setRequestHeader("Authorization", "OAuth " + apiConfig.getAccessToken());
              patch.setRequestHeader("Authorization", "Bearer " + apiConfig.getAccessToken());
              patch.setRequestEntity(new StringRequestEntity(update.toString(),
                      "application/json", null));

              try {
                  httpclient.executeMethod(patch);
                  System.err.println("HTTP status " + patch.getStatusCode()
                          + " testing\n\n");
              } finally {
                  patch.releaseConnection();
              }
              }catch(Exception e){e.printStackTrace();}
       
       
      
       
       
       
      //String instanceUrl="https://na15.salesforce.com";
      String accountId = null;
      HttpClient httpclient = new HttpClient();

      JSONObject account = new JSONObject();

      try {
          account.put("Name", "NEWTESTOBJName");
          //account.put("AccountId", "001i000000pkyrdAAA");
          account.put("email", "NEWTEST@Odd.BJName");
          account.put("FirstName", "first");
          account.put("LastName", "last");
      } catch (JSONException e) {
          e.printStackTrace();
          
      }
      
      
      try {
          
          //-"https://na1.salesforce.com/services/data/v28.0/sobjects/Account/quickActions/CreateContact"
          //"https://na1.salesforce.com/services/data/v28.0/quickActions/"
          //"https://na15.salesforce.com/services/data/v28.0/quickActions/CreateContact"
          //"https://na15.salesforce.com/services/data/v20.0/sobjects/Contact/" 
      PostMethod post = new PostMethod(
              
              "https://na1.salesforce.com/services/data/v28.0/sobjects/Account/quickActions/CreateContact"
              
              );

      post.setRequestHeader("Authorization", "Bearer " + apiConfig.getAccessToken());
      post.setRequestEntity(new StringRequestEntity(account.toString(),"application/json", null));

      
          httpclient.executeMethod(post);
          
          System.err.println("creat.. contact");
          
          
              System.out.println("HTTP status " + post.getStatusCode()
                      + " creating test\n\n");

              if (post.getStatusCode() == HttpStatus.SC_CREATED) {
                  try {
                      JSONObject response = new JSONObject(new JSONTokener(
                              new InputStreamReader(
                                      post.getResponseBodyAsStream())));
                      System.out.println("Create response: "
                              + response.toString(2));

                      if (response.getBoolean("success")) {
                          accountId = response.getString("id");
                          System.out.println("New record id " + accountId + "\n\n");
                      }
                  } catch (JSONException e) {
                      e.printStackTrace();
                      //throw new ServletException(e);
                  }
              }
      }catch(Exception e){e.printStackTrace();}
      
      
      
      
      
      curl  -H 'Authorization: Bearer 00DZ000000MhaMd!ARwAQIq.itOShytvj98ChfZgbHiCM66jIyhqHYHXhhWHiihoJCgGZzKPakhg0aKd761WP1HWZi7sCG8anFWcim_Y7PoKWQ.m' https://cs11.salesforce.com/services/data/v20.0/sobjects/Account/001Z000000lTBLaIAO
      
      
      
      curl  -H 'Authorization: Bearer 00DZ000000MhaMd!ARwAQIq.itOShytvj98ChfZgbHiCM66jIyhqHYHXhhWHiihoJCgGZzKPakhg0aKd761WP1HWZi7sCG8anFWcim_Y7PoKWQ.m' https://cs11.salesforce.com/services/data/v20.0/query/?q=SELECT+name+from+A
      *
      *
      */   
       
      // curl https://na1.salesforce.com/services/data/v28.0/sobjects/Account/quickActions -H "Authorization: Bearer 00Di0000000kwPv!AREAQNNgzCpFdjBE84Ehg7xgd5Nwoub9w0CY676TtKG5oNxblmWYtQF3q1lYrtIootXcIagDujwL7VEPbV.m3mmF43IgfHnM
  }
  
public void deleteContact(ApiConfig apiConfig, String contactId)  {
  
  try{
  HttpClient httpclient = new HttpClient();

  DeleteMethod delete = new DeleteMethod(apiConfig.getInstanceUrl()
          + "/services/data/v20.0/sobjects/Contact/" + contactId);

  delete.setRequestHeader("Authorization", "OAuth " + apiConfig.getAccessToken());

  
      httpclient.executeMethod(delete);
      System.out.println("HTTP status " + delete.getStatusCode()
              + " deleting account " + contactId + "\n\n");
  }catch(Exception e){e.printStackTrace();
  } finally {
      //delete.releaseConnection();
  }
}


public java.util.Map <String, String> getContactEmailList(ApiConfig apiConfig){
  
      /*
      apiConfig= new ApiConfig();
      apiConfig.setAccessToken("00DZ000000MhaMd!ARwAQOmZ2MpksgBDo_LPFW7m36oHJ5DtME1OulwhgO5xK1.bGgo9qr7JFGCe7Im56V.oYtmvOqF1ziS_iMzkcvoOMMv9f9ND");
      apiConfig.setInstanceUrl("https://cs11.salesforce.com");
      */
      
      
      java.util.Map emails = new java.util.TreeMap();
      try{
          
      HttpClient httpclient = new HttpClient();
      GetMethod get = new GetMethod(apiConfig.getInstanceUrl()
              + "/services/data/v20.0/query");

      // set the token in the header
      get.setRequestHeader("Authorization", "OAuth " + apiConfig.getAccessToken());

      // set the SOQL as a query param
      NameValuePair[] params = new NameValuePair[1];

      params[0] = new NameValuePair("q",
              "SELECT id,Email from Contact");
      get.setQueryString(params);

      try {
          httpclient.executeMethod(get);
          if (get.getStatusCode() == HttpStatus.SC_OK) {
              // Now lets use the standard java json classes to work with the
              // results
              try {
                  JSONObject response = new JSONObject(
                          new JSONTokener(new InputStreamReader(
                                  get.getResponseBodyAsStream())));
                  System.out.println("Query response: "
                          + response.toString(2));

                  JSONArray results = response.getJSONArray("records");

                  for (int i = 0; i < results.length(); i++) {
                      
                      //Contact contact = new Contact();
                      //contact.setId(results.getJSONObject(i).getString("Id"));
                      //contact.setEmail(results.getJSONObject(i).getString("Email"));
                      
                      //System.err.println(">>"+(results.getJSONObject(i).get("Email") ==null));
                      if( !results.getJSONObject(i).get("Email").toString().equals("null") ){
                          
                          emails.put( results.getJSONObject(i).getString("Id"),
                                  results.getJSONObject(i).getString("Email") );
                          //contact.setEmail(results.getJSONObject(i).getString("Email"));
                          //System.err.println("*** "+ contact.getEmail() );
                      }
                      
                      /*
                      System.out.println(results.getJSONObject(i).getString("Id")
                              + ", "
                              + results.getJSONObject(i).getString("Name")
                              + "\n");
                              */
                  }
                  System.out.println("\n");
              } catch (JSONException e) {
                  e.printStackTrace();
                  
              }
          }
      } finally {
          get.releaseConnection();
      }
      }catch(Exception ex){ex.printStackTrace();}
      return emails;
  
  
  
}


}//end class