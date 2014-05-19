<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<!-- apps/girlscouts/components/page/header.jsp -->
<%
	String headerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header";
	String designPath = currentDesign == null ? "/" : currentDesign.getPath();
%>
		<!-- Modern Browser -->
<!--[if gt IE 8]><!-->
		<aside class="right-off-canvas-menu">
			<ul class="off-canvas-list">
				<li><label class="first">Foundation</label></li>
				<li><a href="index.html">Home</a></li>
			</ul>
			<hr>
			<ul class="off-canvas-list">
				<li><label class="first">Learn</label></li>
				<li><a href="learn/features.html">Features</a></li>
				<li><a href="learn/faq.html">FAQ</a></li>
			</ul>
			<hr>
			<ul class="off-canvas-list">
				<li><label>Develop</label></li>
				<li><a href="templates.html">Add-ons</a></li>
				<li><a href="docs">Docs</a></li>
			</ul>
			<hr>
			<div class="zurb-links">
				<ul class="top">
					<li><a href="http://zurb.com/about">About</a></li>
					<li><a href="http://zurb.com/blog">Blog</a></li>
					<li><a href="http://zurb.com/contact">Contact</a></li>
				</ul>
			</div>
		</aside>
<!--<![endif]-->
		<!--PAGE STRUCTURE: HEADER-->
		<div id="header" class="row">
			<div class="large-4 medium-5 small-24 columns">
<cq:include path="<%= headerPath + "/logo" %>" resourceType="girlscouts/components/logo" />
			</div>    
			<div class="large-20 medium-19 hide-for-small columns topMessage">
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
<% setCssClasses("large-24 medium-24 hide-for-small columns", request); %>
<cq:include path="<%= headerPath + "/global-nav" %>" resourceType="girlscouts/components/global-navigation" />
		</div>
