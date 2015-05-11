<%@include file="/libs/foundation/global.jsp" %>
<!-- header -->
<%
    // All pages share the same header from the site root.
    String headerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header";
%>
<div>/apps/gsusa/components/page/header.jsp</div>
<cq:include path="<%= headerPath %>" resourceType="foundation/components/parsys" />
<!-- END of header -->