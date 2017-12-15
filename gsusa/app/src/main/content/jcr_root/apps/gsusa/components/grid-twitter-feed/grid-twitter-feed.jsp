<%@include file="/libs/foundation/global.jsp"%>
<%@page import="org.apache.sling.commons.json.*, 
				java.io.*, 
				java.net.*,
				java.util.Random" %>
<%
	String desktopHeight = properties.get("desktopheight", "");
	String mobileHeight = properties.get("mobileheight", "");
	String count = properties.get("count", "0");
%>
 
<style>
#tag_social_feed_twitter {
	<% if (!desktopHeight.equals("0")) { %>
	max-height:<%= desktopHeight %>px;
	height:<%= desktopHeight %>px;
	<% } %>
	overflow: auto;
}
@media (max-width: 768px) {
#tag_social_feed_twitter {
	<% if (!mobileHeight.equals("0")) { %>
	max-height:<%= mobileHeight %>px;
	height:<%= mobileHeight %>px;
	<% } %>
}}
</style>


<div id="tag_social_feed_twitter" class="feedwrapper clearfix">
	<span class="icon-social-twitter-tweet-bird"></span>
    <div class="feed-block">
        <a class="twitter-timeline"
        	data-link-color="#00AE58" 
        	href="https://twitter.com/girlscouts" 
        	data-tweet-limit="<%= count %>"
        	data-chrome="noheader nofooter noborders transparent">Tweets by girlscouts</a>
        <script async src="//platform.twitter.com/widgets.js" charset="utf-8"></script>
    </div>
</div>



