<%@page import="com.day.cq.wcm.foundation.List, com.day.cq.wcm.api.PageFilter,com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<%
    
List list = new List(slingRequest, new PageFilter());

request.setAttribute("list", list);

if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
	%><cq:includeClientLib categories="apps.girlscouts.components.authoring"/><%
}

%>