<%@include file="/libs/foundation/global.jsp"%>
<nav class="breadcrumbs">
<%
	Page trail = null;
	String title = "";

    long level = 3;

    String delimStr = currentStyle.get("delim", "");
    String trailStr = currentStyle.get("trail", "");
    int currentLevel = currentPage.getDepth();
    String delim = "";
    while (level < currentLevel - 1) {
        trail = currentPage.getAbsoluteParent((int) level);
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


        %><%= xssAPI.filterHTML(delim) %><%
        %><a href="<%= xssAPI.getValidHref(trail.getPath()+".html") %>"
        %><%= xssAPI.encodeForHTML(title) %><%
        %></a><%

        delim = delimStr;
        level++;
    }

	trail = currentPage.getAbsoluteParent((int) level);
	if(trail != null){

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
    
    
        %><%= xssAPI.filterHTML(delim) %><%
        %><a class="current" href="#"><%= xssAPI.encodeForHTML(title) %></a><%
    
        if (trailStr.length() > 0) {
            %><%= xssAPI.filterHTML(trailStr) %><%
        }
	}

%>
</nav>