<%@include file="/libs/foundation/global.jsp"%>
<%@page import="org.apache.sling.commons.json.*, java.io.*, java.net.*" %>

<%
String fbPage = properties.get("fbPage","GirlScoutsUSA");
String token = properties.get("token","419540344831322|-TeLW4iF6EzJKH-ozdPE0uSWCdQ");
String url = "/" + fbPage + "?fields=posts.limit(1){id,full_picture}&access_token=" + token;
%>

<script>

var toParse = JSON.parse(<%= jsonData %>);
FB.api(
	"<%=url%>",
	function(response) {
		if(response && !response.error){
			console.log("ID: " + response.posts.data[0].id);
			FB.api(
				"/" + response.posts.data[0].id + "?fields=attachments&access_token=<%=token%>",
				function(response) {
					if(response && !response.error){
						if(response.attachments.data[0].subattachments != undefined){
							for(var i=0; i<response.attachments.data[0].subattachments.data.length; i++){
								console.log(response.attachments.data[0].subattachments.data[i].media.image.src);
							}
						}
						else if(response.attachments.data[0].attachments.data[0].media.image.src != undefined){
							console.log(response.attachments.data[0].attachments.data[0].media.image.src);
						}
						else{
							console.log("No image found");
						}
					}
				}
			);
		}
	}
);
</script>