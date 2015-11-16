<%@page import="org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.Council" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
Council council = (Council)request.getAttribute("gsusa_council_info");
if (council == null) {
	council = new Council();
}
%>
<p>
	The cookie sale is underway for the <a href="<%= council.url %>" target="_blank"><strong><%= council.name %></strong></a>.
	No cookie booth sales are currently scheduled, but keep checking back for updated results.
</p>