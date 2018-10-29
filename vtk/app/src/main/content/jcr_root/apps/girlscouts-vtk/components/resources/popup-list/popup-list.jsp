<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
	String title = (String)request.getAttribute("VTK_RESOURCES_ITEM_TITLE");
	String uri = (String)request.getAttribute("VTK_RESOURCES_ITEM_URI");
	String id = (String)request.getAttribute("VTK_RESOURCES_ITEM_ID");
	
	String resPath = resource.getPath();
	if (!resPath.startsWith("/content/vtkcontent")) {
		resPath = resource.getPath().replace("/content","");
	}
%>
<li><a id="<%= id %>" title="<%= title %>" target="_top"><%= title %></a></li>
<script>
	$('#<%= id %>').on('click', function(e){
		initResourcePopup();
		loadModalPage('<%= resPath %>.modal.html',false, null, true, false, undefined, true);
	});
</script>