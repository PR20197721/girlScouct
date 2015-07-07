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
<nav class="left-nav">
    <%= sb.toString() %>
</nav>

<%!
    public void buildMenu(Page rootPage, String currentPath, StringBuilder sb) {

        Iterator<Page> iter = rootPage.listChildren();
        boolean hasChild = false;
        while(iter.hasNext()) {
            if (hasChild == false) {
                sb.append("<ul>");
                hasChild = true;
            }
            
            Page page = iter.next();
            if (page.isHideInNav()) {
                continue;
            }
            String title = page.getTitle();
            if (title != null && !title.isEmpty()) {
                String path = page.getPath();
                boolean isActive = currentPath.startsWith(path);
                String activeCls = isActive ? "active" : "";
                boolean isCurrent = currentPath.equals(path);
                String currentCls = isCurrent ? " current" : "";
                
                if (isActive || isCurrent) {
                    sb.append("<li class=\"" + activeCls + currentCls + "\">");
                } else {
                    sb.append("<li>");
                }
                sb.append("<a href=\"" + page.getPath() + ".html\">");
                sb.append(page.getTitle());
                sb.append("</a>");
                
                if (isActive) {
                    buildMenu(page, currentPath, sb);
                }
                
                sb.append("</li>");
            }
        }
        
        if (hasChild) {
            sb.append("</ul>");
        }
    }
%>