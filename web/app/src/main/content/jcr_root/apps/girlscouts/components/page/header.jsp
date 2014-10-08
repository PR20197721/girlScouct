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
<div class="hide-for-print">
  <div id="header" class="row">
    <div class="large-5 medium-10 small-24 columns logoContainer">
      <cq:include path="<%= headerPath + "/logo" %>" resourceType="girlscouts/components/logo" />
      <%-- TODO: Mike Z. This is an empty <div> that fixes the green box on Chrome. Temp solution. --%>
      <cq:include path="<%= headerPath + "/placeholder" %>" resourceType="girlscouts/components/placeholder" />
    </div>
    <div class="large-19 medium-14 hide-for-small columns topMessage">
       <%setCssClasses("columns noLeftPadding" , request); %>
      <cq:include path="<%= headerPath + "/eyebrow-nav" %>" resourceType="girlscouts/components/eyebrow-navigation" />
      <div class="row">
        <% setCssClasses("large-18 medium-18 small-24 columns", request); %>
        <cq:include path="<%= headerPath + "/login" %>" resourceType="girlscouts/components/login" />
        <% setCssClasses("large-6 medium-6 small-24 columns searchBar", request); %>
        <cq:include path="<%= headerPath + "/search-box" %>" resourceType="girlscouts/components/search-box" />
      </div>
      <div class="emptyrow">&nbsp;</div>
    </div>
    <div class="show-for-small small-24 columns topMessage alt">
      <div class="row vtk-login">
        <% setCssClasses("small-12 columns", request); %>
        <cq:include path="<%= headerPath + "/login" %>" resourceType="girlscouts/components/login" />
        <div class="small-12 columns">
          <div class="small-search-hamburger">
              <a class="search-icon"><img src="<%= designPath %>/images/search_white.png" width="21" height="21" alt="search icon"/></a>
            <a class="right-off-canvas-toggle menu-icon"><img src="<%= designPath %>/images/hamburger.png" width="22" height="28" alt="toggle hamburger side menu icon"/></a>
          </div>
        </div>
      </div>
      <div class="row hide srch-box">
        <% setCssClasses("small-20 columns hide srch-box", request); %>
          <cq:include path="<%= headerPath + "/search-box" %>" resourceType="girlscouts/components/search-box" />
        <div class="small-4 columns">
          <a class="right-off-canvas-toggle menu-icon"><img src="<%= designPath %>/images/hamburger.png" width="22" height="28" alt="right side menu hamburger icon"/></a>
        </div>
      </div>
    </div>
  </div>
<!--PAGE STRUCTURE: HEADER BAR-->
  <div id="headerBar" class="row collapse hide-for-small">
    <% setCssClasses("large-push-5 large-19 medium-23 small-24 columns", request); %>
    <cq:include path="<%= headerPath + "/global-nav" %>" resourceType="girlscouts/components/global-navigation" />
    <div class="small-search-hamburger show-for-medium medium-1 columns">
      <a class="show-for-medium right-off-canvas-toggle menu-icon"><img src="/etc/designs/girlscouts-usa-green/images/hamburger.png" width="19" height="28" alt="side menu icon"></a>
    </div>
  </div>
</div>
<!--[if gt IE 8]><!-->
<!-- SMALL SCREEN CANVAS should be after the global navigation is loaded,since global navigation won't be authorable-->
  <cq:include script="small-screen-menus"/>
<!--<![endif]-->