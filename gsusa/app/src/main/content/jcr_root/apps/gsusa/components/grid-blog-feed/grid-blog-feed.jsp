<%@include file="/libs/foundation/global.jsp"%>
<%@page import="org.apache.sling.commons.json.*, java.io.*, java.net.*" %>

<%
	//String id = properties.get("id","7441709438919444345");
	//String key = properties.get("key", "AIzaSyDyEWV7rt41tGxPcIXZ04kG38-ZNxkBrM0");
	
	// General Tab
	String url = properties.get("url", "");
	String count = properties.get("count", "20");
	String pinID1 = properties.get("postid1","");
	String pinID2 = properties.get("postid2","");
	String pinID3 = properties.get("postid3","");
	
	// Desktop Tab
	String desktoptitlefont = properties.get("desktoptitlefont", "");
	String desktopsnippetfont = properties.get("desktopsnippetfont", "");
	String desktoptitlelines = properties.get("desktoptitlelines", "");
	String desktopsnippetlines = properties.get("desktopsnippetlines", "");
	
	// Mobile Tab
	String mobiletitlefont = properties.get("mobiletitlefont", "");
	String mobilesnippetfont = properties.get("mobilesnippetfont", "");
	String mobiletitlelines = properties.get("mobiletitlelines", "");
	String mobilesnippetlines = properties.get("mobilesnippetlines", "");

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
	//console.log("truncate: " + el);
	if (lines == 0) return;
	var $container = $(el),
		className = "trunc",
		$elspan = $('<span class="' + className + '" />'),
		spantag = '<span style="white-space: nowrap;">',
		currLine = 0,
		currOffset,
		containerHeight,
		setStartEllipAt,
        startEllipAt = 0,
        endEllipAt,
		words,
		baseText = $container.text();
		
	$elspan.text(baseText);	
	$container.empty().append($elspan);
	
	containerHeight = $container.height();

	/*
	if (containerHeight >= $elspan.height()) { 
		console.log("cHeight: " + containerHeight + " spanHeight: " + $elspan.height());
		return; 
	}*/
	
	words = $.trim(baseText).split(/\s+/);
	$elspan.html(spantag + words.join('</span> ' + spantag) + '</span>');
	
	var setStartEllipByLine = function(i, word) {
		var $word = $(word),
			top = $word.position().top;
		if (top != currOffset) {
			currOffset = top;
			currLine += 1;
			
			if (currLine <= lines) {
				startEllipAt = i;
			} else if (currLine > lines) {
				endEllipAt = i-1;
				return;
			}
			
		}
		/*
		if (currLine == lines) {
			if (startEllipAt == 0) startEllipAt = i;
			//return false;
		}
		if (currLine > lines) {
			endEllipAt = i-1;
			return false;
		}
		*/
	};
	$elspan.find('span').each(setStartEllipByLine);

	function getLastLine(start, end) {
		var lastline = "";
		for(var i = 0; i < start; i++) {
			lastline += words[i] + " ";
		}
		lastline += '<span class="' + 'truncline' + '">'; // + words[start];
		if (endEllipAt != undefined) {
			for(var i = start; i <= end; i++) {
				lastline += words[i] + " ";
			}
			lastline += "...";
		} else {
			for(var i = start; i < words.length; i++) {
				lastline += words[i] + " ";
			}
		}
		lastline += "</span>";
		return lastline;
	}
	function updateText(start, end) {		

		console.log("updateText(" + start + ", " + end + ")");
		//words[start] = '<span class="' + 'truncline' + '">' + words[start];
		//if (endEllipAt != undefined) {
		//	words[endEllipAt] = words[endEllipAt] + '...' + '</span>';
		//	words.length = endEllipAt+1;
		//}
		//words.push('</span>');
		//$elspan.html(words.join(' '));
		
		var lastline = getLastLine(start, end);
		$elspan.html(lastline);		
		console.log("$container.width(): " + $container.width());
		console.log("$elspan.width(): " + $elspan.width());	
		while ($elspan.width() > $container.width()) {
			updateText(start, end-1);
		}
		//$elspan.html(lastline);
	}	
	if (startEllipAt != null) {
		console.log("******************");
		console.log("startEllipAt: " + startEllipAt + " = " + words[startEllipAt]);
		console.log("endEllipAt: " + endEllipAt + " = " + words[endEllipAt]);
        updateText(startEllipAt, endEllipAt);
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
	var desktoptitlefont = '<%= desktoptitlefont %>';
	var desktopsnippetfont = '<%= desktopsnippetfont %>';
	var desktoptitlelines = '<%= desktoptitlelines %>';
	var desktopsnippetlines  = '<%= desktopsnippetlines %>';
	var mobiletitlefont = '<%= mobiletitlefont %>';
	var mobilesnippetfont = '<%= mobilesnippetfont %>';
	var mobiletitlelines = '<%= mobiletitlelines %>';
	var mobilesnippetlines = '<%= mobilesnippetlines %>';
	
	
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

			console.log("tfont:" + desktoptitlefont + " sfont: " + desktopsnippetfont);
			console.log("tlines: " + desktoptitlelines + " slines: " + desktopsnippetfont);
			
			$('.blogfeedtitle').each(function() {
				truncate(this, desktoptitlelines);
				});
			$('.blogfeedsnippet').each(function() {
				truncate(this, desktopsnippetlines);
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
