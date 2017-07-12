<%@page import="java.util.Iterator" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>

<%@page import="com.day.cq.wcm.api.WCMMode" %>
 
<%
Iterator<Page> iter = currentPage.listChildren();
if (!iter.hasNext()) {
    response.sendError(HttpServletResponse.SC_NOT_FOUND);
} else {
	//we will try to scan all the children underneath this folder
	//if we found a cookies page, we will need to show the placeholder page
	/* boolean foundCookiesPage = false;
	String cookiePageTitle = "";
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
	        	cookiePageTitle = contentNode.getProperty("articleTitle").getString();
	        	break;
	        }
        }
	} */
	
	//if there are no children underneath this placeholder page, set it to the currentPage
	/* if (firstChild == null) {
		firstChild = currentPage;
	} */
	Page firstChild = iter.next();
	if (isCookiePage(currentPage) && (WCMMode.fromRequest(request) == WCMMode.EDIT)) {
		%>
		<html>
		  <head>
			<cq:include script="/libs/wcm/core/components/init/init.jsp"/>
		  </head>
		  <h2><u>Placeholder Page for the Cookie Finder</u></h2>
		  This is a place-holder for the cookie page in the authoring environment.<br>
		  On the publish environment, you will be redirected to the <a href=<%= firstChild.getPath() + ".html"%>>first child of this page</a>.<br>
		  <br>
		  Please <u>activate</u> this page in the side-kick to make components available through the cookies site.<br>
		  <%-- <div class="header">
		  	<div class="cookie-header">
		  	  <div class="standalone-cookie-header mobile-cookie-header">
		 	    <cq:include path="<%= currentPage.getContentResource().getPath() %>" resourceType="gsusa/components/cookie-header" />
		 	  </div>
		  	</div>
		  </div> --%>
		</html>
		 <%
	} else { 
	    String redirectUrl = firstChild.getPath() + ".html";
	    redirectUrl = resourceResolver.map(redirectUrl);
	    response.setStatus(301);
	    response.setHeader( "Location", redirectUrl);
	    response.setHeader( "Connection", "close" );
	}
}
%>