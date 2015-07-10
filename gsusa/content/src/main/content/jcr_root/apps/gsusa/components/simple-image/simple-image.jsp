<%@page session="false"%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@include file="/apps/girlscouts/components/global.jsp"%><%
	String rendition = displayRendition(resourceResolver, resource.getPath(), "cq5dam.web.1280.1280.jpeg");
	if (rendition == null || "".equals(rendition)) {
%><p>Please click here and add image</p><%
	} else {
%><%= rendition %><%
	}
%>
