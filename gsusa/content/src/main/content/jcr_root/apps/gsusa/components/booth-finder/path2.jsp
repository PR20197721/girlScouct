<%@page import="org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.Council,
                java.util.Map,
                java.util.HashMap" %>
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
<%
Map<String, String> props = new HashMap<String, String>();
props.put("title", "Support Girl Scouts");
props.put("desc", "Enter your info below");
request.setAttribute("gsusa-contact-banner-conf", props);
%>
<cq:include path="path2-contact-us" resourceType="gsusa/components/contact-banner" />
<%
request.setAttribute("gsusa-contact-banner-conf", null);
%>
</p>