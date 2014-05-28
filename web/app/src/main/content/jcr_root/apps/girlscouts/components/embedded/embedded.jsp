<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
	String html = properties.get("html", "");

    if (html.isEmpty() && WCMMode.fromRequest(request) == WCMMode.EDIT) {
		%>##### Embedded HTML #####<%
    } else {
        %><%= html %><%
    }
%>
