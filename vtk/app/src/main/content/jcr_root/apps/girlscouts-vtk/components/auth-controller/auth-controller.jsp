<%@ page import="org.girlscouts.vtk.auth.dao.SalesForceDAO,
	org.girlscouts.vtk.auth.models.ApiConfig,
	org.girlscouts.vtk.auth.models.User" %>

<%
// TODO: from OSGI
String oAuthUrl = "https://test.salesforce.com/services/oauth2/authorize?response_type=code&client_id=3MVG9A2kN3Bn17hsP.Ui7f1jgy.inaXzsZ4VKF6DcS.LKlCZSdhHBeH.992uouy19lQAyvfZC98jgGtM73lIu&redirect_uri=http://localhost:4502/content/testLogin2/login5.html";
String referer = request.getHeader("referer");
String targetUrl = oAuthUrl + "&state=" + referer;
%>

<%
%><%@include file="/libs/foundation/global.jsp"%>
<%
%><%@page session="false"%>
<%
%>
<%

String state= request.getParameter("state");
HttpSession session = request.getSession();
ApiConfig apiConfig = (ApiConfig)session.getAttribute("sales_force_apiConfig");
if( (apiConfig==null || apiConfig.getId()==null || session.getValue("sales_force_apiConfig") ==null) && request.getParameter("code")==null){


   response.sendRedirect(targetUrl);
   if(true)return;

}else if( session.getAttribute("sales_force_apiConfig") ==null && request.getParameter("code")!=null){ 



String code= request.getParameter("code");
SalesForceDAO dao = new SalesForceDAO();
apiConfig = dao.doAuth(code);

session.putValue("sales_force_apiConfig", apiConfig);

}//edn

apiConfig = (ApiConfig)session.getAttribute("sales_force_apiConfig");

SalesForceDAO dao = new SalesForceDAO();
User user = dao.getUser(apiConfig);
session.setAttribute("org.girlscouts.vtk.auth.models.User", user);

if (state == null) {
    state = referer;
}
response.sendRedirect(state);
%>