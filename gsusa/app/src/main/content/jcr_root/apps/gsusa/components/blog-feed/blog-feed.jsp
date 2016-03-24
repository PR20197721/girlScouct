<%@include file="/libs/foundation/global.jsp"%>
<%@page import="org.apache.sling.commons.json.*, java.io.*, java.net.*" %>

<%
	String id = properties.get("id","7441709438919444345");
	String key = properties.get("key", "AIzaSyDyEWV7rt41tGxPcIXZ04kG38-ZNxkBrM0");
	String pinID1 = properties.get("post-id1","");
	String pinID2 = properties.get("post-id2","");
	String pinID3 = properties.get("post-id3","");

	String url = "https://www.googleapis.com/blogger/v3/blogs/" + id + "/posts?key=" + key;
%>
	<!-- <div class="align-center">
		<img src="http://wwwr.girlscouts.org/images/blog/gssm.png" alt="Girl Scout Blog" border="0" id="gsLogo">
		<div class="blog-embed-area"></div>
	</div> -->

	<script type="text/javascript">

	//var blogFeedArea = $(".blog-embed-area");
	var blogFeedArea = $(".blog-grid");

	var id = "<%= id %>";
	var key = "<%= key %>";
	var ip = "<%=slingRequest.getHeader("x-forwarded-for") %>";
	var comma = ip.indexOf(",");
	var pinIDArray = ["<%=pinID1%>","<%=pinID2%>","<%=pinID3%>"];
	var output = "";
	ip = ip.substring(0, comma);
	if(ip == ""){
		if("<%= request.getRemoteAddr() %>" != ""){
			ip = "<%= request.getRemoteAddr() %>";
		}
	}
	var comma = ip.indexOf(",");
	if(comma != -1){
		ip = ip.substring(0, comma);
	}

	/*
	To test run the following on the page console:
	$.get("https://www.googleapis.com/blogger/v3/blogs/7441709438919444345/posts?key=AIzaSyDyEWV7rt41tGxPcIXZ04kG38-ZNxkBrM0&maxResults=1",function(data){
	  console.log(data);
	});

	*/
	$(document).ready(function() {
		if (blogFeedArea[0].textContent.trim() === "") { //a temp/quick fix for an issue causing document.ready firing twice.
			for (var i = 0; i < pinIDArray.length; i ++) {
				$.ajax({
					url: "https://www.googleapis.com/blogger/v3/blogs/" + id + "/posts/" + pinIDArray[i] + "?key=" + key + "&userIp=" + ip,
		        	async: false,
		        	dataType: "json",
		        	success: function(data) {
						var imageUrlPattern = /<img [^>]*src=\"([^\"]*)\"/gmi;
						var contentData = data.content;
						var imageTag = "";
						if (contentData && contentData.match(imageUrlPattern)) {
							imageTag = contentData.match(imageUrlPattern)[0] + " />";
						}
						var blogtext = data.title.toLowerCase().replace(/https?:\/\//i, "").trim().replace(/[^0-9a-z_]/g, "-");
						output += '<li id="tag_blog_item_' + i + '"><div>' + imageTag + '<a id="tag_blog_title_' + blogtext + '" class="button" href="' + data.url + '" target="_blank">' + 'READ MORE' + '</a></div></li>';
					}
		        });
	
				/*
				$.get("https://www.googleapis.com/blogger/v3/blogs/" + id + "/posts/" + pinIDArray[i] + "?key=" + key + "&userIp=" + ip, function(data) {
					var imageUrlPattern = /<img [^>]*src=\"([^\"]*)\"/gmi;
					var contentData = data.content;
					var imageTag = "";
					if (contentData && contentData.match(imageUrlPattern)) {
						imageTag = contentData.match(imageUrlPattern)[0] + " />";
					}
					var blogtext = data.title.toLowerCase().replace(/https?:\/\//i, "").trim().replace(/[^0-9a-z_]/g, "-");
					output += '<li id="tag_blog_item_' + 0 + '"><div>' + imageTag + '<a id="tag_blog_title_' + blogtext + '" class="title" href="' + data.url + '" target="_blank">' + 'READ MORE' + '</a></div></li>';
				}).fail(function() {
					console.log("Blog feed failed to get data");
				});*/
			}
			blogFeedArea.html(output);
		}
	})
</script>
