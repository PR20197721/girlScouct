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

//Adapted from jquery.ellipsis.js
function truncate(el, lines) {
	console.log("truncate: " + el);
	var $container = $(el),
		className = "trunc",
		$elspan = $('<span class="' + className + '" />'),
		spantag = '<span style="white-space: nowrap;">',
		currLine = 0,
		currOffset,
		containerHeight,
		setStartEllipAt,
        startEllipAt,
		words,
		baseText = $container.text();
		
	$elspan.text(baseText);	
	$container.empty().append($elspan);
	
	containerHeight = $container.height();
	
	if (containerHeight >= $elspan.height()) { return; }
	
	words = $.trim(baseText).split(/\s+/);
	$elspan.html(spantag + words.join('</span> ' + spantag) + '</span>');
	
	var setStartEllipByLine = function(i, word) {
		var $word = $(word),
			top = $word.position().top;
		if (top !== currOffset) {
			currOffset = top;
			currLine += 1;
		}
		if (currLine === lines) {
			startEllipAt = i;
			return false;
		}
	};
	$elspan.find('span').each(setStartEllipByLine);
	
	function updateText(nth) {
		words[nth] = '<span class="' + 'truncline' + '">' + words[nth];
		words.push('</span>');
		$elspan.html(words.join(' '));
	}	
	if (startEllipAt != null) {
        updateText(startEllipAt);
    }
	
}

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
			console.log('GRIDBLOG');		
			
			for (var i = 0; i < result.items.length; i++) {
				var data = result.items[i];
				var liwrapper = 
					'<li class="blogger">' + 
						'<a href="' + data.url + '" target="_blank">' + 
							'<div class="blogfeedwrapper">' +
								'<div class="blogfeedimage" style="' + 
									'background-image:url(\'' + data.image + '\');" ' +
								'>' +
									//'<img src="' + data.image + '" />' +
								'</div>' +
								'<div class="blogfeedcontent">' +
									'<div class="blogfeedtitle">' + data.title + '</div>' +
									'<div class="blogfeedsnippet">' + data.content + '</div>' +
								'</div>' +
							'</div>' + 
						'</a>' + 
					'</li>';			

				output += liwrapper;		
			}
						
			blogFeedArea.html(output);
			$('.blogfeedsnippet').each(function() {
				truncate(this, 3);
			});
			},
		error: function(jqXHR, status, error) {
			console.log('GRID BLOG FEED');
			console.log('ERROR: ' + error);
			console.log('STATUS: ' + status);
			}
	});
	
})

</script>
