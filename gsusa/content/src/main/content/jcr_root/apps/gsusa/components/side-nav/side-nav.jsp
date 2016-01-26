<%@page import="java.lang.StringBuilder,
				java.util.regex.Pattern,
    			java.util.regex.Matcher,
                java.util.Iterator,
                com.day.cq.wcm.api.Page" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%
    StringBuilder sb = new StringBuilder();
	boolean isContentHub = isContentHub(currentPage);
	if (isContentHub) {
		PageManager pm = resourceResolver.adaptTo(PageManager.class);
		System.out.println(request.getRequestURI());
		//TODO: please wait to see what the vanity URL is
		//cut out the last part
		String pattern = "(.*[/])";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(request.getRequestURI());
		if (m.find( )) {
	         System.out.println("Found value: " + m.group(0) );
	    } else {
	       System.out.println("NO MATCH");
	    }
		currentPage = pm.getPage(m.group(0));
	}
    Page rootPage = currentPage.getAbsoluteParent(3);
    Iterator<Page> iter = rootPage.listChildren();
    
    String rootPageCurrent = rootPage.getPath().equals(currentPage.getPath()) ? " current" : "";
    while (currentPage.isHideInNav()) {
    	currentPage = currentPage.getParent();
    }
    buildMenu(rootPage, currentPage.getPath(), sb);
    String rootPageDispTitle = "" ;
    
    if (rootPage.getNavigationTitle() != null && !"".equals(rootPage.getNavigationTitle())) {
    	rootPageDispTitle = rootPage.getNavigationTitle(); 
    } else {
    	rootPageDispTitle = rootPage.getTitle();
    }
    
    
%>
<nav class="left-nav">
  <ul>
    <li class="active<%= rootPageCurrent %>">
      <%= sb.toString() %>
    </li>
  </ul>
</nav>

<%!
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
            String title = "";
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
                sb.append("<a href=\"" + page.getPath() + ".html\" title=\"" + title +"\">");
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
%>
