package org.girlscouts.vtk.impl.servlets;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.rmi.ServerException;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
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
import org.girlscouts.vtk.ejb.UserUtil;
import org.girlscouts.vtk.ejb.VtkError;
import org.girlscouts.vtk.helpers.ConfigListener;
import org.girlscouts.vtk.helpers.ConfigManager;
import org.girlscouts.vtk.helpers.CouncilMapper;
import org.girlscouts.vtk.helpers.TroopHashGenerator;
import org.girlscouts.vtk.sso.AccountSettings;
import org.girlscouts.vtk.sso.AppSettings;
import org.girlscouts.vtk.sso.saml.AuthRequest;
import org.girlscouts.vtk.utils.VtkException;
import org.girlscouts.vtk.utils.VtkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(metatype = true, immediate = true)
@Service
@Properties({
	@Property(propertyPrivate = true, name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
	@Property(propertyPrivate = true, name = "sling.servlet.selectors", value = "sfauth"),
	@Property(propertyPrivate = true, name = "sling.servlet.extensions", value = "html"),
	@Property(propertyPrivate = true, name = "sling.servlet.methods", value = { "POST", "GET" }),
        @Property(name="label", value="Girl Scouts VTK Salesforce Authentication Servlet"),
        @Property(name="description", value="Girl Scouts VTK Salesforce Authentication Servlet")
})
// @Property(propertyPrivate = true, name = "sling.servlet.methods", value =
// "GET") })
public class SalesforceAuthServlet extends SlingAllMethodsServlet implements
		ConfigListener {
	// extends SlingSafeMethodsServlet
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
	private String vtkApiTroopUri;
	private String vtkApiUserUri;
	private String vtkApiContactUri;
	private String vtkApiTroopLeadersUri;

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

	@Reference
	private TroopHashGenerator troopHashGenerator;
	
	@Reference
	private UserUtil userUtil;
	
	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) {
	
		String action = request.getParameter(ACTION);
		if (action == null) {
			
			;

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
		vtkApiTroopUri= (String) configs.get("vtkApiTroopUri");
		vtkApiUserUri= (String) configs.get("vtkApiUserUri");
		vtkApiContactUri= (String) configs.get("vtkApiContactUri"); 
		vtkApiTroopLeadersUri= (String) configs.get("vtkApiTroopLeadersUri");
		
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

		System.err.println("testA1");

		// Set referer council
		String refererCouncil = request.getParameter("refererCouncil");
		if (refererCouncil == null) {
			refererCouncil = "";
		}
		System.err.println("testA2");
		ApiConfig config = null;
		try {
			config = (ApiConfig) session
					.getAttribute(ApiConfig.class.getName());
		} catch (Exception e) {
		}
		System.err.println("testA3");
		AppSettings appSettings = new AppSettings();
		appSettings.setAssertionConsumerServiceUrl(callbackUrl);
		appSettings.setIssuer(configManager.getConfig("ssoIssuer"));// "https://gsusa--gsuat.cs11.my.salesforce.com");
		System.err.println("testA4 "+configManager.getConfig("ssoIssuer") );
		AccountSettings accSettings = new AccountSettings();
		accSettings.setIdpSsoTargetUrl(configManager
				.getConfig("idpSsoTargetUrl"));
		System.err.println("testA5");
		AuthRequest authReq = new AuthRequest(appSettings, accSettings);
		try {
			System.err.println("testA6");
			String reqString = authReq.getSSOurl(refererCouncil);
			response.sendRedirect(reqString);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void signOut(SlingHttpServletRequest request,
			SlingHttpServletResponse response) {
		
		
		
		if(true){
			
			boolean isVtkLogin=false;
			if( request.getParameter("isVtkLogin")!=null && request.getParameter("isVtkLogin").equals("true"))
				isVtkLogin=true;
			
			redirect(response, configManager.getConfig("communityUrl")
					+ "/VTKLogout?redirectSource=" + java.net.URLEncoder.encode(configManager.getConfig("baseUrl") + "/content/girlscouts-vtk/controllers/vtk.logout.html"+ (isVtkLogin ? "?isVtkLogin=true": "" )));
			  return;
		}
			
		
		
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
	
				isLogoutApi = userUtil.logoutApi(apiConfig, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
	
				userUtil.logoutApi(apiConfig, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
	
			try {
				String councilId = Integer.toString(apiConfig.getTroops()
						.get(0).getCouncilCode());
				if (councilId == null || councilId.trim().equals("")) {
	
					redirectUrl = councilMapper.getCouncilUrl(VtkUtil
							.getCouncilInClient(request));

				} else {
					redirectUrl = councilMapper.getCouncilUrl(councilId);
						
				}
			} catch (Exception e) {
					
				String refererCouncil = (String) session
						.getAttribute("refererCouncil");

				if (refererCouncil != null && !refererCouncil.isEmpty()) {
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
			
		// baseUrl: config in CRXED /etc/map.publish.dev/http/alex.gsnetx.org.80
		redirectUrl = resourceResolver.map(redirectUrl);
		redirectUrl = configManager.getConfig("communityUrl")
				+ "/VTKLogout?redirectSource=" + configManager.getConfig("baseUrl") + "/content/girlscouts-vtk/en/vtk.logout.html";
		
		redirect(response, redirectUrl);
	}

	@Override
	protected void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServerException,
			IOException {			
		ApiConfig config =null;
		HttpSession session = request.getSession();
		session.setAttribute("fatalError", null);
	try{	
System.err.println("test1");	
		String certificateS = configManager.getConfig("ssoCertificate");
		org.girlscouts.vtk.sso.AccountSettings accountSettings = new org.girlscouts.vtk.sso.AccountSettings();
		accountSettings.setCertificate(certificateS);
		org.girlscouts.vtk.sso.saml.Response samlResponse = null;
		try {
			System.err.println("test2");		
			samlResponse = new org.girlscouts.vtk.sso.saml.Response(
					accountSettings);
			System.err.println("test3");	
		} catch (CertificateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.err.println("test4");	
		String token = null, userId = null;
		try {
			System.err.println("test5 RESP SAML param: "+ request.getParameter("SAMLResponse"));	
			log.debug("RESP SAML param: "+ request.getParameter("SAMLResponse"));
			samlResponse
					.loadXmlFromBase64(request.getParameter("SAMLResponse"));
System.err.println("test6 RESP SAML: "+ samlResponse);
			log.debug("RESP SAML: "+ samlResponse);	

			String requestURL = request.getRequestURL().toString();
System.err.println("test7: "+ requestURL);			
			if (!requestURL.startsWith("http://my-local")) {
System.err.println("test8 ");				
				requestURL = requestURL.replace("http://my", "https://my")
						.replace("http://girlscouts-dev2","https://girlscouts-dev2");
System.err.println("test9 "+requestURL);	
			}
System.err.println("test10");
			samlResponse.setDestinationUrl(requestURL);
System.err.println("test11");			
			if (samlResponse.isValid()) {
System.err.println("test12 valid");				
				token = samlResponse.getNameId();
System.err.println("test13 "+ token);				
				userId = samlResponse.getUserId(request
						.getParameter("SAMLResponse"));
System.err.println("test14 "+ userId);				
			} else {
				try {
	System.err.println("test15 samlResponse.isValid()==false returnning ....." );				
					response.setStatus(500);
					return;
				} catch (Exception exx) {
					exx.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				e.printStackTrace();
System.err.println("test16: returning 500 err");				
				response.setStatus(500);
				return;
			} catch (Exception exx) {
				exx.printStackTrace();
			}

			e.printStackTrace();
		}
		
System.err.println("test17");
			if( request.getParameter("RelayState")==null || (request.getParameter("RelayState")!=null && !request.getParameter("RelayState").contains("sfUserLanding") )){		
				System.err.println("test18");
		
				try{
					SalesforceDAO dao = salesforceDAOFactory.getInstance();
	System.err.println("test19 doa");				
					byte[] data = Base64.decodeBase64(configManager
							.getConfig("gsCertificate"));
	System.err.println("test20");				
					ByteArrayInputStream is = new ByteArrayInputStream(data);
	System.err.println("test21");				
					config = new org.girlscouts.vtk.sso.OAuthJWTHandler_v1()
							.getOAuthConfigs(is, token.substring(token.indexOf("@") + 1), clientId, configManager
									.getConfig("communityUrl"));
	System.err.println("test22");				
					config.setInstanceUrl(configManager.getConfig("ssoWebServiceUrl"));
					config.setWebServicesUrl(configManager.getConfig("ssoWebServiceUrl"));
					String refreshTokenStr = null;
					String id = userId;
	System.err.println("test23  "+ id);				
					config.setId(id);
	System.err.println("test24");				
					config.setUserId(id.substring(id.lastIndexOf("/") + 1));
					System.err.println("test25 " +  config.getUserId());				
					if (refreshTokenStr != null) {
						config.setRefreshToken(refreshTokenStr);
					}
					System.err.println("test26 "+ config.getRefreshToken());
					config.setCallbackUrl(callbackUrl);
					System.err.println("test27: "+ callbackUrl);
					config.setClientId(clientId);
					config.setOAuthUrl(OAuthUrl);
					
					//set config items here
					config.setVtkApiTroopUri( vtkApiTroopUri );
					config.setVtkApiUserUri( vtkApiUserUri );
					config.setVtkApiContactUri(vtkApiContactUri);
					config.setVtkApiTroopLeadersUri(vtkApiTroopLeadersUri);
					config.setAccessTokenValid(true);
			System.err.println("test28");		
					session.setAttribute(ApiConfig.class.getName(), config);
			System.err.println("test29");		
					User user = null;
					try {
						user = dao.getUser(config);
			System.err.println("test30 "+ (user==null));			
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (user == null) {
			System.err.println("test31 user is null");			
						response.setStatus(500);
						return;
					}
					session.setAttribute(User.class.getName(), user);
					config.setUser(user);
			System.err.println("test32");		
					
					
					
					org.girlscouts.vtk.models.User vtkUser = new org.girlscouts.vtk.models.User();
					vtkUser.setApiConfig(config);
			System.err.println("test33");		
					if (config.getTroops() != null && config.getTroops().size() > 0) {
			System.err.println("test34");
						
						// load config
						vtkUser.setCurrentYear(""+VtkUtil.getCurrentGSYear());
						System.err.println("test35 "+ vtkUser.getCurrentYear());			
						}
					session.setAttribute(org.girlscouts.vtk.models.User.class.getName(),
							vtkUser);
			System.err.println("test36");
				    // Set cookie troopDataPath 
					if (config.getTroops() != null && !config.getTroops().isEmpty()) {
						System.err.println("test37");				
					    String troopDataPath = troopHashGenerator.hash(config.getTroops().get(0));
					    Cookie cookie = new Cookie("troopDataToken", troopDataPath);
					    cookie.setPath("/");
					    response.addCookie(cookie);
					}
					
			 }catch(Exception e4){
				 e4.printStackTrace();
				 System.err.println("test37");
				 VtkError err= new VtkError();
				 err.setName("Error logging in");
				 err.setDescription("Error int SalesForceOAuthServet.doPost: found error while getting oAuth token from Salesforce. Exception : " + e4.toString());
				 err.setUserFormattedMsg("There appears to be an error in loggin. Please notify support with error code VTK-oAuth");
				 err.setErrorCode("VTK-oAuth");
				 err.addTarget("home");
				 session.setAttribute("fatalError", err);
				 System.err.println("test38");
				 response.sendRedirect("/content/girlscouts-vtk/en/vtk.home.html");
				 return;
				 
			 }
				System.err.println("test39");
		}//end oAuthtoken
			System.err.println("test40");
		if( request.getParameter("RelayState")!=null && (request.getParameter("RelayState").indexOf("http://")!=-1 || request.getParameter("RelayState").indexOf("https://")!=-1)) {
			    redirect(response, request.getParameter("RelayState"));
				System.err.println("test41");
		}else if(request.getParameter("RelayState")!=null){
			System.err.println("test42");
				setCouncilInClient(response, request.getParameter("RelayState"));
				redirect(response, targetUrl);
		}else {
			System.err.println("test43");
			    redirect(response, targetUrl);

		}
		System.err.println("test44");
	}catch(Exception e){
		 e.printStackTrace();
			System.err.println("test45");
		 VtkError err= new VtkError();
		 err.setName("Error logging in");
		 err.setDescription("Error int SalesForceOAuthServet.doPost: found error while SSO from Salesforce. Exception : " + e.toString());
		 err.setUserFormattedMsg("There appears to be an error in loggin. Please notify support with error code VTK-SSO");
		 err.setErrorCode("VTK-SSO");
		 err.addTarget("home");
		 session.setAttribute("fatalError", err);
			System.err.println("test46");
		 response.sendRedirect("/content/girlscouts-vtk/en/vtk.home.html");
		 return;
		 
	 }
	}

	private void salesforceCallback(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws IllegalAccessException {

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
		
		
		//set config items here
		config.setVtkApiTroopUri( vtkApiTroopUri );
		config.setVtkApiUserUri( vtkApiUserUri );
		config.setVtkApiContactUri(vtkApiContactUri);
		config.setVtkApiTroopLeadersUri(vtkApiTroopLeadersUri);
		
		session.setAttribute(ApiConfig.class.getName(), config);
		User user = null;
		try {
			user = dao.getUser(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (user == null) {
			response.setStatus(500);
			return;
		}

		session.setAttribute(User.class.getName(), user);
		config.setUser(user);
		org.girlscouts.vtk.models.User vtkUser = new org.girlscouts.vtk.models.User();
		vtkUser.setApiConfig(config);
		if (config.getTroops() != null && config.getTroops().size() > 0) {
		
			
		// Set cookie troopDataPath 
		String troopDataPath = troopHashGenerator.hash(config.getTroops().get(0));
		Cookie cookie = new Cookie("troopDataToken", troopDataPath);
		cookie.setPath("/");
		response.addCookie(cookie);

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

	
	// web salesforce logout
	public boolean logoutWeb(ApiConfig apiConfig) throws Exception {
		DataOutputStream wr = null;
		boolean isSucc = false;
		URL obj = null;
		HttpsURLConnection con = null;
		try {
			String url = apiConfig.getInstanceUrl() + "/secur/logout.jsp"; // DYNAMIC
			url = "http://gsuat-gsmembers.cs11.force.com/members/VTKLogout?redirectSource=http://localhost:4503/content/girlscouts-vtk/en/vtk.home.html";

			obj = new URL(url);
			con = (HttpsURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setDoOutput(true);
			wr = new DataOutputStream(con.getOutputStream());
			wr.flush();
			wr.close();
			int responseCode = con.getResponseCode();
			if (responseCode == 200) {
				isSucc = true;
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

	public void setCouncilInClient(
			org.apache.sling.api.SlingHttpServletResponse response,
			String councilCode) {
		Cookie cookie = new Cookie("vtk_referer_council", councilCode);
		cookie.setMaxAge(-1);
		cookie.setPath("/");
		response.addCookie(cookie);
	}
}
