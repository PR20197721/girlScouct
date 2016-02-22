<%@page import="com.day.cq.tagging.Tag" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%@include file="/apps/gsusa/components/global.jsp"%>

<%
	String id = "dynamic-tag-carousel-" + genId();
	String num = properties.get("num", "20");
	
	Tag[] tags = currentPage.getTags();
	// The first tag of the article is the default tag of the carousel.
	String defaultTag;
	if (tags != null && tags.length != 0) {
		defaultTag = tags[0].getName();
	} else {
		defaultTag = "default";
	}

	String sortByPriority = properties.get("sortByPriority", "false");
	if ("true".equals(sortByPriority)) {
		num = "-" + num;
	}
	
	String listingPage = currentPage.getProperties().get("listingPage", "");
	if (!listingPage.isEmpty()) {
		listingPage = resourceResolver.map(listingPage + ".html");
	}
%>
<div id="<%= id %>" class="hide-for-small"></div>

<% if (!listingPage.isEmpty()) { %>
	<div id="dynamic-tag-carousel-listing-page" data="<%= listingPage %>"></div>
<% } %>

<script>
	$(document).ready(function(){
		var carousel = new DynamicTagCarousel('<%= id %>', <%= num %>, '<%= defaultTag %>');
		carousel.load();
	});
</script>