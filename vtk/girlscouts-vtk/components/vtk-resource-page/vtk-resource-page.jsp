<%@page import="com.day.cq.commons.PathInfo" %>
<%@include file="/libs/foundation/global.jsp"%>

<!-- <div class="printer right"/> -->
<%
	final String TYPE_OVERVIEW = "overview";
	final String TYPE_WEB = "web";

	PathInfo pathInfo = new PathInfo(request.getPathInfo());
	String suffix = pathInfo.getSuffix().substring(1);
	
	try {
		String type = suffix.substring(0, suffix.indexOf('/'));	    
		if (type.equals(TYPE_OVERVIEW)){
		   	%><cq:include script="overview.jsp"/><% 
		} else if (type.equals(TYPE_WEB)) {
		   	%><cq:include script="web.jsp"/><% 
		}
	} catch (Exception e) {
	    log.error("Cannot load VTK resource");
	}
%>