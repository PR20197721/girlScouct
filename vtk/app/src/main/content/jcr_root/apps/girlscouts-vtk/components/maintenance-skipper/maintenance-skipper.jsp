<%@include file="/libs/foundation/global.jsp" %>
<%@page session="false" %>
<%
    final String SKIP_COOKIE = "vtk-skip-maintenance";
    String param = request.getParameter("skip");
    Cookie cookie;
    if (param != null && param.equals("true")) {
        cookie = new Cookie(SKIP_COOKIE, "true");
    } else {
        cookie = new Cookie(SKIP_COOKIE, "false");
    }
    cookie.setPath("/content/girlscouts-vtk");
    response.addCookie(cookie);
%>
<script>
    if(window.location.href.includes("uat.")){
         window.location.replace("https://my-uat.girlscouts.org/content/girlscouts-vtk/en/vtk.html");
    }else{
         window.location.replace("https://my.girlscouts.org/content/girlscouts-vtk/en/vtk.html");
    }

</script>