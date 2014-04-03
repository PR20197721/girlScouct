<%@page import="java.util.Set,
	java.util.Arrays,
	javax.servlet.http.HttpServletRequest,
	com.day.cq.wcm.api.components.IncludeOptions" %>
<%!
public void setCssClasses(String tags, HttpServletRequest request) {
	IncludeOptions opt = IncludeOptions.getOptions(request, true);
	Set<String> classes = opt.getCssClassNames();
	classes.addAll(Arrays.asList(tags.split(" ")));
}
%>