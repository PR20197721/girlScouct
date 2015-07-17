<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp" %>
<%
	boolean hideFacebook = properties.get("hideFacebook", false);
	boolean hideTwitter = properties.get("hideTwitter", false);
	boolean hideFlickr = properties.get("hideFlickr", false);
	boolean hideYoutube = properties.get("hideYoutube", false);
	
%>

<ul class="inline-list">
	<% if (!hideFacebook)  {%>
    	<li><a href="https://www.facebook.com/gsgcfl"><img src="/etc/designs/girlscouts-usa-green/images/facebook_30_white.png"></a></li><%
   	}%>
    <% if (!hideTwitter)  {%>
    	<li><a href="https://twitter.com/gsgc"><img src="/etc/designs/girlscouts-usa-green/images/twitter_30_white.png"></a></li><%
    }%>
    <% if (!hideFlickr)  {%>
    	<li><a href="https://www.flickr.com/photos/gsgc/"><img src="/etc/designs/girlscouts-usa-green/images/flickr_30_white.png"></a></li><% 
   	}%>
    <% if (!hideYoutube)  {%>
    	<li><a href="http://www.youtube.com/user/gsgcouncil?feature=watch"><img src="/etc/designs/girlscouts-usa-green/images/youtube_30_white.png"></a></li><%
    }%>
</ul>