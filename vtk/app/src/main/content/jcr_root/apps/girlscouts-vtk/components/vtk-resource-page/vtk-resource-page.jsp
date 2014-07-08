<%@page import="java.util.HashMap,
				org.apache.sling.api.wrappers.ValueMapDecorator,
				org.apache.sling.api.resource.SyntheticResource,
                org.apache.sling.api.resource.ResourceMetadata" %>
<%@include file="/libs/foundation/global.jsp"%>

<%
String meetingPath = request.getParameter("meetingPath");
if (meetingPath != null) {
    String propPath = meetingPath + "/meetingInfo/overview/str";
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