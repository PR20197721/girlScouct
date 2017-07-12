<%@include file="/libs/foundation/global.jsp"%><%
%><%@ page import="org.apache.commons.lang3.StringEscapeUtils,
        com.day.cq.commons.Doctype,
        com.day.cq.commons.DiffInfo,
        com.day.cq.commons.DiffService,
        org.apache.sling.api.resource.ResourceUtil" %>
        <%@page import="com.day.cq.wcm.api.WCMMode" %>
        
     
<%
  String title = properties.get("title", String.class);
  if(title != null && title.trim().length() > 0){
       %><h1><%=title.trim()%></h1><% 
  }else{
	  if (WCMMode.fromRequest(request) == WCMMode.EDIT){
		  %>
		     <h1> Please Enter  Title </h1>
		  <%
	  }
  }
%>
