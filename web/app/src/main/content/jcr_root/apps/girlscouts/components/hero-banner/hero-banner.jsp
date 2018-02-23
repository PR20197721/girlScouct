<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
String cssClasses = properties.get("cssClasses", "");

 //Spring Board First 
 request.setAttribute("FsbDesc", properties.get("firstsbdesc","")); 
 request.setAttribute("FsbTitle",properties.get("firstsbtitle","")); 
 request.setAttribute("FsbButton", properties.get("firstsbbutton",""));
 request.setAttribute("FsbUrl", properties.get("firstsburl",""));
 request.setAttribute("FsbNewWindow", properties.get("firstsbnewwindow","false"));
 

 
 request.setAttribute("SsbDesc", properties.get("secondsbdesc","")); 
 request.setAttribute("SsbTitle",properties.get("secondsbtitle","")); 
 request.setAttribute("SsbUrl", properties.get("secondsburl",""));
 request.setAttribute("SsbButton", properties.get("secondsbbutton",""));
 request.setAttribute("SsbNewWindow", properties.get("secondsbnewwindow","false"));
 request.setAttribute("sbplacement",properties.get("spplacement",""));




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
