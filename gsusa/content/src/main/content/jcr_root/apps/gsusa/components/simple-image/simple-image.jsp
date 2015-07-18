<%@page session="false"%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@include file="/apps/girlscouts/components/global.jsp"%><%
	String resourcePath = resource.getPath();
    String CONTENT_MATCH = "jcr:content/content/";
    String imageStem = resourcePath.substring(resourcePath.indexOf(CONTENT_MATCH) + CONTENT_MATCH.length());
    String imageVar = imageStem.substring(0, imageStem.indexOf("/"));
	String rendition = displayRendition(resourceResolver, resource.getPath(), "cq5dam.resized.web-" + imageVar + ".png");
	if (rendition.indexOf("original") != -1) {
		rendition = displayRendition(resourceResolver, resource.getPath(), "cq5dam.web.1280.1280.jpeg");
	}
if (rendition == null || "".equals(rendition)) {
%><p>Please click here and add image</p><%
	} else {
%><%= rendition %><%
	}
%>

