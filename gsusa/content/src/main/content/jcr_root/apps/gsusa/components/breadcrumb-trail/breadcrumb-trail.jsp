<%@include file="/libs/foundation/global.jsp"%>
<%!
	int MAX_TITLE = 58;
	public String trimTitle(String str) {
                String ret = str;
                int titleLength = ret.length();
                if (ret.length() > MAX_TITLE) {
                        ret = ret.substring(0,MAX_TITLE) + "...";
                }
		return ret;
	}
%>
<nav class="breadcrumbs">
<%
	Resource resrc = resource.getResourceResolver().getResource(currentPage.getPath() +"/jcr:content");
	String t_resourse = resrc.getResourceType();
	String delimStr = currentStyle.get("delim", "");
	String trailStr = currentStyle.get("trail", "");
	String delim = "";
	String title="";


    Page trail = null;
    long level = 2;
    int currentLevel = currentPage.getDepth();
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
        %>
        <%= xssAPI.filterHTML(delim) %><a href="<%= xssAPI.getValidHref(trail.getPath()+".html") %>"><%= xssAPI.encodeForHTML(title) %></a>
        <%
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
        String displayTitle = trimTitle(title);
        %>
        <span class="breadcrumb-current"><%= xssAPI.filterHTML(delim) %><%= xssAPI.encodeForHTML(displayTitle) %></span>
        <%
            if (trailStr.length() > 0) {
            %><%= xssAPI.filterHTML(trailStr) %><%
        }
    }
%>
</nav>
