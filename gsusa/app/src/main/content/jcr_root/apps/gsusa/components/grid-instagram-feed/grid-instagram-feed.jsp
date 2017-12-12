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
	String url = properties.get("url", "");
	int count = properties.get("count",19);
	String pinID = properties.get("pin-id","");
	String height = properties.get("height", "0");
	String uID = generateId();
	String instaID = "instagramfeed_" + uID;
	
	String desktopHeight = properties.get("desktopheight", "");
	String mobileHeight = properties.get("mobileheight", "");
%>
<style>
#tag_social_feed_instagram {
	<% if (desktopHeight != "0") { %>
	max-height:<%= desktopHeight %>px;
	height:<%= desktopHeight %>px;
	<% } %>
	overflow: auto;
}
@media (max-width: 768px) {
#tag_social_feed_instagram {
	<% if (mobileHeight != "0") { %>
	max-height:<%= mobileHeight %>px;
	height:<%= mobileHeight %>px;
	<% } %>
}}
</style>

<div id="tag_social_feed_instagram" class="feedwrapper clearfix">
	<div id="<%= instaID %>" >
		<span class="icon-social-instagram"></span>
	    <div class="feed-block">
	        <div class="block-area">
				<div class="instagram-feed-image-pinned-area centered"></div>
		        <ul class="instagram-feed-image-area"></ul>
		    </div>
		    <p class="centered"><a href="https://instagram.com/girlscouts/" title="see more on instagram">See more</a></p>
	    </div>
	</div>
</div>
<script>

function processInstagram(el, data) {

	if (data.items != undefined) {

		var feedID = $("#" + el);
		var feedHeadArea = $(feedID).find(".instagram-feed-image-pinned-area");
	    var feedArea = $(feedID).find(".instagram-feed-image-area");

	    var posts = [];

	    var count = <%= count %>;
	    var pinID = "<%= pinID %>";
		
	    var postIDPattern = /\/p\/([^\/]*)(\/)/gmi;

	    
	    for(var i=0; i < data.items.length; i++){
	    	var postID = "";
	  		if (data.items[i].link && data.items[i].link.match(postIDPattern)) {
	  			postID = data.items[i].link.match(postIDPattern)[0].replace("/p/","").replace("/","");
	  			if(postID == pinID){
	  				posts.unshift(data.items[i]);
	  			}
	  			else{
	  				posts.push(data.items[i]);
	  			}
	        }
	    }

    	var output = "";
		feedHeadArea.append('<a href="' + posts[0].link + '" target="_blank"><img src="' + posts[0].images.standard_resolution.url + '" /></a>');
    	if(posts.length < count){
    		count = posts.length;
    	}
		for(var i=1; i < count; i++){
    		output = '<li class="instagrid"><a href="' + posts[i].link + '" target="_blank"><img src="' + posts[i].images.low_resolution.url + '" /></a></li>';
    		feedArea.append(output);
    	}

    } else {
        console.log('data is undefined');
    }

}

$(function() {

	var instaID = '<%= instaID %>';
    var ajax_<%= uID %> = $.ajax({
		type: 'GET',
		url: '<%= url %>', 
		dataType: 'json',
		contentType: 'text/plain'
    });

    ajax_<%= uID %>.done(function(data){
        processInstagram(instaID, data);
    }).fail(function(jqXHR, textStatus, errorThrown){
		console.log('ERROR: ' + textStatus + ': ' + errorThrown);
    }).always(function(){
        console.log('COMPLETE');
    });
    
});
</script>


