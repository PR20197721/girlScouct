<%@page import="java.util.Iterator" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:include script="/libs/wcm/core/components/init/init.jsp"/>
<p align="center" style="color:white">EDIT THIS CONTACT THROUGH SCAFFOLDING</p>
<%
    Iterator<Page> iter = currentPage.listChildren();
if (!iter.hasNext()) {
    response.sendError(HttpServletResponse.SC_NOT_FOUND);
} else {
    Page firstChild = iter.next();
    String redirectUrl = firstChild.getPath() + ".html";
    response.setStatus(301);
    response.setHeader( "Location", redirectUrl);
    response.setHeader( "Connection", "close" );
}
%>