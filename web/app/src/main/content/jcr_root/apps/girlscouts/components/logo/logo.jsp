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
<!-- Artifact Browser -->
<!--[if lt IE 9]>
	<nav class="logoLarge">
		<img src="<%= regularImage %>"<%= alt %>/>
	</nav>
<![endif]-->
<!-- Modern Browser -->
<!--[if gt IE 8]><!-->
<% if(noLink != null && noLink == true){
    %>
<nav class="show-for-small mobileFooterLogo">
	<img src="<%= regularImage %>"<%= alt %> width="<%= regularWidth %>" height="<%= regularHeight %>" />
</nav>
<% } else {
%>
<nav class="hide-for-small logoLarge logoLargePadding">
<% if (!linkURL.isEmpty()) { %> <a href="<%= linkURL %>"> <% } %>
	<img src="<%= regularImage %>"<%= alt %> id="logoImg" />
<% if (!linkURL.isEmpty()) { %> </a> <% } %>
</nav>
<nav class="show-for-small logoSmall">
<% if (!linkURL.isEmpty()) { %> <a href="<%= linkURL %>"> <% } %>
	<img src="<%= smallImage %>"<%= alt %> width="<%= smallWidth %>" height="<%= smallHeight %>" />
<% if (!linkURL.isEmpty()) { %> </a> <% } %>
</nav>
<% } %>
<!--<![endif]-->
