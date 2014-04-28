<%@page import="java.util.Set,
	java.util.Arrays,
	com.day.cq.wcm.api.Page,
	com.day.cq.wcm.api.components.IncludeOptions" %>
<%
Page homepage = currentPage.getAbsoluteParent(2);
ValueMap currentSite = homepage.getContentResource().adaptTo(ValueMap.class);
%>
<%!
public void setCssClasses(String tags, HttpServletRequest request) {
	IncludeOptions opt = IncludeOptions.getOptions(request, true);
	Set<String> classes = opt.getCssClassNames();
	classes.addAll(Arrays.asList(tags.split(" ")));
}

public void setHtmlTag(String tag, HttpServletRequest request) {
	IncludeOptions opt = IncludeOptions.getOptions(request, true);
	opt.setDecorationTagName(tag);
}
%>
