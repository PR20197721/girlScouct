<%@page import="com.day.cq.wcm.foundation.List,com.day.cq.wcm.api.Page,java.util.Set,java.util.HashSet,
                java.util.Iterator,com.day.cq.wcm.api.PageFilter,com.day.cq.wcm.api.WCMMode,
                java.util.LinkedList" %>
<%@include file="/libs/foundation/global.jsp"%>
<%
    
List list = new List(slingRequest, new PageFilter());
LinkedList<String> featureEvents = new LinkedList<String>();
if(!list.isEmpty()){
	Iterator<Page> items = list.getPages();
	
	while(items.hasNext()){
		Page item = (Page)items.next();	
		featureEvents.push(item.getPath());
	}
	
	
}
request.setAttribute("featureEvents", featureEvents);
if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
	%><cq:includeClientLib categories="apps.girlscouts.components.authoring"/><%
}

%>