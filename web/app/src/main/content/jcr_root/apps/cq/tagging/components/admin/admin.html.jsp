<%@ page session="false" %><%
%><%@ page import="
        com.day.cq.tagging.*,
        java.util.Iterator,
        org.apache.jackrabbit.util.Text,
        org.apache.sling.api.resource.Resource,
        org.apache.commons.lang3.StringEscapeUtils"
        
%><%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><%@ taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %><%
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%
%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%

%><sling:defineObjects /><%

    TagManager tagManager = slingRequest.getResourceResolver().adaptTo(TagManager.class);
    String searchTag = slingRequest.getParameter("searchTag");
    Tag tag = null;
    if (searchTag != null) {
        tag = tagManager.resolve(searchTag);
        if (tag == null) {
            tag = tagManager.resolveByTitle(searchTag);
        }
    }
    String searchTagTitle = slingRequest.getParameter("searchTagTitle");
%>
<html>
<head>
    <title>Tag Administration Console</title>
</head>
<body>
    <h1>Tag Administration Console</h1>
    
<% if (searchTag == null) {
        searchTag = "";
%>
    <h2>Searching for content</h2>
<% } else if (tag == null) { %>
    <h2>Searching for content tagged with <%= StringEscapeUtils.escapeHtml4(searchTag) %> (tag does not exist)</h2>
<% } else { %>
    <h2>Searching for content tagged with <a href="<%= Text.escape(tag.getPath(), '%', true) %>.html"><%= StringEscapeUtils.escapeHtml4(searchTag) %></a></h2>
<% } %>

    <form method="GET">
        <input name="searchTag" value="<%= StringEscapeUtils.escapeHtml4(searchTag) %>" />
        <input type="hidden" name="_charset_" value="utf-8" />
        <input type="submit" value="Search for Tag" />
    </form>

<%
    if (tag != null) {
        Iterator<Resource> nodes = tag.find();
        if (nodes != null && nodes.hasNext()) {
%>
    <ul>
<%          while (nodes.hasNext()) {
                Resource node = nodes.next();
                String link = node.getPath();
                // hack for cq:Page
                if (link.endsWith("/jcr:content")) {
                    link = link.substring(0, link.length() - "/jcr:content".length());
                }
%>
        <li><a href="<%= Text.escape(link, '%', true) %>.html"><%= node.getPath() %></a></li>
<%          } %>
    </ul>
<%      } else { %>
    <p>Nothing found!</p>
<%      }
    }
%>

<% if (searchTagTitle == null || searchTagTitle.length() == 0) {
        searchTagTitle = "";
%>
    <h2>Searching for content by title</h2>
<% } else { %>
    <h2>Searching for content tagged with title <%= StringEscapeUtils.escapeHtml4(searchTagTitle) %></h2>
<% } %>

    <form method="GET">
        <input name="searchTagTitle" value="<%= StringEscapeUtils.escapeHtml4(searchTagTitle) %>" />
        <input type="hidden" name="_charset_" value="utf-8" />
        <input type="submit" value="Search for Tag title" />
    </form>

<%
    if (searchTagTitle != null && searchTagTitle.length() > 0) {
        TagManager.FindResults results = tagManager.findByTitle(searchTagTitle);
%>
    <h4>Tags searched</h4>
    <ul>    
<%      for (Tag searchedTag : results.tags) { %>
        <li><a href="<%= Text.escape(searchedTag.getPath(), '%', true) %>.html"><%= StringEscapeUtils.escapeHtml4(searchedTag.getName()) %></a></li>
<%      } %>
    </ul>
<%
            
        Iterator<Resource> nodes = results.resources;
        if (nodes != null && nodes.hasNext()) {
%>
    <h4>Content found</h4>
    <ul>
<%          while (nodes.hasNext()) {
                Resource node = nodes.next();
                String link = node.getPath();
                // hack for cq:Page
                if (link.endsWith("/jcr:content")) {
                    link = link.substring(0, link.length() - "/jcr:content".length());
                }
%>
        <li><a href="<%= Text.escape(link, '%', true) %>.html"><%= node.getPath() %></a></li>
<%          } %>
    </ul>
<%      } else { %>
    <p>Nothing found!</p>
<%      }
    }
%>

    <h2>Create or get a Tag</h2>
    
    <p>Specify an exact Tag ID, eg. &quot;car&quot;, &quot;car/vw&quot; or &quot;mysite:car/vw&quot;</p>
    
    <form method="POST" action="<%= resource.getPath() %>">
        <input name="createTag" />
        <input type="hidden" name="_charset_" value="utf-8" />
        <input type="submit" value="Create" />
    </form>
    
    <h2>Create a Tag by Title</h2>
    
    <p>Specify a title path, eg. &quot;Car&quot;, &quot;Car / VW&quot; or &quot;MySite: Car / VW&quot;</p>
    
    <form method="POST" action="<%= resource.getPath() %>">
        <input name="createTagTitlePath" />
        <input type="hidden" name="_charset_" value="utf-8" />
        <input type="submit" value="Create" />
    </form>
    
    <h2>Create random tags</h2>
    
    <p>Creates a number of random tags. Can take a while.</p>
    
    <form method="POST" action="<%= resource.getPath() %>">
        <input type="hidden" name="createRandomTags" value="true" />
        Start: <input name="start" value="0" size="8" /> End: <input name="end" value="100" size="8" />
        <input type="hidden" name="_charset_" value="utf-8" />
        <input type="submit" value="Create" />
    </form>
    
    <h2>Browse Tag Namespaces</h2>

    <ul>    
<%
    Tag[] tags = tagManager.getNamespaces();
    for (Tag nsTag : tags) {
%>
        <li><a href="<%= Text.escape(nsTag.getPath(), '%', true) %>.html"><%= StringEscapeUtils.escapeHtml4(nsTag.getName()) %></a></li>
<%        
    }
%>
    </ul>
</body>
</html>