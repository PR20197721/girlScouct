<%@page import="java.lang.StringBuilder,
                java.util.Iterator,
                java.util.List,
                java.util.ArrayList,
                javax.jcr.Value,
                org.apache.sling.api.resource.ResourceResolver,
                com.day.cq.wcm.api.Page" %>
<%@include file="/libs/foundation/global.jsp" %>
<%
    final String siteRootPath = currentPage.getAbsoluteParent(2).getPath();
    final String headerNavPath = siteRootPath + "/jcr:content/header/header-nav";
    final String eyebrowNavPath = siteRootPath + "/jcr:content/header/eyebrow-nav";
    
    final Value[] headerNavValues = resourceResolver.resolve(headerNavPath).adaptTo(Node.class).getProperty("navs").getValues();
    final Value[] eyebrowNavValues = resourceResolver.resolve(eyebrowNavPath).adaptTo(Node.class).getProperty("navs").getValues();
    
    List<String[]> headerNavs = new ArrayList<String[]>();
    List<String[]> eyebrowNavs = new ArrayList<String[]>();
    
    // find in global nav then
    for (int i = 0; i < headerNavValues.length; i++) {
        String[] nav = headerNavValues[i].getString().split("\\|\\|\\|"); // path, text
        headerNavs.add(nav);
        if (currentPage.getPath().startsWith(nav[0]) && found == -1) { // if current page belong to this branch
            found = i;
        }
    }
    
    int count = 0;
    StringBuilder sb = new StringBuilder();
    sb.append("<nav class=\"right-off-canvas-menu\">");
    buildTopMenu(headerNavs, currentPage.getPath(), resourceResolver, sb, found);
    buildTopMenu(eyebrowNavs, currentPage.getPath(), resourceResolver, sb, -1);
    sb.append("</nav>");
%>
<%= sb.toString() %>

<%!
    public void buildTopMenu(List<String[]> navs, String currentPath, ResourceResolver rr, StringBuilder sb, int found) {
        int count = 0;
        sb.append("<ul class=\"off-canvas-list\">");
        for (String[] nav : navs) {
            if (count == found) {
                if (currentPath.equals(nav[0])) {
                	sb.append("<li class=\"active current\">");
                } else {
                	sb.append("<li class=\"active\">");
                }
            } else {
                sb.append("<li>");
            }
            sb.append("<a href=\"" + genLink(rr, nav[0]) + "\" title=\"" + nav[1] + "\">" + nav[1] + "</a>");
            if (count == found) {
                Page rootPage = rr.resolve(nav[0]).adaptTo(Page.class);
                buildMenu(rootPage, currentPath, sb);
            }
            sb.append("</li>");
                
            count++;
        }
        sb.append("</ul>");
    }

    public String genLink(ResourceResolver rr, String link) {
        // This is a Page resource but yet not end with ".html": append ".html"
        if (rr.resolve(link).getResourceType().equals("cq:Page") && !link.contains(".html")) {
            return link + ".html";
        // Well, do nothing
        } else {
            return link;
        }
    }

    public void buildMenu(Page rootPage, String currentPath, StringBuilder sb) {
        Iterator<Page> iter = rootPage.listChildren();
        boolean hasChild = false;
        while(iter.hasNext()) {
            Page page = iter.next();
            if (page.isHideInNav()) {
                continue;
            }
            if (hasChild == false) {
                sb.append("<ul>");
                hasChild = true;
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