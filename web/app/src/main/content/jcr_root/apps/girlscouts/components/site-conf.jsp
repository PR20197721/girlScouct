<%@page import="com.day.cq.wcm.api.Page" %>
<%
Page homepage = currentPage.getAbsoluteParent(2);
ValueMap currentSite = homepage.getContentResource().adaptTo(ValueMap.class);
%>