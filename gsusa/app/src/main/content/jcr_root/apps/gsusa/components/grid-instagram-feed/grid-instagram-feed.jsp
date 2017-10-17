<%@include file="/libs/foundation/global.jsp"%>
<%@page import="org.apache.sling.commons.json.*, java.io.*, java.net.*" %>

<%
	
	int count = properties.get("count",19);
	String pinID = properties.get("pin-id","");
%>
<div id="tag_social_feed_instagram" class="wrapper clearfix">
	<span class="icon-social-instagram" style="font-size: 50px;display: block;color: #333;text-align: center;"></span>
    <div class="social-block">
        <div class="block-area">
			<div class="instagram-feed-image-head-area"></div>
	        <ul class="instagram-feed-image-area"></ul>
	    </div>
	    <p class="centered"><a href="https://instagram.com/girlscouts/" title="see more on instagram">See more</a></p>
    </div>
</div>
<script>

function process(data) {

	if (data.items != undefined) {
		console.log('processing data');
		
		var feedHeadArea = $(".instagram-feed-image-head-area");
	    var feedArea = $(".instagram-feed-image-area");

	    var posts = [];

	    var count = <%= count %>;
	    var pinID = "<%= pinID %>";
		
	    var postIDPattern = /\/p\/([^\/]*)(\/)/gmi;

	    
	    for(var i=0; i < data.items.length; i++){
		    console.log('item: ' + i);
	    	var postID = "";
	  		if (data.items[i].link && data.items[i].link.match(postIDPattern)) {
	  			postID = data.items[i].link.match(postIDPattern)[0].replace("/p/","").replace("/","");
	  			if(postID == pinID){
	  				posts.unshift(data.items[i]);
	  				//console.log("Found Instagram Pin");
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
    		output = '<li><a href="' + posts[i].link + '" target="_blank"><img src="' + posts[i].images.thumbnail.url + '" /></a></li>';
    		feedArea.append(output);
    	}
    	console.log(posts[1]);
    } else {
        console.log('data is undefined');
    }

	
}

$(function() {
    console.log( "ready!!" );

    $.ajax({
		type: 'GET',
		url: '/socialmediafeed.aspx',
		data: {
			hello: 'qwerty'
			},
		dataType: 'json',
		success: function(data) {
			process(data);
			},
		error: function(data) {
			console.log('ERROR');
			console.log(data);
			},
		complete: function(data) {
			console.log('COMPLETE');
			}
    });

    
});
</script>


