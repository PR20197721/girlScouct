<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
	String title = (String)request.getAttribute("VTK_RESOURCES_ITEM_TITLE");
	String uri = (String)request.getAttribute("VTK_RESOURCES_ITEM_URI");
	String id = (String)request.getAttribute("VTK_RESOURCES_ITEM_ID");
%>
<li><a id="<%= id %>" title="<%= title %>" target="_top"><%= title %></a></li>
<script>
	$('#<%= id %>').on('click', function(e){
		initResourcePopup();
		loadModalPage('<%= uri %>.modal.html',false, null, true, false, undefined, true);
	});
</script>