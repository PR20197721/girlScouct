<%@include file="/libs/foundation/global.jsp"%>

<%@ page import="com.day.cq.wcm.api.Page" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Collection" %>
<%@ page import="org.girlscouts.gsactivities.dataimport.EventsImport" %>

<%
String action = request.getParameter("action");

if (action == null || action.isEmpty()) {
    %>Action is null. Abort<%
    return;
}

if (action.equals("trigger")) {
	Session session = (Session) resourceResolver.adaptTo(Session.class);

    %><br>FTP events are being pulled in...<br><%
    EventsImport eis = sling.getService(EventsImport.class);
    eis.run();

	session.save();
}

%>