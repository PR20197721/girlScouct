<%@page import="org.apache.sling.api.request.RequestPathInfo" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>

<%
	String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
	String id = null;
	for (String selector : selectors) {
		if (!"list-popup".equals(selector)) {
			id = selector;
			break;
		}
	}
	String path = resource.getPath() + "/" + id;
%>

<cq:include path="<%= path %>" resourceType="girlscouts-vtk/components/resources/popup-list" />