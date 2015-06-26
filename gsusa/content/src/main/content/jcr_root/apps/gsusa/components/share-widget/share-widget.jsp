<%@page import="com.day.cq.wcm.api.WCMMode, java.lang.StringBuilder, com.day.cq.commons.Externalizer, java.net.URLEncoder" %>
<%@include file="/libs/foundation/global.jsp"%>

<%
String canonicalUrl = currentPage.getPath().replaceFirst("/content", "http://girlscouts.org");
String url = properties.get("url",canonicalUrl);

String title = properties.get("title","");
boolean fb = properties.get("facebook",true);
boolean twitter = properties.get("tweet",true);
boolean pinterest = properties.get("pinterest",true);
String twitterMsg = properties.get("twitterMessage","");
String otherMsg = properties.get("otherMessage","");

StringBuilder sb = new StringBuilder();

long uniqueId = System.currentTimeMillis();

if(!fb && !twitter && !pinterest){
	%> **Please check at least one social network to share <%
}else{
	%> <div id="addthisToolbox_<%= uniqueId %>" class="addthis_toolbox addthis_default_style addthis_32x32_style" style="margin:2px"> <%
	if(fb){
	%>
    <a class="facebook-icon" onclick="postToFeed(); return false;" />
 
    <script src='http://connect.facebook.net/en_US/all.js'></script>
    <script> 
      FB.init({appId: "1604075353204563", status: true, cookie: true});
 
      function postToFeed() {
 
        // calling the API ...
        var obj = {
          method: 'feed',
          redirect_uri: 'https://www.google.org/gogoldonline',
          link: 'https://www.girlscouts.org/gogoldonline//post.aspx?postid=2514',
          picture: 'https://www.google.com/images/srpr/logo11w.png',
          name: 'Facebook Dialogs',
          caption: 'FB CAPTION HERE',
          description: 'Here is description. Here is description. Here is description. Here is description. Here is description. '
        };
 
        function callback(response) {
        }
 
        FB.ui(obj, callback);
      }
 
    </script>
	<% 
	}

	if(twitter){
		sb = new StringBuilder();
		sb.append("<a id=\"addthis_button_twitter" + uniqueId + "\" class=\"addthis_button_twitter\" addthis:url=\"" + url + "\"");
		if(!twitterMsg.equals("")){
			sb.append(" addthis:title=\"" + title + "\"");
		}
		sb.append("></a>");
		%> <%= sb.toString() %> <%
	}
	
	if(pinterest){
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