<%@ page import="org.girlscouts.web.councilrollout.CouncilCreator" %>
<%@include file="/libs/foundation/global.jsp" %>
<%
String councilTitle = request.getParameter("councilTitle");
String councilName = request.getParameter("councilName");
String contentPath = "/content";

Session session = (Session) resourceResolver.adaptTo(Session.class);
Node contentNode = session.getNode("/content");

//Checks if the council node has already been created. if it has, then abort process.
if(contentNode.hasNode(councilName)){ %>
<cq:include script="form.jsp"/>

Council Already Exists. Abort.
<% } else{

	CouncilCreator creator = sling.getService(CouncilCreator.class);
	String something = creator.generateHomePage(session, resourceResolver, contentPath, councilName, councilTitle);
%>Council homepage created under path <%= something %><%
}
%>