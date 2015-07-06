<%@include file="/libs/foundation/global.jsp"%>
<%@page import="org.apache.sling.commons.json.*, java.io.*, java.net.*" %>

<%
    int count = properties.get("count",20);

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
		<div class="wrapper clearfix">
		    <div class="social-block">
		        <span class="icon-social-instagram"></span>
		        <div class="instagram-feed-image-area"></div>
		    </div>
		    <span class="scroll-more"></span>
		</div>


	    <script>
	    var feedArea = $(".instagram-feed-image-area");

	    var count = <%= count %>;
	    var toParse = <%= result %>;

	    if(toParse.data != undefined){
	    	var output = "";
	    	for(var i=0; i < toParse.data.length; i++){
	    		output = '<a href="' + toParse.data[i].link + '" target="_blank"><img src="' + toParse.data[i].images.standard_resolution.url + '" /></a>';
	    		feedArea.append(output);
	    	}
	    }
	    //console.log(toParse);

	    </script>

	    <%
	    }
	%>