<%@include file="/libs/foundation/global.jsp"%>
<%@page import="org.apache.sling.commons.json.*, java.io.*, java.net.*" %>

<%
    int count = properties.get("count",20);
	String id = properties.get("id","7441709438919444345");
	String key = properties.get("key", "AIzaSyDyEWV7rt41tGxPcIXZ04kG38-ZNxkBrM0");
	String pinID = properties.get("pin-id","");

	String url = "https://www.googleapis.com/blogger/v3/blogs/" + id + "/posts?key=" + key;
	%>
        <div class="wrapper clearfix">
            <div class="social-block">
                <div class="align-center">
                    <img src="http://www.girlscouts.org/images/blog/gssm.png" alt="Girl Scout Blog" border="0" id="gsLogo">
                </div>
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
    var pinID = "<%= pinID %>";
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
    $.get("https://www.googleapis.com/blogger/v3/blogs/" + id + "/posts?key=" + key + "&maxResults=" + count + "&fields=items(published,title,url,content,id)&userIp=" + ip,function(data){
    	var output = "";
    	output += "<ul>";
	var DAYS = ['Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday'];
	var MONTHS = ['January','February','March','April','May','June','July','August','September','October','November','December'];

	var posts = [];
    for (var i=0; i<data.items.length; i++){
    	if(pinID != "" && data.items[i].id == pinID){
    		posts.unshift(data.items[i]);
    	}	
    	else{
    		posts.push(data.items[i]);
    	}
    }
    for(var k=0; k<posts.length; k++){
	  var contentData = posts[k].content;
	  var imageUrlPattern = /<img [^>]*src=\"([^\"]*)\"/gmi
  	  var imageTag = "";
	  if (contentData && contentData.match(imageUrlPattern)) {
		imageTag = contentData.match(imageUrlPattern)[0];
          }
	  var tmpDiv = document.createElement("div");
	  tmpDiv.innerHTML = contentData;
	  var shortDescription = (tmpDiv.textContent || tmpDiv.innerText || "").trim();
	  var dateline = new Date(posts[k].published);
	  var DESCRIPTION_LENGTH = 200;
	  if (shortDescription.length > DESCRIPTION_LENGTH) {
	    shortDescription = shortDescription.substring(0,DESCRIPTION_LENGTH) + "...";
	  }
    		output += '<li><p class="dateline">' + DAYS[dateline.getDay()] + ', ' + MONTHS[dateline.getMonth()] + ' ' + dateline.getDate() + ', ' + dateline.getFullYear() + '</p><a href="' + posts[k].url + '" target="_blank" class="title">' + posts[k].title + '</a>' + imageTag + '<p>' + shortDescription + '</p><p><a href="' + posts[k].url + '" target="_blank" class="title"> continue reading ></a></p></li>';
    	}
    	output += "</ul>";
    	blogFeedArea.html(output);
    })
   	.fail(function() {
   	    console.log("Blog feed failed to get data");
   	  });


    /*if(toParse.data != undefined){
    	var output = "";
    	for(var i=0; i < toParse.data.length; i++){
    		output = '<a href="' + toParse.data[i].link + '" target="_blank"><img src="' + toParse.data[i].images.standard_resolution.url + '" /></a>';
    		feedArea.append(output);
    	}
    }*/

    </script>
