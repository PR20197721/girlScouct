<%
	String regularWidth = properties.get("regularWidth", "188");
	String regularHeight = properties.get("regularHeight", "73");
	String regularImage = properties.get("regularFileReference", "");
	String regularAlt = properties.get("regularAlt", "");
	if (!regularAlt.isEmpty()) regularAlt = " alt=\"" + regularAlt + "\"";

	String smallWidth = properties.get("smallWidth", "293");
	String smallHeight = properties.get("smallHeight", "51");
	String smallImage = properties.get("smallFileReference", "");
	String smallAlt = properties.get("smallAlt", "");
	if (!smallAlt.isEmpty()) smallAlt = " alt=\"" + smallAlt + "\"";
	
	String hamburgerWidth = properties.get("hamburgerWidth", "22");
	String hamburgerHeight = properties.get("hamburgerHeight", "28");
	String hamburgerImage = properties.get("hamburgerFileReference", "");
%>

<div class="large-4 medium-5 small-24 columns">
	<!-- Artifact Browser -->
	<!--[if lt IE 9]>
		<nav class="logoLarge">
			<img src="images/gateway-logo.png" width="188" height="73"/>
		</nav>
	<![endif]-->
	<!-- Modern Browser -->
	<!--[if gt IE 8]><!-->
	<nav class="hide-for-small logoLarge">
		<img src="<%= regularImage %>"<%= regularAlt %> width="<%= regularWidth %>" height="<%= regularHeight %>" />
	</nav>
	<nav class="show-for-small logoSmall">
		<img src="<%= smallImage %>"<%= smallAlt %> width="<%= smallWidth %>" height="<%= smallHeight %>" />
		<a class="right-off-canvas-toggle menu-icon debug">
			<img src="<%= hamburgerImage %>" width="<%= hamburgerWidth %>" height="<%= hamburgerHeight %>" />
		</a>
	</nav>
	<!--<![endif]-->
</div>