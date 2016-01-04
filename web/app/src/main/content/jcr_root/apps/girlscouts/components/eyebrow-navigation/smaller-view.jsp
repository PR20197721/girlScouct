<%@ page
    import="java.util.Arrays,java.util.Iterator,
    java.util.regex.Matcher,
    java.util.regex.Pattern,
    java.util.List"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<div id="right-canvas-menu-bottom">
  <ul class="side-nav">
<%
String currPath = currentPage.getPath();
String[] links = (String[])(request.getAttribute("links"));
for (int i = 0; i < links.length; i++) {
     String[] values = links[i].split("\\|\\|\\|");
     String label = values[0];
     String menuPath = values.length >= 2 ? values[1] : "";
     String path = values.length >= 2 ? values[1] : "";
     path = genLink(resourceResolver, path);
     String clazz = values.length >= 3 ? "class=\""+ values[2] + "\"": "";
     String newWindow = values.length >= 4 && values[3].equalsIgnoreCase("true") ?
             " target=\"_blank\"" : "";
     if(currPath.equals(menuPath)){%>
         <li class="active">
     <%}else{ %>
     	<li>
     <% } %>
		<div><a href="<%= path %>"<%= newWindow %>><%= label %></a></div></li>
 <% } %>
</ul>
</div>