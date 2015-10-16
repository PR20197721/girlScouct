<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
<!-- footer -->
<%
    // All cookie pages share the same cookie footer from the site root.
    String footerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/footer";
    String cookieFooterPath = footerPath + "/cookieFooter";
%>
<% if (isCookiePage(currentPage)) { %>
	<cq:include path="<%= cookieFooterPath %>" resourceType="gsusa/components/cookie-footer" />
<% } %>