<%@include file="/libs/foundation/global.jsp"%>
<%@ page import="com.day.cq.wcm.api.WCMMode" %>

<% 
String tag = properties.get("tag","");
String path = properties.get("path","");
int num = Integer.parseInt(properties.get("num","9"));

if((tag.equals("") || path.equals("")) && WCMMode.fromRequest(request) == WCMMode.EDIT){
	%> *** Please select a tag and/or path *** <%
} else{ %>

<div id="article-list"/></div>

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
		    	path: "<%= path %>"},
		success: function(res){
			$("#article-list").append(res);
		}
	});
}
</script>

<a class="button load-more" >Load More</a>

<script>
$(".load-more").click(function(event){
	event.preventDefault();
	loadResults();
	page++;
});
</script>

<% } %>