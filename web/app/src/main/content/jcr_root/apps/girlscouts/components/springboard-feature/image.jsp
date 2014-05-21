<%@include file="/libs/foundation/global.jsp"%>
<!-- apps/girlscouts/components/feature-shortstory/image.jsp -->
<%
	String fileReference = properties.get("fileReference", "");
	String imgWidth = properties.get("width", "800");
	imgWidth = "width=\"" + imgWidth + "\"";
	String imgHeight = properties.get("height", "");
	if (!imgHeight.isEmpty()) imgHeight= "height=\"" + imgHeight + "\"";
	String imgAlt = properties.get("alt", "");
	if (!imgAlt.isEmpty()) imgAlt = "alt=\"" + imgAlt + "\"";
%>
<div class="small-24 medium-12 large-12 columns">
	<% if(fileReference != null && fileReference.length() > 0) { %>
	   <img src="<%=fileReference%>" <%= imgAlt %> <%= imgWidth %> <%= imgHeight %>/>
	<% } %>
</div>
