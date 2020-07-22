package org.girlscouts.vtk.sling.model;

import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.girlscouts.vtk.osgi.service.GirlScoutsSSOConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Session;

@Model(adaptables = SlingHttpServletRequest.class)
public class VTKSSOUtil {
    private static final Logger log = LoggerFactory.getLogger(VTKSSOUtil.class);
    @Self
    private SlingHttpServletRequest request;
    @Inject
    private GirlScoutsSSOConfigurationService ssoConfig;

    private String principalName;
    private String lastName;
    private String firstName;
    private String gsGlobalId;
    private String email;
    private String gsUserType;

    @PostConstruct
    public void init(){
        ResourceResolver resourceResolver = request.getResourceResolver();
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
        log.info(this.getClass().getName() + " activated.");
    }

    public String getErrorMessage() {
        return request.getParameter("error");
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
        return "<script>gigya.socialize.addEventHandlers({onLogin: function() { gigya.fidm.saml.continueSSO(\"" + getSPName() + "\");}});gigya.accounts.showScreenSet({screenSet: '"+getScreenSet()+"', containerID: \"container\", sessionExpiration: '"+getSessionExpiration()+"'});</script>";
    }
    public String getApiScript(){
        return "<script type=\"text/javascript\" src = \"https://cdns.gigya.com/js/gigya.js?apiKey="+getApiKey()+"\">{loginURL:'"+getLogInPath()+"',logoutURL: '"+getLogOutPath()+"'}</script>";
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
}