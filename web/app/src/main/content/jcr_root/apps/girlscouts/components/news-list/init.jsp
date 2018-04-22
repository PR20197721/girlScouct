<%@page import="com.day.cq.wcm.foundation.List,com.day.cq.wcm.api.Page,java.util.Set,java.util.HashSet,
                java.util.Iterator,com.day.cq.wcm.api.PageFilter,com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<%
List list = new List(slingRequest, new PageFilter());
request.setAttribute("list", list);
Set<String> featureNews = new HashSet<String>();
if(!list.isEmpty()){
	Iterator<Page> items = list.getPages();
	while(items.hasNext()){
		Page item = (Page)items.next();	
		featureNews.add(item.getPath());
	}
}
request.setAttribute("featureNews", featureNews);
if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
	%><cq:includeClientLib categories="apps.girlscouts.components.authoring"/><%
}
%>