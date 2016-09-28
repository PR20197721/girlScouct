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
<div class="header-wrapper row collapse hide-for-print update-list-header">
	<div class='columns'>
		<div id="header" class="row">
            <!-- logo -->
			<div class="large-6 medium-9 columns">
				<cq:include path="<%= headerPath + "/logo" %>"
					resourceType="girlscouts/components/logo" />
				<cq:include path="<%= headerPath + "/placeholder" %>"
					resourceType="girlscouts/components/placeholder" />
			</div>
            <!--eyebrow-->
            <div class="large-18 medium-15 hide-for-small columns topMessage">
      			<cq:include path="<%= headerPath + "/eyebrow-nav" %>" resourceType="girlscouts/components/eyebrow-navigation" />
            </div>
            <!--top nav small window (mobile view) hamburger-->
            <div class="show-for-small small-24 columns topMessage alt">
      			<div class="row collapse">
                    <div class="columns"></div>
        			<div class="small-12 columns">
         			 <div class="small-search-hamburger">
            			<a class="right-off-canvas-toggle menu-icon"><img src="/etc/designs/girlscouts/images/hamburger.png" width="22" height="28" alt="toggle hamburger side menu icon"/></a>
          			 </div>
        			</div>
      			</div>
    		</div>
		</div>
	</div>
	<div class="header-wrapper row collapse hide-for-print">
		<div class='columns'>
			<!--PAGE STRUCTURE: HEADER BAR-->
			<div id="headerBar" class="row collapse hide-for-small">
				<% setCssClasses("medium-23 small-24 columns", request); %>
				<cq:include path="<%= headerPath + "/global-nav" %>"
					resourceType="girlscouts/components/global-navigation" />
                <!-- top nav medium window hamburger -->
				<div class="small-search-hamburger show-for-medium medium-1 columns">
					<a class="show-for-medium right-off-canvas-toggle menu-icon"><img
						src="/etc/designs/girlscouts/images/hamburger.png"
						width="19" height="28" alt="side menu icon"></a>
				</div>
			</div>
		</div>
	</div>
</div>

<!--[if gt IE 8]><!-->
<!-- SMALL SCREEN CANVAS should be after the global navigation is loaded,since global navigation won't be authorable-->
  <cq:include script="small-screen-menus"/>
<!--<![endif]-->