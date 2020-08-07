package org.girlscouts.vtk.sling.model;

import com.google.gson.Gson;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.girlscouts.vtk.osgi.service.SSOConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.jcr.Session;

@Model(adaptables = SlingHttpServletRequest.class)
public class VTKSSOUtil {
    private static final Logger log = LoggerFactory.getLogger(VTKSSOUtil.class);
    @Self
    private SlingHttpServletRequest request;
    @OSGiService
    private SSOConfigurationService ssoConfig;

    private String principalName;
    private String lastName;
    private String firstName;
    private String gsGlobalId;
    private String email;
    private String gsUserType;
    private String error;
    private ResourceResolver resourceResolver;

    @PostConstruct
    public void init(){
        this.resourceResolver = request.getResourceResolver();
        this.error = request.getParameter("error");
        final Session session = resourceResolver.adaptTo(Session.class);
        final UserManager userManager = resourceResolver.adaptTo(UserManager.class);
        try {
            final User user = (User) userManager.getAuthorizable(session.getUserID());
            this.principalName = user.getPrincipal().getName();
            this.lastName = user.getProperty("./profile/familyName") != null ? user.getProperty("./profile/familyName")[0].getString() : null;
            this.firstName = user.getProperty("./profile/givenName") != null ? user.getProperty("./profile/givenName")[0].getString() : null;
            this.gsGlobalId = user.getProperty("./profile/GSGlobalID") != null ? user.getProperty("./profile/GSGlobalID")[0].getString() : null;
            this.email = user.getProperty("./profile/email") != null ? user.getProperty("./profile/email")[0].getString() : null;
            this.gsUserType = user.getProperty("./profile/gsUserType") != null ? user.getProperty("./profile/gsUserType")[0].getString() : null;
        }catch(Exception e){
            log.error("Error occurred:",e);
        }
    }

    public Integer getErrorCode() {
        Integer errorCode = null;
        if(error != null){
            try {
                Error errorObj = new Gson().fromJson(error, Error.class);
                errorCode = errorObj.getErrorCode();
            }catch(Exception e){
                log.error("Error occurred:", e);
            }
        }
        return errorCode;
    }

    public String getErrorMessage() {
        String errorMessage = "";
        if(error != null){
            try {
                Error errorObj = new Gson().fromJson(error, Error.class);
                errorMessage = errorObj.getErrorMessage();
            }catch(Exception e){
                log.error("Error occurred:", e);
            }
        }
        return errorMessage;
    }

    public String getCallId() {
        String callId = "";
        if(error != null){
            try {
                Error errorObj = new Gson().fromJson(error, Error.class);
                callId = errorObj.getCallId();
            }catch(Exception e){
                log.error("Error occurred:", e);
            }
        }
        return callId;
    }

    public String errorDetails() {
        String errorDetails = "";
        if(error != null){
            try {
                Error errorObj = new Gson().fromJson(error, Error.class);
                errorDetails = errorObj.getErrorDetails();
            }catch(Exception e){
                log.error("Error occurred:", e);
            }
        }
        return errorDetails;
    }

    public String errorDescription() {
        String errorDescription = "";
        if(error != null){
            try {
                Error errorObj = new Gson().fromJson(error, Error.class);
                errorDescription = errorObj.getErrorDescription();
            }catch(Exception e){
                log.error("Error occurred:", e);
            }
        }
        return errorDescription;
    }

    public String getApiKey() {
        return ssoConfig.getApiKey();
    }

    public String getSPName() {
        return ssoConfig.getSPName();
    }

    public String getLogInPath() {
        return ssoConfig.getLogInPath();
    }

    public String getLogOutPath() {
        return ssoConfig.getLogOutPath();
    }

    public String getScreenSet() {
        return ssoConfig.getScreenSet();
    }

    public Integer getSessionExpiration() {
        return ssoConfig.getSessionExpiration();
    }

    public String getLogInScript() {
        return "<script>gigya.socialize.addEventHandlers({" +
                "onLogin: function() { gigya.fidm.saml.continueSSO(\"" + getSPName() + "\");}," +
                "callback: gigyaCallback" +
                "});</script>";
    }
    public String getProxyScript(){
        return "<script src=\"https://cdns.gigya.com/js/gigya.saml.js?apiKey="+getApiKey()+"\">{loginURL:\""+getLogInPath()+"\",logoutURL:\""+getLogOutPath()+"\"}</script>";
    }
    public String getApiScript(){
        return "<script type=\"text/javascript\" src=\"https://cdns.gigya.com/js/gigya.js?apiKey="+getApiKey()+"\">{loginURL:\""+getLogInPath()+"\",logoutURL:\""+getLogOutPath()+"\"}</script>";
    }

    public String getFirstName(){
        return this.firstName;
    }
    public String getPrincipalName(){
        return this.principalName;
    }

    public String getLastName(){
        return this.lastName;
    }

    public String getGsGlobalId(){
        return this.gsGlobalId;
    }

    public String getEmail(){
        return this.email;
    }

    public String getGsUserType(){
        return this.gsUserType;
    }

    private class Error {

        private Integer errorCode;
        private String errorMessage;
        private String callId;
        private String errorDetails;
        private String errorDescription;

        public Integer getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(Integer errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getCallId() {
            return callId;
        }

        public void setCallId(String callId) {
            this.callId = callId;
        }

        public String getErrorDetails() {
            return errorDetails;
        }

        public void setErrorDetails(String errorDetails) {
            this.errorDetails = errorDetails;
        }

        public String getErrorDescription() {
            return errorDescription;
        }

        public void setErrorDescription(String errorDescription) {
            this.errorDescription = errorDescription;
        }
    }
}