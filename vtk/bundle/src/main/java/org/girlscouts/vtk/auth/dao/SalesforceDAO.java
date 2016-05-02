package org.girlscouts.vtk.auth.dao;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.codec.binary.Base64;

import java.util.Dictionary;
import java.util.Set;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.auth.models.User;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.dao.TroopDAO;
import org.girlscouts.vtk.ejb.ConnectionFactory;
import org.girlscouts.vtk.ejb.VtkError;
import org.girlscouts.vtk.helpers.ConfigManager;
import org.girlscouts.vtk.models.Contact;
import org.girlscouts.vtk.models.UserGlobConfig;
import org.girlscouts.vtk.salesforce.Troop;
import org.girlscouts.vtk.sso.saml.OAuthRequest;
import org.girlscouts.vtk.sso.saml.Utils;
import org.girlscouts.vtk.utils.VtkException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

// TODO: Need thread pool here
public class SalesforceDAO {
	private final Logger log = LoggerFactory.getLogger("vtk");
	String OAuthUrl;
	String clientId;
	String clientSecret;
	String callbackUrl;
	private TroopDAO troopDAO;
	private ConnectionFactory connectionFactory;
	private java.util.List<VtkError> errors;
	
	public SalesforceDAO(TroopDAO troopDAO, ConnectionFactory connectionFactory) {
		this.troopDAO = troopDAO;
		this.connectionFactory = connectionFactory;
		this.errors = new java.util.ArrayList<VtkError>();
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
			connection = connectionFactory.getConnection();
			CloseableHttpResponse resp = connection.execute(method);
			int statusCode = resp.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + resp.getStatusLine());
//System.err.println(EntityUtils.toString(resp.getEntity().getContent()));	
				//throw new IllegalAccessException();
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

			log.debug(">>>>> " + rsp);
System.err.println(rsp);			
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
								user.setAdminCouncilId(contactJson
										.getJSONObject("Owner").getInt(
												"Council_Code__c"));
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
System.err.println("URL contact: "+ url);		
url= apiConfig.getWebServicesUrl() + "/services/apexrest/troopMembersV2?troopId=" + sfTroopId;
//System.err.println("Url contact v2: "+ url);

HttpGet method = new HttpGet(url);
		method.setHeader("Authorization", "OAuth " + getToken(apiConfig));
		try {
			connection = connectionFactory.getConnection();
			CloseableHttpResponse resp = connection.execute(method);
			int statusCode = resp.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + resp.getStatusLine());
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
System.err.println("RESP CONTACT 1: " + response);		

java.util.Map <String, Boolean> renewals = new java.util.TreeMap();
JSONArray results1 = response.getJSONObject("records").getJSONArray("lstCM");
for (int i = 0; i < results1.length(); i++) {
	try {
		try {
			renewals.put( results1.getJSONObject(i).getString("ContactId"),  results1.getJSONObject(i).getBoolean("Display_Renewal__c"));
		} catch (Exception e) {
		}
	}catch(Exception ex){ex.printStackTrace();}
}//edn for






				JSONArray results = response.getJSONObject("records").getJSONArray("lstCon");
				
				for (int i = 0; i < results.length(); i++) {
					log.debug("_____ " + results.get(i));
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
							contact.setState(results.getJSONObject(i)
									.getString("MailingPostalCode"));
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
								Boolean isRenewal = renewals.get( contact.getId() );
								contact.setRenewalDue(isRenewal ==null ? false : isRenewal);
						} catch (Exception e) {
						}
						
						
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
						/*
						 * try{contactSub.setAccountId(results.getJSONObject(i)
						 * .getJSONObject("Account").getString("Id"));
						 * 
						 * }catch(Exception e){}
						 */

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

