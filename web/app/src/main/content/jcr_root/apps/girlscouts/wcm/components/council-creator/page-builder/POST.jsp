<%@ page
	import="org.girlscouts.web.councilrollout.CouncilCreator,
    com.day.cq.security.Group,
	com.day.cq.wcm.api.Page,
	com.day.cq.tagging.Tag,
    java.util.ArrayList,
    java.util.Iterator
    "%>
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
%><br>PAGES:<br><%
    //UserManager manager = (UserManager) resourceResolver.adaptTo(UserManager.class);
    //		manager.createGroup("test", "test", "/home/groups/girlscouts-usa");
    //     Authorizable b = (Authorizable) resourceResolver.adaptTo(Authorizable.class);
    //Authorizable a = manager.getAuthorizable("administrators");

    CouncilCreator creator = sling.getService(CouncilCreator.class);
    ArrayList<Group> groupList = creator.generateGroups(session, resourceResolver, councilName, councilTitle);
    for(Group g : groupList){ 
%>"<%= g.getName() %>" group created under path:
<%= g.getPath() %>
<br>
<%
   }
%>Create DAM folder structure:
<br>
<form action="assets.html">
    <input type="submit" value="Confirm">
</form>
<br><br>
Create tags:
<br>
<form action="tags.html" method="post">
    <input type="submit" value="Confirm">
</form><%
}
}
%>