<%@include file="/libs/foundation/global.jsp"%>
<%@page import="org.apache.sling.commons.json.*, java.io.*, java.net.*" %>

<%
    int count = properties.get("count",20);
	String id = properties.get("id","7441709438919444345");
	String key = properties.get("key", "AIzaSyDf4hCdKLstx_3GTpnjcbeb_hAyN-d1lBQ");
	
	String url = "https://www.googleapis.com/blogger/v3/blogs/" + id + "/posts?key=" + key;
	%>
    <div class="blog-feed-area"></div>

    <script>
    var blogfeedArea = $(".blog-feed-area");

    var count = <%= count %>;
    var id = <%= id %>;
    var key = <%= key %>;
    
    $.get("https://www.googleapis.com/blogger/v3/blogs/" + id + "/posts?key=" + key,function(data){
    	console.log(data); })
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
