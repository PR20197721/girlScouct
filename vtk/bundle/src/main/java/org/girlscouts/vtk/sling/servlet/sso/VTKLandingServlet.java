package org.girlscouts.vtk.sling.servlet.sso;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.exception.VtkError;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.osgi.component.CouncilMapper;
import org.girlscouts.vtk.osgi.component.util.VtkUtil;
import org.girlscouts.vtk.osgi.service.MulesoftService;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component(service = Servlet.class, property = {
        Constants.SERVICE_DESCRIPTION + "=Girl Scouts VTK Landing Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.extensions=html",
        "sling.servlet.resourceTypes=girlscouts-vtk/components/sso/landing"})
public class VTKLandingServlet extends SlingAllMethodsServlet implements OptingServlet {
    private static final Logger log = LoggerFactory.getLogger(VTKLandingServlet.class);
    /**
     *
     */
    private static final long serialVersionUID = -1341523521748738122L;

    @Reference
    private CouncilMapper councilMapper;

    @Reference
    private MulesoftService mulesoftService;

    @Activate
    private void activate() {
        log.debug("Girl Scouts VTK Landing Servlet activated");
    }

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        processRequest(request, response);
    }

    private void processRequest(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            ResourceResolver resourceResolver = request.getResourceResolver();
            final Session jcrSession = resourceResolver.adaptTo(Session.class);
            final UserManager userManager = resourceResolver.adaptTo(UserManager.class);
            final User aemUser = (User) userManager.getAuthorizable(jcrSession.getUserID());
            String principalName = aemUser.getPrincipal().getName();
            log.debug("Processing user {}",principalName);

            if(isActiveVTKSession(request)){
                String vtkRedirect = "/content/girlscouts-vtk/en/vtk.html";
                try {
                    HttpSession httpSession = request.getSession();
                    ApiConfig apiConfig = ((ApiConfig) httpSession.getAttribute(ApiConfig.class.getName()));
                    if (apiConfig != null && !apiConfig.isFail()) {
                        org.girlscouts.vtk.models.User vtkUser = apiConfig.getUser();
                        List<Troop> userTroops = vtkUser.getTroops();
                        if (userTroops.size() > 0) {
                            try {

                                Troop selectedTroop = userTroops.get(0);
                                if (VtkUtil.getTroop(httpSession) != null) {
                                    selectedTroop = VtkUtil.getTroop(httpSession);
                                } else {
                                    log.debug("setting current user as: "+apiConfig.getUser());
                                    httpSession.setAttribute(org.girlscouts.vtk.models.User.class.getName(), apiConfig.getUser());
                                    log.debug("setting current troop as: "+apiConfig.getUser().getTroops().get(0));
                                    httpSession.setAttribute("VTK_troop", selectedTroop);
                                }
                                String councilId = selectedTroop.getCouncilCode();
                                String userRole = selectedTroop.getRole();
                                userRole = userRole == null ? "" : userRole;
                                if (!userRole.equals("DP") && !userRole.equals("FA")) {
                                    if (vtkUser.isAdmin()) {
                                        vtkRedirect = "/myvtk/" + councilMapper.getCouncilName(councilId) + "/vtk.resource.html";
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }else{
                            returnError( response,  httpSession,  vtkRedirect, "Unable to get troop details");
                        }
                    }
                } catch (Exception e) {
                    log.error("Error occured:", e);
                }
                response.sendRedirect(vtkRedirect);
            }else{
                String vtkRedirect = "/content/girlscouts-vtk/en/vtk.html";
                ApiConfig apiConfig = mulesoftService.getApiConfig(aemUser);
                log.debug("apiConfig="+apiConfig);
                HttpSession httpSession = request.getSession();
                if(apiConfig != null && apiConfig.getUser() != null && apiConfig.getUser().getTroops() != null && apiConfig.getUser().getTroops().size() != 0) {
                    String lastName = aemUser.getProperty("./profile/familyName") != null ? aemUser.getProperty("./profile/familyName")[0].getString() : null;
                    String firstName = aemUser.getProperty("./profile/givenName") != null ? aemUser.getProperty("./profile/givenName")[0].getString() : null;
                    String gsGlobalId = aemUser.getProperty("./profile/GSGlobalID") != null ? aemUser.getProperty("./profile/GSGlobalID")[0].getString() : null;
                    String email = aemUser.getProperty("./profile/email") != null ? aemUser.getProperty("./profile/email")[0].getString() : null;
                    String gsUserType = aemUser.getProperty("./profile/gsUserType") != null ? aemUser.getProperty("./profile/gsUserType")[0].getString() : null;
                    log.debug("AEM User In Session: firstName:"+firstName+", lastName:"+lastName+", gsGlobalId="+gsGlobalId+", email="+email+", gsUserType="+gsUserType);
                    setNameInCookie(firstName, lastName, response);
                    httpSession.setAttribute(ApiConfig.class.getName(), apiConfig);
                    log.debug("setting current user as: "+apiConfig.getUser());
                    httpSession.setAttribute(org.girlscouts.vtk.models.User.class.getName(), apiConfig.getUser());
                    log.debug("setting current troop as: "+apiConfig.getUser().getTroops().get(0));
                    httpSession.setAttribute("VTK_troop", apiConfig.getUser().getTroops().get(0));
                    Troop selectedTroop = apiConfig.getUser().getTroops().get(0);
                    String councilId = selectedTroop.getCouncilCode();
                    setLogoutRedirectInCookie(councilId, response, resourceResolver);
                    String userRole = selectedTroop.getRole();
                    userRole = userRole == null ? "" : userRole;
                    if (!userRole.equals("DP")) {
                        if (apiConfig.getUser().isAdmin()) {
                            vtkRedirect = "/myvtk/" + councilMapper.getCouncilName(councilId) + "/vtk.resource.html";
                        }
                    }
                    response.sendRedirect(vtkRedirect);
                    log.debug("Creating VTK session for: principalName=" + principalName + ", firstName=" + firstName + ", lastName=" + lastName + ", gsGlobalId=" + gsGlobalId + ", email=" + email + ", gsUserType=" + gsUserType);
                }else{
                    if(apiConfig == null) {
                        returnError( response,  httpSession,  vtkRedirect, "Unable to initialize vtk session");
                    }else{
                        if(apiConfig.getUser() == null){
                            returnError( response,  httpSession,  vtkRedirect, "Unable to get user details");
                        }else {
                            httpSession.setAttribute(org.girlscouts.vtk.models.User.class.getName(), apiConfig.getUser());
                            if (apiConfig.getUser().getTroops() == null || apiConfig.getUser().getTroops().size() == 0) {
                                returnError( response,  httpSession,  vtkRedirect, "Unable to get troop details");
                            }
                        }
                    }

                }
            }

        } catch (Exception e) {
            log.error("Error occured: ", e);
        }
    }

    private void setNameInCookie(String firstName, String lastName, SlingHttpServletResponse response) {
        try {
            String name = "";
            if(!StringUtils.isBlank(firstName)){
                name+=firstName+" ";
            }
            if(!StringUtils.isBlank(lastName)){
                name+=lastName+" ";
            }
            Cookie nameCookie = new Cookie("girl-scout-name", URLEncoder.encode(name, StandardCharsets.UTF_8.toString()));
            nameCookie.setPath("/");
            response.addCookie(nameCookie);
        } catch (UnsupportedEncodingException e) {
            log.error("Error occurred:", e);
        }

    }

    private void setLogoutRedirectInCookie(String councilId, SlingHttpServletResponse response, ResourceResolver rr) {
        try {
            String councilPath = councilMapper.getCouncilName(councilId);
            String logoutPath = "";
            if(!councilPath.contains("girlscoutsneny")){
                logoutPath = rr.map("/content/"+councilPath+"/en")+".html";
            }else{
                logoutPath = "https://girlscoutsneny.org/";
            }
            Cookie nameCookie = new Cookie("girl-scout-logout", URLEncoder.encode(logoutPath, StandardCharsets.UTF_8.toString()));
            nameCookie.setPath("/");
            response.addCookie(nameCookie);
        } catch (UnsupportedEncodingException e) {
            log.error("Error occurred:", e);
        }

    }

    private void returnError(SlingHttpServletResponse response, HttpSession httpSession, String vtkRedirect, String  description) throws IOException {
        VtkError error = new VtkError();
        error.setDescription("Unable to get troop details");
        httpSession.setAttribute("fatalError", error);
        response.sendRedirect("/content/girlscouts-vtk/en/vtk.html");
    }
    private boolean isActiveVTKSession(SlingHttpServletRequest request) {
        Boolean isActiveVtkSession = false;
        if(request.getSession().getAttribute(ApiConfig.class.getName()) != null && VtkUtil.getTroop(request.getSession()) != null){
            isActiveVtkSession = true;
        }
        return isActiveVtkSession;
    }

    /**
     * OptingServlet Acceptance Method
     **/
    @Override
    public final boolean accepts(SlingHttpServletRequest request) {
        /*
         * Add logic which inspects the request which determines if this servlet
         * should handle the request. This will only be executed if the
         * Service Configuration's paths/resourcesTypes/selectors accept the request.
         */
        return true;
    }

}