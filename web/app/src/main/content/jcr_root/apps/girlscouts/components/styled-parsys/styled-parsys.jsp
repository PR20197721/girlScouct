<%@include file="/libs/foundation/global.jsp"%>
<%@page import="com.day.cq.wcm.api.components.IncludeOptions,
	java.util.Set,
	java.util.Arrays,
	javax.jcr.Node"%>
<%
	Resource parRes = resource.getChild("par");
	if (parRes != null) {
	    Node parNode = parRes.adaptTo(Node.class);
	    if (parNode.hasProperty("cssClasses")) {
			String cssClassesStr = parNode.getProperty("cssClasses").getString();
			IncludeOptions opt = IncludeOptions.getOptions(request, true);
			Set<String> classes = opt.getCssClassNames();
			classes.addAll(Arrays.asList(cssClassesStr.split(" ")));
	    }
	}
%>
<cq:include path="par" resourceType="girlscouts/components/styled-parsys-inner"/>