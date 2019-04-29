package org.girlscouts.vtk.sling.servlet;

import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.sso.saml.Response;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.ejb.TroopUtil;
import org.girlscouts.vtk.ejb.UserUtil;
import org.girlscouts.vtk.ejb.VtkError;
import org.girlscouts.vtk.osgi.component.ConfigListener;
import org.girlscouts.vtk.osgi.component.ConfigManager;
import org.girlscouts.vtk.osgi.component.CouncilMapper;
import org.girlscouts.vtk.osgi.component.TroopHashGenerator;
import org.girlscouts.vtk.osgi.service.GirlScoutsSalesForceService;
import org.girlscouts.vtk.sso.AccountSettings;
import org.girlscouts.vtk.sso.AppSettings;
import org.girlscouts.vtk.sso.saml.AuthRequest;
import org.girlscouts.vtk.utils.VtkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.rmi.ServerException;
import java.util.Dictionary;

@Component(metatype = true, immediate = true)
@Service
@Properties({
        @Property(propertyPrivate = true, name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
        @Property(propertyPrivate = true, name = "sling.servlet.selectors", value = "sfauth"),
        @Property(propertyPrivate = true, name = "sling.servlet.extensions", value = "html"),
        @Property(propertyPrivate = true, name = "sling.servlet.methods", value = {"POST", "GET"}),
        @Property(name = "label", value = "Girl Scouts VTK Salesforce Authentication Servlet"),
        @Property(name = "description", value = "Girl Scouts VTK Salesforce Authentication Servlet")
})
public class SalesforceAuthServlet extends SlingAllMethodsServlet implements ConfigListener {
    private static final long serialVersionUID = 8152897311719564370L;
    private static final Logger log = LoggerFactory.getLogger(SalesforceAuthServlet.class);
    private static final String ACTION = "action";
    private static final String SIGNIN = "signin";
    private static final String SIGNOUT = "signout";
    // Salesforce callback code
    private static final String CODE = "code";
    private String OAuthUrl;
    private String clientId;
    private String callbackUrl;
    private String targetUrl;
    private String certificateS;
    private AppSettings appSettings;
    private AccountSettings accSettings;

    @Reference
    private ConfigManager configManager;

    @Reference
    private GirlScoutsSalesForceService sfService;

    @Reference
    private CouncilMapper councilMapper;

    @Reference
    private TroopUtil troopUtil;

    @Reference
    private TroopHashGenerator troopHashGenerator;

    @Reference
    private UserUtil userUtil;

    @Activate
    public void init() {
        configManager.register(this);
        this.certificateS = configManager.getConfig("ssoCertificate");
        this.appSettings = new AppSettings();
        this.appSettings.setAssertionConsumerServiceUrl(callbackUrl);
        this.appSettings.setIssuer(configManager.getConfig("ssoIssuer"));
        this.accSettings = new AccountSettings();
        this.accSettings.setIdpSsoTargetUrl(configManager.getConfig("idpSsoTargetUrl"));
        this.accSettings.setCertificate(certificateS);
    }

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        String action = request.getParameter(ACTION);
        if (action != null) {
            if (action.equals(SIGNIN)) {
                signIn(request, response);
            } else if (action.equals(SIGNOUT)) {
                signOut(request, response);
            } else {
                log.error("Unsupported action: " + action);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public void updateConfig(Dictionary configs) {
        this.certificateS = configManager.getConfig("ssoCertificate");
        OAuthUrl = (String) configs.get("OAuthUrl");
        clientId = (String) configs.get("clientId");
        callbackUrl = (String) configs.get("callbackUrl");
        targetUrl = (String) configs.get("targetUrl");
        this.appSettings = new AppSettings();
        this.appSettings.setAssertionConsumerServiceUrl(callbackUrl);
        this.appSettings.setIssuer(configManager.getConfig("ssoIssuer"));
        this.accSettings = new AccountSettings();
        this.accSettings.setIdpSsoTargetUrl(configManager.getConfig("idpSsoTargetUrl"));
        this.accSettings.setCertificate(certificateS);
    }

    private void signIn(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        HttpSession session = request.getSession();
        String refererCouncil = request.getParameter("refererCouncil");
        if (refererCouncil == null) {
            refererCouncil = "";
        }
        ApiConfig config = null;
        try {
            config = (ApiConfig) session.getAttribute(ApiConfig.class.getName());
        } catch (Exception e) {
            log.error("Exception occurred getting ApiConfig from session: ", e);
        }
        AuthRequest authReq = new AuthRequest(appSettings, accSettings);
        try {
            String reqString = authReq.getSSOurl(refererCouncil);
            response.sendRedirect(reqString);
        } catch (Exception e) {
            log.error("Error occured: ",e);
        }
    }

    private void signOut(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        boolean isVtkLogin = false;
        if (request.getParameter("isVtkLogin") != null && request.getParameter("isVtkLogin").equals("true")) {
            isVtkLogin = true;
        }
        redirect(response, configManager.getConfig("communityUrl")
                + "/VTKLogout?redirectSource=" + java.net.URLEncoder.encode(configManager.getConfig("baseUrl") + "/content/girlscouts-vtk/controllers/vtk.logout.html" + (isVtkLogin ? "?isVtkLogin=true" : "")));
        return;
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServerException, IOException {
        ApiConfig config = null;
        HttpSession session = request.getSession();
        session.setAttribute("fatalError", null);
        try {
            String sAMLResponse = request.getParameter("SAMLResponse");
            Response samlResponse = new Response(accSettings, sAMLResponse);
            String token = samlResponse.getToken();
            String userId = samlResponse.getUserId();
            boolean useAsDemo = false;
            if (session.getAttribute("useAsDemo") != null) {
                useAsDemo = true;
            }
            if (request.getParameter("RelayState") == null || (request.getParameter("RelayState") != null && !request.getParameter("RelayState").contains("sfUserLanding"))) {
                try {
                    config = sfService.getApiConfig(token);
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
                    config.setAccessTokenValid(true);
                    config.setUseAsDemo(useAsDemo);
                    session.setAttribute(ApiConfig.class.getName(), config);
                    User user = null;
                    try {
                        user = sfService.getUser(config);
                    } catch (Exception e) {
                        log.error("Error occurred while getting User from salesforce:",e);
                    }
                    if (user == null) {
                        response.setStatus(500);
                        return;
                    }
                    config.setUser(user);
                    config.setTroops(user.getTroops());
                    user.setApiConfig(config);
                    if (config.getUser().getTroops() != null && config.getUser().getTroops().size() > 0) {
                        user.setCurrentYear("" + VtkUtil.getCurrentGSYear());
                    }
                    session.setAttribute(User.class.getName(), user);
                    if (config.getUser().getTroops() != null && !config.getUser().getTroops().isEmpty()) {
                        String troopDataPath = troopHashGenerator.hash(config.getUser().getTroops().get(0));
                        Cookie cookie = new Cookie("troopDataToken", troopDataPath);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    }
                } catch (Exception e4) {
                    log.error("Error occured:",e4);
                    e4.printStackTrace();
                    VtkError err = new VtkError();
                    err.setName("Error logging in");
                    err.setDescription("Error int SalesForceOAuthServet.doPost: found error while getting oAuth token from Salesforce. Exception : " + e4.toString());
                    err.setUserFormattedMsg("There appears to be an error in loggin. Please notify support with error code VTK-oAuth");
                    err.setErrorCode("VTK-oAuth");
                    err.addTarget("home");
                    session.setAttribute("fatalError", err);
                    response.sendRedirect("/content/girlscouts-vtk/en/vtk.home.html");
                    return;
                }
            }//end oAuthtoken
            if (request.getParameter("RelayState") != null && (request.getParameter("RelayState").indexOf("http://") != -1 || request.getParameter("RelayState").indexOf("https://") != -1)) {
                redirect(response, request.getParameter("RelayState"));
            } else if (request.getParameter("RelayState") != null) {
                setCouncilInClient(response, request.getParameter("RelayState"));
                redirect(response, targetUrl);
            } else {
                redirect(response, targetUrl);
            }
        } catch (Exception e) {
            log.error("Error occured:",e);
            VtkError err = new VtkError();
            err.setName("Error logging in");
            err.setDescription("Error int SalesForceOAuthServet.doPost: found error while SSO from Salesforce. Exception : " + e.toString());
            err.setUserFormattedMsg("There appears to be an error in loggin. Please notify support with error code VTK-SSO");
            err.setErrorCode("VTK-SSO");
            err.addTarget("home");
            session.setAttribute("fatalError", err);
            response.sendRedirect("/content/girlscouts-vtk/en/vtk.home.html");
            return;
        }
    }

    private void redirect(SlingHttpServletResponse response, String redirectUrl) {
        try {
            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            // TODO: What to do here? Send 500?
            log.error("Error while sending redirect: " + redirectUrl);
        }
    }

    public void setCouncilInClient(org.apache.sling.api.SlingHttpServletResponse response, String councilCode) {
        Cookie cookie = new Cookie("vtk_referer_council", councilCode);
        cookie.setMaxAge(-1);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
