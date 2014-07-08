<%@page import="com.day.cq.commons.PathInfo" %>
<%@include file="/libs/foundation/global.jsp"%>

<%
	final String TYPE_OVERVIEW = "overview";
	final String TYPE_HTML = "html";

	PathInfo pathInfo = new PathInfo(request.getPathInfo());
	String suffix = pathInfo.getSuffix().substring(1);

	try {
		String type = suffix.substring(0, suffix.indexOf('/'));	    
		if (type.equals(TYPE_OVERVIEW)){
		   	%><cq:include script="overview.jsp"/><% 
		} else if (type.equals(TYPE_HTML)) {
		    
		}
	} catch (Exception e) {
	    log.error("Cannot load VTK resource");
	}
%>