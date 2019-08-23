<%@page import="org.apache.sling.runmode.RunMode,
                org.girlscouts.vtk.auth.models.ApiConfig,
                org.girlscouts.vtk.auth.permission.PermissionConstants,
                org.girlscouts.vtk.osgi.component.dao.TroopDAO,
                org.girlscouts.vtk.osgi.component.util.*,
                org.girlscouts.vtk.models.*,
                org.girlscouts.vtk.exception.*,
                org.girlscouts.vtk.auth.permission.*,
                org.girlscouts.vtk.osgi.component.ConfigManager,
                org.girlscouts.vtk.osgi.component.CouncilMapper,
                org.slf4j.Logger,
                org.slf4j.LoggerFactory,
                java.text.DecimalFormat,
                java.util.HashSet,
                java.util.List,
                java.util.Set, org.girlscouts.vtk.exception.VtkException" %>
<%!
    // put all static in util classes
    final DecimalFormat FORMAT_COST_CENTS = new DecimalFormat("#,##0.00");
    final boolean isCachableContacts = false;
    // Feature set toggles
    final boolean SHOW_BETA = false; // controls feature for all users -- don't set this to true unless you know what I'm talking about
    final String SHOW_VALID_SF_USER_FEATURE = "showValidSfUser";
    final String SESSION_FEATURE_MAP = "sessionFeatureMap"; // session attribute to hold map of enabled features
    final String[] ENABLED_FEATURES = new String[]{SHOW_VALID_SF_USER_FEATURE};
