<%@page import="org.apache.sling.api.resource.ResourceResolver" %>
<%@include file="/libs/foundation/global.jsp"%>
<%!
	final String TEMPLATE_ROOT = "/content/vtkcontent";
	ValueMap getTemplateResourceProperties(Resource resource, ResourceResolver rr) {
		try {
			if (resource.getPath().startsWith(TEMPLATE_ROOT)) {
				return ValueMap.EMPTY;
			}
			String templateResourcePath = resource.getPath().replaceAll("^/[^/]+/[^/]+/[^/]+", TEMPLATE_ROOT); 
			ValueMap vm = rr.resolve(templateResourcePath).adaptTo(ValueMap.class);
			if (vm == null) {
				return ValueMap.EMPTY;
			}
			return vm;
		} catch (Exception e) {
			return ValueMap.EMPTY;
		}
	}
	
	Node getTemplateResourceItems(Resource resource, ResourceResolver rr) {
		try {
			if (resource.getPath().startsWith(TEMPLATE_ROOT)) {
				return null;
			}
			String templateResourcePath = resource.getPath().replaceAll("^/[^/]+/[^/]+/[^/]+", TEMPLATE_ROOT); 
            Node globalResource = rr.resolve(templateResourcePath).adaptTo(Node.class);
            if(globalResource.hasNode("items")){
                Node items = globalResource.getNode("items");
                return items;
            }else{
                return null;
            }
		} catch (Exception e) {
			return null;
		}
	}
%>