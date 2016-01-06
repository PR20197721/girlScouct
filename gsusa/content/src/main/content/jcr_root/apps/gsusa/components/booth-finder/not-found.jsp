<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
String notFoundText = properties.get("notFoundText", "");
%>
<%= notFoundText %>