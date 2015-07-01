package org.girlscouts.vtk.impl.servlets;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.rmi.ServerException;
import java.security.cert.CertificateException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.girlscouts.vtk.auth.dao.SalesforceDAO;
import org.girlscouts.vtk.auth.dao.SalesforceDAOFactory;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.auth.models.User;
import org.girlscouts.vtk.ejb.TroopUtil;
import org.girlscouts.vtk.helpers.ConfigListener;
import org.girlscouts.vtk.helpers.ConfigManager;
import org.girlscouts.vtk.helpers.CouncilMapper;
import org.girlscouts.vtk.sso.AccountSettings;
import org.girlscouts.vtk.sso.AppSettings;
import org.girlscouts.vtk.sso.saml.AuthRequest;
import org.girlscouts.vtk.utils.VtkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(label = "Girl Scouts VTK Salesforce Authentication Servlet", description = "Handles OAuth Authentication with Salesforce", metatype = true, immediate = true)
@Service
@Properties({
		@Property(propertyPrivate = true, name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
		@Property(propertyPrivate = true, name = "sling.servlet.selectors", value = "sfauth"),
		@Property(propertyPrivate = true, name = "sling.servlet.extensions", value = "html"),
		@Property(propertyPrivate = true, name = "sling.servlet.methods", value = {
				"POST", "GET" })
				 })
		//@Property(propertyPrivate = true, name = "sling.servlet.methods", value = "GET") })
public class SalesforceAuthServlet extends SlingAllMethodsServlet implements
		ConfigListener {
	//extends SlingSafeMethodsServlet
	private static final long serialVersionUID = 8152897311719564370L;

	private static final Logger log = LoggerFactory
			.getLogger(SalesforceAuthServlet.class);
	private static final String ACTION = "action";
	private static final String SIGNIN = "signin";
	private static final String SIGNOUT = "signout";
	// Salesforce callback code
	private static final String CODE = "code";
	private String OAuthUrl;
	private String clientId;
	private String callbackUrl;
	private String targetUrl;

	@Reference
	private ConfigManager configManager;

	@Reference
	private SalesforceDAOFactory salesforceDAOFactory;

	@Reference
	private CouncilMapper councilMapper;

	@Reference
	private TroopUtil troopUtil;

	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	private ResourceResolver resourceResolver;

	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) {
		String action = request.getParameter(ACTION);
		if (action == null) {
			salesforceCallback(request, response);
		} else if (action.equals(SIGNIN)) {
			signIn(request, response);
		} else if (action.equals(SIGNOUT)) {
			signOut(request, response);
		} else {
			log.error("Unsupported action: " + action);
		}
	}

	@SuppressWarnings("rawtypes")
	public void updateConfig(Dictionary configs) {
		OAuthUrl = (String) configs.get("OAuthUrl");
		clientId = (String) configs.get("clientId");
		callbackUrl = (String) configs.get("callbackUrl");
		targetUrl = (String) configs.get("targetUrl");
	}

	@Activate
	public void init() {
		configManager.register(this);
		try {
			resourceResolver = resourceResolverFactory
					.getResourceResolver(null);
		} catch (LoginException e) {
			log.error("Cannot get ResourceResolver.");
		}
	}

	private void signInOld(SlingHttpServletRequest request,
			SlingHttpServletResponse response) {
		HttpSession session = request.getSession();
		
		// Set referer council
		String refererCouncil = request.getParameter("refererCouncil");
		if (refererCouncil == null) {
		    refererCouncil = "";
		}
		
		ApiConfig config = null;
		try {
			config = (ApiConfig) session
					.getAttribute(ApiConfig.class.getName());
		} catch (Exception e) {
		}
		String redirectUrl;
		if (config == null || config.getId() == null) {
/*
			String refererCouncil = request.getParameter("refererCouncil");
			if (refererCouncil == null) {
				refererCouncil = "";
			}
*/
			
			redirectUrl = OAuthUrl
					+ "/services/oauth2/authorize?prompt=login&response_type=code&client_id="
					+ clientId + "&redirect_uri=" + callbackUrl + "&state="
					+ refererCouncil;
					
		} else {
			redirectUrl = targetUrl;
		}
		redirect(response, redirectUrl);
	}
	
	
	private void signIn(SlingHttpServletRequest request,
			SlingHttpServletResponse response) {
		HttpSession session = request.getSession();
	
		// Set referer council
		String refererCouncil = request.getParameter("refererCouncil");
		if (refererCouncil == null) {
		    refererCouncil = "";
		}
		
		ApiConfig config = null;
		try {
			config = (ApiConfig) session
					.getAttribute(ApiConfig.class.getName());
		} catch (Exception e) {
		}
		
		/*
		String redirectUrl;
		if (config == null || config.getId() == null) {

			
			redirectUrl = OAuthUrl
					+ "/services/oauth2/authorize?prompt=login&response_type=code&client_id="
					+ clientId + "&redirect_uri=" + callbackUrl + "&state="
					+ refererCouncil;
					
		} else {
			redirectUrl = targetUrl;
		}
		redirect(response, redirectUrl);
		*/
		
		
		
		 AppSettings appSettings = new AppSettings();
		 //appSettings.setAssertionConsumerServiceUrl("http://localhost:4503/content/girlscouts-vtk/controllers/auth.sfauth.html");  
		 appSettings.setAssertionConsumerServiceUrl(callbackUrl);
		 appSettings.setIssuer(configManager.getConfig("ssoIssuer"));//"https://gsusa--gsuat.cs11.my.salesforce.com");
		
		  AccountSettings accSettings = new AccountSettings();
		  accSettings.setIdpSsoTargetUrl(configManager.getConfig("idpSsoTargetUrl"));// "https://gsuat-gsmembers.cs11.force.com/members/idp/login?app=0spZ0000000004h");

		  AuthRequest authReq = new AuthRequest(appSettings, accSettings);
		  
		  try{      
			  
			  System.err.println("xtest refereCoun: "+ refererCouncil);
			  //String reqString = authReq.getSSOurl(relayState);
			  String reqString = authReq.getSSOurl(refererCouncil);
			  System.err.println("******** xtest: "+  reqString);
			  System.err.println("CallbackUrl: "+ callbackUrl);
			  //response.sendRedirect(callbackUrl);
			  response.sendRedirect(reqString);
		  }catch(Exception e){e.printStackTrace();}
		
		
	}

	private void signOut(SlingHttpServletRequest request,
			SlingHttpServletResponse response) {
		boolean isLogoutApi = false, isLogoutWeb = false;
		HttpSession session = request.getSession();
	
	
		try {
			troopUtil.logout(((org.girlscouts.vtk.models.User) session
					.getAttribute(org.girlscouts.vtk.models.User.class
							.getName())),
					(org.girlscouts.vtk.models.Troop) session
							.getAttribute("VTK_troop"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ApiConfig apiConfig = (ApiConfig) session.getAttribute(ApiConfig.class
				.getName());
		
		String redirectUrl = null;
		if (apiConfig != null) {
			try {
				isLogoutApi = logoutApi(apiConfig, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				logoutApi(apiConfig, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			/*
			try {
				isLogoutWeb = logoutWeb(apiConfig);
			} catch (Exception e) {
				e.printStackTrace();
			}
			*/
			
			try {
				String councilId = Integer.toString(apiConfig.getTroops()
						.get(0).getCouncilCode());


//System.err.println("REFCOUN: "+ (String)session.getAttribute("refererCouncil") +" : "+ VtkUtil.getCouncilInClient(request)+" : " +councilId )	;	


				
				if (councilId == null || councilId.trim().equals("")){
					redirectUrl = councilMapper.getCouncilUrl(VtkUtil
							.getCouncilInClient(request));
					
				}else{
					redirectUrl = councilMapper.getCouncilUrl(councilId);
	
					}
			} catch (Exception e) {
				
				
			    String refererCouncil = (String)session.getAttribute("refererCouncil");
			    
	    
			    if (refererCouncil != null  && !refererCouncil.isEmpty()) {
			        redirectUrl = "/content/" + refererCouncil + "/";
			        redirectUrl = resourceResolver.map(redirectUrl);
			    }
			}
		}
		if (redirectUrl == null) {
			
				redirectUrl = councilMapper.getCouncilUrl();
			
		}

		// TODO: language?
		redirectUrl += "en.html";
		try {
			session.invalidate();
		} catch (IllegalStateException e) {
			// Catch request sent twice
		}
		session = null;
		apiConfig = null;
		redirectUrl = redirectUrl.contains("?") ? (redirectUrl = redirectUrl
				+ "&isSignOutSalesForce=true") : (redirectUrl = redirectUrl
				+ "?isSignOutSalesForce=true");
		
		//baseUrl: config in CRXED /etc/map.publish.dev/http/alex.gsnetx.org.80 
		redirectUrl= resourceResolver.map(redirectUrl);	
		redirectUrl = configManager.getConfig("communityUrl")+"/VTKLogout?redirectSource=" + java.net.URLEncoder.encode(redirectUrl);
		redirect(response, redirectUrl);
	}

	
	
	 @Override
protected void doPost(SlingHttpServletRequest request,
		SlingHttpServletResponse response) throws ServerException,
		IOException {


System.err.println("POSTX***********"+ request.getParameter("RelayState"));
/*
Enumeration enParams = request.getParameterNames(); 
while(enParams.hasMoreElements()){
String paramName = (String)enParams.nextElement();
System.out.println("Attribute Name - "+paramName+", Value - "+request.getParameter(paramName));
}
*/
String certificateS=configManager.getConfig("ssoCertificate");//"MIIErDCCA5SgAwIBAgIOAUpP+IcIAAAAADe+GfMwDQYJKoZIhvcNAQEFBQAwgZAxKDAmBgNVBAMMH1NlbGZTaWduZWRDZXJ0XzE1RGVjMjAxNF8yMjAxMzQxGDAWBgNVBAsMDzAwRFowMDAwMDBNaWEwNjEXMBUGA1UECgwOU2FsZXNmb3JjZS5jb20xFjAUBgNVBAcMDVNhbiBGcmFuY2lzY28xCzAJBgNVBAgMAkNBMQwwCgYDVQQGEwNVU0EwHhcNMTQxMjE1MjIwMTM1WhcNMTYxMjE0MjIwMTM1WjCBkDEoMCYGA1UEAwwfU2VsZlNpZ25lZENlcnRfMTVEZWMyMDE0XzIyMDEzNDEYMBYGA1UECwwPMDBEWjAwMDAwME1pYTA2MRcwFQYDVQQKDA5TYWxlc2ZvcmNlLmNvbTEWMBQGA1UEBwwNU2FuIEZyYW5jaXNjbzELMAkGA1UECAwCQ0ExDDAKBgNVBAYTA1VTQTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAN5j2MSAwSC8LK4vUQ7TI1WlxCd4WSf23+L3PKEIHkMp406/W9HQ3xaPoWSpGaAejIErcdFKJ21T8X6I9zpivceIReW57/v+GsPeq+UiAuie4C1tkQsC/+R3rC3YXwdzmAU0nmYhNw3DtDXpw5AVxOGLYSBiN54q2/b6jd0VBU+esJuAkb7JUe+DiY+xjflT1rxuyrM9vmbVC4s/tninYITirQ21A4+bgiKpSPJZPryrUPfD4/9GgT7YZz43pwym6d4eaDghcdZTknUPNAtYHRVY/mgpEucT5qoAnhAU8Knzpdonu6nCy/eZAZ27EZaGCPyH6g38NlnD3SK90/tZYFkCAwEAAaOCAQAwgf0wHQYDVR0OBBYEFNX9rNUaXacYj241ABzrmycw48C6MIHKBgNVHSMEgcIwgb+AFNX9rNUaXacYj241ABzrmycw48C6oYGWpIGTMIGQMSgwJgYDVQQDDB9TZWxmU2lnbmVkQ2VydF8xNURlYzIwMTRfMjIwMTM0MRgwFgYDVQQLDA8wMERaMDAwMDAwTWlhMDYxFzAVBgNVBAoMDlNhbGVzZm9yY2UuY29tMRYwFAYDVQQHDA1TYW4gRnJhbmNpc2NvMQswCQYDVQQIDAJDQTEMMAoGA1UEBhMDVVNBgg4BSk/4hzIAAAAAN74Z8zAPBgNVHRMBAf8EBTADAQH/MA0GCSqGSIb3DQEBBQUAA4IBAQCIRc5HjrN15ZzB7talPEJzYqVhZP0z8gd9BAN5FbFpb488ai57KzhrfTH7f5wVAquFm4OaXAnfizJeAEgmMw4Abp//Dt+/WWDMkM6goUREotAF3vNcoFuYHBmrMyyxMIY4x7HryIB0lOEjn1DOh0+8G7kz2gkZ52BxlD/3tWnXUwasZ+TK9ZYniTUnjArPlwb8k5aUyzcCjWdZ/oNpU4x0slJzHgm89IpT0oqOKj4n0Pl8I/o7EhHoKSzFQDP02XF26r2poudJkS+OQb/p/U7MdnruBIiGFrQ80RGEg81TRTbmN+xpf6h1mXAuwmAlc0O0ezv+pgrLxaz+wLYAogDP";
org.girlscouts.vtk.sso.AccountSettings accountSettings = new org.girlscouts.vtk.sso.AccountSettings();
accountSettings.setCertificate(certificateS);


org.girlscouts.vtk.sso.saml.Response samlResponse = null;
try {
samlResponse = new org.girlscouts.vtk.sso.saml.Response(accountSettings);
} catch (CertificateException e1) {
// TODO Auto-generated catch block
e1.printStackTrace();
}
String token= null, userId= null;
try {
	System.err.println("TATATATAT>>> "+ request.getParameter("SAMLResponse"));
	System.out.println("xSaml target: "+ targetUrl);
	 samlResponse.loadXmlFromBase64(request.getParameter("SAMLResponse"));
	 samlResponse.setDestinationUrl(request.getRequestURL().toString());
	  if (samlResponse.isValid()) {
	
		 token =samlResponse.getNameId(); //samlResponse.getToken(request.getParameter("SAMLResponse"));
		 userId= samlResponse.getUserId(request.getParameter("SAMLResponse"));//samlResponse.getUserId();
	  }else{
		  
		  try{
		        response.setStatus(500);
		        return;
		    } catch (Exception exx) {
		      exx.printStackTrace();
		    }
	  }
} catch (Exception e) {
// TODO Auto-generated catch block
	
	try{
        response.setStatus(500);
        return;
    } catch (Exception exx) {
      exx.printStackTrace();
    }
	
e.printStackTrace();
}
System.out.println("xSaml token: " + token +" : "+  targetUrl);
//response.sendRedirect("http://localhost:4503/content/girlscouts-vtk/en/vtk.html");
//redirect(response, targetUrl);



setCouncilInClient(response, request.getParameter("state"));
SalesforceDAO dao = salesforceDAOFactory.getInstance();
//ApiConfig config = dao.doAuth(code);

ApiConfig config = new ApiConfig();
config.setAccessToken(token);
config.setInstanceUrl(configManager.getConfig("ssoWebServiceUrl"));//"https://gsusa--gsuat.cs11.my.salesforce.com");
config.setWebServicesUrl(configManager.getConfig("ssoWebServiceUrl"));//"https://gsuat-gsmembers.cs11.force.com/members");
String refreshTokenStr = null;
/*
try {
	refreshTokenStr = authResponse.getString("refresh_token");
} catch (Exception npe) {
	// skip refresh token
	log.debug("Skipping refresh token because SF is not providing it");
}
*/
String id =userId;// "005Z0000002PPYb";//"005Z0000002PPYbIAO";
config.setId(id);
config.setUserId(id.substring(id.lastIndexOf("/") + 1));
if (refreshTokenStr != null) {
	config.setRefreshToken(refreshTokenStr);
}
config.setCallbackUrl(callbackUrl);
config.setClientId(clientId);
//config.setClientSecret(clientSecret);
config.setOAuthUrl(OAuthUrl);

HttpSession session = request.getSession();
session.setAttribute(ApiConfig.class.getName(), config);
User user = null;
try{ user=dao.getUser(config);  }catch(Exception e){e.printStackTrace();}
if(user==null){
	 response.setStatus(500);
	 return;
}
session.setAttribute(User.class.getName(), user);
config.setUser(user);
org.girlscouts.vtk.models.User vtkUser = new org.girlscouts.vtk.models.User();
vtkUser.setApiConfig(config);
if (config.getTroops() != null && config.getTroops().size() > 0) {
	// CHN to LOAD PERMISSION HERE
	vtkUser.setPermissions(config.getTroops().get(0)
			.getPermissionTokens());

	// load config
	vtkUser.setCurrentYear(getCurrentYear(
			request.getResourceResolver(), vtkUser.getApiConfig()
					.getTroops().get(0).getCouncilCode()));
}
session.setAttribute(org.girlscouts.vtk.models.User.class.getName(),
		vtkUser);
redirect(response, targetUrl);
}
	
	
	
	private void salesforceCallback(SlingHttpServletRequest request,
			SlingHttpServletResponse response) {
		
		HttpSession session = request.getSession();

		ApiConfig apiConfig = null;
		try {
			apiConfig = (ApiConfig) session.getAttribute(ApiConfig.class
					.getName());
		} catch (java.lang.ClassCastException e) {
			e.printStackTrace();
		}

		if (apiConfig != null) {
			log.error("In Salesforce callback but the ApiConfig already exists. Redirect.");
			redirect(response, targetUrl);
		}

		String code = request.getParameter(CODE);
		if (code == null) {
			log.error("In Salesforce callback but \"code\" parameter not returned. Quit.");
			return;
		} else
			setCouncilInClient(response, request.getParameter("state"));
		SalesforceDAO dao = salesforceDAOFactory.getInstance();
		ApiConfig config = dao.doAuth(code);
		session.setAttribute(ApiConfig.class.getName(), config);
		User user = null;
		try{user= dao.getUser(config);}catch(Exception e){e.printStackTrace();}
		if( user==null ){ response.setStatus(500); return;}
		
		session.setAttribute(User.class.getName(), user);
		config.setUser(user);
		org.girlscouts.vtk.models.User vtkUser = new org.girlscouts.vtk.models.User();
		vtkUser.setApiConfig(config);
		if (config.getTroops() != null && config.getTroops().size() > 0) {
			// CHN to LOAD PERMISSION HERE
			vtkUser.setPermissions(config.getTroops().get(0)
					.getPermissionTokens());

			// load config
			vtkUser.setCurrentYear(getCurrentYear(
					request.getResourceResolver(), vtkUser.getApiConfig()
							.getTroops().get(0).getCouncilCode()));
		}
		session.setAttribute(org.girlscouts.vtk.models.User.class.getName(),
				vtkUser);
		redirect(response, targetUrl);
	}

	private void redirect(SlingHttpServletResponse response, String redirectUrl) {
		try {
			response.sendRedirect(redirectUrl);
		} catch (Exception e) {
			// TODO: What to do here? Send 500?
			log.error("Error while sending redirect: " + redirectUrl);
		}
	}

	// aPI logout
	public boolean logoutApi(ApiConfig apiConfig, boolean isRefreshToken)
			throws Exception {
		DataOutputStream wr = null;
		boolean isSucc = false;
		URL obj = null;
		HttpsURLConnection con = null;
		try {
			String url = "https://test.salesforce.com/services/oauth2/revoke"; // DYNAMIC
			obj = new URL(url);
			con = (HttpsURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			String urlParameters = "token=" + apiConfig.getAccessToken();
			con.setDoOutput(true);
			wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			int responseCode = con.getResponseCode();
			if (responseCode == 200)
				isSucc = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				wr = null;
				obj = null;
				con = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return isSucc;
	}

	// web salesforce logout
	public boolean logoutWeb(ApiConfig apiConfig) throws Exception {
		DataOutputStream wr = null;
		boolean isSucc = false;
		URL obj = null;
		HttpsURLConnection con = null;
		try {
			String url = apiConfig.getInstanceUrl()
					+ "/secur/logout.jsp"; // DYNAMIC
			
	System.err.println("......"+apiConfig.getInstanceUrl()+ "/secur/logout.jsp");		
	
	
	url ="http://gsuat-gsmembers.cs11.force.com/members/VTKLogout?redirectSource=http://localhost:4503/content/girlscouts-vtk/en/vtk.home.html";
	System.err.println("TEsting logout");
			obj = new URL(url);
			con = (HttpsURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			/*
			con.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			String urlParameters = "token=" + apiConfig.getAccessToken();
			*/
			con.setDoOutput(true);
			wr = new DataOutputStream(con.getOutputStream());
			//wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			int responseCode = con.getResponseCode();
			System.err.println("testing logout respCode: "+ responseCode);
			if (responseCode == 200){
				isSucc = true;
			
			System.err.println("testing logout succ");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				wr = null;
				obj = null;
				con = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return isSucc;
	}

	private String getCurrentYear(ResourceResolver resourceResolver,
			int councilId) {
		String elem = null;
		try {
			String branch = councilMapper.getCouncilBranch(councilId + "");
			branch += "/jcr:content";
			ValueMap valueMap = (ValueMap) resourceResolver.resolve(branch)
					.adaptTo(ValueMap.class);
			elem = valueMap.get("currentYear", "");
		} catch (Exception e) {
			System.err.println("SalesforceAuthServlet: Current Year not set");
			e.printStackTrace();
		}
		return (elem == null || elem.trim().equals("")) ? getCurrentYearDefault(resourceResolver)
				: elem;

	}

	private String getCurrentYearDefault(ResourceResolver resourceResolver) {
		String elem = null;
		try {
			String branch = "/content/gateway/jcr:content";
			ValueMap valueMap = (ValueMap) resourceResolver.resolve(branch)
					.adaptTo(ValueMap.class);
			elem = valueMap.get("currentYear", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return elem;
	}

	public void setCouncilInClient(
			org.apache.sling.api.SlingHttpServletResponse response,
			String councilCode) {
		Cookie cookie = new Cookie("vtk_referer_council", councilCode);
		cookie.setMaxAge(-1);
		response.addCookie(cookie);
	}
}
