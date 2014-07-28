<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%
String placeholderText = properties.get("placeholder-text","");
String searchAction = properties.get("searchAction", null);
String action="";
if ((null==searchAction) && WCMMode.fromRequest(request) == WCMMode.EDIT) {
%>
	Please edit Search Box Component
<%
} else {
	action = currentSite.get(searchAction,String.class);
%>
	<form action="<%=action%>.html" method="get">
		<input type="text" name="q" placeholder="<%=placeholderText %>" class="searchField" />
	</form>
<%}%>
