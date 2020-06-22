<%@page import="com.day.cq.tagging.Tag,
				org.slf4j.Logger,
				org.slf4j.LoggerFactory" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%@include file="/apps/gsusa/components/global.jsp"%>

<%
	final Logger dynamicTagLog = LoggerFactory.getLogger(this.getClass());
	String id = "dynamic-tag-carousel-" + genId();
	String num = properties.get("num", "20");
	if(num.isEmpty()){
		num = "20";
    } else{
        try{
    		Integer.parseInt(num);
        } catch(Exception e){
            num = "20";
        }
    }

	Tag[] tags = currentPage.getTags();
	// The first tag of the article is the default tag of the carousel.
	String defaultTag;
	if (tags != null && tags.length != 0) {
		StringBuilder builder = new StringBuilder();
		for (Tag tag : tags) {
			dynamicTagLog.info("Tag is: " + tag.getName());
			builder.append(tag.getName()).append("|");
		}
		builder.deleteCharAt(builder.length() - 1);
		defaultTag = builder.toString();
	} else {
		dynamicTagLog.info("Tags are null");
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
