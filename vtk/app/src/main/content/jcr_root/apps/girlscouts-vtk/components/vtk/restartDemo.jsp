<%@ page
        import="org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.User, org.girlscouts.vtk.ejb.TroopUtil, org.girlscouts.vtk.utils.VtkUtil, javax.servlet.http.Cookie" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%
    HttpSession session = request.getSession();
    try {
        final TroopUtil troopUtil = sling.getService(TroopUtil.class);
        Troop troop = VtkUtil.getTroop(session);
        try {
            troopUtil.rmTroop(troop);
        } catch (Exception e) {
            e.printStackTrace();
        }
        session.setAttribute("VTK_troop", null);
        session.setAttribute(User.class.getName(), null);
        session.setAttribute(ApiConfig.class.getName(), null);
        Cookie killMyCookie = new Cookie("girl-scout-name", null);
        killMyCookie.setMaxAge(0);
        killMyCookie.setPath("/");
        response.addCookie(killMyCookie);
    } catch (Exception e) {
        e.printStackTrace();
    }
    if (request.getParameter("isLogout") != null) {
        session.setAttribute("demoSiteUser", null);
    } else {
        session.setAttribute("demoSiteUser", true);
    }
    response.sendRedirect("http://vtkdemo.girlscouts.org/content/girlscouts-demo/en.html?");
%>
 

