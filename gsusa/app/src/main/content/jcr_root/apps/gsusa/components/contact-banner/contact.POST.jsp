<%@page import="org.girlscouts.web.gsusa.component.boothfinder.BoothFinder" %>
<%@ page import="java.util.Optional" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
    String zipCode = request.getParameter("zipCode");
    String email= request.getParameter("email");
    String firstName = request.getParameter("firstName");
    String phone = request.getParameter("phone");
    String councilAbbrName = Optional.ofNullable(request.getParameter("CouncilAbbrName")).filter(str -> str != null && str.length() > 0).map(str -> "Girlscouts " + str).orElse("the server");
    boolean optIn = "on".equalsIgnoreCase(request.getParameter("optIn"));

    BoothFinder boothFinder = sling.getService(BoothFinder.class);
    String result;
    try {
        result = boothFinder.saveContactMeInformation(zipCode, email, firstName, optIn, phone);
    } catch (Exception e) {
        result = "There is a problem communicating with " + councilAbbrName + ". Please try again later.";
    }
%>
<%= result %>