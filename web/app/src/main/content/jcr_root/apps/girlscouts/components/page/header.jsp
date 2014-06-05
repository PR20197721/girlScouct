<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<!-- apps/girlscouts/components/page/header.jsp -->
<%
	String headerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header";
	String designPath = currentDesign == null ? "/" : currentDesign.getPath();
	int depth = currentPage.getDepth();
	request.setAttribute("headerPath", headerPath);
%>
		<!-- Modern Browser -->
<!--[if gt IE 8]><!-->

<!--<![endif]-->
		<!--PAGE STRUCTURE: HEADER-->
		<div id="header" class="row">
			<div class="large-5 medium-5 small-24 columns">
                <cq:include path="<%= headerPath + "/logo" %>" resourceType="girlscouts/components/logo" />
			</div>    
			<div class="large-19 medium-19 hide-for-small columns topMessage">
<% setCssClasses("columns", request); %>
<cq:include path="<%= headerPath + "/eyebrow-nav" %>" resourceType="girlscouts/components/eyebrow-navigation" />
				<div class="row">
<% setCssClasses("large-17 medium-17 columns", request); %>
<cq:include path="<%= headerPath + "/login" %>" resourceType="girlscouts/components/login" />
<% setCssClasses("large-7 medium-7 small-24 columns searchBar", request); %>
<cq:include path="<%= headerPath + "/search-box" %>" resourceType="girlscouts/components/search-box" />
				</div>
			</div>
			<div class="show-for-small small-24 columns topMessage alt">
				<div class="row">
<% setCssClasses("small-18 columns", request); %>
<cq:include path="<%= headerPath + "/login" %>" resourceType="girlscouts/components/login" />
					<div class="small-6 columns">
						<a class="right-off-canvas-toggle menu-icon"><img src="<%= designPath %>/images/magnifyer-small.png" width="21" height="21"/></a>
						<a class="right-off-canvas-toggle menu-icon"><img src="<%= designPath %>/images/hamburger.png" width="22" height="28"/></a>
					</div>
				</div>
			</div>
		</div>
<!--PAGE STRUCTURE: HEADER BAR-->
<div id="headerBar" class="row">
<% setCssClasses("large-24 medium-24 small-24 columns", request); %>
<cq:include path="<%= headerPath + "/global-nav" %>" resourceType="girlscouts/components/global-navigation" />
</div>
<cq:include script="small-screen-menus"/>
