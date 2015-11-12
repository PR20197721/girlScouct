<%@page import="org.girlscouts.web.gsusa.component.boothfinder.BoothFinder" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
String zipCode = request.getParameter("zipCode");
String email= request.getParameter("email");
String firstName = request.getParameter("firstName");
String phone = request.getParameter("phone");
boolean optIn = "on".equalsIgnoreCase(request.getParameter("optIn"));

BoothFinder boothFinder = sling.getService(BoothFinder.class);
String result = boothFinder.saveContactMeInformation(zipCode, email, firstName, optIn, phone);
%>
<%= result %>