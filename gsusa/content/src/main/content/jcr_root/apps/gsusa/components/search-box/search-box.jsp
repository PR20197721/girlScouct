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
} else if(null != searchAction) {
	action = currentSite.get(searchAction,String.class);
%>
	<form action="<%=action%>.html" method="get" class="search-form">
		<input type="text" name="q" placeholder="<%=placeholderText %>" tabindex="35" pattern=".{3,}" required title="3 characters minimum" /> <%
		//this if case is for the search box in mobile/tablet view
		if ("true".equals((String)request.getAttribute("fromHeaderNav"))) { %>
			<span class="icon-search-magnifying-glass"></span>
		<%}
		%>
	</form>
<%}%>


