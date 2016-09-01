<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page
	import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />
<%@include file="include/session.jsp"%>
<script>
function toggleSection(section) {
	$("#manageCalendarTab").removeClass("active");
	$("#manageLocationTab").removeClass("active");
	$("#manageActivityTab").removeClass("active");
	$("#manageCalendarSection").hide();
	$("#manageLocationSection").hide();
	$("#manageActivitySection").hide();
	if (section == "calendar") {
	  $("#manageCalendarTab").addClass("active");
	  $("#manageCalendarSection").show();
	} else if (section == "location")  {
		$("#manageLocationTab").addClass("active");
		$("#manageLocationSection").show();
	} else if (section == "activity") {
		$("#manageActivityTab").addClass("active");
		$("#manageActivitySection").show();
	}
}
</script>





<div class="header clearfix">
	<%
	
		boolean isWarning=false;
		String instruction = "Select the calendar icon to change the date, time, or cancel an individual meeting.Or select the to use the planning wizard to reconfigure the calendar from that date forward";
%>
	<h3 class="columns small-21">MEETING date and locations</h3>
	<a class="columns small-3"
		href="/content/girlscouts-vtk/en/vtk.html?rand=<%= new java.util.Date().getTime()%>"><i
		class="icon-button-circle-cross"></i></a>
</div>
<div class="scroll" style="max-height:601px">
	<div class="tabs-wrapper">
		<dl class="tabs" data-tab>
			<dd id="manageCalendarTab" class="active manageCalendarTab">
				<a href="#" onclick="toggleSection('calendar')">Calendar</a>
			</dd>
			<dd id="manageLocationTab" class="manageCalendarTab">
				<a href="#" onclick="toggleSection('location')">Location</a>
			</dd>
			<dd id="manageActivityTab" class="manageCalendarTab">
				<a href="#" onclick="toggleSection('activity')">Activities</a>
			</dd>
		</dl>
		<div class="tabs-content">
<%
if (troop.getYearPlan() != null) {
%>
			<div id="manageCalendarSection">
				<div class="content clearfix active" id="panel1">
					<div id="calMng">
						<%
						  if( troop.getYearPlan().getSchedule() == null  || request.getParameter("alterYPStartDate")!=null) {
						%>
							<%@include file="include/calendarAlterStartDate.jsp"%>
						<%
						    } else {
						%>
							<%@include file="include/calList.jsp"%>
						<%  } %>
					</div>
				</div>
			</div>
			<div id="manageLocationSection">
				<%@include file="include/location.jsp"%>
			</div>
			<div id="manageActivitySection">
				<%@include file="include/manageActivities.jsp"%>
			</div>
				<%
				} else {
				%>
					<span class="error">This year plan has no meetings.<br />
						Please select a different year plan.
					</span>
				<%
				}
				%>
		</div>
		<!--tabs-content-->
	</div>
	<!--/tabs-wrapper-->
</div>
<!--/scroll-->
