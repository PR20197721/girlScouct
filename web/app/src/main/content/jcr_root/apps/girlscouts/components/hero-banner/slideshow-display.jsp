<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>

<%
final String[] carouselList = properties.get("carouselList", String[].class);
if(carouselList != null && carouselList.length>0){
	%>
		<cq:include script="slideshow-image-video-include.jsp"/>
	<% }else{%>
		<cq:include script="slideshow-image-include.jsp"/>
<%} %>

