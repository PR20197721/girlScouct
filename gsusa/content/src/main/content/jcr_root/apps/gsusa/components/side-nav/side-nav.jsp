<%@page import="java.lang.StringBuilder,
                java.util.Iterator,
                com.day.cq.wcm.api.Page" %>
<%@include file="/libs/foundation/global.jsp" %>
<%
    StringBuilder sb = new StringBuilder();
    Page rootPage = currentPage.getAbsoluteParent(2);
    Iterator<Page> iter = rootPage.listChildren();
    
    buildMenu(rootPage, currentPage.getPath(), sb);
%>
<%= sb.toString() %>

<%!
    public void buildMenu(Page rootPage, String currentPath, StringBuilder sb) {
        sb.append("<ul>");

        Iterator<Page> iter = rootPage.listChildren();
        while(iter.hasNext()) {
            Page page = iter.next();
            if (page.isHideInNav()) {
                continue;
            }
            String title = page.getTitle();
            if (title != null && !title.isEmpty()) {
                sb.append("<li>");
                sb.append("<a href=\"" + page.getPath() + ".html\">");
                sb.append(page.getTitle());
                sb.append("</a>");
                
                String path = page.getPath();
                if (currentPath.startsWith(path)) {
                    buildMenu(page, currentPath, sb);
                }
                
                sb.append("</li>");
            }
        }

        sb.append("</ul>");
    }
%>