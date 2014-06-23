<%@page import="java.util.Iterator,
                java.util.Map,
                java.util.Queue,
                com.day.cq.wcm.api.PageManager,
                com.day.cq.wcm.api.Page" %>
<%@include file="/libs/foundation/global.jsp" %>

<%
	final String RESOURCE_SEARCH_PROMPT = "type in a search word or term here";
%>

<%-- search box --%>
<div>Search For Resources</div>
<form method="get">
	<input type="text" name="q" placeholder="<%=RESOURCE_SEARCH_PROMPT%>" class="searchField" />
</form>

<%-- categories --%>
<p>Browse Resources by Category</p>

<%
final PageManager manager = (PageManager)resourceResolver.adaptTo(PageManager.class);
try {
    // TODO: this field should come from Salesforce
	final String ROOT_PAGE_PATH = "/content/girlscouts-usa/en/resources";

	final Page rootPage = manager.getPage(ROOT_PAGE_PATH);
	
	Iterator<Page> majorIter = rootPage.listChildren();

	int majorCount = 0;
	while (majorIter.hasNext()) {
	    majorIter.next();
		majorCount++; 
	}
	
	majorIter = rootPage.listChildren();
%>
	
	<ul class="small-block-grid-<%= majorCount %>">
<% 
		while (majorIter.hasNext()) { 
		    Page currentMajor = majorIter.next();
			%><li><% 
				%><div><%= currentMajor.getTitle() %></div><%
				Iterator<Page> minorIter = currentMajor.listChildren();

				minorIter = currentMajor.listChildren();
				while (minorIter.hasNext()) {
				    Page currentMinor = minorIter.next();
				    // TODO: encode URL
				    String link = "?category=" + currentMinor.getPath();
				    String title = currentMinor.getTitle();
				    int minorCount = countAllChildren(currentMinor) - 1;
				    %><div><a href="<%= link %>"><%= title %> (<%= minorCount %>)</a></div><%
				}
			%></li><%
		} 
	%></ul><%
} catch (Exception e) {
	log.error("Cannot get VTK meeting aid categories: " + e.getMessage());
}
%>

<%-- display aids --%>
<%
	String categoryParam = (String)request.getParameter("category");
	Page categoryPage = manager.getPage(categoryParam);
	
	if (categoryPage != null) {
		%><div><%= categoryPage.getTitle() %></div><%
		%><ul><%
			StringBuilder builder = new StringBuilder();
			Iterator<Page> resIter = categoryPage.listChildren();
			while (resIter.hasNext()) {
			    Page resPage = resIter.next();
				displayAllChildren(resPage, builder);
			}
			%><%= builder.toString() %><%
		%></ul><%
	}
%>

<%!
	private int countAllChildren(Page page) {
		// TODO: Need an effecient way.
		if (page == null) {
		    return 0;
		}

		int count = 1;
		Iterator<Page> iter = page.listChildren();
		while (iter.hasNext()) {
		    Page currentPage = iter.next();
		    count += countAllChildren(currentPage);
		}
		return count;
	}

	private void displayAllChildren(Page rootPage, StringBuilder builder) {
	    builder.append("<li>");
	    String path = rootPage.getPath() + ".plain.html";
	    builder.append("<a href=\"javascript:void(0)\" onclick=\"displayHtmlResource('");
	    builder.append(path);
	    builder.append("')\">");
	    builder.append(rootPage.getTitle());
	    builder.append("</a>");
	    Iterator<Page> iter = rootPage.listChildren();
	    while (iter.hasNext()) {
	        Page childPage = iter.next();
		    builder.append("<ul>");
		    displayAllChildren(childPage, builder);
		    builder.append("</ul>");
	    }
	    builder.append("</li>");
	}
%>