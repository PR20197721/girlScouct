<%@page import="com.day.cq.commons.jcr.JcrUtil,
	javax.jcr.Session" %>

<%@include file="/libs/foundation/global.jsp"%>
<%
	String path = request.getParameter("path");
	String type = request.getParameter("type");
	if (type == null || type.isEmpty()) {
		type = "cq:Page";
	}

	Session session = resourceResolver.adaptTo(Session.class);
	
	if (path != null  && !path.isEmpty()) {
		JcrUtil.createPath(path, type, session);
		session.save();
	}
%>