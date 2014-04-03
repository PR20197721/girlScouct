<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/include-options.jsp"%>
<%
	String headerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header";
%>
<div class="off-canvas-wrap">
	<div class="inner-wrap">
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
		<div class="large-20 medium-19 small-24 columns">
			<div class="row">
				<% setCssClasses("large-17 medium-17 hide-for-small columns toplinks", request); %>
				<cq:include path="<%= headerPath + "/eyebrow-nav" %>" resourceType="girlscouts/components/eyebrow-navigation" />
				<% setCssClasses("large-7 medium-7 small-24 columns searchBar", request); %>
				<cq:include path="<%= headerPath + "/search-box" %>" resourceType="girlscouts/components/search-box" />
				<div class="row">
					<div class="message large-24 medium-24 small-24 columns">
						<span>Hello Sandy.</span> <a href="/signout" class="signout">SIGN OUT</a>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!--PAGE STRUCTURE: HEADER BAR-->
	<div id="headerBar" class="row">
		<div class="large-4 medium-5 hide-for-small columns">&nbsp;</div>
		<% setCssClasses("large-20 medium-19 hide-for-small columns", request); %>
		<cq:include path="<%= headerPath + "/global-nav" %>" resourceType="girlscouts/components/global-navigation" />
	</div>