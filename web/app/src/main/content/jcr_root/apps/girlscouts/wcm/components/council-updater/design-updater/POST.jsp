<%@ page

import="org.girlscouts.web.councilupdate.CouncilDesignUpdater,
com.day.cq.wcm.api.Page,
com.day.cq.tagging.Tag,
java.util.ArrayList,
java.util.Iterator"%>

<%@include file="/libs/foundation/global.jsp"%>

<%
String action = request.getParameter("action");
final String contentPath = "/content";

if (action == null || action.isEmpty()) {
    %>Action is null. Abort<%
    return;
}

if (action.equals("update")) {

    String councilName = request.getParameter("councilName");
    if (councilName == null || councilName.isEmpty()) {
        %>HomePage Name is empty. Abort<%
        return;
    }

Session session = (Session) resourceResolver.adaptTo(Session.class);
Node contentNode = session.getNode(contentPath);

//Checks if the council node has already been created. if it has not been, then abort process.
    if (!contentNode.hasNode(councilName)) {   
        %>
        <cq:include script="form.jsp" />
        This Council Does Not Exist. Abort.
        <% 
    } 
    else {
        //Create a service that will generate the Pages, Scaffolding, Assets, Tags and Groups
        CouncilDesignUpdater updater = sling.getService(CouncilDesignUpdater.class);
		String resultMessage = "";
        try{
        	resultMessage = updater.mergeCouncilDesign(session, contentPath, councilName);
        } catch(RuntimeException e){
			resultMessage = e.toString();
        }

        %> <p><%=resultMessage%></p> <%




	}

	session.save();
}

%>