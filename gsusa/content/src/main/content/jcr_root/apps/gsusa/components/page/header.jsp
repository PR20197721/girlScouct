<%@include file="/libs/foundation/global.jsp" %>
<!-- header -->
<%
    // All pages share the same header from the site root.
    String headerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header";
    String headerNavPath = headerPath + "/header-nav";
%>
<div>/apps/gsusa/components/page/header.jsp</div>
<cq:include path="<%= headerNavPath %>" resourceType="gsusa/components/header-nav" />
<!-- END of header -->