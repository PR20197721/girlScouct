<%@page import="com.day.cq.commons.PathInfo" %>
<%@include file="/libs/foundation/global.jsp"%>

<%
	final String PAR_PATH = "jcr:content/content/middle/par";

	PathInfo pathInfo = new PathInfo(request.getPathInfo());
	String suffix = pathInfo.getSuffix().substring(1);

	String resourcePath = null;
	try {
	    resourcePath = suffix.substring(suffix.indexOf('/'));
	} catch (Exception e) {
	   	log.error("Cannot get resource path."); 
	}
	
	if (resourcePath != null) {
	    resourcePath = resourcePath + "/" + PAR_PATH;
	    %><cq:include path="<%= resourcePath %>" resourceType="foundation/components/parsys"/><%
	}
%>
