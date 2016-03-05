<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
String notFoundText = properties.get("notFoundText", "");

if (notFoundText.isEmpty() && WCMMode.fromRequest(request) == WCMMode.EDIT) {
	%>Booth Result Not Found: double click here to configure.<% 
} else {
	%><%= notFoundText %><%
}
%>