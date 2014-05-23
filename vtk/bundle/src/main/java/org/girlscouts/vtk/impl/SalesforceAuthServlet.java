package org.girlscouts.vtk.impl;

import java.util.Dictionary;

import javax.servlet.http.HttpSession;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.girlscouts.vtk.auth.dao.SalesforceDAO;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.auth.models.User;
import org.girlscouts.vtk.impl.filters.ConfigListener;
import org.girlscouts.vtk.impl.helpers.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    label="Girl Scouts VTK Salesforce Authentication Servlet",
    description="Handles OAuth Authentication with Salesforce",
    metatype=true, 
    immediate=true
)
@Service
@Properties ({
    @Property(propertyPrivate=true, name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
    @Property(propertyPrivate=true, name = "sling.servlet.selectors", value = "sfauth"),
    @Property(propertyPrivate=true, name = "sling.servlet.extensions", value = "html"),
    @Property(propertyPrivate=true, name = "sling.servlet.methods", value = "GET")
})
public class SalesforceAuthServlet extends SlingSafeMethodsServlet implements ConfigListener {
    private static final long serialVersionUID = 8152897311719564370L;

    private static final Logger log = LoggerFactory.getLogger(SalesforceAuthServlet.class);
    private static final String ACTION = "action";
    private static final String SIGNIN = "signin";
    private static final String SIGNOUT = "signout";
    // Salesforce callback code
    private static final String CODE = "code";
    
    private String OAuthUrl;
    private String callbackUrl;
    private String targetUrl;
    
    @Reference
    private ConfigManager configManager;
    
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        String action = request.getParameter(ACTION);
        if (action == null) {
            salesforceCallback(request, response);
        } else if (action.equals(SIGNIN)) {
            signIn(request, response);
        } else if (action.equals(SIGNOUT)){
            signOut(request, response);
        } else {
            log.error("Unsupported action: " + action);
        }
    }
    
    @SuppressWarnings("rawtypes")
    public void updateConfig(Dictionary configs) {
        OAuthUrl = (String)configs.get("OAuthUrl");
        callbackUrl = (String)configs.get("callbackUrl");
        targetUrl = (String)configs.get("targetUrl");
    }
    
    @Activate
    public void init() {
        configManager.register(this);
    }
    
    private void signIn(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        HttpSession session = request.getSession();
        ApiConfig config = (ApiConfig)session.getAttribute(ApiConfig.class.getName());
        String redirectUrl;
        if (config == null || config.getId() == null) {
            redirectUrl = OAuthUrl + "&redirect_uri=" + callbackUrl + "&state=" + targetUrl;
        } else {
            redirectUrl = targetUrl;
        }
        
        redirect(response, redirectUrl);
    }

    private void signOut(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        
    }
    
    private void salesforceCallback(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        HttpSession session = request.getSession();
        if ((ApiConfig)session.getAttribute(ApiConfig.class.getName()) != null) {
            log.error("In Salesforce callback but the ApiConfig already exists. Quit.");
            return;
        }
        
        String code = request.getParameter(CODE);
        if (code == null) {
            log.error("In Salesforce callback but \"code\" parameter not returned. Quit.");
            return;
        }
        
        SalesforceDAO dao = new SalesforceDAO();
        ApiConfig config = dao.doAuth(code);
        session.setAttribute(ApiConfig.class.getName(), config);

        User user = dao.getUser(config);
        session.setAttribute(User.class.getName(), user);
        
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
}