<%@page import="org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.Council" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
Council council = (Council)request.getAttribute("gsusa_council_info");
%>
<h1>Ready for Cookies?</h1>
<p>
	The cookie sale is underway for the <strong><%= council.name %></strong>.
	No cookie booth sales are currently scheduled, but keep checking back for updated results.
</p>