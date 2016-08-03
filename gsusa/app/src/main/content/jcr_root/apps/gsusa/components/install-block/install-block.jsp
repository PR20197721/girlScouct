<%@page import="com.day.cq.wcm.api.WCMMode, com.day.cq.wcm.foundation.Placeholder" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>

<%if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
	   %><cq:includeClientLib categories="apps.gsusa.authoring" /><%
	}	
String title = properties.get("title", "Install");
String icon = properties.get("icon", "icon-download");
String desc = properties.get("description", "The official Girl Scout Cookie Finder App");
String color = properties.get("bgcolor", "FAA61A");
String apple = properties.get("apple", "");
String google = properties.get("google", "");

String[] aSplit = apple.split("\\|\\|\\|");
String aPath = aSplit.length >= 1 ? aSplit[0] : "";
String aLink = aSplit.length >= 2 ? aSplit[1] : "";
boolean aIsNewWindow = Boolean.valueOf(aSplit.length >=3 ? aSplit[2] : "false");

String[] gSplit = google.split("\\|\\|\\|");
String gPath = gSplit.length >= 1 ? gSplit[0] : "";
String gLink = gSplit.length >= 2 ? gSplit[1] : "";
boolean gIsNewWindow = Boolean.valueOf(gSplit.length >=3 ? gSplit[2] : "false");

	%><div class="install-app" style="background-color: #<%= color %>">
		<i class="<%= icon %>"></i>
		<div class="wrapper clearfix">
			<p><%= title %></p>
			<p><%= desc %></p>
		</div>
		<div class="wrapper clearfix">
			<% if(!apple.isEmpty()){
				if(aIsNewWindow){ %>
					<a href="<%= aLink %>" target="_blank" class="apple-download">
						<img src="<%= aPath %>" alt="app store">
						<span>Girl Scouts iPhone App</span>
					</a>
				<% } else{ %>
					<a href="<%= aLink %>" class="apple-download">
						<img src="<%= aPath %>" alt="app store">
						<span>Girl Scouts iPhone App</span>
					</a>
				<% }
				}if(!google.isEmpty()){
				if(gIsNewWindow){ %>
					<a href="<%= gLink %>" target="_blank" class="google-play-download">
						<img src="<%= gPath %>" alt="google play store">
						<span>Girl Scouts Android App</span>
					</a>
				<% } else{ %>
					<a href="<%= gLink %>" class="google-play-download">
						<img src="<%= gPath %>" alt="google play store">
						<span>Girl Scouts Android App</span>
					</a>
				<% }
				} %>
		</div>
	</div>