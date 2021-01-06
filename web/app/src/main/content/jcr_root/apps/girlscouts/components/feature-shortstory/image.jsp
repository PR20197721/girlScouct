<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<!-- apps/girlscouts/components/feature-shortstory/image.jsp -->
<%
	String fileReference = properties.get("./image/fileReference", "");
	
%>
<% if(fileReference != null && fileReference.length() > 0) { 
    String alt = properties.get("./alt", "");
    String title = properties.get("./imgtitle", "");
%>

<img alt="image <%= alt%> <%= title%>" width="500" src="<%= fileReference%>">

   <!-- <%= displayRendition(resourceResolver, fileReference, "cq5dam.web.400.400", null, 500,alt,title) %> --> <!-- GSAWDO-79-As discussed in web weekly call, removing rendition for this component only. -->
  <% } %>
