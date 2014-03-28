<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>

<%
if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
	%><cq:includeClientLib categories="apps.girlscouts.components.navigationbar"/><%
}

String basicClass = properties.get("type", "global-nav-cell");

String[] links = properties.get("links", String[].class);
if (links == null || links.length == 0) {
	%>##### Navigation Bar #####<%
} else {
    for (int i = 0; i < links.length; i++) {
        String[] values = links[i].split("\\|\\|\\|");
        String label = values[0];
        String path = values[1];
        String clazz = values.length >= 3 ? " "+ values[2] : "";
		%><a href="<%= path %>" class="<%= basicClass %><%= clazz %>"><%= label %></a><%
    }
}

%>