<%--

  String Tab component.

  Each resources tab contains resources categories belong to that particular level, such as "Daisy".

--%><%
%><%@page import="java.util.*,
	com.day.cq.wcm.api.WCMMode,
	org.girlscouts.vtk.models.Troop,
	org.girlscouts.vtk.models.*,
	com.day.cq.wcm.api.components.IncludeOptions,
	org.girlscouts.vtk.utils.*"%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %>
<%
	User user = (User)request.getAttribute("vtk-request-user");
	Troop troop = (Troop)request.getAttribute("vtk-request-troop");
	WCMMode prevMode = (WCMMode)request.getAttribute("VTK_RESOURCES_PREV_MODE");
	prevMode.toRequest(request);

	final String[] levels = {
		"Daisy",
		"Brownie",
		"Junior",
		"Cadette",
		"Senior",
		"Ambassador",
		"Multi-level"
	};
%>

	<div class="column small-22 medium-20 small-centered medium-centered">
		<div class="__menu">
			<ul>
				<% for (String level : levels) { %>
					<li class="<%= level.toLowerCase() %>"><%= level %></li>
				<% } %>
				<div class="__complement">
					<% for (int i = 0; i < levels.length; i++) { %>
					<% String level = levels[i]; %>
					<%--  String selected = (level.toLowerCase().replaceAll("-level","").equals(VtkUtil.formatLevel(user, troop)) ? "selected" : "");  --%>
					<%  String selected = (i == 0 ? "selected" : ""); %> 
 					<li class="_<%= level.toLowerCase() %> <%= selected %>"></li>
					<% } %>
				</div>
			</ul>
		</div>
		<div class="__content">
			<% for (String level : levels) { %>
			<% Set<String> css = IncludeOptions.getOptions(request, true).getCssClassNames(); %>
			<% css.add("__block"); css.add("row"); css.add(level.toLowerCase()); %>
			<% request.setAttribute("VTK_RESOURCES_LEVEL", level.toLowerCase()); %>
			<cq:include path="<%= level.toLowerCase() %>" resourceType="girlscouts-vtk/components/resources/level" />
			<% request.removeAttribute("VTK_RESOURCES_LEVEL"); %>
			<% } %>

		</div>
	</div>


	<script>
		function initResourcePopup() {
			window.scrollTo(0,0);
			$(document).foundation();
			$('body').addClass('vtk-body');
		}

		// TODO: migrate
		$('.click-open-modal-overview').on('click', function(e){
			initResourcePopup();
			var url = $(this).data('url');
			loadModalPage('',false, null, true, false);
		});
	</script>