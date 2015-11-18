<%@page import="java.util.Iterator" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode" %>
 
<%
Iterator<Page> iter = currentPage.listChildren();
if (!iter.hasNext()) {
    response.sendError(HttpServletResponse.SC_NOT_FOUND);
} else {
	boolean foundCookiesPage = false;
	int counter = 0;
	Page firstChild = null;
	while (iter.hasNext()) {
		Page child = iter.next();
		//if this is the first child, remember it and store it in first Child
		if (counter == 0) {
			firstChild = child;
			counter++;
		}
		Node childNode = child.adaptTo(Node.class);
        Node contentNode = childNode.getNode("jcr:content");
        if (contentNode.hasProperty("isCookiePage")) {
	        if ("true".equals(contentNode.getProperty("isCookiePage").getString())) {
	        	foundCookiesPage = true;
	        	break;
	        }
        }
	}
	
	System.out.println("What we have: " + currentPage.getPath());

	if (foundCookiesPage && (WCMMode.fromRequest(request) == WCMMode.EDIT)) {
		%>
		<html>
		  <head>
			<cq:include script="/libs/wcm/core/components/init/init.jsp"/>
		  </head>
		  <h3><u>Placeholder Page for the Cookie Finder</u></h3>
		  You see this page because you are in an authoring environment with a cookies page underneath this place-holder page<br>
		  In publish environment, this page will be redirected to the <u>first child of this page</u><br>
		  <br>
		  Please <u>activate</u> this page in the side-kick to activate the cookie page finder in mobile.<br>
		  <br>
		  If you have more questions, please contact your site admin.
		  <cq:include path="<%= currentPage.getContentResource().getPath() %>" resourceType="gsusa/components/cookie-header" />
		</html>
		 <%
	} else { 
	    String redirectUrl = firstChild.getPath() + ".html";
	    response.setStatus(301);
	    response.setHeader( "Location", redirectUrl);
	    response.setHeader( "Connection", "close" );
	}
}
%>