<%@include file="/libs/foundation/global.jsp"%>
<%@page import="org.apache.sling.commons.json.*, java.io.*, java.net.*" %>

<%
    int count = properties.get("count",20);
	String id = properties.get("id","7441709438919444345");
	String key = properties.get("key", "AIzaSyDyEWV7rt41tGxPcIXZ04kG38-ZNxkBrM0");

	String url = "https://www.googleapis.com/blogger/v3/blogs/" + id + "/posts?key=" + key;
	%>
        <div class="wrapper clearfix">
            <div class="social-block">
                <span class="icon-social-twitter-tweet-bird"></span>
                <div class="blog-feed-area"></div>
            </div>
            <span class="scroll-more"></span>
        </div>

    <script>
    var blogFeedArea = $(".blog-feed-area");

    var count = <%= count %>;
    var id = "<%= id %>";
    var key = "<%= key %>";
    var ip = "<%=slingRequest.getHeader("x-forwarded-for") %>";
    var comma = ip.indexOf(",");
    ip = ip.substring(0, comma);

    $.get("https://www.googleapis.com/blogger/v3/blogs/" + id + "/posts?key=" + key + "&maxResults=" + count + "&fields=items(title,url)&userIp=" + ip,function(data){
    	//kconsole.log(data);
    	blogFeedArea.append("<ul>");
    	for (var i=0; i<data.items.length; i++){
    		blogFeedArea.append('<li><a href="' + data.items[i].url + '" target="_blank"><div class="title">' + data.items[i].title + '</div></a></li>');
    	}
    	blogFeedArea.append("</ul>");
    })
   	.fail(function() {
   	    console.log("FAILURE");
   	  });


    /*if(toParse.data != undefined){
    	var output = "";
    	for(var i=0; i < toParse.data.length; i++){
    		output = '<a href="' + toParse.data[i].link + '" target="_blank"><img src="' + toParse.data[i].images.standard_resolution.url + '" /></a>';
    		feedArea.append(output);
    	}
    }*/

    </script>
