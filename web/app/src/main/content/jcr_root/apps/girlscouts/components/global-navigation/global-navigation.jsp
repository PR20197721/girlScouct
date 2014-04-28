<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<!-- apps/girlscouts/components/global-navigation/global-navigation.jsp -->
<%
String[] links = properties.get("links", String[].class);
if ((links == null || links.length == 0) && WCMMode.fromRequest(request) == WCMMode.EDIT) {
	%>##### Global Navigation #####<%
} else {
    %><ul class="inline-list"><%
    for (int i = 0; i < links.length; i++) {
        String[] values = links[i].split("\\|\\|\\|");
        String label = values[0];
        String path = values.length >= 2 ? values[1] : "";
        String clazz = values.length >= 3 ? " "+ values[2] : "";
		%><li><a class="menu<%= clazz %>" href="<%= path %>.html"><%= label %></a></li><%
    }
    %></ul><%
}
%>
