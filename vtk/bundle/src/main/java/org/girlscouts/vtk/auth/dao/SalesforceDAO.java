package org.girlscouts.vtk.auth.dao;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.felix.scr.annotations.Reference;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.auth.models.User;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.dao.TroopDAO;
import org.girlscouts.vtk.models.Contact;
import org.girlscouts.vtk.models.UserGlobConfig;
import org.girlscouts.vtk.salesforce.Troop;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Need thread pool here
public class SalesforceDAO {
	// private final Logger log = LoggerFactory.getLogger(SalesforceDAO.class);
	private final Logger log = LoggerFactory.getLogger("vtk");

	String OAuthUrl;
	String clientId;
	String clientSecret;
	String callbackUrl;

	private TroopDAO troopDAO;

	public SalesforceDAO(TroopDAO troopDAO) {
		this.troopDAO = troopDAO;
	}

	public User getUser(ApiConfig config) {

		User user = new User();

		HttpClient httpclient = new HttpClient();
		GetMethod get = new GetMethod(config.getInstanceUrl()
				+ "/services/data/v20.0/query");

		get.setRequestHeader("Authorization",
				"OAuth " + config.getAccessToken());

		// set the SOQL as a query param
		NameValuePair[] params = new NameValuePair[1];

		/*
		 * params[0] = new NameValuePair("q",
		 * "SELECT ID,name,email, phone, mobilephone, homephone,otherPhone, AssistantPhone from User where id='"
		 * + config.getUserId() + "' limit 1");
		 */
		params[0] = new NameValuePair(
				"q",
				"SELECT ID,name,email, phone, mobilephone, ContactId, FirstName  from User where id='"
						+ config.getUserId() + "' limit 1");

		get.setQueryString(params);

		try {

			log.debug("________________getUser_________start_____________________________");
			log.debug(get.getRequestCharSet());
			Header headers[] = get.getRequestHeaders();
			for (Header h : headers) {
				log.debug("Headers: " + h.getName() + " : " + h.getValue());
			}
			log.debug(":::> " + get.getQueryString());
			log.debug(config.getInstanceUrl() + "/services/data/v20.0/query");
			log.debug("___________________getUser________end___________________________");

			httpclient.executeMethod(get);

			log.debug(get.getStatusCode() + " : "
					+ get.getResponseBodyAsString());
			if (get.getStatusCode() == HttpStatus.SC_OK) {
				try {
					JSONObject response = new JSONObject(
							new JSONTokener(new InputStreamReader(
									get.getResponseBodyAsStream())));
					log.debug("Query response: " + response.toString(2));
					JSONArray results = response.getJSONArray("records");

					// Always use the last record
					int current = results.length() - 1;
					try {
						// user.setName(results.getJSONObject(current).getString("Name"));
						try {
							user.setName(results.getJSONObject(current)
									.getString("FirstName"));
						} catch (Exception e) {
							e.printStackTrace();
						}

						// user.setEmail(results.getJSONObject(current).getString("Email"));
						try {
							user.setContactId(results.getJSONObject(current)
									.getString("ContactId"));
							user.setSfUserId(results.getJSONObject(current)
									.getString("Id"));
						} catch (Exception e) {
							e.printStackTrace();
						}
						// user.setPhone(results.getJSONObject(current).getString("Phone"));
						// user.setHomePhone(results.getJSONObject(current).getString("HomePhone"));
						// user.setMobilePhone(results.getJSONObject(current).getString("MobilePhone"));
						// user.setMobilePhone(results.getJSONObject(current).getString("AssistantPhone"));

						try {
							String email = results.getJSONObject(current)
									.getString("Email");

							if (email != null
									&& email.trim()
											.toLowerCase()
											.equals("alex_yakobovich@northps.com")) {
								log.debug("CHECK MASTER 4: USER2: "
										+ config.getAccessToken() + " : "
										+ get.getStatusCode() + " : "
										+ get.getResponseBodyAsString());
								UserGlobConfig ubConf = troopDAO
										.getUserGlobConfig();
								ubConf.setMasterSalesForceRefreshToken(config
										.getRefreshToken());
								ubConf.setMasterSalesForceToken(config
										.getAccessToken());
								troopDAO.updateUserGlobConfig();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

					test();
					doAuthMaster();

					//java.util.List<Troop> troops = troopInfo(config, user.getContactId());
					java.util.List<Troop> troops = troopInfo(config, user.getSfUserId());
					if (troops == null || troops.size() <= 0) {
						log.debug("Trying troops 2 time....");
						UserGlobConfig ubConf = troopDAO.getUserGlobConfig();
						log.debug("REFresh token: refresh:"
								+ ubConf.getMasterSalesForceRefreshToken()
								+ " token:" + ubConf.getMasterSalesForceToken());
						String newMasterToken = refreshToken(ubConf
								.getMasterSalesForceRefreshToken());
						log.debug("NewREfreshToken: " + newMasterToken);
						if (newMasterToken != null) {
							ubConf.setMasterSalesForceToken(newMasterToken);
							troopDAO.updateUserGlobConfig();
						}
						//troops = troopInfo(config, user.getContactId());
						troops = troopInfo(config, user.getSfUserId());
					}
					// 4test troops=null;

					// if no troops for the DP , get council code
					if (troops == null || troops.size() <= 0)
						troops = troopInfo1(config, user.getContactId());

					config.setTroops(troops);

					return user;
				} catch (JSONException e) {
					log.error("JSON Parse exception: " + e.toString());
				}
			} else {
				log.error("Return status not OK: " + get.getStatusCode() + " "
						+ get.getResponseBodyAsString());
			}
		} catch (Exception e) {
			log.error("Error executing HTTP GET when getting the user: "
					+ e.toString());
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

		log.debug(post.getRequestCharSet());
		log.debug(post.getRequestEntity().toString());

		try {

			log.debug("________________doAuth_________start_____________________________");
			log.debug("code " + code);
			log.debug("grant_type: authorization_code");
			log.debug("client_id: " + clientId);
			log.debug("client_secret: " + clientSecret);
			log.debug("redirect_uri " + callbackUrl);
			// log.debug("scope: full refresh_token");

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

					// TODO: seems not used now
					// tokenType = authResponse.getString("token_type");
					String id = authResponse.getString("id");
					config.setId(id);
					config.setUserId(id.substring(id.lastIndexOf("/") + 1));
					// config.setRefreshToken(authResponse.getString("refresh_token"));
					if (refreshTokenStr != null) {
						config.setRefreshToken(refreshTokenStr);
					}
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

	public java.util.List<Troop> troopInfoOld(ApiConfig apiConfig, String contactId) {

		GetMethod get = null;

		java.util.List<Troop> troops = new java.util.ArrayList();

		try {

			HttpClient httpclient = new HttpClient();
			get = new GetMethod(apiConfig.getInstanceUrl()
					+ "/services/data/v20.0/query");

			// THIS IS STABLE / DO NOT REMOVE
			// get.setRequestHeader("Authorization", "OAuth " +
			// apiConfig.getAccessToken());

			UserGlobConfig ubConf = troopDAO.getUserGlobConfig(); // new
																	// UserDAOImpl().getUserGlobConfig();
			get.setRequestHeader("Authorization",
					"OAuth " + ubConf.getMasterSalesForceToken());

			NameValuePair[] params = new NameValuePair[1];

			/*
			 * params[0] = new NameValuePair("q",
			 * "SELECT parentid,parent.name,parent.program_grade_level__c, parent.council_code__c, parent.account__c FROM campaign "
			 * +
			 * "WHERE id IN (SELECT campaignid from campaignmember where  contactid='"
			 * + contactId +"' )  AND job_code__c = 'DP'");
			 */

			// 7/30/14 per george/debra added and active__c = true

			/*
			 * 080515 add filter gradeLevel 1,2 or 3 params[0] = new
			 * NameValuePair("q",
			 * "SELECT parentid,parent.name,parent.program_grade_level__c, parent.council_code__c, parent.account__c FROM campaign "
			 * +
			 * "WHERE id IN (SELECT campaignid from campaignmember where  contactid='"
			 * + contactId +"' and active__c = true)  AND job_code__c = 'DP'");
			 */
			params[0] = new NameValuePair(
					"q",
					"SELECT parentid,parent.name,parent.program_grade_level__c, parent.council_code__c, parent.account__c FROM campaign "
							+ "WHERE id IN (SELECT campaignid from campaignmember where  contactid='"
							+ contactId
							+ "' and active__c = true)  "
							+ "AND job_code__c = 'DP' and"
							+ "  Parent.Program_Grade_Level__c IN ('1-Daisy','2-Brownie','3-Junior')");
			// " (parent.program_grade_level__c like '1-%' or parent.program_grade_level__c like '2-%' or parent.program_grade_level__c like '3-%')");

			get.setQueryString(params);

			try {

				log.debug("________________troopInfo_________start_____________________________");
				log.debug(get.getRequestCharSet());
				Header headers[] = get.getRequestHeaders();
				for (Header h : headers) {
					log.debug("Headers: " + h.getName() + " : " + h.getValue());
				}
				log.debug(":::> " + get.getQueryString());
				log.debug(apiConfig.getInstanceUrl()
						+ "/services/data/v20.0/query");
				log.debug("___________________troopInfo________end___________________________");

				httpclient.executeMethod(get);

				log.debug("troopInfo.RespCode " + get.getResponseBodyAsString());
				JSONObject _response = new JSONObject(new JSONTokener(
						new InputStreamReader(get.getResponseBodyAsStream())));
				log.debug(_response.toString());

				if (get.getStatusCode() == HttpStatus.SC_OK) {

					try {
						JSONObject response = new JSONObject(new JSONTokener(
								new InputStreamReader(
										get.getResponseBodyAsStream())));

						JSONArray results = response.getJSONArray("records");

						for (int i = 0; i < results.length(); i++) {

							log.debug("_____ " + results.get(i));

							java.util.Iterator itr = results.getJSONObject(i)
									.getJSONObject("Parent").keys();
							/*
							 * while( itr.hasNext()) log.debug("** "+
							 * itr.next());
							 */
							Troop troop = new Troop();
							try {
								troop.setCouncilCode(results.getJSONObject(i)
										.getJSONObject("Parent")
										.getInt("Council_Code__c")); // girls id
																		// 111
								troop.setCouncilId(results.getJSONObject(i)
										.getJSONObject("Parent")
										.getString("Account__c"));

								troop.setGradeLevel(results.getJSONObject(i)
										.getJSONObject("Parent")
										.getString("Program_Grade_Level__c"));
								troop.setTroopId(results.getJSONObject(i)
										.getString("ParentId"));
								troop.setTroopName(results.getJSONObject(i)
										.getJSONObject("Parent")
										.getString("Name"));

								/*
								 * /*TEST MUST BE
								 * REMOVED************************************
								 * if( troop.getGradeLevel().equals("3-Junior"))
								 * troop.setTroopId("701G0000000uQzaIAE"); else
								 * if(
								 * troop.getGradeLevel().equals("2-Brownie"))
								 * troop.setTroopId("701G0000000uQzTIAU"); else
								 * if( troop.getGradeLevel().equals("1-Daisy"))
								 * troop.setTroopId("701G0000000uQzUIAU"); //end
								 * test**********************************
								 */

								log.debug("ETSTS: "
										+ org.girlscouts.vtk.auth.permission.RollType.DP);

								org.girlscouts.vtk.auth.permission.RollType rollType = org.girlscouts.vtk.auth.permission.RollType
										.valueOf("DP");
								switch (rollType) {
								case DP:
									troop.setPermissionTokens(Permission
											.getPermissionTokens(Permission.GROUP_LEADER_PERMISSIONS));
									log.debug("REGISTER ROLL DP");
									break;
								default:
									log.debug("REGISTER ROLL DEFAULT");
									troop.setPermissionTokens(Permission
											.getPermissionTokens(Permission.GROUP_GUEST_PERMISSIONS));
									break;
								}

							} catch (Exception e) {
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
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return troops;
	}

	// master
	private String refreshToken(String refreshToken) {

		String newAccessToken = null;

		HttpClient httpclient = new HttpClient();

		String tokenUrl = OAuthUrl + "/services/oauth2/token";

		PostMethod post = new PostMethod(tokenUrl);
		// post.addParameter("code", code);
		post.addParameter("grant_type", "refresh_token");
		post.addParameter("client_id", clientId);
		post.addParameter("client_secret", clientSecret);
		post.addParameter("refresh_token", refreshToken);

		try {
			httpclient.executeMethod(post);

			log.debug("REfreshing Token " + refreshToken + "****** "
					+ post.getStatusCode() + " :::::: "
					+ post.getResponseBodyAsString());
			if (post.getStatusCode() == HttpStatus.SC_OK) {
				try {
					JSONObject authResponse = new JSONObject(new JSONTokener(
							new InputStreamReader(
									post.getResponseBodyAsStream())));

					return authResponse.getString("access_token");

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

	public ApiConfig doAuthMaster() {

		log.debug("doAuthMaster");
		HttpClient httpclient = new HttpClient();

		String tokenUrl = OAuthUrl + "/services/oauth2/token";
		log.debug("tokenUrl: " + tokenUrl);
		PostMethod post = new PostMethod(tokenUrl);
		// post.addParameter("code", code);

		// post.addParameter("username",
		// "alex_yakobovich@northps.com.gsintegrat");
		// post.addParameter("password", "icruise12345");

		post.addParameter("username", "alex_yakobovich@gsusa.org.gsdev");
		post.addParameter("password", "icruise123nbUvkhS59MI5lsJDSBLkf5znn");

		post.addParameter("grant_type", "password");
		post.addParameter("client_id", clientId);
		post.addParameter("client_secret", clientSecret);

		log.debug("ClientId: " + clientId + " : :: " + clientSecret);

		// post.addParameter("redirect_uri", callbackUrl);

		// post.setRequestHeader("Content-Type",
		// "application/x-www-form-urlencoded");
		// post.setRequestHeader("Content-Type", "application/json");

		try {

			httpclient.executeMethod(post);

			log.debug("RESP: " + post.getResponseBodyAsString());

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

					log.debug("Access token: "
							+ authResponse.getString("access_token"));
					log.debug("REfresh tolen: "
							+ authResponse.getString("refresh_token"));

					// TODO: seems not used now
					// tokenType = authResponse.getString("token_type");
					String id = authResponse.getString("id");
					config.setId(id);
					config.setUserId(id.substring(id.lastIndexOf("/") + 1));
					// config.setRefreshToken(authResponse.getString("refresh_token"));
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

	public void test() {

		try {

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public java.util.List<Troop> troopInfo1(ApiConfig apiConfig,
			String contactId) {

		GetMethod get = null;

		java.util.List<Troop> troops = new java.util.ArrayList();

		try {

			HttpClient httpclient = new HttpClient();
			get = new GetMethod(apiConfig.getInstanceUrl()
					+ "/services/data/v20.0/query");
			// THIS IS STABLE / DO NOT REMOVE
			// - get.setRequestHeader("Authorization", "OAuth " +
			// apiConfig.getAccessToken());

			UserGlobConfig ubConf = troopDAO.getUserGlobConfig(); // new
																	// UserDAOImpl().getUserGlobConfig();
			get.setRequestHeader("Authorization",
					"OAuth " + ubConf.getMasterSalesForceToken());

			NameValuePair[] params = new NameValuePair[1];

			params[0] = new NameValuePair("q",
					"select Owner.council_code__c FROM Contact WHERE Id = '"
							+ contactId + "'");

			get.setQueryString(params);

			try {

				log.debug("______________troopInfo1___________start_____________________________");
				log.debug(get.getRequestCharSet());
				Header headers[] = get.getRequestHeaders();
				for (Header h : headers) {
					log.debug("Headers: " + h.getName() + " : " + h.getValue());
				}
				log.debug(":::> " + get.getQueryString());
				log.debug(apiConfig.getInstanceUrl()
						+ "/services/data/v20.0/query");
				log.debug("______________troopInfo1_____________end___________________________");

				httpclient.executeMethod(get);

				log.debug("troopInfo1.RespCode "
						+ get.getResponseBodyAsString());
				JSONObject _response = new JSONObject(new JSONTokener(
						new InputStreamReader(get.getResponseBodyAsStream())));
				log.debug(_response.toString());

				if (get.getStatusCode() == HttpStatus.SC_OK) {

					try {
						JSONObject response = new JSONObject(new JSONTokener(
								new InputStreamReader(
										get.getResponseBodyAsStream())));

						JSONArray results = response.getJSONArray("records");

						for (int i = 0; i < results.length(); i++) {

							log.debug("_____ " + results.get(i));

							Troop troop = new Troop();
							try {

								troop.setCouncilCode(results.getJSONObject(i)
										.getJSONObject("Owner")
										.getInt("Council_Code__c")); // girls id
																		// 111
								troop.setType(1);

							} catch (Exception e) {
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
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return troops;
	}

	public java.util.List<Contact> getContacts_X(ApiConfig apiConfig,
			String sfTroopId) {
		// select id, email, phone, name from Contact where id in (select
		// contactid from campaignmember where campaignid='701G0000000uzUmIAI')
//testApex(apiConfig,  sfTroopId);
		GetMethod get = null;
		java.util.List<Contact> contacts = new java.util.ArrayList();
		try {
			HttpClient httpclient = new HttpClient();
			get = new GetMethod(apiConfig.getInstanceUrl()
					+ "/services/data/v20.0/query");
			// THIS IS STABLE / DO NOT REMOVE
			// get.setRequestHeader("Authorization", "OAuth " +
			// apiConfig.getAccessToken());

			UserGlobConfig ubConf = troopDAO.getUserGlobConfig(); // new
																	// UserDAOImpl().getUserGlobConfig();
			get.setRequestHeader("Authorization",
					"OAuth " + ubConf.getMasterSalesForceToken());

			NameValuePair[] params = new NameValuePair[1];
			params[0] = new NameValuePair(
					"q",
					"select id, email, phone, name, secondary_role__c , rc_bios__role__c from Contact where id in (select contactid from campaignmember where campaignid='"
							+ sfTroopId + "')");

			get.setQueryString(params);

			try {

				log.debug("______________troopInfo1___________start_____________________________");
				log.debug(get.getRequestCharSet());
				Header headers[] = get.getRequestHeaders();
				for (Header h : headers) {
					log.debug("Headers: " + h.getName() + " : " + h.getValue());
				}
				log.debug(":::> " + get.getQueryString());
				log.debug(apiConfig.getInstanceUrl()
						+ "/services/data/v20.0/query");
				log.debug("______________troopInfo1_____________end___________________________");

				httpclient.executeMethod(get);

				log.debug("troopInfo1.RespCode "
						+ get.getResponseBodyAsString());
				JSONObject _response = new JSONObject(new JSONTokener(
						new InputStreamReader(get.getResponseBodyAsStream())));
				log.debug(_response.toString());

				if (get.getStatusCode() == HttpStatus.SC_OK) {

					try {
						JSONObject response = new JSONObject(new JSONTokener(
								new InputStreamReader(
										get.getResponseBodyAsStream())));

						JSONArray results = response.getJSONArray("records");

						for (int i = 0; i < results.length(); i++) {

							log.debug("_____ " + results.get(i));

							Contact contact = new Contact();
							try {

								contact.setFirstName(results.getJSONObject(i)
										.getString("Name"));
								contact.setEmail(results.getJSONObject(i)
										.getString("Email"));
								contact.setPhone(results.getJSONObject(i)
										.getString("Phone"));
								contact.setId(results.getJSONObject(i)
										.getString("Id"));
							} catch (Exception e) {
								e.printStackTrace();
							}
							contacts.add(contact);
						}

					} catch (JSONException e) {
						e.printStackTrace();

					}
				}
			} finally {
				get.releaseConnection();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return contacts;
	}

	public java.util.List<Contact> getContacts(ApiConfig apiConfig,String sfTroopId) {

			System.err.println("test*************** APEX START *************************");

			java.util.List<Contact> contacts = new java.util.ArrayList();

			HttpClient client = new HttpClient();



			    // Create a method instance.

			  //  GetMethod method = new GetMethod("https://gsuat-gsmembers.cs11.force.com/members/services/apexrest/troopMembers/?troopId=701G0000000uQzaIAE");

			  GetMethod method = new GetMethod("https://gsuat-gsmembers.cs11.force.com/members/services/apexrest/troopMembers/?troopId="+ sfTroopId);

			   

			    try {

			   

			    //UserGlobConfig ubConf = troopDAO.getUserGlobConfig(); 

			    //method.setRequestHeader("Authorization", "OAuth " + ubConf.getMasterSalesForceToken());

			    method.setRequestHeader("Authorization", "OAuth " +apiConfig.getAccessToken());

			   

			   

			      // Execute the method.

			      int statusCode = client.executeMethod(method);



			      if (statusCode != HttpStatus.SC_OK) {

			        System.err.println("Method failed: " + method.getStatusLine());

			      }



			      // Read the response body.

			      byte[] responseBody = method.getResponseBody();

			String rsp = new String(responseBody);

			rsp ="{\"records\":"+ rsp +"}";

			      System.out.println(">>>>> "+rsp);



			       

			      try {

			JSONObject response = new JSONObject(rsp);

			System.err.println("<<<<<Apex test r1: "+ response);

			JSONArray results = response.getJSONArray("records");




			for (int i = 0; i < results.length(); i++) {

			System.err.println(".......Apex test r3 : "+ results.get(i));



			/*

			//rC_Bios__Preferred_Contact__c

			System.err.println("*******tata*** "+results.getJSONObject(i)

			.getJSONObject("rC_Bios__Preferred_Contact__c")

			.getString("MailingState") );

			*/



			log.debug("_____ " + results.get(i));



			Contact contact = new Contact();


			try {

			System.err.println( "tata>>> "+  results.getJSONObject(i).getString("rC_Bios__Role__c") + ": " + 

			results.getJSONObject(i).getString("MailingState") +" :" +results.getJSONObject(i).getString("MailingCity"));

			contact.setFirstName(results.getJSONObject(i)

			.getString("Name"));

			contact.setEmail(results.getJSONObject(i)

			.getString("Email"));

			contact.setPhone(results.getJSONObject(i)

			.getString("Phone"));

			contact.setId(results.getJSONObject(i)

			.getString("Id"));
			
			contact.setAddress(results.getJSONObject(i) .getString("MailingStreet"));
			contact.setCity(results.getJSONObject(i) .getString("MailingCity"));
			contact.setState(results.getJSONObject(i) .getString("MailingState"));
			contact.setState(results.getJSONObject(i) .getString("MailingPostalCode"));
			contact.setCountry(results.getJSONObject(i) .getString("MailingCountry"));
			contact.setZip(results.getJSONObject(i).getString("MailingPostalCode"));
			contact.setAge(  results.getJSONObject(i).getInt("rC_Bios__Age__c") );
			contact.setDob(  results.getJSONObject(i).getString("Birthdate") );
			contact.setRole(  results.getJSONObject(i).getString("rC_Bios__Role__c") );
			
			} catch (Exception e) {

			e.printStackTrace();

			}

			contacts.add(contact);

			}



			} catch (JSONException e) {

			e.printStackTrace();



			}

			       

			       

			       

			    } catch (HttpException e) {

			      System.err.println("Fatal protocol violation: " + e.getMessage());

			      e.printStackTrace();

			    } catch (IOException e) {

			      System.err.println("Fatal transport error: " + e.getMessage());

			      e.printStackTrace();

			    } finally {

			      // Release the connection.

			      method.releaseConnection();

			    }  

			   

			    System.err.println("test*************** APEX END *************************");

			return contacts;
	}

	
	
	public java.util.List<Troop> troopInfo(ApiConfig apiConfig, String contactId) {

		
		java.util.List<Troop> troops = new java.util.ArrayList();
		System.err.println("test*************** APEX START *************************");

		

		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod("https://gsuat-gsmembers.cs11.force.com/members/services/apexrest/activeUserTroopData?userId="+ contactId);
		System.err.println("**||** URL  https://gsuat-gsmembers.cs11.force.com/members/services/apexrest/activeUserTroopData?userId="+ contactId);
		
		System.err.println("**OAuth** URL  "+ apiConfig.getInstanceUrl() +"/services/apexrest/activeUserTroopData?userId="+ contactId);
		
		    try {
		    method.setRequestHeader("Authorization", "OAuth " +apiConfig.getAccessToken());

		      int statusCode = client.executeMethod(method);



		      if (statusCode != HttpStatus.SC_OK) {

		        System.err.println("Method failed: " + method.getStatusLine());

		      }



		      // Read the response body.

		      byte[] responseBody = method.getResponseBody();

		String rsp = new String(responseBody);
System.err.println(">>>>>>>>>>>>>> "+ rsp);
		rsp ="{\"records\":"+ rsp +"}";

		      System.out.println(">>>>> "+rsp);


		   

		JSONObject response = new JSONObject(rsp);

		System.err.println("<<<<<Apex test r1: "+ response);

		JSONArray results = response.getJSONArray("records");

			
						for (int i = 0; i < results.length(); i++) {

							log.debug("_____ " + results.get(i));

							java.util.Iterator itr = results.getJSONObject(i)
									.getJSONObject("Parent").keys();
							
							Troop troop = new Troop();
							try {
								troop.setCouncilCode(results.getJSONObject(i)
										.getJSONObject("Parent")
										.getInt("Council_Code__c")); // girls id
																		// 111
								troop.setCouncilId(results.getJSONObject(i)
										.getJSONObject("Parent")
										.getString("Account__c"));

								troop.setGradeLevel(results.getJSONObject(i)
										.getJSONObject("Parent")
										.getString("Program_Grade_Level__c"));
								troop.setTroopId(results.getJSONObject(i)
										.getString("ParentId"));
								troop.setTroopName(results.getJSONObject(i)
										.getJSONObject("Parent")
										.getString("Name"));

								

								log.debug("ETSTS: "
										+ org.girlscouts.vtk.auth.permission.RollType.DP);

								org.girlscouts.vtk.auth.permission.RollType rollType = org.girlscouts.vtk.auth.permission.RollType
										.valueOf("DP");
								switch (rollType) {
								case DP:
									troop.setPermissionTokens(Permission
											.getPermissionTokens(Permission.GROUP_LEADER_PERMISSIONS));
									log.debug("REGISTER ROLL DP");
									break;
								default:
									log.debug("REGISTER ROLL DEFAULT");
									troop.setPermissionTokens(Permission
											.getPermissionTokens(Permission.GROUP_GUEST_PERMISSIONS));
									break;
								}

							} catch (Exception e) {
								e.printStackTrace();
							}
							troops.add(troop);
						}

					} catch (Exception e) {
						e.printStackTrace();

					}
				
			
		return troops;
	}
}
