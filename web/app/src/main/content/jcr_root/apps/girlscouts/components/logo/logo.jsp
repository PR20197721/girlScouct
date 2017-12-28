<%@include file="/libs/foundation/global.jsp" %>
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
	<% if (!linkURL.isEmpty()) { %> <a href="<%= linkURL %>"> <% } %>
		<img src="<%= regularImage %>"<%= alt %> id="logoImg" width="<%= regularWidth %>" height="<%= regularHeight%>" />
	<% if (!linkURL.isEmpty()) { %> </a> <% } %>
	</nav>
	<% } %>
	<!--<![endif]-->
</div>