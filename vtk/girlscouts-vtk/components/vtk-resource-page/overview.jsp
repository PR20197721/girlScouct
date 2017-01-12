<%@page import="java.util.HashMap,
				com.day.cq.commons.PathInfo,
				org.apache.sling.api.wrappers.ValueMapDecorator,
				org.apache.sling.api.resource.SyntheticResource,
                org.apache.sling.api.resource.ResourceMetadata" %>
<%@include file="/libs/foundation/global.jsp"%>
<%
PathInfo pathInfo = new PathInfo(request.getPathInfo());
String suffix = pathInfo.getSuffix().substring(1);
String meetingPath = null;
try {
    meetingPath = suffix.substring(suffix.indexOf('/'));
} catch (Exception e) {
   	log.error("Cannot get meeting path."); 
}
if (meetingPath != null) {
    String propPath = meetingPath + "/name"; 
    Property nameProp = (Property)resourceResolver.resolve(propPath).adaptTo(Property.class);
    if (nameProp != null) {
        String meetingTitle = nameProp.getString();
        %><h1 class="resourceTitle"><%= meetingTitle %></h1><%
        out.flush();
    }

    propPath = meetingPath + "/meetingInfo/overview/str";
    Property overviewProp = (Property)resourceResolver.resolve(propPath).adaptTo(Property.class);
    if (overviewProp != null) {
        final String overview = overviewProp.getString();
		ResourceMetadata metadata = new ResourceMetadata();
		String resolutionPath = "/content/girlscouts-vtk/resources/overviews/" + meetingPath;
		metadata.setResolutionPath(resolutionPath);

		Resource overviewResource = new SyntheticResource(resourceResolver, metadata, "girlscouts/components/text") {
		    public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
		    	if (type == ValueMap.class) {
		    	    ValueMap map = new ValueMapDecorator(new HashMap<String, Object>());
					map.put("text", overview);
					map.put("textIsRich", true);
					return (AdapterType)map;
		    	}
		    	return null;
		    }
		};
		slingRequest.getRequestDispatcher(overviewResource).include(slingRequest, slingResponse);
    }
}
%>