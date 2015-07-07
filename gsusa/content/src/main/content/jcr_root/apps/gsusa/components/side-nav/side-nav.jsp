<%@page import="java.lang.StringBuilder,
                java.util.Iterator,
                com.day.cq.wcm.api.Page" %>
<%@include file="/libs/foundation/global.jsp" %>
<%
    StringBuilder sb = new StringBuilder();
    Page rootPage = currentPage.getAbsoluteParent(2);
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
                sb.append("<a href=\"" + page.getPath() + "\">");
                sb.append(page.getTitle());
                sb.append("</a>");
                
                String rootPath = rootPage.getPath();
                if (currentPath.startsWith(rootPath) && // is a child page
                        !currentPath.equals(rootPath)) { // not a leaf child 
                    buildMenu(page, currentPath, sb);
                }
                
                sb.append("</li>");
            }
        }

        sb.append("</ul>");
    }
%>