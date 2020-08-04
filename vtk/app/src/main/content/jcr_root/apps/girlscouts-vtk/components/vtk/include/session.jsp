<%@page import="org.apache.sling.runmode.RunMode,
                org.girlscouts.vtk.auth.models.ApiConfig,
                org.girlscouts.vtk.auth.permission.PermissionConstants,
                org.girlscouts.vtk.exception.VtkException,
                org.girlscouts.vtk.models.*,
                org.girlscouts.vtk.osgi.component.ConfigManager,
                org.girlscouts.vtk.osgi.component.CouncilMapper,
                org.girlscouts.vtk.osgi.component.dao.TroopDAO,
                org.girlscouts.vtk.osgi.component.util.*,
                org.slf4j.Logger,
                org.slf4j.LoggerFactory,
                java.text.DecimalFormat,
                java.util.HashSet,
                java.util.List,
                java.util.Set" %>
<%!
    // put all static in util classes
    final DecimalFormat FORMAT_COST_CENTS = new DecimalFormat("#,##0.00");
%><%
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
    final TroopDAO troopDAO = sling.getService(TroopDAO.class);
    CouncilMapper councilMapper = sling.getService(CouncilMapper.class);
    HttpSession session = request.getSession();
    Troop selectedTroop = (Troop) session.getAttribute("VTK_troop");
    User user = user = ((User) session.getAttribute(User.class.getName()));
    int vtkSignupYear = VtkUtil.getCurrentGSYear() + 1;
    Logger sessionlog = LoggerFactory.getLogger(this.getClass().getName());
    sessionlog.debug("session.jsp");
    if (session.getAttribute("fatalError") != null) {
        sessionlog.error("fatal error is set in session " + session.getAttribute("fatalError"));
%>
<%@include file="vtkError.jsp" %>
<%
} else {
    boolean isMultiUserFullBlock = true;
    // Why so heavy?  Do we need to load all services here or maybe on demand is better?

    int timeout = session.getMaxInactiveInterval();
    response.setHeader("Refresh", timeout + "; URL = /content/girlscouts-vtk/en/vtk.logout.html");
    ApiConfig apiConfig = null;
    try {
        if (session.getAttribute(ApiConfig.class.getName()) != null) {
            apiConfig = ((ApiConfig) session.getAttribute(ApiConfig.class.getName()));
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            VtkError error = new VtkError();
            error.setDescription("Unable to get session information");
            session.setAttribute("fatalError", error);
            %>
            <%@include file="vtkError.jsp" %>
            <%
        }
    } catch (ClassCastException cce) {
        session.invalidate();
        log.error("ApiConfig class cast exception -- probably due to restart.  Logging out user.");
        try {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception setStatusException) {
            sessionlog.error("Error occured:",setStatusException);
        }
        out.println("Your session has timed out.  Please login.");
        return;
    }
    List<Troop> userTroops = apiConfig.getUser().getTroops();
    if ((userTroops == null || userTroops.size() <= 0 || (userTroops.get(0).getType() == 1))) {

        VtkError error = new VtkError();
        error.setDescription("Unable to get user troop information");
        session.setAttribute("fatalError", error);
%>
<%@include file="vtkError.jsp" %>
<%
        return;
    }

    user.setSid(session.getId());
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
                VtkError error = new VtkError();
                error.setDescription(ec.getMessage());
                session.setAttribute("fatalError", error);
%>
<%@include file="vtkError.jsp" %>
<%
    return;
} catch (IllegalAccessException ex) {
        VtkError error = new VtkError();
        error.setDescription("Sorry, you have no access to view year plan.");
        session.setAttribute("fatalError", error);
%>
<%@include file="vtkError.jsp" %>
<%
        sessionlog.error("Error occured:", ex);
        return;
    }
    if (selectedTroopRepoData == null) {
        try {
            troopUtil.createCouncil(user, selectedTroop);
        } catch (Exception e) {
            VtkError error = new VtkError();
            error.setDescription("Unable to create council record");
            session.setAttribute("fatalError", error);
            %>
            <%@include file="vtkError.jsp" %>
            <%
                sessionlog.error("Error occured:",e);
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
    request.setAttribute("vtk-request-user", user);
    request.setAttribute("vtk-request-troop", selectedTroop);
} else {
    VtkError error = new VtkError();
    error.setDescription("No troop selected");
    session.setAttribute("fatalError", error);
%>
<%@include file="vtkError.jsp" %>
<%
        }
    }
%>