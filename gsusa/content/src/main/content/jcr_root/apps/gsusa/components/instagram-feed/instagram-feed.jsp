<%@include file="/libs/foundation/global.jsp"%>
<%@page import="org.apache.sling.commons.json.*, java.io.*, java.net.*" %>

<%
    int count = properties.get("count",20);
	String pinID = properties.get("pin-id","");

	URL url;
	HttpURLConnection conn;
	BufferedReader rd;
	String line;
	String result = "";
	String jsonData = "";
	//Because the access token is private, the GET request is performed on the backend
	//The response is passed to javascript
	try {
		//NOTE: The access token used was provided by Instagram and does not have any expiration date.
		//However, if Instagram's API changes, the access token could expire.
		//26859285 is Girl Scout's User-ID
	    url = new URL("https://api.instagram.com/v1/users/26859285/media/recent/?count=" + count + "&access_token=1918619595.038a400.61f4ec024684403b96923ba14a551828");
	    conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod("GET");
	    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    while ((line = rd.readLine()) != null) {
	       result += line;
	    }
	    rd.close();
	    //System.out.println("RESULT: " + result);
	 } catch (IOException e) {
	    e.printStackTrace();
	 } catch (Exception e) {
	    e.printStackTrace();
	 }
	if(result != ""){
		%>
		<div id="tag_social_feed_instagram" class="wrapper clearfix">
			<span class="icon-social-instagram"></span>
		    <div class="social-block">
		        <div class="block-area">
					<div class="instagram-feed-image-head-area"></div>
			        <ul class="instagram-feed-image-area"></ul>
			    </div>
			    <p class="centered"><a href="https://instagram.com/girlscouts/" title="see more on instagram">See more</a></p>
		    </div>
		    <span class="scroll-more down"></span>
		    <span class="scroll-more up"></span>
		</div>
	    <script type="text/javascript">
        var feedHeadArea = $(".instagram-feed-image-head-area");
	    var feedArea = $(".instagram-feed-image-area");

	    var posts = [];

	    var count = <%= count %>;
	    var toParse = <%= result %>;
	    var pinID = "<%= pinID %>";

	    //console.log(toParse);
	    if(toParse.data != undefined){
		    var postIDPattern = /\/p\/([^\/]*)(\/)/gmi;
		    for(var i=0; i < toParse.data.length; i++){
		    	var postID = "";
		  		if (toParse.data[i].link && toParse.data[i].link.match(postIDPattern)) {
		  			postID = toParse.data[i].link.match(postIDPattern)[0].replace("/p/","").replace("/","");
		  			if(postID == pinID){
		  				posts.unshift(toParse.data[i]);
		  				//console.log("Found Instagram Pin");
		  			}
		  			else{
		  				posts.push(toParse.data[i]);
		  			}
		        }
		    }

	    	var output = "";
			feedHeadArea.append('<a href="' + posts[0].link + '" target="_blank"><img src="' + posts[0].images.standard_resolution.url + '" /></a>');
	    	for(var i=1; i < posts.length; i++){
	    		output = '<li><a href="' + posts[i].link + '" target="_blank"><img src="' + posts[i].images.standard_resolution.url + '" /></a></li>';
	    		feedArea.append(output);
	    	}
	    }

	    </script>

	    <%
	    }
	%>
