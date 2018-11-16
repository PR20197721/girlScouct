<%@page import="com.day.cq.wcm.api.WCMMode" %>

<%@include file="/apps/girlscouts-vtk/components/resources/global.jsp"%>
<%@page session="false" %>

<%
	String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
	String level = selectors[selectors.length - 1].toLowerCase();
	
	String title = "";
	if ("daisy".equals(level)) {
		title = "Daisy Petal, Badge, and Journey Resources";
	} else if ("multi-level".equals(level)){
		title = "Multi-Level Badge and Journey Resources";
	} else if (level != null && level.length() > 0){
		title = level.substring(0, 1).toUpperCase() + level.substring(1) + " Badge and Journey Resources";
	}
	
	String selectorsToAdd = "include.resource_overviews." + level;
%>
<div class="header clearfix">
	<h3 class="columns small-21"><%= title %></h3>
	<i style="position:absolute; top:5px; right:5px;" class="icon-button-circle-cross" onclick="(function(){$('#gsModal').dialog('close')})()"></i>
</div>
<% if (WCMMode.fromRequest(request) == WCMMode.EDIT || WCMMode.fromRequest(request) == WCMMode.PREVIEW) { %>
	<p></p>
	<p>The "Meeting Overviews" section does not show up in author mode.</p>
	<p>Overviews will show up in VTK.</p>
<% } else { %>
	<sling:include path="/content/girlscouts-vtk/controllers/vtk" replaceSelectors="<%= selectorsToAdd %>" />
<% } %>