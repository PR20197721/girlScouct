package org.girlscouts.vtk.impl.servlets;

import java.io.DataOutputStream;
import java.net.URL;
import java.util.Dictionary;

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
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.girlscouts.vtk.auth.dao.SalesforceDAO;
import org.girlscouts.vtk.auth.dao.SalesforceDAOFactory;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.auth.models.User;
import org.girlscouts.vtk.ejb.TroopUtil;
//import org.girlscouts.vtk.dao.UserDAO;
import org.girlscouts.vtk.helpers.ConfigListener;
import org.girlscouts.vtk.helpers.ConfigManager;
import org.girlscouts.vtk.helpers.CouncilMapper;
import org.girlscouts.vtk.salesforce.Troop;
import org.girlscouts.vtk.utils.VtkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(label = "Girl Scouts VTK Salesforce Authentication Servlet", description = "Handles OAuth Authentication with Salesforce", metatype = true, immediate = true)
@Service
@Properties({
		@Property(propertyPrivate = true, name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
		@Property(propertyPrivate = true, name = "sling.servlet.selectors", value = "sfauth"),
		@Property(propertyPrivate = true, name = "sling.servlet.extensions", value = "html"),
		@Property(propertyPrivate = true, name = "sling.servlet.methods", value = "GET") })
public class SalesforceAuthServlet extends SlingSafeMethodsServlet implements
		ConfigListener {
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
		
		// if(true){ autoLogin(request, response); return; }

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
		String redirectUrl;
		
		

		if (config == null || config.getId() == null) {
			
			String refererCouncil = request.getParameter("refererCouncil");
		    if (refererCouncil == null) {
			           refererCouncil = "";
            }
			
			redirectUrl = OAuthUrl
					+ "/services/oauth2/authorize?prompt=login&response_type=code&client_id="
					+ clientId + "&redirect_uri=" + callbackUrl + "&state="
					+ refererCouncil;
		} else {
			redirectUrl = targetUrl;
		}

		redirect(response, redirectUrl);
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

			try {
				isLogoutWeb = logoutWeb(apiConfig);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				String councilId = Integer.toString(apiConfig.getTroops().get(0).getCouncilCode());
				if( councilId==null || councilId.trim().equals("") )
					redirectUrl = councilMapper.getCouncilUrl(VtkUtil.getCouncilInClient(request));
				else
					redirectUrl = councilMapper.getCouncilUrl(councilId);
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
System.err.println("tataggg: "+ redirectUrl);
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
		redirect(response, redirectUrl);
	}

	private void salesforceCallback(SlingHttpServletRequest request,
			SlingHttpServletResponse response) {
		HttpSession session = request.getSession();

		// save refererCouncil
		String refererCouncil = request.getParameter("state");
		if (refererCouncil != null && !refererCouncil.isEmpty()) {
		    session.setAttribute("refererCouncil", refererCouncil);
		}
		
		ApiConfig apiConfig = null;
		try {
			apiConfig = (ApiConfig) session.getAttribute(ApiConfig.class
					.getName());

		} catch (java.lang.ClassCastException e) {
			e.printStackTrace();
		}
		// if ((ApiConfig)session.getAttribute(ApiConfig.class.getName()) !=
		// null) {
		if (apiConfig != null) {
			log.error("In Salesforce callback but the ApiConfig already exists. Redirect.");
			redirect(response, targetUrl);
		}

		String code = request.getParameter(CODE);
		if (code == null) {
			log.error("In Salesforce callback but \"code\" parameter not returned. Quit.");
			return;
		}else
			setCouncilInClient(response, request.getParameter("state") );
System.err.println("Checking cookie: "+ VtkUtil.getCouncilInClient(request));
		SalesforceDAO dao = salesforceDAOFactory.getInstance();
		ApiConfig config = dao.doAuth(code);
		session.setAttribute(ApiConfig.class.getName(), config);

		// sf user - should not be in session. move into vtk.User
		User user = dao.getUser(config);
		session.setAttribute(User.class.getName(), user);
		config.setUser(user);

		org.girlscouts.vtk.models.User vtkUser = new org.girlscouts.vtk.models.User();
		vtkUser.setApiConfig(config);
		if(config.getTroops()!=null && config.getTroops().size()>0){
				// CHN to LOAD PERMISSION HERE
				vtkUser.setPermissions(config.getTroops().get(0).getPermissionTokens());
		
				// load config
				vtkUser.setCurrentYear(getCurrentYear(request.getResourceResolver(),
						vtkUser.getApiConfig().getTroops().get(0).getCouncilCode()));
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
					+ "/secur/logout.jsp?display=touch"; // DYNAMIC

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

	private void autoLogin(SlingHttpServletRequest request,
			SlingHttpServletResponse response) {
		HttpSession session = request.getSession();

		ApiConfig config = new ApiConfig();
		config.setId("test");
		config.setAccessToken("test");
		config.setInstanceUrl("etst");
		config.setUserId("userId");
		config.setUser(new User());

		java.util.List<Troop> troops = new java.util.ArrayList();
		Troop troop = new Troop();
		troop.setCouncilCode(1);
		troop.setGradeLevel("1-Brownie");
		troop.setTroopId("troopId");
		troop.setTroopName("test");

		troops.add(troop);
		config.setTroops(troops);

		session.setAttribute(ApiConfig.class.getName(), config);

		String redirectUrl;

		// redirect(response,
		// "http://localhost:4503/content/girlscouts-vtk/en/vtk.html");
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
	
	
	public void setCouncilInClient(org.apache.sling.api.SlingHttpServletResponse response, String councilCode){
		Cookie cookie = new Cookie("vtk_referer_council", councilCode);
	    cookie.setMaxAge(-1);
	    response.addCookie(cookie);
	}
	/*
	public String getCouncilInClient(org.apache.sling.api.SlingHttpServletRequest request){
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			theCookie: for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equals("vtk_referer_council")) {
					
							return cookies[i].getValue();
						

				}

			}
		}
		return null;
	}
*/
}