%><%
    Logger sessionlog = LoggerFactory.getLogger(this.getClass().getName());
    sessionlog.debug("session.jsp");
    String relayUrl = "";
    boolean isMultiUserFullBlock = true;
    // Why so heavy?  Do we need to load all services here or maybe on demand is better?
    final CalendarUtil calendarUtil = sling.getService(CalendarUtil.class);
    final LocationUtil locationUtil = sling.getService(LocationUtil.class);
    final MeetingUtil meetingUtil = sling.getService(MeetingUtil.class);
    final YearPlanUtil yearPlanUtil = sling.getService(YearPlanUtil.class);
    final TroopUtil troopUtil = sling.getService(TroopUtil.class);
    final UserUtil userUtil = sling.getService(UserUtil.class);
    final FinanceUtil financeUtil = sling.getService(FinanceUtil.class);
    final ContactUtil contactUtil = sling.getService(ContactUtil.class);
    final VtkUtil vtkUtil = sling.getService(VtkUtil.class);
    final ConfigManager configManager = sling.getService(ConfigManager.class);
    // Why is this here if it say's not to use?
    //dont use
    final TroopDAO troopDAO = sling.getService(TroopDAO.class);
    CouncilMapper councilMapper = sling.getService(CouncilMapper.class);
    User user = null;
    HttpSession session = request.getSession();
    int timeout = session.getMaxInactiveInterval();
    response.setHeader("Refresh", timeout + "; URL = /content/girlscouts-vtk/en/vtk.logout.html");
    if (session.getAttribute(SESSION_FEATURE_MAP) == null) {
        session.setAttribute(SESSION_FEATURE_MAP, new HashSet<String>());
    }
    Set sessionFeatures = (Set) session.getAttribute(SESSION_FEATURE_MAP);
    for (String enabledFeature : ENABLED_FEATURES) {
        if (request.getParameter(enabledFeature) != null) {
            String thisFeatureValue = ((String) request.getParameter(enabledFeature)).trim().toLowerCase();
            if ("true".equals(thisFeatureValue) || "yes".equals(thisFeatureValue) || "1".equals(thisFeatureValue)) {
                sessionFeatures.add(enabledFeature);
            } else if ("false".equals(thisFeatureValue) || "no".equals(thisFeatureValue) || "0".equals(thisFeatureValue)) {
                sessionFeatures.remove(enabledFeature);
            }
        }
    }
    ApiConfig apiConfig = null;
    try {
        if (session.getAttribute(ApiConfig.class.getName()) != null) {
            apiConfig = ((ApiConfig) session.getAttribute(ApiConfig.class.getName()));
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            if (session.getAttribute("fatalError") != null) {
%>
<div id="panelWrapper" class="row meeting-detail content">
    <div class="columns large-20 large-centered">
        <%@include file="vtkError.jsp" %>
    </div>
</div>
<%
} else {
%>
<div id="panelWrapper" class="row meeting-detail content">
    <div class="columns large-20 large-centered">
        <p>
            Your session has timed out. Please refresh this page and login.
        </p>
    </div>
</div>
<%
            }
            return;
        }
    } catch (ClassCastException cce) {
        session.invalidate();
        log.error("ApiConfig class cast exception -- probably due to restart.  Logging out user.");
        try {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception setStatusException) {
            setStatusException.printStackTrace();
        }
        out.println("Your session has timed out.  Please login.");
        return;
    }
    List<Troop> userTroops = apiConfig.getUser().getTroops();
    if (apiConfig.getUser().isAdmin() && (userTroops == null || userTroops.size() <= 0)) {
        Troop dummyVTKAdminTroop = new Troop();
        dummyVTKAdminTroop.setPermissionTokens(org.girlscouts.vtk.auth.permission.Permission.getPermissionTokens(org.girlscouts.vtk.auth.permission.Permission.GROUP_ADMIN_PERMISSIONS));
        dummyVTKAdminTroop.setTroopId("none");
        dummyVTKAdminTroop.setSfTroopName("vtk_virtual_troop");
        dummyVTKAdminTroop.setSfCouncil(user.getAdminCouncilId());
        dummyVTKAdminTroop.setSfUserId("none");
        dummyVTKAdminTroop.setSfTroopId("none");
        dummyVTKAdminTroop.setCouncilCode(user.getAdminCouncilId());
        dummyVTKAdminTroop.setTroopName("vtk_virtual_troop");
        String councilPath = "/vtk" + VtkUtil.getCurrentGSYear() + "/" + dummyVTKAdminTroop.getSfCouncil();
        dummyVTKAdminTroop.setCouncilPath(councilPath);
        String troopPath = councilPath + "/troops/" + dummyVTKAdminTroop.getSfTroopId();
        dummyVTKAdminTroop.setPath(troopPath);
        // user.setPermissions(user_troop.getPermissionTokens());
        userTroops.add(dummyVTKAdminTroop);
    }
    if ((userTroops == null || userTroops.size() <= 0 || (userTroops.get(0).getType() == 1))) {
        int vtkSignupYear = VtkUtil.getCurrentGSYear() + 1;
%>
<div id="panelWrapper" class="row meeting-detail content">
    <div class="columns large-20 large-centered">
        <%@include file="vtkError.jsp" %>
        <p>We're sorry you're having trouble accessing the Volunteer Toolkit. We're here to help!</p>
        <p>The Volunteer Toolkit is a digital planning tool currently available to troop leaders, co-leaders, council-selected administrative volunteers, and primary caregivers of Girl Scouts with active <%= vtkSignupYear %> memberships.</p>
        <p>If you feel you've reached this screen in error, click Contact Us at the top of the page and we'll help you get it sorted out.</p>
        <p>Thank you!</p>
    </div>
</div>
<%
        return;
    }
    user = ((User) session.getAttribute(User.class.getName()));
    user.setSid(session.getId());
    Troop selectedTroop = (Troop) session.getAttribute("VTK_troop");
    if (selectedTroop == null) {
        if (userTroops != null && userTroops.size() > 0) {
            selectedTroop = userTroops.get(0);
            if (selectedTroop == null) {
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    theCookie:
                    for (Cookie cookie : cookies) {
                        if (cookie.getName().equals("vtk_prefTroop")) {
                            for (Troop userTroop : userTroops) {
                                String gradeLevel = userTroop.getGradeLevel();
                                if (gradeLevel != null && gradeLevel.equals(cookie.getValue())) {
                                    selectedTroop = userTroop;
                                    break theCookie;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (selectedTroop != null) {
        if (selectedTroop.getYearPlan() == null || selectedTroop.isRefresh()) {
            Troop selectedTroopRepoData = null;
            try {
                if (!(apiConfig.getUser().isAdmin() && selectedTroop.getTroopId().equals("none"))) {
                    selectedTroopRepoData = troopUtil.getTroopByPath(user, selectedTroop.getPath());
                }
            } catch (VtkException ec) {
%>
<div id="panelWrapper" class="row meeting-detail content">
    <p class="errorNoTroop" style="padding:10px;color: #009447; font-size: 14px;">
        <%=ec.getMessage() %>
        <br/>Please notify Girlscouts VTK support
    </p>
</div>
<%
    return;
} catch (IllegalAccessException ex) {
    ex.printStackTrace();
%>
<span class="error">Sorry, you have no access to view year plan.</span>
<%
        return;
    }
    if (selectedTroopRepoData == null) {
        try {
            troopUtil.createCouncil(user, selectedTroop);
        } catch (Exception e) {
%>
<div id="panelWrapper" class="row meeting-detail content">
    <p class="errorNoTroop" style="padding:10px;color: #009447; font-size: 14px;">
        <%=e.getMessage() %>
        <br/>Please notify Girlscouts VTK support
    </p>
</div>
<%
                e.printStackTrace();
                return;
            }
        } else {
            selectedTroop.setYearPlan(selectedTroopRepoData.getYearPlan());
            selectedTroop.setCurrentTroop(selectedTroopRepoData.getCurrentTroop());
        }
    }
    if (request.getParameter("showGamma") != null && request.getParameter("showGamma").equals("true")) {
        selectedTroop.getPermissionTokens().add(PermissionConstants.PERMISSION_VIEW_FINANCE_ID);
        selectedTroop.getPermissionTokens().add(PermissionConstants.PERMISSION_CAN_VIEW_MEMBER_DETAIL_TROOP_ID);
        selectedTroop.getPermissionTokens().add(PermissionConstants.PERMISSION_VIEW_REPORT_ID);
        selectedTroop.getPermissionTokens().add(PermissionConstants.PERMISSION_EDIT_FINANCE_ID);
        selectedTroop.getPermissionTokens().add(PermissionConstants.PERMISSION_EDIT_FINANCE_FORM_ID);
        session.setAttribute("showGamma", "true");
    } else if (request.getParameter("showGamma") != null && request.getParameter("showGamma").equals("false")) {
        selectedTroop.getPermissionTokens().remove(PermissionConstants.PERMISSION_VIEW_FINANCE_ID);
        selectedTroop.getPermissionTokens().remove(PermissionConstants.PERMISSION_CAN_VIEW_MEMBER_DETAIL_TROOP_ID);
        selectedTroop.getPermissionTokens().remove(PermissionConstants.PERMISSION_VIEW_REPORT_ID);
        selectedTroop.getPermissionTokens().remove(PermissionConstants.PERMISSION_EDIT_FINANCE_ID);
        selectedTroop.getPermissionTokens().remove(PermissionConstants.PERMISSION_EDIT_FINANCE_FORM_ID);
        session.setAttribute("showGamma", null);
    }
    session.setAttribute("VTK_troop", selectedTroop);
    if (session.getAttribute("USER_TROOP_LIST") == null) {
        session.setAttribute("USER_TROOP_LIST", userTroops);
    }
    //check valid cache url /myvtk/
    if (!VtkUtil.isValidUrl(user, selectedTroop, request.getRequestURI(), councilMapper.getCouncilName(selectedTroop.getSfCouncil()))) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return;
    }
    RunMode runModeService = sling.getService(RunMode.class);
    String[] apps = new String[]{"prod"};
    String[] prodButDontTrack = new String[]{"gspreview"};
    if (runModeService.isActive(apps) && !runModeService.isActive(prodButDontTrack)) {
        String footerScript = "<script>window['ga-disable-UA-2646810-36'] = false; vtkInitTracker('" + selectedTroop.getSfTroopName() + "', '" + selectedTroop.getSfTroopId() + "', '" + user.getApiConfig().getUser().getSfUserId() + "', '" + selectedTroop.getSfCouncil() + "', '" + selectedTroop.getSfTroopAge() + "', '" + (selectedTroop.getYearPlan() == null ? "" : selectedTroop.getYearPlan().getName()) + "'); vtkTrackerPushAction('View'); showSelectedDemoTroop('" + selectedTroop.getSfTroopAge() + "');</script>";
        request.setAttribute("footerScript", footerScript);
    } else {
        String footerScript = "<script>window['ga-disable-UA-2646810-36'] = true; showSelectedDemoTroop('" + selectedTroop.getSfTroopAge() + "')</script>";
        request.setAttribute("footerScript", footerScript);
    }
    request.setAttribute("vtk-request-user", user);
    request.setAttribute("vtk-request-troop", selectedTroop);
} else {
%>
<div id="panelWrapper" class="row meeting-detail content">
    <p class="errorNoTroop" style="padding:10px;color: #009447; font-size: 14px;">
        <br/>Please notify Girlscouts VTK support
    </p>
</div>
<%
    }
%>                  