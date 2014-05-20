
<%
// TODO: from OSGI
String oAuthUrl = "https://test.salesforce.com/services/oauth2/authorize?response_type=code&client_id=3MVG9A2kN3Bn17hsP.Ui7f1jgy.inaXzsZ4VKF6DcS.LKlCZSdhHBeH.992uouy19lQAyvfZC98jgGtM73lIu&redirect_uri=http://localhost:4502/content/testLogin2/login5.html";
String referer = request.getHeader("referer");
String targetUrl = oAuthUrl + "&state=" + referer;
response.sendRedirect(targetUrl);
%>

<%@ page import="com.example.service.*, com.example.model.*, com.np.*"%>



<%
%><%@include file="/libs/foundation/global.jsp"%>
<%
%><%@page session="false"%>
<%
%>
<%



String state= request.getParameter("state");
HttpSession session = request.getSession();
ApiConfig apiConfig = (ApiConfig)session.getValue("sales_force_apiConfig");
if( (apiConfig==null || apiConfig.getId()==null || session.getValue("sales_force_apiConfig") ==null) && request.getParameter("code")==null){


   response.sendRedirect("https://test.salesforce.com/services/oauth2/authorize?response_type=code&client_id=3MVG9A2kN3Bn17hsP.Ui7f1jgy.inaXzsZ4VKF6DcS.LKlCZSdhHBeH.992uouy19lQAyvfZC98jgGtM73lIu&redirect_uri=http://localhost:4502/content/testLogin2/login5.html&state="+request.getParameter("state"));
   if(true)return;

}else if( session.getValue("sales_force_apiConfig") ==null && request.getParameter("code")!=null){ 



String code= request.getParameter("code");
com.np.tester3 t= new com.np.tester3();
apiConfig = t.doAuth(code);

session.putValue("sales_force_apiConfig", apiConfig);

}//edn





apiConfig = (ApiConfig)session.getValue("sales_force_apiConfig");

com.np.tester3 t= new com.np.tester3();
Users user = t.getUserInfo( apiConfig);
%>


<h1>
	hello:
	<%=user.getName()%></h1>

from
<%=state%>
