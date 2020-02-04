<%@ page import="org.apache.sling.api.resource.Resource" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<%!
    int MAX_TITLE = 58;

    public String trimTitle(String str) {
        String ret = str;
        if (ret.length() > MAX_TITLE) {
            ret = ret.substring(0, MAX_TITLE) + "...";
        }
        return ret;
    }
%>
<nav class="breadcrumbs">
<%
    Resource resrc = resource.getResourceResolver().getResource(currentPage.getPath() + "/jcr:content");
    String delimStr = currentStyle.get("delim", "");
    String trailStr = currentStyle.get("trail", "");
    // TO DO Move Hard Code into a variable this in the properties file
    if (resrc.isResourceType("girlscouts/components/event-page") || resrc.isResourceType("girlscouts/components/news-page")) {
        String breadcrumb = currentSite.get("leftNavRoot", String.class);
        if (resrc.isResourceType("girlscouts/components/news-page")) {
            breadcrumb = currentSite.get("newsPath", String.class);
        }
        String absolutePath = currentPage.getAbsoluteParent(2).getPath();
        String[] actualPaths = breadcrumb.substring(absolutePath.length() + 1, breadcrumb.length()).split("/");
        for (String str : actualPaths) {
            absolutePath += "/" + str;
            Page parentNode = resource.getResourceResolver().getResource(absolutePath).adaptTo(Page.class);
            String title = parentNode.getTitle();
            %>
            <%= xssAPI.filterHTML(delimStr) %><a href="<%= xssAPI.getValidHref(parentNode.getPath()+".html") %>"><%= xssAPI.encodeForHTML(title) %></a>
            <%
        }
        String displayTitle = trimTitle(currentPage.getTitle());
        %>
        <%= xssAPI.filterHTML(delimStr) %><span class="breadcrumbCurrent"><%= xssAPI.encodeForHTML(displayTitle) %></span>
        <%
    } else {
        long level = 3;
        int currentLevel = currentPage.getDepth();
        String prevTitle = "";
        String prevPath = "";
        if(level < currentLevel){
            while (level < currentLevel) {
                String title = "";
                Page crumb  = currentPage.getAbsoluteParent((int) level);
                if (crumb == null) {
                    break;
                }
                title = crumb.getNavigationTitle();
                if (title == null || title.equals("")) {
                    title = crumb.getNavigationTitle();
                }
                if (title == null || title.equals("")) {
                    title = crumb.getTitle();
                }
                if (title == null || title.equals("")) {
                    title = crumb.getName();
                }
                if(title != null) {
                    if(prevTitle != null && prevTitle.length()>0 && !prevTitle.equals(title)){ %>
                        <%= xssAPI.filterHTML(delimStr) %><a href="<%= xssAPI.getValidHref(prevPath) %>"><%= xssAPI.encodeForHTML(trimTitle(prevTitle)) %></a><%
                    }
                    prevTitle = title;
                    prevPath = crumb.getPath()+".html";
                }
                level++;
            }
            %><span class="breadcrumbCurrent"><%= xssAPI.filterHTML(delimStr) %><%= xssAPI.encodeForHTML(trimTitle(prevTitle)) %></span><%
            if (trailStr.length() > 0) {
                %><%= xssAPI.filterHTML(trailStr) %><%
            }
        }
    }//end of else
    %>
</nav>
