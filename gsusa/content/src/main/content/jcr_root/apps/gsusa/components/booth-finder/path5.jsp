<%@page import="org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.Council,
                java.text.DateFormat,
                java.text.SimpleDateFormat,
                java.util.Date" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
Council council = (Council)request.getAttribute("gsusa_council_info");
if (council == null) {
	council = new Council();
}
DateFormat format = new SimpleDateFormat("M/d/yyyy");
long daysBetween = 0;
try {
	Date startDate = format.parse(council.cookieSaleStartDate);
	// Should we use joda time?
	daysBetween = (startDate.getTime() - new Date().getTime() + 1000*3600*24) / 1000 / 3600 / 24;
	if (daysBetween <= 0) {
		daysBetween = 0;
	}
} catch (java.text.ParseException e) {
	
}

String msg = "Only " + Long.toString(daysBetween) + " days until cookie season.";
request.setAttribute("gsusa-share-model-header", msg);
%>
<h1>Search Results</h1>
<h4 class="special-title"><%= msg %></h4>
<p>
<a href="<%= council.url %>" target="_blank"><strong><%= council.name %></strong></a> cookie sale starts in <span id="cntdwn"><%= Long.toString(daysBetween) %></span> <%= daysBetween == 1 ? "day" : "days" %>.
Until then, check out cookie related merchandise in the <a href="http://www.girlscoutshop.com/" taret="_blank">Girl Scout online shop</a>.
</p>

<%
request.setAttribute("gsusa-share-model-header", "Cookies are coming in " + daysBetween + " days");
request.setAttribute("gsusa-share-modal-tweet", "#GirlScoutsCookie time is coming in " + daysBetween + " days to my neighborhood. Find your cookies. @girlscouts"); 
request.setAttribute("gsusa-share-modal-description", "I searched for Girl Scout Cookies. Search for Girl Scout cookies in your neighborhood");
request.setAttribute("gsusa-share-modal-img-path", "/content/dam/girlscouts-gsusa/images/Cookies/share-default.png");
slingRequest.setAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE, true);
%>
<cq:include path="share-modal" resourceType="gsusa/components/share-modal" />
<%
slingRequest.removeAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE);
request.setAttribute("gsusa-share-modal-img-path", null);
request.setAttribute("gsusa-share-model-header", null);
request.setAttribute("gsusa-share-modal-tweet", null); 
request.setAttribute("gsusa-share-modal-description", null);
%>