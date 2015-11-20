<%@page import="org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.Council" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
Council council = (Council)request.getAttribute("gsusa_council_info");
if (council == null) {
	council = new Council();
}
%>
<h4 class="special-title">We are getting ready for the next cookie season.</h4> 
<p>
The cookie sale has ended for the <a href="<%= council.url %>" target="_blank"><strong><%= council.name %></strong></a>.
Check out cookie related merchandise in the <a href="http://www.girlscoutshop.com/" target="_blank"></a>Girl Scout online shop</a>.
</p>