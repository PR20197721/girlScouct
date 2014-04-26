<%@ page
	import="java.text.Format,
	java.text.ParseException,
	java.lang.Exception,
	java.text.SimpleDateFormat,
	java.util.Date"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />

<%
	String currentPath = currentPage.getPath() + ".html";

	// date and time
    DateFormat dateFormat = new SimpleDateFormat("mm/dd/yy");
    String time = properties.get("time", " ");
	Date startDate = properties.get("start", Date.class); 
	String startDateStr = dateFormat.format(startDate);
	Date endDate = properties.get("end", Date.class); 
	String dateStr = startDateStr;
	if (endDate != null) {
		String endDateStr = dateFormat.format(startDate);
	    dateStr += " to " + endDateStr;
	}
	String endDateStr = dateFormat.format(endDate);
	
	// content
    String title = properties.get("title", "");
    String details = properties.get("details", " ");

    // images
    String fileReference = properties.get("fileReference", "");
    String imgWidth = properties.get("width", "");
    if (!imgWidth.isEmpty()) imgWidth = "image=\"" + imgWidth + "\"";
    String imgHeight = properties.get("height", "");
    if (!imgHeight.isEmpty()) imgHeight = "image=\"" + imgHeight + "\"";
    String imgAlt = properties.get("alt", "");
    if (!imgAlt.isEmpty()) imgAlt = "image=\"" + imgAlt + "\"";

    // location
    String locationPath = properties.get("location", "");
    String location = null;
    if (!locationPath.isEmpty()) {
		Page locationPage = resourceResolver.resolve(locationPath).adaptTo(Page.class);	
		if (locationPage != null) {
		    location = locationPage.getTitle();
		}
    }
%>

<!-- TODO: fix the h2 color in CSS -->
<h2 style="color: green;"><%= title %></h2>
<% if (!fileReference.isEmpty()) { %>
<p>	
	<img src="<%= fileReference %>" <%= imgWidth %> <%= imgHeight %> <%= imgAlt %> />
</p>
<% } %>
<p>
	Time: <%= time %><br/>
	Date: <%= dateStr %> <br/>
	Location: <%= location %>
</p>
<p>
	<%= details %>
</p>
