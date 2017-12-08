<%@page import="java.net.URLEncoder"%>
<%@ page import = "java.util.Map" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.girlscouts.vtk.sso.saml.*,org.girlscouts.vtk.sso.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<%
  AppSettings appSettings = new AppSettings();
  appSettings.setAssertionConsumerServiceUrl("http://localhost:4503/content/girlscouts-vtk/controllers/auth.sso.html?j_validate=false");  
  appSettings.setIssuer("https://gsusa--gsuat.cs11.my.salesforce.com");
  
  AccountSettings accSettings = new AccountSettings();
  accSettings.setIdpSsoTargetUrl("https://gsuat-gsmembers.cs11.force.com/members/idp/login?app=0spZ0000000004h");

  AuthRequest authReq = new AuthRequest(appSettings, accSettings);

  //Get RelayState
  Map<String, String[]> parameters = request.getParameterMap();
  String relayState = null;
        for(String parameter : parameters.keySet()) {
            if(parameter.equalsIgnoreCase("relaystate")) {
                String[] values = parameters.get(parameter);
                relayState = values[0];
            }
        }
  String reqString = authReq.getSSOurl(relayState);
  System.err.println("******** test: "+  reqString);
  response.sendRedirect(reqString);
%>
</head>
<body>
</body>
</html>
