package org.girlscouts.vtk.salesforce;

import java.io.InputStreamReader;
import java.net.URLEncoder;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.girlscouts.vtk.auth.*;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.auth.models.User;

public class tester3 {

	private String clientId = "3MVG9A2kN3Bn17hsP.Ui7f1jgy.inaXzsZ4VKF6DcS.LKlCZSdhHBeH.992uouy19lQAyvfZC98jgGtM73lIu";
	private String clientSecret = "8752122745172672197";

	// private String
	// redirectUri="http://localhost:4502/content/testLogin2/login5.html";
	private String redirectUri = "http://localhost:4502/content/testMy.html";

	String code = "aPrxMZkm7lCkgfSJMlB6uxm.vu1.jTVRhlwG97lMQiRW1qqe_99CUDoLyaa7VtEamBomHFVHEg==";

	public static void main(String[] args) {

		ApiConfig apiConfig = new ApiConfig();
		apiConfig
				.setAccessToken("00D1100000Bufib!ARQAQPtoikqZZIS7JRo6t0KJwSaOMKUSrsN6a_uczZs.9rpVx.Td8u2G4o0Ee86VmXc6HiaEYAn_Ni_Po6txvm17SrpmB9kf");
		apiConfig.setInstanceUrl("https://cs18.salesforce.com");
		// apiConfig.setUserId("005Z00000025MoMIAU");
		apiConfig.setUserId("00511000001Ym5bAAC");// 003G000001ZRqxVIAT");//701Z0000000WZA7");//005Z00000025ybG");//
													// debra go
		// apiConfig.setUserId("005Z00000025nQWIAY");

		tester3 me = new tester3();
		// me.getUserInfo(apiConfig);

		me.showContacts(apiConfig, "0031100000SEtG5AAL");

		if (true)
			return;

		if (true)
			return;

		new tester3().logout(apiConfig);

		// *** Send bulk Email to salesForice
		/*
		 * org.girlscouts.vtk.salesforce.deprUsers user = new
		 * tester3().getUserInfo(apiConfig);
		 * 
		 * Email email = new Email(); email.setFrom(user.getEmail());
		 * email.setTo(user.getEmail()); email.setSubject("test email"+ new
		 * java.util.Date()); email.setTxtEmail("hello from team leader");
		 * email.setParentId("500Z0000007Szyg"); String confId =new
		 * tester3().doEmail(apiConfig, email);
		 */

		// *** end Send bulk Email to salesForice

	}

