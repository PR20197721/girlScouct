<%@page import="org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.Council" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
Council council = (Council)request.getAttribute("gsusa_council_info");
%>
<p>
The cookie sale is underway for the <strong><%= council.name %></strong>.
Fill out the form and girls from the <strong><%= council.name %></strong> will help you place an order.
(Input form)
(After submitting the form, display the following message)
Great! A representative of the <strong><%= council.name %></strong> will contact you shortly. Thanks for supporting the Girl Scout Cookie Program! Also, check out cookie related merchandise in the Girl Scout online shop.
(Link to cookie related merchandise on GS online Shop)
</p>