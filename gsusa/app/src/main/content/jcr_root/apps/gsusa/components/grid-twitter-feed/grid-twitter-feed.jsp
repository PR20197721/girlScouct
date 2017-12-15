<%@include file="/libs/foundation/global.jsp"%>
<%@page import="org.apache.sling.commons.json.*, 
				java.io.*, 
				java.net.*,
				java.util.Random" %>

<%!
public String generateId() {
	Random rand = new Random();
	String possibleLetters = "abcdefghijklmnopqrstuvwxyz";
	StringBuilder sb = new StringBuilder(6);
	for(int i = 0; i < 4; i++)
	    sb.append(possibleLetters.charAt(rand.nextInt(possibleLetters.length())));
	return sb.toString();
}
%>

<%
	String desktopHeight = properties.get("desktopheight", "");
	String mobileHeight = properties.get("mobileheight", "");
%>
 
<style>
#tag_social_feed_instagram {
	<% if (!desktopHeight.equals("0")) { %>
	max-height:<%= desktopHeight %>px;
	height:<%= desktopHeight %>px;
	<% } %>
	overflow: auto;
}
@media (max-width: 768px) {
#tag_social_feed_instagram {
	<% if (!mobileHeight.equals("0")) { %>
	max-height:<%= mobileHeight %>px;
	height:<%= mobileHeight %>px;
	<% } %>
}}
</style>


<div id="tag_social_feed_twitter" class="feedwrapper clearfix">
	<span class="icon-social-twitter"></span>
    <div class="feed-block">
        <a class="twitter-timeline" 
        	data-height="520" 
        	data-link-color="#00AE58" 
        	href="https://twitter.com/girlscouts" 
        	data-tweet-limit="5"
        	data-chrome="noheader nofooter noborders transparent">Tweets by girlscouts</a>
        <script async src="//platform.twitter.com/widgets.js" charset="utf-8"></script>
    </div>

</div>



