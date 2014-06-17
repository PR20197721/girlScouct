<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="java.util.Iterator,org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>

<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<%
java.text.SimpleDateFormat dateFormat4 = new java.text.SimpleDateFormat("MM/dd/yyyy");
java.text.SimpleDateFormat dateFormat41 = new java.text.SimpleDateFormat("a");
java.text.SimpleDateFormat dateFormat42 = new java.text.SimpleDateFormat("hh");
java.text.SimpleDateFormat dateFormat43 = new java.text.SimpleDateFormat("mm");
java.text.SimpleDateFormat dateFormat44 = new java.text.SimpleDateFormat("hh:mm");
%>
<script>
$(function() {
	
	//dev $( "#calStartDt" ).datepicker();
    // PROD
    $( "#calStartDt" ).datepicker({minDate: 0});
   
  });
</script>

<h1>Specify Dates and Locations</h1>
<div style="background-color:gray; color:#fff;">Manage Calendar</div>
<div>
	<p>Set up your meeting dates and times:</p>
	<form>
Start Date<input type="text" id="calStartDt" value="<%=user.getYearPlan().getCalStartDate()==null ? "" : dateFormat4.format(new java.util.Date(user.getYearPlan().getCalStartDate())) %>" />
Time<input type="text" id="calTime" value="<%=user.getYearPlan().getCalStartDate()==null ? (org.girlscouts.vtk.models.api.VTKConfig.CALENDAR_START_TIME_HOUR+":"+org.girlscouts.vtk.models.api.VTKConfig.CALENDAR_START_TIME_MIN) : dateFormat44.format(new java.util.Date(user.getYearPlan().getCalStartDate())) %>"/>
		<select id="calAP">
<% String AM = "am";
	if( user.getYearPlan().getCalStartDate() !=null ){
		AM = dateFormat41.format(new java.util.Date(user.getYearPlan().getCalStartDate()));
	} 
	%>

			<option value="pm" <%=AM.equals("pm") ? " SELECTED" : "" %>>pm</option>
			<option value="am" <%=AM.equals("am") ? " SELECTED" : "" %>>am</option></select>

Frequency:
		<select id="calFreq">
			<option value="weekly" <%= user.getYearPlan().getCalFreq().equals("weekly") ? " SELECTED" : "" %>>weekly</option>
			<option value="biweekly"  <%= user.getYearPlan().getCalFreq().equals("biweekly") ? " SELECTED" : "" %>>biweekly</option>
		</select>
		<div style="padding-top:10px;">Do not schedule a meeting during the week of</div>
		<br/>
<% String exlDates = user.getYearPlan().getCalExclWeeksOf();
exlDates= exlDates==null ? "" : exlDates;
%>
		<br/>
		<input type="checkbox" name="exclDt" value="10/01/2014" <%=exlDates.contains("10/01/2014") ? "CHECKED" : ""  %>/>10/01/2014
		<input type="checkbox" name="exclDt" value="08/01/2014" <%=exlDates.contains("08/01/2014") ? "CHECKED" : ""  %>/>08/01/2014
		<input type="checkbox" name="exclDt" value="07/01/2014" <%=exlDates.contains("07/01/2014") ? "CHECKED" : ""  %>/>07/01/2014
		<input type="checkbox" name="exclDt" value="04/01/2014" <%=exlDates.contains("04/01/2014") ? "CHECKED" : ""  %>/>04/01/2014
		<input type="checkbox" name="exclDt" value="01/01/2014" <%=exlDates.contains("01/01/2014") ? "CHECKED" : ""  %>/>01/01/2014
		<br/><input type="button" value="create calendar" onclick="buildSched()"/>
		<input type="button" value="manage calendar" onclick="document.location='/content/girlscouts-vtk/en/vtk.calendar.html'" />
	</form>
	<div id="calView"></div>
</div>

<%@include file="include/location.jsp" %>
<br/><br/>
<%@include file="include/manageActivities.jsp" %>
