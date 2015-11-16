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
Fill out the form and girls from the <a href="<%= council.url %>" target="_blank"><strong><%= council.name %></strong></a> will help you place an order.
<cq:include script="contact-banner.jsp" />
</p>