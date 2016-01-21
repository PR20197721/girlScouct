<%@include file="/libs/foundation/global.jsp"%>

**Article Tag Listing**

<% slingRequest.setAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE, true); %>

<script>
$.ajax({
	type: "POST",
	url: "<%= resource.getPath() %>.html",
    data: { tag: "gsusa:content-hub/girls/stem",
	    	num: "2",
	    	page: "1"}
});
</script>

<% slingRequest.removeAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE); %>