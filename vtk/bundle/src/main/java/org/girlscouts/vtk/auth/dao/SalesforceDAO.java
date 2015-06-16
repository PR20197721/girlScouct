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
	private final Logger log = LoggerFactory.getLogger("vtk");
	String OAuthUrl;
	String clientId;
	String clientSecret;
	String callbackUrl;
	private TroopDAO troopDAO;
	private ConnectionFactory connectionFactory;

	public SalesforceDAO(TroopDAO troopDAO, ConnectionFactory connectionFactory) {
		this.troopDAO = troopDAO;
		this.connectionFactory = connectionFactory;
	}

	public User getUser(ApiConfig config) {
		User user = new User();
		HttpClient httpclient = new HttpClient();
		GetMethod get = new GetMethod(config.getInstanceUrl()
				+ "/services/data/v20.0/query");
		get.setRequestHeader("Authorization",
				"OAuth " + config.getAccessToken());
		NameValuePair[] params = new NameValuePair[1];
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
						try {
							user.setName(results.getJSONObject(current)
									.getString("FirstName"));
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							user.setEmail(results.getJSONObject(current)
									.getString("Email"));
						} catch (Exception e) {
							System.err
									.println("SAlesforceDAO.getUser: no email");
						}
						try {
							user.setPhone(results.getJSONObject(current)
									.getString("Phone"));
						} catch (Exception e) {
							System.err
									.println("SAlesforceDAO.getUser: no phone");
						}

						try {
							user.setContactId(results.getJSONObject(current)
									.getString("ContactId"));
							user.setSfUserId(results.getJSONObject(current)
									.getString("Id"));
						} catch (Exception e) {
							e.printStackTrace();
						}

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
					java.util.List<Troop> troops = troopInfo(config,
							user.getSfUserId());
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
		CloseableHttpClient connection = null;
		java.util.List<Contact> contacts = new java.util.ArrayList();
		HttpGet method = new HttpGet(apiConfig.getWebServicesUrl()
				+ "/services/apexrest/troopMembers/?troopId=" + sfTroopId);
		method.setHeader("Authorization", "OAuth " + getToken(apiConfig));
		try {
			connection = connectionFactory.getConnection();
			CloseableHttpResponse resp = connection.execute(method);
			int statusCode = resp.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + resp.getStatusLine());
			}
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
						try{contact.setFirstName(results.getJSONObject(i)
								.getString("Name"));
						}catch(Exception e){}
						
						try{contact.setEmail(results.getJSONObject(i).getString(
								"Email"));
						}catch(Exception e){}
						
						
						try{contact.setPhone(results.getJSONObject(i).getString(
								"Phone"));
						}catch(Exception e){}
						
						
						try{contact.setId(results.getJSONObject(i).getString("Id"));
						}catch(Exception e){}
						
						
						try{contact.setAddress(results.getJSONObject(i).getString(
								"MailingStreet"));
						}catch(Exception e){}
						
						
						try{contact.setCity(results.getJSONObject(i).getString(
								"MailingCity"));
						}catch(Exception e){}
						
						
						try{contact.setState(results.getJSONObject(i).getString(
								"MailingState"));
						}catch(Exception e){}
						
						
						try{contact.setState(results.getJSONObject(i).getString(
								"MailingPostalCode"));
						}catch(Exception e){}
						
						
						try{contact.setCountry(results.getJSONObject(i).getString(
								"MailingCountry"));
						}catch(Exception e){}
						
						try{contact.setZip(results.getJSONObject(i).getString(
								"MailingPostalCode"));
						}catch(Exception e){}
						
						try{contact.setAge(results.getJSONObject(i).getInt(
								"rC_Bios__Age__c"));
						}catch(Exception e){}
						
						
						try{contact.setDob(results.getJSONObject(i).getString(
								"Birthdate"));
						}catch(Exception e){}
						
						
						try{contact.setRole(results.getJSONObject(i).getString(
								"rC_Bios__Role__c"));
						}catch(Exception e){}
						
						
						try{contact.setAccountId(results.getJSONObject(i)
								.getString("AccountId"));
						}catch(Exception e){}
						
						
						try{contact.setContactId(results.getJSONObject(i)
								.getString("Id"));
						}catch(Exception e){}
						
						
						contact.setType(0);
						Contact contactSub = new Contact();
						try{contactSub.setEmail(results.getJSONObject(i)
								.getJSONObject("Account")
								.getJSONObject("rC_Bios__Preferred_Contact__r")
								.getString("Email"));
						}catch(Exception e){}
						
						
						try{contactSub.setFirstName(results.getJSONObject(i)
								.getJSONObject("Account")
								.getJSONObject("rC_Bios__Preferred_Contact__r")
								.getString("FirstName"));
						}catch(Exception e){}
						
						
						try{contactSub.setLastName(results.getJSONObject(i)
								.getJSONObject("Account")
								.getJSONObject("rC_Bios__Preferred_Contact__r")
								.getString("LastName"));
						}catch(Exception e){}
						
						
						try{contactSub.setAccountId(results.getJSONObject(i)
								.getJSONObject("Account").getString("Id"));
						
						}catch(Exception e){}
						
						
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
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception eG) {
			System.err.println("Fatal transport error: " + eG.getMessage());
			eG.printStackTrace();
		} finally {
			if (method != null)
				method.releaseConnection();
		}
		return contacts;
	}

	public java.util.List<Troop> troopInfo(ApiConfig apiConfig, String contactId) {
		java.util.List<Troop> troops = new java.util.ArrayList();
		log.debug("**OAuth** troopInfo URL  " + apiConfig.getWebServicesUrl()
				+ "/services/apexrest/activeUserTroopData?userId=" + contactId);
		CloseableHttpClient connection = null;
		HttpGet method = null;
		try {
			method = new HttpGet(apiConfig.getWebServicesUrl()
					+ "/services/apexrest/activeUserTroopData?userId="
					+ contactId);
			method.setHeader("Authorization", "OAuth " + getToken(apiConfig));
			connection = connectionFactory.getConnection();
			HttpResponse resp = connection.execute(method);
			int statusCode = resp.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + resp.getStatusLine());
			}
			HttpEntity entity = resp.getEntity();
			entity.getContent();
			String rsp = EntityUtils.toString(entity);
			rsp = "{\"records\":" + rsp + "}";
			JSONObject response = new JSONObject(rsp);
			log.debug("<<<<<Apex resp: " + response);
			JSONArray results = response.getJSONArray("records");
			for (int i = 0; i < results.length(); i++) {
				java.util.Iterator itr = results.getJSONObject(i)
						.getJSONObject("Parent").keys();
				Troop troop = new Troop();
				try {
					troop.setCouncilCode(results.getJSONObject(i)
							.getJSONObject("Parent").getInt("Council_Code__c")); // girls
																					// id
					troop.setCouncilId(results.getJSONObject(i)
							.getJSONObject("Parent").getString("Account__c"));
					troop.setGradeLevel(results.getJSONObject(i)
							.getJSONObject("Parent")
							.getString("Program_Grade_Level__c"));
					troop.setTroopId(results.getJSONObject(i).getString(
							"ParentId"));
					troop.setTroopName(results.getJSONObject(i)
							.getJSONObject("Parent").getString("Name"));
					
					try{
						troop.setRole(results.getJSONObject(i).getString(
							"Job_Code__c"));
					}catch(Exception e){e.printStackTrace(); troop.setRole("DP");}
					
					
					
					log.debug("User Roll: "
							+ org.girlscouts.vtk.auth.permission.RollType.DP);
					org.girlscouts.vtk.auth.permission.RollType rollType = org.girlscouts.vtk.auth.permission.RollType
							.valueOf(troop.getRole());// "DP");
					
					  try { 
						  if (contactId.equals("005Z0000002J5CYIA0")) {
					  rollType = org.girlscouts.vtk.auth.permission.RollType.valueOf("PA"); 
					  troop.setCouncilCode(603); 
					  // TO BE REMOVED : only 4 // test
					  if(troop.getTroopId().equals("701Z0000000gvSKIAY")) {
					  troop.setTroopId("701G0000000uQzTIAU");
					  troop.setTroopName("Troop 603104");
					  troop.setGradeLevel("2-Brownie"); } } 
						  } catch (Exception
					  nn) { nn.printStackTrace(); }
					 
					switch (rollType) {
					case PA:
						troop.setPermissionTokens(Permission
								.getPermissionTokens(Permission.GROUP_MEMBER_1G_PERMISSIONS));
						log.debug("REGISTER ROLL PA= parent");
						break;
					case DP:
						troop.setPermissionTokens(Permission
								.getPermissionTokens(Permission.GROUP_LEADER_PERMISSIONS));
						log.debug("REGISTER ROLL DP");
						break;
					case CouncilAdmin:
						troop.setPermissionTokens(Permission
								.getPermissionTokens(Permission.GROUP_LEADER_PERMISSIONS));
						troop.getPermissionTokens()
								.addAll(Permission
										.getPermissionTokens(Permission.GROUP_ADMIN_PERMISSIONS));
						log.debug("Council Admin");
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
		} finally {
			try {
				method.releaseConnection();
			} catch (Exception eConn) {
				eConn.printStackTrace();
			}
		}
		return troops;
	}

	private String getToken(ApiConfig apiConfig) {
		java.util.Calendar validTokenTime = java.util.Calendar.getInstance();
		validTokenTime.add(java.util.Calendar.MINUTE, -1);
		if (validTokenTime.getTimeInMillis() > apiConfig
				.getLastTimeTokenRefreshed()) {
			apiConfig = refreshToken(apiConfig);
			log.info("Refreshing Salesforce token");
		}
		return apiConfig.getAccessToken();
	}
}
