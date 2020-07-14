<%@page import="com.day.cq.wcm.foundation.List,com.day.cq.wcm.api.Page,java.util.Set,java.util.HashSet,
                java.util.Iterator,com.day.cq.wcm.api.PageFilter,com.day.cq.wcm.api.WCMMode,com.day.cq.tagging.*,
                java.util.Deque, java.util.ArrayDeque, com.day.cq.commons.RangeIterator" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
	String pathType = properties.get("pathType", "url");
	TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
	String[] tagIDs = (String[])properties.get("cq:tags", null);
	String path = currentSite.get("eventPath",String.class);
	RangeIterator<Resource> it = tagManager.find(path, tagIDs);
	ArrayDeque<String> featureEvents = new ArrayDeque<String>();

	if (pathType.equals("tags")) {
		while (it.hasNext()) {
			Resource event = it.next();
			//Check start date
			//Check for sort
			//if(start date in the future) {
			featureEvents.push(event.getPath());
			//}
		}
	}
	request.setAttribute("featureEvents", featureEvents);
	if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
		%><cq:includeClientLib categories="apps.girlscouts.components.authoring"/><%
	}
%>