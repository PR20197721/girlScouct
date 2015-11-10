<%@page import="org.girlscouts.web.gsusa.component.boothfinder.BoothFinder,
                org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.Council,
                org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.BoothBasic,
                java.util.List" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
Council council = (Council)request.getAttribute("gsusa_council_info");
List<BoothBasic> booths = (List<BoothBasic>)request.getAttribute("gsusa_cookie_booths");
String zip = (String)request.getAttribute("gsusa_booth_list_zip");
String radius = (String)request.getAttribute("gsusa_booth_list_radius");
String date = (String)request.getAttribute("gsusa_booth_list_date");
String sortBy = (String)request.getAttribute("gsusa_booth_list_sortby");
int pageNum = (Integer)request.getAttribute("gsusa_booth_list_pagenum");
int numPerPage = (Integer)request.getAttribute("gsusa_booth_list_numperpage");
int showContactBannerPer = properties.get("showContactBannerPer", 25); 

String nearestDistance = "";
if ("distance".equals(sortBy) && !booths.isEmpty()) {
    nearestDistance = booths.get(0).distance;
} else {
    double nearestDistanceValue = Double.MAX_VALUE;
    for (BoothBasic booth : booths) {
        double distance = Double.MAX_VALUE;
        try {
            nearestDistanceValue = Double.parseDouble(booth.distance);
            if (distance < nearestDistanceValue) {
                nearestDistanceValue = distance;
            }
        } catch (Exception e) {}
    }
    nearestDistance = Double.toString(nearestDistanceValue);
}
%>

<div>Booth Locations near you:</div>
<div>The nearest location is <%= nearestDistance %> miles away from <%= zip %>.</div>
<form>
    <div>Radius:</div>
    <input type="hidden" name="zip" value="<%=zip%>"></input>
    <select name="radius">
        <option value="1" <%= "1".equals(radius) ? "selected" : "" %>>1 miles</option>
        <option value="5" <%= "5".equals(radius) ? "selected" : "" %>>5 miles</option>
        <option value="10" <%= "10".equals(radius) ? "selected" : "" %>>10 miles</option>
        <option value="15" <%= "15".equals(radius) ? "selected" : "" %>>15 miles</option>
        <option value="25" <%= "25".equals(radius) ? "selected" : "" %>>25 miles</option>
        <option value="50" <%= "50".equals(radius) ? "selected" : "" %>>50 miles</option>
        <option value="100" <%= "100".equals(radius) ? "selected" : "" %>>100 miles</option>
        <option value="250" <%= "250".equals(radius) ? "selected" : "" %>>250 miles</option>
        <option value="500" <%= "500".equals(radius) ? "selected" : "" %>>500 miles</option>
    </select>
    <div>Date:</div>
    <select name="date">
        <option value="7" <%= "7".equals(date) ? "selected" : "" %>>1 week</option>
        <option value="14" <%= "14".equals(date) ? "selected" : "" %>>2 weeks</option>
        <option value="30" <%= "30".equals(date) ? "selected" : "" %>>1 month</option>
        <option value="60" <%= "60".equals(date) ? "selected" : "" %>>2 months</option>
        <option value="90" <%= "90".equals(date) ? "selected" : "" %>>3 months</option>
        <option value="all" <%= "all".equals(date) ? "selected" : "" %>>all</option>
    </select>
    <div>Sort by:</div>
    <select name="sortBy">
        <option value="distance" <%= "distance".equals(date) ? "selected" : "" %>>distance</option>
        <option value="date" <%= "date".equals(date) ? "selected" : "" %>>date</option>
    </select>
    <div>
        <input type="submit"></input>
    </div>
</form>
<% 
int count = 0;
boolean shouldDisplayContactBanner = "Path2".equals(council.preferredPath);
for (int i = 0; i < Math.min(booths.size(), numPerPage); i++) {
	request.setAttribute("gsusa-booth-list-item", booths.get(i));
	%><cq:include script="booth-item.jsp"/><%
	
    if (shouldDisplayContactBanner && count == showContactBannerPer - 1) {
        %><cq:include script="contact-banner.jsp"/><%
    }
    count = (count + 1) % showContactBannerPer;
} 

String baseLink = "?zip=" + zip + "&radius=" + radius + 
                  "&date" + date + "&sortBy=" + sortBy + "&numPerPage=" + numPerPage;
if (pageNum != 0) {
    String prevLink = baseLink + "&page=" + Integer.toString(pageNum - 1);
    %><a href="<%= prevLink %>">Prev</a><%
}
// If there are more booths, display next link.
if (booths.size() > numPerPage) {
    String nextLink = baseLink + "&page=" + Integer.toString(pageNum + 1);
    %><a href="<%= nextLink %>">Next</a><%
}
%>