	public java.util.List<Troop> troopInfo(User user, ApiConfig apiConfig,
			String contactId) {
		java.util.List<Troop> troops = new java.util.ArrayList();
		CloseableHttpClient connection = null;
		HttpGet method = null;
		try {
			String vtkApiTroopUri = apiConfig.getVtkApiTroopUri();
			String url = apiConfig.getWebServicesUrl() + vtkApiTroopUri
					+ "?userId=" + contactId;

			method = new HttpGet(url); // no filters
			method.setHeader("Authorization", "OAuth " + getToken(apiConfig));

			connection = connectionFactory.getConnection();
			HttpResponse resp = connection.execute(method);
			int statusCode = resp.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + resp.getStatusLine());
				throw new IllegalAccessException();
			}
			apiConfig.setLastTimeTokenRefreshed(java.util.Calendar.getInstance().getTimeInMillis() );
			HttpEntity entity = resp.getEntity();
			entity.getContent();
			String rsp = EntityUtils.toString(entity);
			rsp = "{\"records\":" + rsp + "}";
			JSONObject response = new JSONObject(rsp);
			log.debug("<<<<<Apex resp: " + response);
System.err.println(">>>>>>>>>>>>>test>>>>>>  "+response);
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
//if(i==0)throw new VtkException("test");	
					troop.setCouncilCode(results.getJSONObject(i)
							.getJSONObject("Parent").getInt("Council_Code__c")); // girls
																					// id
					troop.setCouncilId(results.getJSONObject(i)
							.getJSONObject("Parent").getString("Account__c"));
					troop.setGradeLevel(results.getJSONObject(i)
							.getJSONObject("Parent")
							.getString("Program_Grade_Level__c"));

					

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
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
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
			user_troop.setCouncilCode(user.getAdminCouncilId());
			user_troop.setTroopName("vtk_virtual_troop");
			// user.setPermissions(user_troop.getPermissionTokens());
			troops.add(user_troop);
		}
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

				System.err.println("Method failed: " + resp.getStatusLine());

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

	public java.util.List<Troop> getTroops_merged(User user,
			ApiConfig apiConfig, String contactId, JSONArray parentTroops) {
		
		
		java.util.List<Troop> troops_withAssociation = troopInfo(user,
				apiConfig, user.getSfUserId());
		java.util.List<Troop> troops_withOutAssociation = parseParentTroops(user,
				parentTroops);
		java.util.List<Troop> merged_troops = mergeTroops(
				troops_withAssociation, troops_withOutAssociation);
		return merged_troops;
	}

	public java.util.List<Troop> parseParentTroops(User user, JSONArray results) {
		
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
				troop.setCouncilCode(results.getJSONObject(i).getInt(
						"Council_Code__c"));
				troop.setCouncilId(results.getJSONObject(i).getString(
						"Account__c"));
		
				troop.setGradeLevel(results.getJSONObject(i)

				.getString("Program_Grade_Level__c"));

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
			  System.err.println("Error int SalesForceDAO.parseTroops: found error while parsing troop response json from Salesforce. Ignoring this troop... ");
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
	
	
	
	public boolean isValidOAuthToken(ApiConfig apiConfig) throws IllegalAccessException {
		CloseableHttpClient connection = null;
		boolean isValidToken= false;
		String vtlApiUserUri = apiConfig.getVtkApiUserUri();
		String url = apiConfig.getWebServicesUrl() + vtlApiUserUri
				+ "?USER_ID=" + apiConfig.getUserId();
		HttpGet method = new HttpGet(url);
		method.setHeader("Authorization", "OAuth " + apiConfig.getAccessToken());
		try {
			connection = connectionFactory.getConnection();
			CloseableHttpResponse resp = connection.execute(method);
			int statusCode = resp.getStatusLine().getStatusCode();
			
			if (statusCode == HttpStatus.SC_OK) {
				isValidToken = true;
				apiConfig.setLastTimeTokenRefreshed( java.util.Calendar.getInstance().getTimeInMillis());
System.err.println("OAUth token is valid!!!");				
			}else{
				apiConfig.setAccessTokenValid(false);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			if (method != null)
				method.releaseConnection();
		}
		return isValidToken;
	}
	
	
	public void doJoe (String code, String redirect_uri, String myResource) {
		//String code= "AAABAAAAiL9Kn2Z27UubvWFPbm0gLQIjdGrghn_rLHPWG_fvKbrYy6gWWh4jHO0ymIrFXOAZmgvolGSpSx_gTHbcbRlXZ_tAG0yZ8R197gAQOV3t9SP_NskmRWHWC5vI_735ZOtn8eWV1OuMSeWYkkE0jHbhePCRfFquBDRE3eovGdinbXo26DoeNGTBRqNKYI2BqQivXfiU-o39KxllGk3RyNDPoxzgSnegeNru4z2yHF65dodcdYBW6VDSGu3fBaIh5y4w8ClNaj29yRDsbNcxV7s34YwPNFL8rPkyYRfhu3N64T5LjVL2Yl5alNZQ_BrN0xTuQ3o7Z_e-iOt1PhWjyLabfp8Vru-Hi57WDHL1udGR0hEUCKgucmEhFtQJGJE2bmev-NtTBNdiaXXIn6NUKzSPlCZEoFWiPLTWRiN6N91X98De8aaZ-HZiiRKH76x6ZzJBHyXj1A6zuVn3LRDucxnYhvcu8UATMCGt28-5TG-jBO7YVu7xxYF4Y2UYLt112GhlEzgakN25qTPlH735Tx7wT2traoureEuX3LV25DrL8F-71Npn1pqZbAoCQyu8BX5FIAA&session_state=43ad2a10-cb12-4dd0-96e8-47fda80019ad";
		try {
			code = URLDecoder.decode(code, "UTF-8");
		} catch (Exception e) {
			log.error("Error decoding the code. Left it as is.");
		}
		HttpClient httpclient = new HttpClient();
		String tokenUrl = "https://login.microsoftonline.com/82d58e0a-7ac9-4887-b35b-4badbeb58fa2/oauth2/token";
		PostMethod post = new PostMethod(tokenUrl);
		post.addParameter("code", code);
		post.addParameter("grant_type", "authorization_code");
		post.addParameter("client_id", "685755ef-6295-4343-b28c-d79065511bd0");
		post.addParameter("client_secret", "Wi7sagASVcorobTuqh9oATOUKugyFpdpmaoWj2u4KuI=");
		post.addParameter("redirect_uri", redirect_uri); //"https://graph.windows.net/82d58e0a-7ac9-4887-b35b-4badbeb58fa2");
	post.addParameter("resource", myResource);//"http://localhost:8080/aem-connector");//https://eycms.crm.dynamics.com/aemconnector");
		
		System.err.println(post.getRequestCharSet());
		log.debug(post.getRequestEntity().toString());
		try {
			System.err.println("________________doAuth_________start_____________________________");
			System.err.println("code " + code);
			System.err.println("grant_type: authorization_code");
			System.err.println("client_id: " + clientId);
			System.err.println("client_secret: " + clientSecret);
			System.err.println("redirect_uri " + callbackUrl);
			System.err.println(post.getRequestCharSet());
			Header headers[] = post.getRequestHeaders();
			for (Header h : headers) {
				System.err.println("Headers: " + h.getName() + " : " + h.getValue());
			}
			System.err.println(":::> " + post.getQueryString());
			System.err.println(OAuthUrl + "/services/oauth2/token");
			System.err.println("___________________doAuth________end___________________________");
			httpclient.executeMethod(post);
			System.err.println("doAuth: " + post.getResponseBodyAsString());
			if (post.getStatusCode() == HttpStatus.SC_OK) {
				try {
					JSONObject authResponse = new JSONObject(new JSONTokener(
							new InputStreamReader(
									post.getResponseBodyAsStream())));
					ApiConfig config = new ApiConfig();
					config.setAccessToken(authResponse
							.getString("access_token"));
doJoeApi(config.getAccessToken());					
					
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
						System.err.println("Skipping refresh token because SF is not providing it");
					}
					System.err.println("Access token: "
							+ authResponse.getString("access_token"));
					System.err.println("REfresh tolen: " + refreshTokenStr);

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
					//return config;
					
				} catch (JSONException e) {
					System.err.println("JSON Parse exception: " + e.toString());
				}
			} else {
				System.err.println("Return status not OK: " + post.getStatusCode() + " "
						+ post.getResponseBodyAsString());
			}
		} catch (Exception e) {
			System.err.println("Error executing HTTP POST when authenticating: "
					+ e.toString());
		} finally {
			post.releaseConnection();
		}
		//return null;
	}
	
	
	public void doJoeApi(String access_token){
		
		System.err.println("do Joe API....");

			CloseableHttpClient connection = null;
			
			String url =  "https://eycms.api.crm.dynamics.com/api/data/v8.0/accounts?$select=name";

			HttpGet method = new HttpGet(url);
			method.setHeader("Authorization", "Bearer " + access_token);

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

System.err.println(">>>>> " + rsp);

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

		

		
	}
}// end class

