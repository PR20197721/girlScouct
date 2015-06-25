<%@include file="/libs/foundation/global.jsp"%>
<%@page import="org.apache.sling.commons.json.*, java.io.*, java.net.*" %>

<%
String fbPage = properties.get("fbPage","GirlScoutsUSA");
String token = properties.get("token","419540344831322|-TeLW4iF6EzJKH-ozdPE0uSWCdQ");
String urlStr = "https://graph.facebook.com/v2.3/" + fbPage + "?fields=posts.limit(1){id,full_picture}&access_token=" + token;

URL url;
HttpURLConnection conn;
BufferedReader rd;
String line;
String result = "";
String jsonData = "";
try {
    url = new URL(urlStr);
    conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    while ((line = rd.readLine()) != null) {
       result += line;
    }
    rd.close();
    JSONObject firstJson = new JSONObject(result);
    String id = firstJson.getJSONObject("posts").getJSONArray("data").getJSONObject(0).getString("id");
    urlStr = "https://graph.facebook.com/v2.3/" + id + "?fields=attachments&access_token=" + token;
    url = new URL(urlStr);
    conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    while ((line = rd.readLine()) != null) {
       jsonData += line;
    }
    rd.close();
 } catch (IOException e) {
    e.printStackTrace();
 } catch (Exception e) {
    e.printStackTrace();
 }
if(jsonData != ""){
%>
<div class="facebook-feed-image-area"></div>
<div class="facebook-embed-area">

<div class="fb-page" data-href="https://www.facebook.com/<%= fbPage %>" data-small-header="false" data-adapt-container-width="true" data-hide-cover="false" data-show-facepile="true" data-show-posts="true"><div class="fb-xfbml-parse-ignore"><blockquote cite="https://www.facebook.com/<%= fbPage %>"><a href="https://www.facebook.com/<%= fbPage %>"><%= fbPage %></a></blockquote></div></div>

</div>
<script>

var imageArea = $(".facebook-feed-image-area");

var toParse = <%= jsonData %>;
//console.log(toParse);

if(toParse.attachments.data[0].subattachments != undefined){
	for(var i=0; i<toParse.attachments.data[0].subattachments.data.length; i++){
		//console.log(toParse.attachments.data[0].subattachments.data[i].media.image.src);
		imageArea.append("<img src=\"" + toParse.attachments.data[0].subattachments.data[i].media.image.src + "\" alt=\"Facebook Feed Image\">");
	}
}
else if(toParse.attachments.data[0].media.image.src != undefined){
	//console.log(toParse.attachments.data[0].media.image.src);
	imageArea.append("<img src=\"" + toParse.attachments.data[0].media.image.src + "\" alt=\"Facebook Feed Image\">");
}
else{
	console.log("No image found");
}

</script>

<%
}
%>