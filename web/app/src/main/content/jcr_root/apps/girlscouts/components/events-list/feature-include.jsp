<%@page import="com.day.cq.wcm.foundation.List,com.day.cq.wcm.api.Page,java.util.Set,java.util.HashSet,
                java.util.Iterator,com.day.cq.wcm.api.PageFilter,com.day.cq.wcm.api.WCMMode,com.day.cq.tagging.*,
                java.util.Deque, java.util.ArrayDeque, com.day.cq.commons.RangeIterator" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
	ArrayDeque<String> featureEvents = new ArrayDeque<String>();
	ArrayDeque<String> taggedEvents = new ArrayDeque<String>();

	// Selected
	List list = new List(slingRequest, new PageFilter());
	if (!list.isEmpty()) {
		Iterator<Page> items = list.getPages();
		while (items.hasNext()) {
			Page item = (Page)items.next();
			featureEvents.push(item.getPath());
		}
	}

	// Tags
	TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
	String[] tagIDs = (String[])properties.get("cq:tags", null);
	String path = currentSite.get("eventPath",String.class);
	if (tagIDs != null) {
		for (String tag : tagIDs) {
			RangeIterator<Resource> it = tagManager.find(path, new String[]{tag});
			while (it.hasNext()) {
				Resource event = it.next();
				String eventPath = event.getPath();
				int index = eventPath.lastIndexOf("/jcr:content");
				if (index > 0) {
					eventPath = eventPath.substring(0, index);
				}
				taggedEvents.push(eventPath);
			}
		}
	}

	request.setAttribute("featureEvents", featureEvents);
	request.setAttribute("taggedEvents", taggedEvents);
	if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
		%><cq:includeClientLib categories="apps.girlscouts.components.authoring"/><%
	}
%>