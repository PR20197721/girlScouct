<%@include file="/libs/foundation/global.jsp"%>

<h1>GIRL SCOUTS</h1>

<%
	Page trail = null;
	String title = "";

    long level = 3;

    String delimStr = currentStyle.get("delim", "&nbsp;&gt;&nbsp;");
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
             onclick="CQ_Analytics.record({event:'followBreadcrumb',values: { breadcrumbPath: '<%= xssAPI.getValidHref(trail.getPath()) %>' },collect: false,options: { obj: this },componentPath: '<%=resource.getResourceType()%>'})"><%
        %><%= xssAPI.encodeForHTML(title) %><%
        %></a><%

        delim = delimStr;
        level++;
    }

	trail = currentPage.getAbsoluteParent((int) level);

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

    %><%= xssAPI.encodeForHTML(title) %><%

    if (trailStr.length() > 0) {
        %><%= xssAPI.filterHTML(trailStr) %><%
    }

%>
