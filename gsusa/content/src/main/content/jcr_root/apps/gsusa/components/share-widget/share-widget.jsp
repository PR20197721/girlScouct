<%@ page import="com.day.cq.wcm.api.WCMMode, java.lang.StringBuilder" %>
<%@include file="/libs/foundation/global.jsp"%>

<%
	String fbTitle = properties.get("fbTitle", "");
	String anchor = properties.get("anchor", "");
	String fbMessage = properties.get("fbMessage", "");
	String imagePath = properties.get("fileReference", "");
	
	String twitterMessage = properties.get("twitterMessage", "");
	String hashtags = properties.get("hashtags", "");
	
	String url = anchor.equals("") ? currentPage.getPath() + ".html#" + anchor : currentPage.getPath() + ".html";
	
	if(!fbMessage.equals("")){
		StringBuilder fb = new StringBuilder();
		fb.append("<div class=\"fb-share-button\" data-href=\"" + url + "\" data-layout=\"button_count\" data-desc=\"" + fbMessage + "\"");
		if(!fbTitle.equals("")){
			fb.append(" data-title=\"" + fbTitle + "\"");
		} if(!imagePath.equals("")){
			fb.append(" data-image=\"" + imagePath + "\"");
		}
%>

<%= fb.toString() %>

<% 	}if(!twitterMessage.equals("")){
		StringBuilder tw = new StringBuilder();
		tw.append("<a class=\"twitter-share-button\" href=\"https://twitter.com/intent/tweet\"?url=\"" + url 
				+ "\"&via=\"@girlscouts\"&text=\"" + twitterMessage + "\"");
		if(!hashtags.equals("")){
			tw.append("&hashtags=\"" + hashtags + "\"");
		}
		tw.append(">Tweet</a>");
		
%>

<%= tw.toString() %>

<% } %>