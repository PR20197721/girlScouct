<%@ page
	import="java.text.Format,
	java.text.ParseException,
	java.lang.Exception,
	java.text.SimpleDateFormat,
	java.text.DateFormat,
	java.util.Date"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />

<%
	String currentPath = currentPage.getPath() + ".html";

	// date and time
    DateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
	DateFormat timeFormat = new SimpleDateFormat("KK:mm a");

	Date startDate = properties.get("start", Date.class); 
	String startDateStr = dateFormat.format(startDate);
	String startTimeStr = timeFormat.format(startDate);
	
	Date endDate = properties.get("end", Date.class); 
	String dateStr = startDateStr;
    String time = startTimeStr;
	if (endDate != null) {
		String endDateStr = dateFormat.format(startDate);
		String endTimeStr = timeFormat.format(endDate);
	    dateStr += " to " + endDateStr;
	    time += " to " + endTimeStr;
	}
	String endDateStr = dateFormat.format(endDate);
	
	// content
    String title = currentPage.getTitle();
    String details = properties.get("details", " ");

    // images
    boolean hasImage = currentNode.hasNode("image");
    String fileReference = null;
    String imgWidth = null;
    String imgHeight = null;
    String imgAlt = null;
    if (hasImage) {
		ValueMap imageProps = resourceResolver.resolve(currentNode.getPath() + "/image").adaptTo(ValueMap.class);
	    fileReference = imageProps.get("fileReference", "");
	    imgWidth = imageProps.get("width", "");
	    if (!imgWidth.isEmpty()) imgWidth = "width=\"" + imgWidth + "\"";
	    imgHeight = imageProps.get("height", "");
	    if (!imgHeight.isEmpty()) imgHeight = "height=\"" + imgHeight + "\"";
	    imgAlt = imageProps.get("alt", "");
	    if (!imgAlt.isEmpty()) imgAlt = "alt=\"" + imgAlt + "\"";
    }

    // location
    String locationPath = properties.get("location", "");
    String location = "";
    if (!locationPath.isEmpty()) {
		Page locationPage = resourceResolver.resolve(locationPath).adaptTo(Page.class);	
		if (locationPage != null) {
		    location = locationPage.getTitle();
		}
    }
%>

<!-- TODO: fix the h2 color in CSS -->
<h2 style="color: green;"><%= title %></h2>
<% if (hasImage) { %>
<p>	
	<img src="<%= fileReference %>" <%= imgWidth %> <%= imgHeight %> <%= imgAlt %> />
</p>
<% } %>
<p>
	Time: <%= time %><br/>
	Date: <%= dateStr %> <br/>
<% if (!location.isEmpty()) { %>
	Location: <%= location %>
<% } %>
</p>
<p>
	<%= details %>
</p>
