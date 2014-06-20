<%@page import="com.day.cq.tagging.TagManager,
                com.day.cq.tagging.Tag,
                java.util.Iterator,
                java.util.Map,
                java.util.HashMap" %>
<%@include file="/libs/foundation/global.jsp" %>
Debug: q = <%= (String)request.getParameter("q") %>

<%
	String currentPath = currentPage.getPath() + ".html";
%>
<div>Search For Resources</div>
<form>
    <input type="text" name="q"/>
    <input type="submit" value="Search"/>
</form> 

<p>Browse Resources by Category</p>

<%
try {
	final String CATEGORY_ROOT_TAG = "girlscouts-vtk:category";
	final TagManager manager = resourceResolver.adaptTo(TagManager.class);
	final Tag categoryRootTag = manager.resolve(CATEGORY_ROOT_TAG);
	Iterator<Tag> majorIter = categoryRootTag.listChildren();
	int majorCount = 0;
	while (majorIter.hasNext()) {
	    majorIter.next();
		majorCount++; 
	}
	
	majorIter = categoryRootTag.listChildren();
%>
	
	<ul class="small-block-grid-<%= majorCount %>">
<% 
		while (majorIter.hasNext()) { 
		    Tag currentMajor = majorIter.next();
			%><li><% 
				%><div><%= currentMajor.getTitle() %></div><%
				Iterator<Tag> minorIter = currentMajor.listChildren();
				while (minorIter.hasNext()) {
				    Tag currentMinor = minorIter.next();
				    String link = currentPath + "?" + currentMinor.getPath();
				    String title = currentMinor.getTitle();
				    String count = Long.toString(currentMinor.getCount());
				    %><div><a href="<%= link %>"><%= title %> (<%= count %>)</a></div><%
				}
			%></li><%
		} 
	%></ul><%
} catch (Exception e) {
	log.error("Cannot get VTK asset categories: " + e.getMessage());
}
%>