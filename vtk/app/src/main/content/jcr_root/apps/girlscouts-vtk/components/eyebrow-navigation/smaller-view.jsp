<%@ page
    import="java.util.Arrays,java.util.Iterator,
    java.util.regex.Matcher,
    java.util.regex.Pattern,
    java.util.List,
    com.day.cq.commons.Externalizer,
    org.slf4j.Logger,
    org.slf4j.LoggerFactory,
    org.apache.sling.api.resource.ResourceResolver,
    com.day.cq.wcm.api.Page,
    org.apache.sling.api.SlingHttpServletRequest"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%!
public String generateLink(ResourceResolver rr, String path){
        Logger log = LoggerFactory.getLogger(this.getClass().getName());
        String url = path;
        if(url.startsWith("/content/")){
            try {
                Page thatPage = rr.resolve(path).adaptTo(Page.class);
                final Externalizer externalizer = rr.adaptTo(Externalizer.class);
                String siteRootPath = thatPage.getAbsoluteParent(1).getPath();

                url = externalizer.externalLink(rr,siteRootPath,"http",  path);
                if (!url.endsWith(".html")){
                    url = url + ".html";
                }
                if (!url.startsWith("http")){
                    url = "http" + url;
                } else if (url.startsWith("https")){
                    url = "http" + url.substring(5);
                }
            }catch(Exception e){

            }
        }
        return url;
    }
%>
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
     if(currPath.equals(menuPath)){%>
         <li class="active">
     <%}else{ %>
     	<li>
     <% } %>
		<div><a <%= clazz %> href="<%= generateLink(resourceResolver, path) %>"<%= newWindow %>><%= label %></a></div></li>
 <% } 
}%>
</ul>
</div>