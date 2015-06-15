<%@page import="com.day.cq.wcm.api.WCMMode, java.lang.StringBuilder, com.day.cq.commons.Externalizer, java.net.URLEncoder" %>
<%@include file="/libs/foundation/global.jsp"%>

<%
	String fbTitle = "";
	String fbMessage = "";
	Node currentPageNode = currentPage.adaptTo(Node.class).getNode("jcr:content");
	if(currentPageNode.hasProperty("shareTitle")){
		fbTitle = currentPageNode.getProperty("shareTitle").getString();
	}
	if(currentPageNode.hasProperty("fbMessage")){
		fbMessage = currentPageNode.getProperty("fbMessage").getString();
	}
	String anchor = properties.get("anchor", "");
	String imagePath = properties.get("fileReference", "");
	
	String twitterMessage = properties.get("twitterMessage", "");
	String hashtags = properties.get("hashtags", "");
	
	if(fbMessage.equals("") && twitterMessage.equals("") && WCMMode.fromRequest(request) == WCMMode.EDIT){
		%> **Please configure the share widget component so that there is either a facebook or twitter message <%
	}
	
	else{
		if(anchor.equals("")){
			%> <div> <%
		}else{
			%> <div id="<%= anchor %>">
		<% }
		
		//Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);
		//Externalizer not yet configured
		//String canonicalUrl = externalizer.publishLink(resourceResolver, "http", currentPage.getPath());
		
		String canonicalUrl = currentPage.getPath().replaceFirst("/content", "http://girlscouts.org");
		String url = !anchor.equals("") ? canonicalUrl + ".html#" + anchor : canonicalUrl + ".html";
		
		if(!fbMessage.equals("")){
			StringBuilder fb = new StringBuilder();
			fb.append("<div class=\"fb-share-button\" data-href=\"" + url + "\" data-layout=\"button_count\" data-desc=\"" + fbMessage + "\"");
			if(!fbTitle.equals("")){
				fb.append(" data-title=\"" + fbTitle + "\"");
			} if(!imagePath.equals("")){
				fb.append(" data-image=\"" + imagePath + "\"");
			}
			fb.append("></div>");
	%>
	
	<%= fb.toString() %>
	
	<% 	}if(!twitterMessage.equals("")){
			StringBuilder tw = new StringBuilder();
			String htmlEncodedTweet = URLEncoder.encode(twitterMessage, "US-ASCII");
			tw.append("<a class=\"twitter-share-button\" href=\"https://twitter.com/intent/tweet?url=" + url
					+ "&via=girlscouts&text=" + htmlEncodedTweet);
			if(!hashtags.equals("")){
				tw.append("&hashtags=" + hashtags + "\"");
			}
			tw.append(">Tweet</a>");
			
	%>
	
	<%= tw.toString() %>
		<% } %>
	</div>
<% } %>