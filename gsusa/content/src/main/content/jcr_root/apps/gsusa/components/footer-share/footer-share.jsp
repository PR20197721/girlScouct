<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp" %>
<%
	Boolean hideFacebook = properties.get("hideFacebook", false);
	Boolean hideTwitter = properties.get("hideTwitter", false);
	Boolean hideYoutube = properties.get("hideYoutube", false);

	
%>
<%
//TODO: refactor
//TODOL everything is hardcoded. Some style is inline. It is just temporary until we have time to make everything dynamice and nice
//If all buttons are hidden
if (WCMMode.fromRequest(request) == WCMMode.EDIT && hideFacebook && hideTwitter && hideYoutube) {
%>
	<li>Please click to edit the footer share component</li>
<% 
}
%>

<ul class="inline-list">
	<% if (!hideFacebook)  {%>
	    <li><a href="https://www.facebook.com/GirlScoutsUSA"><img src="/etc/designs/gsusa/images/facebook_30_white.png" tabindex="170"></a></li><%
   	}%>
    <% if (!hideTwitter)  {%>
        <li><a href="https://twitter.com/girlscouts"><img src="/etc/designs/gsusa/images/twitter_30_white.png" tabindex="171"></a></li><%
    }%>
    <% if (!hideYoutube)  {%>
    	<li><a href="https://www.youtube.com/user/girlscoutvideos"><img src="/etc/designs/gsusa/images/youtube_30_white.png" tabindex="172"></i></a></li><%
    }%>
    	<li><a href="http://pinterest.com/gsusa"><img src="/etc/designs/gsusa/images/pinterest_30_white.png" tabindex="173"></a></li>
    	<li><a href="http://blog.girlscouts.org"><img src="/etc/designs/gsusa/clientlibs/images/white_trefoil.png" width="32px" height="32px" style="top:-5px; opacity:0.8; position:relative;" tabindex="174"></a></li>
    	<li><a href="http://www.linkedin.com/company/girl-scouts-of-the-usa/"><img src="/etc/designs/gsusa/images/linkedin_32_white.png" tabindex="175"></a></li>
    	<li><a href="http://instagram.com/girlscouts"><img src="/etc/designs/gsusa/images/instagram_32_white.png" tabindex="176"></a></li>
</ul>