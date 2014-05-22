package org.girlscouts.vtk.impl;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.girlscouts.vtk.auth.dao.SalesforceDAO;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.auth.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SlingServlet(
    resourceTypes = "sling/servlet/default",
    selectors = "sfauth",
    extensions = "html",
    methods = "GET"
)
public class SalesforceAuthServlet extends SlingSafeMethodsServlet {
    private static final Logger log = LoggerFactory.getLogger(SalesforceAuthServlet.class);
    private static final String ACTION = "action";
    private static final String SIGNIN = "signin";
    private static final String SIGNOUT = "signout";
    // Salesforce callback code
    private static final String CODE = "code";
    
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        String action = request.getParameter(ACTION);
        if (action.equals(SIGNIN)) {
            signIn(request, response);
        } else if (action.equals(SIGNOUT)){
            signOut(request, response);
        } else {
            salesforceCallback(request, response);
        }
    }
    
    private void signIn(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        // TODO: from OSGI
        String targetUrl = "http://localhost:4502/content/girlscouts/en.html";

        HttpSession session = request.getSession();
        ApiConfig config = (ApiConfig)session.getAttribute(ApiConfig.class.getName());
        String redirectUrl;
        if (config == null || config.getId() == null) {
            // TODO: from OSGI
            String oAuthUrl = "https://test.salesforce.com/services/oauth2/authorize?response_type=code&client_id=3MVG9A2kN3Bn17hsP.Ui7f1jgy.inaXzsZ4VKF6DcS.LKlCZSdhHBeH.992uouy19lQAyvfZC98jgGtM73lIu";
            // TODO: Do I have to encode it? What if this redirectUrl itself has question marks?
            String callbackUrl = "http://localhost:4502/content/testLogin2/login5.html";
            redirectUrl = oAuthUrl + "&redirect_uri=" + callbackUrl + "&state=" + targetUrl;
        } else {
            redirectUrl = targetUrl;
        }
        
        redirect(response, redirectUrl);
    }

    private void signOut(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        
    }
    
    private void salesforceCallback(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        HttpSession session = request.getSession();
        ApiConfig config = (ApiConfig)session.getAttribute(ApiConfig.class.getName());
        if (config != null) {
            log.error("In Salesforce callback but the ApiConfig already exists. Quitting.");
            return;
        }
        
        String code = request.getParameter(CODE);
        if (code == null) {
            log.error("In Salesforce callback but \"code\"parameter not returned.");
            return;
        }
        
        SalesforceDAO dao = new SalesforceDAO();
        User user = dao.getUser(config);
        session.setAttribute(User.class.getName(), user);
        
        // TODO: from OSGI
        String targetUrl = "http://localhost:4502/content/girlscouts/en.html";
        
        redirect(response, targetUrl);
    }
    
    private void redirect(SlingHttpServletResponse response, String redirectUrl) {
        try {
            response.sendRedirect(redirectUrl);
        } catch (IOException e) {
            // TODO: What to do here? Send 500?
            log.error("Error while sending redirect: " + redirectUrl);
        }
    }
}