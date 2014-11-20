<%@ page import="org.girlscouts.web.councilrollout.CouncilCreator,
    com.day.cq.wcm.api.PageManager" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:include script="form.jsp"/>
<%
CouncilCreator creator = sling.getService(CouncilCreator.class);
creator.create();
String councilTitle = request.getParameter("councilTitle");
String councilName = request.getParameter("councilName");
String councilPath = "/content";

Session session = (Session) resourceResolver.adaptTo(Session.class);
Node contentNode = session.getNode("/content");

//Checks if the council node has already been created. if it has, then abort process.
if(contentNode.hasNode("hanke")){ %>
Council Already Exists. Abort.
<% } else{


}
%>
