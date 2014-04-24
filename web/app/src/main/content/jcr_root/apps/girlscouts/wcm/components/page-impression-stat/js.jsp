<%@page import="org.girlscouts.web.stat.PageImpressionTracker" %>
<%@include file="/libs/foundation/global.jsp" %>

<%
PageImpressionTracker tracker = sling.getService(PageImpressionTracker.class);
String[] paths = request.getParameterValues("path");
for (int i = 0; i < paths.length; i++) {
	tracker.track(paths[i]);
}
%>
Impression logged.