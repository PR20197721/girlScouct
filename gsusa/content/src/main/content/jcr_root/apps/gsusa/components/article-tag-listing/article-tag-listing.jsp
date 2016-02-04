<%@include file="/libs/foundation/global.jsp"%>
<%@ page import="com.day.cq.wcm.api.WCMMode" %>

<%
String tag = properties.get("tag","");
int num = Integer.parseInt(properties.get("num","9"));
String priority = properties.get("priority","false");

if(tag.equals("") && WCMMode.fromRequest(request) == WCMMode.EDIT){
	%> *** Please select a tag and path *** <%
} else{ 
	String linkTagAnchors = "#" + tag.replaceAll("gsusa:content-hub/", "").replaceAll("/", "|");
%>

<div class="related-articles">
	<div class="block-grid">
		<ul id="article-list">
		</ul>
	</div>
</div>

<script>
var page = 1;
var url = window.location.href.split('.');
if(!isNaN(parseInt(url[url.length-2]))){
	page = parseInt(url[url.length-2]);
}

function loadResults(){
	$.ajax({
		type: "GET",
		dataType: "html",
		url: "<%= resource.getPath() %>.ajax."+ page + ".html",
	    data: { tag: "<%= tag %>",
		    	num: "<%= num %>",
		    	page: page,
		    	anchors: "<%= linkTagAnchors %>",
		    	priority: "<%= priority %>" },
		success: function(res){
			$("#article-list").append(res);
			retina(true);
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