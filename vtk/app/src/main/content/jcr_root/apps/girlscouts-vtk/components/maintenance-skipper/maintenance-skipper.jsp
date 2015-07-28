<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
    final String SKIP_COOKIE = "vtk-skip-maintenance";
    String param = request.getParameter("skip");
    if (param != null && param.equals("true")) {
        Cookie cookie = new Cookie(SKIP_COOKIE, "true");
        cookie.setPath("/");
        response.addCookie(cookie);
    }
%>