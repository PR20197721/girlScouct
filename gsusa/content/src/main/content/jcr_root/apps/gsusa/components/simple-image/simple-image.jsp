<%@page session="false"%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@include file="/apps/girlscouts/components/global.jsp"%><%
	String resourcePath = resource.getPath();
	String CONTENT_MATCH = "jcr:content/content/";
	String imageBase = resourcePath.substring(0, resourcePath.indexOf(CONTENT_MATCH) + CONTENT_MATCH.length());
	String imageStem = resourcePath.substring(resourcePath.indexOf(CONTENT_MATCH) + CONTENT_MATCH.length());
	String imageVar = imageStem.substring(0, imageStem.indexOf("/"));
	if ("middle".equals(imageVar)) {
		boolean noRightColumn = resourceResolver.resolve(imageBase + "right").getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING);
		boolean noLeftColumn = resourceResolver.resolve(imageBase + "left").getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING);
		if (noRightColumn && noLeftColumn) {
			imageVar = "top";
		} else if (noRightColumn) {
			imageVar = "hero";
		}
	}

	String rendition = displayRendition(resourceResolver, resource.getPath(), "cq5dam.npd." + imageVar + ".jpeg");
	if (rendition.indexOf("original") != -1) {
		rendition = displayRendition(resourceResolver, resource.getPath(), "cq5dam.web.1280.1280.jpeg");
	}
if (rendition == null || "".equals(rendition)) {
%><p>Please click here and add image</p><%
	} else {
%><%= rendition %><%
	}
%>
