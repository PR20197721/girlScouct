<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%
String placeholderText = properties.get("placeholder-text","");
String searchPath = properties.get("searchPath", null);
if (searchPath==null && WCMMode.fromRequest(request) == WCMMode.EDIT) {
%>
	PLEASE SET A SEARCH PATH
<%
} else if(searchPath == null) {
	%> Search functionality is not configured <%
} else {
%>
	<form action="<%=searchPath%>.html" method="get">
		<input type="text" name="q" placeholder="<%=placeholderText %>" class="searchField" />
	</form>
<%}%>
