<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
String[] links = properties.get("links", String[].class);
Boolean centerLinks = (Boolean) request.getAttribute("centerLinks");
//TODO: Find a way to have the links center dynamically
if ((links == null || links.length == 0) && WCMMode.fromRequest(request) == WCMMode.EDIT) {
	%>##### Footer Navigation #####<%
} else {
	for (int i = 0; i < links.length; i++) {
		String[] values = links[i].split("\\|\\|\\|");
		String label = values[0];
		String path = values.length >= 2 ? values[1] : "";
		path = genLink(resourceResolver, path);
		String clazz = values.length >= 3 ? " "+ values[2] : "";
		if(centerLinks != null && centerLinks == false){
%>
	<a class="menu<%= clazz %>" href="<%= path %>"><%= label %></a>
<%
		} else {
			if (i==0){
%>
	<ul id="smallFooterLinks" class="small-block-grid-2">
<%
			}
%>
		<li>
			<a class="text-center menu<%= clazz %>" href="<%= path %>"><%= label %></a>
		</li>
<%
			if (i == links.length - 1) {
%>
	</ul>
<%
			}
		}
	}
}
%>
