<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode" %> 
 
<%
if(WCMMode.fromRequest(request) == WCMMode.EDIT){
	String redirectUrl = "/etc/importers/gsbulkeditor.html?rp=" + homepage.getPath() + "/contacts&cm=true&deep=true&cv=jcr%3Atitle&ec=title%2Cphone%2Cemail%2Cteam&hib=false&is=true&rt=girlscouts%2Fcomponents%2Fcontact-page&it=contacts&hpc=true";
	response.setStatus(301);
	response.setHeader("Location",redirectUrl);
	response.setHeader("Connection","close");	
}else{
	response.setStatus(301);
	response.setHeader("Location",homepage.getPath() + ".html");
	response.setHeader("Connection","close");
}
%>