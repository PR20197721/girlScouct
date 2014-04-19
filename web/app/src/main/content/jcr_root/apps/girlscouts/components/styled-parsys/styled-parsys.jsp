<%@page import="org.apache.sling.api.request.RequestDispatcherOptions,
                    com.day.cq.wcm.api.components.IncludeOptions,
                    com.day.cq.wcm.api.WCMMode,
                    org.apache.sling.api.resource.Resource,
                    java.util.Set,
                    java.util.Arrays" %><%
%><%@include file="/libs/foundation/global.jsp"%><%

IncludeOptions.clear(slingRequest);
IncludeOptions opts = IncludeOptions.getOptions(slingRequest, true);

//opts.setDecorationTagName("div");
opts.forceCellName("styled-parsys-inner");
opts.forceEditContext(true);

String cssClassesStr = properties.get("cssClasses", "");
if (!cssClassesStr.isEmpty()) {
	Set<String> cssClasses = opts.getCssClassNames();
	cssClasses.addAll(Arrays.asList(cssClassesStr.split(" ")));
}

RequestDispatcherOptions reqOpts = new RequestDispatcherOptions();
reqOpts.setForceResourceType("girlscouts/components/styled-parsys/parsys");

slingRequest.getRequestDispatcher(resource, reqOpts).include(slingRequest, slingResponse);
%>