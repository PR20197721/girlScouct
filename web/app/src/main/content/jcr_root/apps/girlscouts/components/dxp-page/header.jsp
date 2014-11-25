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
<div
	class="header-wrapper row collapse hide-for-print update-list-header">
	<div class='columns'>
		<div id="header" class="row">
			<div class="large-6 medium-9 columns">
				<cq:include path="<%= headerPath + "/logo" %>"
					resourceType="girlscouts/components/logo" />
				<cq:include path="<%= headerPath + "/placeholder" %>"
					resourceType="girlscouts/components/placeholder" />
			</div>
		</div>
	</div>
	<!--PAGE STRUCTURE: GLOBAL NAV-->
	<div class="header-wrapper row collapse hide-for-print">
		<div class='columns'>
			<!--PAGE STRUCTURE: HEADER BAR-->
			<div id="headerBar" class="row collapse hide-for-small">
				<% setCssClasses("large-push-5 large-19 medium-23 small-24 columns", request); %>
				<cq:include path="<%= headerPath + "/global-nav" %>"
					resourceType="girlscouts/components/global-navigation" />
				<div class="small-search-hamburger show-for-medium medium-1 columns">
					<a class="show-for-medium right-off-canvas-toggle menu-icon"><img
						src="/etc/designs/girlscouts-usa-green/images/hamburger.png"
						width="19" height="28" alt="side menu icon"></a>
				</div>
			</div>
		</div>
	</div>
</div>

<!--[if gt IE 8]><!-->
<!-- SMALL SCREEN CANVAS should be after the global navigation is loaded,since global navigation won't be authorable-->

<!--<![endif]-->