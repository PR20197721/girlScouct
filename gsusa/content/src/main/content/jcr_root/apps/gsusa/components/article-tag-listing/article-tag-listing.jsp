<%@include file="/libs/foundation/global.jsp"%>
<%@ page import="com.day.cq.wcm.api.WCMMode" %>

<% 
String tag = properties.get("tag","");
int num = Integer.parseInt(properties.get("num","9"));

if(tag.equals("") && WCMMode.fromRequest(request) == WCMMode.EDIT){
	%> *** Please select a tag *** <%
} else{

slingRequest.setAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE, true); %>

<script>
var page = 1;
var url = window.location.href.split('.');
if(!isNaN(parseInt(url[url.length-2]))){
	page = parseInt(url[url.length-2]);
}

function loadResults(){
	$.ajax({
		type: "POST",
		url: "<%= resource.getPath() %>."+ page + ".html",
	    data: { tag: "<%= tag %>",
		    	num: "<%= num %>",
		    	page: page},
		success: function(res){
			console.log("RESPONSE: " + res);
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

<% slingRequest.removeAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE); %>
<% } %>