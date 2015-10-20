<%@page import="org.girlscouts.web.gsusa.component.boothfinder.BoothFinder,
                org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.BoothBasic,
                java.util.List,
                org.apache.sling.api.SlingHttpServletRequest" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
String zip = getDefault("zip", "", slingRequest);
String radius = getDefault("radius", "20", slingRequest);
String date = getDefault("date", "60", slingRequest);
String sortBy = getDefault("sortBy", "distance", slingRequest);
String pageParam = request.getParameter("page");
int pageNum;
try {
	pageNum = Integer.parseInt(pageParam);
} catch (Exception e) {
	pageNum = 0;
}

int numPerPage = properties.get("numPerPage", 50);

BoothFinder boothFinder = sling.getService(BoothFinder.class);
List<BoothBasic> booths = boothFinder.getBooths(zip, date, radius, sortBy, pageNum, numPerPage);

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

<%!
public String getDefault(String param, String defaultValue, SlingHttpServletRequest req) {
	String value = req.getParameter(param);
	return value == null || value.trim().isEmpty() ? defaultValue : value;
}
%>

<div>Booth Locations near you:</div>
<div>The nearest location is <%= nearestDistance %> miles away from <%= zip %>.</div>
<form action="#">
	<div>Radius:</div>
	<select name="radius">
		<option value="50" <%= "50".equals(radius) ? "selected" : "" %>>50 miles</option>
		<option value="20" <%= "20".equals(radius) ? "selected" : "" %>>20 miles</option>
		<option value="10" <%= "10".equals(radius) ? "selected" : "" %>>10 miles</option>
		<option value="5" <%= "5".equals(radius) ? "selected" : "" %>>5 miles</option>
	</select>
</form>
<% for (BoothBasic booth : booths) { %>
	<div>
		<div><%= booth.location %></div>
		<div><%= booth.address1 %></div>
		<div><%= booth.address2 %></div>
	</div>	
	<div>
		<div></div>
	</div>
	<div>
		<div><%= booth.distance %> Miles</div>
	</div>
<% } %>