<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
    final String SKIP_COOKIE = "vtk-skip-maintenance";
    String param = request.getParameter("skip");
    Cookie cookie = new Cookie(SKIP_COOKIE);
    cookie.setValue(param != null && param.equals("true") ? "true" : "false");
    cookie.setPath("/content/girlscouts-vtk");
    response.addCookie(cookie);
%>