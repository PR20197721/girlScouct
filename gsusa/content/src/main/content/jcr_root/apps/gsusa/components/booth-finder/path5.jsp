<%@page import="org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.Council,
                java.text.DateFormat,
                java.text.SimpleDateFormat,
                java.util.Date" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
Council council = (Council)request.getAttribute("gsusa_council_info");
DateFormat format = new SimpleDateFormat("M/d/yyyy");
Date startDate = format.parse(council.cookieSaleStartDate);
// Should we use joda time?
long daysBetween = (startDate.getTime() - new Date().getTime() + 1000*3600*24) / 1000 / 3600 / 24;
if (daysBetween <= 0) {
	daysBetween = 0;
}
%>
<div>
<strong><%= council.name %></strong> cookie sale starts in <span id="cntdwn"><%= Long.toString(daysBetween) %></span> <%= daysBetween == 1 ? "day" : "days" %>.
Until then, check out cookie related merchandise in the Girl Scout online shop."
(Link to cookie related merchandise on GS online Shop)
</div>