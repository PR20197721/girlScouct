<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
	String title = (String)request.getAttribute("VTK_RESOURCES_ITEM_TITLE");
	String uri = (String)request.getAttribute("VTK_RESOURCES_ITEM_URI");
%>
<li><a data-reveal-id="modal_popup" title="<%= title %>" class="previewItem" data-reveal-ajax="/content/girlscouts-vtk/controllers/vtk.include.modals.modal_youtube.html?resource='<%= uri %>'" target="_top"><%= title %></a></li>