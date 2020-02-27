<%@ page
    import="java.util.Arrays,
    java.util.List,
    org.apache.sling.api.SlingHttpServletRequest,
    com.day.cq.commons.Externalizer"%>
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

                String reqProtocol = request.getHeader("X-Forwarded-Proto");
                url = externalizer.externalLink(rr,siteRootPath,reqProtocol,  path);
                if (!url.endsWith(".html")){
                    url.concat(".html");
                }

            }catch(Exception e){

            }
        }
        return url;
    }
%>

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
		<a href="<%= generateLink(resourceResolver, path) %>"<%= newWindow %>><%= label %></a></li>
    <% } %>

