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
   <%= displayRendition(resourceResolver, fileReference, "cq5dam.web.400.400", null, 500,alt,title) %>
  <% } %>
