<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
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

<!-- apps/girlscouts-vtk/components/vtk/locations.jsp  -->
<div class="row modalHeader">
<%
        boolean isWarning=false;
        String instruction = "Specify Dates and Locations";
        if (isWarning) {
%>
        <div class="small-4 medium-2 large-2 columns">                
                <div class="warning"><img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/warning-small.png" width="20" height="20" align="left
"/></div>
        </div>
        <div class="small-16 medium-20 large-20 columns">
<%
        } else {
%>
        <div class="small-20 medium-22 large-22 columns">
<%
        }
%>
                <span class="instruction"><%= instruction %></span>

        </div>
        <div class="small-4 medium-2 large-2 columns">
		<a class="right" href="/content/girlscouts-vtk/en/vtk.html?rand=<%= new java.util.Date().getTime()%>"><img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/close-small.png" width="20" height="20" border="0" align="right"></a>
        </div>
</div>
<div class="row modalNav">
        <ul class="small-block-grid-1 medium-block-grid-3 large-block-grid-3 specifyDates">
				<li id="manageCalendarTab" class="active manageCalendarTab"><a href="#" onclick="toggleSection('calendar')">Calendar</a></li>
                <li id="manageLocationTab" class="manageCalendarTab"><a href="#" onclick="toggleSection('location')">Location</a></li>
                <li id="manageActivityTab" class="manageCalendarTab"><a href="#" onclick="toggleSection('activity')">Activities</a></li>
	</ul>
</div>
<div class="row modalBody">
        <div class="small-24 medium-24 large-24 columns">
<%
        if (user.getYearPlan() != null) {
%>
                <div id="manageCalendarSection">
                        <div class="sectionBar">Manage Calendar</div>
                       
			<div id="calMng">
<%
                if( user.getYearPlan().getSchedule() == null ){
%>
<%@include file="include/calSched.jsp" %>

<%
                }else{
%>
<!-- jira 114. next release
 <div><input type="button" value="Click here to change your recurring meeting date and time." name="" onclick="showAlterYearPlanStartDate()"/></div>
<div>Or select a calendar icon below to change the date, time, or to cancel a specific meeting</div>
-->
<%@include file="include/calList.jsp" %>

<%
                }
%>
			</div>
                </div>
                <div id="manageLocationSection">
<%
                if( false) {//user.getYearPlan().getSchedule() == null ){
%>
			Please first select your calendar start date to set meeting locations.
<%
		} else {
%>
<%@include file="include/location.jsp" %>
<%
		}
%>
                </div>
                <div id="manageActivitySection">
<%
                if( false) {// user.getYearPlan().getSchedule() == null ){
%>
                        Please first select your calendar start date to set meeting activities.
<%
                } else {
%>
<%@include file="include/manageActivities.jsp" %>
<%
                }
%>
		</div>
<%
        } else {
%>
        <span class="error">This year plan has no meetings.<br/> Please select a different year plan.</span>
<%
        }
%>
        </div>
</div>
