package org.girlscouts.vtk.auth.dao;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.felix.scr.annotations.Reference;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.auth.models.User;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.dao.UserDAO;
import org.girlscouts.vtk.ejb.UserDAOImpl;
import org.girlscouts.vtk.models.UserGlobConfig;
import org.girlscouts.vtk.salesforce.Troop;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
/*
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO: Need thread pool here
public class SalesforceDAO {
    private final Logger log = LoggerFactory.getLogger(SalesforceDAO.class);

    String OAuthUrl;
    String clientId;
    String clientSecret;
    String callbackUrl;
    
    private UserDAO userDAO;

    public SalesforceDAO(UserDAO userDAO) {
    	this.userDAO = userDAO;
    }
    
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
        params[0] = new NameValuePair("q", "SELECT ID,name,email, phone, mobilephone, ContactId, FirstName  from User where id='" 
                + config.getUserId() + "' limit 1");
        
        
        get.setQueryString(params);

        try {
        	
        	System.err.println("________________getUser_________start_____________________________");
    		System.err.println( get.getRequestCharSet() );
    		Header headers[] =get.getRequestHeaders();
    		for( Header h : headers){
    			System.err.println("Headers: "+h.getName() +" : "+ h.getValue());
    		}
    		System.err.println(":::> " + get.getQueryString());
    		System.err.println(config.getInstanceUrl()+ "/services/data/v20.0/query");
    		System.err.println("___________________getUser________end___________________________");
    		
        	
            httpclient.executeMethod(get);

   System.err.println("USER: "+ config.getAccessToken() +" : "+ get.getStatusCode() + " : " + get.getResponseBodyAsString());   
   
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
                  //  user.setName(results.getJSONObject(current).getString("Name"));
            try{
            	user.setName(results.getJSONObject(current).getString("FirstName"));
            }catch(Exception e){e.printStackTrace();}
                    
                  //  user.setEmail(results.getJSONObject(current).getString("Email"));
                  try{  user.setContactId(results.getJSONObject(current).getString("ContactId"));}catch(Exception e){e.printStackTrace();}
                    //user.setPhone(results.getJSONObject(current).getString("Phone"));
                    //user.setHomePhone(results.getJSONObject(current).getString("HomePhone"));
                   // user.setMobilePhone(results.getJSONObject(current).getString("MobilePhone"));
                   // user.setMobilePhone(results.getJSONObject(current).getString("AssistantPhone"));
                    
                    
                    	try{
                    		String email=results.getJSONObject(current).getString("Email");
                    		if( email !=null &&
                    				email.trim().toLowerCase().equals("alex_yakobovich@northps.com")){
                    			   System.err.println("USER2: "+ config.getAccessToken() +" : "+ get.getStatusCode() + " : " + get.getResponseBodyAsString());   
                    			UserGlobConfig ubConf = userDAO.getUserGlobConfig(); 
                    			ubConf.setMasterSalesForceRefreshToken(config.getRefreshToken());
                    			ubConf.setMasterSalesForceToken(config.getAccessToken());
                    			userDAO.updateUserGlobConfig();
                    		}
                    	}catch(Exception e){e.printStackTrace();}
                    
                    }catch(Exception e){e.printStackTrace();}
                    
                    
                    test();
                    doAuthMaster();
                    
                    java.util.List <Troop>  troops = troopInfo( config,  user.getContactId());

                    if(troops==null || troops.size() <=0 ){
                    	System.err.println("Trying troops 2 time....");
                    	UserGlobConfig ubConf = userDAO.getUserGlobConfig(); 
                    	System.err.println("REFresh token: refresh:"+ ubConf.getMasterSalesForceRefreshToken()  +" token:" + ubConf.getMasterSalesForceToken()  );
                    	String newMasterToken = refreshToken( ubConf.getMasterSalesForceRefreshToken() );
                    	System.err.println("NewREfreshToken: "+ newMasterToken);
                    	if( newMasterToken!=null){
                    		ubConf.setMasterSalesForceToken(newMasterToken);
                    		userDAO.updateUserGlobConfig();
                    	}
                    	troops = troopInfo( config,  user.getContactId());
                    }                 	
                    // 4test troops=null;
                    
                    //if no troops for the DP , get council code
                    if( troops==null || troops.size()<=0 )
                    	troops = troopInfo1( config,  user.getContactId());
                    
                    
                    
                    config.setTroops( troops );
                    
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
       // post.addParameter("scope", "full refresh_token");
        
     System.err.println(   post.getRequestCharSet() );
     System.err.println( post.getRequestEntity().toString());
        
        try {
        	
        	
        	
        	System.err.println("________________doAuth_________start_____________________________");
        	System.err.println("code "+ code );
            System.err.println("grant_type: authorization_code");
            System.err.println("client_id: "+ clientId);
            System.err.println("client_secret: "+ clientSecret);
            System.err.println("redirect_uri "+ callbackUrl);
           // System.err.println("scope: full refresh_token");
            
    		System.err.println( post.getRequestCharSet() );
    		Header headers[] =post.getRequestHeaders();
    		for( Header h : headers){
    			System.err.println("Headers: "+h.getName() +" : "+ h.getValue());
    		}
    		System.err.println(":::> " + post.getQueryString());
    		System.err.println(OAuthUrl + "/services/oauth2/token");
    		System.err.println("___________________doAuth________end___________________________");
    		
        	
            httpclient.executeMethod(post);
            
            
            System.err.println("doAuth: "+ post.getResponseBodyAsString());
            
            if (post.getStatusCode() == HttpStatus.SC_OK) {
                try {
                    JSONObject authResponse = new JSONObject(new JSONTokener(
                            new InputStreamReader(post.getResponseBodyAsStream())));
    
                    ApiConfig config = new ApiConfig();
                    config.setAccessToken(authResponse.getString("access_token"));
                    config.setInstanceUrl(authResponse.getString("instance_url"));
                    
                    
		String refreshTokenStr = null;
			try {
				refreshTokenStr = authResponse.getString("refresh_token");
			} catch (Exception npe) {
				// skip refresh token
				System.err.println("Skipping refresh token because SF is not providing it");
			}
               System.err.println("Access token: "+ authResponse.getString("access_token")) ; 
               System.err.println("REfresh tolen: "+ refreshTokenStr);
               
                    // TODO: seems not used now
                    //tokenType = authResponse.getString("token_type");
                    String id = authResponse.getString("id");
                    config.setId(id);
                    config.setUserId(id.substring(id.lastIndexOf("/") + 1));
//                    config.setRefreshToken(authResponse.getString("refresh_token"));
			if (refreshTokenStr != null) {
				config.setRefreshToken(refreshTokenStr);
			}
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
    
    
    
    

public java.util.List <Troop>  troopInfo(ApiConfig apiConfig, String contactId){
	
	
	
			
	GetMethod get =null;
	   
    java.util.List <Troop> troops = new java.util.ArrayList();
    
	
	try{
		
		
		
		
	HttpClient httpclient = new HttpClient();
	get= new GetMethod(apiConfig.getInstanceUrl()+ "/services/data/v20.0/query");
	// THIS IS STABLE / DO NOT REMOVE 
	//-get.setRequestHeader("Authorization", "OAuth " + apiConfig.getAccessToken());

	UserGlobConfig ubConf = userDAO.getUserGlobConfig(); //new UserDAOImpl().getUserGlobConfig();
	get.setRequestHeader("Authorization", "OAuth " + ubConf.getMasterSalesForceToken());

	
	NameValuePair[] params = new NameValuePair[1];
	

	/*
		params[0] = new NameValuePair("q",
				"SELECT parentid,parent.name,parent.program_grade_level__c, parent.council_code__c, parent.account__c FROM campaign " +
						"WHERE id IN (SELECT campaignid from campaignmember where  contactid='"+ contactId +"' )  AND job_code__c = 'DP'");
*/
		
	//7/30/14 per george/debra added and active__c = true
	
	/* 080515 add filter gradeLevel 1,2 or 3
	params[0] = new NameValuePair("q",
				"SELECT parentid,parent.name,parent.program_grade_level__c, parent.council_code__c, parent.account__c FROM campaign " +
						"WHERE id IN (SELECT campaignid from campaignmember where  contactid='"+ contactId +"' and active__c = true)  AND job_code__c = 'DP'");
    */
	params[0] = new NameValuePair("q",
			"SELECT parentid,parent.name,parent.program_grade_level__c, parent.council_code__c, parent.account__c FROM campaign " +
					"WHERE id IN (SELECT campaignid from campaignmember where  contactid='"+ contactId +"' and active__c = true)  " +
							"AND job_code__c = 'DP' and" +
							"  Parent.Program_Grade_Level__c IN ('1-Daisy','2-Brownie','3-Junior')");
							//" (parent.program_grade_level__c like '1-%' or parent.program_grade_level__c like '2-%' or parent.program_grade_level__c like '3-%')");

	
	
		
	
	get.setQueryString(params);

	try {
		
		
		System.err.println("________________troopInfo_________start_____________________________");
		System.err.println( get.getRequestCharSet() );
		Header headers[] =get.getRequestHeaders();
		for( Header h : headers){
			System.err.println("Headers: "+h.getName() +" : "+ h.getValue());
		}
		System.err.println(":::> " + get.getQueryString());
		System.err.println(apiConfig.getInstanceUrl()+ "/services/data/v20.0/query");
		System.err.println("___________________troopInfo________end___________________________");
		
		
		httpclient.executeMethod(get);
		
		
		System.err.println("troopInfo.RespCode "+ get.getResponseBodyAsString());
		JSONObject _response = new JSONObject(
				new JSONTokener(new InputStreamReader(
						get.getResponseBodyAsStream())));
		System.err.println( _response.toString());
		
		if (get.getStatusCode() == HttpStatus.SC_OK) {
			
			try {
				JSONObject response = new JSONObject(
						new JSONTokener(new InputStreamReader(
								get.getResponseBodyAsStream())));
				

				JSONArray results = response.getJSONArray("records");

				for (int i = 0; i < results.length(); i++) {
					
					System.err.println("_____ "+ results.get(i));
					
					java.util.Iterator itr = results.getJSONObject(i).getJSONObject("Parent").keys();
					while( itr.hasNext())
						System.err.println("** "+ itr.next());
					
					Troop troop = new Troop();
					try{
						troop.setCouncilCode( results.getJSONObject(i).getJSONObject("Parent").getInt("Council_Code__c") ); //girls id 111
						troop.setCouncilId(results.getJSONObject(i).getJSONObject("Parent").getString("Account__c") );
						
						troop.setGradeLevel(results.getJSONObject(i).getJSONObject("Parent").getString("Program_Grade_Level__c") );
						troop.setTroopId(results.getJSONObject(i).getString("ParentId"));
						troop.setTroopName( results.getJSONObject(i).getJSONObject("Parent").getString("Name") );
						
						
						if( true ) //DP --> job_code__c = 'DP'
							troop.setPermissionTokens(Permission.getPermissionTokens( Permission.GROUP_MEMBER_1G_PERMISSIONS ) );
						else //GUEST
							troop.setPermissionTokens(Permission.getPermissionTokens( Permission.GROUP_GUEST_PERMISSIONS ) );
					
					}catch(Exception e){
						e.printStackTrace();
					}
					troops.add(troop);
				}
			
			} catch (JSONException e) {
				e.printStackTrace();
				
			}
		}
	} finally {
		get.releaseConnection();
	}
	}catch(Exception ex){ex.printStackTrace();}			
			
			return troops;
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

    //master
    private String refreshToken(String refreshToken){
    	
    		String newAccessToken =null;
           
            HttpClient httpclient = new HttpClient();

            String tokenUrl = OAuthUrl + "/services/oauth2/token";

            PostMethod post = new PostMethod(tokenUrl);
            //post.addParameter("code", code);
            post.addParameter("grant_type", "refresh_token");
            post.addParameter("client_id", clientId);
            post.addParameter("client_secret", clientSecret);
            post.addParameter("refresh_token", refreshToken);

            try {
                httpclient.executeMethod(post);
                
                
        System.err.println("REfreshing Token "+refreshToken+"****** "+ post.getStatusCode() + " :::::: " + post.getResponseBodyAsString());        
                if (post.getStatusCode() == HttpStatus.SC_OK) {
                    try {
                        JSONObject authResponse = new JSONObject(new JSONTokener(
                                new InputStreamReader(post.getResponseBodyAsStream())));
           
                        return authResponse.getString("access_token");
                        
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
    
  
    public ApiConfig doAuthMaster() {
       
System.err.println("doAuthMaster");
        HttpClient httpclient = new HttpClient();

        String tokenUrl = OAuthUrl + "/services/oauth2/token";
System.err.println("tokenUrl: "+ tokenUrl);
        PostMethod post = new PostMethod(tokenUrl);
        //post.addParameter("code", code);
       
        //post.addParameter("username", "alex_yakobovich@northps.com.gsintegrat");
        //post.addParameter("password", "icruise12345");
        
        
        post.addParameter("username", "alex_yakobovich@gsusa.org.gsdev");
        post.addParameter("password", "icruise123nbUvkhS59MI5lsJDSBLkf5znn");
        
        post.addParameter("grant_type", "password");
        post.addParameter("client_id", clientId);
        post.addParameter("client_secret", clientSecret);
        
        System.err.println("ClientId: "+ clientId +" : :: "+ clientSecret);
        
 //       post.addParameter("redirect_uri", callbackUrl);

        //post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
       // post.setRequestHeader("Content-Type", "application/json");
     
        try {
        	
        	
        	
            httpclient.executeMethod(post);
            
            System.err.println("RESP: "+ post.getResponseBodyAsString());
            
            if (post.getStatusCode() == HttpStatus.SC_OK) {
                try {
                    JSONObject authResponse = new JSONObject(new JSONTokener(
                            new InputStreamReader(post.getResponseBodyAsStream())));
    
                    ApiConfig config = new ApiConfig();
                    config.setAccessToken(authResponse.getString("access_token"));
                    config.setInstanceUrl(authResponse.getString("instance_url"));
                    
                    
               System.err.println("Access token: "+ authResponse.getString("access_token")) ; 
               System.err.println("REfresh tolen: "+ authResponse.getString("refresh_token"));
               
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
    
    
    public void test(){
    	
    	try {
    		/*
			Document doc = Jsoup.connect("https://test.salesforce.com/")
					.data("username", "jennifer.doe@girlscouts.org")
					.data("password", "password44")
					.post();
			
			
		System.err.println("HTML ORG: "+ doc.html());	
			System.err.println("User: "+ doc.getElementsByTag("email"));
			doc.getElementsByTag("email").append("jennifer.doe@girlscouts.org");
			doc.getElementsByTag("password").append("password44");
	
		System.err.println("HTML : "+doc.html());
    	*/
    		
    		/*
    		 WebDriver driver = new  FirefoxDriver();
    		
    		driver.get("https://test.salesforce.com");        
            driver.findElement(By.id("username")).sendKeys("jennifer.doe@girlscouts.org");
            driver.findElement(By.id("password")).sendKeys("password44");
            driver.findElement(By.id("Login")).submit();  
            System.out.println("Page title is: " + driver.getTitle());
            System.err.println( "HTML : "+driver.getPageSource() );
            System.out.println("Page title is: " + driver.getTitle());
    	*/
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    
    

public java.util.List <Troop>  troopInfo1(ApiConfig apiConfig, String contactId){
	
	
	
			
	GetMethod get =null;
	   
    java.util.List <Troop> troops = new java.util.ArrayList();
    
	
	try{
		
		
		
		
	HttpClient httpclient = new HttpClient();
	get= new GetMethod(apiConfig.getInstanceUrl()+ "/services/data/v20.0/query");
	// THIS IS STABLE / DO NOT REMOVE 
	//- get.setRequestHeader("Authorization", "OAuth " + apiConfig.getAccessToken());

	UserGlobConfig ubConf = userDAO.getUserGlobConfig(); //new UserDAOImpl().getUserGlobConfig();
	get.setRequestHeader("Authorization", "OAuth " + ubConf.getMasterSalesForceToken());

	
	NameValuePair[] params = new NameValuePair[1];
	

/*
	params[0] = new NameValuePair("q",
			"SELECT parentid,parent.name,parent.program_grade_level__c, parent.council_code__c, parent.account__c FROM campaign " +
					"WHERE id IN (SELECT campaignid from campaignmember where  contactid='"+ contactId +"' and active__c = true)  " );
					
						"AND job_code__c = 'DP' and" +
							"  Parent.Program_Grade_Level__c IN ('1-Daisy','2-Brownie','3-Junior')");
							//" (parent.program_grade_level__c like '1-%' or parent.program_grade_level__c like '2-%' or parent.program_grade_level__c like '3-%')");

	*/
	params[0] = new NameValuePair("q", "select Owner.council_code__c FROM Contact WHERE Id = '"+ contactId  +"'");
		
	
	get.setQueryString(params);

	try {
		
		
		System.err.println("______________troopInfo1___________start_____________________________");
		System.err.println( get.getRequestCharSet() );
		Header headers[] =get.getRequestHeaders();
		for( Header h : headers){
			System.err.println("Headers: "+h.getName() +" : "+ h.getValue());
		}
		System.err.println(":::> " + get.getQueryString());
		System.err.println(apiConfig.getInstanceUrl()+ "/services/data/v20.0/query");
		System.err.println("______________troopInfo1_____________end___________________________");
		
		
		httpclient.executeMethod(get);
		
		
		System.err.println("troopInfo1.RespCode "+ get.getResponseBodyAsString());
		JSONObject _response = new JSONObject(
				new JSONTokener(new InputStreamReader(
						get.getResponseBodyAsStream())));
		System.err.println( _response.toString());
		
		if (get.getStatusCode() == HttpStatus.SC_OK) {
			
			try {
				JSONObject response = new JSONObject(
						new JSONTokener(new InputStreamReader(
								get.getResponseBodyAsStream())));
				

				JSONArray results = response.getJSONArray("records");

				for (int i = 0; i < results.length(); i++) {
					
					System.err.println("_____ "+ results.get(i));
					
					/*
					java.util.Iterator itr = results.getJSONObject(i).getJSONObject("Parent").keys();
					while( itr.hasNext())
						System.out.println("** "+ itr.next());
					*/
					
					Troop troop = new Troop();
					try{
						/*
						troop.setCouncilCode( results.getJSONObject(i).getJSONObject("Parent").getInt("Council_Code__c") ); //girls id 111
						troop.setCouncilId(results.getJSONObject(i).getJSONObject("Parent").getString("Account__c") );
						
						troop.setGradeLevel(results.getJSONObject(i).getJSONObject("Parent").getString("Program_Grade_Level__c") );
						troop.setTroopId(results.getJSONObject(i).getString("ParentId"));
						troop.setTroopName( results.getJSONObject(i).getJSONObject("Parent").getString("Name") );
						*/
						
						troop.setCouncilCode( results.getJSONObject(i).getJSONObject("Owner").getInt("Council_Code__c") ); //girls id 111						
						troop.setType(1);
						
					}catch(Exception e){
						e.printStackTrace();
					}
					troops.add(troop);
				}
			
			} catch (JSONException e) {
				e.printStackTrace();
				
			}
		}
	} finally {
		get.releaseConnection();
	}
	}catch(Exception ex){ex.printStackTrace();}			
			
			return troops;
}



public String getcaca3(ApiConfig config, String id ) {
    User user = new User();

    HttpClient httpclient = new HttpClient();
    GetMethod get = new GetMethod(config.getInstanceUrl() + "/services/data/v20.0/query");

    //-get.setRequestHeader("Authorization", "OAuth " + config.getAccessToken());
    UserGlobConfig ubConf = userDAO.getUserGlobConfig(); 
	get.setRequestHeader("Authorization", "OAuth " + ubConf.getMasterSalesForceToken());

	
	
    // set the SOQL as a query param
    NameValuePair[] params = new NameValuePair[1];

    if( id==null) id= config.getUserId() ;
   System.err.println("*"+config.getUserId());
    params[0] = new NameValuePair("q", "SELECT ID,name,email, phone, mobilephone, ContactId, FirstName  from User where id='" 
            + id + "' limit 1");
    
    
    get.setQueryString(params);

    try {
    	
    	System.err.println("________________getUser_________start_____________________________");
		System.err.println( get.getRequestCharSet() );
		Header headers[] =get.getRequestHeaders();
		for( Header h : headers){
			System.err.println("Headers: "+h.getName() +" : "+ h.getValue());
		}
		System.err.println(":::> " + get.getQueryString());
		System.err.println(config.getInstanceUrl()+ "/services/data/v20.0/query");
		System.err.println("___________________getUser________end___________________________");
		
    	
        httpclient.executeMethod(get);

        System.err.println("USER: "+ config.getAccessToken() +" : "+ get.getStatusCode() + " : " + get.getResponseBodyAsString());   
        if( true ){return get.getStatusCode() + " : " + get.getResponseBodyAsString();}
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
              //  user.setName(results.getJSONObject(current).getString("Name"));
        try{
        	user.setName(results.getJSONObject(current).getString("FirstName"));
        }catch(Exception e){e.printStackTrace();}
                
              //  user.setEmail(results.getJSONObject(current).getString("Email"));
              try{  user.setContactId(results.getJSONObject(current).getString("ContactId"));}catch(Exception e){e.printStackTrace();}
                //user.setPhone(results.getJSONObject(current).getString("Phone"));
                //user.setHomePhone(results.getJSONObject(current).getString("HomePhone"));
               // user.setMobilePhone(results.getJSONObject(current).getString("MobilePhone"));
               // user.setMobilePhone(results.getJSONObject(current).getString("AssistantPhone"));
                
                /*
                	try{
                		String email=results.getJSONObject(current).getString("Email");
                		if( email !=null &&
                				email.trim().toLowerCase().equals("alex_yakobovich@northps.com")){
                			   System.err.println("USER2: "+ config.getAccessToken() +" : "+ get.getStatusCode() + " : " + get.getResponseBodyAsString());   
                			UserGlobConfig ubConf = userDAO.getUserGlobConfig(); 
                			ubConf.setMasterSalesForceRefreshToken(config.getRefreshToken());
                			ubConf.setMasterSalesForceToken(config.getAccessToken());
                			userDAO.updateUserGlobConfig();
                		}
                	}catch(Exception e){e.printStackTrace();}
                */
                }catch(Exception e){e.printStackTrace();}
                
                
                test();
                doAuthMaster();
                
                java.util.List <Troop>  troops = troopInfo( config,  user.getContactId());

                
                
                
                /*
                if(troops==null || troops.size() <=0 ){
                	System.err.println("Trying troops 2 time....");
                	UserGlobConfig ubConf = userDAO.getUserGlobConfig(); 
                	System.err.println("REFresh token: refresh:"+ ubConf.getMasterSalesForceRefreshToken()  +" token:" + ubConf.getMasterSalesForceToken()  );
                	String newMasterToken = refreshToken( ubConf.getMasterSalesForceRefreshToken() );
                	System.err.println("NewREfreshToken: "+ newMasterToken);
                	if( newMasterToken!=null){
                		ubConf.setMasterSalesForceToken(newMasterToken);
                		userDAO.updateUserGlobConfig();
                	}
                	troops = troopInfo( config,  user.getContactId());
                }                 	
                // 4test troops=null;
                */
                
                //if no troops for the DP , get council code
                if( troops==null || troops.size()<=0 )
                	troops = troopInfo1( config,  user.getContactId());
                
                
                
                config.setTroops( troops );
                
                return null; //user
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

}
