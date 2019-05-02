<%@ page
    import="java.util.Arrays,java.util.Iterator,
    java.util.regex.Matcher,
    java.util.regex.Pattern,
    java.util.List"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<div id="right-canvas-menu-bottom">
  <ul class="side-nav" style="background-color:#6b6b6b;">
<%
String currPath = currentPage.getPath();
String[] links = (String[])(request.getAttribute("links"));
if(links != null){
for (int i = 0; i < links.length; i++) {
     String[] values = links[i].split("\\|\\|\\|");
     String label = values[0];
     String menuPath = values.length >= 2 ? values[1] : "";
     String path = values.length >= 2 ? values[1] : "";
     path = genLink(resourceResolver, path);
     String clazz = values.length >= 3 ? "class=\""+ values[2] + "\"": "";
     String newWindow = values.length >= 4 && values[3].equalsIgnoreCase("true") ?
             " target=\"_blank\"" : "";
     if(currPath.equals(menuPath) && i == 0){%>
        <li id="side-nav-bottom-first" class="active">
     <%}else if(currPath.equals(menuPath)){ %>
     	<li class="active">
     <%}else if(i == 0){ %>
        <li id="side-nav-bottom-first">
     <%}else{ %>
        <li>
     <% } %>
		<div><a <%= clazz %> href="<%= path %>"<%= newWindow %>><%= label %></a></div><hr></li>
 <% } 
}%>
//Add main menu link
<li class='side-nav-el'>
<div class='side-nav-wrapper'><a href=\""+ homepagePath +".html\" title=\"MainMenu\">MAIN MENU</a></div><hr>
</li>
</ul>
</div>