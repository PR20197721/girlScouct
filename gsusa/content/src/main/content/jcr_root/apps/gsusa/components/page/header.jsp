<%@include file="/libs/foundation/global.jsp" %>
<!-- header -->
<%
    // All pages share the same header from the site root.
    String headerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header";
    String logoPath = headerPath + "/logo";
    String headerNavPath = headerPath + "/header-nav";
%>
<cq:include path="<%= logoPath %>" resourceType="girlscouts/compoentns/logo" />
<cq:include path="<%= headerNavPath %>" resourceType="gsusa/components/header-nav" />
<!-- END of header -->