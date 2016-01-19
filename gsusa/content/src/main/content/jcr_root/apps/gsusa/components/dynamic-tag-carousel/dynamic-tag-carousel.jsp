<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%@include file="/apps/gsusa/components/global.jsp"%>

<%
	String id = "dynamic-tag-carousel-" + generateId();
	String num = properties.get("num", "20");
	
	// TODO: how to get the default tag from the article page?
	String defaultTag = "defaultTag";
%>
<div id="<%= id %>"></div>

<script>
	$(document).ready(function(){
		var carousel = new DynamicTagCarousel('<%= id %>', <%= num %>, '<%= defaultTag %>');
		carousel.load();
	});
</script>