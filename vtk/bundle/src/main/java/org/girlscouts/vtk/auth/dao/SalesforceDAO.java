package org.girlscouts.vtk.auth.dao;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.auth.models.User;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.dao.TroopDAO;
import org.girlscouts.vtk.ejb.ConnectionFactory;
import org.girlscouts.vtk.ejb.SessionFactory;
import org.girlscouts.vtk.ejb.VtkError;
import org.girlscouts.vtk.models.Contact;
import org.girlscouts.vtk.salesforce.Troop;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrUtil;

// TODO: Need thread pool here
public class SalesforceDAO {
	private final Logger log = LoggerFactory.getLogger("vtk");
	String OAuthUrl;
	String clientId;
	String clientSecret;
	String callbackUrl;
	private TroopDAO troopDAO;
	private ConnectionFactory connectionFactory;
	private Session session;
	private java.util.List<VtkError> errors;
	private String vtkDemoPath ="/content/vtk-demo";
	private String vtkDemoCouncil="999";
	
	public SalesforceDAO(TroopDAO troopDAO, ConnectionFactory connectionFactory) {
		this.troopDAO = troopDAO;
		this.connectionFactory = connectionFactory;
		this.errors = new java.util.ArrayList<VtkError>();
	}
	
	public SalesforceDAO(TroopDAO troopDAO, ConnectionFactory connectionFactory, SessionFactory sessionFactory) {
		this(troopDAO, connectionFactory);
		try {
			this.session = sessionFactory.getSession();
		} catch (RepositoryException e) {
			log.error("Cannot get session due to RepositoryException.");
		}
	}

