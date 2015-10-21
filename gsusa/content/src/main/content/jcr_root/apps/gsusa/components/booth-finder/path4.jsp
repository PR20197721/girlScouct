<%@page import="org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.Council" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
Council council = (Council)request.getAttribute("gsusa_council_info");
%>
<div>
The cookie sale has ended for the <strong><%= council.name %></strong>.
Check out cookie related merchandise in the Girl Scout online shop.
</div>