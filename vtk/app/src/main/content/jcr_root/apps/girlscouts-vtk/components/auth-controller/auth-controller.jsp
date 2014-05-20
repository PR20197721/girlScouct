<%
// TODO: from OSGI
String oauthUrl = "https://test.salesforce.com/services/oauth2/authorize?response_type=code&client_id=3MVG9A2kN3Bn17hsP.Ui7f1jgy.inaXzsZ4VKF6DcS.LKlCZSdhHBeH.992uouy19lQAyvfZC98jgGtM73lIu";
String refer = request.getHeader("referer");
String targetUrl = oAuthUrl + "&referer=" + referer;
response.sendRedirect(targetUrl);
%>