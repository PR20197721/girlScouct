<%@include file="/libs/foundation/global.jsp"%>

<%@include file="/apps/girlscouts/components/global.jsp" %>
<nav class="breadcrumbs">
<%


   Resource resrc = resource.getResourceResolver().getResource(currentPage.getPath() +"/jcr:content");
   String t_resourse = resrc.getResourceType();
   String delimStr = currentStyle.get("delim", "");
   String trailStr = currentStyle.get("trail", "");
   String delim = "";
   String title="";
   // TO DO Move Hard Code into a variable this in the properties file
   if(resrc.isResourceType("girlscouts/components/event-page")){
String breadcrumb = currentSite.get("leftNavRoot", String.class);
String absolutePath = currentPage.getAbsoluteParent(2).getPath();
String[] actualPaths = breadcrumb.substring(absolutePath.length()+1, breadcrumb.length()).split("/");

for(String str: actualPaths)
{
absolutePath += "/"+str;
Page parentNode = resource.getResourceResolver().getResource(absolutePath).adaptTo(Page.class);
title = parentNode.getTitle();
%><%= xssAPI.filterHTML(delim) %><%
%><a href="<%= xssAPI.getValidHref(parentNode.getPath()+".html") %>"><%= xssAPI.encodeForHTML(title) %></a>

<%}%>
<%= xssAPI.filterHTML(delim) %><%
             %><a class="current" href="#"><%= xssAPI.encodeForHTML(currentPage.getTitle()) %></a>
  <%
   }else
   {
Page trail = null;
    long level = 3;
    int currentLevel = currentPage.getDepth();
    while (level < currentLevel - 1) {
        trail = currentPage.getAbsoluteParent((int) level);
        if (trail == null) {
            break;
        }
        title = trail.getNavigationTitle();
        if (title == null || title.equals("")) {
            title = trail.getNavigationTitle();
        }
        if (title == null || title.equals("")) {
            title = trail.getTitle();
        }
        if (title == null || title.equals("")) {
            title = trail.getName();
        }
        %><%= xssAPI.filterHTML(delim) %><%
        %><a href="<%= xssAPI.getValidHref(trail.getPath()+".html") %>"
        %><%= xssAPI.encodeForHTML(title) %><%
        %></a><%
        delim = delimStr;
        level++;
    }
trail = currentPage.getAbsoluteParent((int) level);
if(trail != null){

        title = trail.getNavigationTitle();
    
        if (title == null || title.equals("")) {
            title = trail.getNavigationTitle();
        }
        if (title == null || title.equals("")) {
            title = trail.getTitle();
        }
        if (title == null || title.equals("")) {
            title = trail.getName();
        }
        %><%= xssAPI.filterHTML(delim) %><%
        %><a class="current" href="#"><%= xssAPI.encodeForHTML(title) %></a><%
    
        if (trailStr.length() > 0) {
            %><%= xssAPI.filterHTML(trailStr) %><%
        }
}
   }//end of else
%>
</nav>