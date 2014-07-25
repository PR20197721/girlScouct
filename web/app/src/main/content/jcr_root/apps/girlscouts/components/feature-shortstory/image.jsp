<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<!-- apps/girlscouts/components/feature-shortstory/image.jsp -->
<%
	String fileReference = properties.get("./image/fileReference", "");
	
%>
<div class="small-24 medium-12 large-12 columns imageContainer">
	<% if(fileReference != null && fileReference.length() > 0) { %>
	   <%= displayRendition(resourceResolver, fileReference, "cq5dam.web.400.400", null, 500) %>
	<% } %>
</div>
