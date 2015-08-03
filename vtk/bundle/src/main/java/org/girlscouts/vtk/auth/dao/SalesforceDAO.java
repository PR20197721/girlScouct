package org.girlscouts.vtk.auth.dao;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.codec.binary.Base64;
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
import org.girlscouts.vtk.sso.saml.OAuthRequest;
import org.girlscouts.vtk.sso.saml.Utils;
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

	public SalesforceDAO(TroopDAO troopDAO, ConnectionFactory connectionFactory) {
		this.troopDAO = troopDAO;
		this.connectionFactory = connectionFactory;
	}

	public User getUserOrg(ApiConfig config) {
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
	System.err.println(get.getStatusCode() + " : "
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
System.err.println("<<<<<Apex contacts reponse: " + response +" access_token: "+getToken(apiConfig));			
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

	public java.util.List<Troop> troopInfo(ApiConfig apiConfig, String contactId)throws IllegalAccessException {
		java.util.List<Troop> troops = new java.util.ArrayList();
		log.debug("**OAuth** troopInfo URL  " + apiConfig.getWebServicesUrl()
				+ "/services/apexrest/activeUserTroopData?userId=" + contactId);
		
//System.err.println("tatat:"+ apiConfig.getWebServicesUrl()+ "/services/apexrest/activeUserTroopData?userId=" + contactId);	
		CloseableHttpClient connection = null;
		HttpGet method = null;
		try {
			method = new HttpGet(apiConfig.getWebServicesUrl()
					+ "/services/apexrest/activeUserTroopData?userId="
					+ contactId);
//	System.err.println("tatat "+ apiConfig.getWebServicesUrl()+ "/services/apexrest/activeUserTroopData?userId="+ contactId);		
			method.setHeader("Authorization", "OAuth " +getToken(apiConfig));
	
			connection = connectionFactory.getConnection();
			HttpResponse resp = connection.execute(method);
			int statusCode = resp.getStatusLine().getStatusCode();
			
		////	System.err.println("Status code: "+ statusCode);	
			
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + resp.getStatusLine());
				throw new IllegalAccessException();
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
					}catch(Exception e){e.printStackTrace(); /*troop.setRole("DP");*/}
					
					
					
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
		/*
		java.util.Calendar validTokenTime = java.util.Calendar.getInstance();
		validTokenTime.add(java.util.Calendar.MINUTE, -1);
		if (validTokenTime.getTimeInMillis() > apiConfig
				.getLastTimeTokenRefreshed()) {
			apiConfig = refreshToken(apiConfig);
			log.info("Refreshing Salesforce token");
		}
		*/
		return apiConfig.getAccessToken();
	}
	
	
	public User getUser(ApiConfig apiConfig) throws IllegalAccessException{
		User user= new User();
		CloseableHttpClient connection = null;
		HttpGet method = new HttpGet(apiConfig.getWebServicesUrl()
				+ "/services/apexrest/getUserInfo?USER_ID="+ apiConfig.getUserId());
		method.setHeader("Authorization", "OAuth " + apiConfig.getAccessToken());//getToken(apiConfig) );
		try {
			connection = connectionFactory.getConnection();
			CloseableHttpResponse resp = connection.execute(method);
			int statusCode = resp.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + resp.getStatusLine());
				throw new IllegalAccessException();
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
				log.debug("<<<<<Apex user reponse: " + response);
				JSONArray results = response.getJSONArray("records");
				for (int i = 0; i < results.length(); i++) {
					log.debug("_____ " + results.get(i));
					//int current = results.length() - 1;
					try {
						try {
							user.setName(results.getJSONObject(i)
									.getString("FirstName"));
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							user.setEmail(results.getJSONObject(i)
									.getString("Email"));
						} catch (Exception e) {
							System.err
									.println("SAlesforceDAO.getUser: no email");
						}
						try {
							user.setPhone(results.getJSONObject(i)
									.getString("Phone"));
						} catch (Exception e) {
							System.err
									.println("SAlesforceDAO.getUser: no phone");
						}

						try {
							user.setContactId(results.getJSONObject(i)
									.getString("ContactId"));
							user.setSfUserId(results.getJSONObject(i)
									.getString("Id"));
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							String email = results.getJSONObject(i)
									.getString("Email");

						} catch (Exception e) {
							e.printStackTrace();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
					java.util.List<Troop> troops = troopInfo(apiConfig,
							user.getSfUserId());
					apiConfig.setTroops(troops);

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

	
	public ApiConfig getToken(String ASSERTION, String token, String userId, String certificateS) {
		


/*
//Security Checks
 Document xmlDoc=null;
try {
	
	xmlDoc = Utils.loadXML( new String(new Base64().decode(ASSERTION)));
} catch (Exception e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
}

 org.w3c.dom.Element rootElement = xmlDoc.getDocumentElement();		
			xmlDoc.getDocumentElement().normalize();
			
			
			NodeList nodes = xmlDoc.getElementsByTagNameNS("*", "SignatureValue");
		
			String signatureValue = nodes.item(0).getChildNodes().item(0).getNodeValue();
	System.err.println("tata sign: "+ signatureValue);
*/

String x = new String(new Base64().decode(ASSERTION));
x=x.substring( x.indexOf("<ds:Signature"),  x.indexOf("</ds:Signature>")+15);
System.err.println("tata testX: "+ x);

String y =null, z=null;
try{
 y = new String(new Base64().decode(ASSERTION));
 
  z = y;
 z= z.substring( z.indexOf("IssueInstant=\"")+14, z.indexOf("\" Version"));
 System.err.println("tata z: "+ z);
 
y=y.substring( y.indexOf("auth.sfauth.html\" ID=\"")+22,  y.indexOf("\" IssueInstant="));
System.err.println("tata y: "+ y);
}catch(Exception e){e.printStackTrace();}
	



String assertion="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
"<saml:Assertion xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" ID=\""+y+"\" IssueInstant=\""+z+"\" Version=\"2.0\">"+
  "<saml:Issuer Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">3MVG9ahGHqp.k2_yeQBSRKEBsGHrY.Gjxv0vUjeW_2Dy6AFNe_8TanHRxUQ7BZsForgy38OuJsInpyLsVtcEH</saml:Issuer>"+
  
  
x+
"<saml:Subject xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">"+
"<saml:NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">"+token+"</saml:NameID>"+
  "<saml:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:bearer\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">"+
  "<saml:SubjectConfirmationData NotOnOrAfter=\"2013-09-05T19:30:14.654Z\" Recipient=\"https://gsuat-gsmembers.cs17.force.com/members/services/oauth2/token\"/>"+
  "</saml:SubjectConfirmation>"+
"</saml:Subject>"+
"<saml:Conditions NotBefore=\"2013-09-05T19:25:14.654Z\" NotOnOrAfter=\"2015-07-07T19:30:14.654Z\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">"+
"<saml:AudienceRestriction xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">"+
"<saml:Audience>https://gsuat-gsmembers.cs17.force.com/members</saml:Audience>"+
"</saml:AudienceRestriction>"+
"</saml:Conditions>"+
  "<saml:AuthnStatement AuthnInstant=\"2013-09-05T19:25:14.655Z\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">"+
    "<saml:AuthnContext xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">"+
    "<saml:AuthnContextClassRef>urn:oasis:names:tc:SAML:2.0:ac:classes:unspecified</saml:AuthnContextClassRef>"+
    "</saml:AuthnContext>"+
  "</saml:AuthnStatement>"+
"</saml:Assertion>";

assertion="<?xml version=\"1.0\" encoding=\"UTF-8\"?><Assertion xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" ID=\""+y+"\" IssueInstant=\""+z+"\" Version=\"2.0\">"+
  "<Issuer>https://girlscouts--gsuat.cs17.my.salesforce.com</Issuer>"+
  
  
x+
"<Subject xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">"+
"<NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress\">"+token+"</NameID>"+
  "<SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:bearer\">"+
  "<SubjectConfirmationData NotOnOrAfter=\"2016-09-05T19:30:14.654Z\" Recipient=\"https://login.salesforce.com/services/oauth2/token\"/>"+
  "</SubjectConfirmation>"+
"</Subject>"+
"<Conditions NotBefore=\"2013-09-05T19:25:14.654Z\" NotOnOrAfter=\"2015-07-07T19:30:14.654Z\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">"+
"<AudienceRestriction xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">"+
"<Audience>https://login.salesforce.com</Audience>"+
"</AudienceRestriction>"+
"</Conditions>"+
  "<AuthnStatement AuthnInstant=\"2015-07-07T19:25:14.655Z\">"+
    "<AuthnContext xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">"+
    "<AuthnContextClassRef>urn:oasis:names:tc:SAML:2.0:ac:classes:X509</AuthnContextClassRef>"+
    "</AuthnContext>"+
  "</AuthnStatement>"+
"</Assertion>";


//sample
String xx= "<?xml version=\"1.0\" encoding=\"UTF-8\"?><saml:Assertion xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" ID=\"_cd3649b3639560458bc9d9b33dfee8d21378409114655\" IssueInstant=\"2013-09-05T19:25:14.654Z\" Version=\"2.0\">  <saml:Issuer Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">3MVG9PhR6g6B7ps45QoRvhVGGMmR_DT4kxXzVXOo6TTHF3QO1nmqOAstC92 4qSUiUeEDcuGV4tmAxyo_fV8j</saml:Issuer>  <ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">    <ds:SignedInfo>    <ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>    <ds:SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256\"/>    <ds:Reference URI=\"#_cd3649b3639560458bc9d9b33dfee8d21378409114655\">      <ds:Transforms>      <ds:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/>      <ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"><ec:InclusiveNamespaces xmlns:ec=\"http://www.w3.org/2001/10/xml-exc-c14n#\" PrefixList=\"ds saml\"/></ds:Transform>      </ds:Transforms>    <ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/>    <ds:DigestValue>N8DxylbIeNg8JDO87WIqXGkoIWA=</ds:DigestValue>    </ds:Reference>    </ds:SignedInfo>    <ds:SignatureValue>XV0lFJrkhJykGYQbIs0JBFEHdt4pe2gBgitcXrscNVX2hKGpwQ+WqjF8EKrqV4Q3/Q4KglrXl/6sxJr6WOmxWtIQC4oWhSvVyfag34zQoecZeunEdFSMlnvPtqBVzJu9hJjy/QDqDWfMeWvF9S50Azd0EhJxz/Ly1i28o4aCXQQ=    </ds:SignatureValue>    <ds:KeyInfo>    <ds:X509Data>    <ds:X509Certificate>MIICOzCCAaSgAwIBAgIGAR7RRteKMA0GCSqGSIb3DQEBBQUAMGExCzAJBgNVBAYTAlVTMQswCQYDVQQIEwJDQTEWMBQGA1UEBxMNU2FuIEZyYW5jaXNjbzENMAsGA1UEChMEUEFDUzENMAsGA1UECxMEU0ZEQzEPMA0GA1UEAxMGU0FNTDIwMB4XDTA5MDExMzE4MzUyN1oXDTE0MDExMTE4MzUyN1owYTELMAkGA1UEBhMCVVMxCzAJBgNVBAgTAkNBMRYwFAYDVQQHEw1TYW4gRnJhbmNpc2NvMQ0wCwYDVQQKEwRQQUNTMQ0wCwYDVQQLEwRTRkRDMQ8wDQYDVQQDEwZTQU1MMjAwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAJNGcu8nW6xq2l/dAgbJmSfHLGRn+vCuKWY+LAELw+Kerjaj5Dq3ZGW38HR4BmZksG3g4eA1RXn1hiZGI1Q6Ei59QE/OZQx2zVSTb7+oIwRcDHEB1+RraYT3LJuh4JwUDVfEj3WgDnTjE5vD46l/CR5EXf4VL8uo8T40FkA51AhTAgMBAAEwDQYJKoZIhvcNAQEFBQADgYEAehxggY6tBl8x1SSvCUyUIHvxssAn1AutgZLKWuR1+FXfJzdVdE2F77nrV9YifIERUwhONiS82mBOkKqZZPL1hcKhKSnFZN2iWmm1sspL73I/eAwVsOUj+bS3v9POo4ceAD/QCCY8gUAInTH0Mq1eOdJMhYKnw/blUyqjZn9rajY=    </ds:X509Certificate>    </ds:X509Data>    </ds:KeyInfo>  </ds:Signature><saml:Subject xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\"><saml:NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">test@example.org</saml:NameID>  <saml:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:bearer\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">  <saml:SubjectConfirmationData NotOnOrAfter=\"2013-09-05T19:30:14.654Z\" Recipient=\"https://login.salesforce.com/services/oauth2/token\"/>  </saml:SubjectConfirmation></saml:Subject><saml:Conditions NotBefore=\"2013-09-05T19:25:14.654Z\" NotOnOrAfter=\"2013-09-05T19:30:14.654Z\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\"><saml:AudienceRestriction xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\"><saml:Audience>https://login.salesforce.com/services/oauth2/token</saml:Audience></saml:AudienceRestriction></saml:Conditions>  <saml:AuthnStatement AuthnInstant=\"2013-09-05T19:25:14.655Z\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">    <saml:AuthnContext xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">    <saml:AuthnContextClassRef>urn:oasis:names:tc:SAML:2.0:ac:classes:unspecified</saml:AuthnContextClassRef>    </saml:AuthnContext>  </saml:AuthnStatement></saml:Assertion>";



System.err.println("***********************start*************************************************");
System.err.println("CHECK ASSERTION: "+ assertion);
System.err.println("*************************end***********************************************");

Base64 base64 = new Base64();
/*
try{
	byte[] decodedB = base64.decode(ASSERTION);
	ASSERTION = new String(decodedB);
	//System.err.println("CHECK decoded: "+ ASSERTION);
}catch(Exception e){e.printStackTrace();}
ASSERTION = ASSERTION.replace("https://girlscouts--gsuat.cs17.my.salesforce.com", "3MVG9ahGHqp.k2_yeQBSRKEBsGHrY.Gjxv0vUjeW_2Dy6AFNe_8TanHRxUQ7BZsForgy38OuJsInpyLsVtcEH");
System.err.println("tata ASERTION to TOKEN REQ: "+ ASSERTION);

//byte[] encoded = base64.encode(assertion.getBytes());
System.err.println("************************************************************************");
ASSERTION.replace("https://girlscouts--gsuat.cs17.my.salesforce.com", "");
ASSERTION= new String(base64.encode(ASSERTION.getBytes()));
System.err.println("TEST B4: "+ ASSERTION);
*/


		HttpClient httpclient = new HttpClient();
		String tokenUrl =  "https://gsuat-gsmembers.cs17.force.com/members/services/oauth2/token";
		tokenUrl="https://login.salesforce.com/services/oauth2/token";
		PostMethod post = new PostMethod(tokenUrl);
		
		/*
		post.addParameter("grant_type", "urn:ietf:params:oauth:grant-type:saml2-bearer");//encoded.toString());
		post.addParameter("assertion", new String(base64.encode(assertion.getBytes())));   //ASSERTION); //encoded.toString());//ASSERTION);
		*/
		
		
		String thisAssertion = new String(base64.encode(assertion.getBytes()));
		post.addParameter("grant_type", "assertion");
		try {
			post.addParameter("assertion" , thisAssertion);
//			post.addParameter("assertion" , java.net.URLEncoder.encode(new String(base64.encode(assertion.getBytes())), "UTF-8"));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		post.addParameter("assertion_type", "urn:oasis:names:tc:SAML:2.0:profiles:SSO:browser");

		System.out.println("This assertion: " + post.getParameter("assertion"));
		try {

			
			log.debug(post.getRequestCharSet());
			Header headers[] = post.getRequestHeaders();
			
			httpclient.executeMethod(post);
			
			
System.err.println("tata: token "+post.getResponseBodyAsString());			
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
	
	
	public boolean isValid(ApiConfig apiConfig) {
		if( apiConfig==null ) return false;
System.err.println("checking isValid...");		
		CloseableHttpClient connection = null;
		HttpGet method = new HttpGet(apiConfig.getWebServicesUrl()
				+ "/services/apexrest/getUserInfo?USER_ID="+ apiConfig.getUserId());
		method.setHeader("Authorization", "OAuth " + apiConfig.getAccessToken());//getToken(apiConfig) );
		try {
			connection = connectionFactory.getConnection();
			CloseableHttpResponse resp = connection.execute(method);
			int statusCode = resp.getStatusLine().getStatusCode();
System.err.println("tata statusCode: "+ statusCode);		
			if (statusCode == HttpStatus.SC_OK) {
				
			
			
			
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
System.err.println(">>tata>>> " + rsp);
return true;
			}
			
		}catch(Exception e){e.printStackTrace();}
		return false;
	}
	
	
	

}
