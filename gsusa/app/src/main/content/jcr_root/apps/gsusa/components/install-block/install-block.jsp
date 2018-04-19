<%@page import="com.day.cq.wcm.api.WCMMode, com.day.cq.wcm.foundation.Placeholder" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%@ taglib prefix="gsusa" uri="https://girlscouts.org/gsusa/taglib" %>

<%
	if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
%>
   		<cq:includeClientLib categories="apps.gsusa.authoring" />
<%
	}
	String color = properties.get("bgcolor", "FAA61A");
	if(!color.contains("rgb")){
		color = "#" + color;
	}
%>
<div class="clearfix"></div>
<div class="install-app" style="background-color: <%= color %>">
	<i class="<%= properties.get("icon", "icon-download") %>"></i>
	<div class="wrapper clearfix">
		<p><%= properties.get("title", "Install") %></p>
		<p><%= properties.get("description", "The official Girl Scout Cookie Finder App") %></p>
	</div>
	<div class="wrapper clearfix AppStoreContainer">
		<div class="apple-download">
			<gsusa:image 
				relativePath='appleicon'
				href='<%= properties.get("./applelink", "") %>' 
				alt="google play store" 
				newWindow='<%= properties.get("./applenewwindow", false) %>' 
				description="Girl Scouts iPhone App"/>
		</div>
		<div class="google-play-download">
			<gsusa:image 
				relativePath='googleicon' 
				href='<%= properties.get("./googlelink", "") %>' 
				alt="app store" 
				newWindow='<%= properties.get("./googlenewwindow", false) %>' 
				description="Girl Scouts Android App"/>
		</div>
	</div>
</div>