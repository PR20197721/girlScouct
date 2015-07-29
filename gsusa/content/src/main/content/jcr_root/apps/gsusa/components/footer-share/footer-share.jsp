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
    	<li><a href="https://www.facebook.com/GirlScoutsUSA"><i class="fa fa-facebook fa-2x" tabindex="170"></i></a></li><%
   	}%>
    <% if (!hideTwitter)  {%>
    	<li><a href="https://twitter.com/girlscouts"><i class="fa fa-twitter fa-2x" tabindex="171"></i></a></li><%
    }%>
    <% if (!hideYoutube)  {%>
    	<li><a href="https://www.youtube.com/user/girlscoutvideos"><i class="fa fa-youtube-square fa-2x" tabindex="173"></i></a></li><%
    }%>
    	<li><a href="http://pinterest.com/gsusa"><i class="fa fa-pinterest-p fa-2x" tabindex="174"></i></a></li>
    	<li><a href="http://blog.girlscouts.org"><img src="/etc/designs/gsusa/clientlibs/images/white_trefoil.png" width="32px" height="32px" style="top:-5px; opacity:0.8; position:relative;" tabindex="175"></a></li>
    	<li><a href="http://www.linkedin.com/company/girl-scouts-of-the-usa/"><i class="fa fa-linkedin-square fa-2x" tabindex="176"></i></a></li>
    	<li><a href="http://instagram.com/girlscouts"><i class="fa fa-instagram fa-2x" tabindex="177"></i></a></li>
</ul>