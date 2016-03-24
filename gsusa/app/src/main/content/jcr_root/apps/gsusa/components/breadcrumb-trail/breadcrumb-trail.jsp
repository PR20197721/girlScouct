<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode"%>
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

<%
	String breadcrumbRoot = properties.get("breadcrumbRoot", "");
	Boolean isHidden = properties.get("isHidden", false);

	Resource resrc = resource.getResourceResolver().getResource(currentPage.getPath() +"/jcr:content");
	String t_resourse = resrc.getResourceType();
	String delimStr = currentStyle.get("delim", "");
	String trailStr = currentStyle.get("trail", "");
	String delim = "";
	String title="";

	if (!isHidden) {  %>
		<ul class="breadcrumb inline-list"><%
	    Page trail = null;
	    long level; //topic
	    int currentLevel = currentPage.getDepth();
	    if ("root".equals(breadcrumbRoot)) {
	    	level = 2;
	    } else if ("subTopic".equals(breadcrumbRoot)) {
	    	level = 4;
	    } else {  //default to topic;
	    	level = 3;
	    }
	    
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
	        <li><%= xssAPI.filterHTML(delim) %><a href="<%= xssAPI.getValidHref(trail.getPath()+".html") %>"><%= xssAPI.encodeForHTML(title) %></a></li>
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
	        <li><span><%= xssAPI.filterHTML(delim) %><%= xssAPI.encodeForHTML(displayTitle) %></span></li>
	        <%
	            if (trailStr.length() > 0) {
	            %><%= xssAPI.filterHTML(trailStr) %><%
	        }
	    } else { //breadcrumb would be null if the root of the breadcrumb is set to a subtopic, but the page is actually a topic page
	    	//user mistakes
	    	//The user is not allowed to set the breadcrumb to start with subtopic when he/she is in the topic page
	    	%>Breadcrumb root is not set up properly. Please set the "breadcrumb root" one to two layers up to allow the current page to find its parent.<% 
	    }%>
	    </ul> <%
	} else if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
		 %>Breadcrumb is currently hidden for this page. Please click here to edit. <%
	}
%>

