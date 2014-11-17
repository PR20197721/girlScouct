<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<!-- apps/girlscouts/components/page/header.jsp -->
<%
  // Force currentPage and currentDesign from request
  Page newCurrentPage = (Page)request.getAttribute("newCurrentPage");
  Design newCurrentDesign= (Design)request.getAttribute("newCurrentDesign");
  if (newCurrentPage != null) {
      currentPage = newCurrentPage;
  }
  if (newCurrentDesign != null) {
      currentDesign = newCurrentDesign;
  }
  String headerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header";
  String designPath = currentDesign == null ? "/" : currentDesign.getPath();
  int depth = currentPage.getDepth();
  request.setAttribute("headerPath", headerPath);
%>
<!-- Modern Browser -->
<!--[if gt IE 8]><!-->
<!--<![endif]-->
<!--PAGE STRUCTURE: HEADER-->
<div class="header-wrapper row collapse hide-for-print">
<div class='columns'>
  <div id="header" class="row">
    <div class="large-6 medium-9 columns">
      <cq:include path="<%= headerPath + "/logo" %>" resourceType="girlscouts/components/logo" />
      <%-- TODO: Mike Z. This is an empty <div> that fixes the green box on Chrome. Temp solution. --%>
      <cq:include path="<%= headerPath + "/placeholder" %>" resourceType="girlscouts/components/placeholder" />
    </div>

    </div> 
  </div>
  <!--PAGE STRUCTURE: HEADER BAR-->
</div>
</div>
<!--[if gt IE 8]><!-->
<!-- SMALL SCREEN CANVAS should be after the global navigation is loaded,since global navigation won't be authorable-->

<!--<![endif]-->