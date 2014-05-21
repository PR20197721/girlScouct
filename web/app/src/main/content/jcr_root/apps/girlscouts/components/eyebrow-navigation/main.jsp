<%@ page
    import="java.util.Arrays,
    java.util.List"%>
<%@include file="/libs/foundation/global.jsp"%>
<% 
   String[] links = (String[])(request.getAttribute("links"));
   for (int i = 0; i < links.length; i++) {
        String[] values = links[i].split("\\|\\|\\|");
        String label = values[0];
        String path = values.length >= 2 ? values[1] : "";
        String clazz = values.length >= 3 ? "class=\""+ values[2] + "\"": "";
        %>
        <li><a href="<%= path %>.html"><%= label %></a></li>
        <%
    }
    
    %>