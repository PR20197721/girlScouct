<%@page import="java.util.Iterator" %>
<%@include file="/libs/foundation/global.jsp" %>

<%
Iterator<Page> iter = currentPage.listChildren();
if (!iter.hasNext()) {
    response.sendError(HttpServletResponse.SC_NOT_FOUND);
} else {
    Page firstChild = currentPage.listChildren().next();
    response.sendRedirect(firstChild.getPath() + ".html");
}
%>