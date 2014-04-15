<%@page import="com.day.cq.wcm.api.Page" %>
<%
Page homepage = currentPage.getAbsoluteParent(3);
ValueMap currentSite = currentPage.getContentResource().adaptTo(ValueMap.class);
%>