<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
String cssClasses = properties.get("cssClasses", "");
%>
<%!
  int timer = 0;
%>
<%
  String editFlag = "true";

if (WCMMode.fromRequest(request) == WCMMode.EDIT){
    editFlag ="false"; 
    request.setAttribute("editFlag",editFlag);
    %>
  <% }
%>
<% 
   String sbplacement = properties.get("spplacement","");
   timer = Integer.parseInt(properties.get("slideshowtimer", "6000"));
%>
<%
	if(sbplacement.equals("right")){
    %>
	  <cq:include script="spring-board-right.jsp"/>
	<% }else{%>
	 <cq:include script="default-sboard-rendition.jsp"/>
<%} %>


<script>
 $(document).ready(function(){
	     setTimer("<%=timer%>","<%=editFlag%>");
	});
 </script>
