<%@include file="/libs/foundation/global.jsp"%>
<%@page import="org.apache.sling.commons.json.*, java.io.*, java.net.*" %>

<%
	//String id = properties.get("id","7441709438919444345");
	//String key = properties.get("key", "AIzaSyDyEWV7rt41tGxPcIXZ04kG38-ZNxkBrM0");
	String url = properties.get("url", "");
	String count = properties.get("count", "20");
	String pinID1 = properties.get("postid1","");
	String pinID2 = properties.get("postid2","");
	String pinID3 = properties.get("postid3","");

	//String url = "https://www.googleapis.com/blogger/v3/blogs/" + id + "/posts?key=" + key;
%>
<div id="tag_social_feed_blog" class="feedwrapper clearfix">
	<span class="icon-pencil"></span>
	<div class="feed-block">
		<div class="block-area">
			<div class="blog-feed-image-head-area centered"></div>
			<ul class="blog-feed-area"></ul>
		</div>
	</div>
</div>
<!-- div class="align-center">
	<img src="http://wwwr.girlscouts.org/images/blog/gssm.png" alt="Girl Scout Blog" border="0" id="gsLogo">
	<div class="blog-embed-area"></div>
</div !-->

<script type="text/javascript">


var blogFeedArea = $(".blog-feed-area");

var pinIDArray = ["<%=pinID1%>","<%=pinID2%>","<%=pinID3%>"];
var output = "";

$(document).ready(function() {

	var url = '<%= url %>';
	var count = '<%= count %>';
	var pinPost1 = '<%= pinID1 %>';
	var pinPost2 = '<%= pinID2 %>';
	var pinPost3 = '<%= pinID3 %>';
	
	$.ajax({
		url: url,
		data: {
			count: count, 
			pin1: pinPost1,
			pin2: pinPost2,
			pin3: pinPost3
			},
		dataType: "json",
		success: function(result) {

			var imageUrlPattern = /<img [^>]*src=\"([^\"]*)\"/gmi;
			var imageTag = "";			
			
			for (var i = 0; i < result.items.length; i++) {
				var data = result.items[i];
				var contentData = data.content;

				if (contentData && contentData.match(imageUrlPattern)) {
					imageTag = contentData.match(imageUrlPattern)[0] + " />";
				}
				console.log(imageTag);

				var date = data.published;
				var liwrapper = 
					'<li class="blogger">' + 
						'<a href="' + data.url + '" target="_blank">' + 
							'<div class="blogfeedwrapper">' +
								'<div class="blogfeedleft">' +
									imageTag +
								'</div>' +
								'<div class="blogfeedright">' +
									'<div class="blogfeedtitle">' + data.title + '</div>' +
									'<div class="blogfeedsnippet">Snippet Snippet</div>' +
								'</div>' +
							'</div>' + 
						'</a>' + 
					'</li>';			

				var blogtext = data.title.toLowerCase().replace(/https?:\/\//i, "").trim().replace(/[^0-9a-z_]/g, "-");
				//output += '<li id="tag_blog_item_' + 0 + '"><div>' + imageTag + '<a id="tag_blog_title_' + blogtext + '" class="title" href="' + data.url + '" target="_blank">' + 'READ MORE' + '</a></div></li>';
				output += liwrapper;

				
			}			
			blogFeedArea.html(output);
			$('.blogfeedtitle').ellipsis({ lines:3 });
			},
		error: function(jqXHR, status, error) {
			console.log('ERROR');
			console.log('STATUS: ' + status);
			console.log('ERROR: ' + error);
			}
	});
	
})
</script>
