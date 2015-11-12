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
String result = "";
try {
    result = boothFinder.saveContactMeInformation(zipCode, email, firstName, optIn, phone);
} catch (Exception e) {
	result = "There is a problem communicating with the server. Please try again later.";
}
%>
<%= result %>