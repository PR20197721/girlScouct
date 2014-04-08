<%@page import="com.day.cq.commons.jcr.JcrUtil,
	javax.jcr.Session,
	javax.jcr.Node" %>

<%@include file="/libs/foundation/global.jsp"%>
<%
	String path = request.getParameter("path");
	String type = request.getParameter("type");
	String propertiesStr = request.getParameter("properties");
	
	if (type == null || type.isEmpty()) {
		type = "cq:Page";
	}

	Session session = resourceResolver.adaptTo(Session.class);
	
	if (path != null  && !path.isEmpty()) {
		Node node = JcrUtil.createPath(path, type, session);
		Node jcrNode = node.addNode("jcr:content", "cq:PageContent");
		
		if (propertiesStr != null && !propertiesStr.isEmpty()) {
			String[] targetProperties = propertiesStr.split("\\|\\|\\|");
			for (int i = 0; i < targetProperties.length; i++) {
				String[] tuple = targetProperties[i].split("\\|");
				if (tuple.length == 2) {
					jcrNode.setProperty(tuple[0], tuple[1]);
				}
			}
		}
		session.save();
	}
%>