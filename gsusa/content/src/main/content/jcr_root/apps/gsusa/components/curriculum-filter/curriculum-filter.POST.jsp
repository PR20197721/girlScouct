<%@include file="/libs/foundation/global.jsp"%>
<%@ page import="com.day.cq.commons.TidyJSONWriter" %>

<%
String path = request.getParameter("path");

final TidyJSONWriter writer = new TidyJSONWriter(response.getWriter());
String output = "found";
if(resourceResolver.resolve(path).getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
	output = "not found";
}

writer.setTidy("true".equals(request.getParameter("tidy")));
writer.object();
writer.key("key").value(output);
writer.endObject();
		
%>