<%@ page import="org.apache.sling.api.resource.Resource" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<%!
    int MAX_TITLE = 58;

    public String trimTitle(String str) {
        String ret = str;
        int titleLength = ret.length();
        if (ret.length() > MAX_TITLE) {
            ret = ret.substring(0, MAX_TITLE) + "...";
        }
        return ret;
    }
%>
<nav class="breadcrumbs">
    <%
        Resource resrc = resource.getResourceResolver().getResource(currentPage.getPath() + "/jcr:content");
        String t_resourse = resrc.getResourceType();
        String delimStr = currentStyle.get("delim", "");
        String trailStr = currentStyle.get("trail", "");
        String delim = "";
        String title = "";
        // TO DO Move Hard Code into a variable this in the properties file
        if (resrc.isResourceType("girlscouts/components/event-page") || resrc.isResourceType("girlscouts/components/news-page")) {
            String breadcrumb = currentSite.get("leftNavRoot", String.class);
            ;
            if (resrc.isResourceType("girlscouts/components/news-page")) {
                breadcrumb = currentSite.get("newsPath", String.class);
            }
            String absolutePath = currentPage.getAbsoluteParent(2).getPath();
            String[] actualPaths = breadcrumb.substring(absolutePath.length() + 1, breadcrumb.length()).split("/");
            for (String str : actualPaths) {
                absolutePath += "/" + str;
                Page parentNode = resource.getResourceResolver().getResource(absolutePath).adaptTo(Page.class);
                title = parentNode.getTitle();
    %>
    <%= xssAPI.filterHTML(delim) %><a href="<%= xssAPI.getValidHref(parentNode.getPath()+".html") %>"><%= xssAPI.encodeForHTML(title) %>
</a>
    <%
        }
        String displayTitle = trimTitle(currentPage.getTitle());
    %>
    <%= xssAPI.filterHTML(delim) %><span class="breadcrumbCurrent"><%= xssAPI.encodeForHTML(displayTitle) %></span>
    <%
    } else {
        Page trail = null;
        long level = 3;
        int currentLevel = currentPage.getDepth();
        while (level < currentLevel - 1) {
            trail = currentPage.getAbsoluteParent((int) level);
            Resource trailRcs = trail.adaptTo(Resource.class);
            trailRcs = trailRcs.getChild("jcr:content");
            if (!trailRcs.isResourceType("girlscouts/components/placeholder-page") && !trailRcs.isResourceType("gsusa/components/placeholder-page")) {
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
                %>
                <%= xssAPI.filterHTML(delim) %><a href="<%= xssAPI.getValidHref(trail.getPath()+".html") %>"><%= xssAPI.encodeForHTML(title) %></a>
                <%
                delim = delimStr;
            }
            level++;
        }
        trail = currentPage.getAbsoluteParent((int) level);
        if (trail != null) {
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
            String displayTitle = trimTitle(title);
    %>
    <span class="breadcrumbCurrent"><%= xssAPI.filterHTML(delim) %><%= xssAPI.encodeForHTML(displayTitle) %></span>
    <%
        if (trailStr.length() > 0) {
    %><%= xssAPI.filterHTML(trailStr) %><%
            }
        }
    }//end of else
%>
</nav>
