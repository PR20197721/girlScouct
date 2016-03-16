<%@include file="/libs/foundation/global.jsp"%>
<%@ page import="com.day.cq.wcm.api.WCMMode" %>

<%
String[] tags = (String[])properties.get("tags",String[].class);
int num = Integer.parseInt(properties.get("num","9"));
String priority = properties.get("priority","false");
String hasBorderLine = properties.get("borderLine", String.class);
String listing = properties.get("titleLink","");

if ("on".equals(hasBorderLine)) {%>
	<hr style="border-top: solid 1px #000000">
<%
}


if(tags == null) {
	if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
		%> *** Please select a tag *** <%
	}
} else {
%>

<div class="block-grid">
	<ul id="article-list">
	</ul>
</div>

<script>
var page = 1;
var url = window.location.href.split('.');
if(!isNaN(parseInt(url[url.length-2]))){
	page = parseInt(url[url.length-2]);
}

var tags = [];
<% for (String t : tags){ %>
	tags.push("<%= java.net.URLEncoder.encode(t,"UTF-8") %>");
<% } %>

function loadResults(){
	$.ajax({
		type: "GET",
		dataType: "html",
		url: "<%= resource.getPath() %>.ajax."+ page + ".html",
	    data: { tag: tags.toString(),
		    	num: "<%= num %>",
		    	page: page,
		    	priority: "<%= priority %>",
		    	listing: "<%= listing %>" },
		success: function(res){
			$("#article-list").append(res);
			retina(true);
			setTimeout(function() {
				article_tiles();
			}, 750);
		}
	});
}
</script>

<p class="more-link"><a id="more" title="show more results">LOAD MORE</a></p>

<script>
$("#more").click(function(event){
	event.preventDefault();
	page++;
	loadResults();
});
loadResults();
</script>

<% } %>