package org.girlscouts.vtk.impl.servlets;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.girlscouts.vtk.helpers.ConfigListener;
import org.girlscouts.vtk.helpers.ConfigManager;
import org.girlscouts.vtk.helpers.CouncilMapper;
import org.girlscouts.vtk.helpers.TroopHashGenerator;
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
				"POST", "GET" }) })
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
			/*
			 * String refererCouncil = request.getParameter("refererCouncil");
			 * if (refererCouncil == null) { refererCouncil = ""; }
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

		AppSettings appSettings = new AppSettings();
		// appSettings.setAssertionConsumerServiceUrl("http://localhost:4503/content/girlscouts-vtk/controllers/auth.sfauth.html");
		appSettings.setAssertionConsumerServiceUrl(callbackUrl);
		appSettings.setIssuer(configManager.getConfig("ssoIssuer"));// "https://gsusa--gsuat.cs11.my.salesforce.com");

		AccountSettings accSettings = new AccountSettings();
		accSettings.setIdpSsoTargetUrl(configManager
				.getConfig("idpSsoTargetUrl"));// "https://gsuat-gsmembers.cs11.force.com/members/idp/login?app=0spZ0000000004h");

		AuthRequest authReq = new AuthRequest(appSettings, accSettings);

		try {
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
			/*
			 * try { isLogoutWeb = logoutWeb(apiConfig); } catch (Exception e) {
			 * e.printStackTrace(); }
			 */

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
/*
				    String refererCouncil = VtkUtil.getCouncilInClient(request);
				    if (refererCouncil != null && !refererCouncil.isEmpty()) {
				        redirectUrl = "/content/" + refererCouncil + "/";
				        redirectUrl = resourceResolver.map(redirectUrl);
				    }
				} else {
					redirectUrl = councilMapper.getCouncilUrl(councilId);
				}
			} catch (Exception e) {
				String refererCouncil = VtkUtil.getCouncilInClient(request);
			    if (refererCouncil != null  && !refererCouncil.isEmpty()) {
			        redirectUrl = "/content/" + refererCouncil + "/";
			        redirectUrl = resourceResolver.map(redirectUrl);
			    }
*/
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
				//+ java.net.URLEncoder.encode(configManager.getConfig("baseUrl")+redirectUrl);
		redirect(response, redirectUrl);
	}

	@Override
	protected void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServerException,
			IOException {	
		String certificateS = configManager.getConfig("ssoCertificate");
		org.girlscouts.vtk.sso.AccountSettings accountSettings = new org.girlscouts.vtk.sso.AccountSettings();
		accountSettings.setCertificate(certificateS);
		org.girlscouts.vtk.sso.saml.Response samlResponse = null;
		try {
			samlResponse = new org.girlscouts.vtk.sso.saml.Response(
					accountSettings);
		} catch (CertificateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String token = null, userId = null;
		try {
			samlResponse
					.loadXmlFromBase64(request.getParameter("SAMLResponse"));
			samlResponse.setDestinationUrl(request.getRequestURL().toString()
					.replace("http://my", "https://my").replace("http://girlscouts-dev2","https://girlscouts-dev2") );
					/*
					.replace("http://my-uat", "https://my-uat")
					.replace("http://my-stage", "https://my-stage") );
					*/
			if (samlResponse.isValid()) {
				token = samlResponse.getNameId();
				userId = samlResponse.getUserId(request
						.getParameter("SAMLResponse"));
			} else {
				try {
					response.setStatus(500);
					return;
				} catch (Exception exx) {
					exx.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				response.setStatus(500);
				return;
			} catch (Exception exx) {
				exx.printStackTrace();
			}

			e.printStackTrace();
		}
if( request.getParameter("RelayState")==null || (request.getParameter("RelayState")!=null && !request.getParameter("RelayState").contains("sfUserLanding") )){		
		setCouncilInClient(response, request.getParameter("state"));
		SalesforceDAO dao = salesforceDAOFactory.getInstance();
		byte[] data = Base64.decodeBase64(configManager
				.getConfig("gsCertificate"));
		ByteArrayInputStream is = new ByteArrayInputStream(data);
		ApiConfig config = new org.girlscouts.vtk.sso.OAuthJWTHandler_v1()
				.doIt(is, token.substring(token.indexOf("@") + 1), clientId, configManager
						.getConfig("communityUrl"));
		config.setInstanceUrl(configManager.getConfig("ssoWebServiceUrl"));
		config.setWebServicesUrl(configManager.getConfig("ssoWebServiceUrl"));
		String refreshTokenStr = null;
		String id = userId;
		config.setId(id);
		config.setUserId(id.substring(id.lastIndexOf("/") + 1));
		if (refreshTokenStr != null) {
			config.setRefreshToken(refreshTokenStr);
		}
		config.setCallbackUrl(callbackUrl);
		config.setClientId(clientId);
		config.setOAuthUrl(OAuthUrl);
		
		//set config items here
		config.setVtkApiTroopUri( vtkApiTroopUri );
		config.setVtkApiUserUri( vtkApiUserUri );
		config.setVtkApiContactUri(vtkApiContactUri);
		config.setVtkApiTroopLeadersUri(vtkApiTroopLeadersUri);
		
		HttpSession session = request.getSession();
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
			// CHN to LOAD PERMISSION HERE
			vtkUser.setPermissions(config.getTroops().get(0)
					.getPermissionTokens());
			
			// load config
			vtkUser.setCurrentYear(""+VtkUtil.getCurrentGSYear());
			/*
			vtkUser.setCurrentYear(getCurrentYear(
					request.getResourceResolver(), vtkUser.getApiConfig()
							.getTroops().get(0).getCouncilCode()));
		
			 */
			}
		session.setAttribute(org.girlscouts.vtk.models.User.class.getName(),
				vtkUser);
	}//end oAuthtoken
	if( request.getParameter("RelayState")!=null ){
			redirect(response, request.getParameter("RelayState"));
		}else
			redirect(response, targetUrl);
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
			// CHN to LOAD PERMISSION HERE
			vtkUser.setPermissions(config.getTroops().get(0)
					.getPermissionTokens());

			// load config
			/*
			vtkUser.setCurrentYear(getCurrentYear(
					request.getResourceResolver(), vtkUser.getApiConfig()
							.getTroops().get(0).getCouncilCode()));

		*/
			
			
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