	private void doInit() {
		String environment = "https://login.salesforce.com";
		try {

			String authUrl = environment

			+ "/services/oauth2/authorize?response_type=code&client_id="

			+ clientId + "&redirect_uri="

			+ URLEncoder.encode(redirectUri, "UTF-8");

			HttpClient httpclient = new HttpClient();
			PostMethod post = new PostMethod(authUrl);
			httpclient.executeMethod(post);

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	public ApiConfig doAuth(String code) {

		ApiConfig config = new ApiConfig();

		try {

			code = java.net.URLDecoder.decode(code, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		String accessToken = null;
		String environment = "https://test.salesforce.com";
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
			try {
				JSONObject authResponse = new JSONObject(new JSONTokener(
						new InputStreamReader(post.getResponseBodyAsStream())));

				accessToken = authResponse.getString("access_token");
				String instanceUrl = authResponse.getString("instance_url");

				config.setAccessToken(accessToken);
				config.setInstanceUrl(instanceUrl);
				config.setTokenType(authResponse.getString("token_type"));
				config.setId(authResponse.getString("id"));
				config.setUserId(config.getId().substring(
						config.getId().lastIndexOf("/") + 1));

			} catch (JSONException e) {
				e.printStackTrace();

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return config;
	}

	public void createAccount(Account _account, ApiConfig apiConfig) {

		PostMethod post = null;
		String accountId = null;
		HttpClient httpclient = new HttpClient();

		JSONObject account = new JSONObject();

		try {
			account.put("Name", _account.getName());

		} catch (JSONException e) {
			e.printStackTrace();

		}

		try {

			post = new PostMethod(apiConfig.getInstanceUrl()
					+ "/services/data/v20.0/sobjects/Account/");

			post.setRequestHeader("Authorization",
					"OAuth " + apiConfig.getAccessToken());
			post.setRequestEntity(new StringRequestEntity(account.toString(),
					"application/json", null));

			httpclient.executeMethod(post);

			if (post.getStatusCode() == HttpStatus.SC_CREATED) {
				try {
					JSONObject response = new JSONObject(new JSONTokener(
							new InputStreamReader(
									post.getResponseBodyAsStream())));

					if (response.getBoolean("success")) {
						accountId = response.getString("id");

					}
				} catch (JSONException e) {
					e.printStackTrace();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

	}

	public void deleteAccount(ApiConfig apiConfig, String accountId) {

		DeleteMethod delete = null;
		try {
			HttpClient httpclient = new HttpClient();

			delete = new DeleteMethod(apiConfig.getInstanceUrl()
					+ "/services/data/v20.0/sobjects/Account/" + accountId);

			delete.setRequestHeader("Authorization",
					"OAuth " + apiConfig.getAccessToken());

			httpclient.executeMethod(delete);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			delete.releaseConnection();
		}
	}

	public java.util.List<Account> showAccounts(ApiConfig apiConfig) {

		java.util.List accounts = new java.util.ArrayList();
		try {

			HttpClient httpclient = new HttpClient();
			GetMethod get = new GetMethod(apiConfig.getInstanceUrl()
					+ "/services/data/v20.0/query");

			get.setRequestHeader("Authorization",
					"OAuth " + apiConfig.getAccessToken());

			NameValuePair[] params = new NameValuePair[1];

			params[0] = new NameValuePair("q",
					"SELECT Name, Id from Account LIMIT 100");
			get.setQueryString(params);

			try {
				httpclient.executeMethod(get);
				if (get.getStatusCode() == HttpStatus.SC_OK) {

					try {
						JSONObject response = new JSONObject(new JSONTokener(
								new InputStreamReader(
										get.getResponseBodyAsStream())));

						JSONArray results = response.getJSONArray("records");

						for (int i = 0; i < results.length(); i++) {

							Account acc = new Account();
							acc.setId(results.getJSONObject(i).getString("Id"));
							acc.setName(results.getJSONObject(i).getString(
									"Name"));
							accounts.add(acc);

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
		return accounts;
	}

	public User getUserInfo(ApiConfig config) {

		User user = new User();
		java.util.List accounts = new java.util.ArrayList();
		try {

			HttpClient httpclient = new HttpClient();
			GetMethod get = new GetMethod(config.getInstanceUrl()
					+ "/services/data/v20.0/query");

			get.setRequestHeader("Authorization",
					"OAuth " + config.getAccessToken());

			NameValuePair[] params = new NameValuePair[1];

			params[0] = new NameValuePair("q",
					"SELECT name,email, ContactId from User where id='"
							+ config.getUserId() + "' limit 1");
			get.setQueryString(params);

			try {
				httpclient.executeMethod(get);

				System.err.println("User... " + get.getResponseBodyAsString());

				if (get.getStatusCode() == HttpStatus.SC_OK) {

					try {
						JSONObject response = new JSONObject(new JSONTokener(
								new InputStreamReader(
										get.getResponseBodyAsStream())));

						JSONArray results = response.getJSONArray("records");

						for (int i = 0; i < results.length(); i++) {

							user.setName(results.getJSONObject(i).getString(
									"Name"));
							user.setEmail(results.getJSONObject(i).getString(
									"Email"));
							user.setContactId(results.getJSONObject(i)
									.getString("ContactId"));

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

		return user;

	}

	public void updateAccount(Account _account, ApiConfig apiConfig) {
		try {
			HttpClient httpclient = new HttpClient();

			JSONObject update = new JSONObject();

			try {
				update.put("Name", _account.getName());

			} catch (JSONException e) {
				e.printStackTrace();
			}

			PostMethod patch = new PostMethod(apiConfig.getInstanceUrl()
					+ "/services/data/v20.0/sobjects/Account/"
					+ _account.getId()) {
				@Override
				public String getName() {
					return "PATCH";
				}
			};

			patch.setRequestHeader("Authorization",
					"OAuth " + apiConfig.getAccessToken());
			patch.setRequestEntity(new StringRequestEntity(update.toString(),
					"application/json", null));

			try {
				httpclient.executeMethod(patch);

			} finally {
				patch.releaseConnection();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Contact showContacts(ApiConfig apiConfig, String campaignMemberId) {

		// java.util.List <Contact>contacts = new java.util.ArrayList();
		Contact contact = null;
		try {

			HttpClient httpclient = new HttpClient();
			GetMethod get = new GetMethod(apiConfig.getInstanceUrl()
					+ "/services/data/v20.0/query");

			get.setRequestHeader("Authorization",
					"OAuth " + apiConfig.getAccessToken());

			NameValuePair[] params = new NameValuePair[1];

			params[0] = new NameValuePair("q",
					"SELECT Name, Id,Email from Contact where id='"
							+ campaignMemberId + "'");
			get.setQueryString(params);

			try {
				httpclient.executeMethod(get);

				System.err.println("Contact: " + get.getStatusCode() + " :"
						+ get.getResponseBodyAsString());

				if (get.getStatusCode() == HttpStatus.SC_OK) {

					try {
						JSONObject response = new JSONObject(new JSONTokener(
								new InputStreamReader(
										get.getResponseBodyAsStream())));

						JSONArray results = response.getJSONArray("records");

						for (int i = 0; i < results.length(); i++) {

							contact = new Contact();
							contact.setId(results.getJSONObject(i).getString(
									"Id"));
							contact.setLastName(results.getJSONObject(i)
									.getString("Name"));

							if (!results.getJSONObject(i).get("Email")
									.toString().equals("null")) {

								contact.setEmail(results.getJSONObject(i)
										.getString("Email"));

							} else
								contact.setEmail("");
							// contacts.add(contact);

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
		return contact;
	}

	public void deleteContact(ApiConfig apiConfig, String contactId) {

		DeleteMethod delete = null;
		try {
			HttpClient httpclient = new HttpClient();

			delete = new DeleteMethod(apiConfig.getInstanceUrl()
					+ "/services/data/v20.0/sobjects/Contact/" + contactId);

			delete.setRequestHeader("Authorization",
					"OAuth " + apiConfig.getAccessToken());

			httpclient.executeMethod(delete);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			delete.releaseConnection();
		}
	}

	public void updateContact(Contact _contact, ApiConfig apiConfig) {

		try {
			HttpClient httpclient = new HttpClient();
			JSONObject update = new JSONObject();

			try {
				update.put("LastName", _contact.getLastName());
				update.put("AccountId", _contact.getAccountId());

			} catch (JSONException e) {
				e.printStackTrace();
			}

			PostMethod patch = new PostMethod(apiConfig.getInstanceUrl()
					+ "/services/data/v28.0/sobjects/Contact/"
					+ _contact.getId()) {
				@Override
				public String getName() {
					return "PATCH";
				}
			};

			patch.setRequestHeader("Authorization",
					"OAuth " + apiConfig.getAccessToken());
			patch.setRequestEntity(new StringRequestEntity(update.toString(),
					"application/json", null));

			try {
				httpclient.executeMethod(patch);

			} finally {
				patch.releaseConnection();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createContact(Contact _contact, ApiConfig apiConfig) {

		PostMethod post = null;
		HttpClient httpclient = new HttpClient();
		JSONObject contact = new JSONObject();
		try {
			contact.put("LastName", _contact.getLastName());
			contact.put("AccountId", _contact.getAccountId());
			contact.put("Email", _contact.getEmail());
			contact.put("Phone", _contact.getPhone());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			post = new PostMethod(apiConfig.getInstanceUrl()
					+ "/services/data/v28.0/sobjects/Contact/");
			post.setRequestHeader("Authorization",
					"OAuth " + apiConfig.getAccessToken());
			post.setRequestEntity(new StringRequestEntity(contact.toString(),
					"application/json", null));
			httpclient.executeMethod(post);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (post != null)
				post.releaseConnection();
		}

	}

	public String sendEmail(ApiConfig apiConfig, Email emailInfo) {

		PostMethod post = null;
		String confId = null;
		HttpClient httpclient = new HttpClient();

		JSONObject email = new JSONObject();

		try {
			/*
			 * THIS WORKS //email.put("ActivityId" , "asdf");
			 * email.put("FromAddress" , "alex_yakobovich@northps.com");
			 * email.put("HtmlBody" , "html body"); email.put("TextBody" ,
			 * "text test body"); email.put("Subject" , "test"+ new
			 * java.util.Date()); email.put("ToAddress" ,
			 * "alex_yakobovich@northps.com"); email.put("ParentId",
			 * "500Z0000007Szyg"); //good case"500Z0000007RPTRIA4");
			 * email.put("BccAddress",
			 * "test@test.com, test1@test.com, test2@test.com");
			 */

			email.put("FromAddress", emailInfo.getTo());
			email.put("HtmlBody", emailInfo.getHtmlEmail());
			email.put("TextBody", emailInfo.getTxtEmail());
			email.put("Subject", emailInfo.getSubject());
			email.put("ToAddress", emailInfo.getTo());
			email.put("ParentId", "500Z0000007Szyg"); // good
														// case"500Z0000007RPTRIA4");
			email.put("BccAddress", emailInfo.fmtBcc());

		} catch (JSONException e) {
			e.printStackTrace();

		}

		try {

			post = new PostMethod(apiConfig.getInstanceUrl()
					+ "/services/data/v28.0/sobjects/EmailMessage");

			post.setRequestHeader("Authorization",
					"OAuth " + apiConfig.getAccessToken());
			post.setRequestEntity(new StringRequestEntity(email.toString(),
					"application/json", null));

			httpclient.executeMethod(post);

			System.err.println("Email send " + post.getStatusCode() + " : "
					+ post.getResponseBodyAsString());

			if (post.getStatusCode() == HttpStatus.SC_CREATED) {
				try {
					JSONObject response = new JSONObject(new JSONTokener(
							new InputStreamReader(
									post.getResponseBodyAsStream())));

					if (response.getBoolean("success")) {

						confId = response.getString("id");
					}
				} catch (JSONException e) {
					e.printStackTrace();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return confId;
	}

	public String doEmail(ApiConfig apiConfig, Email email) {

		Campaign campaign = null;// !!!!!!!!getCampaign(apiConfig);

		// compaign contactIds
		java.util.List<String> campaignMembers = getCampaignContactIds(
				apiConfig, campaign);

		// all contactIds
		java.util.Map allContacts = new tester3()
				.getContactEmailList(apiConfig);

		java.util.List<String> bcc_emails = new java.util.ArrayList();
		for (int i = 0; i < campaignMembers.size(); i++) {
			String campaignMember = campaignMembers.get(i);
			String _email = (String) allContacts.get(campaignMember);
			bcc_emails.add(_email);
		}
		email.setBcc(bcc_emails);

		// send emails out via salesforce
		String confId = sendEmail(apiConfig, email);
		return confId + " :" + email.getTxtEmail();
	}

	public java.util.Map<String, String> getContactEmailList(ApiConfig apiConfig) {

		GetMethod get = null;
		java.util.Map emails = new java.util.TreeMap();
		try {
			HttpClient httpclient = new HttpClient();
			get = new GetMethod(apiConfig.getInstanceUrl()
					+ "/services/data/v20.0/query");

			get.setRequestHeader("Authorization",
					"OAuth " + apiConfig.getAccessToken());

			NameValuePair[] params = new NameValuePair[1];

			params[0] = new NameValuePair("q", "SELECT id,Email from Contact");
			get.setQueryString(params);

			try {
				httpclient.executeMethod(get);
				if (get.getStatusCode() == HttpStatus.SC_OK) {

					try {
						JSONObject response = new JSONObject(new JSONTokener(
								new InputStreamReader(
										get.getResponseBodyAsStream())));

						JSONArray results = response.getJSONArray("records");

						for (int i = 0; i < results.length(); i++) {

							if (!results.getJSONObject(i).get("Email")
									.toString().equals("null")) {

								emails.put(
										results.getJSONObject(i)
												.getString("Id"),
										results.getJSONObject(i).getString(
												"Email"));

							}

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
		return emails;

	}

	// get all campaignIds for loggedin user
	public java.util.List<String> getCampaignIds(ApiConfig apiConfig,
			String contactId) {

		System.err.println("UserId: " + apiConfig.getUserId());
		GetMethod get = null;
		// java.util.List <CampaignMember> campaignMembers = new
		// java.util.ArrayList();
		java.util.List<String> campaignIds = new java.util.ArrayList();

		// Campaign campaign = new Campaign();
		try {

			HttpClient httpclient = new HttpClient();
			get = new GetMethod(apiConfig.getInstanceUrl()
					+ "/services/data/v20.0/query");

			get.setRequestHeader("Authorization",
					"OAuth " + apiConfig.getAccessToken());

			NameValuePair[] params = new NameValuePair[1];

			/*
			 * THIS IS GOOD AND TO BE USED params[0] = new NameValuePair("q",
			 * "SELECT CampaignMemberRecordTypeId,name,ownerid,id from " +
			 * " campaign where ownerId='"+apiConfig.getUserId()+"'");
			 */

			// TEST
			/*
			 * params[0] = new NameValuePair("q",
			 * "SELECT  id, contactid, CampaignId from " +
			 * " campaignMember where contactId='"+apiConfig.getUserId()+"'");
			 */

			params[0] = new NameValuePair("q", "SELECT   CampaignId from "
					+ " campaignMember where contactId='" + contactId + "'");

			get.setQueryString(params);

			try {
				httpclient.executeMethod(get);

				System.err.println("RespCode::: "
						+ get.getResponseBodyAsString());
				JSONObject _response = new JSONObject(new JSONTokener(
						new InputStreamReader(get.getResponseBodyAsStream())));
				System.err.println(_response.toString());

				if (get.getStatusCode() == HttpStatus.SC_OK) {

					try {
						JSONObject response = new JSONObject(new JSONTokener(
								new InputStreamReader(
										get.getResponseBodyAsStream())));

						JSONArray results = response.getJSONArray("records");
						System.err.println("REsults: " + results.toString());
						for (int i = 0; i < results.length(); i++) {

							campaignIds.add(results.getJSONObject(i).getString(
									"CampaignId"));
							/*
							 * CampaignMember campaignMember= new
							 * CampaignMember();
							 * campaignMember.setId(results.getJSONObject
							 * (i).getString("CampaignId"));
							 * campaignMembers.add( campaignMember );
							 */
							// System.err.println("*** "+ apiConfig.getUserId()
							// +" : "+
							// results.getJSONObject(i).getString("ContactId")
							// );
							/*
							 * System.err.println(
							 * results.getJSONObject(i).getString("Id") +" :" +
							 * results.getJSONObject(i).getString("ContactId")
							 * +" : "+
							 * results.getJSONObject(i).getString("CampaignId")
							 * );
							 */
							/*
							 * getCampaign(apiConfig,
							 * results.getJSONObject(i).getString
							 * ("CampaignId"));
							 * campaign.setId(results.getJSONObject
							 * (i).getString("Id")); //if(i>3)return campaign;
							 */
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
		return campaignIds;

	}

	public Campaign getCampaign(ApiConfig apiConfig, String campaignId) {

		// System.err.println("UserId: "+ apiConfig.getUserId());
		GetMethod get = null;
		Campaign campaign = new Campaign();
		try {

			HttpClient httpclient = new HttpClient();
			get = new GetMethod(apiConfig.getInstanceUrl()
					+ "/services/data/v20.0/query");

			get.setRequestHeader("Authorization",
					"OAuth " + apiConfig.getAccessToken());

			NameValuePair[] params = new NameValuePair[1];

			/*
			 * THIS IS GOOD AND TO BE USED params[0] = new NameValuePair("q",
			 * "SELECT CampaignMemberRecordTypeId,name,ownerid,id from " +
			 * " campaign where ownerId='"+apiConfig.getUserId()+"'");
			 */
			System.err.println("Apiconfg " + apiConfig.getAccessToken());
			// TEST

			System.err.println(" No Campaign : " + campaignId);
			params[0] = new NameValuePair("q",
					"SELECT Job_Code__c, id, ParentId  from campaign where id='"
							+ campaignId + "'");// and
												// contactId='"+apiConfig.getUserId()
												// +"'");

			// /services/data/v20.0/sobjects/Campaign/701G0000000tONiIAM

			get.setQueryString(params);

			try {
				httpclient.executeMethod(get);

				System.err.println("RespCode " + get.getResponseBodyAsString());
				JSONObject _response = new JSONObject(new JSONTokener(
						new InputStreamReader(get.getResponseBodyAsStream())));
				System.err.println(_response.toString());

				if (get.getStatusCode() == HttpStatus.SC_OK) {

					try {
						JSONObject response = new JSONObject(new JSONTokener(
								new InputStreamReader(
										get.getResponseBodyAsStream())));

						JSONArray results = response.getJSONArray("records");
						System.err.println("REsults: " + results.toString());
						for (int i = 0; i < results.length(); i++) {
							System.err.println("CAmpaing: "
									+

									results.getJSONObject(i).get("Job_Code__c")
											.toString() + " : ");

							campaign.setId(results.getJSONObject(i).getString(
									"Id"));
							try {
								campaign.setJobCode(results.getJSONObject(i)
										.getString("Job_Code__c"));
							} catch (Exception e22) {
								e22.printStackTrace();
							}
							try {
								campaign.setParentCampaignId(results
										.getJSONObject(i).getString("ParentId"));
							} catch (Exception e4) {
								e4.printStackTrace();
							}

							// if(i>3)return campaign;
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
		return campaign;

	}

	// troop id
	public Campaign getCampaign1(ApiConfig apiConfig, String campaignId) {

		// System.err.println("UserId: "+ apiConfig.getUserId());
		GetMethod get = null;
		Campaign campaign = new Campaign();
		try {

			HttpClient httpclient = new HttpClient();
			get = new GetMethod(apiConfig.getInstanceUrl()
					+ "/services/data/v20.0/query");

			get.setRequestHeader("Authorization",
					"OAuth " + apiConfig.getAccessToken());

			NameValuePair[] params = new NameValuePair[1];

			/*
			 * THIS IS GOOD AND TO BE USED params[0] = new NameValuePair("q",
			 * "SELECT CampaignMemberRecordTypeId,name,ownerid,id from " +
			 * " campaign where ownerId='"+apiConfig.getUserId()+"'");
			 */
			System.err.println("Apiconfg " + apiConfig.getAccessToken());
			// TEST

			System.err.println(" No Campaign : " + campaignId);
			params[0] = new NameValuePair("q",
					"SELECT Job_Code__c, id, ParentId  from campaign where id='"
							+ campaignId + "'");// and
												// contactId='"+apiConfig.getUserId()
												// +"'");

			// /services/data/v20.0/sobjects/Campaign/701G0000000tONiIAM

			get.setQueryString(params);

			try {
				httpclient.executeMethod(get);

				System.err.println("RespCode " + get.getResponseBodyAsString());
				JSONObject _response = new JSONObject(new JSONTokener(
						new InputStreamReader(get.getResponseBodyAsStream())));
				System.err.println(_response.toString());

				if (get.getStatusCode() == HttpStatus.SC_OK) {

					try {
						JSONObject response = new JSONObject(new JSONTokener(
								new InputStreamReader(
										get.getResponseBodyAsStream())));

						JSONArray results = response.getJSONArray("records");
						System.err.println("REsults: " + results.toString());
						for (int i = 0; i < results.length(); i++) {
							System.err.println("CAmpaing: "
									+

									results.getJSONObject(i).get("Job_Code__c")
											.toString() + " : ");

							campaign.setId(results.getJSONObject(i).getString(
									"Id"));
							try {
								campaign.setJobCode(results.getJSONObject(i)
										.getString("Job_Code__c"));
							} catch (Exception e22) {
								e22.printStackTrace();
							}
							try {
								campaign.setParentCampaignId(results
										.getJSONObject(i).getString("ParentId"));
							} catch (Exception e4) {
								e4.printStackTrace();
							}

							// if(i>3)return campaign;
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
		return campaign;

	}

	public java.util.List<String> getCampaignContactIds(ApiConfig apiConfig,
			Campaign campaign) {

		GetMethod get = null;
		java.util.List<String> compaignMembers = new java.util.ArrayList();
		try {

			HttpClient httpclient = new HttpClient();
			get = new GetMethod(apiConfig.getInstanceUrl()
					+ "/services/data/v20.0/query");

			get.setRequestHeader("Authorization",
					"OAuth " + apiConfig.getAccessToken());

			NameValuePair[] params = new NameValuePair[1];

			// Job_Code__c='DP' LEADER
			params[0] = new NameValuePair("q",
					"SELECT contactId from campaignmember where campaignId=\'"
							+ campaign.getId() + "\'");
			get.setQueryString(params);

			try {
				httpclient.executeMethod(get);
				if (get.getStatusCode() == HttpStatus.SC_OK) {

					try {
						JSONObject response = new JSONObject(new JSONTokener(
								new InputStreamReader(
										get.getResponseBodyAsStream())));

						JSONArray results = response.getJSONArray("records");

						for (int i = 0; i < results.length(); i++) {

							compaignMembers.add(results.getJSONObject(i)
									.getString("ContactId"));
						}

					} catch (JSONException e) {
						e.printStackTrace();

					}
				}
			} finally {
				if (get != null)
					get.releaseConnection();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return compaignMembers;

	}

	public void logout(ApiConfig apiConfig) {

		HttpClient httpclient = null;
		GetMethod post = null;
		try {

			httpclient = new HttpClient();
			post = new GetMethod(apiConfig.getInstanceUrl()
					+ "/services/oauth2/revoke?token=asdf");

			post.setRequestHeader("Authorization",
					"OAuth " + apiConfig.getAccessToken());
			httpclient.executeMethod(post);
			post.releaseConnection();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (post != null)
				post.releaseConnection();
			post = null;
			httpclient = null;
		}
	}

	public java.util.List<Contact> getGirstEmails(ApiConfig apiConfig,
			String campaignId, String contactType) {

		java.util.List<Contact> contacts = new java.util.ArrayList();

		java.util.List<String> campaignMemberIds = getCampaignMemberIds(
				apiConfig, campaignId, contactType);
		System.err.println("Size: " + campaignMemberIds.size());
		for (int i = 0; i < campaignMemberIds.size(); i++) {
			// Assignment_Type__c

			Contact contact = showContacts(apiConfig, campaignMemberIds.get(i));
			contacts.add(contact);
			System.err.println(campaignId + " Email: " + contact.getEmail());
		}
		return contacts;
	}

	public java.util.List<String> getCampaignMemberIds(ApiConfig apiConfig,
			String campaignId, String contactType) {

		GetMethod get = null;

		java.util.List<String> campaignIds = new java.util.ArrayList();

		try {

			HttpClient httpclient = new HttpClient();
			get = new GetMethod(apiConfig.getInstanceUrl()
					+ "/services/data/v20.0/query");

			get.setRequestHeader("Authorization",
					"OAuth " + apiConfig.getAccessToken());

			NameValuePair[] params = new NameValuePair[1];

			if (contactType != null && !contactType.trim().equals("")) {
				params[0] = new NameValuePair("q", "SELECT   ContactId from "
						+ " campaignMember where campaignId='" + campaignId
						+ "' and Assignment_Type__c='" + contactType + "'");
			} else {
				params[0] = new NameValuePair("q", "SELECT   ContactId from "
						+ " campaignMember where campaignId='" + campaignId
						+ "'");
			}

			get.setQueryString(params);

			try {
				httpclient.executeMethod(get);

				System.err.println("RespCode " + get.getResponseBodyAsString());
				JSONObject _response = new JSONObject(new JSONTokener(
						new InputStreamReader(get.getResponseBodyAsStream())));
				System.err.println(_response.toString());

				if (get.getStatusCode() == HttpStatus.SC_OK) {

					try {
						JSONObject response = new JSONObject(new JSONTokener(
								new InputStreamReader(
										get.getResponseBodyAsStream())));

						JSONArray results = response.getJSONArray("records");
						System.err.println("REsults: " + results.toString());
						for (int i = 0; i < results.length(); i++) {

							campaignIds.add(results.getJSONObject(i).getString(
									"ContactId"));

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
		return campaignIds;

	}

	public java.util.List<Troop> troopInfo(ApiConfig apiConfig, String contactId) {

		GetMethod get = null;

		java.util.List<Troop> troops = new java.util.ArrayList();

		try {

			HttpClient httpclient = new HttpClient();
			get = new GetMethod(apiConfig.getInstanceUrl()
					+ "/services/data/v20.0/query");

			get.setRequestHeader("Authorization",
					"OAuth " + apiConfig.getAccessToken());

			NameValuePair[] params = new NameValuePair[1];

			params[0] = new NameValuePair(
					"q",
					"SELECT parentid,parent.name,parent.program_grade_level__c, parent.council_code__c, parent.account__c FROM campaign "
							+ "WHERE id IN (SELECT campaignid from campaignmember where  contactid='"
							+ contactId + "' )  AND job_code__c = 'DP'");

			get.setQueryString(params);

			try {
				httpclient.executeMethod(get);

				System.err.println("RespCode " + get.getResponseBodyAsString());
				JSONObject _response = new JSONObject(new JSONTokener(
						new InputStreamReader(get.getResponseBodyAsStream())));
				System.err.println(_response.toString());

				if (get.getStatusCode() == HttpStatus.SC_OK) {

					try {
						JSONObject response = new JSONObject(new JSONTokener(
								new InputStreamReader(
										get.getResponseBodyAsStream())));

						JSONArray results = response.getJSONArray("records");
						System.err.println("REsults: " + results.toString());

						for (int i = 0; i < results.length(); i++) {

							System.err.println("_____ " + results.get(i));

							java.util.Iterator itr = results.getJSONObject(i)
									.getJSONObject("Parent").keys();
							while (itr.hasNext())
								System.err.println("** " + itr.next());

							Troop troop = new Troop();
							try {
								// System.err.println("++ "+results.getJSONObject(i).getJSONObject("Parent")..getString("Program_Grade_Level__c"));

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

}

/*
 * {"attributes":{"type":"Campaign","url":
 * "/services/data/v20.0/sobjects/Campaign/701G0000000tONiIAM"
 * },"Id":"701G0000000tONiIAM"
 * ,"IsDeleted":false,"Name":"Unsure","ParentId":"701G0000000tONdIAM"
 * ,"Type":null
 * ,"RecordTypeId":"012G0000001IVBrIAO","Status":"Planned","StartDate"
 * :null,"EndDate"
 * :null,"ExpectedRevenue":null,"BudgetedCost":null,"ActualCost":null
 * ,"ExpectedResponse"
 * :0.0,"NumberSent":7.0,"IsActive":true,"Description":null,"NumberOfLeads"
 * :0,"NumberOfConvertedLeads"
 * :0,"NumberOfContacts":7,"NumberOfResponses":0,"NumberOfOpportunities"
 * :0,"NumberOfWonOpportunities"
 * :0,"AmountAllOpportunities":0.0,"AmountWonOpportunities"
 * :0.0,"OwnerId":"005G0000003tifCIAQ"
 * ,"CreatedDate":"2014-03-13T18:02:55.000+0000"
 * ,"CreatedById":"005G0000003tifCIAQ"
 * ,"LastModifiedDate":"2014-06-03T16:45:05.000+0000"
 * ,"LastModifiedById":"005Z0000002546lIAA"
 * ,"SystemModstamp":"2014-06-03T16:45:05.000+0000"
 * ,"LastActivityDate":null,"CampaignMemberRecordTypeId"
 * :"012G0000001IWnsIAG","rC_Giving__Affiliation__c"
 * :null,"rC_Giving__Appeal_Segment__c"
 * :null,"rC_Giving__Archive_Flag__c":false,"rC_Giving__Average_Gift_Hierarchy__c"
 * :
 * null,"rC_Giving__Average_Gift__c":null,"rC_Giving__Best_Case_Amount_Hierarchy__c"
 * :0.0,"rC_Giving__Best_Case_Amount__c":0.0,"rC_Giving__Best_Case_Bar__c":
 * "<img src=\"/img/samples/color_yellow.gif\" alt=\"yellow\" height=\"10\" width=\"0\" border=\"0\"/> 0%"
 * ,"rC_Giving__Best_Case_Ratio__c":0.0,"rC_Giving__Campaign_Category__c":null,
 * "rC_Giving__Campaign_Type__c"
 * :null,"rC_Giving__Channel__c":null,"rC_Giving__Closed_Amount_Hierarchy__c"
 * :0.0,"rC_Giving__Closed_Amount__c":0.0,"rC_Giving__Closed_Bar__c":
 * "<img src=\"/img/samples/color_green.gif\" alt=\"green\" height=\"10\" width=\"0\" border=\"0\"/> 0%"
 * ,
 * "rC_Giving__Closed_Ratio__c":0.0,"rC_Giving__Commit_Amount_Hierarchy__c":0.0,
 * "rC_Giving__Commit_Amount__c":0.0,"rC_Giving__Commit_Bar__c":
 * "<img src=\"/img/samples/color_green.gif\" alt=\"green\" height=\"10\" width=\"0\" border=\"0\"/> 0%"
 * ,"rC_Giving__Commit_Ratio__c":0.0,"rC_Giving__Cost_Per_Gift__c":null,
 * "rC_Giving__Cost_Per_Piece_Hierarchy__c"
 * :0.0,"rC_Giving__Cost_Per_Piece__c":0.0
 * ,"rC_Giving__Cost_Per_Thousand_Hierarchy__c"
 * :0.0,"rC_Giving__Cost_Per_Thousand__c"
 * :0.0,"rC_Giving__Cost_Per_Won_Opportunity__c"
 * :0.0,"rC_Giving__Creative_Package__c"
 * :null,"rC_Giving__Current_Giving_Amount_Hierarchy__c"
 * :0.0,"rC_Giving__Current_Giving_Amount__c"
 * :0.0,"rC_Giving__Days_Since_Drop__c"
 * :null,"rC_Giving__Drop_Date__c":null,"rC_Giving__Effort__c"
 * :null,"rC_Giving__End_Date_Time__c"
 * :null,"rC_Giving__Expected_Giving_Amount_Hierarchy__c"
 * :0.0,"rC_Giving__Expected_Giving_Amount__c"
 * :0.0,"rC_Giving__External_ID__c":null
 * ,"rC_Giving__GAU__c":null,"rC_Giving__Giving_Type_Engine__c"
 * :null,"rC_Giving__Giving_Type__c"
 * :null,"rC_Giving__Is_Parent__c":false,"rC_Giving__Is_Shopper__c"
 * :false,"rC_Giving__Net_Revenue_Hierarchy__c"
 * :0.0,"rC_Giving__Net_Revenue__c":0.0
 * ,"rC_Giving__Omitted_Amount_Hierarchy__c":
 * 0.0,"rC_Giving__Omitted_Amount__c":0.0,"rC_Giving__Omitted_Bar__c":
 * "<img src=\"/img/samples/color_red.gif\" alt=\"red\" height=\"10\" width=\"0\" border=\"0\"/> 0%"
 * ,
 * "rC_Giving__Omitted_Ratio__c":0.0,"rC_Giving__Percent_Goal_Hierarchy__c":null
 * ,
 * "rC_Giving__Percent_Goal__c":0.0,"rC_Giving__Pipeline_Amount_Hierarchy__c":0.0
 * ,"rC_Giving__Pipeline_Amount__c":0.0,"rC_Giving__Pipeline_Bar__c":
 * "<img src=\"/img/samples/color_yellow.gif\" alt=\"yellow\" height=\"10\" width=\"0\" border=\"0\"/> 0%"
 * ,"rC_Giving__Pipeline_Ratio__c":0.0,"rC_Giving__ROI_Hierarchy__c":null,
 * "rC_Giving__ROI__c":null,"rC_Giving__Refunded_Amount_Hierarchy__c":0.0,
 * "rC_Giving__Refunded_Amount__c":0.0,"rC_Giving__Refunded_Bar__c":
 * "<img src=\"/img/samples/color_red.gif\" alt=\"red\" height=\"10\" width=\"0\" border=\"0\"/> 0%"
 * ,
 * "rC_Giving__Refunded_Ratio__c":0.0,"rC_Giving__Remaining_Goal_Hierarchy__c":null
 * ,"rC_Giving__Remaining_Goal__c":0.0,"rC_Giving__Response_Mechanism__c":null,
 * "rC_Giving__Response_Rate_Hierarchy__c"
 * :null,"rC_Giving__Response_Rate__c":0.0
 * ,"rC_Giving__Rollup_Giving__c":"2014-05-28T17:53:55.000+0000"
 * ,"rC_Giving__Segment__c":null,"rC_Giving__Send_To_Email_Campaign__c":false,
 * "rC_Giving__Solicitation_List__c"
 * :null,"rC_Giving__Solicitation_Type__c":null,
 * "rC_Giving__Source_Code_13_Digits__c"
 * :null,"rC_Giving__Source_Code_Partial__c"
 * :null,"rC_Giving__Source_Code__c":null
 * ,"rC_Giving__Start_Date_Time__c":null,"rC_Giving__Sub_Affiliation__c"
 * :null,"rC_Giving__Sub_Channel__c"
 * :null,"rC_Giving__Support_Designation__c":null
 * ,"rC_Volunteers__Assigned_Volunteer_Count__c"
 * :0.0,"rC_Volunteers__Assigned_Volunteer_Hours__c"
 * :0.0,"rC_Volunteers__Completed_Volunteer_Count__c"
 * :0.0,"rC_Volunteers__Completed_Volunteer_Hours__c"
 * :0.0,"rC_Volunteers__Groups__c"
 * :false,"rC_Volunteers__Job_Description__c":null
 * ,"rC_Volunteers__Match_Age_Range__c"
 * :null,"rC_Volunteers__Match_Cause_Areas__c"
 * :null,"rC_Volunteers__Match_Gender__c"
 * :null,"rC_Volunteers__Match_Role__c":null
 * ,"rC_Volunteers__Match_Skills__c":null
 * ,"rC_Volunteers__Open_Volunteer_Count__c"
 * :0.0,"rC_Volunteers__Open_Volunteer_Hours__c"
 * :0.0,"rC_Volunteers__Project_Description__c"
 * :null,"rC_Volunteers__Project_End_Date__c"
 * :null,"rC_Volunteers__Project_Start_Date__c"
 * :null,"rC_Volunteers__Public__c":false
 * ,"rC_Volunteers__Required_Volunteer_Count__c"
 * :0.0,"rC_Volunteers__Required_Volunteer_Hours__c"
 * :0.0,"rC_Volunteers__Rollup_Campaigns__c"
 * :null,"rC_Volunteers__Rollup_Members__c"
 * :null,"rC_Volunteers__Shift_Date__c":null
 * ,"rC_Volunteers__Shift_End_Time__c":null
 * ,"rC_Volunteers__Shift_Repeats_Day_Of_Week__c"
 * :null,"rC_Volunteers__Shift_Repeats_End_Date__c"
 * :null,"rC_Volunteers__Shift_Repeats_Frequency__c"
 * :null,"rC_Volunteers__Shift_Repeats_Start_Date__c"
 * :null,"rC_Volunteers__Shift_Repeats__c"
 * :false,"rC_Volunteers__Shift_Start_Time__c"
 * :null,"External_ID__c":null,"rC_Event__Added_To_Waitlist_Text__c"
 * :null,"rC_Event__Available_Registered_Count__c"
 * :null,"rC_Event__Available_Waitlisted_Count__c"
 * :null,"rC_Event__End_Date_Time__c"
 * :null,"rC_Event__Event_Code__c":null,"rC_Event__Event_Subtype__c"
 * :null,"rC_Event__Event_Type__c"
 * :null,"rC_Event__Hidden_Tab_Attendance__c":false
 * ,"rC_Event__Hidden_Tab_Attributes__c"
 * :false,"rC_Event__Hidden_Tab_Chatter__c":
 * false,"rC_Event__Hidden_Tab_Details__c"
 * :false,"rC_Event__Hidden_Tab_Form__c":false
 * ,"rC_Event__Hidden_Tab_Groups__c":false
 * ,"rC_Event__Hidden_Tab_Meals__c":false,
 * "rC_Event__Hidden_Tab_Members__c":false
 * ,"rC_Event__Hidden_Tab_Seating__c":false
 * ,"rC_Event__Hidden_Tab_Sessions__c":false
 * ,"rC_Event__Hidden_Tab_Tasks__c":false
 * ,"rC_Event__Hidden_Tab_Tickets__c":false
 * ,"rC_Event__Hidden_Tab_Venues__c":false
 * ,"rC_Event__Parent_Name__c":"Unsure","rC_Event__Pricebook_Link__c":
 * "Not assigned [<a href=\"/apex/rc_events__campaign_managepricebooks?id=701G0000000tONi\" target=\"_self\">Change</a>]"
 * ,"rC_Event__Pricebook_Name__c":null,"rC_Event__Pricebook__c":null,
 * "rC_Event__Primary_Venue__c"
 * :null,"rC_Event__Registered_Count__c":null,"rC_Event__Registered_Full_Date__c"
 * :
 * null,"rC_Event__Registered_Full_Text__c":null,"rC_Event__Registered_Limit__c"
 * :null,"rC_Event__Registered_Status__c":null,
 * "rC_Event__Registration_Closed_Message__c"
 * :null,"rC_Event__Registration_Edit_Deadline__c"
 * :null,"rC_Event__Registration_End_Date_Time__c"
 * :null,"rC_Event__Registration_Landing_Page__c"
 * :null,"rC_Event__Registration_Start_Date_Time__c"
 * :null,"rC_Event__Rollup_Members__c"
 * :null,"rC_Event__Rollup_Venues__c":null,"rC_Event__Session_Status__c"
 * :null,"rC_Event__Special_Instructions__c"
 * :null,"rC_Event__Start_Date_Time__c":
 * null,"rC_Event__Time_Zone__c":null,"rC_Event__Venue_Count__c"
 * :null,"rC_Event__Waitlisted_Count__c"
 * :null,"rC_Event__Waitlisted_Full_Date__c"
 * :null,"rC_Event__Waitlisted_Full_Text__c"
 * :null,"rC_Event__Waitlisted_Limit__c"
 * :null,"rC_Event__Waitlisted_Status__c":null
 * ,"rC_Event__Attended__c":0.0,"rC_Event__No_Show__c"
 * :0.0,"Participation__c":null
 * ,"Outreach__c":false,"Troop_Formation_Status__c":"N/A"
 * ,"Display_on_Website__c"
 * :true,"Grade__c":null,"Program_Grade_Level__c":null,"GS_Community_Year__c"
 * :null
 * ,"Desired_Girl__c":null,"Girls_Assigned__c":0.0,"Girl_Openings_Remaining__c"
 * :0.0,"Account__c":null,"Zip_Code__c":null,"Event_Code__c":null,
 * "Background_Check_Needed__c"
 * :false,"Special_Handling__c":false,"Meeting_Location__c"
 * :null,"Meeting_Day_s__c"
 * :null,"Meeting_Start_Date_time__c":null,"Meeting_End_Date_Time__c"
 * :null,"Meeting_Notes__c"
 * :null,"Council_Code__c":0.0,"Driving_Role__c":false,"Finance_Role__c"
 * :false,"GS_Volunteers_Required__c"
 * :null,"Job_Code_Category__c":null,"Job_Code_Sub_Category__c"
 * :null,"Job_Code__c":null,"Job_Start_Date__c":null,"ProAlexs-MacBook-Pro:~
 * akobovich$
 */

// {"attributes":{"type":"CampaignMember","url":"/services/data/v20.0/sobjects/CampaignMember/00vZ0000001wNkVIAU"},"Id":"00vZ0000001wNkVIAU","IsDeleted":false,"CampaignId":"701Z0000000WZA7IAO","LeadId":null,"ContactId":"003Z000000j8I4jIAE","Status":"Sent","HasResponded":false,"CreatedDate":"2014-05-28T17:56:07.000+0000","CreatedById":"005Z00000025ybGIAQ","LastModifiedDate":"2014-06-04T19:28:20.000+0000","LastModifiedById":"005Z00000025ybGIAQ","SystemModstamp":"2014-06-04T19:28:20.000+0000","FirstRespondedDate":null,"RecordTypeId":"012G0000001IVBuIAO","rC_Giving__Archive_Flag__c":false,"rC_Giving__Send_To_Email_Campaign__c":false,"rC_Volunteers__Feedback__c":null,"rC_Volunteers__Group_Name__c":null,"rC_Volunteers__Group_Signup__c":false,"rC_Volunteers__Members_In_Group__c":null,"rC_Volunteers__Shift_Status__c":null,"rC_Volunteers__Volunteer_Count__c":1.0,"rC_Volunteers__Volunteer_Hours__c":0.0,"rC_Volunteers__Volunteer_Karma__c":10.0,"rC_Event__Attendance_Date__c":null,"rC_Event__Attendance_Status__c":"Pending","rC_Event__Billing_City__c":null,"rC_Event__Billing_Country__c":null,"rC_Event__Billing_Postal_Code__c":null,"rC_Event__Billing_State__c":null,"rC_Event__Billing_Street_Line_1__c":null,"rC_Event__Billing_Street_Line_2__c":null,"rC_Event__Billing_Street__c":null,"rC_Event__Campaign_Group_1__c":null,"rC_Event__Campaign_Group_2__c":null,"rC_Event__Campaign_Group_3__c":null,"rC_Event__Card_Expiration_Month__c":null,"rC_Event__Card_Expiration_Year__c":null,"rC_Event__Card_Holder_Name__c":null,"rC_Event__Card_Number__c":null,"rC_Event__Card_Security_Code__c":null,"rC_Event__Contact_Address_Type__c":null,"rC_Event__Email__c":null,"rC_Event__First_Name__c":null,"rC_Event__Guest_Of__c":null,"rC_Event__Is_Selected__c":false,"rC_Event__Item_1_Item_ID__c":null,"rC_Event__Item_1_Purchase_Price__c":null,"rC_Event__Item_1_Purchase_Quantity__c":null,"rC_Event__Item_2_Item_ID__c":null,"rC_Event__Item_2_Purchase_Price__c":null,"rC_Event__Item_2_Purchase_Quantity__c":null,"rC_Event__Item_3_Item_ID__c":null,"rC_Event__Item_3_Purchase_Price__c":null,"rC_Event__Item_3_Purchase_Quantity__c":null,"rC_Event__Item_4_Item_ID__c":null,"rC_Event__Item_4_Purchase_Price__c":null,"rC_Event__Item_4_Purchase_Quantity__c":null,"rC_Event__Item_5_Item_ID__c":null,"rC_Event__Item_5_Purchase_Price__c":null,"rC_Event__Item_5_Purchase_Quantity__c":null,"rC_Event__Item_ID_Quantity_Zero__c":null,"rC_Event__Last_Name__c":null,"rC_Event__Meal_1_Meal_ID__c":null,"rC_Event__Meal_1_Purchase_Price__c":null,"rC_Event__Meal_1_Purchase_Quantity__c":null,"rC_Event__Meal_1_Status__c":null,"rC_Event__Meal_2_Meal_ID__c":null,"rC_Event__Meal_2_Purchase_Price__c":null,"rC_Event__Meal_2_Purchase_Quantity__c":null,"rC_Event__Meal_2_Status__c":null,"rC_Event__Meal_3_Meal_ID__c":null,"rC_Event__Meal_3_Purchase_Price__c":null,"rC_Event__Meal_3_Purchase_Quantity__c":null,"rC_Event__Meal_3_Status__c":null,"rC_Event__Meal_4_Meal_ID__c":null,"rC_Event__Meal_4_Purchase_Price__c":null,"rC_Event__Meal_4_Purchase_Quantity__c":null,"rC_Event__Meal_4_Status__c":null,"rC_Event__Meal_5_Meal_ID__c":null,"rC_Event__Meal_5_Purchase_Price__c":null,"rC_Event__Meal_5_Purchase_Quantity__c":null,"rC_Event__Meal_5_Status__c":null,"rC_Event__Meal_ID_Quantity_Zero__c":null,"rC_Event__Member_Email__c":"debrago@gmail.com","rC_Event__Member_Name__c":"Debra Go","rC_Event__Member_Phone__c":null,"rC_Event__Member_Role__c":"Member","rC_Event__Parent_Campaign_Member__c":null,"rC_Event__Payment_Method__c":null,"rC_Event__Payment_Processor_Foreign_GUID__c":null,"rC_Event__Payment_Processor_GUID__c":null,"rC_Event__Payment_Processor__c":null,"rC_Event__Payment_Status__c":null,"rC_Event__Payment_Transaction_Time__c":null,"rC_Event__Phone__c":null,"rC_Event__Registered_Count__c":null,"rC_Event__Registered_Date__c":null,"rC_Event__Registered_Meal__c":null,"rC_Event__Registered_Seat__c":null,"rC_Event__Registered_Status__c":null,"rC_Event__Registered_Table_Name__c":null,"rC_Event__Registered_Table__c":null,"rC_Event__Registered_Venue_Name__c":null,"rC_Event__Registered_Venue_Seats__c":null,"rC_Event__Registered_Venue__c":null,"rC_Event__Salutation__c":null,"rC_Event__Shipping_City__c":null,"rC_Event__Shipping_Country__c":null,"rC_Event__Shipping_Postal_Code__c":null,"rC_Event__Shipping_State__c":null,"rC_Event__Shipping_Street_Line_1__c":null,"rC_Event__Shipping_Street_Line_2__c":null,"rC_Event__Shipping_Street__c":null,"rC_Event__Ticket_1_Discount_Code__c":null,"rC_Event__Ticket_1_Purchase_Price__c":null,"rC_Event__Ticket_1_Purchase_Quantity__c":null,"rC_Event__Ticket_1_Status__c":null,"rC_Event__Ticket_1_Ticket_ID__c":null,"rC_Event__Ticket_2_Discount_Code__c":null,"rC_Event__Ticket_2_Purchase_Price__c":null,"rC_Event__Ticket_2_Purchase_Quantity__c":null,"rC_Event__Ticket_2_Status__c":null,"rC_Event__Ticket_2_Ticket_ID__c":null,"rC_Event__Ticket_3_Discount_Code__c":null,"rC_Event__Ticket_3_Purchase_Price__c":null,"rC_Event__Ticket_3_Purchase_Quantity__c":null,"rC_Event__Ticket_3_Status__c":null,"rC_Event__Ticket_3_Ticket_ID__c":null,"rC_Event__Ticket_4_Discount_Code__c":null,"rC_Event__Ticket_4_Purchase_Price__c":null,"rC_Event__Ticket_4_Purchase_Quantity__c":null,"rC_Event__Ticket_4_Status__c":null,"rC_Event__Ticket_4_Ticket_ID__c":null,"rC_Event__Ticket_5_Discount_Code__c":null,"rC_Event__Ticket_5_Purchase_Price__c":null,"rC_Event__Ticket_5_Purchase_Quantity__c":null,"rC_Event__Ticket_5_Status__c":null,"rC_Event__Ticket_5_Ticket_ID__c":null,"rC_Event__Ticket_ID_Quantity_Zero__c":null,"Continue_This_Position__c":null,"Why_are_you_unsure__c":null,"Membership__c":"006Z0000008EDHGIA4","Account__c":null,"Account_Type__c":null,"Active__c":true,"Start_Date__c":null,"End_Date__c":null,"Primary__c":true,"Outreach__c":false,"Participation__c":null,"Assignment_Type__c":"Volunteer","Membership_Status__c":"Payment Pending","Display_Renewal__c":false,"Date_Active__c":null,"Renewable__c":false,"Special_Handling__c":false,"Unqualified__c":false,"X2015_Volunteer_Job_Renewal__c":false,"rC_Connect__Ask_1_Amount__c":null,"rC_Connect__Ask_2_Amount__c":null,"rC_Connect__Ask_3_Amount__c":null,"rC_Connect__Ask_4_Amount__c":null,"rC_Connect__Ask_5_Amount__c":null,"rC_Connect__Campaign_Ask__c":null,"rC_Connect__Has_Volunteered__c":false,"rC_Connect__Hours_Voluntered__c":null,"rC_Connect__Opportunity__c":null,"External_ID__c":null,"Special_Handling_Status__c":null,"Welcome__c":false,"Boomi_Council_Code_Reference__c":null}