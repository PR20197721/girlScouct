<%@page import="com.day.cq.commons.jcr.JcrUtil,
	javax.jcr.Session,
	javax.jcr.Node" %>

<%@include file="/libs/foundation/global.jsp"%>
<%
	String path = request.getParameter("path");
	String type = request.getParameter("type");
	String propertiesStr = request.getParameter("prop");

	if (type == null || type.isEmpty()) {
		type = "cq:Page";
	}

	Session session = resourceResolver.adaptTo(Session.class);

	if (path != null  && !path.isEmpty()) {
		Node node = JcrUtil.createPath(path, false, type, type, session, true);
		Node jcrNode = null;
		if (type.equals("cq:Page")) {
		    if (!node.hasNode("jcr:content")) {
				jcrNode = node.addNode("jcr:content", "cq:PageContent");
                session.save();
		    } else {
				jcrNode = node.getNode("jcr:content");
		    }
		} else {
		    jcrNode = node;
		}

		if (propertiesStr != null && !propertiesStr.isEmpty()) {
			String[] targetProperties = propertiesStr.split("\\|\\|\\|");
			for (int i = 0; i < targetProperties.length; i++) {
				String[] tuple = targetProperties[i].split("\\|");
				if (tuple.length == 2) {
					if (tuple[1].equalsIgnoreCase("true") || tuple[1].equalsIgnoreCase("false")){
						boolean value = Boolean.valueOf(tuple[1]);
						jcrNode.setProperty(tuple[0], value);
					} else {
						jcrNode.setProperty(tuple[0], tuple[1]);
					}
				}
			}
		}
        session.save();
	}
%>
