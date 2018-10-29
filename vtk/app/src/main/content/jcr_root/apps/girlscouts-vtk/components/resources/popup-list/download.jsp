<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
	String title = (String)request.getAttribute("VTK_RESOURCES_ITEM_TITLE");
	String uri = (String)request.getAttribute("VTK_RESOURCES_ITEM_URI");
%>
	<td width="60" style="text-align:center">
		<a href="<%= uri %>" title="<%= title %>" download><i style="font-size:40px" class="icon-files"></i></a>
	</td>
	<td  style="vertical-align:middle">
		<a style="color:green; font-weight:800" href="<%= uri %>" title="<%= title %>" download><%= title %></a>
	</td>