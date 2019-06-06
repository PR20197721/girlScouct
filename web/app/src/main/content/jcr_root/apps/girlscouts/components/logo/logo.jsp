<%@include file="/libs/foundation/global.jsp" %>
<%@taglib prefix="ui" uri="http://www.adobe.com/taglibs/granite/ui/1.0" %>
<ui:includeClientLib categories="apps.girlscouts.components.logo" />
<%
	String regularWidth = properties.get("regular/width", "188");
	String regularHeight = properties.get("regular/height", "73");
	String regularImage = properties.get("regular/fileReference", "");
	String alt = properties.get("alt", "");
	if (!alt.isEmpty()) alt = " alt=\"" + alt + "\"";
	String linkURL = properties.get("linkURL", "");
	if (!linkURL.isEmpty()) linkURL += ".html";

	String smallWidth = properties.get("small/width", "38");
	String smallHeight = properties.get("small/height", "38");
	String smallImage = properties.get("small/fileReference", "");
	String path = currentPage.getPath();
    boolean isVtk = path.toLowerCase().contains("vtk");
	Boolean noLink = (Boolean) request.getAttribute("noLink");
//String linkAttribute = (String) request.getAttribute("links");
%>
<div style="overflow:auto">
	<!-- Artifact Browser -->
	<!--[if lt IE 9]>
		<nav class="logoLarge">
			<img src="<%= regularImage %>"<%= alt %>/>
		</nav>
	<![endif]-->
	<!-- Modern Browser -->
	<!--[if gt IE 8]><!-->
	<% if(noLink != null && noLink == true){
	// this shows for footer for mobile view only
	%>
	<nav class="small-centered columns small-5">
		<img src="<%= smallImage %>"<%= alt %> width="<%= smallWidth %>" height="<%= smallHeight %>" />
	</nav>
	<% } else {
	// this shows for header large only
	%>
	<nav class="column large-24 medium-24">
	<% if (!isVtk) { %>
	<span id="menuIcon"class="mobileIcons">
        <a class="side-nav-toggle menu-icon"><img src="/etc/designs/girlscouts/images/hamburger.png" width="22" height="28" alt="right side menu hamburger icon"/></a>
	</span>
	<% }if (!linkURL.isEmpty()) { %> <a href="<%= linkURL %>"> <% } %>
		<img style="display: inline-block;" src="<%= regularImage %>"<%= alt %> id="logoImg" width="<%= regularWidth %>" height="<%= regularHeight%>" />
	<% if (!linkURL.isEmpty()) { %> </a> <% } %>
	<% if (!isVtk) { %>
	<span id="searchIcon" class="mobileIcons" searchShown="false">
	    <a class="mobile-search-icon"><img src="/etc/designs/girlscouts/images/search_white.png" width="21" height="21" alt="search icon"/></a>
	</span>
	<% } %>
	</nav>
	<% } %>
	<!--<![endif]-->
</div>