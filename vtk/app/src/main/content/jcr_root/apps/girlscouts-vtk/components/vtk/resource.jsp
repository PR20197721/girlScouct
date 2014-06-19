<%@page import="com.day.cq.tagging.TagManager,
                com.day.cq.tagging.Tag,
                java.util.Iterator,
                java.net.URLEncoder,
                org.girlscouts.web.search.DocHitBase" %>
<%@include file="/libs/foundation/global.jsp" %>
Debug: q = <%= (String)request.getParameter("q") %>

<%
    String currentPath = "#";
%>
<div>Search For Resources</div>
<form>
    <input type="text" name="q"/>
    <input type="submit" value="Search"/>
</form> 

<p>Browse Resources by Category</p>

<%
final String CATEGORY_ROOT_TAG = "girlscouts-vtk:category";
final TagManager manager = resourceResolver.adaptTo(TagManager.class);

try {
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
    			    String link = "?category=" + URLEncoder.encode(currentMinor.getTagID());
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

String category = (String)request.getParameter("category");
if (category != null && !category.isEmpty()) {
    try {
        Tag categoryTag = manager.resolve(category);
    	Iterator<Resource> resIter = categoryTag.find();
    	while (resIter.hasNext()) {
    	    Resource res= resIter.next();
    	    try {
				DocHitBase hit = new DocHitBase(res.adaptTo(Node.class));
				String icon = hit.getIcon();
    		    String title = hit.getTitle();
    		    String path = hit.getURL();
    		    // TODO: icon not showing
    		    %><div><%= icon %><a href="<%= path %>"><%= title %></a></div><%
    	    } catch (Exception eRes) {
    			log.error("Cannot get VTK asset: " + eRes.getMessage());
    	    }
    	}
    } catch (Exception eCat) {
    	log.error("Cannot get VTK asset category: " + category + ". Message: "+ eCat.getMessage());
    }
}
%>

<%!
	
%>