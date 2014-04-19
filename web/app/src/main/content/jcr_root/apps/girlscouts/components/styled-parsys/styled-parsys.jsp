<%@page import="org.apache.sling.api.request.RequestDispatcherOptions,
                    com.day.cq.wcm.api.components.IncludeOptions,
                    com.day.cq.wcm.api.WCMMode,
                    org.apache.sling.api.resource.Resource,
                    java.util.Set,
                    java.util.Arrays" %><%
%><%@include file="/libs/foundation/global.jsp"%><%

String cssClassesStr = properties.get("cssClasses", "");
if (!cssClassesStr.isEmpty()) {
	IncludeOptions.clear(slingRequest);
	IncludeOptions opts = IncludeOptions.getOptions(slingRequest, true);

	Set<String> cssClasses = opts.getCssClassNames();
	cssClasses.addAll(Arrays.asList(cssClassesStr.split(" ")));

	opts.setDecorationTagName("div");
	opts.forceCellName("styled-parsys-inner");
	opts.forceEditContext(true);
}

RequestDispatcherOptions reqOpts = new RequestDispatcherOptions();
reqOpts.setForceResourceType("girlscouts/components/styled-parsys/styled-parsys-inner");

slingRequest.getRequestDispatcher(resource, reqOpts).include(slingRequest, slingResponse);
%>