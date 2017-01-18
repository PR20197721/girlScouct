<%@ page session="false" %><%
%><%@ page import="
        com.day.cq.tagging.*,
        java.util.Iterator,
        org.apache.jackrabbit.util.Text,
        org.apache.sling.api.resource.Resource"
        
%><%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><%@ taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %><%
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%
%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<sling:defineObjects />

<%
    Tag tag = resource.adaptTo(Tag.class);
%>

<html>
<head><title><%= tag.isNamespace() ? "Tag Namespace" : "Tag" %> "<%= tag.getTagID() %>"</title></head>
<body>
    <h1><%= tag.isNamespace() ? "Tag Namespace" : "Tag" %> "<%= tag.getTagID() %>"</h1>
    
    <dl>
        <dt>tagID</dt><dd><%= tag.getTagID() %></dd>
        <dt>name</dt><dd><%= tag.getName() %></dd>
        <dt>JCR path</dt><dd><a href="<%= Text.escape(tag.getPath(), '%', true) %>"><%= tag.getPath() %></a></dd>
<%  if (currentNode.hasProperty(TagConstants.PN_MOVED_TO)) {
        String movedTo = currentNode.getProperty(TagConstants.PN_MOVED_TO).getString();
%>
        <dt>movedTo</dt><dd><a href="<%= movedTo %>.html"><%= movedTo %></a></dd>
<%  } %>
        <dt>isNamespace</dt><dd><%= tag.isNamespace() %></dd>
        <dt>namespace</dt><dd><a href="<%= Text.escape(tag.getNamespace().getPath(), '%', true) %>.html"><%= tag.getNamespace().getName() %></a></dd>
        <dt>localTagID</dt><dd><%= tag.getLocalTagID() %></dd>
        <dt>title</dt><dd><%= tag.getTitle() %></dd>
        <dt>description</dt><dd><%= tag.getDescription() %></dd>
<%  if (tag.getParent() != null) { %>
        <dt>parent</dt><dd><a href="<%= Text.escape(tag.getParent().getPath(), '%', true) %>.html"><%= tag.getParent().getName() %></a></dd>
<%  } else { %>
        <dt>parent</dt><dd>none</dd>
<%  } %>
        <dt>titlePath</dt><dd><%= tag.getTitlePath() %></dd>
        <dt>child tags</dt>
        <dd>
<%
    Iterator<Tag> childTags = tag.listChildren();
    if (!childTags.hasNext()) {
%>
    Nothing found
<%  } else { %>
    <ul>
<%      while (childTags.hasNext()) {
            Tag childTag = childTags.next();
%>
        <li><a href="<%= Text.escape(childTag.getPath(), '%', true) %>.html"><%= childTag.getName() %></a></li>
<%      } %>
    </ul>
<%
    }
%>
        </dd>
        <dt>all sub tags</dt>
        <dd>
<%
    childTags = tag.listAllSubTags();
    if (!childTags.hasNext()) {
%>
    Nothing found
<%  } else { %>
    <ul>
<%      while (childTags.hasNext()) {
            Tag childTag = childTags.next();
%>
        <li><a href="<%= Text.escape(childTag.getPath(), '%', true) %>.html"><%= childTag.getLocalTagID() %></a></li>
<%      } %>
    </ul>
<%
    }
%>
        </dd>
        <dt>count</dt><dd><%= tag.getCount() %></dd>
        <dt>tagged content</dt>
        <dd>
<%
    Iterator<Resource> nodes = tag.find();
    if (nodes == null || !nodes.hasNext()) {
%>
    Nothing found
<%  } else { %>
    <ul>
<%      while (nodes.hasNext()) {
            Resource node = nodes.next();
%>
        <li><a href="<%= Text.escape(node.getPath(), '%', true) %>"><%= node.getPath() %></a></li>
<%      } %>
    </ul>
<%
    }
%>
        </dd>
    </dl>
    
    <a href="/libs/cq/tagging/content/debug.html">Back to Tag Debug Administration</a>
    
</body>
</html>