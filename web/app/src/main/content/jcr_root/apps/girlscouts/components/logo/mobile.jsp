<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp" %>

<%
String logoPath = homepage.getContentResource().getPath() + "/header";
Resource logo = resourceResolver.resolve(logoPath+"/logo");
if(logo != null){
	ValueMap logoProps = logo.getValueMap();
	String regularWidth = logoProps.get("regular/width", "188");
	String regularHeight = logoProps.get("regular/height", "73");
	String regularImage = logoProps.get("regular/fileReference", "");
	String alt = logoProps.get("alt", "");
	if (!alt.isEmpty()) alt = " alt=\"" + alt + "\"";
	String linkURL = logoProps.get("linkURL", "");
	if (!linkURL.isEmpty()) linkURL += ".html";

	String smallWidth = logoProps.get("small/width", "38");
	String smallHeight = logoProps.get("small/height", "38");
	String smallImage = logoProps.get("small/fileReference", "");
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
<%
}
%>