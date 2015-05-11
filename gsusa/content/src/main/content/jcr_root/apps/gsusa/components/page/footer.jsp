<%@include file="/libs/foundation/global.jsp" %>
<!-- footer -->
<%
    // All pages share the same footer from the site root.
    String footerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/footer";
%>
footer
<cq:include path="<%= footerPath %>" resourceType="foundation/components/parsys" />
<!-- END of footer -->