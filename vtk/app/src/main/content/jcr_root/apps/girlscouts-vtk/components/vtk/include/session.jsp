<%@page import="org.apache.sling.runmode.RunMode,
                org.girlscouts.vtk.auth.models.ApiConfig,
                org.girlscouts.vtk.auth.permission.Permission,
                org.girlscouts.vtk.auth.permission.PermissionConstants,
                org.girlscouts.vtk.dao.TroopDAO,
                org.girlscouts.vtk.ejb.*,
                org.girlscouts.vtk.helpers.ConfigManager,
                org.girlscouts.vtk.helpers.CouncilMapper,
                org.girlscouts.vtk.models.*,
                org.girlscouts.vtk.utils.VtkUtil,
                java.text.DecimalFormat,
                java.text.NumberFormat,
                java.text.SimpleDateFormat" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%!
    // put all static in util classes
    final NumberFormat FORMAT_CURRENCY = NumberFormat.getCurrencyInstance();
    final DecimalFormat FORMAT_COST_CENTS = new DecimalFormat("#,##0.00");
    final boolean isCachableContacts = false;
    // Feature set toggles
    final boolean SHOW_BETA = false; // controls feature for all users -- don't set this to true unless you know what I'm talking about
    final String SHOW_VALID_SF_USER_FEATURE = "showValidSfUser";
    final String SESSION_FEATURE_MAP = "sessionFeatureMap"; // session attribute to hold map of enabled features
    final String[] ENABLED_FEATURES = new String[]{SHOW_VALID_SF_USER_FEATURE};
%><%
    String relayUrl = "";//sling.getService(org.girlscouts.vtk.helpers.ConfigManager.class).getConfig("idpSsoTargetUrl") +"&RelayState="+sling.getService(org.girlscouts.vtk.helpers.ConfigManager.class).getConfig("baseUrl");
    boolean isMultiUserFullBlock = true;
    // Why so heavy?  Do we need to load all services here or maybe on demand is better?
    final CalendarUtil calendarUtil = sling.getService(CalendarUtil.class);
    final LocationUtil locationUtil = sling.getService(LocationUtil.class);
    final MeetingUtil meetingUtil = sling.getService(MeetingUtil.class);
    final YearPlanUtil yearPlanUtil = sling.getService(YearPlanUtil.class);
    final TroopUtil troopUtil = sling.getService(TroopUtil.class);
    final UserUtil userUtil = sling.getService(UserUtil.class);
    final FinanceUtil financeUtil = sling.getService(FinanceUtil.class);
    final SessionFactory sessionFactory = sling.getService(SessionFactory.class);
    final ContactUtil contactUtil = sling.getService(ContactUtil.class);
    final ConnectionFactory connectionFactory = sling.getService(ConnectionFactory.class);
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
                if (!sessionFeatures.contains(enabledFeature)) {
                    sessionFeatures.add(enabledFeature);
                }
            } else if ("false".equals(thisFeatureValue) || "no".equals(thisFeatureValue) || "0".equals(thisFeatureValue)) {
                if (sessionFeatures.contains(enabledFeature)) {
                    sessionFeatures.remove(enabledFeature);
                }
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
    if ((userTroops == null || userTroops.size() <= 0 || (userTroops.get(0).getType() == 1))) {
        int vtkSignupYear = VtkUtil.getCurrentGSYear() + 1;
        %>
        <div id="panelWrapper" class="row meeting-detail content">
            <div class="columns large-20 large-centered">
                <%@include file="vtkError.jsp" %>

                <p>We're sorry you're having trouble logging into the VTK. We're here to help!</p>
                <p>
                    The Volunteer Toolkit is a digital planning tool currently available for active Troop Leaders and
                    Co-Leaders. To access the VTK, please ensure you have an active <%= vtkSignupYear %> membership. If you're a
                    parent, please make sure your daughter has an active <%= vtkSignupYear %> membership record as well and that
                    she's part of an active troop.
                </p>
                <p>
                    Need help? click Contact Us at the top of the page to connect with the customer service team and we'll get
                    it sorted out.
                </p>
                <p>
                    Thank you!
                </p>
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
        }
        if (selectedTroop == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                theCookie:
                for (int i = 0; i < cookies.length; i++) {
                    if (cookies[i].getName().equals("vtk_prefTroop")) {
                        for (int ii = 0; ii < userTroops.size(); ii++) {
                            String gradeLevel = userTroops.get(ii).getGradeLevel();
                            if (gradeLevel != null && gradeLevel.equals(cookies[i].getValue())) {
                                selectedTroop = userTroops.get(ii);
                                break theCookie;
                            }
                        }
                    }
                }
            }
        }
    }
    if(selectedTroop != null){
        if(selectedTroop.getYearPlan() == null || selectedTroop.isRefresh()) {
            Troop selectedTroopRepoData = null;
            try {
                if (!(apiConfig.getUser().isAdmin() && selectedTroop.getTroopId().equals("none"))) {
                    selectedTroopRepoData = troopUtil.getTroopByPath(user, selectedTroop.getPath());
                    selectedTroop.setYearPlan(selectedTroopRepoData.getYearPlan());
                    selectedTroop.setCurrentTroop(selectedTroopRepoData.getCurrentTroop());
                }
            } catch (org.girlscouts.vtk.utils.VtkException ec) {
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
                    %><span class="error">Sorry, you have no access to view year plan.</span><%
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
        String apps[] = new String[]{"prod"};
        String prodButDontTrack[] = new String[]{"gspreview"};
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