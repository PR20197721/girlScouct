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
						<%-- TODO: Mike Z. This is an empty <div> that fixes the green box on Chrome. Temp solution. --%>
						<cq:include path="<%= headerPath + "/placeholder" %>" resourceType="girlscouts/components/placeholder" />
					</div>
					<div class="large-19 medium-19 hide-for-small columns topMessage">
						 <%setCssClasses("columns noLeftPadding" , request); %>
						<cq:include path="<%= headerPath + "/eyebrow-nav" %>" resourceType="girlscouts/components/eyebrow-navigation" />
						<div class="row">
							<% setCssClasses("large-18 medium-18 columns", request); %>
							<cq:include path="<%= headerPath + "/login" %>" resourceType="girlscouts/components/login" />
							<% setCssClasses("large-6 medium-6 small-24 columns searchBar", request); %>
							<cq:include path="<%= headerPath + "/search-box" %>" resourceType="girlscouts/components/search-box" />
						</div>
						<div class="row emptyrow"></div>
					</div>
					<div class="show-for-small small-24 columns topMessage alt">
						<div class="row vtk-login">
							<% setCssClasses("small-18 columns", request); %>
							<cq:include path="<%= headerPath + "/login" %>" resourceType="girlscouts/components/login" />
							<div class="small-6 columns">
							   	<a class="search-icon"><img src="<%= designPath %>/images/magnifyer-small.png" width="21" height="21"/></a>
								<a class="right-off-canvas-toggle menu-icon"><img src="<%= designPath %>/images/hamburger.png" width="22" height="28"/></a>
							</div>
						</div>
						<div class="row hide srch-box">
						    <%setCssClasses("small-6 columns", request); %>
						    <cq:include path="<%= headerPath + "/login" %>" resourceType="girlscouts/components/login" />
							<% setCssClasses("small-16 columns", request); %>
								<cq:include path="<%= headerPath + "/search-box" %>" resourceType="girlscouts/components/search-box" />
							<div class="small-2 columns">
								<a class="right-off-canvas-toggle menu-icon"><img src="<%= designPath %>/images/hamburger.png" width="22" height="28"/></a>
							</div>
						</div>
						
						
						
					</div>
				</div>
<!--PAGE STRUCTURE: HEADER BAR-->
<div id="headerBar" class="row">
				<div class="large-5 medium-5 hide-for-small columns">&nbsp;</div>
					<% setCssClasses("large-19 medium-19 small-24 columns", request); %>
					<cq:include path="<%= headerPath + "/global-nav" %>" resourceType="girlscouts/components/global-navigation" />
				</div>

<!-- SMALL SCREEN CANVAS should be after the global navigation is loaded,since global navigation won't be authorable-->
				<cq:include script="small-screen-menus"/>
	
