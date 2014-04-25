<%@include file="/libs/foundation/global.jsp" %>
<%
	String regularWidth = properties.get("regular/width", "188");
	String regularHeight = properties.get("regular/height", "73");
	String regularImage = properties.get("regular/fileReference", "");
	String alt = properties.get("alt", "");
	if (!alt.isEmpty()) alt = " alt=\"" + alt + "\"";
	String linkURL = properties.get("linkURL", "");
	if (!linkURL.isEmpty()) linkURL += ".html";

	String smallWidth = properties.get("small/width", "293");
	String smallHeight = properties.get("small/height", "51");
	String smallImage = properties.get("small/fileReference", "");
	
	String hamburgerWidth = properties.get("hamburger/width", "22");
	String hamburgerHeight = properties.get("hamburger/height", "28");
	String hamburgerImage = properties.get("hamburger/fileReference", "");
%>

<!-- Artifact Browser -->
<!--[if lt IE 9]>
	<nav class="logoLarge">
		<img src="<%= regularImage %>"<%= alt %> width="<%= regularWidth %>" height="<%= regularHeight %>" />
	</nav>
<![endif]-->
<!-- Modern Browser -->
<!--[if gt IE 8]><!-->
<nav class="hide-for-small logoLarge">
	<% if (!linkURL.isEmpty()) { %> <a href="<%= linkURL %>"> <% } %>
		<img src="<%= regularImage %>"<%= alt %> width="<%= regularWidth %>" height="<%= regularHeight %>" />
	<% if (!linkURL.isEmpty()) { %> </a> <% } %>
</nav>
<nav class="show-for-small logoSmall">
	<center>
		<% if (!linkURL.isEmpty()) { %> <a href="<%= linkURL %>"> <% } %>
			<img src="<%= smallImage %>"<%= alt %> width="<%= smallWidth %>" height="<%= smallHeight %>" />
		<% if (!linkURL.isEmpty()) { %> </a> <% } %>
		<a class="right-off-canvas-toggle menu-icon debug">
			<img src="<%= hamburgerImage %>" width="<%= hamburgerWidth %>" height="<%= hamburgerHeight %>" />
		</a>
	</center>
</nav>
<!--<![endif]-->