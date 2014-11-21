<%@ page
	import="org.girlscouts.web.councilrollout.CouncilCreator,
    java.util.ArrayList"%>
<%@include file="/libs/foundation/global.jsp"%>
<%
String action = request.getParameter("action");
final String contentPath = "/content";
if (action == null || action.isEmpty()) {
    %>Action is null. Abort<%
    return;
}
if (action.equals("create")) {
	String councilTitle = request.getParameter("councilTitle");
	if (councilTitle == null || councilTitle.isEmpty()) {
	    %>HomePage Title is empty. Abort.<%
	    return;
	}
	String councilName = request.getParameter("councilName");
	if (councilName == null || councilName.isEmpty()) {
	    %>HomePage Name is empty. Abort<%
	    return;
	}

Session session = (Session) resourceResolver.adaptTo(Session.class);
Node contentNode = session.getNode(contentPath);

//Checks if the council node has already been created. if it has, then abort process.
if(contentNode.hasNode(councilName)){ %>
<cq:include script="form.jsp" />

Council Already Exists. Abort.
<% } else{

	CouncilCreator creator = sling.getService(CouncilCreator.class);
	ArrayList pathList = creator.generateHomePage(session, resourceResolver, contentPath, councilName, councilTitle);
%>Council homepage created under path
<%= pathList.get(0) %>
<br>
Council English page created under path
<%= pathList.get(1) %>
<%
}
}
%>