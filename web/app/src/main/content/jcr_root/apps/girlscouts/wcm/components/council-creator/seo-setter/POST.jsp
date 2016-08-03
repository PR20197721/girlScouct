<%@ page import="org.girlscouts.web.councilrollout.SEOSetter,
com.day.cq.wcm.api.Page,
com.day.cq.tagging.Tag,
java.util.ArrayList,
java.util.Iterator"%>

<%@include file="/libs/foundation/global.jsp"%>

<%
String action = request.getParameter("action");

if (action == null || action.isEmpty()) {
    %>Action is null. Abort<%
    return;
}

if (action.equals("set")) {
    String councilPath = request.getParameter("councilPath");
    if (councilPath == null || councilPath.isEmpty()) {
        %>HomePage Title is empty. Abort.<%
        return;
    }

	Session session = (Session) resourceResolver.adaptTo(Session.class);
	Node councilNode = session.getNode(councilPath);
	PageManager pm = (PageManager) resourceResolver.adaptTo(PageManager.class);
	Page councilPage = pm.getPage(councilPath);
	
	%> COUNCIL PATH = <%= councilNode.getPath() %> <br>
	Setting SEO Titles...<%
			
	SEOSetter setter = sling.getService(SEOSetter.class);
	String toDisplay = setter.setSEO(session, councilPage, councilPage.getTitle());
	%> <%= toDisplay %> <%
	session.save();
}

%>