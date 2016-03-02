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
    
    int found = -1;
    
    // find in global nav then
    for (int i = 0; i < headerNavValues.length; i++) {
        String[] nav = headerNavValues[i].getString().split("\\|\\|\\|"); // path, text
        
        if (nav.length < 2) {
            continue;
        }

        // Take small label. If not available, use medium label instead, otherwise large.
        String label = nav.length >= 5 ? nav[4] : "";
        if (label.isEmpty()) {
            label = nav.length >= 4 ? nav[3] : label;
        }
        if (label.isEmpty()) {
            label = nav[0];
        }
        nav[0] = label;

        headerNavs.add(nav);
        
        String topPath = nav[1];
        
        //if external, then nav starts with http
        if (nav[1].startsWith("http")) {
        	continue;
        } else {
        	// rePath restrings nav[1] up to level 4 which is the top menu
        	// /content/gsusa/en/topmenu
        	topPath = rePath(nav[1],4);
        }

        if (currentPage.getPath().startsWith(topPath) && found == -1) { // if current page belong to this branch
            found = i;
        }
    }
    
    // eyebrow nav
    for (int i = 0; i < eyebrowNavValues.length; i++) {
        String[] nav = eyebrowNavValues[i].getString().split("\\|\\|\\|"); // path, text
        headerNavs.add(nav);
    }

    StringBuilder sb = new StringBuilder();
    sb.append("<nav class=\"right-off-canvas-menu\" tabindex=\"-1\">");
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
            	sb.append("<li class=\"active\" tabindex=\"-1\">");
                /**
                This should not happen since this is top menu and top page always calls on 
                its first child page
            	if (currentPath.equals(nav[1])) {
                	sb.append("<li class=\"active current\" tabindex=\"-1\">");
                } else {
                	sb.append("<li class=\"active\" tabindex=\"-1\">");
                }
                **/
            } else {
                sb.append("<li tabindex=\"-1\">");
            }
            if (nav[1].indexOf("http:") != -1 || nav[1].indexOf("https:") != -1) {
                sb.append("<a x-cq-linkchecker=\"skip\" href=\"" + genLink(rr, nav[1]) + "\" title=\"" + nav[0] + "\" tabindex=\"-1\">" + nav[0] + "</a>");
            } else {
                sb.append("<a href=\"" + genLink(rr, nav[1]) + "\" title=\"" + nav[0] + "\" tabindex=\"-1\">" + nav[0] + "</a>");
            }
            if (count == found) {            	
                String topMenuPath = nav[1].substring(0,nav[1].lastIndexOf("/"));
                Page topMenuPathPage = rr.resolve(topMenuPath).adaptTo(Page.class);
                
                buildMenu(topMenuPathPage, currentPath, sb);
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
            
            String title = "";//page.getTitle();
            if (page.getNavigationTitle() != null && !"".equals(page.getNavigationTitle())) {
            	title = page.getNavigationTitle();
            } else {
            	title = page.getTitle();
           	}
            if (title != null && !title.isEmpty()) {
                String path = page.getPath();
                boolean isActive = (currentPath + "/").startsWith(path + "/");
                String activeCls = isActive ? "active" : "";
                boolean isCurrent = currentPath.equals(path);
                String currentCls = isCurrent ? " current" : "";
                
                if (isActive || isCurrent) {
                    sb.append("<li class=\"" + activeCls + currentCls + "\">");
                } else {
                    sb.append("<li>");
                }
                sb.append("<a href=\"" + page.getPath() + ".html\">");
                sb.append(title);
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
    
	public String rePath(String path, int level) {
		
		String[] array = path.split("/");
		level++;
		
		if ((level > array.length) || (level <= 0)) {
			level = array.length;
		}
		
		StringBuilder newPath = new StringBuilder();    	
		for (int i = 1; i < level; i++) {    		
			newPath.append("/"+array[i]);	    		
		}  	    	
		
		return newPath.toString();
	}
%>
