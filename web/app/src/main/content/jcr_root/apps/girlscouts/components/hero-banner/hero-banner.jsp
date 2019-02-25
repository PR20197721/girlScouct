<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@ page import="javax.jcr.Session" %>
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
   try{
       timer = Integer.parseInt(properties.get("slideshowtimer", "6000"));
   }catch(Exception e){
       //Parse property and get only integer characters, update inputted value as well
       String val = properties.get("slideshowtimer", "6000");
       val = val.replaceAll("\\D+","");
       Node node = resource.adaptTo(Node.class);
       node.setProperty("slideshowtimer",val);
       Session session = resourceResolver.adaptTo(Session.class);
       session.save();
   }
   try{
        timer = Integer.parseInt(properties.get("slideshowtimer", "6000"));
   }catch(Exception e){
       //default if invalid string
       Node node = resource.adaptTo(Node.class);
       node.setProperty("slideshowtimer","6000");
       Session session = resourceResolver.adaptTo(Session.class);
       session.save();
       timer = 6000;
   }
   request.setAttribute("HeroBannerTimer", timer);
  
%>
<%
	if(sbplacement.equals("right")){
    %>
	  <cq:include script="spring-board-right.jsp"/>
	<% }else{%>
	 <cq:include script="default-sboard-rendition.jsp"/>
<%} %>
