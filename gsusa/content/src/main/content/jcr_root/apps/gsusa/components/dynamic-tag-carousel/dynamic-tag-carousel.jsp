<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%

%>

<div id='testtest'></div>
<script>
	$(document).ready(function(){
		var carousel = new DynamicTagCarousel('testtest', 20);
		carousel.load();
	});
</script>