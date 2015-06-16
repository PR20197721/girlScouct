<%@page import="com.day.cq.wcm.api.WCMMode, java.lang.StringBuilder, com.day.cq.commons.Externalizer, java.net.URLEncoder" %>
<%@include file="/libs/foundation/global.jsp"%>

<%
String canonicalUrl = currentPage.getPath().replaceFirst("/content", "http://girlscouts.org");
String url = properties.get("url",canonicalUrl);

String title = properties.get("title","");
boolean fb = properties.get("facebook",false);
boolean twitter = properties.get("tweet",false);
boolean pinterest = properties.get("pinterest",false);
String twitterMsg = properties.get("twitterMessage","");
String otherMsg = properties.get("otherMessage","");

StringBuilder sb = new StringBuilder();

long uniqueId = System.currentTimeMillis();

if(!fb && !twitter && !pinterest){
	%> **Please check at least one social network to share <%
}else{
	%> <div id="addthisToolbox_<%= uniqueId %>" class="addthis_toolbox addthis_default_style addthis_32x32_style" style="margin:2px"> <%
	if(fb){
		sb = new StringBuilder();
		sb.append("<a id=\"addthis_button_facebook" + uniqueId + "\" class=\"addthis_button_facebook\" addthis:url=\"" + url + "\"");
		if(!title.equals("")){
			sb.append(" addthis:title=\"" + title + "\"");
		}
		if(!otherMsg.equals("")){
			sb.append(" addthis:description=\"" + otherMsg + "\"");
		}
		sb.append("></a>");
		%> <%= sb.toString() %> <%
	} if(twitter){
		sb = new StringBuilder();
		sb.append("<a id=\"addthis_button_twitter" + uniqueId + "\" class=\"addthis_button_twitter\" addthis:url=\"" + url + "\"");
		if(!twitterMsg.equals("")){
			sb.append(" addthis:title=\"" + title + "\"");
		}
		sb.append("></a>");
		%> <%= sb.toString() %> <%
	} if(pinterest){
		sb = new StringBuilder();
		sb.append("<a id=\"addthis_button_pinterest_share" + uniqueId + "\" class=\"addthis_button_pinterest_share\" addthis:url=\"" + url + "\"");
		if(!title.equals("")){
			sb.append(" addthis:title=\"" + title + "\"");
		}
		if(!otherMsg.equals("")){
			sb.append(" addthis:description=\"" + otherMsg + "\"");
		}
		sb.append("></a>");
		%> <%= sb.toString() %> <%
	}
	%> </div> <%
}


%>