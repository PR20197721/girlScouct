<%@page import="com.day.cq.tagging.Tag" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%@include file="/apps/gsusa/components/global.jsp"%>

<%
	String id = "dynamic-tag-carousel-" + generateId();
	String num = properties.get("num", "20");
	
	Tag[] tags = currentPage.getTags();
	// The first tag of the article is the default tag of the carousel.
	String defaultTag;
	if (tags != null && tags.length != 0) {
		defaultTag = tags[0].getName();
	} else {
		defaultTag = "default";
	}
%>
<div id="<%= id %>"></div>

<script>
	$(document).ready(function(){
		var carousel = new DynamicTagCarousel('<%= id %>', <%= num %>, '<%= defaultTag %>');
		carousel.load();
	});
</script>