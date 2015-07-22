<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp" %>
<%
	Boolean hideFacebook = properties.get("hideFacebook", false);
	Boolean hideTwitter = properties.get("hideTwitter", false);
	Boolean hideFlickr = properties.get("hideFlickr", false);
	Boolean hideYoutube = properties.get("hideYoutube", false);

	
%>
<%
//TODO: refactor
//If all buttons are hidden
if (WCMMode.fromRequest(request) == WCMMode.EDIT && hideFacebook && hideTwitter && hideFlickr && hideYoutube) {
%>
	<li>Please click to edit the footer share component</li>
<% 
}
%>

<ul class="inline-list">
	<% if (!hideFacebook)  {%>
    	<li><a href="https://www.facebook.com/gsgcfl"><img src="/etc/designs/girlscouts-usa-green/images/facebook_30_white.png" tabindex="170"></a></li><%
   	}%>
    <% if (!hideTwitter)  {%>
    	<li><a href="https://twitter.com/gsgc"><img src="/etc/designs/girlscouts-usa-green/images/twitter_30_white.png" tabindex="171"></a></li><%
    }%>
    <% if (!hideFlickr)  {%>
    	<li><a href="https://www.flickr.com/photos/gsgc/"><img src="/etc/designs/girlscouts-usa-green/images/flickr_30_white.png" tabindex="172"></a></li><% 
   	}%>
    <% if (!hideYoutube)  {%>
    	<li><a href="http://www.youtube.com/user/gsgcouncil?feature=watch"><img src="/etc/designs/girlscouts-usa-green/images/youtube_30_white.png" tabindex="173"></a></li><%
    }%>
</ul>