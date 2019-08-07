<%@page import="org.girlscouts.vtk.auth.models.ApiConfig,
                org.girlscouts.vtk.models.Troop,
                org.girlscouts.vtk.models.User,
                org.girlscouts.vtk.osgi.service.GirlScoutsSalesForceService" %>
<%@ page import="org.girlscouts.vtk.osgi.component.util.VtkUtil" %>
<%@ page import="java.util.List, java.util.Set" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<cq:defineObjects/>
<%
    HttpSession session = request.getSession();
    session.invalidate();
    session = request.getSession();
    session.setAttribute("VTK_troop", null);
    session.setAttribute(User.class.getName(), null);
    session.setAttribute(ApiConfig.class.getName(), null);
    Cookie killMyCookie = new Cookie("girl-scout-name", null);
    killMyCookie.setMaxAge(0);
    killMyCookie.setPath("/");
    response.addCookie(killMyCookie);
    boolean isGroupDemo = request.getParameter("isGroupDemo") != null;
    String contactId = request.getParameter("user");
    ApiConfig apiConfig = new ApiConfig();
    apiConfig.setUserId(contactId);
    apiConfig.setDemoUser(true);
    apiConfig.setDemoUserName(contactId);
//getUser
    User user = sling.getService(GirlScoutsSalesForceService.class).getUser(apiConfig);
    List<Troop> userTroops = user.getTroops();
    user.setName(contactId);
    for (Troop troop:userTroops) {
        if (!isGroupDemo && troop.getPermissionTokens().contains(13)) { //if not parent
            troop.setTroopId("SHARED_" + session.getId() + "_" + troop.getSfTroopId()); //group
            troop.setPath(troop.getPath().replace(troop.getSfTroopId(),troop.getTroopId()));
            troop.setSfTroopId(troop.getTroopId());
            troop.setId(troop.getTroopId());
        }
        Set perms = troop.getPermissionTokens();
        perms.remove(304); //edit troop photo
        perms.remove(641); //PERMISSION_SEND_EMAIL_act_ID
        perms.remove(642); //email all parents
        troop.setPermissionTokens(perms);
    }
    session.setAttribute(ApiConfig.class.getName(), apiConfig);
    session.setAttribute(User.class.getName(), user);
    if (request.getParameter("prefGradeLevel") != null) {
%>
<script>
    gsusa.component.dropDown('#vtk-dropdown-3333', {local: true}, '<%=request.getParameter("prefGradeLevel")%>');
</script>
<%
        //set prefTroop
        Cookie cookie = new Cookie("vtk_prefTroop", request.getParameter("prefGradeLevel"));
        cookie.setMaxAge(-1);
        cookie.setPath("/");
        response.addCookie(cookie);
    }//edn if pref level
    Cookie cookie = new Cookie("troopDataToken", userTroops.get(0).getHash());
    cookie.setPath("/");
    response.addCookie(cookie);
    response.sendRedirect("/content/girlscouts-vtk/en/vtk.html");
%>