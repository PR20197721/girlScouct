<%@include file="/libs/foundation/global.jsp"%>

**Article Tag Listing**

<% slingRequest.setAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE, true); %>

<script>
var page = 1;
var url = window.location.href.split('.');
if(!isNaN(parseInt(url[url.length-2]))){
	page = parseInt(url[url.length-2]);
}

$.ajax({
	type: "POST",
	url: "<%= resource.getPath() %>."+ page + ".html",
    data: { tag: "gsusa:content-hub/girls/stem",
	    	num: "1",
	    	page: page}
});
</script>

<% slingRequest.removeAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE); %>