	public User getUser(ApiConfig apiConfig) throws IllegalAccessException {
		this.errors = new java.util.ArrayList<VtkError>();
		User user = new User();
		CloseableHttpClient connection = null;

		String vtlApiUserUri = apiConfig.getVtkApiUserUri();
		String url = apiConfig.getWebServicesUrl() + vtlApiUserUri
				+ "?USER_ID=" + apiConfig.getUserId();

		HttpGet method = new HttpGet(url);
		method.setHeader("Authorization", "OAuth " + apiConfig.getAccessToken());

		try {

			String rsp = null;
			if(  !apiConfig.isDemoUser() ){
			
					connection = connectionFactory.getConnection();
					CloseableHttpResponse resp = connection.execute(method);
					int statusCode = resp.getStatusLine().getStatusCode();
					if (statusCode != HttpStatus.SC_OK) {
						System.err.println("Salesforce API getUser status code: "+ statusCode +" : "+ resp);
						throw new IllegalAccessException();
					}
		
					HttpEntity entity = null;
					
					try {
						entity = resp.getEntity();
						entity.getContent();
						rsp = EntityUtils.toString(entity);
						EntityUtils.consume(entity);
						method.releaseConnection();
						method = null;
					} finally {
						resp.close();
					}
					
					//response = new JSONObject(rsp);
					log.debug(">>>>> " + rsp);
System.err.println("ALEXALEX: "+ rsp);

System.err.println("testtesttest");
log.debug(" " + rsp);

					
					
			}else{
	
				
				String userJsonFile= vtkDemoPath +"/vtkUser_"+apiConfig.getDemoUserName()+".json";
				
				rsp = readFile(userJsonFile).toString();
			}
			
			user = getUser_parse(user, apiConfig, rsp );

		
		
		} catch (HttpException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (Exception eG) {
			
			eG.printStackTrace();
		} finally {
			if (method != null)
				method.releaseConnection();
		}


		return user;
	}
	
	
	public User getUser_parse(User user, ApiConfig apiConfig, String rsp) throws Exception{

		try {
			
			
			JSONObject response = new JSONObject(rsp);
			log.debug("<<<<<Apex user reponse: " + response);
			JSONArray results = response.getJSONArray("users");
			for (int i = 0; i < results.length(); i++) {
				org.json.JSONObject json = results.getJSONObject(i);
				try {
					user.setSfUserId(json.getString("Id"));

					try {
						user.setEmail(json.getString("Email"));
					} catch (org.json.JSONException je) {
						log.info("User " + user.getSfUserId()
								+ " does not have an Email");
					}
					try {
						user.setName(json.getString("FirstName"));
						user.setFirstName(user.getName());
						
						
						if(apiConfig.isUseAsDemo() ){
							//writeToFile("/Users/akobovich/vtk/vtkUser_"+apiConfig.getUser().getName()+".json" , rsp);
							writeToFile(vtkDemoPath +"/vtkUser_"+user.getName()+".json" , rsp);
							
						}
					} catch (org.json.JSONException je) {
						log.info("User " + user.getSfUserId()
								+ " does not have a FirstName");
					}
					try {
						user.setLastName(json.getString("LastName"));
					} catch (org.json.JSONException je) {
						log.info("User " + user.getSfUserId()
								+ " does not have a LastName");
					}
					try {
						user.setPhone(json.getString("Phone"));
					} catch (org.json.JSONException je) {
						log.info("User " + user.getSfUserId()
								+ " does not have a Phone");
					}
					try {
						user.setContactId(json.getString("ContactId"));
					} catch (org.json.JSONException je) {
						log.info("User " + user.getSfUserId()
								+ " does not have a ContactId");
					}
					try {
						org.json.JSONObject contactJson = json
								.getJSONObject("Contact");
						try {
							user.setAdmin(contactJson
									.getBoolean("VTK_Admin__c"));
						} catch (org.json.JSONException je) {
							log.info("User "
									+ user.getSfUserId()
									+ " does not have a Contact VTK_Admin__c");
						}
						try {
							if(  apiConfig.isDemoUser() ){
								user.setAdminCouncilId(Integer.parseInt(vtkDemoCouncil));
							}else{
								user.setAdminCouncilId(contactJson
									.getJSONObject("Owner").getInt(
											"Council_Code__c"));
							}
						} catch (org.json.JSONException je) {
							log.info("User " + user.getSfUserId()
									+ " does not have a Contact Owner");
						}
					} catch (org.json.JSONException je) {
						log.info("User " + user.getSfUserId()
								+ " does not have a Contact");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				
				
				JSONArray parentTroops = response.getJSONArray("camps");
				java.util.List<Troop> troops = getTroops_merged(user,
						apiConfig, user.getSfUserId(), parentTroops);
				
				apiConfig.setTroops(troops);				
				
				java.util.List<VtkError> _errors = apiConfig.getErrors();
				if( errors!=null && errors.size()>0 ){
					if( _errors!=null )
						errors.addAll(_errors);
					apiConfig.setErrors(errors);

					
				}
				return user;

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return user;
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
		log.debug(post.getRequestCharSet());
		log.debug(post.getRequestEntity().toString());
		try {
			log.debug("________________doAuth_________start_____________________________");
			log.debug("code " + code);
			log.debug("grant_type: authorization_code");
			log.debug("client_id: " + clientId);
			log.debug("client_secret: " + clientSecret);
			log.debug("redirect_uri " + callbackUrl);
			log.debug(post.getRequestCharSet());
			Header headers[] = post.getRequestHeaders();
			for (Header h : headers) {
				log.debug("Headers: " + h.getName() + " : " + h.getValue());
			}
			log.debug(":::> " + post.getQueryString());
			log.debug(OAuthUrl + "/services/oauth2/token");
			log.debug("___________________doAuth________end___________________________");
			httpclient.executeMethod(post);
			log.debug("doAuth: " + post.getResponseBodyAsString());
			if (post.getStatusCode() == HttpStatus.SC_OK) {
				try {
					JSONObject authResponse = new JSONObject(new JSONTokener(
							new InputStreamReader(
									post.getResponseBodyAsStream())));
					ApiConfig config = new ApiConfig();
					config.setAccessToken(authResponse
							.getString("access_token"));
					config.setInstanceUrl(authResponse
							.getString("instance_url"));
					config.setWebServicesUrl(authResponse
							.getString("sfdc_community_url"));
					String refreshTokenStr = null;
					try {
						refreshTokenStr = authResponse
								.getString("refresh_token");
					} catch (Exception npe) {
						// skip refresh token
						log.debug("Skipping refresh token because SF is not providing it");
					}
					log.debug("Access token: "
							+ authResponse.getString("access_token"));
					log.debug("REfresh tolen: " + refreshTokenStr);

					String id = authResponse.getString("id");
					config.setId(id);
					config.setUserId(id.substring(id.lastIndexOf("/") + 1));
					if (refreshTokenStr != null) {
						config.setRefreshToken(refreshTokenStr);
					}
					config.setCallbackUrl(callbackUrl);
					config.setClientId(clientId);
					config.setClientSecret(clientSecret);
					config.setOAuthUrl(OAuthUrl);
					return config;
				} catch (JSONException e) {
					log.error("JSON Parse exception: " + e.toString());
				}
			} else {
				log.error("Return status not OK: " + post.getStatusCode() + " "
						+ post.getResponseBodyAsString());
			}
		} catch (Exception e) {
			log.error("Error executing HTTP POST when authenticating: "
					+ e.toString());
		} finally {
			post.releaseConnection();
		}
		return null;
	}

	private ApiConfig refreshToken(ApiConfig config) {
		HttpClient httpclient = new HttpClient();
		String tokenUrl = config.getOAuthUrl() + "/services/oauth2/token";
		PostMethod post = new PostMethod(tokenUrl);
		post.addParameter("grant_type", "refresh_token");
		post.addParameter("client_id", config.getClientId());
		post.addParameter("client_secret", config.getClientSecret());
		post.addParameter("refresh_token", config.getRefreshToken());
		try {
			httpclient.executeMethod(post);
			log.debug("Refreshing Token " + config.getRefreshToken()
					+ "****** " + post.getStatusCode() + " :::::: "
					+ post.getResponseBodyAsString());
			if (post.getStatusCode() == HttpStatus.SC_OK) {
				try {
					JSONObject authResponse = new JSONObject(new JSONTokener(
							new InputStreamReader(
									post.getResponseBodyAsStream())));
					config.setAccessToken(authResponse
							.getString("access_token"));
				} catch (JSONException e) {
					log.error("JSON Parse exception: " + e.toString());
				}
			} else {
				log.error("Return status not OK: " + post.getStatusCode() + " "
						+ post.getResponseBodyAsString());
			}
		} catch (Exception e) {
			log.error("Error executing HTTP POST when authenticating: "
					+ e.toString());
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}
		return config;
	}

	public java.util.List<Contact> getContacts(ApiConfig apiConfig,
			String sfTroopId) {
		this.errors = new java.util.ArrayList<VtkError>();
		CloseableHttpClient connection = null;
		java.util.List<Contact> contacts = new java.util.ArrayList();

		String vtkApiContactUri = apiConfig.getVtkApiContactUri();
		String url = apiConfig.getWebServicesUrl() + vtkApiContactUri
				+ "?troopId=" + sfTroopId;
		
		HttpGet method = new HttpGet(url);

		method.setHeader("Authorization", "OAuth " + getToken(apiConfig));
		String rsp = null;
		try {
			if(  !apiConfig.isDemoUser() ){
				connection = connectionFactory.getConnection();
				CloseableHttpResponse resp = connection.execute(method);
				int statusCode = resp.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK) {
					
					
					try{

						  VtkError err= new VtkError();

						  err.setName("Found error(s) while getting Contacts ");

						  err.setDescription("Error int SalesForceDAO.getContacts-: found error in response json from Salesforce. Exception sf response code: " +statusCode);

						  err.setUserFormattedMsg("There appears to be an error in retrieving troop contacts.  Please notify support with error code VTK-" + sfTroopId + ".  Meanwhile, the application should still function correctly.");

						  err.setErrorCode("VTK-"+ sfTroopId);

						  errors.add(err);

						 

						  if( errors!=null && errors.size()>0 ){

						apiConfig.setErrors(errors);

						  }

						 

						  }catch(Exception e){e.printStackTrace();}

						return contacts;

						
				}
				apiConfig.setLastTimeTokenRefreshed(java.util.Calendar.getInstance().getTimeInMillis() );
				HttpEntity entity = null;
			
				try {
					entity = resp.getEntity();
					entity.getContent();
					rsp = EntityUtils.toString(entity);
System.err.println("ALEXALEX contacts: "+ rsp);
					EntityUtils.consume(entity);
					method.releaseConnection();
					method = null;
				} finally {
					resp.close();
				}
				rsp = "{\"records\":" + rsp + "}";

				if(apiConfig.isUseAsDemo() )
					writeToFile(vtkDemoPath +"/vtkContact_"+apiConfig.getUser().getName()+".json" , rsp);

				
				
			}else{
			
				String userJsonFile=vtkDemoPath +"/vtkContact_"+apiConfig.getDemoUserName()+".json";
				
				rsp = readFile(userJsonFile).toString();
		    }
			
			log.debug(">>>>> " + rsp);

			try {
				JSONObject response = new JSONObject(rsp);




//java.util.Map <String, Boolean> renewals = new java.util.TreeMap();
java.util.Map <String, java.util.Map> renewals = new java.util.TreeMap();

JSONArray results1 = response.getJSONObject("records").getJSONArray("lstCM");

for (int i = 0; i < results1.length(); i++) {



	try {
	
		String cId= results1.getJSONObject(i).getString("ContactId");
		java.util.Map cDetails = renewals.get( cId );
		if( cDetails == null ){
			cDetails = new java.util.TreeMap<Object, Object>();
			renewals.put(cId, cDetails);
		}
		cDetails.put("Display_Renewal__c", results1.getJSONObject(i).getBoolean("Display_Renewal__c") );
		cDetails.put("Membership__r", results1.getJSONObject(i).getJSONObject("Membership__r").getInt("Membership_Year__c") );
		
		
		//renewals.put( cId,  results1.getJSONObject(i).getBoolean("Display_Renewal__c"));
		//renewals.put( results1.getJSONObject(i).getString("Membership__r"),  results1.getJSONObject(i).getBoolean("Display_Renewal__c"));
		
		
	} catch (Exception e) {
	
	}

}//edn for




JSONArray results = response.getJSONObject("records").getJSONArray("lstCon");
				for (int i = 0; i < results.length(); i++) {

				log.debug("___lstCon__ " + results.get(i));

				Contact contact = new Contact();

				try {

					try {
	
					contact.setFirstName(results.getJSONObject(i)
	
					.getString("Name"));
					
					} catch (Exception e) {
	
					}



				try {

				contact.setEmail(results.getJSONObject(i)

				.getString("Email"));

				} catch (Exception e) {

				}



				try {

				contact.setPhone(results.getJSONObject(i)

				.getString("Phone"));

				} catch (Exception e) {

				}



				try {

				contact.setId(results.getJSONObject(i).getString(

				"Id"));

				} catch (Exception e) {

				}



				try {

				contact.setAddress(results.getJSONObject(i)

				.getString("MailingStreet"));

				} catch (Exception e) {

				}

				
				


				try {

				contact.setCity(results.getJSONObject(i).getString(

				"MailingCity"));

				} catch (Exception e) {

				}



				try {

				contact.setState(results.getJSONObject(i)

				.getString("MailingState"));

				} catch (Exception e) {

				}






				try {

				contact.setCountry(results.getJSONObject(i)

				.getString("MailingCountry"));

				} catch (Exception e) {

				}



				try {

				contact.setZip(results.getJSONObject(i).getString(

				"MailingPostalCode"));

				} catch (Exception e) {

				}



				try {

				contact.setAge(results.getJSONObject(i).getInt(

				"rC_Bios__Age__c"));

				} catch (Exception e) {

				}



				try {

				contact.setDob(results.getJSONObject(i).getString(

				"Birthdate"));

				} catch (Exception e) {

				}

				
				try {

					contact.setMembershipYear_adult(results.getJSONObject(i).getInt(

					"of_Adult_Years__c"));

					} catch (Exception e) {

					}
				
				try {

					contact.setMembershipYear_girl(results.getJSONObject(i).getInt(

					"of_Girl_Years__c"));

					} catch (Exception e) {

					}
				
				

				try {

				contact.setRole(results.getJSONObject(i).getString(

				"rC_Bios__Role__c"));

				} catch (Exception e) {

				}



				try {

				contact.setAccountId(results.getJSONObject(i)

				.getString("AccountId"));

				} catch (Exception e) {

				}



				try {

				contact.setContactId(results.getJSONObject(i)

				.getString("Id"));

				} catch (Exception e) {

				}



				//renewal

				try {

					Boolean isRenewal = (Boolean) renewals.get( contact.getId() ).get("Display_Renewal__c");
	
					contact.setRenewalDue(isRenewal ==null ? false : isRenewal);

					contact.setMembershipYear( (Integer) renewals.get( contact.getId() ).get("Membership__r") );
				} catch (Exception e) {

				}

				if( "Adult".equals(contact.getRole()) ){
					try {
	
						contact.setEmailOptIn(results.getJSONObject(i).getBoolean(
	
						"Email_Opt_In__c"));
	
						} catch (Exception e) {
	
						}
					
					
					try {
	
						contact.setTxtOptIn(results.getJSONObject(i).getBoolean(
	
						"Text_Phone_Opt_In__c"));
	
						} catch (Exception e) {
	
						}
				}//edn if
				
				contact.setType(0);

				Contact contactSub = new Contact();

				try {

				contactSub.setEmail(results

				.getJSONObject(i)

				.getJSONObject("Account")

				.getJSONObject(

				"rC_Bios__Preferred_Contact__r")

				.getString("Email"));
				} catch (Exception e) {

				}



				try {

				contactSub.setFirstName(results

				.getJSONObject(i)

				.getJSONObject("Account")

				.getJSONObject(

				"rC_Bios__Preferred_Contact__r")

				.getString("FirstName"));

				} catch (Exception e) {

				}



				try {

				contactSub.setLastName(results

				.getJSONObject(i)

				.getJSONObject("Account")

				.getJSONObject(

				"rC_Bios__Preferred_Contact__r")

				.getString("LastName"));

				} catch (Exception e) {

				}



				try {

				contactSub.setContactId(results

				.getJSONObject(i)

				.getJSONObject("Account")

				.getJSONObject(

				"rC_Bios__Preferred_Contact__r")

				.getString("Id"));

				} catch (Exception e) {

				}

				



				contactSub.setType(1);// caregiver

				java.util.List<Contact> contactsSub = new java.util.ArrayList<Contact>();

				contactsSub.add(contactSub);

				contact.setContacts(contactsSub);

				} catch (Exception e) {

				e.printStackTrace();

				}

				contacts.add(contact);

				}


				
				
				
				
				
				
				
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (HttpException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (Exception eG) {
			
			eG.printStackTrace();
		} finally {
			if (method != null)
				method.releaseConnection();
		}
		return contacts;
	}

	public java.util.List<Troop> troopInfo(User user, ApiConfig apiConfig,
			String contactId) {
		java.util.List<Troop> troops = new java.util.ArrayList();
		CloseableHttpClient connection = null;
		HttpGet method = null;
		try {
				String rsp =null;
			if( !apiConfig.isDemoUser()  ){	
				
				String vtkApiTroopUri = apiConfig.getVtkApiTroopUri();
				String url = apiConfig.getWebServicesUrl() + vtkApiTroopUri
						+ "?userId=" + contactId;
	
				method = new HttpGet(url); // no filters
				method.setHeader("Authorization", "OAuth " + getToken(apiConfig));
	
				connection = connectionFactory.getConnection();
				HttpResponse resp = connection.execute(method);
				int statusCode = resp.getStatusLine().getStatusCode();
	
				if (statusCode != HttpStatus.SC_OK) {
					
					throw new IllegalAccessException();
				}
				HttpEntity entity = resp.getEntity();
				entity.getContent();
				rsp= EntityUtils.toString(entity);
System.err.println("ALEXALEX TL: "+ rsp);		
				if(apiConfig.isUseAsDemo() )
					writeToFile(vtkDemoPath +"/vtkTroop_"+user.getName()+".json" , rsp);
			}else{
				String troopJsonFile= vtkDemoPath +"/vtkTroop_"+apiConfig.getDemoUserName()+".json";
				rsp= readFile(troopJsonFile).toString();
			}
			
		troops= troopInfo_parse(user, rsp, apiConfig);
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if( method!=null)
					method.releaseConnection();
			} catch (Exception eConn) {
				eConn.printStackTrace();
			}
		}

		if (user.isAdmin() && (troops == null || troops.size() <= 0)) {
			org.girlscouts.vtk.salesforce.Troop user_troop = new org.girlscouts.vtk.salesforce.Troop();
			user_troop.setPermissionTokens(Permission
					.getPermissionTokens(Permission.GROUP_ADMIN_PERMISSIONS));
			user_troop.setTroopId("none");
			
			if(  apiConfig.isDemoUser() ){
				user_troop.setCouncilCode(Integer.parseInt(vtkDemoCouncil));
			}else{
				user_troop.setCouncilCode(user.getAdminCouncilId());
			}

			user_troop.setTroopName("vtk_virtual_troop");
			// user.setPermissions(user_troop.getPermissionTokens());
			troops.add(user_troop);
		}
		return troops;
	}

	
	public java.util.List<Troop>  troopInfo_parse(User user, String rsp,ApiConfig apiConfig ) throws Exception{
		
		   java.util.List<Troop> troops = new java.util.ArrayList();
			
			rsp = "{\"records\":" + rsp + "}";
			JSONObject response = new JSONObject(rsp);
			log.debug("<<<<<Apex resp: " + response);

			JSONArray results = response.getJSONArray("records");
			for (int i = 0; i < results.length(); i++) {
				String errorTroopId="-1";
				String errorTroopName="X";
			  try{
				  
				java.util.Iterator itr = results.getJSONObject(i)
						.getJSONObject("Parent").keys();
				Troop troop = new Troop();
	
	
					troop.setTroopId(results.getJSONObject(i).getString("ParentId"));
					errorTroopId= troop.getTroopId();

					troop.setTroopName(results.getJSONObject(i).getJSONObject("Parent").getString("Name"));
					errorTroopName = troop.getTroopName();


					if(  apiConfig.isDemoUser() ){
						troop.setCouncilCode(Integer.parseInt(vtkDemoCouncil));
					}else{						
						troop.setCouncilCode(results.getJSONObject(i)

							.getJSONObject("Parent").getInt("Council_Code__c")); // girls
					}															// id
					troop.setCouncilId(results.getJSONObject(i)
							.getJSONObject("Parent").getString("Account__c"));
					troop.setGradeLevel(results.getJSONObject(i)
							.getJSONObject("Parent")
							.getString("Program_Grade_Level__c"));
//if( i==0){ troop.setGradeLevel("7-Multilevel"); }
					

					try {
						
						
						troop.setRole(results.getJSONObject(i).getString(
								"Job_Code__c"));

					} catch (Exception e) {
						e.printStackTrace();
					}

					org.girlscouts.vtk.auth.permission.RollType rollType = org.girlscouts.vtk.auth.permission.RollType
							.valueOf(troop.getRole());// "DP");

					troop.setPermissionTokens(Permission
							.getPermissionTokens(Permission.GROUP_GUEST_PERMISSIONS));

					if (rollType.getRollType().equals("PA")) {
						troop.getPermissionTokens()
								.addAll(Permission
										.getPermissionTokens(Permission.GROUP_MEMBER_1G_PERMISSIONS));

					}

					if (rollType.getRollType().equals("DP")) {
						troop.getPermissionTokens()
								.addAll(Permission
										.getPermissionTokens(Permission.GROUP_LEADER_PERMISSIONS));
					}

					if (rollType.getRollType().equals("CouncilAdmin")) {

						troop.getPermissionTokens()
								.addAll(Permission
										.getPermissionTokens(Permission.GROUP_MEMBER_COUNCIL_PERMISSIONS));

					}

					if (user.isAdmin()) {

						troop.getPermissionTokens()
								.addAll(Permission
										.getPermissionTokens(Permission.GROUP_ADMIN_PERMISSIONS));
					}

				
				troops.add(troop);
			  }catch(Exception ex){
				  log.error("Error int SalesForceDAO.troopInfo: found error while parsing troop response json from Salesforce. Ignoring this troop... ");
				  ex.printStackTrace();
				  try{
					  VtkError err= new VtkError();
					  err.setName("Error parsing Girl Scouts Troop");
					  err.setDescription("Error int SalesForceDAO.parseTroops- (leader): found error while parsing troop response json from Salesforce. Exception : " + ex.toString());
					  err.setUserFormattedMsg("There appears to be an error in the configuration of one of your troops.  Please notify support with error code VTK-" + errorTroopName + ".  Meanwhile, the application should still function correctly.");
					  err.setErrorCode("VTK-" + errorTroopId);
					  errors.add(err);
				  }catch(Exception e){e.printStackTrace();}
			  }
			}//edn for
		return troops;
	}
	
	private String getToken(ApiConfig apiConfig) {
		return apiConfig.getAccessToken();
	}

	public java.util.List<Contact> getTroopLeaderInfo(ApiConfig apiConfig,
			String sfTroopId) {

		CloseableHttpClient connection = null;

		java.util.List<Contact> contacts = new java.util.ArrayList();

		String vtkApiTroopLeadersUri = apiConfig.getVtkApiTroopLeadersUri();
		String url = apiConfig.getWebServicesUrl() + vtkApiTroopLeadersUri
				+ "?Troop_ID=" + sfTroopId;

		HttpGet method = new HttpGet(url);
		method.setHeader("Authorization", "OAuth " + apiConfig.getAccessToken());

		try {

			connection = connectionFactory.getConnection();

			CloseableHttpResponse resp = connection.execute(method);

			int statusCode = resp.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {

				

			}
			apiConfig.setLastTimeTokenRefreshed(java.util.Calendar.getInstance().getTimeInMillis() );
			HttpEntity entity = null;

			String rsp = null;

			try {

				entity = resp.getEntity();

				entity.getContent();

				rsp = EntityUtils.toString(entity);

				EntityUtils.consume(entity);

				method.releaseConnection();

				method = null;

			} finally {

				resp.close();

			}

			rsp = "{\"records\":" + rsp + "}";

			log.debug(">>>>> " + rsp);
			try {

				JSONObject response = new JSONObject(rsp);

				log.debug("<<<<<Apex contacts reponse: " + response);

				JSONArray results = response.getJSONArray("records");

				for (int i = 0; i < results.length(); i++) {

					log.debug("_____ " + results.get(i));

					Contact contact = new Contact();

					try {

						contact.setFirstName(results.getJSONObject(i)
								.getJSONObject("Contact")
								.getString("FirstName"));

						contact.setLastName(results.getJSONObject(i)
								.getJSONObject("Contact").getString("LastName"));

					} catch (Exception e) {

						e.printStackTrace();

					}

					contacts.add(contact);

				}

			} catch (JSONException e) {

				e.printStackTrace();

			}

		} catch (HttpException e) {

			

			e.printStackTrace();

		} catch (IOException e) {

			

			e.printStackTrace();

		} catch (Exception eG) {

		

			eG.printStackTrace();

		} finally {

			if (method != null)

				method.releaseConnection();

		}

		return contacts;

	}

	public java.util.List<Troop> getTroops_merged(User user,
			ApiConfig apiConfig, String contactId, JSONArray parentTroops) {
		
		
		java.util.List<Troop> troops_withAssociation = troopInfo(user,
				apiConfig, user.getSfUserId());		
		java.util.List<Troop> troops_withOutAssociation = parseParentTroops(user,
				parentTroops, apiConfig);			
		java.util.List<Troop> merged_troops = mergeTroops(
				troops_withAssociation, troops_withOutAssociation);
				
		return merged_troops;
	}

	public java.util.List<Troop> parseParentTroops(User user, JSONArray results, ApiConfig apiConfig) {
		
		java.util.List<Troop> troops = new java.util.ArrayList<Troop>();
		for (int i = 0; i < results.length(); i++) {
		  String errorTroopId = "-1";
		  String errorTroopName = "X";
		  try{
			Troop troop = new Troop();
	
			
				troop.setTroopId(results.getJSONObject(i).getString("Id"));
			
			errorTroopId = troop.getTroopId();
			troop.setTroopName(results.getJSONObject(i).getString("Name"));
			errorTroopName= troop.getTroopName();
				
			if(  apiConfig.isDemoUser() ){
				troop.setCouncilCode(Integer.parseInt(vtkDemoCouncil));
			}else{						
				troop.setCouncilCode(results.getJSONObject(i).getInt(
						"Council_Code__c"));
			}	
				
				
				troop.setCouncilId(results.getJSONObject(i).getString(
						"Account__c"));

			
				troop.setGradeLevel(results.getJSONObject(i).getString("Program_Grade_Level__c"));


				troop.setTroopName(results.getJSONObject(i).getString("Name"));
				troop.setRole("PA");

				org.girlscouts.vtk.auth.permission.RollType rollType = org.girlscouts.vtk.auth.permission.RollType
						.valueOf(troop.getRole());

				troop.setPermissionTokens(Permission
						.getPermissionTokens(Permission.GROUP_GUEST_PERMISSIONS));

				if (rollType.getRollType().equals("PA")) {
					troop.getPermissionTokens()
							.addAll(Permission
									.getPermissionTokens(Permission.GROUP_MEMBER_1G_PERMISSIONS));

				}

				if (rollType.getRollType().equals("DP")) {
					troop.getPermissionTokens()
							.addAll(Permission
									.getPermissionTokens(Permission.GROUP_LEADER_PERMISSIONS));
				}

				if (user.isAdmin()) {

					troop.getPermissionTokens()
							.addAll(Permission
									.getPermissionTokens(Permission.GROUP_ADMIN_PERMISSIONS));
				}

				troops.add(troop);
			
		  }catch(Exception ex){
			  ex.printStackTrace();
			  try{
				  VtkError err= new VtkError();
				  err.setName("Error parsing Girl Scouts Troop");
				  err.setDescription("Error int SalesForceDAO.parseTroops- (parent): found error while parsing troop response json from Salesforce. Exception : " + ex.toString());
				  err.setUserFormattedMsg("There appears to be an error in the configuration of one of your troops.  Please notify support with error code VTK-" + errorTroopName + ".  Meanwhile, the application should still function correctly.");
				  err.setErrorCode("VTK-"+ errorTroopId);
				  errors.add(err);
			  }catch(Exception e){e.printStackTrace();}
		  }
		}// end for

		return troops;

	}

	public java.util.List<Troop> mergeTroops(java.util.List<Troop> A,
			java.util.List<Troop> B) {

		if ((A == null && B == null) || (A.size() <= 0 && B.size() <= 0))
			return new java.util.ArrayList();
		if ((A == null || A.size() <= 0) && B != null)
			return B;
		if ((B == null || B.size() <= 0) && A != null)
			return A;

		java.util.List<Troop> troopDiff = getTroopsNotInA(A, B);
		A.addAll(troopDiff);

		for (int i = 0; i < A.size(); i++) {
			Troop troop = A.get(i);
			for (int y = 0; y < B.size(); y++) {
				Troop _troop = B.get(y);
				if (_troop.getTroopId().equals(troop.getTroopId())) {

					// merge permission into troop A
					try {
						if ("DP".equals(_troop.getRole())
								&& "PA".equals(troop.getRole())) {
							troop.setRole("DP");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					troop.getPermissionTokens().addAll(
							_troop.getPermissionTokens());

				}
			}
		}

		return A;
	}

	private java.util.List<Troop> getTroopsNotInA(java.util.List<Troop> A,
			java.util.List<Troop> B) {
		java.util.List<Troop> troopDiff = new java.util.ArrayList();
		for (int i = 0; i < B.size(); i++) {
			Troop troop = B.get(i);
			boolean isFound = false;
			fA: for (int y = 0; y < A.size(); y++) {
				Troop _troop = A.get(y);
				if (_troop.getTroopId().equals(troop.getTroopId())) {
					isFound = true;
					break fA;
				}
			}
			if (!isFound) {
				troopDiff.add(troop);
			}
		}
		return troopDiff;
	}
	
	
	public StringBuffer readFile(String fileName){
		try {
			InputStream is = session.getNode(fileName + "/jcr:content").getProperty("jcr:data").getBinary().getStream();
			String content = IOUtils.toString(new InputStreamReader(is, "UTF-8"));
			return new StringBuffer(content);
		} catch (RepositoryException re) {
			log.error("Cannot get file node: " + fileName + " due to RepositoryException.");
		} catch (IOException ie) {
			log.error("Cannot get file node: " + fileName + " due to IOException.");
		} catch (NullPointerException npe) {
			log.error("Cannot get file node: " + fileName + " due to NullPointerException.");
		}
		return new StringBuffer();
		
		
	}
	
	@SuppressWarnings("deprecation") // For Node.setProperty
	private void writeToFile( String fileName, String content) {
		try {
			String nodeName = fileName + "/jcr:content";
			if (!session.nodeExists(vtkDemoPath)) {
				JcrUtil.createPath(vtkDemoPath, "nt:unstructured", session);
				session.save();
			}
			if (!session.nodeExists(nodeName)) {
				JcrUtil.createPath(nodeName, false, "nt:file", "nt:resource", session, false);
			}
			InputStream is = IOUtils.toInputStream(content);
			Node node = session.getNode(fileName + "/jcr:content");
			node.setProperty("jcr:data", is);
			session.save();
		} catch (RepositoryException re) {
			log.error("Cannot get file node: " + fileName + " due to RepositoryException");
		}
		

		
	}
}// end class

