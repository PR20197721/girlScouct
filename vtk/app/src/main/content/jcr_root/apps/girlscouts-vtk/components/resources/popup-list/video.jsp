<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
	String title = (String)request.getAttribute("VTK_RESOURCES_ITEM_TITLE");
	String uri = (String)request.getAttribute("VTK_RESOURCES_ITEM_URI");
%>
	<td width="60" style="text-align:center">
		<a data-reveal-id="modal_popup" title="<%= title %>" class="previewItem" data-reveal-ajax="/content/girlscouts-vtk/controllers/vtk.include.modals.modal_youtube.html?resource='<%= uri %>'" target="_top"><i style="font-size:40px" class="icon-video-movie"></i></a>
	</td>
	<td  style="vertical-align:middle">
		<a style="color:green; font-weight:800" data-reveal-id="modal_popup" title="<%= title %>" class="previewItem" data-reveal-ajax="/content/girlscouts-vtk/controllers/vtk.include.modals.modal_youtube.html?resource='<%= uri %>'" target="_top"><%= title %></a></li>
	</td>