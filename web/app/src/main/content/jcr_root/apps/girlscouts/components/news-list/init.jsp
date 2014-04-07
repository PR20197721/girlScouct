<%@page import="com.day.cq.wcm.foundation.List, com.day.cq.wcm.api.PageFilter" %>
<%@include file="/libs/foundation/global.jsp"%>
<%
    
List list = new List(slingRequest, new PageFilter());
request.setAttribute("list", list);

%>